package repository;

import model.Teacher;
import java.util.*;
import java.io.*; // <--- ADD THIS IMPORT

public class TeacherRepository {
    private List<Teacher> teachers = new ArrayList<>();
    // Define the file name where data will be saved
    private static final String FILE_PATH = "teachers.ser"; 

    public TeacherRepository() {
        loadFromFile(); // 1. Try to load existing data on startup

        if (teachers.isEmpty()) {
            // 2. If loading fails or file is empty, load dummy data
            teachers.add(new Teacher("tom", "123", "Science"));
            teachers.add(new Teacher("alice", "456", "Math"));
            saveToFile(); // Save this initial data
        }
    }
    
    public Optional<Teacher> find(String u){
        return teachers.stream().filter(t->t.getUsername().equals(u)).findFirst();
    }
    
    public List<Teacher> getAllTeachers() {
        return teachers;
    }
    
    public void addTeacher(Teacher t) {
        teachers.add(t);
        saveToFile(); // <--- PERSISTENCE: Save immediately after adding
    }
    
    public void deleteTeacher(String username) {
        teachers.removeIf(t -> t.getUsername().equals(username));
        saveToFile(); // <--- PERSISTENCE: Save immediately after deleting
    }

    public void editTeacher(String oldUsername, Teacher newTeacherData) {
        for (int i = 0; i < teachers.size(); i++) {
            if (teachers.get(i).getUsername().equals(oldUsername)) {
                teachers.set(i, newTeacherData);
                saveToFile(); // <--- PERSISTENCE: Save immediately after editing
                return;
            }
        }
    }

    // --- Persistence Implementation using Serialization ---

    @SuppressWarnings("unchecked") 
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            // Read the entire list object from the file
            teachers = (List<Teacher>) ois.readObject();
            System.out.println("Teachers data loaded from " + FILE_PATH);
        } catch (FileNotFoundException e) {
            // This is expected the first time the app runs
            System.out.println("Teacher data file not found. Starting with default data.");
            teachers = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            // Catch other loading errors
            e.printStackTrace();
            System.err.println("Error loading teachers data. Starting fresh.");
            teachers = new ArrayList<>(); 
        }
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            // Write the entire list object to the file
            oos.writeObject(teachers);
            System.out.println("Teachers data saved to " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving teachers data.");
        }
    }
}