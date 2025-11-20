package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.text.DecimalFormat;

import model.Teacher;
import model.Student;
import model.AttendanceRecord;
import repository.StudentRepository;
import repository.AttendanceRepository; // ADD THIS

public class TeacherDashboardFrame extends JFrame {
    
    private Teacher teacher; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo; // ADD THIS
    
    private JTable classManagementTable;
    private DefaultTableModel classManagementTableModel;
    
    private JTable attendanceTable; // New table for attendance tracking
    private DefaultTableModel attendanceTableModel; // New model

    private static final DecimalFormat df = new DecimalFormat("0.00"); // For percentage formatting
    
    // CONSTRUCTOR: Now accepts StudentRepository AND AttendanceRepository
    public TeacherDashboardFrame(Teacher t, StudentRepository studentRepo, AttendanceRepository attendanceRepo){ // UPDATED
        this.teacher = t;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo; // ASSIGN
        
        setTitle("Teacher Dashboard - Class: " + t.getSubject());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Welcome, " + teacher.getUsername() + " (Class: " + teacher.getSubject() + ")");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 139, 34)); 
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Content
        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("SansSerif", Font.PLAIN, 16));

        mainTabs.addTab("Attendance", createAttendancePanel()); // UPDATED METHOD
        mainTabs.addTab("Class Management", createClassManagementPanel());
        mainTabs.addTab("Reports", createReportsPanel());

        add(mainTabs, BorderLayout.CENTER);
        
