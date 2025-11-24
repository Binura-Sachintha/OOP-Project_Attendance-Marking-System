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

// ----------------------------
// CUSTOM BACKGROUND PANEL
// ----------------------------
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
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

    public LoginFrame(TeacherRepository teacherRepo, StudentRepository studentRepo, AttendanceRepository attendanceRepo){
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo;

        this.auth = new AuthService(teacherRepo, studentRepo); 
        
        setNimbusLookAndFeel(); 
        init();
    }

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
        }
    }

    private void init() {
        setTitle("Attendance Management System - Login");
        setSize(550, 400); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(60, 140, 220));
        JLabel titleLabel = new JLabel("Attendance System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));

        // Background only inside login panels
        tabs.addTab("Owner Login", createLoginTabPanel(true));
        tabs.addTab("Teacher Login", createLoginTabPanel(false));

        contentPane.add(tabs, BorderLayout.CENTER);
        add(contentPane);
    }

    private JPanel createLoginTabPanel(boolean isOwner) {

        // ------------------------------
        // Replace normal JPanel with background panel
        // ------------------------------
        BackgroundPanel panel = new BackgroundPanel("/ui/images/login_bg.jpg");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel(isOwner ? "Owner Username:" : "Teacher Username:");
        JLabel passwordLabel = new JLabel(isOwner ? "Owner Password:" : "Teacher Password:");

        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16)); 

        JTextField currentUserField = new JTextField(20);
        JPasswordField currentPassField = new JPasswordField(20);

        if (isOwner) {
            ownerUserField = currentUserField;
            ownerPassField = currentPassField;
        } else {
            teacherUserField = currentUserField;
            teacherPassField = currentPassField;
        }

        addRowToPanel(panel, gbc, 0, usernameLabel, currentUserField);
        addRowToPanel(panel, gbc, 1, passwordLabel, currentPassField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 180, 70));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(150, 40));

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

    private void addRowToPanel(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(component, gbc);
    }
}
