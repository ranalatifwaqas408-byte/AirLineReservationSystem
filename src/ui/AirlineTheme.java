package ui;

import java.awt.*;
import javax.swing.*;

public class AirlineTheme {
    // Primary Colors - Fly Jinnah inspired
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);     // Deep Blue
    public static final Color SECONDARY_COLOR = new Color(46, 204, 113);  // Fly Jinnah Green
    public static final Color ACCENT_COLOR = new Color(241, 196, 15);     // Gold/Yellow
    public static final Color BACKGROUND_COLOR = new Color(245, 248, 250); // Light Gray-Blue
    public static final Color TEXT_COLOR = new Color(44, 62, 80);          // Dark Blue-Gray
    public static final Color HOVER_COLOR = new Color(41, 128, 185);       // Lighter Blue
    public static final Color ERROR_COLOR = new Color(231, 76, 60);        // Red
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);     // Green

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public static void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
    }

    public static void styleTable(JTable table) {
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(248, 249, 250));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
    }
}