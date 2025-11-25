package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the error messages for undeclared variables in DAWAT
 */
public class DawatErrorMessageTest {

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
    public void testErrorMessageForUndeclaredVariable() {
        String source = """
            SUGOD
                MUGNA TIPIK result = 5.8
                DAWAT: x
                IPAKITA: result
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        try {
            parser.parseProgram();
            fail("Expected ParseError to be thrown");
        } catch (Parser.ParseError e) {
            // Expected - verify error was reported
            assertTrue(ErrorReporter.hadError(), "Error should have been reported");
            
            String errorOutput = errContent.toString();
            
            // Verify error message contains key information
            assertTrue(errorOutput.contains("Undefined variable 'x'"),
                "Error message should mention the undefined variable 'x'");
            assertTrue(errorOutput.contains("MUGNA"),
                "Error message should mention MUGNA keyword");
            assertTrue(errorOutput.contains("DAWAT"),
                "Error message should mention DAWAT keyword");
            
            System.out.println("Error message output:");
            System.out.println(errorOutput);
        }
    }

    @Test
    public void testErrorMessageShowsCorrectVariable() {
        String source = """
            SUGOD
                MUGNA NUMERO a, b
                DAWAT: a, b, c, d
                IPAKITA: a
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        try {
            parser.parseProgram();
            fail("Expected ParseError to be thrown");
        } catch (Parser.ParseError e) {
            String errorOutput = errContent.toString();
            
            // Should report 'c' as the first undeclared variable
            assertTrue(errorOutput.contains("Undefined variable 'c'"),
                "Error should identify 'c' as the first undeclared variable");
            
            System.out.println("Error message for multiple variables:");
            System.out.println(errorOutput);
        }
    }

    @Test
    public void testErrorMessageForDawatBeforeDeclaration() {
        String source = """
            SUGOD
                DAWAT: value
                MUGNA NUMERO value
                IPAKITA: value
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        try {
            parser.parseProgram();
            fail("Expected ParseError to be thrown");
        } catch (Parser.ParseError e) {
            String errorOutput = errContent.toString();
            
            assertTrue(errorOutput.contains("Undefined variable 'value'"),
                "Error should catch forward reference");
            
            System.out.println("Error message for forward reference:");
            System.out.println(errorOutput);
        }
    }
}
