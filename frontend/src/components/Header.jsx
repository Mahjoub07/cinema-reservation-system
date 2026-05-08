import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Header.css';

const Header = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setMobileMenuOpen(false);
    navigate('/login');
  };

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo" onClick={closeMenu}>
          <span className="logo-icon">&#127909;</span>
          <h1>CineReserve</h1>
        </Link>

        <nav className={`nav ${mobileMenuOpen ? 'nav-open' : ''}`}>
          {user ? (
            <>
              <Link to="/movies" className="nav-link" onClick={closeMenu}>Movies</Link>
              <Link to="/my-bookings" className="nav-link" onClick={closeMenu}>My Bookings</Link>
              {isAdmin() && <Link to="/admin" className="nav-link" onClick={closeMenu}>Admin</Link>}
              <div className="nav-divider" />
              <button onClick={handleLogout} className="logout-button">
                <span>Logout</span>
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link" onClick={closeMenu}>Login</Link>
              <Link to="/register" className="nav-link btn-nav-primary" onClick={closeMenu}>Register</Link>
            </>
          )}
        </nav>

        <button
          className={`hamburger ${mobileMenuOpen ? 'open' : ''}`}
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label="Toggle menu"
          aria-expanded={mobileMenuOpen}
        >
          <span />
          <span />
          <span />
        </button>
      </div>
      {mobileMenuOpen && <div className="nav-backdrop" onClick={closeMenu} />}
    </header>
  );
};

export default Header;
