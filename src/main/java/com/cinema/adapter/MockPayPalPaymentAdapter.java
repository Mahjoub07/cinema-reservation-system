package com.cinema.adapter;

import org.springframework.stereotype.Component;

/**
 * Adapter Pattern: Concrete Adapter for a mock PayPal gateway.
 * Adapts the external (mock) PayPal API to the internal PaymentProcessor interface.
 */
@Component
public class MockPayPalPaymentAdapter implements PaymentProcessor {

    @Override
    public boolean processPayment(double amount, String email) {
        // Simulate PayPal API integration
        System.out.println("[MockPayPal] Processing $" + String.format("%.2f", amount) + " via PayPal for " + email);
        return true;
    }

    @Override
    public String getProcessorName() {
        return "PayPal";
    }
}
