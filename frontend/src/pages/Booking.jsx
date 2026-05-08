import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { createBooking, downloadTicket, getBookedSeats } from '../api/bookings';
import { useAuth } from '../context/AuthContext';
import '../styles/Booking.css';

const TOTAL_SEATS = 64; // 8x8 grid for visual seat picker

const Booking = () => {
  const [selectedSeats, setSelectedSeats] = useState(new Set());
  const [takenSeats, setTakenSeats] = useState(new Set());
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [fetchingSeats, setFetchingSeats] = useState(true);
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

  const fetchBookedSeats = useCallback(async () => {
    try {
      const data = await getBookedSeats(movie.id, movie.showTime);
      setTakenSeats(new Set(data.bookedSeats || []));
    } catch (err) {
      // silently ignore — keep whatever we already have
    } finally {
      setFetchingSeats(false);
    }
  }, [movie.id, movie.showTime]);

  useEffect(() => {
    fetchBookedSeats();
  }, [fetchBookedSeats]);

  // Auto-refresh booked seats every 10 seconds
  useEffect(() => {
    if (success) return;
    const interval = setInterval(() => {
      fetchBookedSeats();
    }, 10000);
    return () => clearInterval(interval);
  }, [fetchBookedSeats, success]);

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
  const seatList = Array.from(selectedSeats).sort((a, b) => a - b);
  const seatNumbersText = seatList.map(i => i + 1).join(', ');

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

  // Submit booking request to backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) return;

    // Ensure movie has a show time set (required for booking)
    if (!movie.showTime) {
      setError('This movie has no show time set. Please contact admin.');
      return;
    }

    setLoading(true);
    try {
      const result = await createBooking(movie.id, seatList, movie.showTime);
      setBookingResult(result);
      setSuccess(true);
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Booking failed';
      setError(msg);
      // Refresh booked seats in case conflict came from another user
      fetchBookedSeats();
      // Deselect any newly-taken seats
      setSelectedSeats(prev => {
        const next = new Set(prev);
        for (const s of next) {
          if (takenSeats.has(s)) next.delete(s);
        }
        return next;
      });
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
              <span>#{seatNumbersText}</span>
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
          {fetchingSeats && (
            <div style={{ color: '#888', fontSize: '0.85rem', marginBottom: '12px' }}>
              Loading seat availability...
            </div>
          )}
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
                  disabled={isTaken || loading || fetchingSeats}
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
            <span>
              {seats} seat{seats !== 1 ? 's' : ''}
              {seats > 0 && <small style={{ color: '#888', display: 'block' }}>#{seatNumbersText}</small>}
            </span>
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
              disabled={loading || seats === 0 || fetchingSeats}
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
