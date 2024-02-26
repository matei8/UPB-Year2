# *_Sistem Informatic pentru Gestiunea și Repartizarea Studenților_*

## *Descriere*
Proiectul presupune implementarea unui sistem informatic pentru gestionarea 
și repartizarea studenților la materiile opționale din facultate. 
Acest sistem are ca scop principal facilitarea procesului de administrare 
a datelor legate de mediile studenților, preferințele de repartizare și 
specificul cursurilor. Documentația de mai jos oferă o prezentare detaliată 
a funcționalităților implementate și a entităților cheie implicate.

## *Entități*
### *Student, StudentLicenta si StudentMaster*

-- **Entitatea** `Student` reprezintă un student al facultății, iar clasele `StudentLicenta`
si `StudentMaster` mostenind-o detin aceleasi caracteristici. Acestea avand urmatoarele atribute:

* **nume:** Reprezintă numele studentului (atribut final).
* **medie:** Reprezintă media studentului.
* **cursAsignat:** Reprezintă cursul asignat studentului.
* **preferinte:** Reprezintă o listă de preferințe legate de cursuri, implementata ca un
`ArrayList` de `String`-uri (numele cursurilor) pentru a facilita adaugarea cursurilor si evitarea complexitatii
accesarii datelor despre cursuri. Utilizarea unei liste de tipul `List<Curs<? extends Student>>` ar fi presupus
o cautare mai complexa in cazurile de query pe lista de preferinte, deoarece ar fi trebuit sa fie apelate mai multe 
metode din cadrul clasei `Curs`. 

-- **Functia Comparator**: `public static Comparator<Student> StuNameComparator = new Comparator<Student>()` este
un comparator static pentru sortarea studentilor dupa nume in ordine alfabetica, folosita pentru sortarea studentilor
din cadrul unui curs. (folosita in functia `Secretariat.repartizare()`)

-- **Subclase:** Clasa `Student` are doua subclase: `StudentLicenta` si `StudentMasterat`, care reprezinta studentii 
de licenta si de masterat. Constructorii celor doua subclase primesc ca paramteru tot un nume si o medie insa in 
interiorul constructorului se apeleaza constructorul clasei de baza `Student` cu parametrii respectivi prin 
keyword-ul `super`.

### *Curs*
-- **Entitatea** `Curs` se ocupa cu gestionarea cursurilor și a studenților înscriși în acestea. Clasa utilizează 
genericitatea pentru a permite utilizarea oricărui tip de student `StudentLicenta`/`StudentMaster` --> <T extends Student>.
Clasa are urmatoarele atribute:
* **nume:** Reprezintă numele cursului.
* **capacitate:** Reprezintă numărul maxim de studenți care pot fi înscriși la curs.
* **studenti:** Reprezintă lista de studenți înscriși la curs, implementată ca un `ArrayList` de tipul `T` 
(studenti de licenta sau masterat), folosind in continuare genericitatea pentru a facilita adaugarea studentilor si gestionarea
lor.

-- **Functia valabilInscriere():** `public boolean valabilInscriere()` verifica daca mai sunt locuri disponibile la curs.

### *Secretariat*

-- **Entitatea** `Secretariat` realizeaza gestionarea informațiilor legate de studenți, cursuri și repartizare.
Aceasta oferă funcționalități precum adăugarea de studenți, calcularea mediilor, adaugarea cursurilor și repartizarea 
studenților în funcție de preferințe lor si disponibilitatea cursurilor. Atributele clasei sunt:

* **cursuri:** Listă de cursuri, fiecare acceptând orice tip de student (`T extends Student`).
* **studenti:** Listă de studenți.
* **medii:** Listă de medii ale studenților.

-- **Benficiile Colectiilor Alese:** 
Predominant au fost folosite Colectii ale Interfetei List. Utilizarea genericității (Curs<? extends Student>) permite 
adăugarea oricărui tip de curs care extinde clasa Student. Capacitate de Stocare: Folosirea unei liste din clasa 
ArrayList oferă un mecanism eficient de stocare, asigurând gestionarea dinamică a cursurilor fără a specifica dimensiunea 
la inițializare. Totodată, metodele de acces și getterele entitatilor permit flexibilitate mare in accesarea datelor.
Sortarea ulterioară a listei permite menținerea unui clasament corect al studenților în funcție de medii.

-- **Unele dintre metodele cele mai relevante din cadrul acestei clase sunt:**
#### `public void adaugaStudent(String info) throws StudentDuplicat`

- Verifică dacă studentul există deja și aruncă o excepție `StudentDuplicat` în caz afirmativ.
- Adaugă un student pe baza informațiilor furnizate.

#### `private Student creeazaStudent(String info)`

- Creează un obiect `Student` pe baza informațiilor furnizate. Din parametrul info se extrage si tipul studentului, 
astfel functia va return un student ori de tipul `StudentLicenta` ori de tipul `StudentMaster`.

#### `public void citesteMedii(List<String> note)`

- Actualizează mediile studenților pe baza unei liste de note.
- Sortează lista de medii prima oara dupa medie, apoi alfabetic in cazul in care doua medii sunt egale.

#### `public void updateMedie(String nume, double medieNoua)`

- Actualizează media unui student și actualizează lista de medii.
- Sortează lista de medii prima oara dupa medie, apoi alfabetic in cazul in care doua medii sunt egale.

#### `public void repartizare()`

- Lista de medii (this.medii) este sortată în ordine descrescătoare, astfel încât studenții cu medii 
mai mari să fie repartizați primii.
- Pentru fiecare linie de medie, se obține numele studentului (nume) și se caută obiectul student corespunzător în
lista de studenți (getStudent(nume)).
- Se verifică tipul de student (licență sau master) și se apelează metoda corespunzătoare pentru repartizare 
(repartizeazStudLicenta sau repartizeazaStudMaster).
- Sortează lista de studenți pentru fiecare curs.
- După finalizarea repartizării, studenții din fiecare curs sunt sortați în ordine alfabetică, folosind comparatorul 
`Student.StuNameComparator()`.
- 
#### `private void repartizeazStudLicenta(StudentLicenta student)` si `private void repartizeazaStudMaster(StudentMaster student)`

- Se parcurg preferințele studentului și se caută cursul corespunzător în lista de cursuri (getCurs(numeCurs)).
- Dacă cursul este valid (valabilInscriere), se adaugă studentul la curs (adaugaStudent(student)). Dacă cursul nu este 
valid, se trece la următoarea preferință.