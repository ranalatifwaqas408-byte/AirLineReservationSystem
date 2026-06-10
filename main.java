import ui.LoginFrame;
import ui.AirlineTheme;
import dao.DatabaseConnection;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Apply dark theme defaults before any UI is created
        AirlineTheme.applyGlobalDefaults();

        // Test DB connection
        DatabaseConnection.getInstance().testConnection();

        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
