import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;       // <-- ZAROORI IMPORT
import java.sql.ResultSet;  // <-- ZAROORI IMPORT
import javax.swing.*;         // <-- ZAROORI IMPORT

public class LoginScreen {

    public static void main(String[] args) {
        // Swing ko hamesha thread-safe tareeke se run karna chahiye
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // 1. Window (Frame) banana
        JFrame frame = new JFrame("Online Exam - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);

        // 2. Panel banana
        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        // 3. Components
        JLabel titleLabel = new JLabel("Welcome");
        titleLabel.setBounds(150, 20, 100, 25);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 60, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(130, 60, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(130, 100, 165, 25);
        panel.add(passwordText);

        JLabel messageLabel = new JLabel("");
        messageLabel.setBounds(130, 170, 200, 25);
        messageLabel.setForeground(Color.RED);
        panel.add(messageLabel);

        // 4. Login Button
        JButton loginButton = new JButton("Sign in");
        loginButton.setBounds(130, 140, 80, 25);
        panel.add(loginButton);

        // --- YEH NAYA BUTTON HAI ---
        // 4b. Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(220, 140, 90, 25); // Login button ke bagal mein
        panel.add(registerButton);
        
        // Register Button ka Action
        registerButton.addActionListener(e -> {
            // Nayi RegistrationScreen window kholo
            new RegistrationScreen();
        });
        // --- NAYA CODE END ---

        // 5. Login Button ka Click Action
        // 5. Login Button ka Click Action
        loginButton.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passwordText.getPassword()).trim();

            // ValidateLogin ab 'User' object (ya 'null') return karega
            User loggedInUser = validateLogin(username, password); 

            if (loggedInUser != null) {
                // Login successful!
                frame.dispose(); // Login window band karo

                if (loggedInUser.getRole().equals("admin")) {
                    // Admin hai, Admin Dashboard kholo
                    new AdminDashboard(loggedInUser.getUsername());
                } else {
                    // Student hai, Student Dashboard kholo
                    new StudentDashboard(loggedInUser); // User object pass karo
                }

            } else {
                // Login fail hua
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password");
            }
        });

        // 6. Frame ko visible karna
        frame.setVisible(true);
    }

    // --- YEH NAYA VALIDATE LOGIN FUNCTION HAI ---
    // Database se login check karne ka function
    // Yeh ab 'null' return karega agar login fail hota hai,
    // ya 'admin'/'student' return karega agar success hota hai.
    // --- YEH NAYA VALIDATE LOGIN FUNCTION HAI ---
    // Yeh ab 'null' return karega agar login fail hota hai,
    // ya ek 'User' object return karega agar success hota hai.
    private static User validateLogin(String username, String password) {

        System.out.println("Validating user: " + username);
        String sql = "SELECT user_id, password_hash, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Asli password check
                if (SecurityUtil.checkPassword(password, storedHash)) {
                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");
                    System.out.println("Login Successful! Role: " + role);

                    // Naya User object return karo
                    return new User(userId, username, role);
                }
            }

            System.out.println("Invalid username or password.");
            return null; // User nahi mila ya password galat hai

        } catch (Exception e) {
            e.printStackTrace();
            return null; // DB error
        }
    }
}