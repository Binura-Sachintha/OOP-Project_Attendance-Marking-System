package service;

import repository.TeacherRepository;
import repository.StudentRepository;
import model.Teacher;
import java.util.*;
import java.io.*;

public class AuthService {
    
    private TeacherRepository teacherRepo;
    private StudentRepository studentRepo;
    
    // File to store owner credentials persistently
    private static final String OWNER_FILE = "owner_config.ser"; 
    
    public AuthService(TeacherRepository tr, StudentRepository sr){
        this.teacherRepo = tr;
        this.studentRepo = sr;
    }
    
    // UPDATED: Owner Login Logic with File Support
    public boolean ownerLogin(String u, String p){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OWNER_FILE))) {
            // Read credentials from file if it exists
            String[] creds = (String[]) ois.readObject();
            return creds[0].equals(u) && creds[1].equals(p);
        } catch (Exception e) {
            // Fallback to default credentials if file does not exist
            return u.equals("owner") && p.equals("123");
        }
    }
    
    // NEW: Method to update owner credentials from Settings
    public void updateOwnerCredentials(String newUsername, String newPassword) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OWNER_FILE))) {
            oos.writeObject(new String[]{newUsername, newPassword});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Teacher Login Logic (Unchanged)
    public Optional<Teacher> teacherLogin(String u,String p){
        Optional<Teacher> t = teacherRepo.find(u);
        if(t.isPresent() && t.get().getPassword().equals(p)) return t;
        return Optional.empty();
    }
}