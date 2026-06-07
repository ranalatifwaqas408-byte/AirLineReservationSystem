package model;

public class InternationalFlight extends Flight {
    private static final double TAX_RATE = 0.18;
    private static final double FUEL_SURCHARGE = 5000;
    private static final double INSURANCE_FEE = 1000;

    public InternationalFlight() {}

    public InternationalFlight(String flightNumber, String airline, String source, String destination,
                               java.time.LocalDate departureDate, java.time.LocalTime departureTime,
                               java.time.LocalTime arrivalTime, int totalSeats, double basePrice) {
        super(flightNumber, airline, source, destination, departureDate,
                departureTime, arrivalTime, totalSeats, basePrice);
    }

    @Override
    public double calculateFare() {
        return basePrice + (basePrice * TAX_RATE) + FUEL_SURCHARGE + INSURANCE_FEE;
    }
}