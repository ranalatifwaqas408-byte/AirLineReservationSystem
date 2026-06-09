package service;

import model.*;
import exceptions.*;
import java.util.List;

public interface IReservationService {
    Booking bookFlight(User user, Flight flight, List<String> seatNumbers, int passengerCount)
            throws InsufficientSeatsException;

    boolean cancelBooking(String bookingReference)
            throws BookingNotFoundException;

    List<Booking> getUserBookings(int userId);

    Booking getBookingDetails(String bookingReference);

    double calculateTotalFare(Flight flight, int passengerCount);

    boolean checkSeatAvailability(Flight flight, int requestedSeats);
}
