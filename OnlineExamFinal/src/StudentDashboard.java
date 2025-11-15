import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List; // Import karein
import java.util.Vector;

public class StudentDashboard {

    JFrame frame;
    private JTable examTable;
    private DefaultTableModel tableModel;
    private User currentUser; 

    public StudentDashboard(User user) {
        this.currentUser = user; 

        frame = new JFrame("Student Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setBounds(20, 20, 400, 30);
        frame.add(welcomeLabel);

        JLabel infoLabel = new JLabel("Please select an exam from the list and click 'Start Exam'.");
        infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        infoLabel.setBounds(20, 60, 500, 25);
        frame.add(infoLabel);

        String[] columnNames = {"Exam Code", "Exam Name", "Duration (mins)"}; // Column naam badla
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        examTable = new JTable(tableModel);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 

        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setBounds(20, 100, 540, 280);
        frame.add(scrollPane);

        JButton startButton = new JButton("Start Exam");
        startButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        startButton.setBounds(220, 400, 150, 40);
        frame.add(startButton);

        startButton.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an exam to start.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Webcam Index poocchna
            List<com.github.sarxos.webcam.Webcam> webcams = com.github.sarxos.webcam.Webcam.getWebcams();
            if (webcams.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No webcam found. Cannot start exam.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            StringBuilder webcamListString = new StringBuilder("Available Webcams:\n\n");
            for (int i = 0; i < webcams.size(); i++) {
                webcamListString.append(i + ": " + webcams.get(i).getName() + "\n");
            }
            webcamListString.append("\nPlease enter the index (e.g., 0, 1) you want to use:");
            String input = JOptionPane.showInputDialog(frame, webcamListString.toString(), "Select Webcam", JOptionPane.QUESTION_MESSAGE);
            int webcamIndex = 0;
            if (input != null && !input.isEmpty()) {
                try {
                    webcamIndex = Integer.parseInt(input);
                    if (webcamIndex < 0 || webcamIndex >= webcams.size()) {
                        JOptionPane.showMessageDialog(frame, "Invalid index. Using default webcam (0).", "Warning", JOptionPane.WARNING_MESSAGE);
                        webcamIndex = 0;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number. Using default webcam (0).", "Warning", JOptionPane.WARNING_MESSAGE);
                    webcamIndex = 0;
                }
            } else {
                System.out.println("Webcam selection cancelled or empty. Using default (0).");
                webcamIndex = 0;
            }
            
            // Exam ki details nikaalna
            String examCode = (String) tableModel.getValueAt(selectedRow, 0); // Ab String hai
            String examName = (String) tableModel.getValueAt(selectedRow, 1);
            int duration = (int) tableModel.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to start the exam?\n\n" + 
                "Exam: " + examName + "\n" +
                "Using Webcam: " + webcams.get(webcamIndex).getName(),
                "Confirm Start", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                new ExamWindow(currentUser, examCode, duration, webcamIndex); // examCode (String) pass kiya
                frame.dispose(); 
            }
        });

        frame.setVisible(true);
        loadAvailableExams();
    }

    private void loadAvailableExams() {
        tableModel.setRowCount(0); 
        String sql = "SELECT exam_code, exam_name, duration_minutes FROM exams";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("exam_code")); // Ab String hai
                row.add(rs.getString("exam_name"));
                row.add(rs.getInt("duration_minutes"));
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}