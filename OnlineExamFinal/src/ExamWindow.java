import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class ExamWindow implements WindowFocusListener {

    private JFrame frame;
    private User currentUser;
    private String examCode; 
    
    private ArrayList<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    
    private JLabel timerLabel;
    private JTextArea questionArea;
    private JRadioButton optA, optB, optC, optD;
    private ButtonGroup optionsGroup;

    private Timer swingTimer;
    private int timeRemaining; 
    private ProctorWindow proctorWindow; 

    public ExamWindow(User user, String examCode, int durationMinutes, int webcamIndex) {
        this.currentUser = user;
        this.examCode = examCode; 
        this.timeRemaining = durationMinutes * 60;

        frame = new JFrame("Online Exam");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel qLabel = new JLabel("Question " + (currentQuestionIndex + 1));
        qLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        topPanel.add(qLabel, BorderLayout.WEST);
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        topPanel.add(timerLabel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        questionArea = new JTextArea("Question text goes here...");
        questionArea.setFont(new Font("Tahoma", Font.PLAIN, 18));
        questionArea.setWrapStyleWord(true);
        questionArea.setLineWrap(true);
        questionArea.setEditable(false);
        questionArea.setMargin(new Insets(10, 10, 10, 10));
        centerPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        optA = new JRadioButton("Option A");
        optB = new JRadioButton("Option B");
        optC = new JRadioButton("Option C");
        optD = new JRadioButton("Option D");
        Font optionFont = new Font("Tahoma", Font.PLAIN, 16);
        optA.setFont(optionFont);
        optB.setFont(optionFont);
        optC.setFont(optionFont);
        optD.setFont(optionFont);
        optionsGroup = new ButtonGroup();
        optionsGroup.add(optA);
        optionsGroup.add(optB);
        optionsGroup.add(optC);
        optionsGroup.add(optD);
        optionsPanel.add(optA);
        optionsPanel.add(optB);
        optionsPanel.add(optC);
        optionsPanel.add(optD);
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton submitButton = new JButton("Submit Exam");
        submitButton.setBackground(Color.GREEN);
        bottomPanel.add(prevButton, BorderLayout.WEST);
        bottomPanel.add(nextButton, BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        ActionListener saveAnswerAction = e -> saveSelectedAnswer();
        optA.addActionListener(saveAnswerAction);
        optB.addActionListener(saveAnswerAction);
        optC.addActionListener(saveAnswerAction);
        optD.addActionListener(saveAnswerAction);

        nextButton.addActionListener(e -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
                qLabel.setText("Question " + (currentQuestionIndex + 1));
            }
        });
        prevButton.addActionListener(e -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
                qLabel.setText("Question " + (currentQuestionIndex + 1));
            }
        });
        
        // --- BADLAAV 1: Submit button ab submitExam(false) call karega ---
        submitButton.addActionListener(e -> submitExam(false)); // false matlab 'normal submit'

        frame.setVisible(true);
        frame.addWindowFocusListener(this);
        proctorWindow = new ProctorWindow(webcamIndex); 
        
        loadQuestions(); 
        displayQuestion(); 
        startTimer(); 
    }
    
    private void loadQuestions() {
        String sql = "SELECT * FROM questions WHERE exam_code = ? ORDER BY RAND()"; 
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, examCode); 
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("question_id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_answer")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion() {
        if (questions.isEmpty()) {
            questionArea.setText("No questions found for this exam.");
            return;
        }
        Question q = questions.get(currentQuestionIndex);
        questionArea.setText(q.getQuestionText());
        optA.setText(q.getOptionA());
        optB.setText(q.getOptionB());
        optC.setText(q.getOptionC());
        optD.setText(q.getOptionD());
        optionsGroup.clearSelection();
        String savedAnswer = q.getSelectedAnswer();
        if (savedAnswer.equals("A")) optA.setSelected(true);
        else if (savedAnswer.equals("B")) optB.setSelected(true);
        else if (savedAnswer.equals("C")) optC.setSelected(true);
        else if (savedAnswer.equals("D")) optD.setSelected(true);
    }

    private void saveSelectedAnswer() {
        Question q = questions.get(currentQuestionIndex);
        if (optA.isSelected()) q.setSelectedAnswer("A");
        else if (optB.isSelected()) q.setSelectedAnswer("B");
        else if (optC.isSelected()) q.setSelectedAnswer("C");
        else if (optD.isSelected()) q.setSelectedAnswer("D");
    }

    private void startTimer() {
        swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));

                if (timeRemaining <= 0) {
                    swingTimer.stop();
                    JOptionPane.showMessageDialog(frame, "Time is up! Auto-submitting.", "Time Up", JOptionPane.WARNING_MESSAGE);
                    // --- BADLAAV 2: Time up ab submitExam(false) call karega ---
                    submitExam(false); // false matlab 'normal submit'
                }
            }
        });
        swingTimer.start();
    }

    // --- BADLAAV 3: submitExam function ab 'isCheating' parameter lega ---
    private void submitExam(boolean isCheating) {
        
        // Check karo ki window abhi bhi dikh rahi hai (taaki yeh function 2 baar na chale)
        if (!frame.isDisplayable()) {
            return;
        }
        
        frame.removeWindowFocusListener(this);
        
        if (swingTimer.isRunning()) {
            swingTimer.stop();
        }
        
        if (proctorWindow != null) {
            proctorWindow.closeWindow();
        }

        int score = 0;
        String resultMessage;

        if (isCheating) {
            // Agar cheating ki hai, toh score 0 kar do
            score = 0;
            resultMessage = "CHEATING DETECTED!\n" +
                            "You moved away from the exam window.\n" +
                            "Your exam has been submitted with a score of 0.";
        } else {
            // Normal score calculation
            for (Question q : questions) {
                if (q.getSelectedAnswer().equals(q.getCorrectAnswer())) {
                    score++;
                }
            }
            resultMessage = "Exam Submitted!\n\n" +
                            "Your Score: " + score + " out of " + questions.size();
        }
        
        // Score ko database mein save karo
        String sql = "INSERT INTO results (user_id, exam_code, score) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getUserId());
            pstmt.setString(2, examCode); 
            pstmt.setInt(3, score); // Score (0 ya calculated) save hoga
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Exam window band karke result dikhao
        frame.dispose();
        JOptionPane.showMessageDialog(null, 
            resultMessage,
            "Result", 
            isCheating ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE); // Cheating par Error popup
            
        new StudentDashboard(currentUser);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        // Isse chhedne ki zaroorat nahi
        System.out.println("Window focus GAINED.");
    }

    // --- BADLAAV 4: windowLostFocus ab submitExam(true) call karega ---
    @Override
    public void windowLostFocus(WindowEvent e) {
        System.out.println("Window focus LOST. Potential cheating! Submitting exam.");
        
        // Koi warning mat dikhao, seedha exam submit kar do
        // true ka matlab hai 'cheating submission'
        submitExam(true);
    }
}