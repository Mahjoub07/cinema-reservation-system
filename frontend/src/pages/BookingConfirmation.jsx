import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { verifyBooking, downloadTicket } from '../api/bookings';
import { useAuth } from '../context/AuthContext';
import '../styles/BookingConfirmation.css';

const BookingConfirmation = () => {
  const { bookingId } = useParams();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [downloading, setDownloading] = useState(false);

  const { user } = useAuth();

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const data = await verifyBooking(bookingId);
        setBooking(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Booking not found or invalid');
      } finally {
        setLoading(false);
      }
    };

    if (bookingId) {
      fetchBooking();
    }
  }, [bookingId]);

  const handleDownload = async () => {
    setDownloading(true);
    try {
      await downloadTicket(booking.id);
    } catch (err) {
      setError('Failed to download ticket');
    } finally {
      setDownloading(false);
    }
  };

  if (loading) {
    return (
      <div className="confirmation-page">
        <div className="confirmation-loading">
          <div className="spinner-ring"><div /><div /><div /><div /></div>
          <p>Loading ticket...</p>
        </div>
      </div>
    );
  }

  if (error || !booking) {
    return (
      <div className="confirmation-page">
        <div className="confirmation-error">
          <div className="error-icon">&#10006;</div>
          <h2>Invalid Ticket</h2>
          <p>{error || 'This booking does not exist'}</p>
          <Link to="/" className="btn btn-primary">Back to Home</Link>
        </div>
      </div>
    );
  }

  const isValid = booking.status === 'CONFIRMED';
  const isCancelled = booking.status === 'CANCELLED';
  const seatNumbersText = booking.seatNumbers?.map(s => s + 1).join(', ') || 'N/A';
  const showTime = booking.showTime ? new Date(booking.showTime).toLocaleString() : 'TBA';
  const bookingDate = booking.bookingDate ? new Date(booking.bookingDate).toLocaleString() : 'N/A';
  const hallNumber = booking.movieId ? `Hall ${(booking.movieId % 4) + 1}` : 'Main Hall';

  return (
    <div className="confirmation-page">
      <div className={`ticket-card ${isValid ? 'valid' : isCancelled ? 'cancelled' : 'invalid'}`}>
        {/* Ticket Header */}
        <div className="ticket-header">
          <div className="ticket-brand">
            <span className="ticket-brand-icon">&#127909;</span>
            <span className="ticket-brand-text">CineReserve</span>
          </div>
          <div className={`ticket-status-badge ${booking.status.toLowerCase()}`}>
            {isValid ? 'Confirmed' : isCancelled ? 'Cancelled' : booking.status}
          </div>
        </div>

        {/* Movie Section */}
        <div className="ticket-movie">
          {booking.moviePosterUrl ? (
            <img src={booking.moviePosterUrl} alt={booking.movieTitle} className="ticket-poster" />
          ) : (
            <div className="ticket-poster-fallback">
              <span>&#127909;</span>
            </div>
          )}
          <div className="ticket-movie-info">
            <h1 className="ticket-movie-title">{booking.movieTitle || 'Unknown Movie'}</h1>
            <div className="ticket-movie-meta">
              <span className="ticket-meta-item">&#128197; {showTime}</span>
              <span className="ticket-meta-item">&#127970; {hallNumber}</span>
            </div>
          </div>
        </div>

        {/* Perforation Line */}
        <div className="ticket-perforation">
          <div className="ticket-notch left" />
          <div className="ticket-dashes" />
          <div className="ticket-notch right" />
        </div>

        {/* Booking Details */}
        <div className="ticket-details">
          <div className="ticket-detail-group">
            <h3>Booking Details</h3>
            <div className="ticket-detail-row">
              <span className="ticket-label">Reference</span>
              <span className="ticket-value ticket-reference">#{booking.id}</span>
            </div>
            <div className="ticket-detail-row">
              <span className="ticket-label">Booked On</span>
              <span className="ticket-value">{bookingDate}</span>
            </div>
            <div className="ticket-detail-row">
              <span className="ticket-label">Status</span>
              <span className={`ticket-value status-text ${booking.status.toLowerCase()}`}>
                {booking.status}
              </span>
            </div>
          </div>

          <div className="ticket-detail-group">
            <h3>Customer</h3>
            <div className="ticket-detail-row">
              <span className="ticket-label">Name</span>
              <span className="ticket-value">{booking.userName || 'Guest'}</span>
            </div>
            <div className="ticket-detail-row">
              <span className="ticket-label">Email</span>
              <span className="ticket-value">{booking.userEmail}</span>
            </div>
          </div>

          <div className="ticket-detail-group">
            <h3>Seats &amp; Payment</h3>
            <div className="ticket-detail-row">
              <span className="ticket-label">Seats</span>
              <span className="ticket-value ticket-seats">#{seatNumbersText}</span>
            </div>
            <div className="ticket-detail-row">
              <span className="ticket-label">Tickets</span>
              <span className="ticket-value">{booking.numberOfSeats}</span>
            </div>
            <div className="ticket-detail-row ticket-total-row">
              <span className="ticket-label">Total Paid</span>
              <span className="ticket-value ticket-price">${booking.totalPrice?.toFixed(2) || '0.00'}</span>
            </div>
          </div>
        </div>

        {/* Perforation Line */}
        <div className="ticket-perforation">
          <div className="ticket-notch left" />
          <div className="ticket-dashes" />
          <div className="ticket-notch right" />
        </div>

        {/* QR Section */}
        <div className="ticket-qr-section">
          <p className="ticket-qr-label">Scan at entrance</p>
          <img
            src={`https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=${encodeURIComponent(
              typeof window !== 'undefined'
                ? window.location.href
                : `https://mahjoub07.github.io/cinema-reservation-system/#/booking-confirmation/${booking.id}`
            )}`}
            alt="Ticket QR Code"
            className="ticket-qr-code"
          />
          <p className="ticket-qr-id">Ref: #{booking.id}</p>
        </div>

        {/* Actions */}
        {isValid && (
          <div className="ticket-actions">
            {user && (
              <button
                className="btn btn-primary btn-lg ticket-download"
                onClick={handleDownload}
                disabled={downloading}
              >
                <span>&#128196;</span>
                {downloading ? 'Downloading...' : 'Download Ticket'}
              </button>
            )}
            <Link to="/movies" className="btn btn-secondary btn-lg">
              Browse More Movies
            </Link>
          </div>
        )}

        {!isValid && (
          <div className="ticket-actions">
            <Link to="/movies" className="btn btn-primary btn-lg">
              Book a Movie
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default BookingConfirmation;
