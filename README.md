🎬 Cinema Reservation System

A full-stack cinema ticket booking application with real-time seat selection, QR code tickets, and role-based access control.

🌐 Live Demo: mahjoub07.github.io/cinema-reservation-system


✨ Features


JWT Authentication — Secure stateless auth with role-based access (USER / ADMIN / MAIN_ADMIN)
Movie Browsing — Search and filter movies with posters and backdrops via Supabase Storage
Real-time Seat Selection — WebSocket-based seat locking to prevent double bookings
Booking System — Select seats, confirm bookings, and download PDF tickets with QR codes
Watchlist — Save movies for later viewing
Admin Panel — Full CRUD for movies, view all bookings and users



🛠 Tech Stack

LayerTechnologyBackendSpring Boot 4.0.6, Java 21, Spring Security, JWTORM / DBHibernate/JPA, PostgreSQL (prod), H2 (dev)WebSocketSpring WebSocketQR / PDFZXing 3.5.2, OpenPDF 1.3.30FrontendReact 18.3.1, Vite 5.4.8, React Router v6, AxiosStorageSupabase (movie posters & backdrops)DevOpsDocker, GitHub Actions, SonarCloud, RailwayTestingJUnit 5, Mockito, JaCoCo 0.8.13


🏗 Architecture & Design Patterns

Layered architecture (Controller → Service → Repository) with 8 design patterns implemented:

PatternPackagePurposeBridgecom.cinema.bridgeDecouples abstraction from implementationCompositecom.cinema.compositeUniform object tree handlingDecoratorcom.cinema.decoratorDynamic behavior additionFaçadecom.cinema.facadeSimplified interface for subsystemsFlyweightcom.cinema.flyweight99.7% memory reduction via object sharingStrategycom.cinema.pricingEncapsulates pricing algorithmsFactoryIntegratedConsistent object creationDTOcom.cinema.dtoSecure data transfer


📊 Code Quality & Coverage

MetricCoverageInstructions82%Lines84%Methods88%Classes91%

SonarCloud: Grade A — 0 Bugs · 0 Vulnerabilities · 0 Code Smells · 0.45% Duplication


🚀 CI/CD Pipeline

Automated on every push / pull request via GitHub Actions:


Checkout code
Set up Java 21
Maven build & tests
JaCoCo coverage report
SonarCloud analysis
JAR packaging
Auto-deploy: backend → Railway · frontend → GitHub Pages


Branch protection rules (main): PR review required · All CI checks must pass · Coverage ≥ 80% · SonarCloud gate passed


⚡ API Performance

EndpointResponse TimeGET /api/movies~50msPOST /api/bookings~150msGET /api/bookings/{id}~80ms


📋 Prerequisites


Java 21+
Maven 3.9+
Node.js 16+ & npm
PostgreSQL 14+ (or Supabase)
Docker (optional)



🔧 Local Setup

Backend

bashgit clone https://github.com/Mahjoub07/cinema-reservation-system.git
cd cinema-reservation-system

# Configure src/main/resources/application.properties
./mvnw clean install
./mvnw spring-boot:run
# Runs on http://localhost:9090

Frontend

bashcd frontend
npm install
# Configure .env.development with backend URL
npm run dev
# Runs on http://localhost:3000

Docker (full stack)

bashdocker-compose up --build

📚 API Endpoints

Auth

MethodEndpointDescriptionPOST/api/users/registerRegister new userPOST/api/users/loginLogin — returns JWT

Movies

MethodEndpointAuthGET/api/moviesPublicGET/api/movies/{id}PublicGET/api/movies/search?title={title}PublicPOST/api/moviesADMINPUT/api/movies/{id}ADMINDELETE/api/movies/{id}ADMIN

Bookings

MethodEndpointDescriptionPOST/api/bookings?userId={id}&movieId={id}&seats={seats}Create bookingGET/api/bookings/user/{userId}User's bookingsPUT/api/bookings/{bookingId}/cancelCancel bookingGET/api/bookings/{bookingId}/ticketDownload PDF ticket

Watchlist

MethodEndpointDescriptionPOST/api/watchlist?userId={id}&movieId={id}Add to watchlistGET/api/watchlist/user/{userId}Get watchlistDELETE/api/watchlist/{id}Remove from watchlist

Admin

MethodEndpointDescriptionGET/api/admin/bookingsAll bookingsGET/api/admin/usersAll users


🧪 Testing

bash# Run tests
./mvnw test

# Generate JaCoCo coverage report
./mvnw jacoco:report
# View at: target/site/jacoco/index.html


🔐 Security


Stateless JWT authentication
BCrypt password hashing
Role-based access control (USER / ADMIN / MAIN_ADMIN)
CORS configured for GitHub Pages ↔ Railway
Input validation & SQL injection prevention via JPA
Secrets stored as environment variables (never hardcoded)



📦 Deployment

Backend → Railway

bash./mvnw clean package -DskipTests
docker build -t cinema-app .
# Push to Railway via GitHub Actions

Frontend → GitHub Pages

bashcd frontend
npm run build
npm run deploy


🗄 Database Schema (key tables)

Users: id, email (unique), password (BCrypt), name, role

Movies: id, title, genre, duration, showTime, availableSeats, price, posterUrl, backdropUrl

Bookings: id, user_id, movie_id, seatNumbers, bookingDate, status (CONFIRMED/CANCELLED), totalPrice, qrCode, verificationToken

Watchlist: id, user_id, movie_id, addedDate


👥 Authors


EL MAHJOUB BOUNHAR — github.com/Mahjoub07
YASSINE AIT OUMGHAR


Université Cadi Ayyad — Faculté des Sciences Semlalia, Marrakech
Module: Génie Logiciel Avancé · Encadrant: Pr. Fahd Kalloubi · 2025-2026


📝 License

This project is licensed under the MIT License.
