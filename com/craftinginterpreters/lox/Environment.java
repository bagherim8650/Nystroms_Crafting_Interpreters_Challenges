package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();
  private Object[] slots = new Object[8];

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  void define(String name, Object value) {
    values.put(name, value);
  }

  void defineAt(int index, Object value) {
    ensureCapacity(index);
    slots[index] = value;
  }

  Object getAt(int distance, int index) {
    return ancestor(distance).slots[index];
  }

  void assignAt(int distance, int index, Object value) {
    Environment environment = ancestor(distance);
    environment.ensureCapacity(index);
    environment.slots[index] = value;
  }

  private void ensureCapacity(int index) {
    if (index < slots.length) return;

    int capacity = slots.length;
    while (index >= capacity) {
      capacity *= 2;
    }

    Object[] expanded = new Object[capacity];
    System.arraycopy(slots, 0, expanded, 0, slots.length);
    slots = expanded;
  }

  private Environment ancestor(int distance) {
    Environment environment = this;

    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }

    return environment;
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    if (enclosing != null) return enclosing.get(name);

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  Object getAt(int distance, String name) {
    return ancestor(distance).values.get(name);
  }

  void assignAt(int distance, Token name, Object value) {
    ancestor(distance).values.put(name.lexeme, value);
  }
}