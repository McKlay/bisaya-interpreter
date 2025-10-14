package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class PostfixAndCommentsTest {

    // Helper methods for testing
    private String runProgram(String program) {
        Lexer lexer = new Lexer(program);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parseProgram();
        
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        java.io.PrintStream printStream = new java.io.PrintStream(outputStream);
        
        Interpreter interpreter = new Interpreter(printStream);
        interpreter.interpret(statements);
        
        return outputStream.toString().trim();
    }
    
    // ========================================================================================
    // POSTFIX INCREMENT/DECREMENT TESTS
    // ========================================================================================
    
    @Test
    @DisplayName("Postfix increment: x++")
    public void testPostfixIncrement() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 5
            IPAKITA: x++ & " " & x
            KATAPUSAN
            """;
        
        String expected = "5 6";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Postfix decrement: x--")
    public void testPostfixDecrement() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 10
            IPAKITA: x-- & " " & x
            KATAPUSAN
            """;
        
        String expected = "10 9";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Prefix vs postfix increment difference")
    public void testPrefixVsPostfixIncrement() {
        String program = """
            SUGOD
            MUGNA NUMERO a = 5, b = 5
            IPAKITA: "Prefix: " & ++a & " Final a: " & a & $
            IPAKITA: "Postfix: " & b++ & " Final b: " & b
            KATAPUSAN
            """;
        
        String expected = "Prefix: 6 Final a: 6\nPostfix: 5 Final b: 6";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Prefix vs postfix decrement difference")
    public void testPrefixVsPostfixDecrement() {
        String program = """
            SUGOD
            MUGNA NUMERO a = 10, b = 10
            IPAKITA: "Prefix: " & --a & " Final a: " & a & $
            IPAKITA: "Postfix: " & b-- & " Final b: " & b
            KATAPUSAN
            """;
        
        String expected = "Prefix: 9 Final a: 9\nPostfix: 10 Final b: 9";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Multiple postfix operations")
    public void testMultiplePostfixOperations() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 0
            IPAKITA: x++ & " " & x++ & " " & x
            KATAPUSAN
            """;
        
        String expected = "0 1 2";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Postfix with TIPIK (decimal)")
    public void testPostfixWithDecimal() {
        String program = """
            SUGOD
            MUGNA TIPIK x = 5.5
            IPAKITA: x++ & " " & x
            KATAPUSAN
            """;
        
        String expected = "5.5 6.5";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Postfix in expressions")
    public void testPostfixInExpressions() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 5, result
            result = x++ * 2
            IPAKITA: "Result: " & result & " x: " & x
            KATAPUSAN
            """;
        
        String expected = "Result: 10 x: 6";
        assertEquals(expected, runProgram(program));
    }

    @Test
    @DisplayName("Complex expression with mixed prefix and postfix")
    public void testMixedPrefixPostfix() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 5, y = 3, result
            result = ++x + y--
            IPAKITA: result & " " & x & " " & y
            KATAPUSAN
            """;
        
        String expected = "9 6 2";
        assertEquals(expected, runProgram(program));
    }
    
    // ========================================================================================
    // IMPROVED COMMENT HANDLING TESTS (Updated for "comments only at start of line" rule)
    // ========================================================================================
    
    @Test
    @DisplayName("Comments without spaces after --")
    public void testCommentsWithoutSpaces() {
        String program = """
            SUGOD
            --this is a comment without space after --
            MUGNA NUMERO x = 5
            IPAKITA: x
            KATAPUSAN
            """;
        
        String expected = "5";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Comments with various punctuation")
    public void testCommentsWithPunctuation() {
        String program = """
            SUGOD
            --!@#$%^&*() special characters in comment
            MUGNA NUMERO x = 10
            IPAKITA: x
            KATAPUSAN
            """;
        
        String expected = "10";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Decrement operator vs comment disambiguation")
    public void testDecrementVsComment() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 10, y = 5
            -- decrement y in expression (comment at line start)
            x = x + --y
            IPAKITA: x & " " & y
            KATAPUSAN
            """;
        
        String expected = "14 4";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Comment vs prefix decrement at start of line")
    public void testCommentVsPrefixDecrementAtLineStart() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 10
            -- this is a comment with space
            --x
            IPAKITA: x
            KATAPUSAN
            """;
        
        // --x at start of line (no space after --) is prefix decrement
        String expected = "9";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Comments with identifiers containing underscores")
    public void testCommentsWithUnderscoreIdentifiers() {
        String program = """
            SUGOD
            --comment with underscore_identifiers
            MUGNA NUMERO test_var = 100
            IPAKITA: test_var
            KATAPUSAN
            """;
        
        String expected = "100";
        assertEquals(expected, runProgram(program));
    }
    
    // ========================================================================================
    // EDGE CASES AND ERROR HANDLING
    // ========================================================================================
    
    @Test
    @DisplayName("Postfix on non-variable should throw error")
    public void testPostfixOnNonVariable() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 5++
            KATAPUSAN
            """;
        
        assertThrows(RuntimeException.class, () -> runProgram(program));
    }
    
    @Test
    @DisplayName("Multiple consecutive postfix operations")
    public void testConsecutivePostfixOperations() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 5
            IPAKITA: x++ & " " & x++ & " " & x++ & " " & x
            KATAPUSAN
            """;
        
        String expected = "5 6 7 8";
        assertEquals(expected, runProgram(program));
    }
    
    @Test
    @DisplayName("Postfix in complex arithmetic")
    public void testPostfixInComplexArithmetic() {
        String program = """
            SUGOD
            MUGNA NUMERO a = 2, b = 3, result
            result = a++ * b-- + ++a
            IPAKITA: result & " " & a & " " & b
            KATAPUSAN
            """;
        
        String expected = "10 4 2";
        assertEquals(expected, runProgram(program));
    }
}
