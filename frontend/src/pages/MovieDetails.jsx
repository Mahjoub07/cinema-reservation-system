import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getMovieById } from '../api/movies';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/MovieDetails.css';

const MovieDetails = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    fetchMovie();
  }, [id]);

  const fetchMovie = async () => {
    setLoading(true);
    try {
      const data = await getMovieById(id);
      setMovie(data);
    } catch (err) {
      setError('Failed to load movie details');
    } finally {
      setLoading(false);
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
