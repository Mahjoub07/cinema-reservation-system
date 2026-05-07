package com.cinema.adapter;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Adapter Pattern: Concrete Adapter for a mock Stripe gateway.
 * Adapts the external (mock) Stripe API to the internal PaymentProcessor interface.
 */
@Component
@Primary
public class MockStripePaymentAdapter implements PaymentProcessor {

    @Override
    public boolean processPayment(double amount, String email) {
        // Simulate Stripe API integration
        System.out.println("[MockStripe] Charging $" + String.format("%.2f", amount) + " for " + email);
        return true;
    }

    @Override
    public String getProcessorName() {
        return "Stripe";
    }
}
