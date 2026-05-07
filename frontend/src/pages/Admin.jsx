import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getAllMovies, addMovie, updateMovie, deleteMovie, uploadPoster } from '../api/movies';
import { getAllBookingsAdmin, getDashboardStats, getAllUsers, updateUserRole, deleteUser, createAdmin } from '../api/admin';
import LoadingSpinner from '../components/LoadingSpinner';
import '../styles/Admin.css';

const Admin = () => {
  const [movies, setMovies] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [users, setUsers] = useState([]);
  const [stats, setStats] = useState(null);
  const [activeTab, setActiveTab] = useState('dashboard');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingMovie, setEditingMovie] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    genre: '',
    duration: '',
    showTime: '',
    availableSeats: '',
    price: '',
    posterUrl: ''
  });
  const [showAdminModal, setShowAdminModal] = useState(false);
  const [adminFormData, setAdminFormData] = useState({ name: '', email: '', password: '' });

  const genres = [
    'Action', 'Adventure', 'Animation', 'Comedy', 'Crime',
    'Documentary', 'Drama', 'Fantasy', 'Horror', 'Musical',
    'Mystery', 'Romance', 'Sci-Fi', 'Thriller', 'Western'
  ];

  const { user } = useAuth();
  const { addToast } = useToast();

  useEffect(() => {
    if (user?.role !== 'ROLE_ADMIN') return;
    fetchData();
  }, [user, activeTab]);

  const fetchData = async () => {
    setLoading(true);
    try {
      if (activeTab === 'dashboard') {
        const data = await getDashboardStats();
        setStats(data);
      } else if (activeTab === 'movies') {
        const data = await getAllMovies();
        setMovies(data);
      } else if (activeTab === 'bookings') {
        const data = await getAllBookingsAdmin();
        setBookings(data);
      } else if (activeTab === 'users') {
        const data = await getAllUsers();
        setUsers(data);
      }
    } catch (err) {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleAddMovie = () => {
    setEditingMovie(null);
    setFormData({
      title: '',
      description: '',
      genre: '',
      duration: '',
      showTime: '',
      availableSeats: '',
      price: '',
      posterUrl: ''
    });
    setShowModal(true);
  };

  const handleEditMovie = (movie) => {
    setEditingMovie(movie);
    setFormData({
      title: movie.title,
      description: movie.description || '',
      genre: movie.genre || '',
      duration: movie.duration,
      showTime: movie.showTime ? movie.showTime.slice(0, 16) : '',
      availableSeats: movie.availableSeats,
      price: movie.price || '',
      posterUrl: movie.posterUrl || ''
    });
    setShowModal(true);
  };

  const handleDeleteMovie = async (id) => {
    if (!window.confirm('Are you sure you want to delete this movie?')) return;

    try {
      await deleteMovie(id);
      setMovies(movies.filter(m => m.id !== id));
    } catch (err) {
      setError('Failed to delete movie');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingMovie) {
        await updateMovie(editingMovie.id, formData);
        setMovies(movies.map(m => m.id === editingMovie.id ? { ...m, ...formData } : m));
      } else {
        const newMovie = await addMovie(formData);
        setMovies([...movies, newMovie]);
      }
      setShowModal(false);
    } catch (err) {
      setError('Failed to save movie');
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePosterUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      const result = await uploadPoster(file);
      setFormData({ ...formData, posterUrl: result.url });
      addToast('Poster uploaded successfully', 'success');
    } catch (err) {
      addToast('Failed to upload poster', 'error');
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      await updateUserRole(userId, newRole);
      setUsers(users.map(u => u.id === userId ? { ...u, role: newRole } : u));
    } catch (err) {
      setError('Failed to update role');
    }
  };

  const handleDeleteUser = async (userId) => {
    if (!window.confirm('Are you sure you want to delete this user?')) return;

    try {
      await deleteUser(userId);
      setUsers(users.filter(u => u.id !== userId));
    } catch (err) {
      setError('Failed to delete user');
    }
  };

  const handleAddAdmin = () => {
    setAdminFormData({ name: '', email: '', password: '' });
    setShowAdminModal(true);
  };

  const handleAdminFormChange = (e) => {
    setAdminFormData({ ...adminFormData, [e.target.name]: e.target.value });
  };

  const handleAdminSubmit = async (e) => {
    e.preventDefault();
    try {
      const newAdmin = await createAdmin(adminFormData);
      setUsers([...users, newAdmin]);
      setShowAdminModal(false);
      addToast('Admin created successfully', 'success');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to create admin', 'error');
    }
  };

  if (user?.role !== 'ROLE_ADMIN') {
    return (
      <div className="admin-access-denied">
        <div className="error-message">Access Denied</div>
      </div>
    );
  }

  const tabItems = [
    { key: 'dashboard', label: 'Dashboard', icon: '&#128202;' },
    { key: 'movies', label: 'Movies', icon: '&#127909;' },
    { key: 'bookings', label: 'Bookings', icon: '&#127915;' },
    { key: 'users', label: 'Users', icon: '&#128100;' }
  ];

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Admin Dashboard</h1>
      </div>

      <div className="admin-tabs">
        {tabItems.map(tab => (
          <button
            key={tab.key}
            className={activeTab === tab.key ? 'active' : ''}
            onClick={() => setActiveTab(tab.key)}
          >
            <span dangerouslySetInnerHTML={{ __html: tab.icon }} />
            {tab.label}
          </button>
        ))}
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <LoadingSpinner text="Loading admin data..." />
      ) : (
        <>
          {activeTab === 'dashboard' && (
            <div className="admin-section">
              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-icon">&#128100;</div>
                  <div className="stat-info">
                    <h3>Total Users</h3>
                    <p className="stat-number">{stats?.totalUsers || 0}</p>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">&#127915;</div>
                  <div className="stat-info">
                    <h3>Total Bookings</h3>
                    <p className="stat-number">{stats?.totalBookings || 0}</p>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">&#127909;</div>
                  <div className="stat-info">
                    <h3>Total Movies</h3>
                    <p className="stat-number">{stats?.totalMovies || 0}</p>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">&#9989;</div>
                  <div className="stat-info">
                    <h3>Active Bookings</h3>
                    <p className="stat-number">{stats?.activeBookings || 0}</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'movies' && !showModal && (
            <div className="admin-section">
              <div className="section-toolbar">
                <h2>Movies</h2>
                <button className="btn btn-primary btn-sm" onClick={handleAddMovie}>
                  <span>+</span> Add Movie
                </button>
              </div>
              <div className="admin-table-wrap">
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th>Title</th>
                        <th>Genre</th>
                        <th>Duration</th>
                        <th>Seats</th>
                        <th>Show Time</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {movies.map((movie) => (
                        <tr key={movie.id}>
                          <td>
                            <span className="cell-title">{movie.title}</span>
                          </td>
                          <td><span className="cell-badge">{movie.genre || 'N/A'}</span></td>
                          <td>{movie.duration} min</td>
                          <td>{movie.availableSeats}</td>
                          <td>{movie.showTime ? new Date(movie.showTime).toLocaleString() : 'N/A'}</td>
                          <td>
                            <div className="row-actions">
                              <button className="btn btn-sm btn-secondary" onClick={() => handleEditMovie(movie)}>Edit</button>
                              <button className="btn btn-sm btn-ghost" onClick={() => handleDeleteMovie(movie.id)}>Delete</button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'movies' && showModal && (
            <div className="admin-section movie-form-page">
              <div className="form-page-toolbar">
                <button className="btn btn-sm btn-secondary back-btn" onClick={() => setShowModal(false)}>
                  &#8592; Back to Movies
                </button>
                <h2>{editingMovie ? 'Edit Movie' : 'Add New Movie'}</h2>
                <span></span>
              </div>
              <div className="movie-form-inner">
                <form onSubmit={handleSubmit}>
                  <div className="form-page-grid">
                    <div className="form-group form-group-full">
                      <label>Title</label>
                      <input type="text" name="title" value={formData.title} onChange={handleChange} required />
                    </div>

                    <div className="form-group">
                      <label>Genre</label>
                      <select name="genre" value={formData.genre} onChange={handleChange} required>
                        <option value="">Select genre</option>
                        {genres.map((genre) => (
                          <option key={genre} value={genre}>{genre}</option>
                        ))}
                      </select>
                    </div>
                    <div className="form-group">
                      <label>Duration (min)</label>
                      <input type="number" name="duration" value={formData.duration} onChange={handleChange} required />
                    </div>

                    <div className="form-group">
                      <label>Show Time</label>
                      <input type="datetime-local" name="showTime" value={formData.showTime} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                      <label>Seats</label>
                      <input type="number" name="availableSeats" value={formData.availableSeats} onChange={handleChange} required />
                    </div>

                    <div className="form-group">
                      <label>Price ($)</label>
                      <input type="number" name="price" value={formData.price} onChange={handleChange} step="0.01" min="0" />
                    </div>
                    <div className="form-group poster-group">
                      <label>Poster</label>
                      <div className="poster-upload-card">
                        <input type="file" accept="image/*" onChange={handlePosterUpload} />
                        {formData.posterUrl ? (
                          <div className="poster-preview-inline">
                            <img src={formData.posterUrl} alt="Poster" />
                          </div>
                        ) : (
                          <div className="poster-placeholder">
                            <span>&#127909;</span>
                            <small>Drop image or click</small>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="form-group form-group-full">
                    <label>Description</label>
                    <textarea name="description" value={formData.description} onChange={handleChange} rows={2} placeholder="Short plot summary..." />
                  </div>

                  <div className="form-page-actions">
                    <button type="submit" className="btn btn-primary btn-lg">
                      {editingMovie ? 'Update Movie' : 'Add Movie'}
                    </button>
                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {activeTab === 'bookings' && (
            <div className="admin-section">
              <div className="section-toolbar">
                <h2>Bookings</h2>
              </div>
              <div className="admin-table-wrap">
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Movie</th>
                        <th>User</th>
                        <th>Seats</th>
                        <th>Date</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {bookings.map((booking) => (
                        <tr key={booking.id}>
                          <td><span className="cell-mono">#{booking.id}</span></td>
                          <td><span className="cell-title">{booking.movieTitle || 'N/A'}</span></td>
                          <td>{booking.userEmail || 'N/A'}</td>
                          <td>{booking.numberOfSeats}</td>
                          <td>{booking.bookingDate ? new Date(booking.bookingDate).toLocaleString() : 'N/A'}</td>
                          <td>
                            <span className={`table-status ${booking.status.toLowerCase()}`}>
                              {booking.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'users' && (
            <div className="admin-section">
              <div className="section-toolbar">
                <h2>Users</h2>
                <button className="btn btn-primary btn-sm" onClick={handleAddAdmin}>
                  <span>+</span> Add Admin
                </button>
              </div>
              <div className="admin-table-wrap">
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.map((u) => (
                        <tr key={u.id}>
                          <td><span className="cell-mono">#{u.id}</span></td>
                          <td>
                            <div className="user-cell">
                              <span className="user-avatar-sm">{(u.name || 'U').charAt(0).toUpperCase()}</span>
                              <span className="cell-title">{u.name || 'N/A'}</span>
                            </div>
                          </td>
                          <td>{u.email}</td>
                          <td>
                            <select
                              value={u.role}
                              onChange={(e) => handleRoleChange(u.id, e.target.value)}
                              className="role-select"
                            >
                              <option value="ROLE_USER">USER</option>
                              <option value="ROLE_ADMIN">ADMIN</option>
                            </select>
                          </td>
                          <td>
                            <div className="row-actions">
                              <button className="btn btn-sm btn-ghost" onClick={() => handleDeleteUser(u.id)}>
                                Delete
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {showAdminModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>Add Admin</h2>
              <button className="modal-close" onClick={() => setShowAdminModal(false)}>&#10005;</button>
            </div>
            <form onSubmit={handleAdminSubmit}>
              <div className="form-group">
                <label>Name</label>
                <input type="text" name="name" value={adminFormData.name} onChange={handleAdminFormChange} required />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input type="email" name="email" value={adminFormData.email} onChange={handleAdminFormChange} required />
              </div>
              <div className="form-group">
                <label>Password</label>
                <input type="password" name="password" value={adminFormData.password} onChange={handleAdminFormChange} required minLength={6} />
              </div>
              <div className="modal-buttons">
                <button type="submit" className="btn btn-primary">Create Admin</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowAdminModal(false)}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Admin;
