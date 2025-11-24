package ui;

import model.Teacher;
import repository.TeacherRepository;
import javax.swing.*;
import java.awt.*;

public class TeacherFormDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField subjectField;
    private TeacherRepository repo;
    private Teacher teacherToEdit; // null means Add, set means Edit
    private OwnerDashboardFrame parentFrame; // To refresh data

    // Constructor for Add/Edit
    public TeacherFormDialog(OwnerDashboardFrame parent, TeacherRepository repo, Teacher teacherToEdit) {
        super(parent, teacherToEdit == null ? "Add New Teacher" : "Edit Teacher", true);
        this.parentFrame = parent;
        this.repo = repo;
        this.teacherToEdit = teacherToEdit;
        
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Components ---
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        subjectField = new JTextField(15);
        JButton saveButton = new JButton("Save");

        // Layout: Username, Password, Subject
        addRowToPanel(formPanel, gbc, 0, new JLabel("Username:"), usernameField);
        addRowToPanel(formPanel, gbc, 1, new JLabel("Password:"), passwordField);
        addRowToPanel(formPanel, gbc, 2, new JLabel("Subject:"), subjectField);
        
        // Populate fields if in Edit mode
        if (teacherToEdit != null) {
            usernameField.setText(teacherToEdit.getUsername());
            usernameField.setEnabled(false); // Disables editing the Username
            passwordField.setText(teacherToEdit.getPassword());
            subjectField.setText(teacherToEdit.getSubject());
        }

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton.addActionListener(e -> saveTeacher());
        btnPanel.add(saveButton);
        btnPanel.add(new JButton("Cancel")); 

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addRowToPanel(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; panel.add(label, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST; panel.add(component, gbc);
    }
    
    private void saveTeacher() {
        String u = usernameField.getText();
        String p = new String(passwordField.getPassword());
        String s = subjectField.getText();
        
        if (u.isEmpty() || p.isEmpty() || s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Teacher newTeacher = new Teacher(u, p, s);

        if (teacherToEdit == null) {
            // Add mode
            if (repo.find(u).isPresent()) {
                 JOptionPane.showMessageDialog(this, "This Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            repo.addTeacher(newTeacher);
            JOptionPane.showMessageDialog(this, "Teacher " + u + " added successfully!");
        } else {
            // Edit mode
            repo.editTeacher(teacherToEdit.getUsername(), newTeacher);
            JOptionPane.showMessageDialog(this, "Teacher " + u + " updated successfully!");
        }
        
        parentFrame.loadTeacherData(); // Refresh data in dashboard
        dispose();
    }
}