package com.bisayapp;

import java.io.PrintStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final Environment env = new Environment();
    private final PrintStream out;
    private final InputStream in;
    private Scanner scanner;

    public Interpreter(PrintStream out) {
        this(out, System.in);
    }

    public Interpreter(PrintStream out, InputStream in) {
        this.out = out;
        this.in = in;
        this.scanner = new Scanner(in);
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
        // No automatic newline - user must explicitly use $ for newlines
        out.print(sb.toString());
        return null;
    }

    @Override
    public Void visitInput(Stmt.Input s) {
        String line = scanner.nextLine().trim();
        String[] values = line.split(",");
        
        if (values.length != s.varNames.size()) {
            throw new RuntimeException("DAWAT expects " + s.varNames.size() + 
                " value(s), but got " + values.length);
        }
        
        for (int i = 0; i < s.varNames.size(); i++) {
            String varName = s.varNames.get(i);
            String inputValue = values[i].trim();
            
            // Check if variable exists - MUST check before getType()
            if (!env.isDeclared(varName)) {
                throw new RuntimeException("Undefined variable '" + varName + 
                    "'. Variables must be declared with MUGNA before using DAWAT.");
            }
            
            // Get the variable's type (safe now, we know it exists)
            TokenType type = env.getType(varName);
            
            // Validate we have a non-null type
            if (type == null) {
                throw new RuntimeException("Internal error: Variable '" + varName + 
                    "' exists but has no type information.");
            }
            
            // Parse and validate the input value
            Object value = parseInputValue(inputValue, type, varName);
            env.assign(varName, value);
        }
        
        return null;
    }

    private Object parseInputValue(String input, TokenType type, String varName) {
        // Check for empty input first
        if (input.isEmpty()) {
            throw new RuntimeException("DAWAT: Empty input for variable '" + varName + 
                "' of type " + type);
        }
        
        try {
            switch (type) {
                case NUMERO -> {
                    // Parse as integer
                    if (input.contains(".")) {
                        throw new RuntimeException("DAWAT: NUMERO cannot have decimal values. Got: " + input);
                    }
                    return Integer.valueOf(input);
                }
                case TIPIK -> {
                    // Parse as double
                    return Double.valueOf(input);
                }
                case LETRA -> {
                    // Must be single character
                    if (input.length() != 1) {
                        throw new RuntimeException("DAWAT: LETRA must be exactly one character. Got: '" + input + "' (length: " + input.length() + ")");
                    }
                    return input.charAt(0);
                }
                case TINUOD -> {
                    // Must be "OO" or "DILI"
                    if (input.equals("OO")) return true;
                    if (input.equals("DILI")) return false;
                    throw new RuntimeException("DAWAT: TINUOD must be 'OO' or 'DILI'. Got: " + input);
                }
                default -> throw new RuntimeException("DAWAT: Unknown type: " + type);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("DAWAT: Invalid " + type + " value: '" + input + "'");
        }
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

    @Override
    public Void visitIf(Stmt.If s) {
        Object conditionValue = eval(s.condition);
        
        if (isTruthy(conditionValue)) {
            execute(s.thenBranch);
        } else if (s.elseBranch != null) {
            execute(s.elseBranch);
        }
        
        return null;
    }

    @Override
    public Void visitBlock(Stmt.Block s) {
        for (Stmt stmt : s.statements) {
            execute(stmt);
        }
        return null;
    }

    @Override
    public Void visitFor(Stmt.For s) {
        // Execute initializer once
        if (s.initializer != null) {
            execute(s.initializer);
        }
        
        // Loop while condition is true
        while (isTruthy(eval(s.condition))) {
            // Execute body
            execute(s.body);
            
            // Execute update
            if (s.update != null) {
                execute(s.update);
            }
        }
        
        return null;
    }

    @Override
    public Void visitWhile(Stmt.While s) {
        // Loop while condition is true
        while (isTruthy(eval(s.condition))) {
            // Execute body
            execute(s.body);
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
        // Require variables to be declared before assignment
        if (!env.isDeclared(e.name)) {
            throw new RuntimeException("Undefined variable '" + e.name + "'. Variables must be declared with MUGNA before assignment.");
        }
        env.assign(e.name, v);
        return v;
    }

    @Override
    public Object visitBinary(Expr.Binary e) {
        Object left = eval(e.left);
        
        // Handle short-circuit operators FIRST, before evaluating right operand
        // This prevents unnecessary evaluation and potential runtime errors
        if (e.operator.type == TokenType.UG) { // AND operator
            // Both operands must be boolean
            boolean leftBool = requireBoolean(left, e.operator, "UG operator (AND)");
            // If left is false, result is always false - don't evaluate right
            if (!leftBool) return false;
            // Only evaluate right if left is true
            Object right = eval(e.right);
            boolean rightBool = requireBoolean(right, e.operator, "UG operator (AND)");
            return rightBool;
        }
        
        if (e.operator.type == TokenType.O) { // OR operator
            // Both operands must be boolean
            boolean leftBool = requireBoolean(left, e.operator, "O operator (OR)");
            // If left is true, result is always true - don't evaluate right
            if (leftBool) return true;
            // Only evaluate right if left is false
            Object right = eval(e.right);
            boolean rightBool = requireBoolean(right, e.operator, "O operator (OR)");
            return rightBool;
        }
        
        // For all other operators, evaluate right operand normally
        Object right = eval(e.right);
        
        switch (e.operator.type) {
            case AMPERSAND:
                return stringify(left) + stringify(right);
            
            // Arithmetic operators
            case PLUS:
                return addNumbers(left, right, e.operator);
            case MINUS:
                return subtractNumbers(left, right, e.operator);
            case STAR:
                return multiplyNumbers(left, right, e.operator);
            case SLASH:
                return divideNumbers(left, right, e.operator);
            case PERCENT:
                return moduloNumbers(left, right, e.operator);
            
            // Comparison operators
            case GREATER:
                return compareNumbers(left, right, e.operator) > 0;
            case GREATER_EQUAL:
                return compareNumbers(left, right, e.operator) >= 0;
            case LESS:
                return compareNumbers(left, right, e.operator) < 0;
            case LESS_EQUAL:
                return compareNumbers(left, right, e.operator) <= 0;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case LT_GT:
                return !isEqual(left, right);
            
            // Note: UG (AND) and O (OR) are handled above with short-circuit evaluation
            
            default:
                throw runtimeError(e.operator, "Unsupported binary operator: " + e.operator.lexeme);
        }
    }

    @Override
    public Object visitUnary(Expr.Unary e) {
        Object operand = eval(e.operand);
        
        switch (e.operator.type) {
            case MINUS:
                // Negative operator
                Number num = requireNumber(operand, e.operator);
                if (num instanceof Integer) {
                    return -num.intValue();
                }
                return -num.doubleValue();
            
            case MINUS_MINUS:
                // Decrement operator
                if (e.operand instanceof Expr.Variable var) {
                    Number n = requireNumber(operand, e.operator);
                    Object result;
                    if (n instanceof Integer) {
                        result = n.intValue() - 1;
                    } else {
                        result = n.doubleValue() - 1.0;
                    }
                    env.assign(var.name, result);
                    return result;
                }
                throw runtimeError(e.operator, "Decrement operator requires a variable.");
            
            case PLUS:
                // Positive operator (unary +)
                return requireNumber(operand, e.operator);
            
            case PLUS_PLUS:
                // Increment operator
                if (e.operand instanceof Expr.Variable var) {
                    Number n = requireNumber(operand, e.operator);
                    Object result;
                    if (n instanceof Integer) {
                        result = n.intValue() + 1;
                    } else {
                        result = n.doubleValue() + 1.0;
                    }
                    env.assign(var.name, result);
                    return result;
                }
                throw runtimeError(e.operator, "Increment operator requires a variable.");
            
            case DILI: // NOT
                boolean bool = requireBoolean(operand, e.operator, "DILI operator (NOT)");
                return !bool;
            
            default:
                throw runtimeError(e.operator, "Unsupported unary operator: " + e.operator.lexeme);
        }
    }

    @Override
    public Object visitPostfix(Expr.Postfix e) {
        Object operand = eval(e.operand);
        
        switch (e.operator.type) {
            case PLUS_PLUS:
                // Postfix increment: return old value, then increment
                if (e.operand instanceof Expr.Variable var) {
                    Number n = requireNumber(operand, e.operator);
                    Object oldValue = operand;
                    Object newValue;
                    if (n instanceof Integer) {
                        newValue = n.intValue() + 1;
                    } else {
                        newValue = n.doubleValue() + 1.0;
                    }
                    env.assign(var.name, newValue);
                    return oldValue; // Return old value for postfix
                }
                throw runtimeError(e.operator, "Postfix increment operator requires a variable.");
            
            case MINUS_MINUS:
                // Postfix decrement: return old value, then decrement
                if (e.operand instanceof Expr.Variable var) {
                    Number n = requireNumber(operand, e.operator);
                    Object oldValue = operand;
                    Object newValue;
                    if (n instanceof Integer) {
                        newValue = n.intValue() - 1;
                    } else {
                        newValue = n.doubleValue() - 1.0;
                    }
                    env.assign(var.name, newValue);
                    return oldValue; // Return old value for postfix
                }
                throw runtimeError(e.operator, "Postfix decrement operator requires a variable.");
            
            default:
                throw runtimeError(e.operator, "Unsupported postfix operator: " + e.operator.lexeme);
        }
    }

    @Override
    public Object visitGrouping(Expr.Grouping e) {
        return eval(e.expression);
    }

    // --- Helper methods ---
    
    /**
     * Creates a runtime error with line and column information.
     * This provides professional error messages that help users locate issues.
     */
    private RuntimeException runtimeError(Token token, String message) {
        return new RuntimeException("[line " + token.line + " col " + token.col + "] " + message);
    }
    
    /**
     * Ensures a value is a number, throwing an error with location if not.
     */
    private Number requireNumber(Object value, Token operator) {
        if (value instanceof Number n) return n;
        throw runtimeError(operator, "Operand must be a number for operator '" + operator.lexeme + "'. Got: " + getTypeName(value));
    }
    
    /**
     * Ensures a value is a boolean, throwing an error with location if not.
     */
    private boolean requireBoolean(Object value, Token token, String context) {
        if (value instanceof Boolean b) return b;
        if (value instanceof String s && (s.equals("OO") || s.equals("DILI"))) {
            return s.equals("OO");
        }
        throw runtimeError(token, context + " requires a boolean value (OO or DILI). Got: " + getTypeName(value));
    }
    
    /**
     * Returns the type name of a value for error messages.
     */
    private String getTypeName(Object value) {
        if (value == null) return "null";
        if (value instanceof Integer) return "NUMERO";
        if (value instanceof Double) return "TIPIK";
        if (value instanceof Character) return "LETRA";
        if (value instanceof Boolean) return "TINUOD";
        if (value instanceof String) return "text";
        return value.getClass().getSimpleName();
    }

    private Object addNumbers(Object left, Object right, Token operator) {
        Number l = requireNumber(left, operator);
        Number r = requireNumber(right, operator);
        
        if (l instanceof Integer && r instanceof Integer) {
            return l.intValue() + r.intValue();
        }
        return l.doubleValue() + r.doubleValue();
    }

    private Object subtractNumbers(Object left, Object right, Token operator) {
        Number l = requireNumber(left, operator);
        Number r = requireNumber(right, operator);
        
        if (l instanceof Integer && r instanceof Integer) {
            return l.intValue() - r.intValue();
        }
        return l.doubleValue() - r.doubleValue();
    }

    private Object multiplyNumbers(Object left, Object right, Token operator) {
        Number l = requireNumber(left, operator);
        Number r = requireNumber(right, operator);
        
        if (l instanceof Integer && r instanceof Integer) {
            return l.intValue() * r.intValue();
        }
        return l.doubleValue() * r.doubleValue();
    }

    private Object divideNumbers(Object left, Object right, Token operator) {
        Number l = requireNumber(left, operator);
        Number r = requireNumber(right, operator);
        
        if (r.doubleValue() == 0.0) {
            throw runtimeError(operator, "Division by zero.");
        }
        
        if (l instanceof Integer && r instanceof Integer) {
            return l.intValue() / r.intValue();
        }
        return l.doubleValue() / r.doubleValue();
    }

    private Object moduloNumbers(Object left, Object right, Token operator) {
        Number l = requireNumber(left, operator);
        Number r = requireNumber(right, operator);
        
        if (r.doubleValue() == 0.0) {
            throw runtimeError(operator, "Modulo by zero.");
        }
        
        if (l instanceof Integer && r instanceof Integer) {
            return l.intValue() % r.intValue();
        }
        return l.doubleValue() % r.doubleValue();
    }

    private int compareNumbers(Object left, Object right, Token operator) {
        Number l = requireNumber(left, operator);
        Number r = requireNumber(right, operator);
        
        return Double.compare(l.doubleValue(), r.doubleValue());
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        
        // Handle numeric comparisons (Integer vs Double, etc.)
        if (left instanceof Number && right instanceof Number) {
            Number l = (Number) left;
            Number r = (Number) right;
            return Double.compare(l.doubleValue(), r.doubleValue()) == 0;
        }
        
        return left.equals(right);
    }

    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return s.equals("OO");
        return true;
    }

    private String stringify(Object v) {
        if (v == null) return "null";
        if (v instanceof Double d) {
            if (d == d.intValue()) return String.valueOf(d.intValue());
            return v.toString();
        }
        if (v instanceof Boolean b) {
            return b ? "OO" : "DILI";
        }
        return v.toString();
    }
}
