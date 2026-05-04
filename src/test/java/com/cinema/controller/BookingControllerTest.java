package com.cinema.controller;

import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private Booking booking;
    private User user;
    private Movie movie;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Mahjoub");
        user.setEmail("mahjoub@cinema.com");

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
        booking.setBookingDate(LocalDateTime.now());
    }

    @Test
    void shouldCreateBooking() {
        when(bookingService.createBooking(1L, 1L, 2)).thenReturn(booking);

        Booking result = bookingController.createBooking(1L, 1L, 2).getBody();

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, result.getNumberOfSeats());
        verify(bookingService, times(1)).createBooking(1L, 1L, 2);
    }

    @Test
    void shouldGetUserBookings() {
        when(bookingService.getUserBookings(1L)).thenReturn(Arrays.asList(booking));

        List<Booking> result = bookingController.getUserBookings(1L).getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
        verify(bookingService, times(1)).getUserBookings(1L);
    }

    @Test
    void shouldCancelBooking() {
        doNothing().when(bookingService).cancelBooking(1L);

        bookingController.cancelBooking(1L);

        verify(bookingService, times(1)).cancelBooking(1L);
    }
}