package repository;

import model.Teacher;
import java.sql.*;
import java.util.*;

public class TeacherRepository {

    public TeacherRepository() {
        // No setup needed, directly connects to DB
    }
    
    // Find teacher by username (for Login)
    public Optional<Teacher> find(String username){
        String sql = "SELECT * FROM Teachers WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Teacher t = new Teacher(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("subject")
                );
                return Optional.of(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM Teachers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Teacher(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("subject")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void addTeacher(Teacher t) {
        String sql = "INSERT INTO Teachers (username, password, subject) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, t.getUsername());
            stmt.setString(2, t.getPassword());
            stmt.setString(3, t.getSubject());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteTeacher(String username) {
        String sql = "DELETE FROM Teachers WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editTeacher(String oldUsername, Teacher newTeacherData) {
        String sql = "UPDATE Teachers SET password=?, subject=? WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newTeacherData.getPassword());
            stmt.setString(2, newTeacherData.getSubject());
            stmt.setString(3, oldUsername);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}