package repository;

import model.AttendanceRecord;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class AttendanceRepository {

    public void addRecord(AttendanceRecord record) {
        if (recordExists(record.getStudentId(), record.getSubject(), record.getDate())) {
            return;
        }

        String sql = "INSERT INTO Attendance (student_id, subject, date, is_present) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, record.getStudentId());
            stmt.setString(2, record.getSubject());
            stmt.setDate(3, java.sql.Date.valueOf(record.getDate()));
            stmt.setBoolean(4, record.isPresent());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean recordExists(String studentId, String subject, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Attendance WHERE student_id=? AND subject=? AND date=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, subject);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AttendanceRecord> getAllRecords() {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Attendance";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new AttendanceRecord(
                    rs.getString("student_id"),
                    rs.getString("subject"),
                    rs.getDate("date").toLocalDate(),
                    rs.getBoolean("is_present")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // --- NEW METHOD: Get Records by Date Range ---
    public List<AttendanceRecord> getRecordsByDateRange(String subject, LocalDate fromDate, LocalDate toDate) {
        List<AttendanceRecord> list = new ArrayList<>();
        // SQL Query to filter by date
        String sql = "SELECT * FROM Attendance WHERE subject = ? AND date >= ? AND date <= ? ORDER BY date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, subject);
            stmt.setDate(2, java.sql.Date.valueOf(fromDate));
            stmt.setDate(3, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new AttendanceRecord(
                    rs.getString("student_id"),
                    rs.getString("subject"),
                    rs.getDate("date").toLocalDate(),
                    rs.getBoolean("is_present")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getAttendancePercentage(String studentId, String subject) {
        String sqlTotal = "SELECT COUNT(*) FROM Attendance WHERE student_id=? AND subject=?";
        String sqlPresent = "SELECT COUNT(*) FROM Attendance WHERE student_id=? AND subject=? AND is_present=1";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            long total = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlTotal)) {
                stmt.setString(1, studentId);
                stmt.setString(2, subject);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) total = rs.getLong(1);
            }
            
            if (total == 0) return 0.0;
            
            long present = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlPresent)) {
                stmt.setString(1, studentId);
                stmt.setString(2, subject);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) present = rs.getLong(1);
            }
            return (double) present / total * 100.0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}