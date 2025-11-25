package com.bisayapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the improved DAWAT statement validation including duplicate detection
 */
public class DawatImprovedValidationTest {

    @Test
    public void testDuplicateVariableInDawat_ShouldFail() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x, x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should now fail due to duplicate variable
        Parser.ParseError exception = assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testMultipleDuplicatesInDawat_ShouldFail() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y, z
                DAWAT: x, y, x, z, y
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should fail on first duplicate (second 'x')
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testNoDuplicatesInDawat_ShouldPass() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y, z
                DAWAT: x, y, z
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should pass - no duplicates
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
        });
    }

    @Test
    public void testCaseSensitiveVariables_ShouldPass() {
        String source = """
            SUGOD
                MUGNA NUMERO x, X
                DAWAT: x, X
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should pass - x and X are different variables
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
        });
    }

    @Test
    public void testSingleVariableInDawat_ShouldPass() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should pass - single variable, no duplicates possible
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
        });
    }

    @Test
    public void testDuplicateInSecondPosition_ShouldFail() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: y, x, y
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should fail when second 'y' is encountered
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }
}
