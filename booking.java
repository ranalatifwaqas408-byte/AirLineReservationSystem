package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int bookingId;
    private String bookingReference;
    private User user;
    private Flight flight;
    private List<String> seatNumbers;
    private int passengerCount;
    private double totalAmount;
    private String status;
    private LocalDateTime bookingDate;

    public Booking() {
        this.seatNumbers = new ArrayList<>();
        this.status = "confirmed";
        this.bookingDate = LocalDateTime.now();
    }

    public Booking(String bookingReference, User user, Flight flight,
                   List<String> seatNumbers, int passengerCount, double totalAmount) {
        this();
        this.bookingReference = bookingReference;
        this.user = user;
        this.flight = flight;
        this.seatNumbers = seatNumbers != null ? seatNumbers : new ArrayList<>();
        this.passengerCount = passengerCount;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    public void addSeatNumber(String seatNumber) { this.seatNumbers.add(seatNumber); }

    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public boolean isCancellable() {
        return status.equals("confirmed") &&
                flight != null &&
                flight.getDepartureDate().isAfter(LocalDateTime.now().toLocalDate());
    }

    public String getSeatNumbersString() {
        return String.join(", ", seatNumbers);
    }

    public String getFormattedBookingDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return bookingDate.format(formatter);
    }
}
