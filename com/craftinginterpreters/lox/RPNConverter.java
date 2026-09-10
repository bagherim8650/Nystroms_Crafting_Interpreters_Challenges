package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Grouping;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Expr.Unary;

/**
 * Converts Lox expression trees into Reverse Polish Notation (RPN).
 */
public class RPNConverter implements Expr.Visitor<String> {

    /**
     * Prints an expression in RPN format.
     */
    public String print(Expr expr) {
        return expr.accept(this);
    }

    /**
     * Converts an expression into RPN format.
     */
    public String convert(Expr expr) {
        return expr.accept(this);
    }

    /**
     * Visits a binary expression by printing the left operand,
     * the right operand, and then the operator.
     */
    @Override
    public String visitBinaryExpr(Binary expr) {
        return print(expr.left) + ' ' + print(expr.right) + expr.operator.lexeme;
    }

    /**
     * Visits a grouping expression and ignores the parentheses.
     */
    @Override
    public String visitGroupingExpr(Grouping expr) {
        return print(expr.expression);
    }

    /**
     * Visits a literal value.
     */
    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value == null) {
            return "nil";
        } else {
            return expr.value.toString();
        }
    }

    /**
     * Visits a unary expression by printing its operand followed by the operator.
     */
    @Override
    public String visitUnaryExpr(Unary expr) {
        return print(expr.right) + ' ' + expr.operator.lexeme;
    }

    /**
     * Runs sample expressions to demonstrate the converter.
     */
    public static void main(String[] args) {
        RPNConverter converter = new RPNConverter();

        // Operators used to construct the sample expression trees.
        Token plus = new Token(TokenType.PLUS, "+", null, 1);
        Token minus = new Token(TokenType.MINUS, "-", null, 1);
        Token star = new Token(TokenType.STAR, "*", null, 1);

        // Test 1: 1
        Expr e1 = new Expr.Literal(1);
        System.out.println("1 -> " + converter.convert(e1));

        // Test 2: 1 + 2
        Expr e2 = new Expr.Binary(
            new Expr.Literal(1),
            plus,
            new Expr.Literal(2));
        System.out.println("1 + 2 -> " + converter.convert(e2));

        // Test 3: -1
        Expr e3 = new Expr.Unary(minus, new Expr.Literal(1));
        System.out.println("-1 -> " + converter.convert(e3));

        // Test 4: (1 + 2) * (4 - 3)
        Expr e4 = new Expr.Binary(
            new Expr.Grouping(new Expr.Binary(
                new Expr.Literal(1), plus, new Expr.Literal(2))),
            star,
            new Expr.Grouping(new Expr.Binary(
                new Expr.Literal(4), minus, new Expr.Literal(3))));
        System.out.println("(1 + 2) * (4 - 3) -> " + converter.convert(e4));

        // Test 5: 1 + 2 * 3 - 4
        Expr e5 = new Expr.Binary(
            new Expr.Binary(
                new Expr.Literal(1),
                plus,
                new Expr.Binary(
                    new Expr.Literal(2), star, new Expr.Literal(3))),
            minus,
            new Expr.Literal(4));
        System.out.println("1 + 2 * 3 - 4 -> " + converter.convert(e5));
    }
}