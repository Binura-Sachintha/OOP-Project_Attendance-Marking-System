package main;
import ui.LoginFrame;
import repository.TeacherRepository;
import repository.StudentRepository;
import repository.AttendanceRepository;
import javax.swing.UIManager; // Required import for Look and Feel

public class Main {
    public static void main(String[] args){
    	
        // --- 1. Set Nimbus Look and Feel for modern UI ---
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fall back to default L&F if Nimbus is unavailable
            e.printStackTrace();
        }
        // ------------------------------------------------
        
        // --- 2. Initialize Repositories and Start the application ---
        TeacherRepository teacherRepo = new TeacherRepository();
        StudentRepository studentRepo = new StudentRepository();
        AttendanceRepository attendanceRepo = new AttendanceRepository(); // NEW: Attendance Repo
        
        // Pass ALL three repositories to the updated LoginFrame constructor
        new LoginFrame(teacherRepo, studentRepo, attendanceRepo).setVisible(true); // UPDATED
    }
}