import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { verifyBooking } from '../api/bookings';
import '../styles/VerifyBooking.css';

const VerifyBooking = () => {
  const { bookingId } = useParams();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const data = await verifyBooking(bookingId);
        setBooking(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Booking not found');
      } finally {
        setLoading(false);
      }
    };

    if (bookingId) {
      fetchBooking();
    }
  }, [bookingId]);

  if (loading) {
    return (
      <div className="verify-container">
        <div className="loading-spinner">Loading booking details...</div>
      </div>
    );
  }

  if (error || !booking) {
    return (
      <div className="verify-container">
        <div className="verify-card invalid">
          <div className="invalid-icon">&#10006;</div>
          <h2>Invalid Booking</h2>
          <p className="error-message">{error || 'Booking not found'}</p>
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

  return (
    <div className="verify-container">
      <div className={`verify-card ${isValid ? 'valid' : isCancelled ? 'cancelled' : 'invalid'}`}>
        <div className={`status-icon ${isValid ? 'valid' : isCancelled ? 'cancelled' : 'invalid'}`}>
          {isValid ? '&#10004;' : isCancelled ? '&#10006;' : '&#10006;'}
        </div>
        
        <h2 className="status-title">
          {isValid ? 'Valid Booking' : isCancelled ? 'Cancelled Booking' : 'Invalid Booking'}
        </h2>
        
        <div className="booking-details">
          <div className="detail-section">
            <h3>Booking Information</h3>
            <div className="detail-row">
              <span className="label">Booking ID:</span>
              <span className="value">#{booking.id}</span>
            </div>
            <div className="detail-row">
              <span className="label">Status:</span>
              <span className={`status-badge ${booking.status.toLowerCase()}`}>
                {booking.status}
              </span>
            </div>
            <div className="detail-row">
              <span className="label">Booked On:</span>
              <span className="value">{bookingDate}</span>
            </div>
          </div>

          <div className="detail-section">
            <h3>Movie Details</h3>
            <div className="detail-row">
              <span className="label">Movie:</span>
              <span className="value">{booking.movieTitle}</span>
            </div>
            <div className="detail-row">
              <span className="label">Showtime:</span>
              <span className="value">{showTime}</span>
            </div>
          </div>

          <div className="detail-section">
            <h3>Ticket Details</h3>
            <div className="detail-row">
              <span className="label">Seats:</span>
              <span className="value">#{seatNumbersText}</span>
            </div>
            <div className="detail-row">
              <span className="label">Number of Seats:</span>
              <span className="value">{booking.numberOfSeats}</span>
            </div>
            <div className="detail-row">
              <span className="label">Total Price:</span>
              <span className="value price">${booking.totalPrice?.toFixed(2) || '0.00'}</span>
            </div>
          </div>
        </div>

        <div className="verify-actions">
          <Link to="/" className="btn btn-primary">Back to Home</Link>
        </div>
      </div>
    </div>
  );
};

export default VerifyBooking;
