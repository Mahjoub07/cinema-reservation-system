package com.cinema.bridge;

import org.springframework.stereotype.Component;

/**
 * Bridge Pattern: Concrete Implementation.
 * Simulates sending notifications via email (SMTP).
 * In production, this would integrate JavaMailSender.
 */
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("[EmailNotification] To: " + recipient + " | Body: " + message);
    }
}
