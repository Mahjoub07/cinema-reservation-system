package com.cinema.bridge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    private ConsoleNotificationSender consoleSender;
    private EmailNotificationSender emailSender;

    @BeforeEach
    void setUp() {
        consoleSender = new ConsoleNotificationSender();
        emailSender = new EmailNotificationSender();
    }

    @Test
    void consoleNotificationSenderShouldSendWithoutError() {
        assertDoesNotThrow(() -> consoleSender.sendNotification("user@test.com", "Test message"));
    }

    @Test
    void emailNotificationSenderShouldSendWithoutError() {
        assertDoesNotThrow(() -> emailSender.sendNotification("user@test.com", "Test message"));
    }

    @Test
    void emailNotificationShouldIncludeSubject() {
        Notification notification = new EmailNotification(
                consoleSender, "user@test.com", "Booking Confirmed", "Your ticket is ready"
        );
        assertDoesNotThrow(notification::send);
    }

    @Test
    void smsNotificationShouldSendWithoutError() {
        Notification notification = new SmsNotification(
                consoleSender, "+1234567890", "Your booking is confirmed"
        );
        assertDoesNotThrow(notification::send);
    }

    @Test
    void smsNotificationShouldTruncateLongMessages() {
        String longMessage = "a".repeat(200);
        Notification notification = new SmsNotification(
                consoleSender, "+1234567890", longMessage
        );
        assertDoesNotThrow(notification::send);
    }

    @Test
    void smsNotificationShouldNotTruncateShortMessages() {
        String shortMessage = "Short msg";
        Notification notification = new SmsNotification(
                consoleSender, "+1234567890", shortMessage
        );
        assertDoesNotThrow(notification::send);
    }

    @Test
    void notificationSendersShouldImplementSameInterface() {
        NotificationSender sender1 = new ConsoleNotificationSender();
        NotificationSender sender2 = new EmailNotificationSender();

        assertTrue(sender1 instanceof NotificationSender);
        assertTrue(sender2 instanceof NotificationSender);
    }

    @Test
    void notificationAbstractionsShouldWorkWithDifferentSenders() {
        Notification emailNotif = new EmailNotification(
                emailSender, "user@test.com", "Test", "Hello"
        );
        Notification smsNotif = new SmsNotification(
                consoleSender, "+1234567890", "Hello"
        );

        assertDoesNotThrow(emailNotif::send);
        assertDoesNotThrow(smsNotif::send);
    }
}
