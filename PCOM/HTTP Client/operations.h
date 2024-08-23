#ifndef _OPERATIONS_
#define _OPERATIONS_

void process_register(int sockfd);
void process_exit(int sockfd);
char* process_login(int sockfd, char *cookie);
void process_logout(int sockfd, char *cookie);
char* process_enter_library(int sockfd, char *cookie);
void process_get_books(int sockfd, char *tokenJWT);
void process_add_book(int sockfd, char *tokenJWT, char *cookie);
void process_get_book_id(int sockfd, char *tokenJWT);
void process_delete_book(int sockfd, char *tokenJWT);

#endif