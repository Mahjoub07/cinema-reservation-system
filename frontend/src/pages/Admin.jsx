import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getAllMovies, addMovie, updateMovie, deleteMovie } from '../api/movies';
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
    availableSeats: ''
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
      availableSeats: ''
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
      availableSeats: movie.availableSeats
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
    return <div className="error-message">Access Denied</div>;
  }

  return (
    <div className="admin-container">
      <h1>Admin Panel</h1>
      <div className="admin-tabs">
        <button className={activeTab === 'dashboard' ? 'active' : ''} onClick={() => setActiveTab('dashboard')}>
          Dashboard
        </button>
        <button className={activeTab === 'movies' ? 'active' : ''} onClick={() => setActiveTab('movies')}>
          Movies
        </button>
        <button className={activeTab === 'bookings' ? 'active' : ''} onClick={() => setActiveTab('bookings')}>
          Bookings
        </button>
        <button className={activeTab === 'users' ? 'active' : ''} onClick={() => setActiveTab('users')}>
          Users
        </button>
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
                  <h3>Total Users</h3>
                  <p className="stat-number">{stats?.totalUsers || 0}</p>
                </div>
                <div className="stat-card">
                  <h3>Total Bookings</h3>
                  <p className="stat-number">{stats?.totalBookings || 0}</p>
                </div>
                <div className="stat-card">
                  <h3>Total Movies</h3>
                  <p className="stat-number">{stats?.totalMovies || 0}</p>
                </div>
                <div className="stat-card">
                  <h3>Active Bookings</h3>
                  <p className="stat-number">{stats?.activeBookings || 0}</p>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'movies' && (
            <div className="admin-section">
              <button className="add-button" onClick={handleAddMovie}>Add Movie</button>
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
                        <td>{movie.title}</td>
                        <td>{movie.genre || 'N/A'}</td>
                        <td>{movie.duration} min</td>
                        <td>{movie.availableSeats}</td>
                        <td>{movie.showTime ? new Date(movie.showTime).toLocaleString() : 'N/A'}</td>
                        <td>
                          <button className="edit-button" onClick={() => handleEditMovie(movie)}>Edit</button>
                          <button className="delete-button" onClick={() => handleDeleteMovie(movie.id)}>Delete</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === 'bookings' && (
            <div className="admin-section">
              <div className="admin-table">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Movie</th>
                      <th>User Email</th>
                      <th>Seats</th>
                      <th>Date</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bookings.map((booking) => (
                      <tr key={booking.id}>
                        <td>{booking.id}</td>
                        <td>{booking.movieTitle || 'N/A'}</td>
                        <td>{booking.userEmail || 'N/A'}</td>
                        <td>{booking.numberOfSeats}</td>
                        <td>{booking.bookingDate ? new Date(booking.bookingDate).toLocaleString() : 'N/A'}</td>
                        <td className={`status ${booking.status.toLowerCase()}`}>{booking.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === 'users' && (
            <div className="admin-section">
              <button className="add-button" onClick={handleAddAdmin}>Add Admin</button>
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
                    {users.map((user) => (
                      <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.name || 'N/A'}</td>
                        <td>{user.email}</td>
                        <td>
                          <select
                            value={user.role}
                            onChange={(e) => handleRoleChange(user.id, e.target.value)}
                            className="role-select"
                          >
                            <option value="ROLE_USER">USER</option>
                            <option value="ROLE_ADMIN">ADMIN</option>
                          </select>
                        </td>
                        <td>
                          <button className="delete-button" onClick={() => handleDeleteUser(user.id)}>
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </>
      )}

      {showAdminModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Add Admin</h2>
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
                <button type="submit" className="submit-button">Create Admin</button>
                <button type="button" className="cancel-button" onClick={() => setShowAdminModal(false)}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>{editingMovie ? 'Edit Movie' : 'Add Movie'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Title</label>
                <input type="text" name="title" value={formData.title} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea name="description" value={formData.description} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Genre</label>
                <select name="genre" value={formData.genre} onChange={handleChange} required>
                  <option value="">Select a genre</option>
                  {genres.map((genre) => (
                    <option key={genre} value={genre}>{genre}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Duration (minutes)</label>
                <input type="number" name="duration" value={formData.duration} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label>Show Time</label>
                <input type="datetime-local" name="showTime" value={formData.showTime} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Available Seats</label>
                <input type="number" name="availableSeats" value={formData.availableSeats} onChange={handleChange} required />
              </div>
              <div className="modal-buttons">
                <button type="submit" className="submit-button">
                  {editingMovie ? 'Update' : 'Add'}
                </button>
                <button type="button" className="cancel-button" onClick={() => setShowModal(false)}>
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
