package com.bisayapp;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitPrint(Print s);
        R visitExprStmt(ExprStmt s);
        R visitVarDecl(VarDecl s);
        R visitInput(Input s);
        R visitIf(If s);
        R visitBlock(Block s);
        R visitFor(For s);
        R visitWhile(While s);
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

    public static final class Input extends Stmt {
        public final List<String> varNames;
        public Input(List<String> varNames) { this.varNames = varNames; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitInput(this); }
    }

    public static final class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch; // may be null
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        @Override public <R> R accept(Visitor<R> v) { return v.visitIf(this); }
    }

    public static final class Block extends Stmt {
        public final List<Stmt> statements;
        public Block(List<Stmt> statements) { this.statements = statements; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitBlock(this); }
    }

    public static final class For extends Stmt {
        public final Stmt initializer;  // Variable assignment (e.g., ctr=1)
        public final Expr condition;    // Loop condition (e.g., ctr<=10)
        public final Stmt update;       // Update statement (e.g., ctr++)
        public final Stmt body;         // Loop body (PUNDOK{...})
        
        public For(Stmt initializer, Expr condition, Stmt update, Stmt body) {
            this.initializer = initializer;
            this.condition = condition;
            this.update = update;
            this.body = body;
        }
        @Override public <R> R accept(Visitor<R> v) { return v.visitFor(this); }
    }

    public static final class While extends Stmt {
        public final Expr condition;    // Loop condition
        public final Stmt body;         // Loop body (PUNDOK{...})
        
        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }
        @Override public <R> R accept(Visitor<R> v) { return v.visitWhile(this); }
    }

    public abstract <R> R accept(Visitor<R> v);
}