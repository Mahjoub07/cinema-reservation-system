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
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
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
        LocalDateTime showTime = LocalDateTime.now().plusDays(1);
        bookingDTO = new BookingDTO(1L, 1L, "mahjoub@cinema.com", 1L, "Inception", 2, LocalDateTime.now(), "CONFIRMED", null, List.of(1, 2), showTime, "test-token-123");
        bookingRequest = new BookingRequestDTO(1L, 2, List.of(1, 2), showTime);
    }

    @Test
    void shouldCreateBooking() {
        // Create mock Authentication object
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("mahjoub@cinema.com");

        when(bookingService.createBooking(any(String.class), any(BookingRequestDTO.class)))
                .thenReturn(bookingDTO);

        // Pass the mock authentication instead of null
        BookingDTO result = bookingController.createBooking(bookingRequest, authentication).getBody();

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, result.getNumberOfSeats());
        verify(bookingService, times(1)).createBooking(eq("mahjoub@cinema.com"), any(BookingRequestDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        assertThrows(RuntimeException.class,
                () -> bookingController.createBooking(bookingRequest, null));
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
    void shouldGetAllBookings() {
        when(bookingService.getAllBookings()).thenReturn(Arrays.asList(bookingDTO));

        List<BookingDTO> result = bookingController.getAllBookings().getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
    }

    @Test
    void shouldCancelBooking() {
        doNothing().when(bookingService).cancelBooking(1L);

        bookingController.cancelBooking(1L);

        verify(bookingService, times(1)).cancelBooking(1L);
    }

    @Test
    void shouldDeleteBooking() {
        doNothing().when(bookingService).cancelBooking(1L);

        var response = bookingController.deleteBooking(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(bookingService, times(1)).cancelBooking(1L);
    }

    @Test
    void shouldBulkDeleteBookings() {
        doNothing().when(bookingService).bulkDeleteBookings(anyList());

        var response = bookingController.bulkDeleteBookings(Arrays.asList(1L, 2L));

        assertEquals(204, response.getStatusCode().value());
        verify(bookingService, times(1)).bulkDeleteBookings(Arrays.asList(1L, 2L));
    }

    @Test
    void shouldDownloadTicketPdf() {
        byte[] pdfBytes = new byte[]{1, 2, 3, 4, 5};
        when(bookingService.generateTicketPdf(1L)).thenReturn(pdfBytes);

        var response = bookingController.downloadTicket(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertArrayEquals(pdfBytes, response.getBody());
    }

    @Test
    void shouldReturnErrorWhenDownloadTicketBookingNotFound() {
        when(bookingService.generateTicketPdf(99L)).thenThrow(new RuntimeException("Booking not found"));

        assertThrows(RuntimeException.class, () -> bookingController.downloadTicket(99L));
    }
}