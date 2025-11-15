import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewResults {

    JFrame frame;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ViewResults() {
        frame = new JFrame("View All Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("All Student Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Result ID", "Student Name", "Exam Code", "Exam Name", "Score", "Date Taken"};
        tableModel = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; 
             }
        };
        resultsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
        loadAllResults();
    }

    private void loadAllResults() {
        tableModel.setRowCount(0);
        
        // SQL query ko update kiya taaki exam_code bhi dikhe
        String sql = "SELECT r.result_id, u.full_name, e.exam_code, e.exam_name, r.score, r.date_taken " +
                     "FROM results r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "JOIN exams e ON r.exam_code = e.exam_code " + // Ab exam_code par join hoga
                     "ORDER BY r.date_taken DESC"; 

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("result_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("exam_code")); // Naya column
                row.add(rs.getString("exam_name"));
                row.add(rs.getInt("score"));
                row.add(rs.getTimestamp("date_taken").toString());
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading results.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}