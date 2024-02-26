package org.example;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static List<String> getFileContents(String fileName) throws IOException {
        File studFile = new File(fileName);
        return Files.readAllLines(studFile.toPath());
    }

    private static List<String> getStudInfo(String[] args) throws IOException {
        String studFile = "src/main/resources/" + args[0] + "/" + args[0] + ".in";
        List<String> studInfo;

        studInfo = getFileContents(studFile);
        return studInfo;
    }

    private static List<String> getStudNote(String[] args) throws IOException {
        String studNote1 = "src/main/resources/" + args[0] + "/note_1.txt";
        String filePrefix = "src/main/resources/" + args[0] + "/note_";
        List<String> studNote;

        studNote = getFileContents(studNote1);

        if (new File(filePrefix + "2.txt").exists()) {
            studNote.addAll(getFileContents(filePrefix + "2.txt"));
        }

        if (new File(filePrefix + "3.txt").exists()) {
            studNote.addAll(getFileContents(filePrefix + "3.txt"));
        }

        return studNote;
    }

    private static void writeToFile(String output, String message) {
        try {
            FileWriter fw = new FileWriter(output, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("***\n" + message);
            pw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String output = "src/main/resources/" + args[0] + "/" + args[0] + ".out";

	    List<String> infoStudenti;
        List<String> noteStudenti;

        try {
            infoStudenti = getStudInfo(args);
            noteStudenti = getStudNote(args);
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return;
        }

        Secretariat secretariat = new Secretariat();

        for (String line : infoStudenti) {
            String comanda = line.split(" - ")[0];
            if (comanda.equals("adauga_student")) {
                try {
                    secretariat.adaugaStudent(line);
                } catch (StudentDuplicat e) {
                    writeToFile(output, e.getMessage(line.split(" - ")[2]));
                }
            } else if (comanda.equals("citeste_mediile")) {
                secretariat.citesteMedii(noteStudenti);
            } else if (comanda.equals("posteaza_mediile")) {
                writeToFile(output, secretariat.getMediiAsString());
            } else if (comanda.equals("contestatie")) {
                String nume = line.split(" - ")[1];
                double medieNoua = Double.parseDouble(line.split(" - ")[2]);
                secretariat.updateMedie(nume, medieNoua);
            } else if (comanda.equals("adauga_curs")) {
                String tipCurs = line.split(" - ")[1];
                String numeCurs = line.split(" - ")[2];
                int capacitate = Integer.parseInt(line.split(" - ")[3]);
                secretariat.adaugaCurs(tipCurs, numeCurs, capacitate);
            } else if (comanda.equals("adauga_preferinte")) {
                String nume = line.split(" - ")[1];
                /*
                 "adauga_preferinte".length() + 2 * " - ".length() = 23

                 cu metoda split se obtine un array de cursuri ca si stringuri
                 care se transmite ca paramteru metodei of() al intereeftei List
                 */
                List<String> cursuri = List.of(line.substring(23 + nume.length()).split(" - "));
                secretariat.adaugaPreferinta(nume, cursuri);
            } else if (comanda.equals("repartizeaza")) {
                secretariat.repartizare();
            } else if (comanda.equals("posteaza_curs")) {
                String curs = secretariat.printeazaCurs(line.split(" - ")[1]);
                writeToFile(output, curs);
            } else if (comanda.equals("posteaza_student")) {
                String student = secretariat.studentToString(line.split(" - ")[1]);
                writeToFile(output, student);
            }
        }
    }
}
