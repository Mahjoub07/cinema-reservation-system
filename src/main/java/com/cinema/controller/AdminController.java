package com.cinema.controller;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.DashboardStatsDTO;
import com.cinema.dto.MovieDTO;
import com.cinema.dto.UserDTO;
import com.cinema.service.BookingService;
import com.cinema.service.MovieService;
import com.cinema.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final MovieService movieService;
    private final BookingService bookingService;

    public AdminController(UserService userService, MovieService movieService, BookingService bookingService) {
        this.userService = userService;
        this.movieService = movieService;
        this.bookingService = bookingService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        long totalUsers = userService.findAll().size();
        long totalMovies = movieService.getAllMovies().size();
        List<BookingDTO> allBookings = bookingService.getAllBookings();
        long totalBookings = allBookings.size();
        long activeBookings = allBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .count();

        return ResponseEntity.ok(new DashboardStatsDTO(totalUsers, totalBookings, totalMovies, activeBookings));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<UserDTO> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        UserDTO admin = userService.createAdmin(request.getName(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(admin);
    }

    @PostMapping("/users/promote/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<UserDTO> promoteUser(@PathVariable Long id) {
        UserDTO user = userService.promoteUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/demote/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<UserDTO> demoteUser(@PathVariable Long id) {
        UserDTO user = userService.demoteUser(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateAdminRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
