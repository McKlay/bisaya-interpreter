package com.bisayapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Comprehensive edge case testing for DAWAT statement to identify additional issues
 */
public class DawatEdgeCasesTest {

    @Test
    public void testDawatWithReservedWords() {
        // Should not allow reserved words as variable names in DAWAT
        String source = """
            SUGOD
                DAWAT: SUGOD
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // This should fail at lexer level since SUGOD is a reserved word
        // But let's see what happens
        assertThrows(Exception.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithNoColon() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT x
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
    public void testDawatWithNoVariables() {
        String source = """
            SUGOD
                DAWAT:
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
    public void testDawatWithTrailingComma() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x, y,
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
    public void testDawatWithExtraTokensAfter() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x extra tokens here
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
    public void testDawatWithDuplicateVariables() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x, x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should now fail due to duplicate variable detection
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithNumbersAsVariableNames() {
        String source = """
            SUGOD
                DAWAT: 123
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
    public void testDawatWithStringLiterals() {
        String source = """
            SUGOD
                DAWAT: "hello"
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
    public void testDawatWithMixedValidInvalid() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x, undeclared_var
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should catch the undeclared variable
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatWithValidVariablesButWrongSyntax() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x y z
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Missing commas between variables
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatInsideComments() {
        String source = """
            SUGOD
                MUGNA NUMERO x
                -- DAWAT: x this should be ignored
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should parse successfully since DAWAT is in comment
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
            assertEquals(2, program.size()); // varDecl, print (no input stmt)
        });
    }

    @Test
    public void testMultipleDawatStatements() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x
                DAWAT: y
                IPAKITA: x & y
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Multiple DAWAT statements should be allowed
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
            assertEquals(4, program.size()); // varDecl, input, input, print
        });
    }

    @Test
    public void testDawatWithDifferentDataTypes() {
        String source = """
            SUGOD
                MUGNA NUMERO num
                MUGNA LETRA ch
                MUGNA TINUOD flag
                MUGNA TIPIK decimal
                DAWAT: num, ch, flag, decimal
                IPAKITA: num
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should work fine - parser doesn't care about types
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
        });
    }
}
