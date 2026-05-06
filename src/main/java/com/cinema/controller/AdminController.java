package com.cinema.controller;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.DashboardStatsDTO;
import com.cinema.dto.MovieDTO;
import com.cinema.dto.UserDTO;
import com.cinema.service.BookingService;
import com.cinema.service.MovieService;
import com.cinema.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
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
        long totalBookings = bookingService.getAllBookings().size();
        long activeBookings = bookingService.getAllBookings().stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .count();

        DashboardStatsDTO stats = new DashboardStatsDTO(totalUsers, totalBookings, totalMovies, activeBookings);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
