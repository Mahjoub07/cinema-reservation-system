package com.cinema.bridge;

/**
 * Bridge Pattern: Refined Abstraction.
 * Represents an email notification with a subject line.
 */
public class EmailNotification extends Notification {

    private final String subject;

    public EmailNotification(NotificationSender sender, String recipient, String subject, String message) {
        super(sender, recipient, message);
        this.subject = subject;
    }

    @Override
    public void send() {
        sender.sendNotification(recipient, "Subject: " + subject + " | " + message);
    }
}
