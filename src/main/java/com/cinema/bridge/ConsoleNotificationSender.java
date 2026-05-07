package com.cinema.bridge;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Bridge Pattern: Concrete Implementation.
 * Logs notifications to the console (useful for development/demo).
 */
@Component
@Primary
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("[ConsoleNotification] To: " + recipient + " | " + message);
    }
}
