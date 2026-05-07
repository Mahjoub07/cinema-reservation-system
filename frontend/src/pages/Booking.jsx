import { useState, useMemo } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { createBooking, downloadTicket } from '../api/bookings';
import { useAuth } from '../context/AuthContext';
import '../styles/Booking.css';

const TOTAL_SEATS = 64; // 8x8 grid for visual seat picker

const Booking = () => {
  const [selectedSeats, setSelectedSeats] = useState(new Set());
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

  // Simulate taken seats based on availableSeats vs total capacity
  const takenSeats = useMemo(() => {
    const taken = new Set();
    const takenCount = Math.max(0, TOTAL_SEATS - (movie.availableSeats || 0));
    let count = 0;
    for (let i = 0; i < TOTAL_SEATS && count < takenCount; i++) {
      // Deterministic "random" pattern
      if ((i * 7 + 3) % 5 !== 0) {
        taken.add(i);
        count++;
      }
    }
    return taken;
  }, [movie.availableSeats]);

  const toggleSeat = (index) => {
    if (takenSeats.has(index)) return;
    setError('');
    setSelectedSeats(prev => {
      const next = new Set(prev);
      if (next.has(index)) {
        next.delete(index);
      } else {
        next.add(index);
      }
      return next;
    });
  };

  const seats = selectedSeats.size;
  const totalPrice = (movie.price || 0) * seats;

  const validateForm = () => {
    if (seats <= 0) {
      setError('Please select at least one seat');
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
        <div className="confirmation-card">
          <div className="confirmation-icon">&#127881;</div>
          <h2>Booking Confirmed!</h2>
          <p className="confirmation-subtitle">Your tickets have been booked successfully.</p>
          <div className="confirmation-details">
            <div className="confirmation-row">
              <span>Movie</span>
              <span>{movie.title}</span>
            </div>
            <div className="confirmation-row">
              <span>Seats</span>
              <span>{bookingResult?.numberOfSeats || seats}</span>
            </div>
            <div className="confirmation-row">
              <span>Total</span>
              <span className="confirmation-price">${bookingResult?.totalPrice?.toFixed(2) || totalPrice.toFixed(2)}</span>
            </div>
          </div>
          <div className="confirmation-actions">
            <button className="btn btn-primary btn-lg" onClick={() => downloadTicket(bookingResult.id)}>
              <span>&#128196;</span> Download Ticket
            </button>
            <Link to="/my-bookings" className="btn btn-secondary btn-lg">
              View My Bookings
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="booking-layout">
      <div className="booking-main">
        <div className="booking-header">
          <Link to="/movies" className="back-link">&#8592; Back to Movies</Link>
          <h1>Book Tickets</h1>
          <p className="booking-movie-title">{movie.title}</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="seat-section">
          <div className="screen">Screen</div>
          <div className="seat-grid">
            {Array.from({ length: TOTAL_SEATS }).map((_, i) => {
              const isTaken = takenSeats.has(i);
              const isSelected = selectedSeats.has(i);
              return (
                <button
                  key={i}
                  type="button"
                  className={`seat ${isTaken ? 'taken' : ''} ${isSelected ? 'selected' : ''}`}
                  onClick={() => toggleSeat(i)}
                  disabled={isTaken || loading}
                  aria-label={`Seat ${i + 1} ${isTaken ? 'taken' : isSelected ? 'selected' : 'available'}`}
                />
              );
            })}
          </div>
          <div className="seat-legend">
            <div className="legend-item"><span className="seat-sample" /> Available</div>
            <div className="legend-item"><span className="seat-sample taken" /> Taken</div>
            <div className="legend-item"><span className="seat-sample selected" /> Selected</div>
          </div>
        </div>
      </div>

      <div className="booking-sidebar">
        <div className="sidebar-card">
          <h3>Booking Summary</h3>
          <div className="summary-row">
            <span>Movie</span>
            <span>{movie.title}</span>
          </div>
          <div className="summary-row">
            <span>Showtime</span>
            <span>{movie.showTime ? new Date(movie.showTime).toLocaleString() : 'TBA'}</span>
          </div>
          <div className="summary-row">
            <span>Price / seat</span>
            <span>${movie.price?.toFixed(2) || '0.00'}</span>
          </div>
          <div className="summary-row">
            <span>Selected</span>
            <span>{seats} seat{seats !== 1 ? 's' : ''}</span>
          </div>
          <div className="summary-divider" />
          <div className="summary-row total">
            <span>Total</span>
            <span>${totalPrice.toFixed(2)}</span>
          </div>
          <form onSubmit={handleSubmit}>
            <button
              type="submit"
              className="btn btn-primary btn-lg"
              disabled={loading || seats === 0}
              style={{ width: '100%', marginTop: '16px' }}
            >
              {loading ? 'Processing...' : 'Confirm Booking'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Booking;
