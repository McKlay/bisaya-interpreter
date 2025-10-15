package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class VariableRedeclarationTest {
    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter(System.out);
    }

    @Test
    void testVariableRedeclarationThrowsError() {
        String source = """
            SUGOD
                MUGNA TIPIK x = 5.5
                MUGNA NUMERO x = 9
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        var statements = parser.parseProgram();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interpreter.interpret(statements);
        });

        assertEquals("Variable 'x' is already declared.", exception.getMessage());
    }

    @Test
    void testVariableRedeclarationWithSameType() {
        String source = """
            SUGOD
                MUGNA NUMERO x = 5
                MUGNA NUMERO x = 9
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        var statements = parser.parseProgram();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interpreter.interpret(statements);
        });

        assertEquals("Variable 'x' is already declared.", exception.getMessage());
    }

    @Test
    void testVariableRedeclarationWithDifferentNames() {
        // This should work fine - different variable names
        String source = """
            SUGOD
                MUGNA TIPIK x = 5.5
                MUGNA NUMERO y = 9
                IPAKITA: x & " " & y
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        var statements = parser.parseProgram();

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            interpreter.interpret(statements);
        });
    }

    @Test
    void testVariableAssignmentAfterDeclaration() {
        // This should work fine - assignment after declaration is allowed
        String source = """
            SUGOD
                MUGNA NUMERO x = 5
                x = 10
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        var statements = parser.parseProgram();

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            interpreter.interpret(statements);
        });
    }

    @Test
    void testMultipleVariableDeclarationInSameStatement() {
        // Test declaring multiple variables in one MUGNA statement
        String source = """
            SUGOD
                MUGNA NUMERO x = 5, y = 10
                IPAKITA: x & " " & y
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        var statements = parser.parseProgram();

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            interpreter.interpret(statements);
        });
    }

    @Test
    void testRedeclarationOfOneVariableInMultipleDeclaration() {
        // Test redeclaring one of the variables from a multiple declaration
        String source = """
            SUGOD
                MUGNA NUMERO x = 5, y = 10
                MUGNA TIPIK x = 3.14
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        var statements = parser.parseProgram();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interpreter.interpret(statements);
        });

        assertEquals("Variable 'x' is already declared.", exception.getMessage());
    }
}
