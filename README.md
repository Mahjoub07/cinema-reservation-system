# 🎬 Cinema Reservation System

A full-stack cinema ticket booking application with real-time seat selection, QR code tickets, and role-based access control.

**🌐 Live Demo:** [mahjoub07.github.io/cinema-reservation-system](https://mahjoub07.github.io/cinema-reservation-system/)

---

## ✨ Features

- **JWT Authentication** — Secure stateless auth with role-based access (USER / ADMIN / MAIN_ADMIN)
- **Movie Browsing** — Search and filter movies with posters and backdrops via Supabase Storage
- **Real-time Seat Selection** — WebSocket-based seat locking to prevent double bookings
- **Booking System** — Select seats, confirm bookings, and download PDF tickets with QR codes
- **Watchlist** — Save movies for later viewing
- **Admin Panel** — Full CRUD for movies, view all bookings and users

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Spring Boot 4.0.6, Java 21, Spring Security, JWT |
| **ORM / DB** | Hibernate/JPA, PostgreSQL (prod), H2 (dev) |
| **WebSocket** | Spring WebSocket |
| **QR / PDF** | ZXing 3.5.2, OpenPDF 1.3.30 |
| **Frontend** | React 18.3.1, Vite 5.4.8, React Router v6, Axios |
| **Storage** | Supabase (movie posters & backdrops) |
| **DevOps** | Docker, GitHub Actions, SonarCloud, Railway |
| **Testing** | JUnit 5, Mockito, JaCoCo 0.8.13 |

---

## 🏗 Architecture & Design Patterns

Layered architecture (Controller → Service → Repository) with 8 design patterns implemented:

| Pattern | Package | Purpose |
|---|---|---|
| Bridge | `com.cinema.bridge` | Decouples abstraction from implementation |
| Composite | `com.cinema.composite` | Uniform object tree handling |
| Decorator | `com.cinema.decorator` | Dynamic behavior addition |
| Façade | `com.cinema.facade` | Simplified interface for subsystems |
| Flyweight | `com.cinema.flyweight` | 99.7% memory reduction via object sharing |
| Strategy | `com.cinema.pricing` | Encapsulates pricing algorithms |
| Factory | Integrated | Consistent object creation |
| DTO | `com.cinema.dto` | Secure data transfer |

---

## 📊 Code Quality & Coverage

| Metric | Coverage |
|---|---|
| Instructions | 82% |
| Lines | 84% |
| Methods | 88% |
| Classes | 91% |

**SonarCloud:** Grade A — 0 Bugs · 0 Vulnerabilities · 0 Code Smells · 0.45% Duplication

---

## 🚀 CI/CD Pipeline

Automated on every push / pull request via GitHub Actions:

1. Checkout code
2. Set up Java 21
3. Maven build & tests
4. JaCoCo coverage report
5. SonarCloud analysis
6. JAR packaging
7. Auto-deploy: backend → Railway · frontend → GitHub Pages

**Branch protection rules (main):** PR review required · All CI checks must pass · Coverage ≥ 80% · SonarCloud gate passed

---

## ⚡ API Performance

| Endpoint | Response Time |
|---|---|
| `GET /api/movies` | ~50ms |
| `POST /api/bookings` | ~150ms |
| `GET /api/bookings/{id}` | ~80ms |

---

## 📋 Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 16+ & npm
- PostgreSQL 14+ (or Supabase)
- Docker (optional)

---

## 🔧 Local Setup

### Backend

```bash
git clone https://github.com/Mahjoub07/cinema-reservation-system.git
cd cinema-reservation-system

# Configure src/main/resources/application.properties
./mvnw clean install
./mvnw spring-boot:run
# Runs on http://localhost:9090
```

### Frontend

```bash
cd frontend
npm install
# Configure .env.development with backend URL
npm run dev
# Runs on http://localhost:3000
```

### Docker (full stack)

```bash
docker-compose up --build
```

---

## 📚 API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login — returns JWT |

### Movies
| Method | Endpoint | Auth |
|---|---|---|
| GET | `/api/movies` | Public |
| GET | `/api/movies/{id}` | Public |
| GET | `/api/movies/search?title={title}` | Public |
| POST | `/api/movies` | ADMIN |
| PUT | `/api/movies/{id}` | ADMIN |
| DELETE | `/api/movies/{id}` | ADMIN |

### Bookings
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/bookings?userId={id}&movieId={id}&seats={seats}` | Create booking |
| GET | `/api/bookings/user/{userId}` | User's bookings |
| PUT | `/api/bookings/{bookingId}/cancel` | Cancel booking |
| GET | `/api/bookings/{bookingId}/ticket` | Download PDF ticket |

### Watchlist
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/watchlist?userId={id}&movieId={id}` | Add to watchlist |
| GET | `/api/watchlist/user/{userId}` | Get watchlist |
| DELETE | `/api/watchlist/{id}` | Remove from watchlist |

### Admin
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/bookings` | All bookings |
| GET | `/api/admin/users` | All users |

---

## 🧪 Testing

```bash
# Run tests
./mvnw test

# Generate JaCoCo coverage report
./mvnw jacoco:report
# View at: target/site/jacoco/index.html
```

---

## 🔐 Security

- Stateless JWT authentication
- BCrypt password hashing
- Role-based access control (USER / ADMIN / MAIN_ADMIN)
- CORS configured for GitHub Pages ↔ Railway
- Input validation & SQL injection prevention via JPA
- Secrets stored as environment variables (never hardcoded)

---

## 📦 Deployment

### Backend → Railway

```bash
./mvnw clean package -DskipTests
docker build -t cinema-app .
# Push to Railway via GitHub Actions
```

### Frontend → GitHub Pages

```bash
cd frontend
npm run build
npm run deploy
```

---

## 🗄 Database Schema (key tables)

**Users:** `id`, `email` (unique), `password` (BCrypt), `name`, `role`

**Movies:** `id`, `title`, `genre`, `duration`, `showTime`, `availableSeats`, `price`, `posterUrl`, `backdropUrl`

**Bookings:** `id`, `user_id`, `movie_id`, `seatNumbers`, `bookingDate`, `status` (CONFIRMED/CANCELLED), `totalPrice`, `qrCode`, `verificationToken`

**Watchlist:** `id`, `user_id`, `movie_id`, `addedDate`

---

## 👥 Authors

- **EL MAHJOUB BOUNHAR** — [github.com/Mahjoub07](https://github.com/Mahjoub07)
- **YASSINE AIT OUMGHAR**

Université Cadi Ayyad — Faculté des Sciences Semlalia, Marrakech  
Module: Génie Logiciel Avancé · Encadrant: Pr. Fahd Kalloubi · 2025-2026

---

## 📝 License

This project is licensed under the [MIT License](LICENSE).
