import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationScreen {

    JFrame frame;

    public RegistrationScreen() {
        // 1. Frame banana
        frame = new JFrame("Student Registration");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Sirf is window ko band karega
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        // 2. Title
        JLabel titleLabel = new JLabel("Create Student Account");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setBounds(70, 20, 300, 25);
        frame.add(titleLabel);

        // 3. Components
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 70, 80, 25);
        frame.add(nameLabel);

        JTextField nameText = new JTextField();
        nameText.setBounds(140, 70, 180, 25);
        frame.add(nameText);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 110, 80, 25);
        frame.add(userLabel);

        JTextField userText = new JTextField();
        userText.setBounds(140, 110, 180, 25);
        frame.add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 150, 80, 25);
        frame.add(passLabel);

        JPasswordField passText = new JPasswordField();
        passText.setBounds(140, 150, 180, 25);
        frame.add(passText);
        
        JLabel messageLabel = new JLabel("");
        messageLabel.setBounds(140, 240, 200, 25);
        messageLabel.setForeground(Color.RED);
        frame.add(messageLabel);

        // 4. Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(140, 200, 100, 30);
        frame.add(registerButton);

        // --- Button Action ---
        registerButton.addActionListener(e -> {
            String fullName = nameText.getText().trim();
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            // Hashing password (ab humein SecurityUtil ki zaroorat padegi)
            String hashedPassword = SecurityUtil.hashPassword(password);

            if (registerStudent(fullName, username, hashedPassword)) {
                JOptionPane.showMessageDialog(frame, "Registration successful! You can now login.");
                frame.dispose(); // Window band kar do
            } else {
                messageLabel.setText("Username already exists or DB error.");
            }
        });

        frame.setVisible(true);
    }

    // Naye student ko database mein add karne ka function
    private boolean registerStudent(String fullName, String username, String hashedPassword) {
        String sql = "INSERT INTO users (full_name, username, password_hash, role) VALUES (?, ?, ?, 'student')";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            // Check karein ki galti 'duplicate entry' ki hai (username pehle se hai)
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Username already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
}