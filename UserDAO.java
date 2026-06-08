package dao;

import model.*;
import java.sql.*;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, phone_number, role) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getRole());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
        return false;
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                if (rs.getString("role").equals("admin")) {
                    Admin admin = new Admin();
                    admin.setUserId(rs.getInt("user_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    admin.setEmail(rs.getString("email"));
                    admin.setFullName(rs.getString("full_name"));
                    admin.setPhoneNumber(rs.getString("phone_number"));
                    return admin;
                } else {
                    Passenger passenger = new Passenger();
                    passenger.setUserId(rs.getInt("user_id"));
                    passenger.setUsername(rs.getString("username"));
                    passenger.setPassword(rs.getString("password"));
                    passenger.setEmail(rs.getString("email"));
                    passenger.setFullName(rs.getString("full_name"));
                    passenger.setPhoneNumber(rs.getString("phone_number"));
                    return passenger;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
    }
// }
