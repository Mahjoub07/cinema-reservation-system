package com.cinema.adapter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessorTest {

    @Test
    void mockStripeShouldProcessPaymentSuccessfully() {
        MockStripePaymentAdapter adapter = new MockStripePaymentAdapter();
        boolean result = adapter.processPayment(25.0, "user@example.com");
        assertTrue(result);
    }

    @Test
    void mockStripeShouldReturnCorrectProcessorName() {
        MockStripePaymentAdapter adapter = new MockStripePaymentAdapter();
        assertEquals("Stripe", adapter.getProcessorName());
    }

    @Test
    void mockPayPalShouldProcessPaymentSuccessfully() {
        MockPayPalPaymentAdapter adapter = new MockPayPalPaymentAdapter();
        boolean result = adapter.processPayment(30.0, "user@example.com");
        assertTrue(result);
    }

    @Test
    void mockPayPalShouldReturnCorrectProcessorName() {
        MockPayPalPaymentAdapter adapter = new MockPayPalPaymentAdapter();
        assertEquals("PayPal", adapter.getProcessorName());
    }

    @Test
    void mockStripeShouldHandleZeroAmount() {
        MockStripePaymentAdapter adapter = new MockStripePaymentAdapter();
        assertTrue(adapter.processPayment(0.0, "test@test.com"));
    }

    @Test
    void mockPayPalShouldHandleZeroAmount() {
        MockPayPalPaymentAdapter adapter = new MockPayPalPaymentAdapter();
        assertTrue(adapter.processPayment(0.0, "test@test.com"));
    }

    @Test
    void bothAdaptersShouldImplementSameInterface() {
        PaymentProcessor stripe = new MockStripePaymentAdapter();
        PaymentProcessor paypal = new MockPayPalPaymentAdapter();

        assertTrue(stripe instanceof PaymentProcessor);
        assertTrue(paypal instanceof PaymentProcessor);
    }
}
