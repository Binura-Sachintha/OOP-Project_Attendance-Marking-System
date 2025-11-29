package ui;

import model.Student;
import model.AttendanceRecord;
import repository.AttendanceRepository;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AttendanceMarkingDialog extends JDialog {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Student> students;
    private AttendanceRepository attendanceRepo;
    private String subject;
    private TeacherDashboardFrame parentFrame;

    public AttendanceMarkingDialog(TeacherDashboardFrame parent, List<Student> students, AttendanceRepository repo, String subject) {
        super(parent, "Mark Attendance - " + LocalDate.now(), true);
        this.parentFrame = parent;
        this.students = students;
        this.attendanceRepo = repo;
        this.subject = subject;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // Header
        JLabel headerLabel = new JLabel("Mark Attendance for " + LocalDate.now());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"ID", "Name", "Present"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // This tells Java to render the 3rd column (index 2) as a Checkbox
                if (columnIndex == 2) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the Checkbox column is editable
                return column == 2;
            }
        };

        // Load students
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), true});
        }

        table = new JTable(tableModel);
        table.setRowHeight(35); // Taller rows for better touch/click
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- COLORED SAVE BUTTON ---
        JButton saveBtn = new JButton("Save Attendance");
        
        // 1. Force Flat Style (Removes Nimbus default look)
        saveBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        // 2. Styling
        saveBtn.setBackground(new Color(39, 174, 96)); // Green Color
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setFocusPainted(false);

        // 3. Hover Effect
        saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveBtn.setBackground(new Color(33, 145, 80)); // Darker green on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveBtn.setBackground(new Color(39, 174, 96)); // Back to normal
            }
        });

        saveBtn.addActionListener(e -> saveAttendance());
        
        // Button Container with padding
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void saveAttendance() {
        int rowCount = tableModel.getRowCount();
        LocalDate today = LocalDate.now();
        int presentCount = 0;
        int absentCount = 0;

        for (int i = 0; i < rowCount; i++) {
            String studentId = (String) tableModel.getValueAt(i, 0);
            Boolean isPresent = (Boolean) tableModel.getValueAt(i, 2);

            // Create record
            AttendanceRecord record = new AttendanceRecord(studentId, subject, today, isPresent);
            attendanceRepo.addRecord(record);
            
            if(isPresent) presentCount++;
            else absentCount++;
        }

        JOptionPane.showMessageDialog(this, 
            "Attendance Saved!\nPresent: " + presentCount + "\nAbsent: " + absentCount, 
            "Success", JOptionPane.INFORMATION_MESSAGE);
        
        parentFrame.loadAttendancePercentageData(); // Refresh parent
        dispose(); // Close dialog
    }
}