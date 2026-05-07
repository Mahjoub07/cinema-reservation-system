import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getMyBookings, cancelBooking, downloadTicket } from '../api/bookings';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/MyBookings.css';

const MyBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cancelling, setCancelling] = useState(null);

  const { user } = useAuth();

  useEffect(() => {
    fetchBookings();
  }, [user]);

  const fetchBookings = async () => {
    if (!user) return;
    setLoading(true);
    try {
      const data = await getMyBookings();
      setBookings(data);
    } catch (err) {
      setError('Failed to load bookings');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (bookingId) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return;

    setCancelling(bookingId);
    try {
      await cancelBooking(bookingId);
      setBookings(bookings.map(b =>
        b.id === bookingId ? { ...b, status: 'CANCELLED' } : b
      ));
    } catch (err) {
      setError('Failed to cancel booking');
    } finally {
      setCancelling(null);
    }
  };

  if (loading) {
    return <LoadingSpinner text="Loading your bookings..." />;
  }

  if (error) {
    return (
      <div className="bookings-container">
        <h1>My Bookings</h1>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  if (bookings.length === 0) {
    return (
      <div className="bookings-container">
        <h1>My Bookings</h1>
        <div className="empty-state">
          <p>No bookings yet</p>
          <span>Book your first movie and the ticket will appear here.</span>
          <Link to="/movies" className="btn btn-primary" style={{ marginTop: '16px' }}>
            Browse Movies
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="bookings-container">
      <div className="bookings-header">
        <h1>My Bookings</h1>
        <span className="bookings-count">{bookings.length} total</span>
      </div>

      <div className="bookings-grid">
        {bookings.map((booking) => (
          <div
            key={booking.id}
            className={`booking-card ${booking.status === 'CANCELLED' ? 'cancelled' : ''}`}
          >
            <div className="booking-card-top">
              <div className="booking-movie">
                <h3>{booking.movieTitle || 'Unknown Movie'}</h3>
                <span className={`status-badge ${booking.status.toLowerCase()}`}>
                  {booking.status}
                </span>
              </div>
              <div className="booking-meta">
                <div className="meta-block">
                  <span className="meta-label">Seats</span>
                  <span className="meta-value">{booking.numberOfSeats}</span>
                </div>
                <div className="meta-block">
                  <span className="meta-label">Date</span>
                  <span className="meta-value">
                    {booking.bookingDate ? new Date(booking.bookingDate).toLocaleDateString() : 'N/A'}
                  </span>
                </div>
                <div className="meta-block">
                  <span className="meta-label">Total</span>
                  <span className="meta-value price">${booking.totalPrice?.toFixed(2) || '0.00'}</span>
                </div>
              </div>
            </div>

            {booking.status === 'CONFIRMED' && (
              <div className="booking-card-actions">
                <button
                  className="btn btn-sm btn-secondary"
                  onClick={() => downloadTicket(booking.id)}
                >
                  <span>&#128196;</span> Download
                </button>
                <button
                  className="btn btn-sm btn-ghost"
                  onClick={() => handleCancel(booking.id)}
                  disabled={cancelling === booking.id}
                >
                  {cancelling === booking.id ? 'Cancelling...' : 'Cancel'}
                </button>
              </div>
            )}

            <div className="ticket-notch left" />
            <div className="ticket-notch right" />
          </div>
        ))}
      </div>
    </div>
  );
};

export default MyBookings;
