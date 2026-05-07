package com.cinema.flyweight;

/**
 * Flyweight Pattern: Flyweight class.
 * Represents immutable, shared seat type metadata.
 * Intrinsic state (name, price multiplier, icon) is stored inside the object.
 * Extrinsic state (seat position, booking reference) is passed by the client.
 */
public final class SeatType {

    private final String name;
    private final double priceMultiplier;
    private final String icon;

    private SeatType(String name, double priceMultiplier, String icon) {
        this.name = name;
        this.priceMultiplier = priceMultiplier;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public String getIcon() {
        return icon;
    }

    // Factory methods for creating canonical instances (used by SeatTypeFactory)
    static SeatType createStandard() {
        return new SeatType("Standard", 1.0, "SEAT");
    }

    static SeatType createVip() {
        return new SeatType("VIP", 1.5, "VIP");
    }

    static SeatType createPremium() {
        return new SeatType("Premium", 2.0, "PREM");
    }
}
