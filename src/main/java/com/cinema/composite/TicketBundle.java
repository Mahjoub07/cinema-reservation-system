package com.cinema.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite Pattern: Composite class.
 * Represents a bundle of tickets that may contain
 * both individual tickets and other nested bundles.
 */
public class TicketBundle implements TicketComponent {

    private final String bundleName;
    private final List<TicketComponent> components = new ArrayList<>();

    public TicketBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    public void add(TicketComponent component) {
        components.add(component);
    }

    public void remove(TicketComponent component) {
        components.remove(component);
    }

    @Override
    public double getPrice() {
        return components.stream()
                .mapToDouble(TicketComponent::getPrice)
                .sum();
    }

    @Override
    public String getDescription() {
        return "Bundle: " + bundleName + " (" + components.size() + " items, "
                + getSeatCount() + " seats)";
    }

    @Override
    public int getSeatCount() {
        return components.stream()
                .mapToInt(TicketComponent::getSeatCount)
                .sum();
    }

    public List<TicketComponent> getComponents() {
        return List.copyOf(components);
    }

    public String getBundleName() {
        return bundleName;
    }
}
