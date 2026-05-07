package com.cinema.bridge;

/**
 * Bridge Pattern: Refined Abstraction.
 * Represents an SMS notification with a character-limited format.
 */
public class SmsNotification extends Notification {

    public SmsNotification(NotificationSender sender, String recipient, String message) {
        super(sender, recipient, message);
    }

    @Override
    public void send() {
        String truncated = message.length() > 160 ? message.substring(0, 157) + "..." : message;
        sender.sendNotification(recipient, "[SMS] " + truncated);
    }
}
