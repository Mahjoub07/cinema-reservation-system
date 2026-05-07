package com.cinema.composite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketComponentTest {

    @Test
    void singleTicketShouldReturnCorrectPrice() {
        SingleTicket ticket = new SingleTicket("A1", 12.50);
        assertEquals(12.50, ticket.getPrice(), 0.001);
    }

    @Test
    void singleTicketShouldReturnCorrectDescription() {
        SingleTicket ticket = new SingleTicket("B3", 15.0);
        assertEquals("Single Ticket: B3", ticket.getDescription());
    }

    @Test
    void singleTicketShouldReturnSeatCountOne() {
        SingleTicket ticket = new SingleTicket("C5", 20.0);
        assertEquals(1, ticket.getSeatCount());
    }

    @Test
    void singleTicketShouldReturnSeatLabel() {
        SingleTicket ticket = new SingleTicket("D7", 10.0);
        assertEquals("D7", ticket.getSeatLabel());
    }

    @Test
    void ticketBundleShouldAggregatePrice() {
        TicketBundle bundle = new TicketBundle("Family Pack");
        bundle.add(new SingleTicket("A1", 12.50));
        bundle.add(new SingleTicket("A2", 12.50));
        assertEquals(25.0, bundle.getPrice(), 0.001);
    }

    @Test
    void ticketBundleShouldAggregateSeatCount() {
        TicketBundle bundle = new TicketBundle("Couple Pack");
        bundle.add(new SingleTicket("B1", 15.0));
        bundle.add(new SingleTicket("B2", 15.0));
        bundle.add(new SingleTicket("B3", 15.0));
        assertEquals(3, bundle.getSeatCount());
    }

    @Test
    void ticketBundleShouldReturnCorrectDescription() {
        TicketBundle bundle = new TicketBundle("VIP Section");
        bundle.add(new SingleTicket("A1", 20.0));
        String desc = bundle.getDescription();
        assertTrue(desc.contains("VIP Section"));
        assertTrue(desc.contains("1 items"));
        assertTrue(desc.contains("1 seats"));
    }

    @Test
    void ticketBundleShouldHandleEmptyBundle() {
        TicketBundle emptyBundle = new TicketBundle("Empty");
        assertEquals(0.0, emptyBundle.getPrice(), 0.001);
        assertEquals(0, emptyBundle.getSeatCount());
        assertTrue(emptyBundle.getDescription().contains("0 items"));
    }

    @Test
    void ticketBundleShouldSupportNestedBundles() {
        TicketBundle parentBundle = new TicketBundle("Cinema Experience");
        TicketBundle childBundle = new TicketBundle("Front Row");

        childBundle.add(new SingleTicket("A1", 20.0));
        childBundle.add(new SingleTicket("A2", 20.0));

        parentBundle.add(childBundle);
        parentBundle.add(new SingleTicket("B5", 15.0));

        assertEquals(55.0, parentBundle.getPrice(), 0.001);
        assertEquals(3, parentBundle.getSeatCount());
        assertTrue(parentBundle.getDescription().contains("2 items"));
    }

    @Test
    void ticketBundleShouldAllowRemoval() {
        TicketBundle bundle = new TicketBundle("Test Bundle");
        SingleTicket ticket = new SingleTicket("C1", 10.0);
        bundle.add(ticket);
        assertEquals(1, bundle.getComponents().size());

        bundle.remove(ticket);
        assertEquals(0, bundle.getComponents().size());
    }

    @Test
    void ticketBundleShouldReturnComponentsCopy() {
        TicketBundle bundle = new TicketBundle("Copy Test");
        bundle.add(new SingleTicket("A1", 10.0));
        assertEquals(1, bundle.getComponents().size());
    }

    @Test
    void ticketBundleShouldReturnBundleName() {
        TicketBundle bundle = new TicketBundle("Premium");
        assertEquals("Premium", bundle.getBundleName());
    }
}
