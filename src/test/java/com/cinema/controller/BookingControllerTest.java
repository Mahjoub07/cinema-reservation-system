package com.cinema.controller;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
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

    private BookingDTO bookingDTO;
    private BookingRequestDTO bookingRequest;

    @BeforeEach
    void setUp() {
        bookingDTO = new BookingDTO(1L, 1L, "mahjoub@cinema.com", 1L, "Inception", 2, LocalDateTime.now(), "CONFIRMED");
        bookingRequest = new BookingRequestDTO(1L, 2);
    }

    @Test
    void shouldCreateBooking() {
        when(bookingService.createBooking(any(String.class), any(BookingRequestDTO.class))).thenReturn(bookingDTO);

        BookingDTO result = bookingController.createBooking(bookingRequest, null).getBody();

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, result.getNumberOfSeats());
        verify(bookingService, times(1)).createBooking(any(String.class), any(BookingRequestDTO.class));
    }

    @Test
    void shouldGetUserBookings() {
        when(bookingService.getUserBookingsByUserId(1L)).thenReturn(Arrays.asList(bookingDTO));

        List<BookingDTO> result = bookingController.getUserBookings(1L).getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
        verify(bookingService, times(1)).getUserBookingsByUserId(1L);
    }

    @Test
    void shouldCancelBooking() {
        doNothing().when(bookingService).cancelBooking(1L);

        bookingController.cancelBooking(1L);

        verify(bookingService, times(1)).cancelBooking(1L);
    }
}