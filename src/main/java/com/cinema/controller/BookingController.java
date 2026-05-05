package com.cinema.controller;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.createBooking(email, request));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.getUserBookings(email));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookingsByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}