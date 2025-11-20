package model;

import java.io.Serializable; // ADD THIS IMPORT

public class Student implements Serializable { // ADD 'implements Serializable'
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    // We should add the subject/class the student belongs to
    private String subject; 

    public Student(String id,String name, String subject){
        this.id=id; 
        this.name=name;
        this.subject=subject;
    }
    
    public String getId(){return id;}
    public String getName(){return name;}
    public String getSubject(){return subject;}
    
    // Setters for Edit functionality
    public void setName(String name) {
        this.name = name;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}