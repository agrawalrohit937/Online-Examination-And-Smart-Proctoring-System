import java.awt.*;
import javax.swing.*;

public class AdminDashboard {

    JFrame frame;

    public AdminDashboard(String adminUsername) {
        // 1. Frame banana
        frame = new JFrame("Admin Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Center mein

        // 2. Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome, " + adminUsername + "!");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setBounds(20, 20, 400, 30);
        frame.add(welcomeLabel);

        // 3. Buttons ke liye Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 20, 20)); // 3 rows, 1 col
        panel.setBounds(150, 80, 300, 250); // Panel ki position

        // 4. Buttons banana
        JButton manageExamsBtn = new JButton("Manage Exams");
        manageExamsBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));

        JButton manageQuestionsBtn = new JButton("Manage Questions");
        manageQuestionsBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
        
        JButton viewResultsBtn = new JButton("View Student Results");
        viewResultsBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));

        // Buttons ko panel mein add karna
        panel.add(manageExamsBtn);
        panel.add(manageQuestionsBtn);
        panel.add(viewResultsBtn);

        frame.add(panel);

        // Abhi ke liye layout ko null rakhte hain taaki setBounds kaam kare
        frame.setLayout(null);
        frame.setVisible(true);
        
        // --- Button Actions ---
        
        // Yeh button hum agle step mein banayenge
        manageExamsBtn.addActionListener(e -> {
            // Nayi ManageExams window kholo
            new ManageExams();
        });
        
// ...
        manageQuestionsBtn.addActionListener(e -> {
            // Nayi ManageQuestions window kholo
            new ManageQuestions();
        });
// ...
        
// ...
        viewResultsBtn.addActionListener(e -> {
            // Nayi ViewResults window kholo
            new ViewResults();
        });
// ...
    }
}