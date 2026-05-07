package com.cinema.bridge;

/**
 * Bridge Pattern: Abstraction class.
 * Represents a generic notification. The abstraction and implementation
 * hierarchies can vary independently.
 */
public abstract class Notification {

    protected NotificationSender sender;
    protected String recipient;
    protected String message;

    public Notification(NotificationSender sender, String recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    /**
     * Sends the notification using the bound sender implementation.
     */
    public abstract void send();
}
