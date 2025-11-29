package ui;

import service.AuthService;
import repository.TeacherRepository;
import repository.StudentRepository;
import repository.AttendanceRepository;
import model.Teacher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

// ----------------------------
// CUSTOM BACKGROUND PANEL
// ----------------------------
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.err.println("Background image not found: " + imagePath);
        }
        setLayout(new GridBagLayout()); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class LoginFrame extends JFrame {

    private AuthService auth;
    private TeacherRepository teacherRepo; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo;

    private JTextField ownerUserField;
    private JPasswordField ownerPassField;
    private JTextField teacherUserField;
    private JPasswordField teacherPassField;

    // Custom Tab Buttons
    private JButton btnOwnerTab;
    private JButton btnTeacherTab;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Colors
    private final Color ACTIVE_TAB_COLOR = new Color(41, 128, 185); // Blue
    private final Color INACTIVE_TAB_COLOR = new Color(240, 240, 240); // Light Grey
    private final Color ACTIVE_TEXT_COLOR = Color.WHITE;
    private final Color INACTIVE_TEXT_COLOR = Color.GRAY;

    public LoginFrame(TeacherRepository teacherRepo, StudentRepository studentRepo, AttendanceRepository attendanceRepo){
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo;

        this.auth = new AuthService(teacherRepo, studentRepo); 
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        init();
    }

    private void init() {
        setTitle("Attendance Management System");
        setSize(900, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Background
        BackgroundPanel bgPanel = new BackgroundPanel("/ui/images/login_bg.jpg");
        
        // 2. Main Login Box (FIXED GHOSTING ISSUE)
        JPanel loginBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Manually paint the semi-transparent background
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        // Important: Set Opaque to FALSE so it repaints the background image underneath first
        loginBox.setOpaque(false); 
        loginBox.setBackground(new Color(255, 255, 255, 230)); // Semi-transparent white
        
        loginBox.setBorder(new EmptyBorder(0, 0, 0, 0)); 
        loginBox.setPreferredSize(new Dimension(450, 500)); 

        // --- A. TOP TOGGLE TABS ---
        JPanel togglePanel = new JPanel(new GridLayout(1, 2));
        togglePanel.setPreferredSize(new Dimension(450, 50));
        togglePanel.setOpaque(false); // Ensure this is also transparent-safe
        
        btnOwnerTab = createTabButton("Owner Login", true);
        btnTeacherTab = createTabButton("Teacher Login", false);
        
        // Switch Logic
        btnOwnerTab.addActionListener(e -> {
            cardLayout.show(cardPanel, "OWNER");
            updateTabStyles(true);
        });
        
        btnTeacherTab.addActionListener(e -> {
            cardLayout.show(cardPanel, "TEACHER");
            updateTabStyles(false);
        });
        
        togglePanel.add(btnOwnerTab);
        togglePanel.add(btnTeacherTab);
        
        loginBox.add(togglePanel, BorderLayout.NORTH);

        // --- B. FORMS AREA (CardLayout) ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false); // Important
        cardPanel.setBorder(new EmptyBorder(20, 40, 30, 40)); 
        
        cardPanel.add(createFormPanel(true), "OWNER");
        cardPanel.add(createFormPanel(false), "TEACHER");
        
        loginBox.add(cardPanel, BorderLayout.CENTER);

        // Add login box to background
        bgPanel.add(loginBox);
        setContentPane(bgPanel);
    }
    
    // --- Helper to Create Tab Buttons ---
    private JButton createTabButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isActive) {
            btn.setBackground(ACTIVE_TAB_COLOR);
            btn.setForeground(ACTIVE_TEXT_COLOR);
        } else {
            btn.setBackground(INACTIVE_TAB_COLOR);
            btn.setForeground(INACTIVE_TEXT_COLOR);
        }
        return btn;
    }
    
    // --- Helper to Switch Styles ---
    private void updateTabStyles(boolean isOwnerActive) {
        if (isOwnerActive) {
            btnOwnerTab.setBackground(ACTIVE_TAB_COLOR);
            btnOwnerTab.setForeground(ACTIVE_TEXT_COLOR);
            btnTeacherTab.setBackground(INACTIVE_TAB_COLOR);
            btnTeacherTab.setForeground(INACTIVE_TEXT_COLOR);
        } else {
            btnOwnerTab.setBackground(INACTIVE_TAB_COLOR);
            btnOwnerTab.setForeground(INACTIVE_TEXT_COLOR);
            btnTeacherTab.setBackground(ACTIVE_TAB_COLOR);
            btnTeacherTab.setForeground(ACTIVE_TEXT_COLOR);
        }
    }

    private JPanel createFormPanel(boolean isOwner) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Header
        JLabel welcomeLabel = new JLabel(isOwner ? "Welcome Back, Owner" : "Welcome Back, Teacher");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(50, 50, 50));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.insets = new Insets(0, 0, 20, 0); 
        panel.add(welcomeLabel, gbc);

        // Inputs
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(labelFont);
        userLabel.setForeground(Color.DARK_GRAY);
        
        JTextField userField = new JTextField(15);
        userField.setFont(fieldFont);
        userField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)), 
                new EmptyBorder(10, 10, 10, 10))); 

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(labelFont);
        passLabel.setForeground(Color.DARK_GRAY);
        
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(fieldFont);
        passField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)), 
                new EmptyBorder(10, 10, 10, 10)));

        if (isOwner) {
            ownerUserField = userField;
            ownerPassField = passField;
        } else {
            teacherUserField = userField;
            teacherPassField = passField;
        }

        // Add Components
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 1; panel.add(userLabel, gbc);
        gbc.gridy = 2; panel.add(userField, gbc);
        gbc.gridy = 3; panel.add(passLabel, gbc);
        gbc.gridy = 4; panel.add(passField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login to System");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(30, 144, 255)); 
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginButton.setBackground(new Color(20, 120, 240)); }
            public void mouseExited(MouseEvent e) { loginButton.setBackground(new Color(30, 144, 255)); }
        });

        loginButton.addActionListener(e -> {
            if (isOwner) {
                if(auth.ownerLogin(ownerUserField.getText(), new String(ownerPassField.getPassword()))){
                    new OwnerDashboardFrame(teacherRepo, studentRepo, attendanceRepo).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,"Invalid owner credentials", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Optional<Teacher> ot = auth.teacherLogin(teacherUserField.getText(), new String(teacherPassField.getPassword()));
                if(ot.isPresent()){
                    new TeacherDashboardFrame(ot.get(), studentRepo, attendanceRepo).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,"Invalid teacher credentials", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridy = 5; 
        gbc.insets = new Insets(30, 0, 10, 0); 
        panel.add(loginButton, gbc);

        return panel;
    }
}