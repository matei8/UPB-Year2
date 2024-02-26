package org.example;

public class StudentDuplicat extends Exception {
    public String getMessage(String nume) {
        return "Student duplicat: " + nume;
    }
}
