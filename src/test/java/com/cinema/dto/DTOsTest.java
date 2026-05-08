package com.cinema.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOsTest {

    @Test
    void shouldCreateBookingDTOWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        BookingDTO dto = new BookingDTO(1L, 2L, "user@test.com", 3L, "Inception", 2, now, "CONFIRMED", 25.0, List.of(1, 2), now.plusDays(1), "token");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals("user@test.com", dto.getUserEmail());
        assertEquals(3L, dto.getMovieId());
        assertEquals("Inception", dto.getMovieTitle());
        assertEquals(2, dto.getNumberOfSeats());
        assertEquals("CONFIRMED", dto.getStatus());
        assertEquals(25.0, dto.getTotalPrice());
        assertEquals(List.of(1, 2), dto.getSeatNumbers());
        assertEquals("token", dto.getVerificationToken());
    }

    @Test
    void shouldSetAndGetBookingDTOFields() {
        BookingDTO dto = new BookingDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(1L);
        dto.setUserId(2L);
        dto.setUserEmail("user@test.com");
        dto.setMovieId(3L);
        dto.setMovieTitle("Inception");
        dto.setNumberOfSeats(2);
        dto.setBookingDate(now);
        dto.setStatus("CONFIRMED");
        dto.setTotalPrice(25.0);
        dto.setSeatNumbers(List.of(1, 2));
        dto.setShowTime(now.plusDays(1));
        dto.setVerificationToken("token");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals("user@test.com", dto.getUserEmail());
        assertEquals(3L, dto.getMovieId());
        assertEquals("Inception", dto.getMovieTitle());
        assertEquals(2, dto.getNumberOfSeats());
        assertEquals(now, dto.getBookingDate());
        assertEquals("CONFIRMED", dto.getStatus());
        assertEquals(25.0, dto.getTotalPrice());
        assertEquals(List.of(1, 2), dto.getSeatNumbers());
        assertEquals(now.plusDays(1), dto.getShowTime());
        assertEquals("token", dto.getVerificationToken());
    }

    @Test
    void shouldCreateMovieDTOWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        MovieDTO dto = new MovieDTO(1L, "Inception", "A mind-bending thriller", "Sci-Fi", 148, now, 100, "/poster.jpg", 12.5);

        assertEquals(1L, dto.getId());
        assertEquals("Inception", dto.getTitle());
        assertEquals("A mind-bending thriller", dto.getDescription());
        assertEquals("Sci-Fi", dto.getGenre());
        assertEquals(148, dto.getDuration());
        assertEquals(now, dto.getShowTime());
        assertEquals(100, dto.getAvailableSeats());
        assertEquals("/poster.jpg", dto.getPosterUrl());
        assertEquals(12.5, dto.getPrice());
    }

    @Test
    void shouldSetAndGetMovieDTOFields() {
        MovieDTO dto = new MovieDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(1L);
        dto.setTitle("Inception");
        dto.setDescription("A mind-bending thriller");
        dto.setGenre("Sci-Fi");
        dto.setDuration(148);
        dto.setShowTime(now);
        dto.setAvailableSeats(100);
        dto.setPosterUrl("/poster.jpg");
        dto.setPrice(12.5);

        assertEquals(1L, dto.getId());
        assertEquals("Inception", dto.getTitle());
        assertEquals("A mind-bending thriller", dto.getDescription());
        assertEquals("Sci-Fi", dto.getGenre());
        assertEquals(148, dto.getDuration());
        assertEquals(now, dto.getShowTime());
        assertEquals(100, dto.getAvailableSeats());
        assertEquals("/poster.jpg", dto.getPosterUrl());
        assertEquals(12.5, dto.getPrice());
    }

    @Test
    void shouldCreateUserDTOWithAllArgsConstructor() {
        UserDTO dto = new UserDTO(1L, "user@test.com", "User", "ROLE_USER");

        assertEquals(1L, dto.getId());
        assertEquals("user@test.com", dto.getEmail());
        assertEquals("User", dto.getName());
        assertEquals("ROLE_USER", dto.getRole());
    }

    @Test
    void shouldSetAndGetUserDTOFields() {
        UserDTO dto = new UserDTO();

        dto.setId(1L);
        dto.setEmail("user@test.com");
        dto.setName("User");
        dto.setRole("ROLE_USER");

        assertEquals(1L, dto.getId());
        assertEquals("user@test.com", dto.getEmail());
        assertEquals("User", dto.getName());
        assertEquals("ROLE_USER", dto.getRole());
    }

    @Test
    void shouldCreateLoginRequestDTOWithAllArgsConstructor() {
        LoginRequestDTO dto = new LoginRequestDTO("user@test.com", "password123");

        assertEquals("user@test.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void shouldSetAndGetLoginRequestDTOFields() {
        LoginRequestDTO dto = new LoginRequestDTO();

        dto.setEmail("user@test.com");
        dto.setPassword("password123");

        assertEquals("user@test.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void shouldCreateRegisterRequestDTOWithAllArgsConstructor() {
        RegisterRequestDTO dto = new RegisterRequestDTO("User", "user@test.com", "password123");

        assertEquals("User", dto.getName());
        assertEquals("user@test.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void shouldSetAndGetRegisterRequestDTOFields() {
        RegisterRequestDTO dto = new RegisterRequestDTO();

        dto.setName("User");
        dto.setEmail("user@test.com");
        dto.setPassword("password123");

        assertEquals("User", dto.getName());
        assertEquals("user@test.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void shouldCreateBookingRequestDTOWithAllArgsConstructor() {
        LocalDateTime showTime = LocalDateTime.now().plusDays(1);
        BookingRequestDTO dto = new BookingRequestDTO(1L, 2, List.of(1, 2), showTime);

        assertEquals(1L, dto.getMovieId());
        assertEquals(2, dto.getSeats());
        assertEquals(List.of(1, 2), dto.getSeatNumbers());
        assertEquals(showTime, dto.getShowTime());
    }

    @Test
    void shouldSetAndGetBookingRequestDTOFields() {
        BookingRequestDTO dto = new BookingRequestDTO();
        LocalDateTime showTime = LocalDateTime.now().plusDays(1);

        dto.setMovieId(1L);
        dto.setSeats(2);
        dto.setSeatNumbers(List.of(1, 2));
        dto.setShowTime(showTime);

        assertEquals(1L, dto.getMovieId());
        assertEquals(2, dto.getSeats());
        assertEquals(List.of(1, 2), dto.getSeatNumbers());
        assertEquals(showTime, dto.getShowTime());
    }
}
