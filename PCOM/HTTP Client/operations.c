#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/in.h>
#include <sys/socket.h>

#include "operations.h"
#include "requests.h"
#include "helpers.h"
#include "buffer.h"
#include "parson.h"

void process_exit(int sockfd) {
    close_connection(sockfd);
    exit(0);
}

void process_register(int sockfd) {
    char *response;

    // Asteapta datele de înregistrare de la utilizator
    char username[BUFLEN];
    printf("username=");
    fgets(username, BUFLEN, stdin);

    char password[BUFLEN];
    printf("password=");
    fgets(password, BUFLEN, stdin);

    // Elimină newline-ul de la sfârșitul stringurilor
    username[strlen(username) - 1] = '\0';
    password[strlen(password) - 1] = '\0';

    // Verifică dacă username-ul conține spații
    if (strstr(username, " ") != NULL) {
        printf("ERROR: Username nu poate conține spații!\n");
        return;
    }

    // Creează un obiect JSON cu datele de înregistrare
    JSON_Value *root_value = json_value_init_object();
    JSON_Object *root_object = json_value_get_object(root_value);
    json_object_set_string(root_object, "username", username);
    json_object_set_string(root_object, "password", password);

    char *user_data = json_serialize_to_string_pretty(root_value);
    char *message = compute_post_request(HOST, REGISTER_PATH, JSON_TYPE, &user_data, 1, NULL, 0, NULL);

    // Trimite cererea la server
    send_to_server(sockfd, message);

    // Primește răspunsul de la server
    response = receive_from_server(sockfd);

    // Extrage codul intors de request din răspuns
    char *status_code_str = strtok(response, " ");
    status_code_str = strtok(NULL, " ");
    int status_code = atoi(status_code_str);

    // Verifică ccodul intors de request și afișează un mesaj corespunzător
    if (status_code >= 200 && status_code < 300) {
        printf("SUCCESS: Utilizator înregistrat cu succes!\n");
    } else if (status_code >= 400 && status_code < 600) {
        if (strstr(response, "username") != NULL) {
            printf("ERROR: Acest username este deja folosit de către cineva!\n");
        } else {
            printf("ERROR: Eroare la înregistrare!\n");
        }
    }

    // Eliberează memoria alocată
    free(message);
    json_value_free(root_value);
    json_free_serialized_string(user_data);
}

char *process_login(int sockfd, char *cookie) {
    char *response;

    // Asteapta datele de autentificare de la utilizator
    char username[BUFLEN];
    printf("username=");
    fgets(username, BUFLEN, stdin);

    char password[BUFLEN];
    printf("password=");
    fgets(password, BUFLEN, stdin);

    // Elimină newline-ul de la sfârșitul stringurilor
    username[strlen(username) - 1] = '\0';
    password[strlen(password) - 1] = '\0';

    // Verifică dacă username-ul conține spații
    if (strstr(username, " ") != NULL) {
        printf("ERROR: Username nu poate conține spații!\n");
        return NULL;
    }

    // Creează un obiect JSON cu datele de autentificare
    JSON_Value *root_value = json_value_init_object();
    JSON_Object *root_object = json_value_get_object(root_value);
    json_object_set_string(root_object, "username", username);
    json_object_set_string(root_object, "password", password);

    char *user_data = json_serialize_to_string_pretty(root_value);
    char *message = compute_post_request(HOST, LOGIN_PATH, JSON_TYPE, &user_data, 1, NULL, 0, NULL);

    // Trimite cererea la server
    send_to_server(sockfd, message);

    // Primește răspunsul de la server
    response = receive_from_server(sockfd);

    // Creează o copie a răspunsului pentru a extrage codul de stare
    // altfel strtok va modifica răspunsul original rezultand in erori
    char *response_copy = strdup(response);
    if (response_copy == NULL) {
        perror("Failed to allocate memory for response_copy");
        return NULL;
    }

    // Extrage codul intors de request din răspuns
    char *status_code_str = strtok(response_copy, " ");
    status_code_str = strtok(NULL, " ");
    int status_code = atoi(status_code_str);

    free(response_copy);

    // Verifică codul intors de request și afișează un mesaj corespunzător
    if (status_code >= 200 && status_code < 300) {
        printf("SUCCESS: Utilizator autentificat cu succes!\n");
        // Extrage cookie-ul de sesiune din răspuns
        cookie = get_cookie(response);
    } else if (status_code >= 400 && status_code < 600) {
        if (strstr(response, "account") != NULL) {
            printf("ERROR: This client with this username does not exist!\n");
        } else {
            printf("ERROR: The username and the password don't match!\n");
        }
    }

    free(response);
    free(message);
    json_value_free(root_value);
    json_free_serialized_string(user_data);

    // Returnează cookie-ul de sesiune
    return cookie;
}

