package com.bisayapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests to identify specific issues with DAWAT statement validation
 */
public class DawatSpecificIssuesTest {

    @Test
    public void testDawatWithDuplicateVariables_NowFixed() {
        // Testing that duplicate variables in DAWAT are now properly rejected
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

        // Now properly rejected by parser
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithEmptyVariableList() {
        // What happens if there's DAWAT: (no variables)?
        String source = """
            SUGOD
                DAWAT:
                IPAKITA: "hello"
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should this be allowed? DAWAT with no variables is meaningless
        Exception exception = assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithTrailingComma() {
        // Testing trailing comma: DAWAT: x, y,
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x, y,
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should fail - trailing comma is bad syntax
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testMultipleColonsInDawat() {
        // What about DAWAT:: x or DAWAT: x: y
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT:: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithAssignmentOperator() {
        // Testing DAWAT: x = 5 (should not be allowed)
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x = 5
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatCaseSensitivity() {
        // Testing case sensitivity - should X and x be different?
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: X
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should fail - X is not the same as x
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithSameVariableDifferentCase() {
        // Testing if we can have both x and X
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

        // Should work - x and X are different variables
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
        });
    }
}
