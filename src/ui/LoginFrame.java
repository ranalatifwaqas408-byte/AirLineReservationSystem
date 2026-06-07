package ui;

import dao.UserDAO;
import model.*;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public LoginFrame() {
        userDAO = new UserDAO();
        setTitle("Fly Jinnah - Airline Reservation System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createRegisterPanel(), "register");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 102, 204),
                        getWidth(), getHeight(), new Color(46, 204, 113));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // White form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Airline Logo/Title
        JLabel titleLabel = new JLabel("FLY JINNAH AIRLINES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Welcome Back! Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        gbc.gridy = 1;
        formPanel.add(subtitleLabel, gbc);

        // Separator
        JSeparator separator = new JSeparator();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        formPanel.add(separator, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;

        // Username
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel userIcon = new JLabel("USERNAME:");
        userIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(userIcon, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel passIcon = new JLabel("PASSWORD:");
        passIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(passIcon, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(passwordField, gbc);

        // Login Button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> performLogin());

        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(0, 102, 204));
            }
        });

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(loginBtn, gbc);

        // Register link
        JButton registerLink = new JButton("New User? Register Here");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(new Color(46, 204, 113));
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        gbc.gridy = 6;
        formPanel.add(registerLink, gbc);

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(50, 50, 50, 50);
        panel.add(formPanel, mainGbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(46, 204, 113),
                        getWidth(), getHeight(), new Color(0, 102, 204));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        // Title
        JLabel titleLabel = new JLabel("CREATE NEW ACCOUNT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Registration fields
        String[] labels = {"FULL NAME:", "USERNAME:", "PASSWORD:", "CONFIRM PASSWORD:", "EMAIL:", "PHONE NUMBER:"};
        JTextField[] fields = new JTextField[6];
        fields[0] = new JTextField(15);
        fields[1] = new JTextField(15);
        fields[2] = new JPasswordField(15);
        fields[3] = new JPasswordField(15);
        fields[4] = new JTextField(15);
        fields[5] = new JTextField(15);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridwidth = 1;
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            formPanel.add(label, gbc);
            gbc.gridx = 1;
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            formPanel.add(fields[i], gbc);
        }

        // Register Button
        JButton registerBtn = new JButton("REGISTER");
        registerBtn.setBackground(new Color(0, 102, 204));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> {
            if (!new String(((JPasswordField)fields[2]).getPassword()).equals(new String(((JPasswordField)fields[3]).getPassword()))) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            Passenger passenger = new Passenger(
                    fields[1].getText(),
                    new String(((JPasswordField)fields[2]).getPassword()),
                    fields[4].getText(),
                    fields[0].getText(),
                    fields[5].getText()
            );

            if (userDAO.registerUser(passenger)) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                cardLayout.show(mainPanel, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed! Username may already exist.");
            }
        });

        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(0, 102, 204));
            }
        });

        gbc.gridy = labels.length + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(registerBtn, gbc);

        // Back to login link
        JButton backBtn = new JButton("BACK TO LOGIN");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(46, 204, 113));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = labels.length + 2;
        formPanel.add(backBtn, gbc);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(30, 50, 30, 50);
        panel.add(formPanel, mainGbc);

        return panel;
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = userDAO.loginUser(username, password);
        if (user != null) {
            if (user instanceof Admin) {
                new AdminDashboard((Admin) user).setVisible(true);
            } else {
                new UserDashboard((Passenger) user).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }
}