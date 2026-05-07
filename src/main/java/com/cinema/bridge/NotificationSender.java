package com.cinema.bridge;

/**
 * Bridge Pattern: Implementation interface.
 * Defines the contract for sending notifications through various channels.
 * This side of the bridge can be extended independently of the Notification abstraction.
 */
public interface NotificationSender {

    /**
     * Sends a notification message to a recipient.
     *
     * @param recipient email, phone number, or other identifier
     * @param message   the message content
     */
    void sendNotification(String recipient, String message);
}
