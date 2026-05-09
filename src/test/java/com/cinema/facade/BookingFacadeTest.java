package com.cinema.facade;

import com.cinema.bridge.ConsoleNotificationSender;
import com.cinema.bridge.NotificationSender;
import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.pricing.PricingContext;
import com.cinema.repository.BookingRepository;
import com.cinema.service.BookingService;
import com.cinema.service.MovieService;
import com.cinema.service.QRCodeService;
import com.cinema.service.UserService;
import com.cinema.websocket.SeatLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingFacadeTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private UserService userService;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private PricingContext pricingContext;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SeatLockService seatLockService;

    private BookingService bookingService;
    private BookingFacade bookingFacade;

    private User user;
    private Movie movie;
    private Booking booking;
    private BookingRequestDTO request;

    @BeforeEach
    void setUp() {
        // Use real services where appropriate for the facade integration
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test User");

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setAvailableSeats(100);
        movie.setPrice(12.50);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(2);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setTotalPrice(25.0);
        booking.setVerificationToken("test-token-123");

        LocalDateTime showTime = LocalDateTime.now().plusDays(1);
        request = new BookingRequestDTO(1L, 2, List.of(1, 2), showTime);

        NotificationSender notificationSender = new ConsoleNotificationSender();

        // Use BookingService with mocks
        bookingService = new BookingService(
                bookingRepository,
                movieService,
                userService,
                qrCodeService,
                pricingContext,
                messagingTemplate,
                seatLockService
        );

        bookingFacade = new BookingFacade(
                bookingService,
                notificationSender
        );
    }

    @Test
    void shouldCreateFacadeSuccessfully() {
        assertNotNull(bookingFacade);
    }

    @Test
    void shouldCompleteBookingWithMocks() throws Exception {
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });
        doReturn("mock-qr-code").when(qrCodeService).generateQRCode(any(), any(), any(), anyInt());

        BookingDTO result = bookingFacade.completeBooking("test@test.com", request);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, result.getNumberOfSeats());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userService.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingFacade.completeBooking("unknown@test.com", request));
    }

    @Test
    void shouldGenerateAndDownloadTicket() throws Exception {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        byte[] validQrBytes = new QRCodeService().generateQRCodeBytes(1L, "Test User", "Inception", 2);
        doReturn(validQrBytes).when(qrCodeService).generateQRCodeBytes(any(), any(), any(), anyInt());

        byte[] pdf = bookingFacade.generateAndDownloadTicket(1L);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
