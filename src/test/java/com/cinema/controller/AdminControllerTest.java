package com.cinema.controller;

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

import java.util.Arrays;

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
}
