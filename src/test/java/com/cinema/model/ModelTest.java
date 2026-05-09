package com.cinema.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void shouldCreateBookingWithDefaultConstructor() {
        Booking booking = new Booking();
        assertNull(booking.getId());
        assertNull(booking.getUser());
        assertNull(booking.getMovie());
        assertEquals(0, booking.getNumberOfSeats());
        assertNull(booking.getBookingDate());
        assertNull(booking.getStatus());
        assertNull(booking.getTotalPrice());
        assertNull(booking.getQrCode());
        assertNull(booking.getSeatNumbers());
        assertNull(booking.getShowTime());
        assertNull(booking.getVerificationToken());
    }

    @Test
    void shouldSetAndGetBookingFields() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setId(1L);
        Movie movie = new Movie();
        movie.setId(2L);

        booking.setId(3L);
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(4);
        booking.setBookingDate(now);
        booking.setStatus("CONFIRMED");
        booking.setTotalPrice(25.0);
        booking.setQrCode("qr123");
        booking.setSeatNumbers("1,2,3");
        booking.setShowTime(now.plusDays(1));
        booking.setVerificationToken("token123");

        assertEquals(3L, booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(movie, booking.getMovie());
        assertEquals(4, booking.getNumberOfSeats());
        assertEquals(now, booking.getBookingDate());
        assertEquals("CONFIRMED", booking.getStatus());
        assertEquals(25.0, booking.getTotalPrice());
        assertEquals("qr123", booking.getQrCode());
        assertEquals("1,2,3", booking.getSeatNumbers());
        assertEquals(now.plusDays(1), booking.getShowTime());
        assertEquals("token123", booking.getVerificationToken());
    }

    @Test
    void shouldCreateMovieWithDefaultConstructor() {
        Movie movie = new Movie();
        assertNull(movie.getId());
        assertNull(movie.getTitle());
        assertNull(movie.getDescription());
        assertNull(movie.getGenre());
        assertEquals(0, movie.getDuration());
        assertNull(movie.getShowTime());
        assertEquals(0, movie.getAvailableSeats());
        assertNull(movie.getPrice());
        assertNull(movie.getPosterUrl());
        assertNull(movie.getBackdropUrl());
    }

    @Test
    void shouldSetAndGetMovieFields() {
        Movie movie = new Movie();
        LocalDateTime now = LocalDateTime.now();

        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDescription("A mind-bending thriller");
        movie.setGenre("Sci-Fi");
        movie.setDuration(148);
        movie.setShowTime(now);
        movie.setAvailableSeats(100);
        movie.setPrice(12.5);
        movie.setPosterUrl("/poster.jpg");
        movie.setBackdropUrl("/backdrop.jpg");

        assertEquals(1L, movie.getId());
        assertEquals("Inception", movie.getTitle());
        assertEquals("A mind-bending thriller", movie.getDescription());
        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals(148, movie.getDuration());
        assertEquals(now, movie.getShowTime());
        assertEquals(100, movie.getAvailableSeats());
        assertEquals(12.5, movie.getPrice());
        assertEquals("/poster.jpg", movie.getPosterUrl());
        assertEquals("/backdrop.jpg", movie.getBackdropUrl());
    }

    @Test
    void shouldCreateUserWithDefaultConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getName());
        assertNull(user.getRole());
    }

    @Test
    void shouldSetAndGetUserFields() {
        User user = new User();

        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("password123");
        user.setName("Test User");
        user.setRole(Role.ROLE_ADMIN);

        assertEquals(1L, user.getId());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("Test User", user.getName());
        assertEquals(Role.ROLE_ADMIN, user.getRole());
    }

    @Test
    void shouldCreateWatchlistWithDefaultConstructor() {
        Watchlist watchlist = new Watchlist();
        assertNull(watchlist.getId());
        assertNull(watchlist.getUser());
        assertNull(watchlist.getMovie());
        assertNotNull(watchlist.getCreatedAt());
    }

    @Test
    void shouldSetAndGetWatchlistFields() {
        Watchlist watchlist = new Watchlist();
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setId(1L);
        Movie movie = new Movie();
        movie.setId(2L);

        watchlist.setId(3L);
        watchlist.setUser(user);
        watchlist.setMovie(movie);
        watchlist.setCreatedAt(now);

        assertEquals(3L, watchlist.getId());
        assertEquals(user, watchlist.getUser());
        assertEquals(movie, watchlist.getMovie());
        assertEquals(now, watchlist.getCreatedAt());
    }

    @Test
    void roleEnumShouldContainExpectedValues() {
        assertNotNull(Role.ROLE_USER);
        assertNotNull(Role.ROLE_ADMIN);
        assertNotNull(Role.ROLE_MAIN_ADMIN);
        assertEquals(3, Role.values().length);
    }
}
