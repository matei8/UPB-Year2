package org.example;

import java.util.*;

public class Secretariat {
    static class Sort implements Comparator<String> {
        @Override
        public int compare(String line1, String line2) {
            double medie1 = Double.parseDouble(line1.split(" - ")[1]);
            double medie2 = Double.parseDouble(line2.split(" - ")[1]);

            if (medie1 == medie2) { // If the grades are equal, order by names
                String name1 = line1.split(" - ")[0];
                String name2 = line2.split(" - ")[0];
                return name1.compareToIgnoreCase(name2);
            }

            return Double.compare(medie2, medie1);
        }
    }

    final private List<Curs<? extends Student>> cursuri = new ArrayList<>();
    final private List<Student> studenti = new ArrayList<>();
    private List<String> medii = new ArrayList<>();

    public void adaugaStudent(String info) throws StudentDuplicat {
        verificaStudent(info.split(" - ")[2]);
        Student student = creeazaStudent(info);
        this.studenti.add(student);
    }

    public void verificaStudent(String nume) throws StudentDuplicat {
        for (Student student : studenti) {
            if (student.getNume().equals(nume)) {
                throw new StudentDuplicat();
            }
        }
    }

    private Student creeazaStudent(String info) {
        String nume = info.split(" - ")[2];

        if (info.split(" - ")[1].equals("licenta")) {
            return new StudentLicenta(nume, 0);
        }

        return new StudentMaster(nume, 0);
    }

    private double getMedie(String nume) {
        for (String line : medii) {
            if (line.split(" - ")[0].equals(nume)) {
                return Double.parseDouble(line.split(" - ")[1]);
            }
        }
        return -1;
    }

    private Student getStudent(String nume) {
        for (Student stud : studenti) {
            if (stud.getNume().equals(nume)) {
                return stud;
            }
        }
        return null;
    }

    private Curs<? extends Student> getCurs(String nume) {
        for (Curs<? extends Student> c : cursuri) {
            if (c.getDenumire().equals(nume)) {
                return c;
            }
        }
        return null;
    }

    public void citesteMedii(List<String> note) {
        this.medii = note;
        this.medii.sort(new Sort());

        for (Student student : studenti) {
            student.setMedie(getMedie(student.getNume()));
        }
    }

    public String getMediiAsString() {
        String mediiString = "";

        for (int i = 0; i < medii.size(); i++) {
            mediiString = mediiString.concat(medii.get(i));
            if (i != medii.size() - 1) {
                mediiString = mediiString.concat("\n");
            }
        }

        return mediiString;
    }

    public void updateMedie(String nume, double medieNoua) {
        for (Student stud : studenti) {
            if (stud.getNume().equals(nume)) {
                stud.setMedie(medieNoua);
                break;
            }
        }

        for (String medie : medii) {
            String numeStudCurent = medie.split(" - ")[0];
            if (numeStudCurent.equals(nume)) {
                int index = medii.indexOf(medie);
                String newInfo = nume + " - " + medieNoua;
                medii.set(index, newInfo);
                break;
            }
        }

        this.medii.sort(new Sort());
    }

    public void adaugaCurs(String tipCurs, String numeCurs, int capacitate) {
        Curs<? extends Student> curs;
        if (tipCurs.equals("licenta")) {
            curs = new Curs<StudentLicenta>(numeCurs, capacitate);
        } else {
            curs = new Curs<StudentMaster>(numeCurs, capacitate);
        }

        this.cursuri.add(curs);
    }

    public void adaugaPreferinta(String nume, List<String> cursuri) {
        Student student = getStudent(nume);
        if (student == null) {
            return;
        }

        for (String c : cursuri) {
            for (Curs<? extends Student> curs : this.cursuri) {
                if (curs.getDenumire().equals(c)) {
                    student.addPreferinta(curs.getDenumire());
                }
            }
        }
    }

    private void repartizeazStudLicenta(StudentLicenta student) {
        for (String c : student.getPreferinte()) {
            Curs<StudentLicenta> curs = (Curs<StudentLicenta>) getCurs(c);
            if (curs == null) {
                return;
            }

            if (curs.valabilInscriere()) {
                curs.inscrieStudent(student);
                student.setCurs(curs.getDenumire());
                return;
            } else {
                StudentLicenta studAux = curs.getStudenti().get(curs.getCapacitateMaxima() - 1);
                if (studAux.getMedie() == student.getMedie()) {
                    curs.inscrieStudent(student);
                    student.setCurs(curs.getDenumire());
                    return;
                }
            }
        }
    }

    private void repartizeazaStudMaster(StudentMaster student) {
        for (String c : student.getPreferinte()) {
            Curs<StudentMaster> curs = (Curs<StudentMaster>) getCurs(c);
            if (curs == null) {
                return;
            }

            if (curs.valabilInscriere()) {
                curs.inscrieStudent(student);
                student.setCurs(curs.getDenumire());
                return;
            } else {
                StudentMaster studAux = curs.getStudenti().get(curs.getCapacitateMaxima() - 1);
                if (studAux.getMedie() == student.getMedie()) {
                    curs.inscrieStudent(student);
                    student.setCurs(curs.getDenumire());
                    return;
                }
            }
        }
    }

    public void repartizare() {
        this.medii.sort(new Sort());

        for (String line : medii) {
            String nume = line.split(" - ")[0];
            Student student = getStudent(nume);
            if (student == null) {
                return;
            }
            
            if (student instanceof StudentLicenta) {
                repartizeazStudLicenta((StudentLicenta) student);
            } else {
                repartizeazaStudMaster((StudentMaster) student);
            }
        }

        for (Curs<? extends Student> curs : cursuri) {
            curs.getStudenti().sort(Student.StuNameComparator);
        }
    }

    public String printeazaCurs(String numeCurs) {
        Curs<? extends Student> curs = getCurs(numeCurs);
        if (curs == null) {
            return null;
        }
        return curs.cursToString();
    }

    public String studentToString(String nume) {
        Student student = getStudent(nume);
        if (student == null) {
            return null;
        }

        return student.toString();
    }
}
