package com.bisayapp;

import java.io.PrintStream;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final Environment env = new Environment();
    private final PrintStream out;

    public Interpreter(PrintStream out) {
        this.out = out;
    }

    public void interpret(List<Stmt> program) {
        for (Stmt s : program) execute(s);
    }

    private void execute(Stmt s) { s.accept(this); }
    private Object eval(Expr e) { return e.accept(this); }

    // --- Stmt ---
    @Override
    public Void visitPrint(Stmt.Print s) {
        StringBuilder sb = new StringBuilder();
        for (Expr e : s.parts) sb.append(stringify(eval(e)));
        String output = sb.toString();
        
        // Add newline if the output doesn't already end with one
        if (!output.endsWith("\n")) {
            output += "\n";
        }
        
        out.print(output);
        return null;
    }

    @Override
    public Void visitExprStmt(Stmt.ExprStmt s) {
        eval(s.expr);
        return null;
    }

    @Override
    public Void visitVarDecl(Stmt.VarDecl s) {
        for (Stmt.VarDecl.Item it : s.items) {
            Object value = (it.init == null) ? null : eval(it.init);
            env.declare(it.name, s.type, value);
        }
        return null;
    }

    // --- Expr ---
    @Override
    public Object visitLiteral(Expr.Literal e) { return e.value; }

    @Override
    public Object visitVariable(Expr.Variable e) {
        Object v = env.get(e.name);
        TokenType t = env.getType(e.name);
        if (t == TokenType.TINUOD && v instanceof Boolean b) {
            return b ? "OO" : "DILI";
        }
        return v;
    }

    @Override
    public Object visitAssign(Expr.Assign e) {
        Object v = eval(e.value);
        // TODO: Fixed - Require variables to be declared before assignment
        if (!env.isDeclared(e.name)) {
            throw new RuntimeException("Undefined variable '" + e.name + "'. Variables must be declared with MUGNA before assignment.");
        }
        env.assign(e.name, v);
        return v;
    }

    @Override
    public Object visitBinary(Expr.Binary e) {
        Object left = eval(e.left);
        Object right = eval(e.right);
        
        if (e.operator.type == TokenType.AMPERSAND) {
            return stringify(left) + stringify(right);
        }
        
        throw new RuntimeException("Unsupported binary operator: " + e.operator.lexeme);
    }

    // --- helpers ---
    private String stringify(Object v) {
        if (v == null) return "null";
        if (v instanceof Double d) {
            if (d == d.intValue()) return String.valueOf(d.intValue());
            return v.toString();
        }
        return v.toString();
    }
}
