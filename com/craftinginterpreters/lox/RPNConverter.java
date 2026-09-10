package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Grouping;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Expr.Unary;

public class RPNConverter implements Expr.Visitor<String>{

    @Override
    public String visitBinaryExpr(Binary expr) {
        return null;
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return null;
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return null;
    }
    
}