void process_logout(int sockfd, char *cookie) {
    char *response;

    // Creează mesajul de cerere GET
    char *message = compute_get_request(HOST, LOGOUT_PATH, NULL, &cookie, 1);
    send_to_server(sockfd, message);
    response = receive_from_server(sockfd);

    // Extrage codul de stare din răspuns
    char *status_code_str = strtok(response, " ");
    status_code_str = strtok(NULL, " ");
    int status_code = atoi(status_code_str);

    // Verifică codul intors de request și afișează un mesaj corespunzător
    if (status_code >= 200 && status_code < 300) {
        printf("SUCCESS: Utilizator deconectat cu succes!\n");
        // Eliberează memoria alocată pentru cookie
        free(cookie);
        cookie = NULL;
    } else if (status_code >= 400 && status_code < 600) {
        printf("ERROR: Eroare la deconectare!\n");
    }

    free(message);
}

char* process_enter_library(int sockfd, char *cookie) {
    char *message, *response;

    // Include cookie-ul de sesiune în matricea de cookie-uri
    char **cookies = calloc(1, sizeof(char *));
    cookies[0] = calloc(LINELEN, sizeof(char));
    memcpy(cookies[0], cookie, strlen(cookie));

    message = compute_get_request(HOST, ACCESS_PATH, NULL, cookies, 1);
    send_to_server(sockfd, message);
    response = receive_from_server(sockfd);

    // Creează o copie a răspunsului pentru a extrage codul de stare
    // altfel strtok va modifica răspunsul original rezultand in erori
    char *response_copy = strdup(response);
    if (response_copy == NULL) {
        perror("Failed to allocate memory for response_copy");
        return NULL;
    }

    // Extragecodul intors de request din răspuns
    char *status_code_str = strtok(response_copy, " ");
    status_code_str = strtok(NULL, " ");
    int status_code = atoi(status_code_str);

    free(response_copy);

    // Verifică codul intors de request și afișează un mesaj corespunzător
    if (status_code >= 200 && status_code < 300) {
        printf("SUCCESS: Acces la bibliotecă reușit!\n");
    } else if (status_code >= 400 && status_code < 600) {
        printf("ERROR: Eroare la accesarea bibliotecii!\n");
    }

    // Extrage token-ul din răspuns
    char *finalToken = get_token(response);

    free(message);
    free(response);
    free(cookies[0]);
    free(cookies);

    // Returnează token-ul
    return finalToken;
}

void process_get_books(int sockfd, char *tokenJWT) {
    char *message, *response;

    // Creează mesajul de cerere GET
    message = compute_get_request(HOST, BOOKS_PATH, NULL, NULL, 0);
    memset(message + strlen(message) - 2, 0, 2);
    strcat(message, "Authorization: Bearer ");
    strcat(message, tokenJWT);
    strcat(message, "\r\n\r\n");

    send_to_server(sockfd, message);
    response = receive_from_server(sockfd);

    // Extrage lista de cărți din răspuns
    char *res = basic_extract_json_response(response);
    if (res != NULL) {
        printf("%s\n", res);
    }

    free(message);
    free(response);
}

