package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import repository.TeacherRepository;
import repository.StudentRepository;
import repository.AttendanceRepository;
import service.AuthService;
import model.Teacher;
import model.Student;

public class OwnerDashboardFrame extends JFrame {
    
    // --- MODERN COLORS ---
    private static final Color HEADER_BG = new Color(41, 128, 185); // Modern Blue
    private static final Color SIDEBAR_BG = new Color(44, 62, 80); // Dark Blue-Grey
    private static final Color MAIN_BG = new Color(236, 240, 241); // Clean Grey
    
    // Button Colors
    private static final Color BTN_GREEN = new Color(39, 174, 96);
    private static final Color BTN_BLUE = new Color(41, 128, 185);
    private static final Color BTN_RED = new Color(231, 76, 60);

    private TeacherRepository teacherRepo; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo; 
    
    private JPanel sideMenuPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private JTable teacherTable; 
    private JTable studentTable; 
    private DefaultTableModel teacherTableModel; 
    private DefaultTableModel studentTableModel; 

    public OwnerDashboardFrame(TeacherRepository teacherRepo, StudentRepository studentRepo, AttendanceRepository attendanceRepo){ 
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo; 
        this.attendanceRepo = attendanceRepo;
        
        setTitle("Owner Dashboard - System Administration");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout()); 

        // 1. TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Hamburger Button
        JButton menuBtn = new JButton("\u2630"); 
        styleIconButton(menuBtn, HEADER_BG); // Style as Icon
        
        menuBtn.addActionListener(e -> {
            boolean visible = sideMenuPanel.isVisible();
            sideMenuPanel.setVisible(!visible);
        });

        JLabel titleLabel = new JLabel("  System Administration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(menuBtn);
        leftHeader.add(titleLabel);
        
        // --- MODERN LOGOUT BUTTON ---
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, BTN_RED); // Flat Red Button
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        
        logoutBtn.addActionListener(e -> {
            new LoginFrame(teacherRepo, studentRepo, attendanceRepo).setVisible(true);
            dispose();
        });

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // 2. SIDE MENU
        sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.setBackground(SIDEBAR_BG); 
        sideMenuPanel.setPreferredSize(new Dimension(240, getHeight()));
        sideMenuPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        addMenuButton("Teacher Management", "TEACHERS");
        addMenuButton("Student Management", "STUDENTS");
        addMenuButton("System Settings", "SETTINGS");
        
        add(sideMenuPanel, BorderLayout.WEST);

        // 3. MAIN CONTENT
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(MAIN_BG);
        
