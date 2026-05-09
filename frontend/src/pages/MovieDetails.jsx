import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getMovieById } from '../api/movies';
import { checkWatchlist, addToWatchlist, removeFromWatchlist } from '../api/watchlist';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/MovieDetails.css';

const MovieDetails = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { user } = useAuth();
  const { addToast } = useToast();
  const [inWatchlist, setInWatchlist] = useState(false);
  const [watchlistLoading, setWatchlistLoading] = useState(false);

  useEffect(() => {
    fetchMovie();
  }, [id]);

  const fetchMovie = async () => {
    setLoading(true);
    try {
      const data = await getMovieById(id);
      setMovie(data);
      if (user) {
        checkMovieInWatchlist(id);
      }
    } catch (err) {
      setError('Failed to load movie details');
    } finally {
      setLoading(false);
    }
  };

  const checkMovieInWatchlist = async (movieId) => {
    if (!user) return;
    try {
      const inList = await checkWatchlist(movieId);
      setInWatchlist(inList);
    } catch (err) {
      console.error('Failed to check watchlist:', err);
    }
  };

  const toggleWatchlist = async () => {
    if (!user) {
      addToast('Please login to add movies to watchlist', 'info');
      navigate('/login');
      return;
    }

    setWatchlistLoading(true);
    const wasInWatchlist = inWatchlist;

    try {
      if (wasInWatchlist) {
        await removeFromWatchlist(id);
        setInWatchlist(false);
        addToast('Removed from watchlist', 'info');
      } else {
        await addToWatchlist(id);
        setInWatchlist(true);
        addToast('Added to watchlist', 'success');
      }
    } catch (err) {
      addToast('Failed to update watchlist', 'error');
      setInWatchlist(wasInWatchlist);
    } finally {
      setWatchlistLoading(false);
    }
  };

  const handleBookNow = () => {
    if (!user) {
      navigate('/login', { state: { redirectTo: `/booking/${id}` } });
    } else {
      navigate(`/booking/${id}`, { state: { movie } });
    }
  };

  if (loading) {
    return (
      <div className="movie-details-container">
        <div className="details-skeleton">
          <div className="skeleton backdrop-skeleton" />
          <div className="skeleton info-skeleton" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="movie-details-container">
        <div className="error-message">{error}</div>
      </div>
    );
  }

  if (!movie) {
    return (
      <div className="movie-details-container">
        <div className="empty-state">
          <p>Movie not found</p>
          <Link to="/movies" className="btn btn-secondary">Browse Movies</Link>
        </div>
      </div>
    );
  }

  const available = movie.availableSeats > 0;

  return (
    <div className="movie-details-container">
      <div
        className="details-backdrop"
        style={movie.backdropUrl ? { backgroundImage: `url(${movie.backdropUrl})` } : {}}
      >
        <div className="backdrop-gradient" />
      </div>

      <div className="details-content">
        <div className="details-poster">
          {movie.posterUrl ? (
            <img src={movie.posterUrl} alt={movie.title} />
          ) : (
            <div className="poster-fallback-large">
              <span>&#127909;</span>
              <h3>{movie.title}</h3>
            </div>
          )}
        </div>

        <div className="details-info">
          <div className="details-badges">
            <span className="genre-badge">{movie.genre || 'N/A'}</span>
            <span className={`status-badge ${available ? 'available' : 'sold-out'}`}>
              {available ? `${movie.availableSeats} seats left` : 'Sold Out'}
            </span>
          </div>

          <h1 className="details-title">{movie.title}</h1>

          <div className="details-meta">
            <span className="meta-item">&#9201; {movie.duration} min</span>
            <span className="meta-item">&#128176; ${movie.price?.toFixed(2) || '0.00'}</span>
            {movie.showTime && (
              <span className="meta-item">&#128197; {new Date(movie.showTime).toLocaleString()}</span>
            )}
          </div>

          {movie.description && (
            <p className="details-description">{movie.description}</p>
          )}

          <div className="details-actions">
            <button
              className="details-watchlist-btn"
              onClick={toggleWatchlist}
              disabled={watchlistLoading}
            >
              <svg viewBox="0 0 24 24" fill={inWatchlist ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
              </svg>
              {watchlistLoading ? 'Loading...' : inWatchlist ? 'In Watchlist' : 'Add to Watchlist'}
            </button>
            <button
              className="btn btn-primary btn-lg"
              onClick={handleBookNow}
              disabled={!available}
            >
              <span>&#127909;</span>
              {available ? 'Book Tickets' : 'Sold Out'}
            </button>
            <Link to="/movies" className="btn btn-secondary btn-lg">
              &#8592; Back to Movies
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;
