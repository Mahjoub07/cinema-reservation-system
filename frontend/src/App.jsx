import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import Header from './components/Header';
import Footer from './components/Footer';
import ProtectedRoute from './routes/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Movies from './pages/Movies';
import MovieDetails from './pages/MovieDetails';
import Booking from './pages/Booking';
import MyBookings from './pages/MyBookings';
import Admin from './pages/Admin';
import VerifyBooking from './pages/VerifyBooking';
import './styles/App.css';
import './styles/Toast.css';

function AnimatedRoutes() {
  const location = useLocation();

  return (
    <main className="main-content">
      <div key={location.pathname} className="page-enter">
        <Routes location={location}>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<Movies />} />
          <Route path="/movies" element={<Movies />} />
          <Route path="/movie/:id" element={<MovieDetails />} />
          <Route path="/booking/:id" element={<ProtectedRoute><Booking /></ProtectedRoute>} />
          <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
          <Route path="/admin" element={<ProtectedRoute requireAdmin><Admin /></ProtectedRoute>} />
          <Route path="/verify/:bookingId" element={<VerifyBooking />} />
        </Routes>
      </div>
    </main>
  );
}

const basename = import.meta.env.BASE_URL?.replace(/\/$/, '') || '/';

function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <Router basename={basename}>
          <div className="app">
            <Header />
            <AnimatedRoutes />
            <Footer />
          </div>
        </Router>
      </ToastProvider>
    </AuthProvider>
  );
}

export default App;
