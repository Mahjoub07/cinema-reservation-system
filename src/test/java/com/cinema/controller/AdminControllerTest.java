package com.cinema.controller;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.DashboardStatsDTO;
import com.cinema.dto.UserDTO;
import com.cinema.service.BookingService;
import com.cinema.service.MovieService;
import com.cinema.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private MovieService movieService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private AdminController adminController;

    private UserDTO adminDTO;
    private AdminController.CreateAdminRequest createAdminRequest;

    @BeforeEach
    void setUp() {
        adminDTO = new UserDTO(1L, "admin@cinema.com", "Admin", "ROLE_ADMIN");
        createAdminRequest = new AdminController.CreateAdminRequest();
        createAdminRequest.setName("Admin");
        createAdminRequest.setEmail("admin@cinema.com");
        createAdminRequest.setPassword("adminpass123");
    }

    @Test
    void shouldCreateAdminSuccessfully() {
        when(userService.createAdmin("Admin", "admin@cinema.com", "adminpass123"))
                .thenReturn(adminDTO);

        var response = adminController.createAdmin(createAdminRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("admin@cinema.com", response.getBody().getEmail());
        assertEquals("ROLE_ADMIN", response.getBody().getRole());
    }

    @Test
    void shouldGetAllUsers() {
        when(userService.findAll()).thenReturn(Arrays.asList(adminDTO));

        var response = adminController.getAllUsers();

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnErrorWhenCreateAdminValidationFails() {
        createAdminRequest.setEmail("invalid-email");
        createAdminRequest.setPassword("123");

        when(userService.createAdmin(any(), any(), any()))
                .thenThrow(new RuntimeException("Validation failed"));

        assertThrows(RuntimeException.class, () -> adminController.createAdmin(createAdminRequest));
    }

    @Test
    void shouldGetAllBookings() {
        BookingDTO bookingDTO = new BookingDTO(1L, 1L, "user@test.com", 1L, "Inception", 2, LocalDateTime.now(), "CONFIRMED", 25.0, List.of(1, 2), LocalDateTime.now().plusDays(1), "token");
        when(bookingService.getAllBookings()).thenReturn(Arrays.asList(bookingDTO));

        var response = adminController.getAllBookings();

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldGetDashboardStats() {
        when(userService.findAll()).thenReturn(Arrays.asList(adminDTO));
        when(movieService.getAllMovies()).thenReturn(Arrays.asList());
        when(bookingService.getAllBookings()).thenReturn(Arrays.asList());

        var response = adminController.getDashboardStats();

        assertNotNull(response);
        DashboardStatsDTO stats = response.getBody();
        assertEquals(1, stats.getTotalUsers());
        assertEquals(0, stats.getTotalBookings());
        assertEquals(0, stats.getTotalMovies());
        assertEquals(0, stats.getActiveBookings());
    }

    @Test
    void shouldGetDashboardStatsWithActiveBookings() {
        BookingDTO active = new BookingDTO(1L, 1L, "user@test.com", 1L, "Inception", 2, LocalDateTime.now(), "CONFIRMED", 25.0, List.of(1, 2), LocalDateTime.now().plusDays(1), "token");
        BookingDTO cancelled = new BookingDTO(2L, 1L, "user@test.com", 1L, "Inception", 1, LocalDateTime.now(), "CANCELLED", 12.5, List.of(), LocalDateTime.now().plusDays(1), "token2");
        when(userService.findAll()).thenReturn(Arrays.asList());
        when(movieService.getAllMovies()).thenReturn(Arrays.asList());
        when(bookingService.getAllBookings()).thenReturn(Arrays.asList(active, cancelled));

        var response = adminController.getDashboardStats();

        assertNotNull(response);
        assertEquals(2, response.getBody().getTotalBookings());
        assertEquals(1, response.getBody().getActiveBookings());
    }

    @Test
    void shouldCreateAdminRequestGettersAndSetters() {
        AdminController.CreateAdminRequest request = new AdminController.CreateAdminRequest();
        request.setName("Admin");
        request.setEmail("admin@test.com");
        request.setPassword("password123");

        assertEquals("Admin", request.getName());
        assertEquals("admin@test.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }
}
