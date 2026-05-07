package com.cinema.composite;

/**
 * Composite Pattern: Leaf class.
 * Represents a single individual ticket.
 */
public class SingleTicket implements TicketComponent {

    private final String seatLabel;
    private final double price;

    public SingleTicket(String seatLabel, double price) {
        this.seatLabel = seatLabel;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getDescription() {
        return "Single Ticket: " + seatLabel;
    }

    @Override
    public int getSeatCount() {
        return 1;
    }

    public String getSeatLabel() {
        return seatLabel;
    }
}
