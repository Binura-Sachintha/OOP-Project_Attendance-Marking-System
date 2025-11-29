package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // 1. Imported for Sorting/Filtering
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

import model.Teacher;
import model.Student;
import model.AttendanceRecord;
import repository.StudentRepository;
import repository.AttendanceRepository;

public class TeacherDashboardFrame extends JFrame {
    
    // --- COLORS ---
    private static final Color HEADER_BG = new Color(39, 174, 96); 
    private static final Color SIDEBAR_BG = new Color(44, 62, 80); 
    private static final Color MAIN_BG = new Color(236, 240, 241); 
    
    private static final Color BTN_BLUE = new Color(52, 152, 219);
    private static final Color BTN_GREEN = new Color(39, 174, 96);
    private static final Color BTN_ORANGE = new Color(243, 156, 18);
    private static final Color BTN_RED = new Color(231, 76, 60);

    private Teacher teacher; 
    private StudentRepository studentRepo; 
    private AttendanceRepository attendanceRepo; 
    
    private JPanel sideMenuPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JTable classManagementTable;
    private DefaultTableModel classManagementTableModel;
    private JTable attendanceTable; 
    private DefaultTableModel attendanceTableModel; 
    
    // 2. Added Sorter variable to handle filtering
    private TableRowSorter<DefaultTableModel> studentSorter;

    private static final DecimalFormat df = new DecimalFormat("0.00"); 
    
