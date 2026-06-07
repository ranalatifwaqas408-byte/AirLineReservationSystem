package model;

public class DomesticFlight extends Flight {
    private static final double TAX_RATE = 0.08;
    private static final double AIRLINE_FEE = 500;

    public DomesticFlight() {}

    public DomesticFlight(String flightNumber, String airline, String source, String destination,
                          java.time.LocalDate departureDate, java.time.LocalTime departureTime,
                          java.time.LocalTime arrivalTime, int totalSeats, double basePrice) {
        super(flightNumber, airline, source, destination, departureDate,
                departureTime, arrivalTime, totalSeats, basePrice);
    }

    @Override
    public double calculateFare() {
        return basePrice + (basePrice * TAX_RATE) + AIRLINE_FEE;
    }
}