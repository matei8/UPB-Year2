## Utilizarea bibliotecii de parsare JSON

În acest proiect, am ales să folosim biblioteca Parson pentru parsarea JSON. Parson este o bibliotecă de parsare JSON 
în limbajul C, care este ușor de utilizat și are o amprentă de memorie mică.

Am ales Parson din mai multe motive:

**Ușurința de utilizare**: Parson oferă o interfață simplă și intuitivă pentru manipularea și parsarea JSON. Acest 
lucru ne-a permis să ne concentrăm mai mult pe logica aplicației noastre, în loc să ne îngrijorăm de detaliile de 
nivel scăzut ale parsării JSON.

**Amprenta de memorie mică**: Parson este proiectat pentru a fi ușor și eficient în ceea ce privește utilizarea 
memoriei. Acest lucru este important pentru o aplicație, deoarece asigura că ruleaza eficient. 

În proiect am folosit Parson pentru a parsa răspunsurile JSON pe care le primim de la server. De asemenea, îl 
folosesc si pentru a genera obiecte JSON pe care le trimitem la server în cererile noastre. Acest ermite să 
prelucrez datele într-un format structurat și ușor de înțeles in momentul in care exista o colectie mai mare de obiecte
care se afla intr-un server, iar userul face query in acesta.

## Bucla Principală în client.c

Bucla principală din `client.c` este responsabilă pentru gestionarea intrărilor de la utilizator și efectuarea 
operațiunilor corespunzătoare. Aceasta citește în mod continuu comenzi de la intrarea standard și le procesează până 
când comanda `exit` este introdusă.

### Structura Buclei

Structura buclei este o buclă `while (1)`, ceea ce înseamnă că va rula la nesfârșit până când este întreruptă explicit.

## Procesarea Comenzilor

Bucla citește o comandă de la intrarea standard folosind `fgets`. Apoi deschide o conexiune la server folosind 
`open_connection`.

Comanda este apoi procesată folosind o serie de declarații `if` și `else if`, fiecare verificând dacă șirul de
comenzi conține un anumit cuvânt cheie folosind `strstr`.

## Comenzi

Iată comenzile pe care bucla le poate procesa:

- `exit`: Această comandă întrerupe bucla și încheie programul. Ea apelează funcția `process_exit` înainte de a 
- întrerupe bucla.

- `logout`: Această comandă deconectează utilizatorul. Mai întâi verifică dacă utilizatorul este conectat verificând 
- dacă `cookie` este `NULL`. Dacă utilizatorul nu este conectat, afișează un mesaj de eroare și continuă la următoarea 
- iterație a buclei. Dacă utilizatorul este conectat, apelează funcția `process_logout`.

- `register`: Această comandă înregistrează un utilizator nou. Ea apelează funcția `process_register`.

- `login`: Această comandă conectează un utilizator. Ea apelează funcția `process_login` și stochează cookie-ul returnat.

- `enter_library`: Această comandă permite unui utilizator conectat să intre în bibliotecă. Mai întâi verifică dacă
- utilizatorul este conectat verificând dacă `cookie` este `NULL`. Dacă utilizatorul nu este conectat, afișează un
- mesaj de eroare și continuă la următoarea iterație a buclei. Dacă utilizatorul este conectat, apelează funcția 
- `process_enter_library` și stochează token-ul returnat.

- `get_books`: Această comandă recuperează toate cărțile. Mai întâi verifică dacă utilizatorul este conectat verificând 
- dacă `token` este `NULL`. Dacă utilizatorul nu este conectat, afișează un mesaj de eroare și continuă la următoarea 
- iterație a buclei. Dacă utilizatorul este conectat, apelează funcția `process_get_books`.

- `get_book`: Această comandă recuperează o carte specifică după ID-ul său. Mai întâi verifică dacă utilizatorul este 
- conectat verificând dacă `token` este `NULL`. Dacă utilizatorul nu este conectat, afișează un mesaj de eroare și 
- continuă la următoarea iterație a buclei. Dacă utilizatorul este conectat, apelează funcția `process_get_book_id`.

- `add_book`: Această comandă adaugă o carte nouă. Mai întâi verifică dacă utilizatorul este conectat verificând dacă 
- `token` este `NULL`. Dacă utilizatorul nu este conectat, afișează un mesaj de eroare și continuă la următoarea 
- iterație a buclei. Dacă utilizatorul este conectat, apelează funcția `process_add_book`.

- `delete_book`: Această comandă șterge o carte specifică după ID-ul său. Mai întâi verifică dacă utilizatorul este c
- onectat verificând dacă `token` este `NULL`. Dacă utilizatorul nu este conectat, afișează un mesaj de eroare și continuă la următoarea iterație a buclei. Dacă utilizatorul este conectat, apelează funcția `process_delete_book`.

## Gestionarea Erorilor

