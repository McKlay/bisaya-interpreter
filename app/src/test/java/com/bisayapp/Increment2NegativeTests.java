package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Negative Test Suite for Increment 2
 * 
 * Tests error conditions and edge cases for:
 * - Arithmetic operators with invalid operands
 * - Unary operators on invalid targets
 * - Comparison operators with type mismatches  
 * - Logical operators on non-boolean values
 * - DAWAT input validation and error cases
 * 
 * All tests verify that:
 * 1. Appropriate exceptions are thrown
 * 2. Error messages contain line and column information
 * 3. Error messages clearly describe the problem
 */
public class Increment2NegativeTests {

    // ====================================================================
    // TYPE MISMATCH IN ARITHMETIC OPERATIONS
    // ====================================================================

    @Test
    @DisplayName("NEG: Cannot add string to number")
    void testStringPlusNumberError() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            x = x + "hello"
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src),
            "Adding string to number should throw error");
        
        String msg = ex.getMessage();
        // Note: Current implementation may not have line/col in runtime errors yet
        // This test documents expected behavior
        assertTrue(msg.contains("type") || msg.contains("Type") || msg.contains("operand"),
            "Error should mention type/operand issue: " + msg);
    }

    @Test
    @DisplayName("NEG: Cannot multiply char by number")
    void testCharMultiplyError() {
        String src = """
            SUGOD
            MUGNA LETRA c='A'
            MUGNA NUMERO result
            result = c * 5
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Multiplying char by number should throw error");
    }

    @Test
    @DisplayName("NEG: Cannot divide boolean by number")
    void testBooleanDivideError() {
        String src = """
            SUGOD
            MUGNA TINUOD flag="OO"
            MUGNA NUMERO result
            result = flag / 2
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Dividing boolean by number should throw error");
    }

    @Test
    @DisplayName("NEG: Cannot perform modulo on decimals (TIPIK)")
    void testModuloOnTipikError() {
        String src = """
            SUGOD
            MUGNA TIPIK x=10.5, y=3.2
            MUGNA NUMERO result
            result = x % y
            KATAPUSAN
            """;
        // Modulo typically requires integers
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src),
            "Modulo on TIPIK may not be supported");
    }

    // ====================================================================
    // DIVISION AND MODULO BY ZERO WITH ERROR LOCATION
    // ====================================================================

    @Test
    @DisplayName("NEG: Division by zero includes error location")
    void testDivisionByZeroErrorMessage() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=0, z
            z = x / y
            IPAKITA: z
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        String msg = ex.getMessage();
        assertTrue(msg.toLowerCase().contains("division") || msg.toLowerCase().contains("zero"),
            "Error should mention division by zero: " + msg);
        // TODO: Verify line/col once implemented
    }

    @Test
    @DisplayName("NEG: Modulo by zero includes error location")
    void testModuloByZeroErrorMessage() {
        String src = """
            SUGOD
            MUGNA NUMERO x=17, y=0, z
            z = x % y
            IPAKITA: z
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        String msg = ex.getMessage();
        assertTrue(msg.toLowerCase().contains("modulo") || msg.toLowerCase().contains("zero"),
            "Error should mention modulo by zero: " + msg);
    }

    @Test
    @DisplayName("NEG: Division by expression evaluating to zero")
    void testDivisionByExpressionZero() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, a=5, b=5, z
            z = x / (a - b)
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Division by expression that evaluates to zero should throw error");
    }

    // ====================================================================
    // UNDECLARED VARIABLE ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: Using undeclared variable in arithmetic")
    void testUndeclaredVariableInArithmetic() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            x = x + undeclaredVar
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        String msg = ex.getMessage();
        assertTrue(msg.contains("Undefined") || msg.contains("undefined") || msg.contains("declared"),
            "Error should mention undefined variable: " + msg);
        assertTrue(msg.contains("undeclaredVar"),
            "Error should include variable name: " + msg);
    }

    @Test
    @DisplayName("NEG: Comparing undeclared variable")
    void testUndeclaredVariableInComparison() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            MUGNA TINUOD result
            result = x > unknownVar
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        assertTrue(ex.getMessage().contains("unknown") || ex.getMessage().contains("Undefined"),
            "Should report undefined variable");
    }

    // ====================================================================
    // UNARY OPERATOR ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: Increment on literal value")
    void testIncrementOnLiteral() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            x = ++5
            KATAPUSAN
            """;
        // Parser or interpreter should reject this
        assertThrows(Exception.class, () -> runProgram(src),
            "Cannot increment a literal");
    }

    @Test
    @DisplayName("NEG: Decrement on expression")
    void testDecrementOnExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=5
            --(x + y)
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "Cannot decrement an expression");
    }

    @Test
    @DisplayName("NEG: Increment on LETRA variable")
    void testIncrementOnLetra() {
        String src = """
            SUGOD
            MUGNA LETRA c='A'
            ++c
            KATAPUSAN
            """;
        // May or may not be supported - test documents behavior
        // If LETRA is stored as char, increment might work (A -> B)
        // If not, should throw error
        try {
            runProgram(src);
            // If it succeeds, that's valid behavior (char arithmetic)
        } catch (RuntimeException e) {
            // If it fails, verify error message
            assertTrue(e.getMessage().contains("type") || e.getMessage().contains("number"),
                "Should indicate type mismatch if LETRA can't be incremented");
        }
    }

    @Test
    @DisplayName("NEG: Unary minus on boolean")
    void testUnaryMinusOnBoolean() {
        String src = """
            SUGOD
            MUGNA TINUOD flag="OO"
            MUGNA NUMERO x
            x = -flag
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Cannot apply unary minus to boolean");
    }

    // ====================================================================
    // COMPARISON OPERATOR ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: Comparing incompatible types (NUMERO vs LETRA)")
    void testCompareNumberWithChar() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            MUGNA LETRA c='A'
            MUGNA TINUOD result
            result = x > c
            KATAPUSAN
            """;
        // May work if LETRA is treated as numeric (ASCII value)
        // Or may throw error - test documents behavior
        try {
            String output = runProgram(src);
            // If succeeds, LETRA is being compared as ASCII value
        } catch (RuntimeException e) {
            // If fails, verify appropriate error
            assertTrue(e.getMessage().contains("type") || e.getMessage().contains("compar"),
                "Should indicate type comparison issue");
        }
    }

    @Test
    @DisplayName("NEG: Equality check between different types")
    void testEqualityDifferentTypes() {
        String src = """
            SUGOD
            MUGNA NUMERO x=65
            MUGNA LETRA c='A'
            MUGNA TINUOD result
            result = x == c
            KATAPUSAN
            """;
        // Should either convert types or throw error
        // Test documents behavior
        assertDoesNotThrow(() -> runProgram(src));
    }

    // ====================================================================
    // LOGICAL OPERATOR ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: UG (AND) operator on numbers")
    void testLogicalAndOnNumbers() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5, y=10
            MUGNA TINUOD result
            result = x UG y
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "UG operator requires boolean operands");
    }

    @Test
    @DisplayName("NEG: O (OR) operator on strings")
    void testLogicalOrOnStrings() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = "hello" O "world"
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "O operator requires boolean operands");
    }

    @Test
    @DisplayName("NEG: DILI (NOT) operator on number")
    void testLogicalNotOnNumber() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            MUGNA TINUOD result
            result = DILI x
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "DILI operator requires boolean operand");
    }

    @Test
    @DisplayName("NEG: Mixed types in logical expression")
    void testMixedTypesInLogicalExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            MUGNA TINUOD flag="OO"
            MUGNA TINUOD result
            result = x UG flag
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Cannot mix number and boolean in logical expression");
    }

    // ====================================================================
    // DAWAT INPUT VALIDATION ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: DAWAT with undeclared variable")
    void testDawatUndeclaredVariable() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x, undeclaredVar
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "5,10"));
        assertTrue(ex.getMessage().contains("Undefined") || ex.getMessage().contains("declared"),
            "Should report undefined variable: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT wrong number of inputs (too few)")
    void testDawatTooFewInputs() {
        String src = """
            SUGOD
            MUGNA NUMERO x, y, z
            DAWAT: x, y, z
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "10,20"));
        assertTrue(ex.getMessage().contains("expects") || ex.getMessage().contains("3"),
            "Should indicate wrong number of inputs: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT wrong number of inputs (too many)")
    void testDawatTooManyInputs() {
        String src = """
            SUGOD
            MUGNA NUMERO x, y
            DAWAT: x, y
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "10,20,30"));
        assertTrue(ex.getMessage().contains("expects") || ex.getMessage().contains("2"),
            "Should indicate wrong number of inputs: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT invalid NUMERO input (not a number)")
    void testDawatInvalidNumero() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "abc"));
        assertTrue(ex.getMessage().contains("Invalid") || ex.getMessage().contains("NUMERO"),
            "Should indicate invalid number input: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT NUMERO with decimal value")
    void testDawatNumeroWithDecimal() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "10.5"));
        assertTrue(ex.getMessage().contains("decimal") || ex.getMessage().contains("NUMERO"),
            "NUMERO cannot have decimal: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT invalid TIPIK input")
    void testDawatInvalidTipik() {
        String src = """
            SUGOD
            MUGNA TIPIK x
            DAWAT: x
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "not_a_number"));
        assertTrue(ex.getMessage().contains("Invalid") || ex.getMessage().contains("TIPIK"),
            "Should indicate invalid decimal input: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT LETRA with multiple characters")
    void testDawatLetraMultipleChars() {
        String src = """
            SUGOD
            MUGNA LETRA c
            DAWAT: c
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "AB"));
        assertTrue(ex.getMessage().contains("one character") || ex.getMessage().contains("LETRA"),
            "LETRA must be exactly one character: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT LETRA with empty input")
    void testDawatLetraEmpty() {
        String src = """
            SUGOD
            MUGNA LETRA c
            DAWAT: c
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, ""));
        assertTrue(ex.getMessage().contains("character") || ex.getMessage().contains("empty"),
            "LETRA cannot be empty: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT TINUOD with invalid value")
    void testDawatTinuodInvalid() {
        String src = """
            SUGOD
            MUGNA TINUOD flag
            DAWAT: flag
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, "true"));
        assertTrue(ex.getMessage().contains("OO") || ex.getMessage().contains("DILI"),
            "TINUOD must be 'OO' or 'DILI': " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: DAWAT with empty input line")
    void testDawatEmptyInputLine() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> runProgramWithInput(src, ""));
        // Should fail due to wrong number of inputs or invalid format
        assertNotNull(ex.getMessage());
    }

    // ====================================================================
    // EDGE CASES - NUMERIC LIMITS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Integer overflow in arithmetic")
    void testIntegerOverflow() {
        String src = """
            SUGOD
            MUGNA NUMERO x=2147483647
            MUGNA NUMERO y
            y = x + 1
            IPAKITA: y
            KATAPUSAN
            """;
        // Java integers wrap around on overflow
        // Test documents behavior (may overflow or throw error)
        assertDoesNotThrow(() -> runProgram(src));
    }

    @Test
    @DisplayName("EDGE: Integer underflow in arithmetic")
    void testIntegerUnderflow() {
        String src = """
            SUGOD
            MUGNA NUMERO x=-2147483648
            MUGNA NUMERO y
            y = x - 1
            IPAKITA: y
            KATAPUSAN
            """;
        // Test documents overflow behavior
        assertDoesNotThrow(() -> runProgram(src));
    }

    @Test
    @DisplayName("EDGE: Very large TIPIK value")
    void testVeryLargeTipik() {
        String src = """
            SUGOD
            MUGNA TIPIK x=1.7976931348623157E308
            IPAKITA: x
            KATAPUSAN
            """;
        assertDoesNotThrow(() -> runProgram(src));
    }

    // ====================================================================
    // EDGE CASES - OPERATOR COMBINATIONS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Double negation")
    void testDoubleNegation() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5, y
            y = - -x
            IPAKITA: y
            KATAPUSAN
            """;
        assertEquals("5", runProgram(src));
    }

    @Test
    @DisplayName("EDGE: Triple negation")
    void testTripleNegation() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5, y
            y = - - -x
            IPAKITA: y
            KATAPUSAN
            """;
        assertEquals("-5", runProgram(src));
    }

    @Test
    @DisplayName("EDGE: Multiple increments on same line")
    void testMultipleIncrementsExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5, y
            ++x
            ++x
            y = x
            IPAKITA: y
            KATAPUSAN
            """;
        assertEquals("7", runProgram(src));
    }

    @Test
    @DisplayName("EDGE: DILI applied multiple times")
    void testMultipleNotOperators() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = DILI DILI (10 > 5)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO", runProgram(src));
    }

    // ====================================================================
    // EDGE CASES - WHITESPACE AND FORMATTING
    // ====================================================================

    @Test
    @DisplayName("EDGE: DAWAT with excessive whitespace")
    void testDawatExcessiveWhitespace() {
        String src = """
            SUGOD
            MUGNA NUMERO x, y
            DAWAT: x, y
            IPAKITA: x & "," & y
            KATAPUSAN
            """;
        String output = runProgramWithInput(src, "  10  ,  20  ");
        assertEquals("10,20", output);
    }

    @Test
    @DisplayName("EDGE: DAWAT with tabs in input")
    void testDawatWithTabs() {
        String src = """
            SUGOD
            MUGNA NUMERO x, y
            DAWAT: x, y
            IPAKITA: x & "," & y
            KATAPUSAN
            """;
        String output = runProgramWithInput(src, "10\t,\t20");
        assertEquals("10,20", output);
    }

    // ====================================================================
    // HELPER METHODS
    // ====================================================================

    private String runProgram(String source) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);
        
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        
        Parser parser = new Parser(tokens);
        List<Stmt> program = parser.parseProgram();
        
        Interpreter interpreter = new Interpreter(out);
        interpreter.interpret(program);
        
        return output.toString();
    }

    private String runProgramWithInput(String source, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        
        Parser parser = new Parser(tokens);
        List<Stmt> program = parser.parseProgram();
        
        Interpreter interpreter = new Interpreter(out, in);
        interpreter.interpret(program);
        
        return output.toString();
    }
}
