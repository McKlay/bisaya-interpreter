package com.bisayapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for validating that DAWAT statement catches undeclared variables at parse time
 */
public class UndeclaredVariableInDawatTest {

    @Test
    public void testUndeclaredVariableInDawat() {
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

        // Should throw a ParseError for undeclared variable
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDeclaredVariableInDawat() {
        String source = """
            SUGOD
                MUGNA TIPIK x
                DAWAT: x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should NOT throw an error - variable is declared
        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
            assertEquals(3, program.size()); // varDecl, input, print
        });
    }

    @Test
    public void testMultipleVariablesInDawat_AllDeclared() {
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

        assertDoesNotThrow(() -> {
            List<Stmt> program = parser.parseProgram();
            assertNotNull(program);
        });
    }

    @Test
    public void testMultipleVariablesInDawat_OneUndeclared() {
        String source = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x, y, z
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should throw error because 'z' is not declared
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDawatBeforeDeclaration() {
        String source = """
            SUGOD
                DAWAT: x
                MUGNA NUMERO x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Should throw error because variable is used before declaration
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }
}
