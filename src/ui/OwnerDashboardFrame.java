package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import repository.TeacherRepository;
import repository.StudentRepository;
import repository.AttendanceRepository; // 1. ADDED: Required for logout
import model.Teacher;
import model.Student;

public class OwnerDashboardFrame extends JFrame {
    
    private TeacherRepository teacherRepo; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo; // 2. ADDED FIELD
    private JTable teacherTable; 
    private JTable studentTable; 
    private DefaultTableModel teacherTableModel; 
    private DefaultTableModel studentTableModel; 

    // 3. UPDATED CONSTRUCTOR: Accepts ALL three repositories
    public OwnerDashboardFrame(TeacherRepository teacherRepo, StudentRepository studentRepo, AttendanceRepository attendanceRepo){ 
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo; 
        this.attendanceRepo = attendanceRepo; // Assign the new repository
        
        setTitle("Owner Dashboard - System Administration");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(10, 10)); 
        
        // 1. Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("System Administration Panel");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 144, 255));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Content Area
        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("SansSerif", Font.PLAIN, 16));

        mainTabs.addTab("Teacher Management", createTeacherManagementPanel());
        mainTabs.addTab("Student Management", createStudentManagementPanel()); 
        mainTabs.addTab("System Settings", createSettingsPanel());

        add(mainTabs, BorderLayout.CENTER);
        
        // 3. Footer/Status Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(255, 69, 0));
        logoutBtn.setForeground(Color.WHITE);
        
        // 4. LOGOUT FIX: Pass ALL THREE repositories back to LoginFrame
        logoutBtn.addActionListener(e -> {
            new LoginFrame(teacherRepo, studentRepo, attendanceRepo).setVisible(true); // FIX IS HERE
            dispose();
        });
        
        footerPanel.add(new JLabel("Status: Active | "));
        footerPanel.add(logoutBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    // --- Teacher Management Methods ---
    public void loadTeacherData() {
        if (teacherTableModel == null) return;
        teacherTableModel.setRowCount(0); 
        for (Teacher t : teacherRepo.getAllTeachers()) {
            teacherTableModel.addRow(new Object[]{t.getUsername(), t.getPassword(), t.getSubject()}); 
        }
    }
    
    private JPanel createTeacherManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
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
        teacherTable.setRowHeight(25);
        panel.add(new JScrollPane(teacherTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addBtn = new JButton("Add New Teacher");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        // ADD Button
        addBtn.addActionListener(e -> {
            // Note: TeacherFormDialog may need the AttendanceRepo if it needs to update Main/Login,
            // but for now, we assume it only needs TeacherRepo.
            new TeacherFormDialog(this, teacherRepo, null).setVisible(true); 
        });
        
        // EDIT Button
        editBtn.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                String u = (String) teacherTable.getValueAt(selectedRow, 0);
                String p = (String) teacherTable.getValueAt(selectedRow, 1);
                String s = (String) teacherTable.getValueAt(selectedRow, 2);
                Teacher teacherToEdit = new Teacher(u, p, s);
                new TeacherFormDialog(this, teacherRepo, teacherToEdit).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a teacher to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // DELETE Button
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
            } else {
                JOptionPane.showMessageDialog(this, "Please select a teacher to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadTeacherData(); 
        return panel;
    }

    // --- Student Management Methods ---
    public void loadStudentData() {
        if (studentTableModel == null) return;
        
        studentTableModel.setRowCount(0); 
        
        for (Student s : studentRepo.getAll()) {
            studentTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getSubject()}); 
        }
    }

    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Manage Student Records", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columns = {"ID", "Name", "Subject/Class"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(25);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        // --- End Table Setup ---

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addBtn = new JButton("Add New Student");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        // ADD Button
        addBtn.addActionListener(e -> {
            // StudentFormDialog is now generalized to accept a JFrame parent ('this')
            new StudentFormDialog(this, studentRepo, null).setVisible(true); 
            loadStudentData(); // Refresh data after dialog close
        });
        
        // EDIT Button
        editBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) studentTable.getValueAt(selectedRow, 0);
                String name = (String) studentTable.getValueAt(selectedRow, 1);
                String subject = (String) studentTable.getValueAt(selectedRow, 2);
                
                Student studentToEdit = new Student(id, name, subject);
                
                new StudentFormDialog(this, studentRepo, studentToEdit).setVisible(true);
                loadStudentData(); // Refresh data after dialog close
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // DELETE Button
        deleteBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = (String) studentTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    studentRepo.deleteStudent(id);
                    JOptionPane.showMessageDialog(this, "Student ID " + id + " deleted successfully.");
                    loadStudentData(); // Refresh table
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadStudentData(); 

        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("System Settings Area", SwingConstants.CENTER));
        return panel;
    }
}