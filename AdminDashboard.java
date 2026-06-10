package ui;

import model.*;
import dao.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminDashboard extends JFrame {

    private Admin          currentAdmin;
    private FlightDAO      flightDAO;
    private JPanel         contentArea;
    private CardLayout     contentLayout;

    private JTable            flightsTable;
    private DefaultTableModel flightsModel;

    // Add-flight form fields
    private JTextField         flightNoF, dateF, depTimeF, arrTimeF, priceF;
    private JComboBox<String>  airlineC, sourceC, destC, typeC;
    private JSpinner           seatsS;

    public AdminDashboard(Admin admin) {
        this.currentAdmin = admin;
        this.flightDAO    = new FlightDAO();

        AirlineTheme.applyGlobalDefaults();
        setTitle("Fly Jinnah — Admin Console");
        setSize(1360, 820);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AirlineTheme.BACKGROUND);

        setLayout(new BorderLayout());
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        loadAllFlights();
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

        JLabel brand = new JLabel("✈  FLY JINNAH  ·  Admin Console");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(Color.WHITE);
        bar.add(brand, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        JLabel badge = new JLabel(" ADMIN ");
        badge.setFont(AirlineTheme.FONT_SMALL);
        badge.setForeground(Color.WHITE);
        badge.setBackground(AirlineTheme.SECONDARY);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel nameL = new JLabel(currentAdmin.getFullName());
        nameL.setFont(AirlineTheme.FONT_LABEL);
        nameL.setForeground(AirlineTheme.TEXT_PRIMARY);

        JButton logout = AirlineTheme.dangerButton("Logout");
        logout.addActionListener(e -> { new LoginFrame(); dispose(); });

        right.add(badge);
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

        side.add(adminNavBtn("📊  Dashboard",       "stats"));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(adminNavBtn("✈  Manage Flights",   "flights"));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(adminNavBtn("➕  Add Flight",       "add"));
        return side;
    }

    private JButton adminNavBtn(String label, String card) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 100, 220, 40));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(AirlineTheme.SECONDARY);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(AirlineTheme.FONT_BODY);
        btn.setForeground(AirlineTheme.TEXT_SECONDARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 16));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> contentLayout.show(contentArea, card));
        return btn;
    }

    // ── Content ──────────────────────────────────────────────────────────────

    private JPanel buildContent() {
        contentLayout = new CardLayout();
        contentArea   = new JPanel(contentLayout);
        contentArea.setOpaque(false);
        contentArea.add(buildStatsPanel(),   "stats");
        contentArea.add(buildFlightsPanel(), "flights");
        contentArea.add(buildAddPanel(),     "add");
        return contentArea;
    }

    // ── Stats dashboard ───────────────────────────────────────────────────────

    private JPanel buildStatsPanel() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        page.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
        cards.setOpaque(false);

        List<Flight> all = flightDAO.getAllFlights();
        long active   = all.stream().filter(f -> "active".equals(f.getStatus())).count();
        int  totSeats = all.stream().mapToInt(Flight::getTotalSeats).sum();
        int  avSeats  = all.stream().mapToInt(Flight::getAvailableSeats).sum();

        cards.add(AirlineTheme.statCard("Total Flights",     String.valueOf(all.size()), AirlineTheme.SECONDARY));
        cards.add(AirlineTheme.statCard("Active Flights",    String.valueOf(active),     AirlineTheme.ACCENT));
        cards.add(AirlineTheme.statCard("Total Seats",       String.valueOf(totSeats),   AirlineTheme.WARNING));
        cards.add(AirlineTheme.statCard("Available Seats",   String.valueOf(avSeats),    AirlineTheme.SUCCESS));

        page.add(cards, BorderLayout.CENTER);

        // Quick-action hint
        JLabel hint = new JLabel("Use the sidebar to manage flights or add new ones.");
        hint.setFont(AirlineTheme.FONT_BODY);
        hint.setForeground(AirlineTheme.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        page.add(hint, BorderLayout.SOUTH);

        return page;
    }

    // ── Manage Flights ────────────────────────────────────────────────────────

    private JPanel buildFlightsPanel() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Manage Flights");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton refresh = AirlineTheme.primaryButton("↻  Refresh");
        refresh.addActionListener(e -> loadAllFlights());
        header.add(refresh, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Flight No", "Airline", "From", "To", "Date", "Departs", "Arrives", "Total", "Avail.", "Price", "Type", "Action"};
        flightsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        flightsTable = new JTable(flightsModel);
        AirlineTheme.styleTable(flightsTable);

        // Hide ID column visually
        flightsTable.getColumnModel().getColumn(0).setMinWidth(0);
        flightsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        flightsTable.getColumnModel().getColumn(0).setWidth(0);

        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = flightsTable.rowAtPoint(e.getPoint());
                int col = flightsTable.columnAtPoint(e.getPoint());
                if (col == 12 && row >= 0) {
                    int id = (int) flightsModel.getValueAt(row, 0);
                    deleteFlight(id, flightsModel.getValueAt(row, 1).toString());
                }
            }
        });

        page.add(AirlineTheme.styledScroll(flightsTable), BorderLayout.CENTER);
        return page;
    }

    private void deleteFlight(int id, String flightNo) {
        int c = JOptionPane.showConfirmDialog(this,
                "Delete flight " + flightNo + "? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            if (flightDAO.deleteFlight(id)) {
                JOptionPane.showMessageDialog(this, "Flight deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
                loadAllFlights();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete flight.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAllFlights() {
        if (flightsModel == null) return;
        flightsModel.setRowCount(0);
        for (Flight f : flightDAO.getAllFlights()) {
            flightsModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getFlightNumber(),
                    f.getAirline(),
                    f.getSource(),
                    f.getDestination(),
                    f.getDepartureDate(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    f.getTotalSeats(),
                    f.getAvailableSeats(),
                    String.format("Rs.%.0f", f.getBasePrice()),
                    f instanceof InternationalFlight ? "International" : "Domestic",
                    "DELETE"
            });
        }
    }

    // ── Add Flight ────────────────────────────────────────────────────────────

    private JPanel buildAddPanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AirlineTheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(AirlineTheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.setColor(AirlineTheme.SECONDARY);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 8, 0);
        gc.gridx  = 0; gc.gridy = 0; gc.gridwidth = 4;

        JLabel title = new JLabel("Add New Flight");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(title, gc);

        String[] cities   = {"Karachi (KHI)", "Lahore (LHE)", "Islamabad (ISB)", "Multan (MUX)", "Peshawar (PEW)", "Dubai (DXB)", "London (LHR)", "Jeddah (JED)"};
        String[] airlines = {"Fly Jinnah", "PIA", "Airblue"};

        flightNoF  = AirlineTheme.styledField(12);
        airlineC   = AirlineTheme.styledCombo(airlines);
        sourceC    = AirlineTheme.styledCombo(cities);
        destC      = AirlineTheme.styledCombo(cities);
        dateF      = AirlineTheme.styledField(12);  dateF.setText(LocalDate.now().plusDays(1).toString());
        depTimeF   = AirlineTheme.styledField(8);   depTimeF.setText("08:00");
        arrTimeF   = AirlineTheme.styledField(8);   arrTimeF.setText("10:00");
        seatsS     = new JSpinner(new SpinnerNumberModel(150, 50, 500, 10));
        seatsS.setBackground(AirlineTheme.SURFACE_LIGHT);
        seatsS.setForeground(AirlineTheme.TEXT_PRIMARY);
        priceF     = AirlineTheme.styledField(10);  priceF.setText("15000");
        typeC      = AirlineTheme.styledCombo(new String[]{"domestic", "international"});

        // Layout rows: label | field | label | field
        addFormRow(card, gc, "Flight Number",        flightNoF,  "Airline",          airlineC,  1);
        addFormRow(card, gc, "From",                 sourceC,    "To",               destC,     2);
        addFormRow(card, gc, "Date (YYYY-MM-DD)",    dateF,      "Flight Type",      typeC,     3);
        addFormRow(card, gc, "Departure (HH:MM)",    depTimeF,   "Arrival (HH:MM)",  arrTimeF,  4);
        addFormRow(card, gc, "Total Seats",          seatsS,     "Base Price (Rs.)", priceF,    5);

        // Submit button
        gc.gridy = 6; gc.gridx = 0; gc.gridwidth = 4;
        gc.insets = new Insets(24, 0, 0, 0);
        JButton addBtn = AirlineTheme.primaryButton("  ➕  ADD FLIGHT  ");
        addBtn.setPreferredSize(new Dimension(200, 44));
        addBtn.addActionListener(e -> submitFlight());
        card.add(addBtn, gc);

        page.add(card);
        return page;
    }

    private void addFormRow(JPanel form, GridBagConstraints gc,
                            String lbl1, JComponent f1,
                            String lbl2, JComponent f2, int row) {
        gc.insets = new Insets(0, 0, 6, 16);
        gc.gridwidth = 1; gc.weightx = 0;

        gc.gridy = row * 2 - 1; gc.gridx = 0;
        form.add(AirlineTheme.fieldLabel(lbl1), gc);
        gc.gridx = 2;
        form.add(AirlineTheme.fieldLabel(lbl2), gc);

        gc.gridy = row * 2; gc.gridx = 0;
        gc.weightx = 1; gc.insets = new Insets(0, 0, 18, 24);
        form.add(f1, gc);
        gc.gridx = 2;
        form.add(f2, gc);
    }

    private void submitFlight() {
        try {
            LocalDate date    = LocalDate.parse(dateF.getText().trim());
            LocalTime depTime = LocalTime.parse(depTimeF.getText().trim() + ":00");
            LocalTime arrTime = LocalTime.parse(arrTimeF.getText().trim() + ":00");
            int       seats   = (Integer) seatsS.getValue();
            double    price   = Double.parseDouble(priceF.getText().trim());

            Flight flight;
            if ("international".equals(typeC.getSelectedItem())) {
                flight = new InternationalFlight(
                        flightNoF.getText(), (String) airlineC.getSelectedItem(),
                        (String) sourceC.getSelectedItem(), (String) destC.getSelectedItem(),
                        date, depTime, arrTime, seats, price);
            } else {
                flight = new DomesticFlight(
                        flightNoF.getText(), (String) airlineC.getSelectedItem(),
                        (String) sourceC.getSelectedItem(), (String) destC.getSelectedItem(),
                        date, depTime, arrTime, seats, price);
            }

            if (flightDAO.addFlight(flight)) {
                JOptionPane.showMessageDialog(this,
                        "Flight " + flightNoF.getText() + " added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllFlights();
                contentLayout.show(contentArea, "flights");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add flight.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
