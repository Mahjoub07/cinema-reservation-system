import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getAllMovies, addMovie, updateMovie, deleteMovie, uploadPoster, uploadBackdrop } from '../api/movies';
import { getAllBookingsAdmin, getDashboardStats, getAllUsers, promoteUser, demoteUser, deleteUser, createAdmin } from '../api/admin';
import api from '../api/axios';
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
  const [selectedMovies, setSelectedMovies] = useState(new Set());
  const [selectedBookings, setSelectedBookings] = useState(new Set());
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    genre: '',
    duration: '',
    showTime: '',
    availableSeats: '',
    price: '',
    posterUrl: '',
    backdropUrl: ''
  });
  const [showAdminModal, setShowAdminModal] = useState(false);
  const [adminFormData, setAdminFormData] = useState({ name: '', email: '', password: '' });
  const [userSearch, setUserSearch] = useState('');
  const [confirmModal, setConfirmModal] = useState({
    open: false,
    type: '',
    userId: null,
    userName: '',
    title: '',
    message: ''
  });

  const genres = [
    'Action', 'Adventure', 'Animation', 'Comedy', 'Crime',
    'Documentary', 'Drama', 'Fantasy', 'Horror', 'Musical',
    'Mystery', 'Romance', 'Sci-Fi', 'Thriller', 'Western'
  ];

  const { user, isMainAdmin } = useAuth();
  const { addToast } = useToast();

  useEffect(() => {
    if (user?.role !== 'ROLE_ADMIN' && user?.role !== 'ROLE_MAIN_ADMIN') return;
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
      posterUrl: '',
      backdropUrl: ''
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
      posterUrl: movie.posterUrl || '',
      backdropUrl: movie.backdropUrl || ''
    });
    setShowModal(true);
  };

  const handleDeleteMovie = async (id) => {
    if (!window.confirm('Are you sure you want to delete this movie?')) return;

    try {
      await deleteMovie(id);
      setMovies(prev => prev.filter(m => m.id !== id));
      addToast('Movie deleted successfully', 'success');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete movie';
      setError(msg);
      addToast(msg, 'error');
    }
  };

  const handleDeleteBooking = async (id) => {
    if (!window.confirm('Are you sure you want to delete this booking?')) return;

    try {
      await api.delete(`/bookings/${id}`);
      setBookings(prev => prev.filter(b => b.id !== id));
      addToast('Booking deleted successfully', 'success');
    } catch (err) {
      setError('Failed to delete booking');
    }
  };

  const handleSelectMovie = (id) => {
    setSelectedMovies(prev => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const handleSelectAllMovies = () => {
    if (selectedMovies.size === movies.length) {
      setSelectedMovies(new Set());
    } else {
      setSelectedMovies(new Set(movies.map(m => m.id)));
    }
  };

  const handleBulkDeleteMovies = async () => {
    if (selectedMovies.size === 0) return;
    const count = selectedMovies.size;
    if (!window.confirm(`Are you sure you want to delete ${count} movie(s)?`)) return;

    try {
      await api.delete('/movies/bulk', { data: Array.from(selectedMovies) });
      setMovies(prev => prev.filter(m => !selectedMovies.has(m.id)));
      setSelectedMovies(new Set());
      addToast(`${count} movie(s) deleted successfully`, 'success');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete movies';
      setError(msg);
      addToast(msg, 'error');
    }
  };

  const handleSelectBooking = (id) => {
    setSelectedBookings(prev => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const handleSelectAllBookings = () => {
    if (selectedBookings.size === bookings.length) {
      setSelectedBookings(new Set());
    } else {
      setSelectedBookings(new Set(bookings.map(b => b.id)));
    }
  };

  const handleBulkDeleteBookings = async () => {
    if (selectedBookings.size === 0) return;
    const count = selectedBookings.size;
    if (!window.confirm(`Are you sure you want to delete ${count} booking(s)?`)) return;

    try {
      await api.delete('/bookings/bulk', { data: Array.from(selectedBookings) });
      setBookings(prev => prev.filter(b => !selectedBookings.has(b.id)));
      setSelectedBookings(new Set());
      addToast(`${count} booking(s) deleted successfully`, 'success');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete bookings';
      setError(msg);
      addToast(msg, 'error');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingMovie) {
        await updateMovie(editingMovie.id, formData);
        setMovies(prev => prev.map(m => m.id === editingMovie.id ? { ...m, ...formData } : m));
      } else {
        const newMovie = await addMovie(formData);
        setMovies(prev => [...prev, newMovie]);
      }
      setShowModal(false);
    } catch (err) {
      setError('Failed to save movie');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  // Upload poster to Supabase Storage and update form data with returned URL
  const handlePosterUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      const result = await uploadPoster(file);
      setFormData(prev => ({ ...prev, posterUrl: result.url }));
      addToast('Poster uploaded successfully', 'success');
    } catch (err) {
      addToast(`Failed to upload poster: ${err.response?.data?.message || err.message || 'Unknown error'}`, 'error');
    }
  };

  // Upload backdrop to Supabase Storage and update form data with returned URL
  const handleBackdropUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      const result = await uploadBackdrop(file);
      setFormData(prev => ({ ...prev, backdropUrl: result.url }));
      addToast('Backdrop uploaded successfully', 'success');
    } catch (err) {
      addToast(`Failed to upload backdrop: ${err.response?.data?.message || err.message || 'Unknown error'}`, 'error');
    }
  };

  const handleDeleteUser = (userId, userName) => {
    openConfirmModal('delete', userId, userName, 'Delete User', `Are you sure you want to delete ${userName}?`);
  };

  const handlePromoteClick = (userId, userName) => {
    openConfirmModal('promote', userId, userName, 'Promote to Admin', `Are you sure you want to grant admin access to ${userName}?`);
  };

  const handleDemoteClick = (userId, userName) => {
    openConfirmModal('demote', userId, userName, 'Remove Admin Privileges', `Are you sure you want to remove admin privileges from ${userName}?`);
  };

  const openConfirmModal = (type, userId, userName, title, message) => {
    setConfirmModal({ open: true, type, userId, userName, title, message });
  };

  const closeConfirmModal = () => {
    setConfirmModal({ open: false, type: '', userId: null, userName: '', title: '', message: '' });
  };

  const executeConfirmAction = async () => {
    const { type, userId } = confirmModal;
    closeConfirmModal();

    try {
      if (type === 'promote') {
        await promoteUser(userId);
        setUsers(prev => prev.map(u => u.id === userId ? { ...u, role: 'ROLE_ADMIN' } : u));
        addToast('User promoted to admin', 'success');
      } else if (type === 'demote') {
        await demoteUser(userId);
        setUsers(prev => prev.map(u => u.id === userId ? { ...u, role: 'ROLE_USER' } : u));
        addToast('Admin demoted to user', 'success');
      } else if (type === 'delete') {
        await deleteUser(userId);
        setUsers(prev => prev.filter(u => u.id !== userId));
        addToast('User deleted', 'success');
      }
    } catch (err) {
      addToast(err.response?.data?.message || `Failed to ${type} user`, 'error');
    }
  };

  const handleAddAdmin = () => {
    setAdminFormData({ name: '', email: '', password: '' });
    setShowAdminModal(true);
  };

  const handleAdminFormChange = (e) => {
    const { name, value } = e.target;
    setAdminFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleAdminSubmit = async (e) => {
    e.preventDefault();
    try {
      const newAdmin = await createAdmin(adminFormData);
      setUsers(prev => [...prev, newAdmin]);
      setShowAdminModal(false);
      addToast('Admin created successfully', 'success');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to create admin', 'error');
    }
  };

  if (user?.role !== 'ROLE_ADMIN' && user?.role !== 'ROLE_MAIN_ADMIN') {
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
                <div className="toolbar-actions">
                  {selectedMovies.size > 0 && (
                    <button className="btn btn-sm btn-danger" onClick={handleBulkDeleteMovies}>
                      Delete Selected ({selectedMovies.size})
                    </button>
                  )}
                  <button className="btn btn-primary btn-sm" onClick={handleAddMovie}>
                    <span>+</span> Add Movie
                  </button>
                </div>
              </div>
              <div className="admin-table-wrap">
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th style={{ width: '40px' }}>
                          <input
                            type="checkbox"
                            checked={selectedMovies.size === movies.length && movies.length > 0}
                            onChange={handleSelectAllMovies}
                          />
                        </th>
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
                            <input
                              type="checkbox"
                              checked={selectedMovies.has(movie.id)}
                              onChange={() => handleSelectMovie(movie.id)}
                            />
                          </td>
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
                      <input type="number" name="duration" value={formData.duration} onChange={handleChange} required min="1" />
                    </div>

                    <div className="form-group">
                      <label>Show Time</label>
                      <input type="datetime-local" name="showTime" value={formData.showTime} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                      <label>Seats</label>
                      <input type="number" name="availableSeats" value={formData.availableSeats} onChange={handleChange} required min="1" />
                    </div>

                    <div className="form-group">
                      <label>Price ($)</label>
                      <input type="number" name="price" value={formData.price} onChange={handleChange} step="0.01" min="0" required />
                    </div>
                    <div className="form-group poster-group">
                      <label>Poster (Vertical)</label>
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
                    <div className="form-group poster-group">
                      <label>Backdrop (Horizontal for Carousel)</label>
                      <div className="poster-upload-card">
                        <input type="file" accept="image/*" onChange={handleBackdropUpload} />
                        {formData.backdropUrl ? (
                          <div className="poster-preview-inline">
                            <img src={formData.backdropUrl} alt="Backdrop" />
                          </div>
                        ) : (
                          <div className="poster-placeholder">
                            <span>&#127916;</span>
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
                <div className="toolbar-actions">
                  {selectedBookings.size > 0 && (
                    <button className="btn btn-sm btn-danger" onClick={handleBulkDeleteBookings}>
                      Delete Selected ({selectedBookings.size})
                    </button>
                  )}
                </div>
              </div>
              <div className="admin-table-wrap">
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th style={{ width: '40px' }}>
                          <input
                            type="checkbox"
                            checked={selectedBookings.size === bookings.length && bookings.length > 0}
                            onChange={handleSelectAllBookings}
                          />
                        </th>
                        <th>ID</th>
                        <th>Movie</th>
                        <th>User</th>
                        <th>Seats</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {bookings.map((booking) => (
                        <tr key={booking.id}>
                          <td>
                            <input
                              type="checkbox"
                              checked={selectedBookings.has(booking.id)}
                              onChange={() => handleSelectBooking(booking.id)}
                            />
                          </td>
                          <td><span className="cell-mono">#{booking.id}</span></td>
                          <td><span className="cell-title">{booking.movieTitle || 'N/A'}</span></td>
                          <td>{booking.userEmail || 'N/A'}</td>
                          <td>
                            {booking.seatNumbers && booking.seatNumbers.length > 0
                              ? '#' + booking.seatNumbers.map(s => s + 1).join(', ')
                              : booking.numberOfSeats}
                          </td>
                          <td>{booking.bookingDate ? new Date(booking.bookingDate).toLocaleString() : 'N/A'}</td>
                          <td>
                            <span className={`table-status ${booking.status.toLowerCase()}`}>
                              {booking.status}
                            </span>
                          </td>
                          <td>
                            <div className="row-actions">
                              <button className="btn btn-sm btn-ghost" onClick={() => handleDeleteBooking(booking.id)}>Delete</button>
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

          {activeTab === 'users' && (
            <div className="admin-section">
              <div className="section-toolbar">
                <h2>Users</h2>
                {isMainAdmin() && (
                  <button className="btn btn-primary btn-sm" onClick={handleAddAdmin}>
                    <span>+</span> Add Admin
                  </button>
                )}
              </div>

              <div className="user-search-bar">
                <input
                  type="text"
                  placeholder="Search by name or email..."
                  value={userSearch}
                  onChange={(e) => setUserSearch(e.target.value)}
                  className="user-search-input"
                />
                {userSearch && (
                  <button className="user-search-clear" onClick={() => setUserSearch('')}>&#10005;</button>
                )}
              </div>

              {(() => {
                const filtered = users.filter(u =>
                  !userSearch ||
                  u.name?.toLowerCase().includes(userSearch.toLowerCase()) ||
                  u.email?.toLowerCase().includes(userSearch.toLowerCase())
                );
                const admins = filtered.filter(u => u.role === 'ROLE_ADMIN' || u.role === 'ROLE_MAIN_ADMIN');
                const regulars = filtered.filter(u => u.role === 'ROLE_USER');

                return (
                  <>
                    {/* Administrators Section */}
                    {admins.length > 0 && (
                      <div className="user-section">
                        <h3 className="user-section-title">
                          <span className="user-section-icon">&#128187;</span>
                          Administrators
                          <span className="user-count-badge">{admins.length}</span>
                        </h3>
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
                                {admins.map((u) => (
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
                                      <span className={`role-badge role-${u.role.toLowerCase().replace('role_', '')}`}>
                                        {u.role === 'ROLE_MAIN_ADMIN' ? 'MAIN ADMIN' : 'ADMIN'}
                                      </span>
                                    </td>
                                    <td>
                                      <div className="row-actions">
                                        {isMainAdmin() && u.role !== 'ROLE_MAIN_ADMIN' && (
                                          <button
                                            className="btn btn-sm btn-secondary"
                                            onClick={() => handleDemoteClick(u.id, u.name)}
                                            title="Remove admin privileges"
                                          >
                                            Demote
                                          </button>
                                        )}
                                        {isMainAdmin() && u.role !== 'ROLE_MAIN_ADMIN' && (
                                          <button
                                            className="btn btn-sm btn-danger"
                                            onClick={() => handleDeleteUser(u.id, u.name)}
                                            title="Delete admin"
                                          >
                                            Delete
                                          </button>
                                        )}
                                        {u.role === 'ROLE_MAIN_ADMIN' && (
                                          <span className="protected-label">Protected</span>
                                        )}
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

                    {/* Regular Users Section */}
                    {regulars.length > 0 && (
                      <div className="user-section">
                        <h3 className="user-section-title">
                          <span className="user-section-icon">&#128100;</span>
                          Regular Users
                          <span className="user-count-badge">{regulars.length}</span>
                        </h3>
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
                                {regulars.map((u) => (
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
                                      <span className="role-badge role-user">USER</span>
                                    </td>
                                    <td>
                                      <div className="row-actions">
                                        {isMainAdmin() && (
                                          <button
                                            className="btn btn-sm btn-secondary"
                                            onClick={() => handlePromoteClick(u.id, u.name)}
                                            title="Grant admin access"
                                          >
                                            Promote
                                          </button>
                                        )}
                                        <button
                                          className="btn btn-sm btn-ghost"
                                          onClick={() => handleDeleteUser(u.id, u.name)}
                                          title="Delete user"
                                        >
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

                    {filtered.length === 0 && (
                      <div className="empty-state">
                        <p>No users match your search</p>
                      </div>
                    )}
                  </>
                );
              })()}
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

      {confirmModal.open && (
        <div className="modal-overlay">
          <div className="modal confirm-modal">
            <div className="modal-header">
              <h2>{confirmModal.title}</h2>
              <button className="modal-close" onClick={closeConfirmModal}>&#10005;</button>
            </div>
            <div className="confirm-body">
              <p className="confirm-message">{confirmModal.message}</p>
            </div>
            <div className="modal-buttons">
              <button
                className={`btn ${confirmModal.type === 'delete' ? 'btn-danger' : 'btn-primary'}`}
                onClick={executeConfirmAction}
              >
                Confirm
              </button>
              <button className="btn btn-secondary" onClick={closeConfirmModal}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Admin;
