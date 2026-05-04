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

    public BookingService(BookingRepository bookingRepository,
                          MovieService movieService,
                          UserService userService) {
        this.bookingRepository = bookingRepository;
        this.movieService = movieService;
        this.userService = userService;
    }

    public Booking createBooking(Long userId, Long movieId, int seats) {
        Movie movie = movieService.getMovieById(movieId);
        User user = userService.findByEmail(
            userService.findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getEmail()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        if (movie.getAvailableSeats() < seats) {
            throw new RuntimeException("Not enough seats available");
        }

        movie.setAvailableSeats(movie.getAvailableSeats() - seats);
        movieService.addMovie(movie);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(seats);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("CANCELLED");
        Movie movie = booking.getMovie();
        movie.setAvailableSeats(movie.getAvailableSeats() + booking.getNumberOfSeats());
        movieService.addMovie(movie);
        bookingRepository.save(booking);
    }
}