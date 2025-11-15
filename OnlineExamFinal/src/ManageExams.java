import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageExams {

    JFrame frame;
    private JTable examTable;
    private DefaultTableModel tableModel;

    public ManageExams() {
        frame = new JFrame("Manage Exams");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null); 

        JLabel titleLabel = new JLabel("Manage Exams");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setBounds(220, 20, 200, 30);
        frame.add(titleLabel);

        JPanel addPanel = new JPanel();
        addPanel.setLayout(null);
        addPanel.setBounds(20, 70, 540, 120); // Panel ko thoda bada kiya
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Exam"));
        frame.add(addPanel);

        // --- NAYA FIELD ---
        JLabel codeLabel = new JLabel("Exam Code:");
        codeLabel.setBounds(20, 30, 80, 25);
        addPanel.add(codeLabel);

        JTextField codeText = new JTextField();
        codeText.setBounds(110, 30, 100, 25);
        addPanel.add(codeText);
        // --- END ---

        JLabel nameLabel = new JLabel("Exam Name:");
        nameLabel.setBounds(230, 30, 80, 25);
        addPanel.add(nameLabel);

        JTextField nameText = new JTextField();
        nameText.setBounds(310, 30, 210, 25);
        addPanel.add(nameText);

        JLabel durationLabel = new JLabel("Duration (mins):");
        durationLabel.setBounds(20, 70, 100, 25);
        addPanel.add(durationLabel);

        JTextField durationText = new JTextField();
        durationText.setBounds(130, 70, 50, 25);
        addPanel.add(durationText);

        JButton addButton = new JButton("Add Exam");
        addButton.setBounds(220, 70, 100, 25);
        addPanel.add(addButton);

        String[] columnNames = {"Exam Code", "Exam Name", "Duration (mins)"}; // Column naam badla
        tableModel = new DefaultTableModel(columnNames, 0);
        examTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setBounds(20, 210, 540, 180); // Jagah update ki
        frame.add(scrollPane);

        JButton deleteButton = new JButton("Delete Selected Exam");
        deleteButton.setBounds(210, 410, 180, 25);
        frame.add(deleteButton);
        
        addButton.addActionListener(e -> {
            String examCode = codeText.getText().trim(); // Naya code read kiya
            String examName = nameText.getText().trim();
            String durationStr = durationText.getText().trim();

            if (examCode.isEmpty() || examName.isEmpty() || durationStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int duration = Integer.parseInt(durationStr);
                addExamToDB(examCode, examName, duration);
                loadExams(); // Table ko refresh karo
                codeText.setText("");
                nameText.setText("");
                durationText.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Duration must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an exam to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String examCode = (String) tableModel.getValueAt(selectedRow, 0); // Ab int nahi, String hai
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this exam?\nThis will also delete all related questions and results.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                deleteExamFromDB(examCode);
                loadExams(); // Table ko refresh karo
            }
        });

        frame.setVisible(true);
        loadExams();
    }

    private void loadExams() {
        tableModel.setRowCount(0);
        String sql = "SELECT exam_code, exam_name, duration_minutes FROM exams";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("exam_code")); // ID ki jagah code
                row.add(rs.getString("exam_name"));
                row.add(rs.getInt("duration_minutes"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addExamToDB(String code, String name, int duration) {
        String sql = "INSERT INTO exams (exam_code, exam_name, duration_minutes) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.setInt(3, duration);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Exam added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding exam. (Is the code unique?)", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExamFromDB(String examCode) { // Ab int nahi, String
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); 

            String sqlResults = "DELETE FROM results WHERE exam_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlResults)) {
                pstmt.setString(1, examCode);
                pstmt.executeUpdate();
            }

            String sqlQuestions = "DELETE FROM questions WHERE exam_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlQuestions)) {
                pstmt.setString(1, examCode);
                pstmt.executeUpdate();
            }

            String sqlExam = "DELETE FROM exams WHERE exam_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlExam)) {
                pstmt.setString(1, examCode);
                pstmt.executeUpdate();
            }
            
            conn.commit(); 
            JOptionPane.showMessageDialog(frame, "Exam deleted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting exam.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}