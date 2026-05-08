import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllMovies, searchMovies, getMoviesByGenre } from '../api/movies';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/Movies.css';

const SkeletonCard = () => (
  <div className="movie-card skeleton-card">
    <div className="movie-poster skeleton" style={{ width: '100%', aspectRatio: '2 / 3' }} />
    <div className="movie-info" style={{ gap: '12px', display: 'flex', flexDirection: 'column' }}>
      <div className="skeleton" style={{ width: '60px', height: '20px', borderRadius: '20px' }} />
      <div className="skeleton" style={{ width: '80%', height: '16px' }} />
      <div className="skeleton" style={{ width: '50%', height: '14px' }} />
      <div className="skeleton" style={{ width: '100%', height: '40px', borderRadius: '10px', marginTop: '8px' }} />
    </div>
  </div>
);

const Movies = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const navigate = useNavigate();
  const { user } = useAuth();
  const [currentSlide, setCurrentSlide] = useState(0);

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
    setError('');
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
    setError('');
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
    setError('');
    try {
      const data = await getMoviesByGenre(genre);
      setMovies(data);
    } catch (err) {
      setError('Failed to filter movies');
    } finally {
      setLoading(false);
    }
  };

  const featuredMovies = movies.slice(0, 5);

  useEffect(() => {
    if (featuredMovies.length <= 1) return;
    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % featuredMovies.length);
    }, 5000);
    return () => clearInterval(interval);
  }, [featuredMovies.length]);

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

  if (error) {
    return (
      <div className="movies-container">
        <div className="movies-hero">
          <h1>Now Showing</h1>
          <p>Discover and book your favorite movies</p>
        </div>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="movies-container">
      {featuredMovies.length > 0 && !loading && !searchTerm && !selectedGenre && (
        <div className="hero-banner">
          <div 
            className="carousel-track"
            style={{ transform: `translateX(-${currentSlide * 100}%)` }}
          >
            {featuredMovies.map((movie, index) => (
              <div key={movie.id} className="hero-slide">
                <div className="hero-poster-side">
                  {movie.posterUrl ? (
                    <img src={movie.posterUrl} alt={movie.title} />
                  ) : (
                    <div className="poster-fallback">
                      <span>&#127909;</span>
                      <h3>{movie.title}</h3>
                    </div>
                  )}
                </div>
                <div className="hero-info-side">
                  <div className="hero-content" key={`content-${currentSlide}`}>
                    <span className="hero-badge">Featured</span>
                    <h2 className="hero-title">{movie.title}</h2>
                    <p className="hero-meta">
                      {movie.genre} &middot; {movie.duration} min &middot; ${movie.price?.toFixed(2)}
                    </p>
                    <p className="hero-description">{movie.description}</p>
                    <div className="hero-actions">
                      <button className="btn btn-primary btn-lg" onClick={() => handleBook(movie)}>
                        <span>&#127909;</span> Book Now
                      </button>
                      <button className="btn btn-secondary btn-lg" onClick={() => handleViewDetails(movie)}>
                        More Info
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {featuredMovies.length > 1 && (
            <div className="carousel-dots">
              {featuredMovies.map((_, index) => (
                <div
                  key={index}
                  className={`carousel-dot ${index === currentSlide ? 'active' : ''}`}
                />
              ))}
            </div>
          )}
        </div>
      )}

      <div className="movies-hero">
        <h1>Now Showing</h1>
        <p>Discover and book your favorite movies</p>
      </div>

      <div className="movies-filters">
        <div className="search-wrapper">
          <input
            type="text"
            placeholder="Search movies..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
          {searchTerm && (
            <button className="search-clear" onClick={() => setSearchTerm('')} aria-label="Clear search">
              &#10005;
            </button>
          )}
        </div>
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

      {loading ? (
        <div className="movies-grid">
          {Array.from({ length: 6 }).map((_, i) => (
            <SkeletonCard key={i} />
          ))}
        </div>
      ) : movies.length === 0 ? (
        <div className="empty-state">
          <p>No movies found</p>
          {(searchTerm || selectedGenre) && (
            <button className="btn btn-secondary" onClick={() => { setSearchTerm(''); setSelectedGenre(''); }}>
              Clear Filters
            </button>
          )}
        </div>
      ) : (
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
                  <span className="movie-genre">{movie.genre || 'N/A'}</span>
                  <span className={`movie-badge ${movie.availableSeats === 0 ? 'sold-out' : ''}`}>
                    {movie.availableSeats === 0 ? 'Sold Out' : `${movie.availableSeats} left`}
                  </span>
                </div>
                <h3 className="movie-title">{movie.title}</h3>
                <div className="movie-meta">
                  <span className="movie-duration">&#9201; {movie.duration} min</span>
                  <span className="movie-time">
                    &#128197; {movie.showTime ? new Date(movie.showTime).toLocaleDateString() : 'TBA'}
                  </span>
                </div>
                <p className="movie-price">${movie.price?.toFixed(2) || '0.00'}</p>
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
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Movies;
