import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllMovies, searchMovies, getMoviesByGenre } from '../api/movies';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/Movies.css';

const Movies = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const navigate = useNavigate();
  const { user } = useAuth();

  const genres = [
    'Action', 'Adventure', 'Animation', 'Comedy', 'Crime',
    'Documentary', 'Drama', 'Fantasy', 'Horror', 'Musical',
    'Mystery', 'Romance', 'Sci-Fi', 'Thriller', 'Western'
  ];

  useEffect(() => {
    fetchMovies();
  }, []);

  useEffect(() => {
    const debounceTimer = setTimeout(() => {
      if (searchTerm) {
        handleSearch(searchTerm);
      } else {
        fetchMovies();
      }
    }, 500);
    return () => clearTimeout(debounceTimer);
  }, [searchTerm]);

  useEffect(() => {
    if (selectedGenre) {
      handleGenreFilter(selectedGenre);
    } else if (!searchTerm) {
      fetchMovies();
    }
  }, [selectedGenre]);

  const fetchMovies = async () => {
    setLoading(true);
    try {
      const data = await getAllMovies();
      setMovies(data);
    } catch (err) {
      setError('Failed to load movies');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (term) => {
    setLoading(true);
    try {
      const data = await searchMovies(term);
      setMovies(data);
    } catch (err) {
      setError('Failed to search movies');
    } finally {
      setLoading(false);
    }
  };

  const handleGenreFilter = async (genre) => {
    setLoading(true);
    try {
      const data = await getMoviesByGenre(genre);
      setMovies(data);
    } catch (err) {
      setError('Failed to filter movies');
    } finally {
      setLoading(false);
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
    return <LoadingSpinner text="Loading movies..." />;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (movies.length === 0) {
    return (
      <div className="movies-container">
        <div className="movies-hero">
          <h1>Now Showing</h1>
          <p>Discover and book your favorite movies</p>
        </div>
        <div className="empty-state">No movies found</div>
      </div>
    );
  }

  return (
    <div className="movies-container">
      <div className="movies-hero">
        <h1>Now Showing</h1>
        <p>Discover and book your favorite movies</p>
      </div>
      <div className="movies-filters">
        <input
          type="text"
          placeholder="Search movies..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <select
          value={selectedGenre}
          onChange={(e) => setSelectedGenre(e.target.value)}
          className="genre-select"
        >
          <option value="">All Genres</option>
          {genres.map((genre) => (
            <option key={genre} value={genre}>{genre}</option>
          ))}
        </select>
      </div>
      <div className="movies-grid">
        {movies.map((movie) => (
          <div key={movie.id} className="movie-card">
            <div className="movie-poster">
              <h3>{movie.title}</h3>
            </div>
            <div className="movie-info">
              <p className="movie-genre">{movie.genre || 'N/A'}</p>
              <p className="movie-duration">{movie.duration} min</p>
              <p className="movie-seats">{movie.availableSeats} seats available</p>
              <p className="movie-time">
                {movie.showTime ? new Date(movie.showTime).toLocaleString() : 'TBA'}
              </p>
              {movie.description && (
                <p className="movie-description">{movie.description}</p>
              )}
              <div className="movie-actions">
                <button
                  className="details-button"
                  onClick={() => handleViewDetails(movie)}
                >
                  View Details
                </button>
                <button
                  className="book-button"
                  onClick={() => handleBook(movie)}
                  disabled={movie.availableSeats === 0}
                >
                  {movie.availableSeats === 0 ? 'Sold Out' : 'Book Now'}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Movies;
