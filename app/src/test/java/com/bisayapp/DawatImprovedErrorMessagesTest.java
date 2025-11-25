package com.bisayapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Tests for error messages from improved DAWAT validation
 */
public class DawatImprovedErrorMessagesTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
        ErrorReporter.reset();
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
        ErrorReporter.reset();
    }

    @Test
    public void testDuplicateVariableErrorMessage() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x, y, x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });

        String errorOutput = errContent.toString();
        System.out.println("Duplicate variable error message:");
        System.out.println(errorOutput);
        
        assertTrue(errorOutput.contains("Duplicate variable"), 
            "Error should mention duplicate variable");
        assertTrue(errorOutput.contains("x"), 
            "Error should mention the specific variable name");
        assertTrue(errorOutput.contains("should appear only once"), 
            "Error should explain the constraint");
    }

    @Test
    public void testUndeclaredVariableErrorMessage() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x, undeclared_var
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });

        String errorOutput = errContent.toString();
        System.out.println("Undeclared variable error message:");
        System.out.println(errorOutput);
        
        assertTrue(errorOutput.contains("Undefined variable"), 
            "Error should mention undefined variable");
        assertTrue(errorOutput.contains("undeclared_var"), 
            "Error should mention the specific variable name");
        assertTrue(errorOutput.contains("MUGNA"), 
            "Error should mention how to fix it");
    }
}
