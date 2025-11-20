package repository;

import model.AttendanceRecord;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AttendanceRepository {
    private List<AttendanceRecord> records = new ArrayList<>();
    private static final String FILE_PATH = "attendance.ser"; 

    public AttendanceRepository() {
        loadFromFile();
        if (records.isEmpty()) {
            // Dummy data for initial testing
            records.add(new AttendanceRecord("S001", "Science", LocalDate.now().minusDays(3), true));
            records.add(new AttendanceRecord("S001", "Science", LocalDate.now().minusDays(2), true));
            records.add(new AttendanceRecord("S001", "Science", LocalDate.now().minusDays(1), false)); 
            saveToFile();
        }
    }
    
    // Adds a new attendance record and saves to file
    public void addRecord(AttendanceRecord record) {
        // Prevent duplicate records for the same student on the same day
        boolean exists = records.stream().anyMatch(r -> 
            r.getStudentId().equals(record.getStudentId()) && 
            r.getDate().equals(record.getDate()) &&
            r.getSubject().equals(record.getSubject())
        );
        if (!exists) {
            records.add(record);
            saveToFile();
        }
    }

    public List<AttendanceRecord> getAllRecords() {
        return records;
    }
    
    // Calculates the attendance percentage for a specific student in a specific subject
    public double getAttendancePercentage(String studentId, String subject) {
        List<AttendanceRecord> studentRecords = records.stream()
                .filter(r -> r.getStudentId().equals(studentId) && r.getSubject().equalsIgnoreCase(subject))
                .collect(Collectors.toList());

        if (studentRecords.isEmpty()) return 0.0;

        long totalClasses = studentRecords.size();
        long classesPresent = studentRecords.stream().filter(AttendanceRecord::isPresent).count();

        return (double) classesPresent / totalClasses * 100.0;
    }
    
    // --- Persistence Implementation ---

    @SuppressWarnings("unchecked") 
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            records = (List<AttendanceRecord>) ois.readObject();
            System.out.println("Attendance data loaded from " + FILE_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("Attendance data file not found. Starting fresh.");
            records = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error loading attendance data. Starting fresh.");
            records = new ArrayList<>(); 
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(records);
            System.out.println("Attendance data saved to " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving attendance data.");
        }
    }
}