void process_add_book(int sockfd, char *tokenJWT, char *cookie) {
    char *message;

    // Alocarea memoriei pentru datele formularului
    char **form_data = calloc(2, sizeof(char *));
    form_data[0] = calloc(LINELEN, sizeof(char));
    form_data[1] = calloc(LINELEN, sizeof(char));

    // Extragerea token-ului JWT din string-ul JSON
    JSON_Value *token_value = json_parse_string(tokenJWT);
    JSON_Object *token_object = json_value_get_object(token_value);
    const char *finalToken = json_object_get_string(token_object, "token");
    sprintf(form_data[0], "Authorization: Bearer %s", finalToken);

    // Inițializarea variabilelor pentru datele cărții
    char title[LINELEN], author[LINELEN], genre[LINELEN], publisher[LINELEN];
    char page_count_string[LINELEN];
    int page_count;
    int check_validity = 0;

    // Citirea datelor cărții de la utilizator
    printf("title=");
    fgets(title, LINELEN, stdin);
    if (title[0] == '\n') {
        check_validity = 1;
    }
    title[strlen(title) - 1] = '\0';

    printf("author=");
    fgets(author, LINELEN, stdin);
    if (author[0] == '\n') {
        check_validity = 1;
    }
    author[strlen(author) - 1] = '\0';

    printf("genre=");
    fgets(genre, LINELEN, stdin);
    if (genre[0] == '\n') {
        check_validity = 1;
    }
    genre[strlen(genre) - 1] = '\0';

    printf("publisher=");
    fgets(publisher, LINELEN, stdin);
    if (publisher[0] == '\n') {
        check_validity = 1;
    }
    publisher[strlen(publisher) - 1] = '\0';

    printf("page_count=");
    fgets(page_count_string, LINELEN, stdin);
    page_count = atoi(page_count_string);

    // Verificarea validității datelor introduse
    if (check_validity == 1) {
        printf("ERROR: Câmpurile nu pot fi goale!\n");
        free(form_data[0]);
        free(form_data[1]);
        free(form_data);
        json_value_free(token_value);
        return;
    } else if (page_count == 0 && page_count_string[0] != '0') {
        printf("ERROR: Tip de date incorect pentru numarul de pagini\n");
        free(form_data[0]);
        free(form_data[1]);
        free(form_data);
        json_value_free(token_value);
        return;
    }

    // Crearea obiectului JSON cu datele cărții
    JSON_Value *root_value = json_value_init_object();
    JSON_Object *root_object = json_value_get_object(root_value);
    json_object_set_string(root_object, "title", title);
    json_object_set_string(root_object, "author", author);
    json_object_set_string(root_object, "genre", genre);
    json_object_set_string(root_object, "publisher", publisher);
    json_object_set_number(root_object, "page_count", page_count);

    char *book_data = json_serialize_to_string_pretty(root_value);

    // Crearea cererii POST
    message = compute_post_request(HOST, BOOKS_PATH, JSON_TYPE, &book_data, 1, &cookie, 1, tokenJWT);

    send_to_server(sockfd, message);
    char *response;
    response = receive_from_server(sockfd);

    // Analizarea răspunsului
    char *status_code_str = strtok(response, " ");
    status_code_str = strtok(NULL, " ");
    int status_code = atoi(status_code_str);

    if (status_code >= 200 && status_code < 300) {
        printf("SUCCESS: Cartea a fost adăugată cu succes!\n");
    } else if (status_code >= 400 && status_code < 600) {
        printf("ERROR: Eroare la adăugarea cărții!\n");
    }

    free(message);
    json_value_free(root_value);
    json_free_serialized_string(book_data);
    free(response);
}

void process_get_book_id(int sockfd, char *tokenJWT) {
    char *message, *response;
    char book_id[LINELEN];

    // Citirea ID-ului cărții de la utilizator
    printf("id=");
    fgets(book_id, LINELEN, stdin);
    book_id[strlen(book_id) - 1] = '\0';

    // Crearea căii pentru cererea GET
    char path[LINELEN];
    sprintf(path, "%s/%s", BOOKS_PATH, book_id);

    message = compute_get_request(HOST, path, NULL, NULL, 0);
    memset(message + strlen(message) - 2, 0, 2);

    // Adăugarea token-ului JWT la cerere pentru autorizare
    strcat(message, "Authorization: Bearer ");
    strcat(message, tokenJWT);
    strcat(message, "\r\n\r\n");

    send_to_server(sockfd, message);
    response = receive_from_server(sockfd);

    // Extrage lista de cărți din răspuns
    char *book = basic_extract_json_response(response);
    if (book != NULL) {
        JSON_Value *parsed_json = json_parse_string(book);
        // Afișarea cărții sub forma de JSON
        char *serialized_json = json_serialize_to_string_pretty(parsed_json);
        printf("%s\n", serialized_json);

        json_value_free(parsed_json);
        json_free_serialized_string(serialized_json);
    } else {
        printf("ERROR: Eroare la extragerea cărții!\n");
    }

    free(message);
    free(response);
}

void process_delete_book(int sockfd, char *tokenJWT) {
    char *message, *response;
    char book_id[LINELEN];

    // Citirea ID-ului cărții de la utilizator
    printf("id=");
    fgets(book_id, LINELEN, stdin);
    book_id[strlen(book_id) - 1] = '\0';

    // Crearea căii pentru cererea DELETE
    char path[LINELEN];
    sprintf(path, "%s/%s", BOOKS_PATH, book_id);

    // Crearea cererii DELETE
    message = compute_delete_request(HOST, path, NULL, NULL, 0);
    memset(message + strlen(message) - 2, 0, 2);
    strcat(message, "Authorization: Bearer ");
    strcat(message, tokenJWT);
    strcat(message, "\r\n\r\n");

    send_to_server(sockfd, message);
    response = receive_from_server(sockfd);

    // Analizarea răspunsului
    char *status_code_str = strtok(response, " ");
    status_code_str = strtok(NULL, " ");
    int status_code = atoi(status_code_str);

    if (status_code >= 200 && status_code < 300) {
        printf("SUCCESS: Cartea a fost ștearsă cu succes!\n");
    } else if (status_code >= 400 && status_code < 600) {
        printf("ERROR: Eroare la ștergerea cărții!\n");
    }

    free(message);
    free(response);
}
