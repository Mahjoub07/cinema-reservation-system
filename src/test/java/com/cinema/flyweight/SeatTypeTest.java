package com.cinema.flyweight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTypeTest {

    private SeatTypeFactory factory;

    @BeforeEach
    void setUp() {
        factory = new SeatTypeFactory();
    }

    @Test
    void shouldCreateStandardSeatType() {
        SeatType standard = factory.getSeatType("STANDARD");
        assertNotNull(standard);
        assertEquals("Standard", standard.getName());
        assertEquals(1.0, standard.getPriceMultiplier(), 0.001);
        assertEquals("SEAT", standard.getIcon());
    }

    @Test
    void shouldCreateVipSeatType() {
        SeatType vip = factory.getSeatType("VIP");
        assertNotNull(vip);
        assertEquals("VIP", vip.getName());
        assertEquals(1.5, vip.getPriceMultiplier(), 0.001);
        assertEquals("VIP", vip.getIcon());
    }

    @Test
    void shouldCreatePremiumSeatType() {
        SeatType premium = factory.getSeatType("PREMIUM");
        assertNotNull(premium);
        assertEquals("Premium", premium.getName());
        assertEquals(2.0, premium.getPriceMultiplier(), 0.001);
        assertEquals("PREM", premium.getIcon());
    }

    @Test
    void shouldReturnSameInstanceForStandard() {
        SeatType s1 = factory.getSeatType("STANDARD");
        SeatType s2 = factory.getSeatType("standard"); // case insensitive
        assertSame(s1, s2);
    }

    @Test
    void shouldReturnSameInstanceForVip() {
        SeatType v1 = factory.getSeatType("VIP");
        SeatType v2 = factory.getSeatType("vip"); // case insensitive
        assertSame(v1, v2);
    }

    @Test
    void shouldReturnSameInstanceForPremium() {
        SeatType p1 = factory.getSeatType("PREMIUM");
        SeatType p2 = factory.getSeatType("premium"); // case insensitive
        assertSame(p1, p2);
    }

    @Test
    void shouldReturnDefaultForUnknownType() {
        SeatType unknown = factory.getSeatType("UNKNOWN");
        assertNotNull(unknown);
        assertEquals("Standard", unknown.getName()); // falls back to standard
    }

    @Test
    void shouldTrackPoolSize() {
        assertEquals(0, factory.getPoolSize());
        factory.getSeatType("STANDARD");
        assertEquals(1, factory.getPoolSize());
        factory.getSeatType("VIP");
        assertEquals(2, factory.getPoolSize());
        factory.getSeatType("STANDARD"); // already in pool
        assertEquals(2, factory.getPoolSize());
    }

    @Test
    void shouldCreateAllThreeTypes() {
        factory.getSeatType("STANDARD");
        factory.getSeatType("VIP");
        factory.getSeatType("PREMIUM");
        assertEquals(3, factory.getPoolSize());
    }

    @Test
    void seatTypesShouldBeImmutable() {
        SeatType standard = factory.getSeatType("STANDARD");
        // No setters exist - verify state consistency
        assertEquals("Standard", standard.getName());
        assertEquals(1.0, standard.getPriceMultiplier(), 0.001);
        assertEquals("SEAT", standard.getIcon());
    }
}
