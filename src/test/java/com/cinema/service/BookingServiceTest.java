package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Movie movie;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("mahjoub@cinema.com");
        user.setName("Mahjoub");

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setAvailableSeats(100);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(2);
        booking.setStatus("CONFIRMED");
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(userService.findAll()).thenReturn(Arrays.asList(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.createBooking(1L, 1L, 2);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, result.getNumberOfSeats());
    }
    @Test
    void shouldThrowExceptionWhenNotEnoughSeats() {
        movie.setAvailableSeats(1);
        when(movieService.getMovieById(1L)).thenReturn(movie);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(1L, 1L, 5));

        assertEquals("Not enough seats available", exception.getMessage());
    }

    @Test
    void shouldGetUserBookings() {
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(booking));

        List<Booking> result = bookingService.getUserBookings(1L);

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
    }

    @Test
    void shouldCancelBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.cancelBooking(1L);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals("CANCELLED", booking.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking(99L));

        assertEquals("Booking not found", exception.getMessage());
    }
}