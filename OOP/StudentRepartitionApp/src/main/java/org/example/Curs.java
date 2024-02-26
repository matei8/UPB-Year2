package org.example;

import java.util.ArrayList;
import java.util.List;

public class Curs<T extends Student> {
    final private List<T> studenti = new ArrayList<>();

    final private String numeCurs;
    final private int capacitate;

    public Curs(String numeCurs, int capacitate) {
        this.numeCurs = numeCurs;
        this.capacitate = capacitate;
    }

    public String getDenumire() {
        return this.numeCurs;
    }

    public int getCapacitateMaxima() {
        return this.capacitate;
    }

    public List<T> getStudenti () {
        return this.studenti;
    }

    public void inscrieStudent(T student) {
        if (student != null) {
            this.studenti.add(student);
        }
    }

    public boolean valabilInscriere() {
        return this.studenti.size() != this.capacitate;
    }

    public String cursToString() {
        String out = this.getDenumire() + " (" + this.getCapacitateMaxima() + ")\n";

        for (int i = 0; i < this.getStudenti().size(); i++) {
            String numeStudent = this.getStudenti().get(i).getNume();
            String medieStudent = String.valueOf(this.getStudenti().get(i).getMedie());

            out = out.concat(numeStudent + " - " + medieStudent);
            if (i != this.getStudenti().size() - 1) {
                out = out.concat("\n");
            }
        }

        return out;
    }
}
