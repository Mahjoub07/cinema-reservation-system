import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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
    return <LoadingSpinner text="Loading movie details..." />;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (!movie) {
    return <div className="empty-state">Movie not found</div>;
  }

  return (
    <div className="movie-details-container">
      <div className="movie-details-header">
        <div className="movie-poster-large">
          {movie.posterUrl ? (
            <img src={movie.posterUrl} alt={movie.title} style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '12px' }} />
          ) : (
            <h1>{movie.title}</h1>
          )}
        </div>
        <div className="movie-details-info">
          <h1>{movie.title}</h1>
          <p className="movie-genre">{movie.genre || 'N/A'}</p>
          <p className="movie-duration">{movie.duration} minutes</p>
          <p className="movie-price">Price: ${movie.price?.toFixed(2) || '0.00'}</p>
          <p className="movie-seats">{movie.availableSeats} seats available</p>
          {movie.showTime && (
            <p className="movie-time">
              Showtime: {new Date(movie.showTime).toLocaleString()}
            </p>
          )}
          {movie.description && (
            <p className="movie-description">{movie.description}</p>
          )}
          <button
            className="book-now-button"
            onClick={handleBookNow}
            disabled={movie.availableSeats === 0}
          >
            {movie.availableSeats === 0 ? 'Sold Out' : 'Book Now'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;
