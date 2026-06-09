package service;

import model.Payment;

public interface PaymentProcessor {
    Payment processPayment(int bookingId, double amount, String paymentMethod);
    boolean refundPayment(int bookingId);
    Payment getPaymentStatus(int bookingId);
}
