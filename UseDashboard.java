package ui;

import model.*;
import dao.*;
import service.ReservationServiceImpl;
import exceptions.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class UserDashboard extends JFrame {

    private Passenger              currentUser;
    private FlightDAO              flightDAO;
    private ReservationServiceImpl reservationService;
    private JPanel                 contentArea;
    private CardLayout             contentLayout;

    private JTable             flightsTable;
    private JTable             bookingsTable;
    private DefaultTableModel  flightsModel;
    private DefaultTableModel  bookingsModel;

    // Search controls (kept as fields for action listener)
    private JComboBox<String>  sourceCombo;
    private JComboBox<String>  destCombo;
    private JTextField         dateField;

    public UserDashboard(Passenger user) {
        this.currentUser      = user;
        this.flightDAO        = new FlightDAO();
        this.reservationService = new ReservationServiceImpl();

        AirlineTheme.applyGlobalDefaults();
        setTitle("Fly Jinnah — Passenger Portal");
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 660));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AirlineTheme.BACKGROUND);

        setLayout(new BorderLayout());
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildContent(),   BorderLayout.CENTER);

        loadUserBookings();
    }

    // ── Top bar ──────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AirlineTheme.SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AirlineTheme.BORDER_COLOR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 62));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        // Brand
        JLabel brand = new JLabel("✈  FLY JINNAH");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(Color.WHITE);
        bar.add(brand, BorderLayout.WEST);

        // User info + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        JLabel avatar = new JLabel(initials(currentUser.getFullName())) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AirlineTheme.SECONDARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));

        JLabel nameL = new JLabel(currentUser.getFullName());
        nameL.setFont(AirlineTheme.FONT_LABEL);
        nameL.setForeground(AirlineTheme.TEXT_PRIMARY);

        JButton logout = AirlineTheme.dangerButton("Logout");
        logout.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        right.add(avatar);
        right.add(nameL);
        right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AirlineTheme.SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AirlineTheme.BORDER_COLOR);
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        side.add(sideNavItem("🔍  Search Flights",  "search",  true));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(sideNavItem("📋  My Bookings",     "bookings", false));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(sideNavItem("👤  My Profile",      "profile",  false));

        return side;
    }

    private JButton sideNavItem(String label, String card, boolean active) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover() || active) {
                    g2.setColor(new Color(0, 100, 220, 40));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(AirlineTheme.SECONDARY);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(active ? AirlineTheme.FONT_LABEL : AirlineTheme.FONT_BODY);
        btn.setForeground(active ? Color.WHITE : AirlineTheme.TEXT_SECONDARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 16));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> contentLayout.show(contentArea, card));
        return btn;
    }

    // ── Content area ─────────────────────────────────────────────────────────

    private JPanel buildContent() {
        contentLayout = new CardLayout();
        contentArea   = new JPanel(contentLayout);
        contentArea.setOpaque(false);
        contentArea.add(buildSearchPanel(),   "search");
        contentArea.add(buildBookingsPanel(), "bookings");
        contentArea.add(buildProfilePanel(),  "profile");
        return contentArea;
    }

    // ── Search & Book ────────────────────────────────────────────────────────

    private JPanel buildSearchPanel() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Page title
        JLabel title = new JLabel("Search Flights");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        page.add(title, BorderLayout.NORTH);

        // Search bar card
        JPanel searchCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AirlineTheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(AirlineTheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        searchCard.setOpaque(false);

        String[] cities = {
                "Karachi (KHI)", "Lahore (LHE)", "Islamabad (ISB)",
                "Multan (MUX)", "Peshawar (PEW)", "Dubai (DXB)",
                "London (LHR)", "Jeddah (JED)"
        };

        sourceCombo = AirlineTheme.styledCombo(cities);
        destCombo   = AirlineTheme.styledCombo(cities);
        dateField   = AirlineTheme.styledField(12);
        dateField.setText(LocalDate.now().plusDays(1).toString());

        searchCard.add(makeSearchGroup("From", sourceCombo));
        searchCard.add(makeSearchGroup("To",   destCombo));
        searchCard.add(makeSearchGroup("Date (YYYY-MM-DD)", dateField));

        JButton searchBtn = AirlineTheme.primaryButton("🔍  Search");
        searchBtn.setPreferredSize(new Dimension(130, 42));
        searchBtn.addActionListener(e -> performSearch());
        searchCard.add(searchBtn);

        page.add(searchCard, BorderLayout.NORTH);

        // Results table
        String[] cols = {"Flight No", "Airline", "From", "To", "Date", "Departs", "Arrives", "Duration", "Type", "Price (Rs.)", "Seats", "Action"};
        flightsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        flightsTable = new JTable(flightsModel);
        AirlineTheme.styleTable(flightsTable);

        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = flightsTable.rowAtPoint(e.getPoint());
                int col = flightsTable.columnAtPoint(e.getPoint());
                if (col == 11 && row >= 0) {
                    bookFlight(flightsModel.getValueAt(row, 0).toString());
                }
            }
        });

        JScrollPane scroll = AirlineTheme.styledScroll(flightsTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(16, 0, 0, 0),
                BorderFactory.createLineBorder(AirlineTheme.BORDER_COLOR)
        ));
        page.add(scroll, BorderLayout.CENTER);

        return page;
    }

    private JPanel makeSearchGroup(String label, JComponent field) {
        JPanel g = new JPanel(new BorderLayout(0, 5));
        g.setOpaque(false);
        JLabel l = AirlineTheme.fieldLabel(label);
        g.add(l, BorderLayout.NORTH);
        g.add(field, BorderLayout.CENTER);
        return g;
    }

    private void performSearch() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String src  = (String) sourceCombo.getSelectedItem();
            String dest = (String) destCombo.getSelectedItem();
            List<Flight> flights = flightDAO.searchFlights(src, dest, date);

            flightsModel.setRowCount(0);
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No flights found for the selected route and date.",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Flight f : flights) {
                flightsModel.addRow(new Object[]{
                        f.getFlightNumber(),
                        f.getAirline(),
                        f.getSource(),
                        f.getDestination(),
                        f.getDepartureDate(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        f.getDuration(),
                        f instanceof InternationalFlight ? "International" : "Domestic",
                        String.format("%.0f", f.calculateFare()),
                        f.getAvailableSeats(),
                        "BOOK"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookFlight(String flightNo) {
        Flight flight = flightDAO.getAllFlights().stream()
                .filter(f -> f.getFlightNumber().equals(flightNo))
                .findFirst().orElse(null);
        if (flight == null) return;

        // Seat input dialog
        JPanel dlg = new JPanel(new GridLayout(0, 2, 10, 10));
        dlg.setBackground(AirlineTheme.SURFACE);
        dlg.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField seatsF = AirlineTheme.styledField(12);
        JTextField countF = AirlineTheme.styledField(5);
        seatsF.setText("A1, A2");
        countF.setText("1");

        dlg.add(AirlineTheme.fieldLabel("Seat numbers (e.g. A1, A2):")); dlg.add(seatsF);
        dlg.add(AirlineTheme.fieldLabel("Number of passengers:"));        dlg.add(countF);

        int res = JOptionPane.showConfirmDialog(this, dlg,
                "Book " + flightNo + " — " + flight.getSource() + " → " + flight.getDestination(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        try {
            int count   = Integer.parseInt(countF.getText().trim());
            List<String> seats = List.of(seatsF.getText().split(","));

            Booking booking = reservationService.bookFlight(currentUser, flight, seats, count);
            if (booking != null) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>Booking Confirmed!</b><br><br>" +
                                "Reference: <b>" + booking.getBookingReference() + "</b><br>" +
                                "Route: " + flight.getSource() + " → " + flight.getDestination() + "<br>" +
                                "Amount: <b>Rs. " + String.format("%.0f", booking.getTotalAmount()) + "</b></html>",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUserBookings();
            }
        } catch (InsufficientSeatsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Insufficient Seats", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── My Bookings ──────────────────────────────────────────────────────────

    private JPanel buildBookingsPanel() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("My Bookings");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton refresh = AirlineTheme.primaryButton("↻  Refresh");
        refresh.addActionListener(e -> loadUserBookings());
        header.add(refresh, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        String[] cols = {"Booking Ref", "Flight", "Route", "Date", "Seats", "Passengers", "Amount (Rs.)", "Status", "Action"};
        bookingsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingsTable = new JTable(bookingsModel);
        AirlineTheme.styleTable(bookingsTable);

        bookingsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = bookingsTable.rowAtPoint(e.getPoint());
                int col = bookingsTable.columnAtPoint(e.getPoint());
                if (col == 8 && row >= 0) {
                    String val = bookingsModel.getValueAt(row, col).toString();
                    if ("CANCEL".equals(val)) {
                        cancelBooking(bookingsModel.getValueAt(row, 0).toString());
                    }
                }
            }
        });

        page.add(AirlineTheme.styledScroll(bookingsTable), BorderLayout.CENTER);
        return page;
    }

    private void loadUserBookings() {
        if (bookingsModel == null) return;
        bookingsModel.setRowCount(0);
        for (Booking b : reservationService.getUserBookings(currentUser.getUserId())) {
            Flight f = b.getFlight();
            bookingsModel.addRow(new Object[]{
                    b.getBookingReference(),
                    f.getFlightNumber(),
                    f.getSource() + " → " + f.getDestination(),
                    f.getDepartureDate(),
                    b.getSeatNumbersString(),
                    b.getPassengerCount(),
                    String.format("%.0f", b.getTotalAmount()),
                    b.getStatus().toUpperCase(),
                    (b.getStatus().equals("confirmed") && b.isCancellable()) ? "CANCEL" : "—"
            });
        }
    }

    private void cancelBooking(String ref) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + ref + "?\nA full refund will be processed.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                reservationService.cancelBooking(ref);
                JOptionPane.showMessageDialog(this, "Booking cancelled. Refund initiated.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadUserBookings();
            } catch (BookingNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Profile panel ────────────────────────────────────────────────────────

    private JPanel buildProfilePanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AirlineTheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(AirlineTheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                // Accent top bar
                g2.setColor(AirlineTheme.SECONDARY);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.CENTER;

        // Avatar circle
        JLabel av = new JLabel(initials(currentUser.getFullName())) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AirlineTheme.SECONDARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("Segoe UI", Font.BOLD, 28));
        av.setForeground(Color.WHITE);
        av.setHorizontalAlignment(SwingConstants.CENTER);
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(72, 72));
        gc.gridy = 0; gc.insets = new Insets(0,0,16,0);
        card.add(av, gc);

        JLabel nameL = new JLabel(currentUser.getFullName());
        nameL.setFont(AirlineTheme.FONT_TITLE);
        nameL.setForeground(Color.WHITE);
        gc.gridy++; gc.insets = new Insets(0,0,4,0);
        card.add(nameL, gc);

        JLabel roleL = new JLabel("Passenger  ·  @" + currentUser.getUsername());
        roleL.setFont(AirlineTheme.FONT_BODY);
        roleL.setForeground(AirlineTheme.TEXT_SECONDARY);
        gc.gridy++; gc.insets = new Insets(0,0,28,0);
        card.add(roleL, gc);

        // Details grid
        JPanel details = new JPanel(new GridLayout(2, 2, 24, 16));
        details.setOpaque(false);
        details.add(profileField("Email",        currentUser.getEmail()));
        details.add(profileField("Phone",        currentUser.getPhoneNumber()));
        details.add(profileField("Member Since", "2024"));
        details.add(profileField("Status",       "Active Passenger"));
        gc.gridy++; gc.insets = new Insets(0,0,0,0);
        card.add(details, gc);

        page.add(card);
        return page;
    }

    private JPanel profileField(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AirlineTheme.SURFACE_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JLabel l = AirlineTheme.sectionLabel(label);
        JLabel v = new JLabel(value != null ? value : "—");
        v.setFont(AirlineTheme.FONT_LABEL);
        v.setForeground(Color.WHITE);
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++)
            if (!parts[i].isEmpty()) sb.append(parts[i].charAt(0));
        return sb.toString().toUpperCase();
    }
}
