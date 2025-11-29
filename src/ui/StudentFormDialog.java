package ui;

import model.Student;
import repository.StudentRepository;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class StudentFormDialog extends JDialog {
    private JTextField idField;
    private JTextField nameField;
    private JTextField subjectField;
    private StudentRepository repo;
    private Student studentToEdit; // null means Add, set means Edit
    private JFrame parentFrame; // <--- GENERALIZED to JFrame

    // Constructor for Add/Edit - NOW ACCEPTS JFrame
    public StudentFormDialog(JFrame parent, StudentRepository repo, Student studentToEdit) {
        super(parent, studentToEdit == null ? "Add New Student" : "Edit Student", true);
        this.parentFrame = parent;
        this.repo = repo;
        this.studentToEdit = studentToEdit;
        
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
        idField = new JTextField(15);
        nameField = new JTextField(15);
        subjectField = new JTextField(15);
        JButton saveButton = new JButton("Save");

        // Layout: ID, Name, Subject
        addRowToPanel(formPanel, gbc, 0, new JLabel("Student ID:"), idField);
        addRowToPanel(formPanel, gbc, 1, new JLabel("Name:"), nameField);
        addRowToPanel(formPanel, gbc, 2, new JLabel("Subject/Class:"), subjectField);
        
        // Populate fields if in Edit mode
        if (studentToEdit != null) {
            idField.setText(studentToEdit.getId());
            idField.setEnabled(false); // ID should not be editable
            nameField.setText(studentToEdit.getName());
            subjectField.setText(studentToEdit.getSubject());
        }

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton.addActionListener(e -> saveStudent());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        btnPanel.add(saveButton);
        btnPanel.add(cancelButton); 

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addRowToPanel(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; panel.add(label, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST; panel.add(component, gbc);
    }
    
    private void saveStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String subject = subjectField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty() || subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student newStudent = new Student(id, name, subject);

        if (studentToEdit == null) {
            // Add mode
            if (repo.findById(id).isPresent()) {
                 JOptionPane.showMessageDialog(this, "This Student ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            repo.addStudent(newStudent);
            JOptionPane.showMessageDialog(this, "Student " + name + " added successfully!");
        } else {
            // Edit mode (ID is not changed)
            repo.editStudent(studentToEdit.getId(), newStudent);
            JOptionPane.showMessageDialog(this, "Student " + name + " updated successfully!");
        }
        
        // **IMPORTANT:** We no longer call parentFrame.loadStudentData() here.
        // The calling frame (Dashboard) is responsible for refreshing its own data *after* the dialog closes.
        dispose();
    }
}