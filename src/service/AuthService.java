package service;

import repository.TeacherRepository;
import repository.StudentRepository; // NEW: StudentRepository එක import කරන ලදී.
import model.Teacher;
import java.util.*;

public class AuthService {
    
    private TeacherRepository teacherRepo; // නම වෙනස් කරන ලදී.
    private StudentRepository studentRepo; // NEW: StudentRepository field එක.
    
    // UPDATED CONSTRUCTOR: දැන් TeacherRepository සහ StudentRepository යන දෙකම බාර ගනී.
    public AuthService(TeacherRepository tr, StudentRepository sr){
        this.teacherRepo = tr;
        this.studentRepo = sr; // StudentRepository එක assign කරන ලදී.
    }
    
    // Owner Login Logic
    public boolean ownerLogin(String u,String p){
        // Owner credentials are hardcoded
        return u.equals("owner") && p.equals("123");
    }
    
    // Teacher Login Logic
    public Optional<Teacher> teacherLogin(String u,String p){
        Optional<Teacher> t = teacherRepo.find(u);
        if(t.isPresent() && t.get().getPassword().equals(p)) return t;
        return Optional.empty();
    }
}