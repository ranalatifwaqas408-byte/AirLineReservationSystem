package ui;

import dao.UserDAO;
import model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private UserDAO        userDAO;
    private CardLayout     cardLayout;
    private JPanel         mainPanel;

    public LoginFrame() {
        userDAO = new UserDAO();
        AirlineTheme.applyGlobalDefaults();

        setTitle("Fly Jinnah — Airline Reservation System");
        setSize(1000, 660);
        setMinimumSize(new Dimension(860, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AirlineTheme.BACKGROUND);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        mainPanel.add(buildLoginPage(),    "login");
        mainPanel.add(buildRegisterPage(), "register");

        add(mainPanel);
        setVisible(true);
    }

    // ── Shared left hero panel ───────────────────────────────────────────────

    private JPanel buildHeroPanel() {
        JPanel hero = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark gradient base
                g2.setPaint(new GradientPaint(0, 0, new Color(5, 12, 45),
                        0, getHeight(), new Color(0, 60, 160)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(-60, -60, 320, 320);
                g2.fillOval(getWidth()-120, getHeight()-120, 280, 280);

                // Glowing accent line
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(0, 212, 170, 60));
                g2.drawLine(40, getHeight()/2 - 80, 40, getHeight()/2 + 80);

                g2.dispose();
            }
        };
        hero.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = GridBagConstraints.RELATIVE;
        gc.insets = new Insets(6, 32, 6, 32);
        gc.anchor = GridBagConstraints.WEST;

        // Plane icon (unicode)
        JLabel plane = new JLabel("✈");
        plane.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        plane.setForeground(AirlineTheme.ACCENT);
        hero.add(plane, gc);

        JLabel brand = new JLabel("FLY JINNAH");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 34));
        brand.setForeground(Color.WHITE);
        hero.add(brand, gc);

        JLabel tagline = new JLabel("<html><span style='color:#8AACD2;font-size:12pt'>Pakistan's Premium<br>Airline Experience</span></html>");
        tagline.setFont(AirlineTheme.FONT_BODY);
        hero.add(tagline, gc);

        // Decorative separator
        JPanel sep = new JPanel();
        sep.setPreferredSize(new Dimension(60, 3));
        sep.setBackground(AirlineTheme.ACCENT);
        gc.insets = new Insets(12, 32, 12, 32);
        hero.add(sep, gc);
        gc.insets = new Insets(6, 32, 6, 32);

        String[] feats = {"✔  Book flights instantly", "✔  Manage your reservations", "✔  Domestic & International"};
        for (String f : feats) {
            JLabel fl = new JLabel(f);
            fl.setFont(AirlineTheme.FONT_BODY);
            fl.setForeground(new Color(140, 172, 210));
            hero.add(fl, gc);
        }

        return hero;
    }

    // ── Login page ───────────────────────────────────────────────────────────

    private JPanel buildLoginPage() {
        JPanel root = AirlineTheme.gradientPanel();
        root.setLayout(new GridLayout(1, 2));

        root.add(buildHeroPanel());
        root.add(buildLoginForm());
        return root;
    }

    private JPanel buildLoginForm() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AirlineTheme.SURFACE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AirlineTheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(42, 44, 36, 44)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 0, 4, 0);

        // Header
        JLabel title = new JLabel("Welcome Back");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        form.add(title, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 28, 0);
        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(AirlineTheme.FONT_BODY);
        sub.setForeground(AirlineTheme.TEXT_SECONDARY);
        form.add(sub, gc);

        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 6, 0);

        // Username
        gc.gridy++; gc.gridx = 0; gc.gridwidth = 2;
        form.add(AirlineTheme.fieldLabel("Username"), gc);
        gc.gridy++;
        gc.insets = new Insets(0, 0, 18, 0);
        usernameField = AirlineTheme.styledField(18);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username");
        form.add(usernameField, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 6, 0);
        form.add(AirlineTheme.fieldLabel("Password"), gc);
        gc.gridy++;
        gc.insets = new Insets(0, 0, 28, 0);
        passwordField = AirlineTheme.styledPassword(18);
        form.add(passwordField, gc);

        // Login button
        gc.gridy++;
        gc.insets = new Insets(0, 0, 16, 0);
        JButton loginBtn = AirlineTheme.primaryButton("SIGN IN  →");
        loginBtn.setPreferredSize(new Dimension(320, 44));
        loginBtn.addActionListener(e -> performLogin());
        // Allow Enter key
        getRootPane().setDefaultButton(loginBtn);
        form.add(loginBtn, gc);

        // Register link
        gc.gridy++;
        gc.insets = new Insets(0, 0, 0, 0);
        JButton regLink = AirlineTheme.ghostButton("New passenger? Create an account");
        regLink.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        form.add(regLink, gc);

        outer.add(form);
        return outer;
    }

    // ── Register page ────────────────────────────────────────────────────────

    private JPanel buildRegisterPage() {
        JPanel root = AirlineTheme.gradientPanel();
        root.setLayout(new GridLayout(1, 2));

        root.add(buildHeroPanel());
        root.add(buildRegisterForm());
        return root;
    }

    private JPanel buildRegisterForm() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AirlineTheme.SURFACE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AirlineTheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(30, 44, 28, 44)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 0, 4, 0);

        JLabel title = new JLabel("Create Account");
        title.setFont(AirlineTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        form.add(title, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 22, 0);
        JLabel sub = new JLabel("Join Fly Jinnah today");
        sub.setFont(AirlineTheme.FONT_BODY);
        sub.setForeground(AirlineTheme.TEXT_SECONDARY);
        form.add(sub, gc);

        // Two-column layout for fields
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 6, 8);
        gc.weightx = 1.0;

        // Row 1: Full Name | Username
        gc.gridy++; gc.gridwidth = 1; gc.gridx = 0;
        form.add(AirlineTheme.fieldLabel("Full Name"), gc);
        gc.gridx = 1;
        form.add(AirlineTheme.fieldLabel("Username"), gc);

        JTextField fullNameF  = AirlineTheme.styledField(14);
        JTextField usernameF  = AirlineTheme.styledField(14);
        gc.gridy++; gc.insets = new Insets(0, 0, 14, 8);
        gc.gridx = 0; form.add(fullNameF,  gc);
        gc.gridx = 1; form.add(usernameF,  gc);

        // Row 2: Password | Confirm Password
        gc.gridy++; gc.insets = new Insets(0, 0, 6, 8);
        gc.gridx = 0; form.add(AirlineTheme.fieldLabel("Password"), gc);
        gc.gridx = 1; form.add(AirlineTheme.fieldLabel("Confirm Password"), gc);

        JPasswordField passF    = AirlineTheme.styledPassword(14);
        JPasswordField confirmF = AirlineTheme.styledPassword(14);
        gc.gridy++; gc.insets = new Insets(0, 0, 14, 8);
        gc.gridx = 0; form.add(passF,    gc);
        gc.gridx = 1; form.add(confirmF, gc);

        // Row 3: Email | Phone
        gc.gridy++; gc.insets = new Insets(0, 0, 6, 8);
        gc.gridx = 0; form.add(AirlineTheme.fieldLabel("Email Address"), gc);
        gc.gridx = 1; form.add(AirlineTheme.fieldLabel("Phone (11 digits)"), gc);

        JTextField emailF = AirlineTheme.styledField(14);
        JTextField phoneF = AirlineTheme.styledField(14);
        gc.gridy++; gc.insets = new Insets(0, 0, 24, 8);
        gc.gridx = 0; form.add(emailF, gc);
        gc.gridx = 1; form.add(phoneF, gc);

        // Register button (full width)
        gc.gridy++; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(0, 0, 12, 0);
        JButton regBtn = AirlineTheme.primaryButton("CREATE ACCOUNT  →");
        regBtn.setPreferredSize(new Dimension(360, 44));
        regBtn.addActionListener(e -> {
            String pass    = new String(passF.getPassword());
            String confirm = new String(confirmF.getPassword());
            if (!pass.equals(confirm)) {
                showError("Passwords do not match."); return;
            }
            try {
                Passenger p = new Passenger(
                        usernameF.getText(), pass,
                        emailF.getText(), fullNameF.getText(), phoneF.getText()
                );
                if (userDAO.registerUser(p)) {
                    JOptionPane.showMessageDialog(this,
                            "Account created! You can now sign in.",
                            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "login");
                } else {
                    showError("Registration failed — username may already exist.");
                }
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        form.add(regBtn, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 0, 0);
        JButton backBtn = AirlineTheme.ghostButton("← Back to Sign In");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        form.add(backBtn, gc);

        outer.add(form);
        return outer;
    }

    // ── Login logic ──────────────────────────────────────────────────────────

    private void performLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please enter your username and password.");
            return;
        }
        User u = userDAO.loginUser(user, pass);
        if (u instanceof Admin) {
            new AdminDashboard((Admin) u).setVisible(true);
            dispose();
        } else if (u instanceof Passenger) {
            new UserDashboard((Passenger) u).setVisible(true);
            dispose();
        } else {
            showError("Invalid username or password.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
