#include <stdio.h>

int main(){
    printf("hello world");
}

typedef struct{
    char contents[];
    Node prev;
    Node next;
} Node;