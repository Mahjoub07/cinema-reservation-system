package com.cinema.facade;

import com.cinema.adapter.PaymentProcessor;
import com.cinema.bridge.EmailNotification;
import com.cinema.bridge.NotificationSender;
import com.cinema.dto.BookingDTO;
import com.cinema.dto.BookingRequestDTO;
import com.cinema.service.BookingService;
import org.springframework.stereotype.Component;

/**
 * Facade Pattern: Simplified unified interface for the complex booking workflow.
 * Orchestrates multiple subsystems:
 * - Booking creation (BookingService)
 * - Payment processing (PaymentProcessor adapter)
 * - QR generation (handled inside BookingService)
 * - Email notification (Bridge pattern notification)
 *
 * Controllers or other clients can call this single facade
 * instead of coordinating multiple services manually.
 */
@Component
public class BookingFacade {

    private final BookingService bookingService;
    private final PaymentProcessor paymentProcessor;
    private final NotificationSender notificationSender;

    public BookingFacade(BookingService bookingService,
                         PaymentProcessor paymentProcessor,
                         NotificationSender notificationSender) {
        this.bookingService = bookingService;
        this.paymentProcessor = paymentProcessor;
        this.notificationSender = notificationSender;
    }

    /**
     * Complete booking workflow:
     * 1. Create the booking via BookingService
     * 2. Process payment via adapted payment gateway
     * 3. Send confirmation email via bridged notification sender
     *
     * @param email   authenticated user email
     * @param request booking request
     * @return the created booking DTO
     */
    public BookingDTO completeBooking(String email, BookingRequestDTO request) {
        // Step 1: Create booking (includes QR generation inside BookingService)
        BookingDTO booking = bookingService.createBooking(email, request);

        // Step 2: Process payment
        boolean paymentSuccess = paymentProcessor.processPayment(
                booking.getTotalPrice(),
                email
        );
        if (!paymentSuccess) {
            throw new RuntimeException("Payment processing failed for booking " + booking.getId());
        }

        // Step 3: Send confirmation notification
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
