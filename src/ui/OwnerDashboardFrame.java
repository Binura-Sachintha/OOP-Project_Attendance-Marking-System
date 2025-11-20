package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import repository.TeacherRepository;
import repository.StudentRepository;
import repository.AttendanceRepository;
import model.Teacher;
import model.Student;

public class OwnerDashboardFrame extends JFrame {
    
    // Repositories
    private TeacherRepository teacherRepo; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo; 

    // UI Components for Table Data
    private JTable teacherTable; 
    private JTable studentTable; 
    private DefaultTableModel teacherTableModel; 
    private DefaultTableModel studentTableModel; 

    // UI Components for Navigation (NEW)
    private JPanel sideMenuPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout; // To switch views easily
    private JButton menuToggleBtn;

    // Card identifiers
    private static final String TEACHER_CARD = "TeacherManagement";
    private static final String STUDENT_CARD = "StudentManagement";
    private static final String SETTINGS_CARD = "SystemSettings";


    // Constructor accepts all three repositories
    public OwnerDashboardFrame(TeacherRepository teacherRepo, StudentRepository studentRepo, AttendanceRepository attendanceRepo){ 
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo; 
        this.attendanceRepo = attendanceRepo;
        
        setTitle("Owner Dashboard - System Administration");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        
        // Load data initially
        loadTeacherData(); 
        loadStudentData();
    }
    
    private void initUI() {
        // Use BorderLayout for Header (NORTH), Sidebar (WEST), and Content (CENTER)
        setLayout(new BorderLayout()); 
        
        // 1. Header Panel (NORTH)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 140, 220)); 
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        
        // Hamburger Menu Button (Left side of Header)
        menuToggleBtn = new JButton("☰ Menu");
        menuToggleBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuToggleBtn.setForeground(Color.WHITE);
        menuToggleBtn.setBackground(new Color(60, 140, 220));
        menuToggleBtn.setBorderPainted(false);
        menuToggleBtn.setFocusPainted(false);
        menuToggleBtn.addActionListener(e -> handleSidebarToggle());
        
        JPanel menuWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        menuWrapper.setBackground(new Color(60, 140, 220));
        menuWrapper.add(menuToggleBtn);
        headerPanel.add(menuWrapper, BorderLayout.WEST);

        // Title Label (Center of Header)
        JLabel titleLabel = new JLabel("System Administration Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // 2. Sidebar Panel (WEST)
        sideMenuPanel = createSidebar();
        add(sideMenuPanel, BorderLayout.WEST);

        // 3. Main Content Area (CENTER)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);

        // Add initial content cards
        mainContentPanel.add(createTeacherManagementPanel(), TEACHER_CARD);
        mainContentPanel.add(createStudentManagementPanel(), STUDENT_CARD);
        mainContentPanel.add(createSettingsPanel(), SETTINGS_CARD);
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Show Teacher Management by default
        cardLayout.show(mainContentPanel, TEACHER_CARD);


        // 4. Footer Panel (SOUTH) - Remains the same
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footerPanel.setBackground(new Color(45, 60,  80));
        footerPanel.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel statusLabel = new JLabel("System Status: Active");
        statusLabel.setForeground(new Color(102, 255, 102));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(255, 69, 0)); 
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        logoutBtn.addActionListener(e -> {
            new LoginFrame(teacherRepo, studentRepo, attendanceRepo).setVisible(true); 
            dispose();
        });
        
        footerPanel.add(statusLabel);
        footerPanel.add(logoutBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    // --- NEW: Method to create the Sidebar ---
    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 70, 90)); // Dark sidebar background
        panel.setPreferredSize(new Dimension(220, getHeight()));
        
        addSidebarButton(panel, "👨‍🏫 Teacher Management", TEACHER_CARD);
        addSidebarButton(panel, "🎓 Student Management", STUDENT_CARD);
        
        panel.add(Box.createVerticalStrut(20)); // Separator space
        
        addSidebarButton(panel, "⚙️ System Settings", SETTINGS_CARD);
        
