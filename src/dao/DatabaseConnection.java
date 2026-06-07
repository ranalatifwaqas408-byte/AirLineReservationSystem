package dao;

import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private BlockingQueue<Connection> connectionPool;
    private static final int POOL_SIZE = 10;

    // Update these credentials
    private static final String URL = "jdbc:mysql://localhost:3306/airline_reservation_pk?useSSL=false&serverTimezone=Asia/Karachi";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Allah4us4560()";

    private DatabaseConnection() {
        connectionPool = new LinkedBlockingQueue<>(POOL_SIZE);
        initializeConnectionPool();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void initializeConnectionPool() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                connectionPool.offer(conn);
            }
            System.out.println("Database connection pool initialized with " + POOL_SIZE + " connections");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionPool.poll(5, TimeUnit.SECONDS);
            if (conn == null) {
                throw new SQLException("Timeout waiting for connection from pool");
            }
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    connectionPool.offer(conn);
                }
            } catch (SQLException e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            }
        }
    }

    public static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (conn != null) {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
    }

    public void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}