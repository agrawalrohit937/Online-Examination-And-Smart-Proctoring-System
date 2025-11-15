import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.*;

public class ManageQuestions {

    JFrame frame;
    JComboBox<ExamItem> examComboBox; 
    Vector<ExamItem> examList = new Vector<>(); 

    public ManageQuestions() {
        frame = new JFrame("Manage Questions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("Manage Questions");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setBounds(190, 20, 300, 30);
        frame.add(titleLabel);

        JPanel selectPanel = new JPanel(null);
        selectPanel.setBounds(20, 70, 540, 80);
        selectPanel.setBorder(BorderFactory.createTitledBorder("Select Exam"));
        frame.add(selectPanel);

        JLabel examLabel = new JLabel("Select Exam:");
        examLabel.setBounds(20, 30, 100, 25);
        selectPanel.add(examLabel);

        examComboBox = new JComboBox<>();
        examComboBox.setBounds(120, 30, 400, 25);
        selectPanel.add(examComboBox);

        JPanel addPanel = new JPanel(null);
        addPanel.setBounds(20, 170, 540, 380);
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Question"));
        frame.add(addPanel);

        JLabel qLabel = new JLabel("Question:");
        qLabel.setBounds(20, 30, 80, 25);
        addPanel.add(qLabel);

        JTextArea qText = new JTextArea();
        qText.setLineWrap(true);
        qText.setWrapStyleWord(true);
        JScrollPane qScrollPane = new JScrollPane(qText);
        qScrollPane.setBounds(100, 30, 420, 60);
        addPanel.add(qScrollPane);

        JLabel optALabel = new JLabel("Option A:");
        optALabel.setBounds(20, 100, 80, 25);
        addPanel.add(optALabel);
        JTextField optAText = new JTextField();
        optAText.setBounds(100, 100, 420, 25);
        addPanel.add(optAText);

        JLabel optBLabel = new JLabel("Option B:");
        optBLabel.setBounds(20, 140, 80, 25);
        addPanel.add(optBLabel);
        JTextField optBText = new JTextField();
        optBText.setBounds(100, 140, 420, 25);
        addPanel.add(optBText);

        JLabel optCLabel = new JLabel("Option C:");
        optCLabel.setBounds(20, 180, 80, 25);
        addPanel.add(optCLabel);
        JTextField optCText = new JTextField();
        optCText.setBounds(100, 180, 420, 25);
        addPanel.add(optCText);

        JLabel optDLabel = new JLabel("Option D:");
        optDLabel.setBounds(20, 220, 80, 25);
        addPanel.add(optDLabel);
        JTextField optDText = new JTextField();
        optDText.setBounds(100, 220, 420, 25);
        addPanel.add(optDText);

        JLabel correctLabel = new JLabel("Correct Answer:");
        correctLabel.setBounds(20, 270, 120, 25);
        addPanel.add(correctLabel);

        String[] answers = {"A", "B", "C", "D"};
        JComboBox<String> correctComboBox = new JComboBox<>(answers);
        correctComboBox.setBounds(140, 270, 100, 25);
        addPanel.add(correctComboBox);

        JButton addButton = new JButton("Add Question");
        addButton.setBounds(220, 320, 150, 30);
        addPanel.add(addButton);

        addButton.addActionListener(e -> {
            ExamItem selectedExam = (ExamItem) examComboBox.getSelectedItem();
            if (selectedExam == null) {
                JOptionPane.showMessageDialog(frame, "Please select an exam first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String examCode = selectedExam.getCode(); // Ab Code nikaalenge, ID nahi

            String question = qText.getText().trim();
            String optA = optAText.getText().trim();
            String optB = optBText.getText().trim();
            String optC = optCText.getText().trim();
            String optD = optDText.getText().trim();
            String correctAns = (String) correctComboBox.getSelectedItem();

            if (question.isEmpty() || optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            addQuestionToDB(examCode, question, optA, optB, optC, optD, correctAns);

            qText.setText("");
            optAText.setText("");
            optBText.setText("");
            optCText.setText("");
            optDText.setText("");
        });

        frame.setVisible(true);
        loadExamsIntoComboBox();
    }

    private void loadExamsIntoComboBox() {
        String sql = "SELECT exam_code, exam_name FROM exams";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            examList.clear(); 
            examComboBox.removeAllItems(); 

            while (rs.next()) {
                String code = rs.getString("exam_code");
                String name = rs.getString("exam_name");
                ExamItem item = new ExamItem(code, name);
                
                examList.add(item);
                examComboBox.addItem(item); 
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addQuestionToDB(String examCode, String q, String a, String b, String c, String d, String correct) {
        String sql = "INSERT INTO questions (exam_code, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, examCode);
            pstmt.setString(2, q);
            pstmt.setString(3, a);
            pstmt.setString(4, b);
            pstmt.setString(5, c);
            pstmt.setString(6, d);
            pstmt.setString(7, correct);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(frame, "Question added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding question.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class ab int id ki jagah String code legi
    private class ExamItem {
        private String code;
        private String name;

        public ExamItem(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return name + " (Code: " + code + ")"; // Dropdown mein naam aur code dono dikhega
        }
    }
}