        return panel;
    }
    
    // --- NEW: Helper method to create and add menu buttons ---
    private void addSidebarButton(JPanel parent, String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(50, 70, 90));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding
        
        button.addActionListener(e -> {
            cardLayout.show(mainContentPanel, cardName); // Switch view
            // Optional: You could hide the sidebar after selection here
        });
        
        parent.add(button);
        parent.add(Box.createVerticalStrut(5)); // Small spacing between buttons
    }

    // --- NEW: Method to handle sidebar visibility toggle ---
    private void handleSidebarToggle() {
        // Toggle the visibility and revalidate the layout
        sideMenuPanel.setVisible(!sideMenuPanel.isVisible());
        revalidate(); 
        repaint();
    }
    
    // --- Data Loading Methods (Keep as is) ---
    public void loadTeacherData() {
        if (teacherTableModel == null) return;
        teacherTableModel.setRowCount(0); 
        for (Teacher t : teacherRepo.getAllTeachers()) {
            teacherTableModel.addRow(new Object[]{t.getUsername(), t.getPassword(), t.getSubject()}); 
        }
    }
    
    public void loadStudentData() {
        if (studentTableModel == null) return;
        studentTableModel.setRowCount(0); 
        for (Student s : studentRepo.getAll()) {
            studentTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getSubject()}); 
        }
    }

    // --- Content Panel Creation Methods (Keep internal structure) ---
    // Note: These methods are now added as 'cards' to the mainContentPanel.
    private JPanel createTeacherManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        // ... (Keep the existing implementation for Teacher Management table and buttons)
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Manage Teacher Accounts", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Username", "Password", "Subject"};
        teacherTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        teacherTable = new JTable(teacherTableModel);
        teacherTable.setRowHeight(28);
        
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton addBtn = new JButton("Add New Teacher");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        // Set button styles
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        editBtn.setBackground(new Color(255, 165, 0));
        editBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(220, 20, 60));
        deleteBtn.setForeground(Color.WHITE);
        
        // Action Listeners 
        addBtn.addActionListener(e -> { new TeacherFormDialog(this, teacherRepo, null).setVisible(true); });
        editBtn.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                String u = (String) teacherTable.getValueAt(selectedRow, 0);
                String p = (String) teacherTable.getValueAt(selectedRow, 1);
                String s = (String) teacherTable.getValueAt(selectedRow, 2);
                Teacher teacherToEdit = new Teacher(u, p, s);
                new TeacherFormDialog(this, teacherRepo, teacherToEdit).setVisible(true);
            } else { JOptionPane.showMessageDialog(this, "Please select a teacher to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE); }
        });
        deleteBtn.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                String username = (String) teacherTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete teacher: " + username + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    teacherRepo.deleteTeacher(username);
                    JOptionPane.showMessageDialog(this, "Teacher " + username + " deleted successfully.");
                    loadTeacherData(); 
                }
            } else { JOptionPane.showMessageDialog(this, "Please select a teacher to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE); }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        // ... (Keep the existing implementation for Student Management table and buttons)
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Manage Student Records", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Subject/Class"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(28);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton addBtn = new JButton("Add New Student");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        // Set button styles
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        editBtn.setBackground(new Color(255, 165, 0));
        editBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(220, 20, 60));
        deleteBtn.setForeground(Color.WHITE);
        
        // Action Listeners 
        addBtn.addActionListener(e -> { new StudentFormDialog(this, studentRepo, null).setVisible(true); loadStudentData(); });
        editBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) studentTable.getValueAt(selectedRow, 0);
                String name = (String) studentTable.getValueAt(selectedRow, 1);
                String subject = (String) studentTable.getValueAt(selectedRow, 2);
                
                Student studentToEdit = new Student(id, name, subject);
                new StudentFormDialog(this, studentRepo, studentToEdit).setVisible(true);
                loadStudentData(); 
            } else { JOptionPane.showMessageDialog(this, "Please select a student to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE); }
        });
        deleteBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) studentTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    studentRepo.deleteStudent(id);
                    JOptionPane.showMessageDialog(this, "Student ID " + id + " deleted successfully.");
                    loadStudentData(); 
                }
            } else { JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE); }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("System Settings Area - Configurations and Backups go here.", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(label, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        return panel;
    }
}