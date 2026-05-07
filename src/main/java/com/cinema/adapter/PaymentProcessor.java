package com.cinema.adapter;

/**
 * Adapter Pattern: Target interface.
 * Defines the contract that internal code uses for payment processing.
 * Adapters translate external payment gateway APIs to this uniform interface.
 */
public interface PaymentProcessor {

    /**
     * Processes a payment for the given amount and customer.
     *
     * @param amount amount to charge
     * @param email  customer email
     * @return true if payment succeeded
     */
    boolean processPayment(double amount, String email);

    /**
     * Returns the name of the payment provider.
     */
    String getProcessorName();
}
