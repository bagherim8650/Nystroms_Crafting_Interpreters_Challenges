#include <stdio.h>
#include <stdlib.h>

int main(){
    printf("Creating new linked list 'pets'");
    struct linkedList pets;

}

struct linkedList
{
    struct Node head;
};


struct Node{
    void *data;
    struct Node *prev;
    struct Node *next;
};

struct Node* insertAfter(struct Node *target, void *data){
    //allocate memory for node
    struct Node* newNode = (struct Node*)malloc(sizeof(struct Node));
    newNode->data = malloc(sizeof(*data));
    newNode->next = target->next;
    newNode->prev = target;

    //copy new node to memory allocated
    memcpy(newNode->data, data, sizeof(*data));
};