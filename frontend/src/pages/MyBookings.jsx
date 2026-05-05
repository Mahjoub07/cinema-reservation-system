import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getMyBookings, cancelBooking } from '../api/bookings';
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
    return <div className="loading-spinner">Loading...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (bookings.length === 0) {
    return <div className="empty-state">No bookings yet</div>;
  }

  return (
    <div className="bookings-container">
      <h1>My Bookings</h1>
      <div className="bookings-list">
        {bookings.map((booking) => (
          <div key={booking.id} className={`booking-card ${booking.status === 'CANCELLED' ? 'cancelled' : ''}`}>
            <div className="booking-info">
              <h3>{booking.movieTitle || 'Unknown Movie'}</h3>
              <p>Seats: {booking.numberOfSeats}</p>
              <p>Booking Date: {booking.bookingDate ? new Date(booking.bookingDate).toLocaleString() : 'N/A'}</p>
              <p className={`status ${booking.status.toLowerCase()}`}>{booking.status}</p>
            </div>
            {booking.status === 'CONFIRMED' && (
              <button
                className="cancel-button"
                onClick={() => handleCancel(booking.id)}
                disabled={cancelling === booking.id}
              >
                {cancelling === booking.id ? 'Cancelling...' : 'Cancel Booking'}
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default MyBookings;
