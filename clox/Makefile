CC := gcc
CFLAGS := -std=c99 -Wall -Wextra -Wpedantic -g -MMD -MP

TARGET := clox
SOURCES := $(wildcard *.c)
OBJECTS := $(SOURCES:.c=.o)
DEPS := $(OBJECTS:.o=.d)

.PHONY: all run clean

all: $(TARGET)

$(TARGET): $(OBJECTS)
	$(CC) $(CFLAGS) $^ -o $@

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

-include $(DEPS)

run: $(TARGET)
	./$(TARGET)

clean:
	rm -f $(TARGET) $(OBJECTS) $(DEPS)