import java.sql.Connection; // Swing ko import kiya
import java.sql.DriverManager;
import javax.swing.*;

public class MainTest {

    // Database Credentials (Aapke purane details)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/online_exam_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "012007";

    public static void main(String[] args) {

        String connectionMessage = "";

        // 1. Database Connection Test
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (conn != null) {
                connectionMessage = "DATABASE CONNECTION SUCCESSFUL!";
                conn.close();
            }
        } catch (Exception e) {
            connectionMessage = "DATABASE CONNECTION FAILED! " + e.getMessage();
            e.printStackTrace();
        }

        // 2. Swing Window (GUI) Test
        // Hum 'invokeLater' ka istemaal karte hain taaki Swing thread-safe rahe
        final String finalMessage = connectionMessage;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Setup Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 200);

            JLabel label = new JLabel(finalMessage);
            label.setHorizontalAlignment(JLabel.CENTER); // Text ko center mein rakha

            frame.add(label);
            frame.setVisible(true); // Window ko dikhaya
        });
    }
}