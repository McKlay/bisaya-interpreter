package com.bisayapp;

public abstract class Expr {
    public interface Visitor<R> {
        R visitLiteral(Literal e);
        R visitVariable(Variable e);
        R visitAssign(Assign e);
        R visitBinary(Binary e);
        R visitUnary(Unary e);
        R visitPostfix(Postfix e);
        R visitGrouping(Grouping e);
    }

    public static final class Literal extends Expr {
        public final Object value;
        public Literal(Object value) { this.value = value; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitLiteral(this); }
    }

    public static final class Variable extends Expr {
        public final Token token;
        public final String name;
        public Variable(Token token, String name) { 
            this.token = token;
            this.name = name; 
        }
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

    public static final class Unary extends Expr {
        public final Token operator;
        public final Expr operand;
        public Unary(Token operator, Expr operand) {
            this.operator = operator; this.operand = operand;
        }
        @Override public <R> R accept(Visitor<R> v) { return v.visitUnary(this); }
    }

    public static final class Postfix extends Expr {
        public final Expr operand;
        public final Token operator;
        public Postfix(Expr operand, Token operator) {
            this.operand = operand; this.operator = operator;
        }
        @Override public <R> R accept(Visitor<R> v) { return v.visitPostfix(this); }
    }

    public static final class Grouping extends Expr {
        public final Expr expression;
        public Grouping(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> v) { return v.visitGrouping(this); }
    }

    public abstract <R> R accept(Visitor<R> v);
}
