package service;

import dao.PaymentDAO;
import model.Payment;
import utils.FileLogger;

public class PaymentProcessorImpl implements PaymentProcessor {
    private PaymentDAO paymentDAO;
    private FileLogger fileLogger;

    public PaymentProcessorImpl() {
        this.paymentDAO = new PaymentDAO();
        this.fileLogger = FileLogger.getInstance();
    }

    @Override
    public Payment processPayment(int bookingId, double amount, String paymentMethod) {
        Payment payment = new Payment(bookingId, amount, paymentMethod);
        payment.setPaymentStatus(Payment.STATUS_SUCCESS);

        int paymentId = paymentDAO.createPayment(payment);
        payment.setPaymentId(paymentId);

        fileLogger.logTransaction("PAYMENT_SUCCESS",
                String.format("BookingID:%d Amount:%.2f Txn:%s", bookingId, amount, payment.getTransactionId()));

        return payment;
    }

    @Override
    public boolean refundPayment(int bookingId) {
        boolean refunded = paymentDAO.refundPayment(bookingId);
        if (refunded) {
            fileLogger.logTransaction("REFUND_SUCCESS", String.format("BookingID:%d", bookingId));
        }
        return refunded;
    }

    @Override
    public Payment getPaymentStatus(int bookingId) {
        return paymentDAO.getPaymentByBookingId(bookingId);
    }
}