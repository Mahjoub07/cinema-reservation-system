import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getWatchlist, removeFromWatchlist } from '../api/watchlist';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/Movies.css';

const Watchlist = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [removing, setRemoving] = useState(null);
  const navigate = useNavigate();
  const { user } = useAuth();
  const { addToast } = useToast();

  useEffect(() => {
    fetchWatchlist();
  }, [user]);

  const fetchWatchlist = async () => {
    if (!user) return;
    setLoading(true);
    setError('');
    try {
      const data = await getWatchlist();
      // Extract movie objects from watchlist items
      const moviesData = data.map(item => item.movie);
      setMovies(moviesData);
    } catch (err) {
      setError('Failed to load watchlist');
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveFromWatchlist = async (movieId) => {
    setRemoving(movieId);
    try {
      await removeFromWatchlist(movieId);
      setMovies(movies.filter(m => m.id !== movieId));
      addToast('Removed from watchlist', 'info');
    } catch (err) {
      setError('Failed to remove movie from watchlist');
      addToast('Failed to remove movie', 'error');
    } finally {
      setRemoving(null);
    }
  };

  const handleBook = (movie) => {
    if (!user) {
      navigate('/login', { state: { redirectTo: `/booking/${movie.id}` } });
    } else {
      navigate(`/booking/${movie.id}`, { state: { movie } });
    }
  };

  const handleViewDetails = (movie) => {
    navigate(`/movie/${movie.id}`);
  };

  if (loading) {
    return <LoadingSpinner text="Loading your watchlist..." />;
  }

  if (error) {
    return (
      <div className="movies-container">
        <h1>My Watchlist</h1>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  if (movies.length === 0) {
    return (
      <div className="movies-container">
        <div className="movies-hero">
          <h1>My Watchlist</h1>
          <p>Save movies to watch later</p>
        </div>
        <div className="empty-state">
          <p>Your watchlist is empty</p>
          <span>Add movies to your watchlist to keep track of movies you want to watch.</span>
          <button
            className="btn btn-primary"
            onClick={() => navigate('/movies')}
            style={{ marginTop: '16px' }}
          >
            Browse Movies
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="movies-container">
      <div className="movies-hero">
        <h1>My Watchlist</h1>
        <p>Save movies to watch later</p>
      </div>

      <div className="movies-grid">
        {movies.map((movie) => (
          <div key={movie.id} className="movie-card">
            <div className="movie-poster">
              {movie.posterUrl ? (
                <img src={movie.posterUrl} alt={movie.title} />
              ) : (
                <div className="poster-fallback">
                  <span>&#127909;</span>
                  <h3>{movie.title}</h3>
                </div>
              )}
              <div className="poster-overlay">
                <button className="overlay-btn" onClick={() => handleViewDetails(movie)}>
                  View Details
                </button>
              </div>
            </div>
            <div className="movie-info">
              <div className="movie-meta-row">
                {movie.genre && <span className="movie-genre">{movie.genre}</span>}
                <span className={`movie-badge ${movie.availableSeats === 0 ? 'sold-out' : ''}`}>
                  {movie.availableSeats === 0 ? 'Sold Out' : `${movie.availableSeats} left`}
                </span>
              </div>
              <h3 className="movie-title">{movie.title}</h3>
              <div className="movie-meta">
                {movie.duration && <span className="movie-duration">&#9201; {movie.duration} min</span>}
                <span className="movie-time">
                  &#128197; {movie.showTime ? new Date(movie.showTime).toLocaleDateString() : 'TBA'}
                </span>
              </div>
              <p className="movie-price">
                {movie.price !== undefined ? `$${movie.price.toFixed(2)}` : 'Price TBA'}
              </p>
              {movie.description && (
                <p className="movie-description">{movie.description}</p>
              )}
              <div className="movie-actions">
                <button
                  className="btn btn-primary"
                  onClick={() => handleBook(movie)}
                  disabled={movie.availableSeats === 0}
                >
                  {movie.availableSeats === 0 ? 'Sold Out' : 'Book Now'}
                </button>
                <button
                  className="btn btn-ghost"
                  onClick={() => handleRemoveFromWatchlist(movie.id)}
                  disabled={removing === movie.id}
                >
                  {removing === movie.id ? 'Removing...' : '✕ Remove'}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Watchlist;

