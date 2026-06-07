package service;

import dao.*;
import model.*;
import exceptions.*;
import utils.FileLogger;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservationServiceImpl implements IReservationService {
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private PaymentProcessor paymentProcessor;
    private FileLogger fileLogger;

    public ReservationServiceImpl() {
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        this.paymentProcessor = new PaymentProcessorImpl();
        this.fileLogger = FileLogger.getInstance();
    }

    @Override
    public Booking bookFlight(User user, Flight flight, List<String> seatNumbers, int passengerCount)
            throws InsufficientSeatsException {

        if (flight.getAvailableSeats() < passengerCount) {
            throw new InsufficientSeatsException(
                    String.format("Only %d seats available. Requested: %d seats",
                            flight.getAvailableSeats(), passengerCount)
            );
        }

        double totalFare = calculateTotalFare(flight, passengerCount);
        String bookingReference = generateBookingReference();

        Booking booking = new Booking();
        booking.setBookingReference(bookingReference);
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setSeatNumbers(seatNumbers);
        booking.setPassengerCount(passengerCount);
        booking.setTotalAmount(totalFare);
        booking.setStatus("confirmed");
        booking.setBookingDate(LocalDateTime.now());

        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            int bookingId = bookingDAO.createBooking(booking);
            if (bookingId > 0) {
                booking.setBookingId(bookingId);

                Payment payment = paymentProcessor.processPayment(bookingId, totalFare, "Credit Card");

                if (payment.isSuccessful()) {
                    boolean seatsUpdated = flightDAO.updateSeatAvailability(flight.getFlightId(), -passengerCount);

                    if (seatsUpdated) {
                        conn.commit();
                        fileLogger.logBooking(booking);
                        return booking;
                    }
                }
            }
            conn.rollback();
            return null;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Booking failed: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    DatabaseConnection.getInstance().releaseConnection(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean cancelBooking(String bookingReference) throws BookingNotFoundException {
        Booking booking = bookingDAO.getBookingByReference(bookingReference);
        if (booking == null) {
            throw new BookingNotFoundException("Booking not found: " + bookingReference);
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            boolean cancelled = bookingDAO.cancelBooking(booking.getBookingId());
            if (cancelled) {
                flightDAO.updateSeatAvailability(booking.getFlight().getFlightId(), booking.getPassengerCount());
                paymentProcessor.refundPayment(booking.getBookingId());
                conn.commit();
                fileLogger.logCancellation(booking);
                return true;
            }
            conn.rollback();
            return false;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Cancellation failed: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    DatabaseConnection.getInstance().releaseConnection(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Booking> getUserBookings(int userId) {
        return bookingDAO.getBookingsByUser(userId);
    }

    @Override
    public Booking getBookingDetails(String bookingReference) {
        return bookingDAO.getBookingByReference(bookingReference);
    }

    @Override
    public double calculateTotalFare(Flight flight, int passengerCount) {
        return flight.calculateFare() * passengerCount;
    }

    @Override
    public boolean checkSeatAvailability(Flight flight, int requestedSeats) {
        return flight.getAvailableSeats() >= requestedSeats;
    }

    private String generateBookingReference() {
        return "BOOK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}