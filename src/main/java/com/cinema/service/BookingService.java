package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public Booking createBooking(Long userId, Long movieId, int seats) {
        Movie movie = movieService.getMovieById(movieId);

        if (movie.getAvailableSeats() < seats) {
            throw new IllegalArgumentException("Not enough seats available");
        }

        User user = userService.findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        movie.setAvailableSeats(movie.getAvailableSeats() - seats);
        movieService.addMovie(movie);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(seats);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);

        try {
            String qrCode = qrCodeService.generateQRCode(
                savedBooking.getId(),
                user.getName(),
                movie.getTitle(),
                seats
            );
            savedBooking.setQrCode(qrCode);
            bookingRepository.save(savedBooking);
        } catch (Exception e) {
            // QR generation failed, booking still valid
        }

        return savedBooking;
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        booking.setStatus("CANCELLED");
        Movie movie = booking.getMovie();
        movie.setAvailableSeats(movie.getAvailableSeats() + booking.getNumberOfSeats());
        movieService.addMovie(movie);
        bookingRepository.save(booking);
    }
}