import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Header.css';

const Header = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          <h1>Cinema Booking</h1>
        </Link>
        <nav className="nav">
          {user ? (
            <>
              <Link to="/movies" className="nav-link">Movies</Link>
              <Link to="/my-bookings" className="nav-link">My Bookings</Link>
              {isAdmin() && <Link to="/admin" className="nav-link">Admin</Link>}
              <button onClick={handleLogout} className="logout-button">Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">Login</Link>
              <Link to="/register" className="nav-link">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
