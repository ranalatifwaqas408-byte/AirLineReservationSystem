package ui;

import model.*;
import dao.*;
import service.ReservationServiceImpl;
import exceptions.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class UserDashboard extends JFrame {
    private Passenger currentUser;
    private FlightDAO flightDAO;
    private ReservationServiceImpl reservationService;
    private JTabbedPane tabbedPane;
    private JTable flightsTable;
    private JTable bookingsTable;
    private DefaultTableModel flightsTableModel;
    private DefaultTableModel bookingsTableModel;

    public UserDashboard(Passenger user) {
        this.currentUser = user;
        this.flightDAO = new FlightDAO();
        this.reservationService = new ReservationServiceImpl();

        setTitle("Fly Jinnah - Passenger Dashboard: " + user.getFullName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadUserBookings();
    }

    private void initComponents() {
        getContentPane().setBackground(Color.LIGHT_GRAY);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("SEARCH FLIGHTS", createSearchFlightsPanel());
        tabbedPane.addTab("MY BOOKINGS", createMyBookingsPanel());
        tabbedPane.addTab("MY PROFILE", createProfilePanel());
        add(tabbedPane);
    }

    private JPanel createSearchFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        String[] cities = {"Karachi (KHI)", "Lahore (LHE)", "Islamabad (ISB)", "Multan (MUX)", "Peshawar (PEW)", "Dubai (DXB)", "London (LHR)", "Jeddah (JED)"};

        JComboBox<String> sourceCombo = new JComboBox<>(cities);
        JComboBox<String> destCombo = new JComboBox<>(cities);
        JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString(), 10);

        searchPanel.add(new JLabel("FROM:"));
        searchPanel.add(sourceCombo);
        searchPanel.add(new JLabel("TO:"));
        searchPanel.add(destCombo);
        searchPanel.add(new JLabel("DATE:"));
        searchPanel.add(dateField);

        JButton searchBtn = new JButton("SEARCH");
        searchBtn.setBackground(Color.BLACK);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Flights Table
        String[] columns = {"Flight No", "Airline", "From", "To", "Date", "Departure", "Arrival", "Price", "Seats", "Book"};
        flightsTableModel = new DefaultTableModel(columns, 0);
        flightsTable = new JTable(flightsTableModel);
        flightsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        flightsTable.setRowHeight(30);

        // SIMPLE HIGH CONTRAST HEADER
        flightsTable.getTableHeader().setBackground(Color.BLACK);
        flightsTable.getTableHeader().setForeground(Color.WHITE);
        flightsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText());
                List<Flight> flights = flightDAO.searchFlights(
                        (String) sourceCombo.getSelectedItem(),
                        (String) destCombo.getSelectedItem(),
                        date);
                displayFlights(flights);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date! Use YYYY-MM-DD");
            }
        });

        return panel;
    }

    private void displayFlights(List<Flight> flights) {
        flightsTableModel.setRowCount(0);
        for (Flight flight : flights) {
            Object[] row = {
                    flight.getFlightNumber(),
                    flight.getAirline(),
                    flight.getSource(),
                    flight.getDestination(),
                    flight.getDepartureDate(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    String.format("Rs.%.0f", flight.calculateFare()),
                    flight.getAvailableSeats(),
                    "BOOK"
            };
            flightsTableModel.addRow(row);
        }

        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = flightsTable.rowAtPoint(evt.getPoint());
                int col = flightsTable.columnAtPoint(evt.getPoint());
                if (col == 9 && row >= 0) {
                    String flightNo = flightsTableModel.getValueAt(row, 0).toString();
                    bookFlight(flightNo);
                }
            }
        });
    }

    private void bookFlight(String flightNo) {
        Flight flight = null;
        for (Flight f : flightDAO.getAllFlights()) {
            if (f.getFlightNumber().equals(flightNo)) {
                flight = f;
                break;
            }
        }

        if (flight != null) {
            String seats = JOptionPane.showInputDialog(this, "Enter seat numbers (comma separated):\nExample: A1, A2", "Book Flight", JOptionPane.QUESTION_MESSAGE);
            if (seats != null && !seats.trim().isEmpty()) {
                String passengerCountStr = JOptionPane.showInputDialog(this, "Number of passengers:", "Passenger Count", JOptionPane.QUESTION_MESSAGE);
                if (passengerCountStr != null) {
                    try {
                        int passengerCount = Integer.parseInt(passengerCountStr);
                        List<String> seatNumbers = List.of(seats.split(","));

                        Booking booking = reservationService.bookFlight(currentUser, flight, seatNumbers, passengerCount);
                        JOptionPane.showMessageDialog(this, "Booking Successful!\nReference: " + booking.getBookingReference() + "\nAmount: Rs." + booking.getTotalAmount());
                        loadUserBookings();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);

        String[] columns = {"Booking Ref", "Flight", "Date", "Seats", "Passengers", "Amount", "Status", "Cancel"};
        bookingsTableModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingsTable.setRowHeight(30);

        // SIMPLE HIGH CONTRAST HEADER
        bookingsTable.getTableHeader().setBackground(Color.BLACK);
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setBackground(Color.BLACK);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadUserBookings());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUserBookings() {
        bookingsTableModel.setRowCount(0);
        List<Booking> bookings = reservationService.getUserBookings(currentUser.getUserId());

        for (Booking booking : bookings) {
            Object[] row = {
                    booking.getBookingReference(),
                    booking.getFlight().getFlightNumber(),
                    booking.getFlight().getDepartureDate(),
                    booking.getSeatNumbersString(),
                    booking.getPassengerCount(),
                    String.format("Rs.%.2f", booking.getTotalAmount()),
                    booking.getStatus().toUpperCase(),
                    booking.getStatus().equals("confirmed") && booking.isCancellable() ? "CANCEL" : ""
            };
            bookingsTableModel.addRow(row);
        }

        bookingsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = bookingsTable.rowAtPoint(evt.getPoint());
                int col = bookingsTable.columnAtPoint(evt.getPoint());
                if (col == 7 && row >= 0) {
                    String value = bookingsTableModel.getValueAt(row, col).toString();
                    if (value.equals("CANCEL")) {
                        String bookingRef = bookingsTableModel.getValueAt(row, 0).toString();
                        cancelBooking(bookingRef);
                    }
                }
            }
        });
    }

    private void cancelBooking(String bookingRef) {
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel booking " + bookingRef + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                reservationService.cancelBooking(bookingRef);
                JOptionPane.showMessageDialog(this, "Booking cancelled!");
                loadUserBookings();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoPanel.add(new JLabel("Full Name:"));
        infoPanel.add(new JLabel(currentUser.getFullName()));
        infoPanel.add(new JLabel("Username:"));
        infoPanel.add(new JLabel(currentUser.getUsername()));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(currentUser.getEmail()));
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(currentUser.getPhoneNumber()));

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        panel.add(infoPanel);
        panel.add(logoutBtn);
        return panel;
    }
}