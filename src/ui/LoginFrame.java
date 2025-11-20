package ui;

import service.AuthService;
import repository.TeacherRepository;
import repository.StudentRepository;
import repository.AttendanceRepository;
import model.Teacher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private AuthService auth;
    private TeacherRepository teacherRepo; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo; 

    private JTextField ownerUserField;
    private JPasswordField ownerPassField;
    private JTextField teacherUserField;
    private JPasswordField teacherPassField;

    // Constructor MUST accept three repositories
    public LoginFrame(TeacherRepository teacherRepo, StudentRepository studentRepo, AttendanceRepository attendanceRepo){ 
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo;
        
        this.auth = new AuthService(teacherRepo, studentRepo); 
        
        setNimbusLookAndFeel(); // Re-included here for guaranteed load
        init();
    }

    // Re-integrated the L&F setting method
    private void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: If Nimbus fails, the default system L&F will be used.
        }
    }

    private void init() {
        setTitle("Attendance Management System - Login");
        setSize(700, 500); // Slightly larger for the "fuller" look
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        // --- 1. Main Background Panel (The dark/colorful background) ---
        JPanel mainPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout to center content
        mainPanel.setBackground(new Color(45, 60, 80)); // Deep dark blue/gray background
        
        // --- 2. Floating Login Card (The white, centered element) ---
        JPanel loginCard = new JPanel(new BorderLayout());
        loginCard.setPreferredSize(new Dimension(550, 400)); // Fixed size for the central card
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Light border

        // 2a. Header inside the card
        JPanel cardHeader = new JPanel();
        cardHeader.setBackground(new Color(60, 140, 220)); 
        cardHeader.setPreferredSize(new Dimension(550, 60));
        JLabel titleLabel = new JLabel("Attendance System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        cardHeader.add(titleLabel);
        loginCard.add(cardHeader, BorderLayout.NORTH);
        
        // 2b. Tabbed Content
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14)); 
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10)); // Inner padding
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Owner Login", createLoginTabPanel(true));
        tabs.addTab("Teacher Login", createLoginTabPanel(false));
        
        loginCard.add(tabs, BorderLayout.CENTER);
        
        // Add the centered card to the main background panel
        mainPanel.add(loginCard);
        
        setContentPane(mainPanel); 
    }

    private JPanel createLoginTabPanel(boolean isOwner) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE); // Ensure tab content is white
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel usernameLabel = new JLabel(isOwner ? "Owner Username:" : "Teacher Username:");
        JLabel passwordLabel = new JLabel(isOwner ? "Owner Password:" : "Teacher Password:");
        
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField currentUserField = new JTextField(20);
        JPasswordField currentPassField = new JPasswordField(20);
        
        currentUserField.setPreferredSize(new Dimension(200, 30));
        currentPassField.setPreferredSize(new Dimension(200, 30));
        
        if (isOwner) {
            ownerUserField = currentUserField;
            ownerPassField = currentPassField;
        } else {
            teacherUserField = currentUserField;
            teacherPassField = currentPassField;
        }
        
        // Layout
        addRowToPanel(panel, gbc, 0, usernameLabel, currentUserField);
        addRowToPanel(panel, gbc, 1, passwordLabel, currentPassField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 180, 70)); 
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false); 
        loginButton.setPreferredSize(new Dimension(150, 40)); 
        
        // Action Listener (Logic remains the same)
        loginButton.addActionListener(e -> {
            if (isOwner) {
                if(auth.ownerLogin(ownerUserField.getText(), new String(ownerPassField.getPassword()))){
                    new OwnerDashboardFrame(teacherRepo, studentRepo, attendanceRepo).setVisible(true); 
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,"Invalid owner login", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Optional<Teacher> ot = auth.teacherLogin(teacherUserField.getText(), new String(teacherPassField.getPassword()));
                if(ot.isPresent()){
                    new TeacherDashboardFrame(ot.get(), studentRepo, attendanceRepo).setVisible(true); 
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,"Invalid teacher login", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10); 
        panel.add(loginButton, gbc);

        return panel;
    }
    
    // Helper method for clean GridBagLayout setup
    private void addRowToPanel(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; 
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST; 
        panel.add(component, gbc);
    }
}