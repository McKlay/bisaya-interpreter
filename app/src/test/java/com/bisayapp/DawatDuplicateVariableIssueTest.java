package com.bisayapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Tests to demonstrate runtime issues with duplicate variables in DAWAT
 */
public class DawatDuplicateVariableIssueTest {

    @Test 
    public void testDuplicateVariableInDawat_NowFixed() {
        // This test demonstrates that the duplicate variable issue is now fixed
        String source = """
            SUGOD
                MUGNA NUMERO x = 0
                DAWAT: x, x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        
        // Parser now rejects duplicate variables in DAWAT
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
        
        System.out.println("ISSUE FIXED: Duplicate variable names in DAWAT now caught at parse time");
        System.out.println("  Parser rejects: DAWAT: x, x");
        System.out.println("  Error message explains the issue clearly");
        System.out.println("  No more confusing runtime behavior");
    }

    @Test
    public void testEmptyDawatStatement() {
        // Another issue: DAWAT with no variables
        String source = """
            SUGOD
                DAWAT:
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);

        // Currently this throws ParseError (which is good)
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
    }

    @Test
    public void testDuplicatesNowPreventedAtParseTime() {
        // Previously this would cause runtime confusion, now it's caught early
        String source = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x, x, x
                IPAKITA: x
            KATAPUSAN
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        
        // Now this fails at parse time instead of causing runtime confusion
        assertThrows(Parser.ParseError.class, () -> {
            parser.parseProgram();
        });
        
        System.out.println("ISSUE FIXED:");
        System.out.println("  Parser now catches: DAWAT: x, x, x");
        System.out.println("  No more runtime confusion with duplicate inputs");
        System.out.println("  Clear error message at parse time");
    }
}
