package com.cinema.service;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.repository.BookingRepository;
import com.cinema.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MovieService movieService;
    private final UserService userService;
    private final QRCodeService qrCodeService;

    public BookingService(BookingRepository bookingRepository,
                          MovieService movieService,
                          UserService userService,
                          QRCodeService qrCodeService) {
        this.bookingRepository = bookingRepository;
        this.movieService = movieService;
        this.userService = userService;
        this.qrCodeService = qrCodeService;
    }

    public BookingDTO createBooking(String email, BookingRequestDTO request) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieService.getMovieById(request.getMovieId());

        if (movie.getAvailableSeats() < request.getSeats()) {
            throw new IllegalArgumentException("Not enough seats available");
        }

        movie.setAvailableSeats(movie.getAvailableSeats() - request.getSeats());
        movieService.updateMovieSeats(movie);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(request.getSeats());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);

        try {
            String qrCode = qrCodeService.generateQRCode(
                savedBooking.getId(),
                user.getName(),
                movie.getTitle(),
                request.getSeats()
            );
            savedBooking.setQrCode(qrCode);
            bookingRepository.save(savedBooking);
        } catch (Exception e) {
            // QR generation failed, booking still valid
        }

        return convertToDTO(savedBooking);
    }

    public List<BookingDTO> getUserBookings(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserIdWithDetails(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getUserBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdWithDetails(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus("CANCELLED");
        Movie movie = booking.getMovie();
        movie.setAvailableSeats(movie.getAvailableSeats() + booking.getNumberOfSeats());
        movieService.updateMovieSeats(movie);
        bookingRepository.save(booking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        return new BookingDTO(
            booking.getId(),
            booking.getUser().getId(),
            booking.getUser().getEmail(),
            booking.getMovie().getId(),
            booking.getMovie().getTitle(),
            booking.getNumberOfSeats(),
            booking.getBookingDate(),
            booking.getStatus()
        );
    }
}