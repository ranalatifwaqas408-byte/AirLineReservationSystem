package ui;

import model.*;
import dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminDashboard extends JFrame {
    private Admin currentAdmin;
    private FlightDAO flightDAO;
    private JTabbedPane tabbedPane;
    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;

    public AdminDashboard(Admin admin) {
        this.currentAdmin = admin;
        this.flightDAO = new FlightDAO();

        setTitle("Fly Jinnah - Admin Dashboard: " + admin.getFullName());
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadAllFlights();
    }

    private void initComponents() {
        getContentPane().setBackground(Color.LIGHT_GRAY);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("MANAGE FLIGHTS", createManageFlightsPanel());
        tabbedPane.addTab("ADD FLIGHT", createAddFlightPanel());
        tabbedPane.addTab("STATISTICS", createStatisticsPanel());
        add(tabbedPane);
    }

    private JPanel createManageFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);

        String[] columns = {"ID", "Flight No", "Airline", "From", "To", "Date", "Departure", "Arrival", "Total", "Available", "Price", "Type", "Delete"};
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

        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setBackground(Color.BLACK);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadAllFlights());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = flightsTable.rowAtPoint(evt.getPoint());
                int col = flightsTable.columnAtPoint(evt.getPoint());
                if (col == 12 && row >= 0) {
                    int flightId = (int) flightsTableModel.getValueAt(row, 0);
                    deleteFlight(flightId);
                }
            }
        });

        return panel;
    }

    private void deleteFlight(int flightId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete flight?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (flightDAO.deleteFlight(flightId)) {
                JOptionPane.showMessageDialog(this, "Flight deleted!");
                loadAllFlights();
            }
        }
    }

    private JPanel createAddFlightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] cities = {"Karachi (KHI)", "Lahore (LHE)", "Islamabad (ISB)", "Multan (MUX)", "Peshawar (PEW)", "Dubai (DXB)", "London (LHR)", "Jeddah (JED)"};

        JTextField flightNoField = new JTextField();
        JComboBox<String> airlineCombo = new JComboBox<>(new String[]{"Fly Jinnah", "PIA", "Airblue"});
        JComboBox<String> sourceCombo = new JComboBox<>(cities);
        JComboBox<String> destCombo = new JComboBox<>(cities);
        JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString());
        JTextField depTimeField = new JTextField("08:00");
        JTextField arrTimeField = new JTextField("10:00");
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(150, 50, 500, 10));
        JTextField priceField = new JTextField("15000");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"domestic", "international"});

        formPanel.add(new JLabel("Flight Number:"));
        formPanel.add(flightNoField);
        formPanel.add(new JLabel("Airline:"));
        formPanel.add(airlineCombo);
        formPanel.add(new JLabel("Source:"));
        formPanel.add(sourceCombo);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destCombo);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Departure Time (HH:MM):"));
        formPanel.add(depTimeField);
        formPanel.add(new JLabel("Arrival Time (HH:MM):"));
        formPanel.add(arrTimeField);
        formPanel.add(new JLabel("Total Seats:"));
        formPanel.add(seatsSpinner);
        formPanel.add(new JLabel("Base Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Flight Type:"));
        formPanel.add(typeCombo);

        JButton addBtn = new JButton("ADD FLIGHT");
        addBtn.setBackground(Color.BLACK);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText());
                LocalTime depTime = LocalTime.parse(depTimeField.getText() + ":00");
                LocalTime arrTime = LocalTime.parse(arrTimeField.getText() + ":00");

                Flight flight;
                if (typeCombo.getSelectedItem().equals("international")) {
                    flight = new InternationalFlight(flightNoField.getText(), (String)airlineCombo.getSelectedItem(),
                            (String)sourceCombo.getSelectedItem(), (String)destCombo.getSelectedItem(),
                            date, depTime, arrTime, (Integer)seatsSpinner.getValue(), Double.parseDouble(priceField.getText()));
                } else {
                    flight = new DomesticFlight(flightNoField.getText(), (String)airlineCombo.getSelectedItem(),
                            (String)sourceCombo.getSelectedItem(), (String)destCombo.getSelectedItem(),
                            date, depTime, arrTime, (Integer)seatsSpinner.getValue(), Double.parseDouble(priceField.getText()));
                }

                if (flightDAO.addFlight(flight)) {
                    JOptionPane.showMessageDialog(this, "Flight added!");
                    loadAllFlights();
                    tabbedPane.setSelectedIndex(0);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel);
        panel.add(addBtn);
        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setBackground(Color.LIGHT_GRAY);

        List<Flight> flights = flightDAO.getAllFlights();

        JPanel card1 = createStatCard("Total Flights", String.valueOf(flights.size()), Color.BLACK);
        JPanel card2 = createStatCard("Active Flights", String.valueOf(flights.stream().filter(f -> f.getStatus().equals("active")).count()), Color.BLACK);
        JPanel card3 = createStatCard("Total Seats", String.valueOf(flights.stream().mapToInt(Flight::getTotalSeats).sum()), Color.BLACK);
        JPanel card4 = createStatCard("Available Seats", String.valueOf(flights.stream().mapToInt(Flight::getAvailableSeats).sum()), Color.BLACK);

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        panel.add(logoutBtn);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(200, 120));

        return card;
    }

    private void loadAllFlights() {
        flightsTableModel.setRowCount(0);
        for (Flight flight : flightDAO.getAllFlights()) {
            Object[] row = {
                    flight.getFlightId(),
                    flight.getFlightNumber(),
                    flight.getAirline(),
                    flight.getSource(),
                    flight.getDestination(),
                    flight.getDepartureDate(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    flight.getTotalSeats(),
                    flight.getAvailableSeats(),
                    String.format("Rs.%.0f", flight.getBasePrice()),
                    flight instanceof InternationalFlight ? "Intl" : "Dom",
                    "DELETE"
            };
            flightsTableModel.addRow(row);
        }
    }
}