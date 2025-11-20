package repository;

import model.Student;
import java.util.*;
import java.io.*; // ADD THIS IMPORT

public class StudentRepository {
    private List<Student> students = new ArrayList<>();
    private static final String FILE_PATH = "students.ser"; 

    public StudentRepository() {
        loadFromFile(); // Load existing data on startup

        if (students.isEmpty()) {
            // If loading fails or file is empty, load dummy data
            students.add(new Student("S001", "John Perera", "Science"));
            students.add(new Student("S002", "Kamal Silva", "Math"));
            students.add(new Student("S003", "Nimal Fernando", "Science"));
            saveToFile(); // Save this initial data
        }
    }
    
    public List<Student> getAll(){
        return students;
    }
    
    // Check if a student ID exists (useful for Add operation)
    public Optional<Student> findById(String id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst();
    }
    
    // --- CRUD Methods ---
    
    public void addStudent(Student s) {
        students.add(s);
        saveToFile(); // Save immediately after adding
    }
    
    public void deleteStudent(String id) {
        students.removeIf(s -> s.getId().equals(id));
        saveToFile(); // Save immediately after deleting
    }

    public void editStudent(String oldId, Student newStudentData) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(oldId)) {
                students.set(i, newStudentData);
                saveToFile(); // Save immediately after editing
                return;
            }
        }
    }
    
    // --- Persistence Implementation ---

    @SuppressWarnings("unchecked") 
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            students = (List<Student>) ois.readObject();
            System.out.println("Students data loaded from " + FILE_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("Student data file not found. Starting with default data.");
            students = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error loading student data. Starting fresh.");
            students = new ArrayList<>(); 
        }
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(students);
            System.out.println("Students data saved to " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving students data.");
        }
    }
}