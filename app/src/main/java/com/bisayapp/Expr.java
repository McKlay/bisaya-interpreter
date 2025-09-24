package com.bisayapp;

public abstract class Expr {
    public interface Visitor<R> {
        R visitLiteral(Literal e);
        R visitVariable(Variable e);
        R visitAssign(Assign e);
        R visitBinary(Binary e);
    }

    public static final class Literal extends Expr {
        public final Object value;
        public Literal(Object value) { this.value = value; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitLiteral(this); }
    }

    public static final class Variable extends Expr {
        public final String name;
        public Variable(String name) { this.name = name; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitVariable(this); }
    }

    public static final class Assign extends Expr {
        public final String name;
        public final Expr value;
        public Assign(String name, Expr value) { this.name = name; this.value = value; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitAssign(this); }
    }

    public static final class Binary extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;
        public Binary(Expr left, Token operator, Expr right) { 
            this.left = left; this.operator = operator; this.right = right; 
        }
        @Override public <R> R accept(Visitor<R> v) { return v.visitBinary(this); }
    }

    public abstract <R> R accept(Visitor<R> v);
}