Dacă o comandă necesită ca utilizatorul să fie conectat, dar utilizatorul nu este, bucla afișează un mesaj de 
eroare și continuă la următoarea iterație. Acest lucru împiedică programul să încerce să efectueze operațiuni care 
necesită autentificare fără un cookie sau un token valid.

## Macrouri din `helpers.h`

In helpers.h sunt definite urmatoarele macrouri cu scopul de a facilita utilizarea recurenta a stringurilor respective
pe parcursul programului:

```
#define BUFLEN 4096
#define LINELEN 1000

#define HOST "34.246.184.49"
#define PORT 8080

#define REGISTER_PATH "/api/v1/tema/auth/register"
#define LOGIN_PATH "/api/v1/tema/auth/login"
#define ACCESS_PATH "/api/v1/tema/library/access"
#define BOOKS_PATH "/api/v1/tema/library/books"
#define LOGOUT_PATH "/api/v1/tema/auth/logout"
#define JSON_TYPE "application/json"
```

## Funcții

### `void process_exit(int sockfd)`

Această funcție este folosită pentru a încheia programul. Primeste descriptorul de fișier al socketului ca argument
și închide conexiunea cu serverul folosind descriptorul. Închiderea conexiunii se face prin funcția `close(sockfd)`, 
care închide socketul și eliberează toate resursele asociate acestuia.

### `void process_register(int sockfd)`

Această funcție este folosită pentru a înregistra un utilizator nou. Ia descriptorul de fișier al socketului ca
argument și trimite o cerere de înregistrare la server folosind  descriptorul. Cererea de înregistrare
include detalii precum numele de utilizator și parola, care sunt introduse de utilizator. Acestea sunt trimise 
la server într-un format specific.

### `char* process_login(int sockfd, char *cookie)`

Functia este folosită pentru a conecta un utilizator. Primeste descriptorul de fișier al socketului și cookie-ul 
ca argumente. Funcția trimite o cerere de autentificare la server folosind descriptorul de fișier al socketului și 
returnează un cookie de sesiune dacă autentificarea a reusit. Cookie-ul de sesiune este obținut prin apelul 
funcției get_cookie(response), unde response este răspunsul primit de la server după trimiterea cererii de autentificare.

### `char* process_enter_library(int sockfd, char *cookie)`

Este folosită pentru a permite unui utilizator conectat să intre în bibliotecă. Ea ia descriptorul de 
fișier al socketului și cookie-ul ca argumente. Funcția trimite o cerere la server pentru a intra în bibliotecă, 
folosind cookie-ul pentru a se autentifica. Dacă cererea este reușită, funcția returnează un token de acces la 
bibliotecă. Token-ul este obținut prin apelul funcției `get_token(response)`, unde response este răspunsul primit de 
la server după trimiterea cererii de intrare în bibliotecă.

### `void process_get_books(int sockfd, char *token)`

Această funcție este folosită pentru a recupera toate cărțile. Ea ia descriptorul de fișier al socketului și token-ul 
ca argumente. Funcția trimite o cerere la server pentru a obține o listă de cărți, folosind token-ul pentru a se 
autentifica. Lista de cărți este apoi afișată utilizatorului. Afișarea cărților se face prin parcurgerea răspunsului 
primit de la server și extragerea informațiilor despre fiecare carte.

### `void process_get_book_id(int sockfd, char *token)`

Această funcție este folosită pentru a recupera o carte specifică după ID-ul său. Funcția trimite o cerere la server 
pentru a obține detalii despre o carte specifică, folosind token-ul pentru a se autentifica. Detaliile cărții sunt 
apoi afișate in STDOUT. Afișarea detaliilor cărții se face prin extragerea informațiilor din răspunsul primit de 
la server și afișarea acestora.

### `void process_add_book(int sockfd, char *token, char *cookie)`

Se adăuga o carte nouă. Funcția trimite o cerere la server pentru a adăuga o carte nouă, folosind token-ul și cookie-ul
pentru a se autentifica. Detaliile cărții sunt introduse de utilizator și trimise la server.

### `void process_delete_book(int sockfd, char *token)`

Această funcție este folosită pentru a șterge o carte specifică după ID-ul său. Primeste descriptorul de fișier al 
socketului și token-ul ca argumente. Funcția trimite o cerere la server pentru a șterge o carte specifică, folosind 
token-ul pentru a se autentifica. Se foloseste de functia `char *compute_delete_request(char *host, char *url, char *query_params,
char **cookies, int cookies_count)` din requests.c pentru a construi cererea de ștergere.

# Funcții din helpers.c

## `char* get_token(char *response)`

Această funcție este folosită pentru a extrage un token din răspunsul unui server. Ea ia răspunsul serverului ca 
argument și returnează token-ul extras.

## `char* get_cookie(char *response)`

Această funcție este folosită pentru a extrage un cookie din răspunsul unui server. Ea ia răspunsul serverului ca 
argument și returnează cookie-ul extras.