        contentPanel.add(createTeacherManagementPanel(), "TEACHERS");
        contentPanel.add(createStudentManagementPanel(), "STUDENTS");
        contentPanel.add(createSettingsPanel(), "SETTINGS");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    // --- Helper for Side Menu Buttons ---
    private void addMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Force Flat
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94)); // Lighter on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sideMenuPanel.add(btn);
    }
    
    // --- Teacher Panel ---
    public void loadTeacherData() {
        if (teacherTableModel == null) return;
        teacherTableModel.setRowCount(0); 
        for (Teacher t : teacherRepo.getAllTeachers()) {
            teacherTableModel.addRow(new Object[]{t.getUsername(), t.getPassword(), t.getSubject()}); 
        }
    }
    
    private JPanel createTeacherManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(MAIN_BG);

        JLabel header = new JLabel("Manage Teacher Accounts");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(SIDEBAR_BG);
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Username", "Password", "Subject"};
        teacherTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        teacherTable = new JTable(teacherTableModel);
        setupTable(teacherTable); // Apply table styling
        
        panel.add(new JScrollPane(teacherTable), BorderLayout.CENTER);

        // --- BUTTONS ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(MAIN_BG);
        
        JButton addBtn = new JButton("Add New Teacher");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        // Apply Modern Styling
        styleButton(addBtn, BTN_GREEN);
        styleButton(editBtn, BTN_BLUE);
        styleButton(deleteBtn, BTN_RED);

        addBtn.addActionListener(e -> new TeacherFormDialog(this, teacherRepo, null).setVisible(true));
        
        editBtn.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                String u = (String) teacherTable.getValueAt(selectedRow, 0);
                String p = (String) teacherTable.getValueAt(selectedRow, 1);
                String s = (String) teacherTable.getValueAt(selectedRow, 2);
                new TeacherFormDialog(this, teacherRepo, new Teacher(u, p, s)).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Select a teacher to edit.");
            }
        });
        
        deleteBtn.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                String username = (String) teacherTable.getValueAt(selectedRow, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete " + username + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    teacherRepo.deleteTeacher(username);
                    loadTeacherData(); 
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a teacher to delete.");
            }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadTeacherData(); 
        return panel;
    }

    // --- Student Panel ---
    public void loadStudentData() {
        if (studentTableModel == null) return;
        studentTableModel.setRowCount(0); 
        for (Student s : studentRepo.getAll()) {
            studentTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getSubject()}); 
        }
    }

    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(MAIN_BG);

        JLabel header = new JLabel("Manage Student Records");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(SIDEBAR_BG);
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Subject/Class"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        setupTable(studentTable);
        
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(MAIN_BG);
        
        JButton addBtn = new JButton("Add New Student");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        styleButton(addBtn, BTN_GREEN);
        styleButton(editBtn, BTN_BLUE);
        styleButton(deleteBtn, BTN_RED);
        
        addBtn.addActionListener(e -> {
            new StudentFormDialog(this, studentRepo, null).setVisible(true); 
            loadStudentData(); 
        });
        
        editBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) studentTable.getValueAt(selectedRow, 0);
                String name = (String) studentTable.getValueAt(selectedRow, 1);
                String subject = (String) studentTable.getValueAt(selectedRow, 2);
                new StudentFormDialog(this, studentRepo, new Student(id, name, subject)).setVisible(true);
                loadStudentData(); 
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to edit.");
            }
        });
        
        deleteBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) studentTable.getValueAt(selectedRow, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete Student " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    studentRepo.deleteStudent(id);
                    loadStudentData(); 
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to delete.");
            }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadStudentData(); 
        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        panel.setBackground(MAIN_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel headerLabel = new JLabel("Update Owner Credentials");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(SIDEBAR_BG);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(headerLabel, gbc);
        
        gbc.gridy = 1; panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

        JLabel userLabel = new JLabel("New Username:"); userLabel.setFont(labelFont);
        JTextField userField = new JTextField(20); userField.setFont(fieldFont);
        
        JLabel passLabel = new JLabel("New Password:"); passLabel.setFont(labelFont);
        JPasswordField passField = new JPasswordField(20); passField.setFont(fieldFont);
        
        JLabel confirmLabel = new JLabel("Confirm Password:"); confirmLabel.setFont(labelFont);
        JPasswordField confirmField = new JPasswordField(20); confirmField.setFont(fieldFont);
        
        gbc.gridy = 2; gbc.gridx = 0; panel.add(userLabel, gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        
        gbc.gridy = 3; gbc.gridx = 0; panel.add(passLabel, gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        
        gbc.gridy = 4; gbc.gridx = 0; panel.add(confirmLabel, gbc);
        gbc.gridx = 1; panel.add(confirmField, gbc);
        
        JButton updateBtn = new JButton("Update Credentials");
        styleButton(updateBtn, BTN_BLUE);
        updateBtn.setPreferredSize(new Dimension(200, 45));
        
        updateBtn.addActionListener(e -> {
            String newUser = userField.getText().trim();
            String newPass = new String(passField.getPassword());
            if (newUser.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empty fields!", "Error", JOptionPane.ERROR_MESSAGE); return;
            }
            AuthService auth = new AuthService(teacherRepo, studentRepo);
            auth.updateOwnerCredentials(newUser, newPass);
            JOptionPane.showMessageDialog(this, "Credentials updated! Please login again.");
            userField.setText(""); passField.setText(""); confirmField.setText("");
        });
        
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 10, 10, 10);
        panel.add(updateBtn, gbc);
        
        return panel;
    }

    // --- STYLE UTILITIES ---
    
    // THIS IS THE KEY METHOD FOR FLAT MODERN BUTTONS
    private void styleButton(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // 1. Force Basic UI to remove Nimbus effects
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Flat padding
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add manual hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
    }

    private void styleIconButton(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 24));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
    }
}