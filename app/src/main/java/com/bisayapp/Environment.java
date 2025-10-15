package com.bisayapp;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, TokenType> types = new HashMap<>();

    public void define(String name, Object value) { values.put(name, value); }

    // declare with type
    public void declare(String name, TokenType type, Object value) {
        // Check if variable is already declared
        if (types.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' is already declared.");
        }
        types.put(name, type);
        values.put(name, coerce(type, value));
    }

    // get declared type (or null)
    public TokenType getType(String name) { return types.get(name); }
    public boolean isDeclared(String name) { return types.containsKey(name); }

    public void assign(String name, Object value) {
        if (!values.containsKey(name)) throw new RuntimeException("Undefined variable '" + name + "'");
        TokenType t = types.get(name);
        values.put(name, t == null ? value : coerce(t, value));
    }

    public Object get(String name) {
        if (!values.containsKey(name)) throw new RuntimeException("Undefined variable '" + name + "'");
        return values.get(name);
    }

    public boolean isDefined(String name) { return values.containsKey(name); }

    // simple coercion for Increment 1
    private Object coerce(TokenType t, Object v) {
        if (v == null) return null;
        switch (t) {
            case NUMERO -> {
                // TODO: Fixed - NUMERO should reject decimal values
                if (v instanceof Double d) {
                    if (d != d.intValue()) {
                        throw new RuntimeException("Type error: NUMERO cannot have decimal values. Use TIPIK for decimal numbers. Got: " + d);
                    }
                    return Integer.valueOf(d.intValue());
                }
                if (v instanceof Number n) return Integer.valueOf(n.intValue());
                if (v instanceof String s && s.matches("-?\\d+")) return Integer.valueOf(s);
                if (v instanceof String s && s.matches("-?\\d+\\.\\d+")) {
                    throw new RuntimeException("Type error: NUMERO cannot have decimal values. Use TIPIK for decimal numbers. Got: " + s);
                }
            }
            case TIPIK -> {
                if (v instanceof Number n) return Double.valueOf(n.doubleValue());
                if (v instanceof String s && s.matches("-?\\d+(\\.\\d+)?")) return Double.valueOf(s);
            }
            case LETRA -> {
                if (v instanceof Character c) return c;
                // TODO: Fixed - LETRA must be exactly 1 character
                if (v instanceof String s) {
                    if (s.length() == 0) {
                        throw new RuntimeException("Type error: LETRA cannot be empty - must be exactly one character");
                    }
                    if (s.length() > 1) {
                        throw new RuntimeException("Type error: LETRA can only hold one character, got: " + s);
                    }
                    return s.charAt(0);
                }
            }
            case TINUOD -> {
                // per spec: "OO" true, "DILI" false
                if (v instanceof Boolean b) return b;
                if (v instanceof String s) {
                    if (s.equals("OO")) return Boolean.TRUE;
                    if (s.equals("DILI")) return Boolean.FALSE;
                }
            }
            default -> { /* no-op */ }
        }
        throw new RuntimeException("Type error: cannot assign " + v + " to " + t);
    }
}
