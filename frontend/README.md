# Cinema Reservation System - Frontend

A modern React frontend for the Cinema Reservation System with a Netflix-inspired black and red theme.

## 🚀 Features

- **Authentication**: Login and Register with JWT token handling
- **Movies**: Browse and view movie details with card-based layout
- **Booking**: Book tickets with seat selection
- **My Bookings**: View and cancel personal bookings
- **Admin Panel**: Manage movies (CRUD) and view all bookings (Admin only)
- **Role-Based Access**: Protected routes for users and admin-only features
- **Responsive Design**: Mobile-friendly interface
- **Modern UI**: Black and red Netflix-style theme

## 📁 Project Structure

```
frontend/
├── src/
│   ├── api/              # API service layer
│   │   ├── axios.js      # Axios configuration with JWT interceptor
│   │   ├── auth.js       # Authentication API calls
│   │   ├── movies.js     # Movie API calls
│   │   └── bookings.js   # Booking API calls
│   ├── components/       # Reusable components
│   │   ├── Header.jsx    # Navigation header
│   │   └── Footer.jsx    # Footer component
│   ├── context/          # React Context
│   │   └── AuthContext.jsx # Authentication state management
│   ├── pages/            # Page components
│   │   ├── Login.jsx     # Login page
│   │   ├── Register.jsx  # Registration page
│   │   ├── Movies.jsx    # Movies listing page
│   │   ├── Booking.jsx   # Booking page
│   │   ├── MyBookings.jsx # User bookings page
│   │   └── Admin.jsx     # Admin panel
│   ├── routes/           # Route protection
│   │   └── ProtectedRoute.jsx
│   ├── styles/           # CSS styles
│   │   ├── App.css
│   │   ├── Auth.css
│   │   ├── Movies.css
│   │   ├── Booking.css
│   │   ├── MyBookings.css
│   │   ├── Admin.css
│   │   ├── Header.css
│   │   └── Footer.css
│   ├── App.jsx           # Main app component
│   └── main.jsx          # Entry point
├── index.html
├── package.json
└── vite.config.js
```

## 🔧 Setup Instructions

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- Backend server running on `http://localhost:9090`

### Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

### Running the Application

Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:3000`

### Building for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.

## 🔌 API Configuration

The frontend is configured to proxy API requests to the backend:

- **Backend URL**: `http://localhost:9090`
- **API Base Path**: `/api`

All API calls are automatically proxied through Vite's proxy configuration.

## 🔐 Authentication

- JWT tokens are stored in `localStorage`
- Tokens are automatically included in request headers via Axios interceptor
- 401 errors trigger automatic logout and redirect to login

## 👤 User Roles

- **ROLE_USER**: Can view movies, book tickets, and manage their bookings
- **ROLE_ADMIN**: All user permissions plus movie management and viewing all bookings

## 🎨 Theme

The application uses a Netflix-inspired theme:
- **Primary Color**: `#e50914` (Netflix Red)
- **Background**: `#000` (Black)
- **Card Background**: `#141414` (Dark Gray)
- **Text**: `#fff` (White)
- **Secondary Text**: `#999` (Gray)

## 📝 API Endpoints Used

### Authentication
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - Login user

### Movies
- `GET /api/movies` - Get all movies
- `GET /api/movies/:id` - Get movie by ID
- `GET /api/movies/search?title=` - Search movies
- `POST /api/movies` - Add movie (Admin)
- `PUT /api/movies/:id` - Update movie (Admin)
- `DELETE /api/movies/:id` - Delete movie (Admin)

### Bookings
- `POST /api/bookings?userId=&movieId=&seats=` - Create booking
- `GET /api/bookings/user/:userId` - Get user bookings
- `PUT /api/bookings/:bookingId/cancel` - Cancel booking

## 🛡️ Security Features

- Protected routes require authentication
- Admin routes require `ROLE_ADMIN` permission
- Automatic token refresh handling
- Form validation on all inputs
- SQL injection prevention (handled by backend)

## 📱 Responsive Design

The application is fully responsive and works on:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (< 768px)

## 🐛 Troubleshooting

### Backend Connection Issues
- Ensure the backend is running on port 9090
- Check that the database is accessible
- Verify CORS configuration on the backend

### Authentication Issues
- Clear browser localStorage
- Check that JWT secret matches between frontend and backend
- Verify token expiration time

### Build Issues
- Delete `node_modules` and reinstall dependencies
- Clear Vite cache: `npm run dev -- --force`
