package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Student {
    final private String nume;
    private double medie;
    private String cursAsignat;
    final private List<String> preferinte = new ArrayList<>();

    public Student(String nume, double medie) {
        this.nume = nume;
        this.medie = medie;
    }

    public static Comparator<Student> StuNameComparator = new Comparator<>() {
        public int compare(Student s1, Student s2) {
            String studNume1 = s1.getNume().toUpperCase();
            String studNume2 = s2.getNume().toUpperCase();

            return studNume1.compareTo(studNume2);
        }
    };

    public String getNume() {
        return this.nume;
    }

    public double getMedie() {
        return this.medie;
    }

    public String getCursAsignat() {
        return this.cursAsignat;
    }

    public void setMedie(double medie) {
        this.medie = medie;
    }

    public void setCurs(String numeCurs) {
        this.cursAsignat = numeCurs;
    }

    public void addPreferinta(String curs) {
        this.preferinte.add(curs);
    }

    public List<String> getPreferinte() {
        return this.preferinte;
    }

    public String toString() {
        if (this instanceof StudentMaster) {
            return "Student Master: " + this.getNume() + " - " + this.getMedie() + " - " + this.getCursAsignat();
        } else {
            return "Student Licenta: " + this.getNume() + " - " + this.getMedie() + " - " + this.getCursAsignat();
        }
    }
}
