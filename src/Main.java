import ui.LoginFrame;
import dao.DatabaseConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseConnection.getInstance().testConnection();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}