        // Footer/Logout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(255, 69, 0));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            // For a complete restart, you would pass all repos to a new LoginFrame
            new LoginFrame(null, null, null).setVisible(true); 
            dispose();
        });
        footerPanel.add(logoutBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    // --- Data Filtering and Calculation Logic ---
    
    private List<Student> getStudentsForClass() {
        String teacherSubject = teacher.getSubject();
        return studentRepo.getAll().stream()
                .filter(s -> s.getSubject().equalsIgnoreCase(teacherSubject))
                .collect(Collectors.toList());
    }
    
    public void loadClassManagementData() {
        if (classManagementTableModel == null) return;
        classManagementTableModel.setRowCount(0); 
        List<Student> classStudents = getStudentsForClass();
        for (Student s : classStudents) {
            classManagementTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getSubject()}); 
        }
    }
    
    // Loads student attendance percentage data
    public void loadAttendancePercentageData() {
        if (attendanceTableModel == null) return;
        
        attendanceTableModel.setRowCount(0); 
        
        List<Student> classStudents = getStudentsForClass();
        String subject = teacher.getSubject();
        
        for (Student s : classStudents) {
            double percentage = attendanceRepo.getAttendancePercentage(s.getId(), subject);
            
            attendanceTableModel.addRow(new Object[]{
                s.getId(), 
                s.getName(), 
                df.format(percentage) + "%" // Display percentage
            }); 
        }
    }

    // --- Panel Implementations ---
    
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Attendance Overview for " + teacher.getSubject() + " Class", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // --- Table Setup for Percentage ---
        String[] columns = {"ID", "Student Name", "Attendance Percentage"};
        attendanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        attendanceTable = new JTable(attendanceTableModel);
        attendanceTable.setRowHeight(25);
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        // --- End Table Setup ---
        
        // --- Button Panel for Marking Attendance ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JButton refreshBtn = new JButton("Refresh Percentage Data");
        refreshBtn.addActionListener(e -> loadAttendancePercentageData());
        
        JButton markAttendanceBtn = new JButton("Mark Attendance for TODAY (" + LocalDate.now() + ")");
        markAttendanceBtn.setBackground(new Color(60, 179, 113));
        markAttendanceBtn.setForeground(Color.WHITE);
        
        markAttendanceBtn.addActionListener(e -> markAttendance());
        
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonGroup.add(markAttendanceBtn);
        buttonGroup.add(refreshBtn);
        
        bottomPanel.add(buttonGroup, BorderLayout.CENTER);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        loadAttendancePercentageData(); // Initial load
        
        return panel;
    }
    
    // Method to handle the marking attendance process (Simplified for this step)
    private void markAttendance() {
        String subject = teacher.getSubject();
        LocalDate today = LocalDate.now();
        
        // 1. Check if attendance is already marked for today (Prevents double marking)
        boolean alreadyMarked = attendanceRepo.getAllRecords().stream()
            .anyMatch(r -> r.getSubject().equalsIgnoreCase(subject) && r.getDate().equals(today));
            
        if (alreadyMarked) {
            JOptionPane.showMessageDialog(this, "Attendance has already been marked for " + subject + " class today (" + today + ").", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Student> students = getStudentsForClass();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found in the " + subject + " class.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Mark all " + students.size() + " students in " + subject + " class as PRESENT for today (" + today + ")?", 
            "Confirm Attendance Marking", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
             // For simplicity, we mark all matching students as PRESENT.
             int presentCount = 0;
             for (Student s : students) {
                 AttendanceRecord record = new AttendanceRecord(s.getId(), subject, today, true); // true = Present
                 attendanceRepo.addRecord(record);
                 presentCount++;
             }
             
             JOptionPane.showMessageDialog(this, 
                 "Attendance marked successfully! " + presentCount + " students marked Present.", 
                 "Success", JOptionPane.INFORMATION_MESSAGE);
             
             loadAttendancePercentageData(); // Refresh the percentage data
             
        }
    }

    // --- Class Management Panel (Only name changes to match model names) ---
    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Students in " + teacher.getSubject() + " Class", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columns = {"ID", "Name", "Subject/Class"};
        classManagementTableModel = new DefaultTableModel(columns, 0) { 
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        classManagementTable = new JTable(classManagementTableModel); 
        classManagementTable.setRowHeight(25);
        panel.add(new JScrollPane(classManagementTable), BorderLayout.CENTER);

        // Buttons 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton refreshBtn = new JButton("Refresh List"); 
        JButton editInfoBtn = new JButton("Edit Student Info");
        
        // --- FUNCTIONALITY: Refresh Button ---
        refreshBtn.addActionListener(e -> loadClassManagementData());
        
        // --- FUNCTIONALITY: EDIT Button ---
        editInfoBtn.addActionListener(e -> {
            int selectedRow = classManagementTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) classManagementTable.getValueAt(selectedRow, 0);
                String name = (String) classManagementTable.getValueAt(selectedRow, 1);
                String subject = (String) classManagementTable.getValueAt(selectedRow, 2);
                
                Student studentToEdit = new Student(id, name, subject);
                
                // Using the generalized StudentFormDialog
                StudentFormDialog dialog = new StudentFormDialog(this, studentRepo, studentToEdit); 
                dialog.setVisible(true);
                
                loadClassManagementData(); 
                loadAttendancePercentageData(); // Refresh attendance data after potential subject change
                
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnPanel.add(refreshBtn);
        btnPanel.add(editInfoBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadClassManagementData(); 

        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel instruction = new JLabel("Report Generation", SwingConstants.CENTER);
        instruction.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(instruction, BorderLayout.NORTH);
        
        JComboBox<String> periodSelector = new JComboBox<>(new String[]{"Weekly", "Monthly", "Term"});
        JButton generateBtn = new JButton("Generate Report");
        
        generateBtn.addActionListener(e -> {
            String period = (String) periodSelector.getSelectedItem();
            JOptionPane.showMessageDialog(this, "Generating " + period + " Report for " + teacher.getSubject() + " class.");
        });
        
        JPanel controls = new JPanel(new FlowLayout());
        controls.add(new JLabel("Report Period:"));
        controls.add(periodSelector);
        controls.add(generateBtn);
        
        panel.add(controls, BorderLayout.CENTER);
        
        return panel;
    }
}