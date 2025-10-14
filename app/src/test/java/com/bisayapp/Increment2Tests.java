package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Comprehensive test suite for Increment 2 features:
 * - Unary operators: +, -, ++, --
 * - DAWAT command for user input
 * - Arithmetic operations: +, -, *, /, %
 * - Comparison operators: >, <, >=, <=, ==, <>
 * - Logical operations: UG (AND), O (OR), DILI (NOT)
 * - Boolean literals: "OO" (true), "DILI" (false)
 */
public class Increment2Tests {

    // ====================================================================
    // ARITHMETIC OPERATORS TESTS
    // ====================================================================

    @Test
    @DisplayName("Addition: integer + integer")
    void testAdditionIntegers() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=20, z
            z = x + y
            IPAKITA: z
            KATAPUSAN
            """;
        assertEquals("30\n", runProgram(src));
    }

    @Test
    @DisplayName("Addition: decimal + decimal")
    void testAdditionDecimals() {
        String src = """
            SUGOD
            MUGNA TIPIK x=10.5, y=20.3, z
            z = x + y
            IPAKITA: z
            KATAPUSAN
            """;
        assertEquals("30.8\n", runProgram(src));
    }

    @Test
    @DisplayName("Subtraction: integer - integer")
    void testSubtractionIntegers() {
        String src = """
            SUGOD
            MUGNA NUMERO x=50, y=20, z
            z = x - y
            IPAKITA: z
            KATAPUSAN
            """;
        assertEquals("30\n", runProgram(src));
    }

    @Test
    @DisplayName("Multiplication: integer * integer")
    void testMultiplicationIntegers() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5, y=6, z
            z = x * y
            IPAKITA: z
            KATAPUSAN
            """;
        assertEquals("30\n", runProgram(src));
    }

    @Test
    @DisplayName("Division: integer / integer")
    void testDivisionIntegers() {
        String src = """
            SUGOD
            MUGNA NUMERO x=20, y=4, z
            z = x / y
            IPAKITA: z
            KATAPUSAN
            """;
        assertEquals("5\n", runProgram(src));
    }

    @Test
    @DisplayName("Modulo: integer % integer")
    void testModuloIntegers() {
        String src = """
            SUGOD
            MUGNA NUMERO x=17, y=5, z
            z = x % y
            IPAKITA: z
            KATAPUSAN
            """;
        assertEquals("2\n", runProgram(src));
    }

    @Test
    @DisplayName("Complex arithmetic expression with precedence")
    void testArithmeticPrecedence() {
        String src = """
            SUGOD
            MUGNA NUMERO result
            result = 2 + 3 * 4
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("14\n", runProgram(src));
    }

    @Test
    @DisplayName("Arithmetic with parentheses")
    void testArithmeticWithParentheses() {
        String src = """
            SUGOD
            MUGNA NUMERO result
            result = (2 + 3) * 4
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("20\n", runProgram(src));
    }

    @Test
    @DisplayName("Division by zero throws error")
    void testDivisionByZero() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=0, z
            z = x / y
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src), 
            "Division by zero should throw RuntimeException");
    }

    @Test
    @DisplayName("Modulo by zero throws error")
    void testModuloByZero() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=0, z
            z = x % y
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Modulo by zero should throw RuntimeException");
    }

    // ====================================================================
    // UNARY OPERATORS TESTS
    // ====================================================================

    @Test
    @DisplayName("Unary minus on positive number")
    void testUnaryMinus() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y
            y = -x
            IPAKITA: y
            KATAPUSAN
            """;
        assertEquals("-10\n", runProgram(src));
    }

    @Test
    @DisplayName("Unary plus on number")
    void testUnaryPlus() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y
            y = +x
            IPAKITA: y
            KATAPUSAN
            """;
        assertEquals("10\n", runProgram(src));
    }

    @Test
    @DisplayName("Increment operator (++)")
    void testIncrementOperator() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            ++x
            IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("6\n", runProgram(src));
    }

    @Test
    @DisplayName("Decrement operator (--)")
    void testDecrementOperator() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            --x
            IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("4\n", runProgram(src));
    }

    @Test
    @DisplayName("Multiple increments")
    void testMultipleIncrements() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            ++x
            ++x
            ++x
            IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("4\n", runProgram(src));
    }

    // ====================================================================
    // COMPARISON OPERATORS TESTS
    // ====================================================================

    @Test
    @DisplayName("Greater than: true case")
    void testGreaterThanTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 10 > 5
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Greater than: false case")
    void testGreaterThanFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 5 > 10
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    @Test
    @DisplayName("Less than: true case")
    void testLessThanTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 5 < 10
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Greater than or equal: equal case")
    void testGreaterOrEqualEqual() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 10 >= 10
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Less than or equal: less case")
    void testLessOrEqualLess() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 5 <= 10
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Equal to: true case")
    void testEqualTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 10 == 10
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Equal to: false case")
    void testEqualFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 10 == 5
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    @Test
    @DisplayName("Not equal to: true case")
    void testNotEqualTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 10 <> 5
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Not equal to: false case")
    void testNotEqualFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = 10 <> 10
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    // ====================================================================
    // LOGICAL OPERATORS TESTS
    // ====================================================================

    @Test
    @DisplayName("Logical AND (UG): both true")
    void testLogicalAndBothTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = (10 > 5) UG (20 > 15)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical AND (UG): one false")
    void testLogicalAndOneFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = (10 > 5) UG (5 > 15)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical AND (UG): both false")
    void testLogicalAndBothFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = (5 > 10) UG (5 > 15)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical OR (O): both true")
    void testLogicalOrBothTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = (10 > 5) O (20 > 15)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical OR (O): one true")
    void testLogicalOrOneTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = (10 > 5) O (5 > 15)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical OR (O): both false")
    void testLogicalOrBothFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = (5 > 10) O (5 > 15)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical NOT (DILI): true becomes false")
    void testLogicalNotTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = DILI (10 > 5)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgram(src));
    }

    @Test
    @DisplayName("Logical NOT (DILI): false becomes true")
    void testLogicalNotFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD result
            result = DILI (5 > 10)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Complex logical expression")
    void testComplexLogicalExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO a=100, b=200, c=300
            MUGNA TINUOD d
            d = (a < b) UG (c <> 200)
            IPAKITA: d
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    // ====================================================================
    // DAWAT (INPUT) TESTS
    // ====================================================================

    @Test
    @DisplayName("DAWAT: single NUMERO variable")
    void testInputSingleNumero() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x
            IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("42\n", runProgramWithInput(src, "42"));
    }

    @Test
    @DisplayName("DAWAT: multiple variables")
    void testInputMultipleVariables() {
        String src = """
            SUGOD
            MUGNA NUMERO x, y
            DAWAT: x, y
            IPAKITA: x & " " & y
            KATAPUSAN
            """;
        assertEquals("10 20\n", runProgramWithInput(src, "10, 20"));
    }

    @Test
    @DisplayName("DAWAT: LETRA variable")
    void testInputLetra() {
        String src = """
            SUGOD
            MUGNA LETRA c
            DAWAT: c
            IPAKITA: c
            KATAPUSAN
            """;
        assertEquals("a\n", runProgramWithInput(src, "a"));
    }

    @Test
    @DisplayName("DAWAT: TIPIK variable")
    void testInputTipik() {
        String src = """
            SUGOD
            MUGNA TIPIK x
            DAWAT: x
            IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("3.14\n", runProgramWithInput(src, "3.14"));
    }

    @Test
    @DisplayName("DAWAT: TINUOD variable with OO")
    void testInputTinuodTrue() {
        String src = """
            SUGOD
            MUGNA TINUOD flag
            DAWAT: flag
            IPAKITA: flag
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgramWithInput(src, "OO"));
    }

    @Test
    @DisplayName("DAWAT: TINUOD variable with DILI")
    void testInputTinuodFalse() {
        String src = """
            SUGOD
            MUGNA TINUOD flag
            DAWAT: flag
            IPAKITA: flag
            KATAPUSAN
            """;
        assertEquals("DILI\n", runProgramWithInput(src, "DILI"));
    }

    @Test
    @DisplayName("DAWAT: undeclared variable throws error")
    void testInputUndeclaredVariable() {
        String src = """
            SUGOD
            DAWAT: x
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgramWithInput(src, "42"),
            "DAWAT on undeclared variable should throw RuntimeException");
    }

    @Test
    @DisplayName("DAWAT: wrong number of inputs throws error")
    void testInputWrongCount() {
        String src = """
            SUGOD
            MUGNA NUMERO x, y
            DAWAT: x, y
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgramWithInput(src, "42"),
            "Wrong number of inputs should throw RuntimeException");
    }

    @Test
    @DisplayName("DAWAT: invalid NUMERO input throws error")
    void testInputInvalidNumero() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgramWithInput(src, "abc"),
            "Invalid NUMERO input should throw RuntimeException");
    }

    @Test
    @DisplayName("DAWAT: NUMERO with decimal throws error")
    void testInputNumeroWithDecimal() {
        String src = """
            SUGOD
            MUGNA NUMERO x
            DAWAT: x
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgramWithInput(src, "3.14"),
            "NUMERO cannot accept decimal values");
    }

    @Test
    @DisplayName("DAWAT: LETRA with multiple characters throws error")
    void testInputLetraMultipleChars() {
        String src = """
            SUGOD
            MUGNA LETRA c
            DAWAT: c
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgramWithInput(src, "ab"),
            "LETRA must be exactly one character");
    }

    @Test
    @DisplayName("DAWAT: TINUOD with invalid value throws error")
    void testInputTinuodInvalid() {
        String src = """
            SUGOD
            MUGNA TINUOD flag
            DAWAT: flag
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgramWithInput(src, "true"),
            "TINUOD must be 'OO' or 'DILI'");
    }

    // ====================================================================
    // INTEGRATION TESTS
    // ====================================================================

    @Test
    @DisplayName("Sample program from spec: arithmetic operation")
    void testSpecSampleArithmetic() {
        String src = """
            SUGOD
            MUGNA NUMERO xyz, abc=100
            xyz = ((abc * 5) / 10 + 10) * -1
            IPAKITA: "[" & xyz & "]"
            KATAPUSAN
            """;
        assertEquals("[-60]\n", runProgram(src));
    }

    @Test
    @DisplayName("Sample program from spec: logical operation")
    void testSpecSampleLogical() {
        String src = """
            SUGOD
            MUGNA NUMERO a=100, b=200, c=300
            MUGNA TINUOD d
            d = (a < b) UG (c <> 200)
            IPAKITA: d
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
    }

    @Test
    @DisplayName("Complex expression with mixed operators")
    void testComplexMixedExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO result
            result = (10 + 5) * 2 - 8 / 2
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("26\n", runProgram(src));
    }

    @Test
    @DisplayName("Chained comparisons with logical operators")
    void testChainedComparisons() {
        String src = """
            SUGOD
            MUGNA NUMERO x=15
            MUGNA TINUOD result
            result = (x > 10) UG (x < 20) UG (x <> 12)
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("OO\n", runProgram(src));
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
