#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>

#include "helpers.h"
#include "requests.h"
#include "parson.h"
#include "operations.h"

int main(int argc, char *argv[]) {
    int sockfd;
    char *command = (char *)malloc(BUFLEN);
    char *cookie = NULL; // cookie retine cookie-ul de sesiune
    char *token = NULL; // token retine token-ul JWT

    /* Urmatorul while cicleaza pentru a parsa comenzi de la utilizator
     * iesind din bucla numai in momentul in care se primeste comanda exit.
     * De asemenea, pentru comenzile care necesita autentificare, se verifica
     * daca utilizatorul este autentificat, in caz contrar se afiseaza un mesaj
     * de eroare, prin verificarea cookie sau a tokenului ed acces in biblioteca.
     */

    while (1) {
        fgets(command, BUFLEN, stdin);
        sockfd = open_connection(HOST, PORT, AF_INET, SOCK_STREAM, 0);

        if (strstr(command, "exit")) {
            process_exit(sockfd);
            break;
        } else if (strstr(command, "logout")) {
            if (cookie == NULL) {
                printf("ERROR: You must be logged in to logout\n");
                continue;
            }
            process_logout(sockfd, cookie);
        } else if (strstr(command, "register")) {
            process_register(sockfd);
        } else if (strstr(command, "login")) {
            cookie = process_login(sockfd, cookie);
        } else if (strstr(command, "enter_library")) {
            if (cookie == NULL) {
                printf("ERROR: You must be logged in to enter the library\n");
                continue;
            }
            token = process_enter_library(sockfd, cookie);
        } else if (strstr(command, "get_books")) {
            if (token == NULL) {
                printf("ERROR: You must be logged in to get the books\n");
                continue;
            }
            process_get_books(sockfd, token);
        } else if (strstr(command, "get_book")) {
            if (token == NULL) {
                printf("ERROR: You must be logged in to get the book\n");
                continue;
            }
            process_get_book_id(sockfd, token);
        } else if (strstr(command, "add_book")) {
            if (token == NULL) {
                printf("ERROR: You must be logged in to add a book\n");
                continue;
            }
            process_add_book(sockfd, token, cookie);
        } else if (strstr(command, "delete_book")) {
            if (token == NULL) {
                printf("ERROR: You must be logged in to delete a book\n");
                continue;
            }
            process_delete_book(sockfd, token);
        }
    }

    return 0;
}