package dao;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_reference, user_id, flight_id, seat_numbers, passenger_count, total_amount, booking_status, booking_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, booking.getBookingReference());
            pstmt.setInt(2, booking.getUser().getUserId());
            pstmt.setInt(3, booking.getFlight().getFlightId());
            pstmt.setString(4, booking.getSeatNumbersString());
            pstmt.setInt(5, booking.getPassengerCount());
            pstmt.setDouble(6, booking.getTotalAmount());
            pstmt.setString(7, booking.getStatus());
            pstmt.setTimestamp(8, Timestamp.valueOf(booking.getBookingDate()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return -1;
    }

    public Booking getBookingByReference(String bookingReference) {
        String sql = "SELECT b.*, u.*, f.* FROM bookings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN flights f ON b.flight_id = f.flight_id " +
                "WHERE b.booking_reference = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookingReference);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBookingFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.*, f.* FROM bookings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN flights f ON b.flight_id = f.flight_id " +
                "WHERE b.user_id = ? ORDER BY b.booking_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return bookings;
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET booking_status = 'cancelled' WHERE booking_id = ? AND booking_status = 'confirmed'";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();

        booking.setBookingId(rs.getInt("b.booking_id"));
        booking.setBookingReference(rs.getString("b.booking_reference"));
        booking.setPassengerCount(rs.getInt("b.passenger_count"));
        booking.setTotalAmount(rs.getDouble("b.total_amount"));
        booking.setStatus(rs.getString("b.booking_status"));
        booking.setBookingDate(rs.getTimestamp("b.booking_date").toLocalDateTime());

        String seatNumbersStr = rs.getString("b.seat_numbers");
        if (seatNumbersStr != null && !seatNumbersStr.isEmpty()) {
            String[] seats = seatNumbersStr.split(", ");
            for (String seat : seats) {
                booking.addSeatNumber(seat);
            }
        }

        Passenger user = new Passenger();
        user.setUserId(rs.getInt("u.user_id"));
        user.setUsername(rs.getString("u.username"));
        user.setFullName(rs.getString("u.full_name"));
        user.setEmail(rs.getString("u.email"));
        user.setPhoneNumber(rs.getString("u.phone_number"));
        booking.setUser(user);

        String flightType = rs.getString("f.flight_type");
        Flight flight;
        if ("international".equals(flightType)) {
            flight = new InternationalFlight();
        } else {
            flight = new DomesticFlight();
        }

        flight.setFlightId(rs.getInt("f.flight_id"));
        flight.setFlightNumber(rs.getString("f.flight_number"));
        flight.setAirline(rs.getString("f.airline"));
        flight.setSource(rs.getString("f.source"));
        flight.setDestination(rs.getString("f.destination"));
        flight.setDepartureDate(rs.getDate("f.departure_date").toLocalDate());
        flight.setDepartureTime(rs.getTime("f.departure_time").toLocalTime());
        flight.setArrivalTime(rs.getTime("f.arrival_time").toLocalTime());
        flight.setTotalSeats(rs.getInt("f.total_seats"));
        flight.setAvailableSeats(rs.getInt("f.available_seats"));
        flight.setBasePrice(rs.getDouble("f.base_price"));

        booking.setFlight(flight);

        return booking;
    }
}