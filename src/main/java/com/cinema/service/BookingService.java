package com.cinema.service;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public BookingDTO createBooking(String email, BookingRequestDTO request) {
        Movie movie = movieService.getMovieById(request.getMovieId());

        if (movie.getAvailableSeats() < request.getSeats()) {
            throw new BadRequestException("Not enough seats available");
        }

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        movie.setAvailableSeats(movie.getAvailableSeats() - request.getSeats());
        movieService.updateMovie(movie.getId(), convertToMovieDTO(movie));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(request.getSeats());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);
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

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");
        Movie movie = booking.getMovie();
        movie.setAvailableSeats(movie.getAvailableSeats() + booking.getNumberOfSeats());
        movieService.updateMovie(movie.getId(), convertToMovieDTO(movie));
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

    private com.cinema.dto.MovieDTO convertToMovieDTO(Movie movie) {
        return new com.cinema.dto.MovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getGenre(),
            movie.getDuration(),
            movie.getShowTime(),
            movie.getAvailableSeats(),
            null
        );
    }
}