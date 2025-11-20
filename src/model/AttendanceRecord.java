package model;

import java.io.Serializable;
import java.time.LocalDate;

public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String studentId;
    private String subject; // To link to the teacher/class
    private LocalDate date;
    private boolean isPresent;

    public AttendanceRecord(String studentId, String subject, LocalDate date, boolean isPresent) {
        this.studentId = studentId;
        this.subject = subject;
        this.date = date;
        this.isPresent = isPresent;
    }

    public String getStudentId() { return studentId; }
    public String getSubject() { return subject; }
    public LocalDate getDate() { return date; }
    public boolean isPresent() { return isPresent; }
}