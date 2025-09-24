package com.bisayapp;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitPrint(Print s);
        R visitExprStmt(ExprStmt s);
        R visitVarDecl(VarDecl s);
    }

    public static final class Print extends Stmt {
        public final List<Expr> parts;
        public Print(List<Expr> parts) { this.parts = parts; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitPrint(this); }
    }

    public static final class ExprStmt extends Stmt {
        public final Expr expr;
        public ExprStmt(Expr expr) { this.expr = expr; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitExprStmt(this); }
    }

    public static final class VarDecl extends Stmt {
        public static final class Item {
            public final String name;
            public final Expr init; // may be null
            public Item(String name, Expr init) { this.name = name; this.init = init; }
        }
        public final TokenType type;        // NUMERO | LETRA | TINUOD | TIPIK
        public final java.util.List<Item> items;
        public VarDecl(TokenType type, java.util.List<Item> items) { this.type = type; this.items = items; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitVarDecl(this); }
    }

    public abstract <R> R accept(Visitor<R> v);
}