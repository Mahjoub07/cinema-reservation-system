package com.cinema.service;

import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.pricing.PricingContext;
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

    @Mock
    private PricingContext pricingContext;

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
        movie.setPrice(12.50);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setMovie(movie);
        booking.setNumberOfSeats(2);
        booking.setStatus("CONFIRMED");
        booking.setTotalPrice(25.0);

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
    void shouldCalculateTotalPriceWhenCreatingBooking() {
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(userService.findByEmail("mahjoub@cinema.com")).thenReturn(Optional.of(user));
        when(pricingContext.calculatePrice(12.50, 2)).thenReturn(25.0);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookingDTO result = bookingService.createBooking("mahjoub@cinema.com", bookingRequest);

        assertNotNull(result);
        assertEquals(25.0, result.getTotalPrice());
    }

    @Test
    void shouldCalculateZeroPriceWhenMoviePriceIsNull() {
        movie.setPrice(null);
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(userService.findByEmail("mahjoub@cinema.com")).thenReturn(Optional.of(user));
        when(pricingContext.calculatePrice(0.0, 2)).thenReturn(0.0);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookingDTO result = bookingService.createBooking("mahjoub@cinema.com", bookingRequest);

        assertNotNull(result);
        assertEquals(0.0, result.getTotalPrice());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughSeats() {
        movie.setAvailableSeats(1);
        when(movieService.getMovieById(1L)).thenReturn(movie);
        when(userService.findByEmail("mahjoub@cinema.com")).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking("mahjoub@cinema.com", bookingRequest));

        assertEquals("Not enough seats available", exception.getMessage());
    }

    @Test
    void shouldGetUserBookings() {
        when(userService.findByEmail("mahjoub@cinema.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByUserIdWithDetails(1L)).thenReturn(Arrays.asList(booking));

        List<BookingDTO> result = bookingService.getUserBookings("mahjoub@cinema.com");

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
    }

    @Test
    void shouldCancelBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        doNothing().when(movieService).updateMovieSeats(any(Movie.class));
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

    @Test
    void shouldGetBookingById() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenGetBookingByIdNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getBookingById(99L));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void shouldGenerateTicketPdf() {
        QRCodeService realQrService = new QRCodeService();
        PricingContext realPricing = new PricingContext(List.of());
        BookingService service = new BookingService(bookingRepository, movieService, userService, realQrService, realPricing);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        byte[] pdf = service.generateTicketPdf(1L);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void shouldThrowExceptionWhenGenerateTicketPdfBookingNotFound() {
        QRCodeService realQrService = new QRCodeService();
        PricingContext realPricing = new PricingContext(List.of());
        BookingService service = new BookingService(bookingRepository, movieService, userService, realQrService, realPricing);
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.generateTicketPdf(99L));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInCreateBooking() {
        lenient().when(userService.findByEmail("unknown@cinema.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.createBooking("unknown@cinema.com", bookingRequest));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInGetUserBookings() {
        when(userService.findByEmail("unknown@cinema.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getUserBookings("unknown@cinema.com"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldGetUserBookingsByUserId() {
        when(bookingRepository.findByUserIdWithDetails(1L)).thenReturn(Arrays.asList(booking));

        List<BookingDTO> result = bookingService.getUserBookingsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
    }

    @Test
    void shouldGetAllBookings() {
        when(bookingRepository.findAllWithDetails()).thenReturn(Arrays.asList(booking));

        List<BookingDTO> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
    }

    @Test
    void shouldCancelBookingAndRestoreSeats() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.cancelBooking(1L);

        verify(movieService, times(1)).updateMovieSeats(movie);
        assertEquals("CANCELLED", booking.getStatus());
        assertEquals(102, movie.getAvailableSeats()); // 100 + 2
    }
}