package com.cinema.composite;

/**
 * Composite Pattern: Component interface.
 * Common interface for both individual tickets (leaf) and ticket bundles (composite).
 * Represents any bookable ticket item in the cinema reservation system.
 */
public interface TicketComponent {

    /**
     * Returns the total price of this component.
     */
    double getPrice();

    /**
     * Returns a description of this component.
     */
    String getDescription();

    /**
     * Returns the number of seats represented by this component.
     */
    int getSeatCount();
}
