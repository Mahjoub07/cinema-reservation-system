package com.cinema.service;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
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
    private BookingRequestDTO bookingRequest;

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

        bookingRequest = new BookingRequestDTO(1L, 2);
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(userService.findByEmail("mahjoub@cinema.com")).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDTO result = bookingService.createBooking("mahjoub@cinema.com", bookingRequest);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, result.getNumberOfSeats());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughSeats() {
        movie.setAvailableSeats(1);
        when(movieService.getMovieById(1L)).thenReturn(movie);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking("mahjoub@cinema.com", bookingRequest));

        assertEquals("Not enough seats available", exception.getMessage());
    }

    @Test
    void shouldGetUserBookings() {
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(booking));

        List<BookingDTO> result = bookingService.getUserBookings("mahjoub@cinema.com");

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
    }

    @Test
    void shouldCancelBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(movieService.updateMovie(any(Long.class), any())).thenReturn(null);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.cancelBooking(1L);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals("CANCELLED", booking.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.cancelBooking(99L));

        assertEquals("Booking not found", exception.getMessage());
    }
}