package com.cinema.controller;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.service.BookingService;
import com.cinema.websocket.SeatLockService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SeatLockService seatLockService;

    public BookingController(BookingService bookingService, SeatLockService seatLockService) {
        this.bookingService = bookingService;
        this.seatLockService = seatLockService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO request,
            Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        return ResponseEntity.ok(bookingService.createBooking(email, request));
    }

    @GetMapping("/seats/{movieId}")
    public ResponseEntity<Map<String, List<Integer>>> getBookedSeats(
            @PathVariable Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime showtime) {
        List<Integer> bookedSeats = bookingService.getBookedSeatNumbers(movieId, showtime);
        return ResponseEntity.ok(Map.of("bookedSeats", bookedSeats));
    }

    @GetMapping("/locked-seats/{movieId}")
    public ResponseEntity<Map<String, Object>> getLockedSeats(
            @PathVariable Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime showtime) {
        String showTimeStr = showtime.toString();
        java.util.Set<String> locked = seatLockService.getLockedSeats(movieId, showTimeStr);
        return ResponseEntity.ok(Map.of(
                "lockedSeats", locked,
                "count", locked.size()
        ));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        return ResponseEntity.ok(bookingService.getUserBookings(email));
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Authentication required");
        }
        return authentication.getName();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookingsByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkDeleteBookings(@RequestBody List<Long> ids) {
        bookingService.bulkDeleteBookings(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/ticket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long id) {
        byte[] pdf = bookingService.generateTicketPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/verify/{bookingId}")
    public ResponseEntity<BookingDTO> verifyBooking(@PathVariable Long bookingId) {
        BookingDTO booking = bookingService.verifyBooking(bookingId);
        return ResponseEntity.ok(booking);
    }
}