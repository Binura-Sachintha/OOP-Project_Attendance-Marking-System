package repository;

import model.Student;
import java.sql.*;
import java.util.*;

public class StudentRepository {

    public List<Student> getAll(){
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("subject")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public Optional<Student> findById(String id) {
        String sql = "SELECT * FROM Students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("subject")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public void addStudent(Student s) {
        String sql = "INSERT INTO Students (id, name, subject) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, s.getId());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getSubject());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteStudent(String id) {
        String sql = "DELETE FROM Students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editStudent(String oldId, Student newStudent) {
        String sql = "UPDATE Students SET name=?, subject=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStudent.getName());
            stmt.setString(2, newStudent.getSubject());
            stmt.setString(3, oldId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}