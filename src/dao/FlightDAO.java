package dao;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public boolean addFlight(Flight flight) {
        String sql = "INSERT INTO flights (flight_number, airline, source, destination, departure_date, departure_time, arrival_time, total_seats, available_seats, base_price, flight_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setString(2, flight.getAirline());
            pstmt.setString(3, flight.getSource());
            pstmt.setString(4, flight.getDestination());
            pstmt.setDate(5, Date.valueOf(flight.getDepartureDate()));
            pstmt.setTime(6, Time.valueOf(flight.getDepartureTime()));
            pstmt.setTime(7, Time.valueOf(flight.getArrivalTime()));
            pstmt.setInt(8, flight.getTotalSeats());
            pstmt.setInt(9, flight.getAvailableSeats());
            pstmt.setDouble(10, flight.getBasePrice());
            pstmt.setString(11, flight instanceof DomesticFlight ? "domestic" : "international");
            pstmt.setString(12, flight.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    public List<Flight> searchFlights(String source, String destination, LocalDate date) {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights WHERE source LIKE ? AND destination LIKE ? AND departure_date = ? AND available_seats > 0 AND status = 'active'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + source + "%");
            pstmt.setString(2, "%" + destination + "%");
            pstmt.setDate(3, Date.valueOf(date));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                flights.add(createFlightFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return flights;
    }

    public Flight getFlightById(int flightId) {
        String sql = "SELECT * FROM flights WHERE flight_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return createFlightFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE flights SET flight_number=?, airline=?, source=?, destination=?, departure_date=?, departure_time=?, arrival_time=?, total_seats=?, available_seats=?, base_price=?, flight_type=?, status=? WHERE flight_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setString(2, flight.getAirline());
            pstmt.setString(3, flight.getSource());
            pstmt.setString(4, flight.getDestination());
            pstmt.setDate(5, Date.valueOf(flight.getDepartureDate()));
            pstmt.setTime(6, Time.valueOf(flight.getDepartureTime()));
            pstmt.setTime(7, Time.valueOf(flight.getArrivalTime()));
            pstmt.setInt(8, flight.getTotalSeats());
            pstmt.setInt(9, flight.getAvailableSeats());
            pstmt.setDouble(10, flight.getBasePrice());
            pstmt.setString(11, flight instanceof DomesticFlight ? "domestic" : "international");
            pstmt.setString(12, flight.getStatus());
            pstmt.setInt(13, flight.getFlightId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM flights WHERE flight_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY departure_date, departure_time";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                flights.add(createFlightFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return flights;
    }

    private Flight createFlightFromResultSet(ResultSet rs) throws SQLException {
        String flightType = rs.getString("flight_type");
        Flight flight;

        if ("international".equals(flightType)) {
            flight = new InternationalFlight();
        } else {
            flight = new DomesticFlight();
        }

        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setAirline(rs.getString("airline"));
        flight.setSource(rs.getString("source"));
        flight.setDestination(rs.getString("destination"));
        flight.setDepartureDate(rs.getDate("departure_date").toLocalDate());
        flight.setDepartureTime(rs.getTime("departure_time").toLocalTime());
        flight.setArrivalTime(rs.getTime("arrival_time").toLocalTime());
        flight.setTotalSeats(rs.getInt("total_seats"));
        flight.setAvailableSeats(rs.getInt("available_seats"));
        flight.setBasePrice(rs.getDouble("base_price"));
        flight.setStatus(rs.getString("status"));

        return flight;
    }

    public boolean updateSeatAvailability(int flightId, int seatChange) {
        String sql = "UPDATE flights SET available_seats = available_seats + ? WHERE flight_id = ? AND available_seats + ? >= 0";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, seatChange);
            pstmt.setInt(2, flightId);
            pstmt.setInt(3, seatChange);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
}