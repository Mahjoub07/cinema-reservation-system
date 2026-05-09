package com.cinema.facade;

import com.cinema.bridge.EmailNotification;
import com.cinema.bridge.NotificationSender;
import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.service.BookingService;
import org.springframework.stereotype.Component;

/**
 * Facade Pattern: Simplified unified interface for the booking workflow.
 * Orchestrates booking creation and confirmation notification.
 */
@Component
public class BookingFacade {

    private final BookingService bookingService;
    private final NotificationSender notificationSender;

    public BookingFacade(BookingService bookingService,
                         NotificationSender notificationSender) {
        this.bookingService = bookingService;
        this.notificationSender = notificationSender;
    }

    /**
     * Creates a booking and sends a confirmation notification.
     *
     * @param email   authenticated user email
     * @param request booking request
     * @return the created booking DTO
     */
    public BookingDTO completeBooking(String email, BookingRequestDTO request) {
        BookingDTO booking = bookingService.createBooking(email, request);

        String message = String.format(
                "Your booking for '%s' is confirmed! Seats: %d | Total: $%.2f",
                booking.getMovieTitle(),
                booking.getNumberOfSeats(),
                booking.getTotalPrice()
        );
        EmailNotification notification = new EmailNotification(
                notificationSender,
                email,
                "Booking Confirmation",
                message
        );
        notification.send();

        return booking;
    }

    /**
     * Convenience method for quick ticket generation + download flow.
     */
    public byte[] generateAndDownloadTicket(Long bookingId) {
        return bookingService.generateTicketPdf(bookingId);
    }
}
