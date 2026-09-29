package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.craftinginterpreters.lox.Expr.Ternary;
import com.craftinginterpreters.lox.Expr.Unary;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
	private final Interpreter interpreter;
  private final Stack<Scope> scopes = new Stack<>();
  private FunctionType currentFunction = FunctionType.NONE;

	Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

  private enum FunctionType {
    NONE,
    FUNCTION
  }

  void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		beginScope();
		resolve(stmt.statements);
		endScope();
		return null;
	}

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    int index = declare(stmt.name);
    if (index != -1) {
      interpreter.resolve(stmt, index);
    }

    define(stmt.name);
    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
	}

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Can't return from top-level code.");
    }

		if (stmt.value != null) {
      resolve(stmt.value);
    }

    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    int index = declare(stmt.name);

    if (index != -1) {
      interpreter.resolve(stmt, index);
    }

    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }

    define(stmt.name);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    if (!scopes.isEmpty()) {
      Local local = scopes.peek().locals.get(expr.name.lexeme);

      if (local != null && !local.defined) {
        Lox.error(expr.name,
            "Can't read local variable in its own initializer.");
      }
    }

    resolveLocal(expr, expr.name);
    return null;
  }

  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  private void resolve(Expr expr) {
    expr.accept(this);
  }

  private void resolveFunction(Stmt.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;
    beginScope();
    for (Token param : function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();
    currentFunction = enclosingFunction;
  }

  private void beginScope() {
    scopes.push(new Scope());
  }

  private void endScope() {
    scopes.pop();
  }

  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      Scope scope = scopes.get(i);
      Local local = scope.locals.get(name.lexeme);

      if (local != null) {
        int distance = scopes.size() - 1 - i;
        interpreter.resolve(expr, distance, local.index);
        return;
      }
    }
  }

	@Override
	public Void visitTernaryExpr(Ternary expr) {
		throw new UnsupportedOperationException("Unimplemented method 'visitTernaryExpr'");
	}

  private static class Local {
    boolean defined;
    final int index;

    Local(int index) {
      this.index = index;
    }
  }

  private static class Scope {
    final Map<String, Local> locals = new HashMap<>();
    int nextIndex = 0;
  }

  private int declare(Token name) {
    if (scopes.isEmpty()) return -1;

    Scope scope = scopes.peek();

    if (scope.locals.containsKey(name.lexeme)) {
      Lox.error(name,
          "Already a variable with this name in this scope.");
    }

    int index = scope.nextIndex++;
    scope.locals.put(name.lexeme, new Local(index));

    return index;
  }

  private void define(Token name) {
    if (scopes.isEmpty()) return;

    scope().locals.get(name.lexeme).defined = true;
  }

  private Scope scope() {
    return scopes.peek();
  }
}