package model;

import java.io.Serializable; // <--- ADD THIS IMPORT

public class Teacher implements Serializable { // <--- ADD THIS
    private static final long serialVersionUID = 1L; // Recommended best practice
    private String username;
    private String password;
    private String subject;
    
    public Teacher(String u,String p,String s){
        this.username=u; this.password=p; this.subject=s;
    }
    
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getSubject(){return subject;}
    
    // Setter for update (Optional, but often needed)
    public void setPassword(String password) {
        this.password = password;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}