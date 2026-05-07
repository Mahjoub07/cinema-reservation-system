import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { createBooking, downloadTicket } from '../api/bookings';
import { useAuth } from '../context/AuthContext';
import '../styles/Booking.css';

const Booking = () => {
  const [seats, setSeats] = useState(1);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [bookingResult, setBookingResult] = useState(null);
  
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const movie = location.state?.movie;

  if (!movie) {
    navigate('/movies');
    return null;
  }

  const validateForm = () => {
    if (seats <= 0) {
      setError('Number of seats must be greater than 0');
      return false;
    }
    if (seats > movie.availableSeats) {
      setError(`Only ${movie.availableSeats} seats available`);
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      const result = await createBooking(movie.id, seats);
      setBookingResult(result);
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Booking failed');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="booking-container">
        <div className="success-message">
          <h2>Booking Confirmed!</h2>
          <p>Your tickets have been booked successfully.</p>
          <p>Total Price: ${bookingResult?.totalPrice?.toFixed(2) || '0.00'}</p>
          <button className="booking-button" onClick={() => downloadTicket(bookingResult.id)}>
            Download Ticket (PDF)
          </button>
          <button className="cancel-button" onClick={() => navigate('/my-bookings')}>
            View My Bookings
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="booking-container">
      <div className="booking-box">
        <h1>Book Tickets</h1>
        <div className="movie-summary">
          <h2>{movie.title}</h2>
          <p>{movie.genre || 'N/A'}</p>
          <p>{movie.showTime ? new Date(movie.showTime).toLocaleString() : 'TBA'}</p>
          <p>Available seats: {movie.availableSeats}</p>
          <p>Price: ${movie.price?.toFixed(2) || '0.00'}</p>
          <p>Total: ${((movie.price || 0) * seats).toFixed(2)}</p>
        </div>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Number of Seats</label>
            <input
              type="number"
              min="1"
              max={movie.availableSeats}
              value={seats}
              onChange={(e) => setSeats(parseInt(e.target.value))}
              disabled={loading}
            />
          </div>
          <button type="submit" className="booking-button" disabled={loading}>
            {loading ? 'Processing...' : 'Confirm Booking'}
          </button>
        </form>
        <button className="cancel-button" onClick={() => navigate('/movies')} disabled={loading}>
          Cancel
        </button>
      </div>
    </div>
  );
};

export default Booking;
