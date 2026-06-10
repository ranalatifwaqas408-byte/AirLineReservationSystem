package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class AirlineTheme {

    // ── Palette ────────────────────────────────────────────────────────────────
    public static final Color PRIMARY         = new Color(10,  25,  60);   // Deep Navy
    public static final Color SECONDARY       = new Color(0,  122, 255);   // Electric Blue
    public static final Color ACCENT          = new Color(0,  212, 170);   // Teal/Cyan accent
    public static final Color BACKGROUND      = new Color(8,  16,  40);    // Dark Navy BG
    public static final Color SURFACE         = new Color(15, 30,  70);    // Card surface
    public static final Color SURFACE_LIGHT   = new Color(20, 40,  90);    // Lighter card
    public static final Color BORDER_COLOR    = new Color(30, 60,  120);   // Subtle border
    public static final Color TEXT_PRIMARY    = new Color(240, 245, 255);  // Near white
    public static final Color TEXT_SECONDARY  = new Color(140, 165, 210);  // Muted blue-white
    public static final Color TEXT_MUTED      = new Color(80,  110, 160);  // Dim text
    public static final Color SUCCESS         = new Color(0,  210, 130);   // Green
    public static final Color DANGER          = new Color(255, 80,  80);   // Red
    public static final Color WARNING         = new Color(255, 190, 50);   // Amber
    public static final Color HOVER_BLUE      = new Color(30, 150, 255);   // Hover state
    public static final Color TABLE_ROW_EVEN  = new Color(12, 24,  58);
    public static final Color TABLE_ROW_ODD   = new Color(17, 34,  75);
    public static final Color TABLE_SELECTED  = new Color(0,  80,  200);
    public static final Color TABLE_HEADER    = new Color(5,  15,  45);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_HERO    = new Font("Segoe UI", Font.BOLD,  32);
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TABLE_H = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.BOLD,  13);

    // ── Global UIManager defaults ─────────────────────────────────────────────
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",              BACKGROUND);
        UIManager.put("OptionPane.background",         SURFACE);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
        UIManager.put("Button.background",             SECONDARY);
        UIManager.put("Button.foreground",             Color.WHITE);
        UIManager.put("Button.font",                   FONT_BUTTON);
        UIManager.put("Label.foreground",              TEXT_PRIMARY);
        UIManager.put("Label.font",                    FONT_BODY);
        UIManager.put("TextField.background",          SURFACE_LIGHT);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     ACCENT);
        UIManager.put("PasswordField.background",      SURFACE_LIGHT);
        UIManager.put("PasswordField.foreground",      TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground", ACCENT);
        UIManager.put("ComboBox.background",           SURFACE_LIGHT);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",  SECONDARY);
        UIManager.put("ComboBox.selectionForeground",  Color.WHITE);
        UIManager.put("ScrollPane.background",         BACKGROUND);
        UIManager.put("Viewport.background",           BACKGROUND);
        UIManager.put("TabbedPane.background",         SURFACE);
        UIManager.put("TabbedPane.foreground",         TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",           SECONDARY);
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.tabAreaBackground",  SURFACE);
        UIManager.put("Spinner.background",            SURFACE_LIGHT);
        UIManager.put("Spinner.foreground",            TEXT_PRIMARY);
    }

    // ── Component factories ───────────────────────────────────────────────────

    /** Primary action button — filled electric blue */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed()  ? new Color(0, 90, 200)
                        : getModel().isRollover() ? HOVER_BLUE
                        : SECONDARY;
                g2.setPaint(new GradientPaint(0,0, base.brighter(), 0, getHeight(), base));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return btn;
    }

    /** Danger / cancel button — filled red */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? new Color(220,50,50) : DANGER;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    /** Ghost / link-style button */
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(ACCENT);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setForeground(ACCENT); }
        });
        return btn;
    }

    /** Styled text field */
    public static JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(SURFACE_LIGHT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        return f;
    }

    /** Styled password field */
    public static JPasswordField styledPassword(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setBackground(SURFACE_LIGHT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        return f;
    }

    /** Styled combo box */
    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(SURFACE_LIGHT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        return cb;
    }

    /** Card panel (rounded, surface color) */
    public static JPanel cardPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /** Section label (small caps style) */
    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    /** Field label */
    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    /** Status badge */
    public static JLabel statusBadge(String status) {
        JLabel l = new JLabel(" " + status.toUpperCase() + " ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color col = "confirmed".equalsIgnoreCase(status) ? SUCCESS
                        : "cancelled".equalsIgnoreCase(status)  ? DANGER
                        : WARNING;
                g2.setColor(col.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(FONT_SMALL);
        l.setForeground(Color.WHITE);
        l.setOpaque(false);
        return l;
    }

    /** Applies modern styling to any JTable */
    public static void styleTable(JTable table) {
        table.setBackground(TABLE_ROW_EVEN);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_TABLE);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(FONT_TABLE_H);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setFont(FONT_TABLE);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    setForeground(TEXT_PRIMARY);
                }
                // Highlight BOOK / DELETE cells
                String val = value != null ? value.toString() : "";
                if ("BOOK".equals(val)) {
                    setForeground(ACCENT);
                    setFont(FONT_BUTTON);
                } else if ("DELETE".equals(val)) {
                    setForeground(DANGER);
                    setFont(FONT_BUTTON);
                } else if ("CANCEL".equals(val)) {
                    setForeground(WARNING);
                    setFont(FONT_BUTTON);
                } else if ("CONFIRMED".equals(val)) {
                    setForeground(SUCCESS);
                } else if ("CANCELLED".equals(val)) {
                    setForeground(DANGER);
                }
                return this;
            }
        });
    }

    /** Styled scroll pane */
    public static JScrollPane styledScroll(JComponent view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(TABLE_ROW_EVEN);
        sp.setBackground(BACKGROUND);
        sp.getVerticalScrollBar().setBackground(SURFACE);
        sp.getVerticalScrollBar().setForeground(BORDER_COLOR);
        return sp;
    }

    /** Gradient background panel — vertical navy gradient */
    public static JPanel gradientPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BACKGROUND,
                        0, getHeight(), new Color(5, 12, 35)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    /** Stat card with a colored top bar */
    public static JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(FONT_SMALL);
        t.setForeground(TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 36));
        v.setForeground(accent);

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }
}
