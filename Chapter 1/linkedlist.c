#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

typedef struct Node{
    void *data;
    struct Node *prev;
    struct Node *next;
} Node;

Node* createNode(char data[])
{
    Node* newNode = (Node*)malloc(sizeof(Node));
    if (newNode == NULL) {
        fprintf(stderr, "Out of memory.\n");
        exit(1);
    }

    newNode->data = data;
    newNode->next = NULL;
    newNode->prev = NULL;
    return newNode;
}

void insertNode(Node** list, char data[]){
    Node* newNode = createNode(data);

    if(*list == NULL){
        *list = newNode;
    }else{
        newNode->next = *list;
        (*list)->prev = newNode;
        *list = newNode;
    }
}

bool findItem(Node* list, char target[]){
    Node* currentNode = list;

    while(currentNode != NULL){
        if(strcmp((char*)currentNode->data, target) == 0){
            return true;
        }
        currentNode = currentNode->next;
    }

    return false;
}

void deleteItem(Node** list, char target[]){
    if(*list == NULL || !findItem(*list, target)){
        printf("Item does not exist.\n");
        return;
    }

    Node* currentNode = *list;

    while(currentNode != NULL){
        if(strcmp((char*)currentNode->data, target) == 0){
            if(currentNode->prev != NULL){
                currentNode->prev->next = currentNode->next;
            } else {
                *list = currentNode->next;
            }

            if(currentNode->next != NULL){
                currentNode->next->prev = currentNode->prev;
            }

            printf("Deleted '%s'.\n", (char*)currentNode->data);
            free(currentNode);
            return;
        }
        currentNode = currentNode->next;
    }

    printf("Item does not exist.\n");
}

int main(void){
    printf("Creating a new linked list for pets");
    Node* petList = NULL;

    char *pet1 = "dog";
    char *pet2 = "cat";
    char *pet3 = "hamster";
    char *pet4 = "parrot";
    char *pet5 = "fish";

    insertNode(&petList, pet1);
    insertNode(&petList, pet2);
    insertNode(&petList, pet3);
    insertNode(&petList, pet4);
    insertNode(&petList, pet5);

    printf("Search for 'cat' -> %s\n", findItem(petList, "cat") ? "FOUND" : "NOT FOUND");
    printf("Search for 'turtle' -> %s\n", findItem(petList, "turtle") ? "FOUND" : "NOT FOUND");

    printf("\nDeleting an existing item:\n");
    deleteItem(&petList, "hamster");

    printf("\nDeleting a non-existent item:\n");
    deleteItem(&petList, "turtle");

    printf("\nFinal list:\n");
    for (Node* current = petList; current != NULL; current = current->next) {
        printf(" - %s\n", (char*)current->data);
    }

    return 0;
}