    public TeacherDashboardFrame(Teacher t, StudentRepository studentRepo, AttendanceRepository attendanceRepo){ 
        this.teacher = t;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo;
        
        setTitle("Teacher Dashboard - Class: " + t.getSubject());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        // 1. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton menuBtn = new JButton("\u2630"); 
        styleIconButton(menuBtn, HEADER_BG);
        
        menuBtn.addActionListener(e -> {
            boolean visible = sideMenuPanel.isVisible();
            sideMenuPanel.setVisible(!visible);
        });

        JLabel titleLabel = new JLabel("  Teacher Dashboard (" + teacher.getSubject() + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(menuBtn);
        leftHeader.add(titleLabel);
        
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, BTN_RED);
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        
        logoutBtn.addActionListener(e -> {
            new LoginFrame(null, null, null).setVisible(true); 
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
        
        addMenuButton("Attendance", "ATTENDANCE");
        addMenuButton("Class Management", "CLASS");
        addMenuButton("Reports", "REPORTS");
        
        add(sideMenuPanel, BorderLayout.WEST);
        
        // 3. CONTENT
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(MAIN_BG);
        
        contentPanel.add(createAttendancePanel(), "ATTENDANCE");
        contentPanel.add(createClassManagementPanel(), "CLASS");
        contentPanel.add(createReportsPanel(), "REPORTS");

        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void addMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sideMenuPanel.add(btn);
    }
    
    private void styleButton(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI()); 
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    
    // --- Panels --- 
    
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(MAIN_BG);

        JLabel header = new JLabel("Attendance Overview");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(SIDEBAR_BG);
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Student Name", "Attendance Percentage"};
        attendanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        attendanceTable = new JTable(attendanceTableModel);
        setupTable(attendanceTable);
        
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(MAIN_BG);
        
        JButton refreshBtn = new JButton("Refresh Data");
        JButton markAttendanceBtn = new JButton("Mark Attendance for TODAY");
        
        styleButton(refreshBtn, BTN_BLUE);
        styleButton(markAttendanceBtn, BTN_GREEN);
        
        refreshBtn.addActionListener(e -> loadAttendancePercentageData());
        markAttendanceBtn.addActionListener(e -> markAttendance());
        
        bottomPanel.add(markAttendanceBtn);
        bottomPanel.add(refreshBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        loadAttendancePercentageData(); 
        return panel;
    }

    private void markAttendance() {
        String subject = teacher.getSubject();
        LocalDate today = LocalDate.now();
        boolean alreadyMarked = attendanceRepo.getAllRecords().stream()
            .anyMatch(r -> r.getSubject().equalsIgnoreCase(subject) && r.getDate().equals(today));
            
        if (alreadyMarked) {
            JOptionPane.showMessageDialog(this, "Attendance has already been marked for today.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Student> students = getStudentsForClass();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found in class.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new AttendanceMarkingDialog(this, students, attendanceRepo, subject).setVisible(true);
    }

    // --- UPDATED: Class Management Panel with Search ---
    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(MAIN_BG);

        // 1. Top Container for Header and Search
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setBackground(MAIN_BG);
        
        JLabel header = new JLabel("Class Management", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(SIDEBAR_BG);
        topContainer.add(header, BorderLayout.NORTH);

        // 2. Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(MAIN_BG);
        
        JLabel searchLabel = new JLabel("Search Student: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(SIDEBAR_BG);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        topContainer.add(searchPanel, BorderLayout.SOUTH);
        panel.add(topContainer, BorderLayout.NORTH);

        // 3. Table Setup with Sorting
        String[] columns = {"ID", "Name", "Subject/Class"};
        classManagementTableModel = new DefaultTableModel(columns, 0) { 
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        classManagementTable = new JTable(classManagementTableModel); 
        setupTable(classManagementTable);
        
        // ** ADDED: Table Sorter **
        studentSorter = new TableRowSorter<>(classManagementTableModel);
        classManagementTable.setRowSorter(studentSorter);
        
        // ** ADDED: Search Logic **
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    studentSorter.setRowFilter(null);
                } else {
                    // Case-insensitive regex filter
                    studentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        panel.add(new JScrollPane(classManagementTable), BorderLayout.CENTER);

        // 4. Buttons 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBackground(MAIN_BG);
        
        JButton refreshBtn = new JButton("Refresh List"); 
        JButton editInfoBtn = new JButton("Edit Student Info");
        
        styleButton(refreshBtn, BTN_BLUE);
        styleButton(editInfoBtn, BTN_ORANGE);
        
        refreshBtn.addActionListener(e -> {
            loadClassManagementData();
            searchField.setText(""); // Clear search on refresh
        });
        
        editInfoBtn.addActionListener(e -> {
            int selectedRow = classManagementTable.getSelectedRow();
            if (selectedRow != -1) {
                // Convert view index to model index (important when sorting/filtering!)
                int modelRow = classManagementTable.convertRowIndexToModel(selectedRow);
                
                String id = (String) classManagementTableModel.getValueAt(modelRow, 0);
                String name = (String) classManagementTableModel.getValueAt(modelRow, 1);
                String subject = (String) classManagementTableModel.getValueAt(modelRow, 2);
                new StudentFormDialog(this, studentRepo, new Student(id, name, subject)).setVisible(true);
                loadClassManagementData(); 
                loadAttendancePercentageData(); 
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            }
        });
        
        btnPanel.add(refreshBtn);
        btnPanel.add(editInfoBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadClassManagementData(); 
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(MAIN_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel header = new JLabel("Generate Attendance Reports");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(SIDEBAR_BG);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(header, gbc);
        
        JLabel descLabel = new JLabel("Download a summary of student attendance for your records.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(descLabel, gbc);
        
        JButton generateBtn = new JButton("Download Full Report (.txt)");
        styleButton(generateBtn, BTN_BLUE);
        generateBtn.setPreferredSize(new Dimension(250, 50));
        
        generateBtn.addActionListener(e -> generateReport());
        
        gbc.gridy = 2; gbc.insets = new Insets(40, 10, 10, 10);
        panel.add(generateBtn, gbc);
        
        return panel;
    }
    
    private void generateReport() {
        List<Student> students = getStudentsForClass();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to report on.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report As");
        String defaultFileName = "Attendance_Report_" + teacher.getSubject() + "_" + LocalDate.now() + ".txt";
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }

            final File finalFile = fileToSave;
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            new Thread(() -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(finalFile))) {
                    
                    writer.write("====================================================================="); writer.newLine();
                    writer.write("                  ATTENDANCE REPORT"); writer.newLine();
                    writer.write("====================================================================="); writer.newLine();
                    writer.write("Subject: " + teacher.getSubject()); writer.newLine();
                    writer.write("Teacher: " + teacher.getUsername()); writer.newLine();
                    writer.write("Date Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); writer.newLine();
                    writer.write("====================================================================="); writer.newLine(); writer.newLine();
                    
                    writer.write(String.format("%-10s %-25s %-15s", "ID", "Student Name", "Percentage")); writer.newLine();
                    writer.write("----------------------------------------------------------"); writer.newLine();
                    
                    for (Student s : students) {
                        double percentage = attendanceRepo.getAttendancePercentage(s.getId(), teacher.getSubject());
                        writer.write(String.format("%-10s %-25s %-15s", 
                            s.getId(), 
                            s.getName(), 
                            df.format(percentage) + "%"
                        ));
                        writer.newLine();
                    }
                    
                    writer.newLine();
                    writer.write("=====================================================================");
                    writer.write("\nEnd of Report");
                    
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor()); 
                        JOptionPane.showMessageDialog(TeacherDashboardFrame.this, 
                            "Report saved successfully to:\n" + finalFile.getAbsolutePath(), 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    });

                } catch (IOException ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(TeacherDashboardFrame.this, 
                            "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start(); 
        }
    }

    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
    }
    
    // --- Data Loaders ---
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
    
    public void loadAttendancePercentageData() {
        if (attendanceTableModel == null) return;
        attendanceTableModel.setRowCount(0); 
        List<Student> classStudents = getStudentsForClass();
        String subject = teacher.getSubject();
        for (Student s : classStudents) {
            double percentage = attendanceRepo.getAttendancePercentage(s.getId(), subject);
            attendanceTableModel.addRow(new Object[]{s.getId(), s.getName(), df.format(percentage) + "%"}); 
        }
    }
}