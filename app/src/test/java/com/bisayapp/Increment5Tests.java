package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;

/**
 * Test suite for Increment 5 - SAMTANG (While Loop) implementation
 * 
 * Tests verify:
 * 1. Basic while loop functionality
 * 2. Loop condition evaluation
 * 3. Nested while loops
 * 4. While loops with various operations
 * 5. Edge cases (zero iterations, infinite loop prevention)
 */
public class Increment5Tests {

    /**
     * Helper method to run a Bisaya++ program and capture output
     */
    private String runProgram(String source) {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.scanTokens());
        Interpreter interp = new Interpreter(new PrintStream(outContent));
        interp.interpret(parser.parseProgram());
        return outContent.toString();
    }

    /**
     * Helper method to run a program with input
     */
    private String runProgramWithInput(String source, String input) {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.scanTokens());
        Interpreter interp = new Interpreter(new PrintStream(outContent), inContent);
        interp.interpret(parser.parseProgram());
        return outContent.toString();
    }

    // ============================================================================
    // BASIC WHILE LOOP TESTS
    // ============================================================================

    @Test
    @DisplayName("Basic while loop - count from 1 to 5")
    public void testBasicWhileLoop() {
        String program = """
            SUGOD
            MUGNA NUMERO ctr = 1
            SAMTANG (ctr <= 5)
            PUNDOK{
                IPAKITA: "Count: " & ctr & $
                ctr = ctr + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "Count: 1\nCount: 2\nCount: 3\nCount: 4\nCount: 5\n";
        assertEquals(expected, output);
    }

    @Test
    @DisplayName("While loop with simple counter")
    public void testSimpleCounter() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 0
            SAMTANG (x < 3)
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("0 1 2 ", output);
    }

    @Test
    @DisplayName("While loop with decrement")
    public void testWhileLoopDecrement() {
        String program = """
            SUGOD
            MUGNA NUMERO countdown = 5
            SAMTANG (countdown > 0)
            PUNDOK{
                IPAKITA: countdown & " "
                countdown = countdown - 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("5 4 3 2 1 ", output);
    }

    @Test
    @DisplayName("While loop - zero iterations (condition false from start)")
    public void testWhileLoopZeroIterations() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 10
            IPAKITA: "Before loop" & $
            SAMTANG (x < 5)
            PUNDOK{
                IPAKITA: "Inside loop" & $
                x = x + 1
            }
            IPAKITA: "After loop" & $
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "Before loop\nAfter loop\n";
        assertEquals(expected, output);
    }

    // ============================================================================
    // WHILE LOOP WITH ARITHMETIC
    // ============================================================================

    @Test
    @DisplayName("While loop - sum calculation")
    public void testWhileLoopSum() {
        String program = """
            SUGOD
            MUGNA NUMERO sum = 0, i = 1
            SAMTANG (i <= 5)
            PUNDOK{
                sum = sum + i
                i = i + 1
            }
            IPAKITA: "Sum: " & sum
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("Sum: 15", output);
    }

    @Test
    @DisplayName("While loop - factorial calculation")
    public void testWhileLoopFactorial() {
        String program = """
            SUGOD
            MUGNA NUMERO n = 5, factorial = 1
            SAMTANG (n > 0)
            PUNDOK{
                factorial = factorial * n
                n = n - 1
            }
            IPAKITA: "Factorial: " & factorial
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("Factorial: 120", output);
    }

    // ============================================================================
    // WHILE LOOP WITH LOGICAL CONDITIONS
    // ============================================================================

    @Test
    @DisplayName("While loop with compound condition (AND)")
    public void testWhileLoopCompoundAND() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 1, y = 10
            SAMTANG (x < 5 UG y > 5)
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
                y = y - 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("1 2 3 4 ", output);
    }

    @Test
    @DisplayName("While loop with compound condition (OR)")
    public void testWhileLoopCompoundOR() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 1
            SAMTANG (x < 3 O x == 5)
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("1 2 ", output);
    }

    @Test
    @DisplayName("While loop with boolean variable condition")
    public void testWhileLoopBooleanCondition() {
        String program = """
            SUGOD
            MUGNA NUMERO count = 0
            MUGNA TINUOD running = "OO"
            SAMTANG (running == "OO")
            PUNDOK{
                count = count + 1
                IPAKITA: count & " "
                KUNG (count >= 3)
                PUNDOK{
                    running = "DILI"
                }
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("1 2 3 ", output);
    }

    // ============================================================================
    // NESTED WHILE LOOPS
    // ============================================================================

    @Test
    @DisplayName("Nested while loops - simple pattern")
    public void testNestedWhileLoops() {
        String program = """
            SUGOD
            MUGNA NUMERO i = 1, j
            SAMTANG (i <= 3)
            PUNDOK{
                j = 1
                SAMTANG (j <= 2)
                PUNDOK{
                    IPAKITA: i & "," & j & " "
                    j = j + 1
                }
                i = i + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("1,1 1,2 2,1 2,2 3,1 3,2 ", output);
    }

    @Test
    @DisplayName("Nested while loops - multiplication table")
    public void testNestedWhileLoopsMultiplication() {
        String program = """
            SUGOD
            MUGNA NUMERO i = 1, j, product
            SAMTANG (i <= 3)
            PUNDOK{
                j = 1
                SAMTANG (j <= 3)
                PUNDOK{
                    product = i * j
                    IPAKITA: product & " "
                    j = j + 1
                }
                IPAKITA: $
                i = i + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "1 2 3 \n2 4 6 \n3 6 9 \n";
        assertEquals(expected, output);
    }

    @Test
    @DisplayName("Deeply nested while loops")
    public void testDeeplyNestedWhileLoops() {
        String program = """
            SUGOD
            MUGNA NUMERO i = 1, j, k
            SAMTANG (i <= 2)
            PUNDOK{
                j = 1
                SAMTANG (j <= 2)
                PUNDOK{
                    k = 1
                    SAMTANG (k <= 2)
                    PUNDOK{
                        IPAKITA: i & j & k & " "
                        k = k + 1
                    }
                    j = j + 1
                }
                i = i + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("111 112 121 122 211 212 221 222 ", output);
    }

    // ============================================================================
    // WHILE LOOPS WITH CONTROL FLOW
    // ============================================================================

    @Test
    @DisplayName("While loop with conditional inside")
    public void testWhileLoopWithConditional() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 1
            SAMTANG (x <= 10)
            PUNDOK{
                KUNG (x % 2 == 0)
                PUNDOK{
                    IPAKITA: x & " "
                }
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("2 4 6 8 10 ", output);
    }

    @Test
    @DisplayName("While loop with if-else inside")
    public void testWhileLoopWithIfElse() {
        String program = """
            SUGOD
            MUGNA NUMERO n = 1
            SAMTANG (n <= 5)
            PUNDOK{
                KUNG (n % 2 == 0)
                PUNDOK{
                    IPAKITA: "Even: " & n & $
                }
                KUNG WALA
                PUNDOK{
                    IPAKITA: "Odd: " & n & $
                }
                n = n + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "Odd: 1\nEven: 2\nOdd: 3\nEven: 4\nOdd: 5\n";
        assertEquals(expected, output);
    }

    // ============================================================================
    // WHILE LOOPS WITH USER INPUT
    // ============================================================================

    @Test
    @DisplayName("While loop with input - sum until zero")
    public void testWhileLoopWithInput() {
        String program = """
            SUGOD
            MUGNA NUMERO num = 1, sum = 0
            SAMTANG (num <> 0)
            PUNDOK{
                IPAKITA: "Enter number: "
                DAWAT: num
                sum = sum + num
            }
            IPAKITA: "Total sum: " & sum
            KATAPUSAN
            """;
        String input = "5\n3\n7\n0\n";
        String output = runProgramWithInput(program, input);
        assertTrue(output.contains("Total sum: 15"));
    }

    // ============================================================================
    // WHILE LOOPS WITH INCREMENT/DECREMENT OPERATORS
    // ============================================================================

    @Test
    @DisplayName("While loop with ++ operator")
    public void testWhileLoopWithIncrement() {
        String program = """
            SUGOD
            MUGNA NUMERO i = 0
            SAMTANG (i < 5)
            PUNDOK{
                IPAKITA: i & " "
                i++
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("0 1 2 3 4 ", output);
    }

    @Test
    @DisplayName("While loop with -- operator")
    public void testWhileLoopWithDecrement() {
        String program = """
            SUGOD
            MUGNA NUMERO i = 5
            SAMTANG (i > 0)
            PUNDOK{
                IPAKITA: i & " "
                i--
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("5 4 3 2 1 ", output);
    }

    // ============================================================================
    // MIXING FOR AND WHILE LOOPS
    // ============================================================================

    @Test
    @DisplayName("For loop inside while loop")
    public void testForInsideWhile() {
        String program = """
            SUGOD
            MUGNA NUMERO outer = 1, inner
            SAMTANG (outer <= 2)
            PUNDOK{
                IPAKITA: "Outer: " & outer & $
                ALANG SA (inner=1, inner<=3, inner++)
                PUNDOK{
                    IPAKITA: "  Inner: " & inner & $
                }
                outer = outer + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "Outer: 1\n  Inner: 1\n  Inner: 2\n  Inner: 3\nOuter: 2\n  Inner: 1\n  Inner: 2\n  Inner: 3\n";
        assertEquals(expected, output);
    }

    @Test
    @DisplayName("While loop inside for loop")
    public void testWhileInsideFor() {
        String program = """
            SUGOD
            MUGNA NUMERO outer, inner
            ALANG SA (outer=1, outer<=2, outer++)
            PUNDOK{
                IPAKITA: "Outer: " & outer & $
                inner = 1
                SAMTANG (inner <= 2)
                PUNDOK{
                    IPAKITA: "  Inner: " & inner & $
                    inner = inner + 1
                }
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "Outer: 1\n  Inner: 1\n  Inner: 2\nOuter: 2\n  Inner: 1\n  Inner: 2\n";
        assertEquals(expected, output);
    }

    // ============================================================================
    // EDGE CASES AND ERROR CONDITIONS
    // ============================================================================

    @Test
    @DisplayName("While loop with finite condition")
    public void testWhileLoopWithFiniteCondition() {
        String program = """
            SUGOD
            MUGNA NUMERO count = 0
            SAMTANG (count < 5)
            PUNDOK{
                count = count + 1
                IPAKITA: count & " "
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("1 2 3 4 5 ", output);
    }

    @Test
    @DisplayName("While loop with unary operators in condition")
    public void testWhileLoopUnaryCondition() {
        String program = """
            SUGOD
            MUGNA NUMERO x = -3
            SAMTANG (x < 0)
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("-3 -2 -1 ", output);
    }

    @Test
    @DisplayName("While loop - single iteration")
    public void testWhileLoopSingleIteration() {
        String program = """
            SUGOD
            MUGNA NUMERO x = 1
            SAMTANG (x == 1)
            PUNDOK{
                IPAKITA: "Once" & $
                x = 2
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("Once\n", output);
    }

    // ============================================================================
    // COMPLEX SCENARIOS
    // ============================================================================

    @Test
    @DisplayName("While loop - pattern printing")
    public void testWhileLoopPattern() {
        String program = """
            SUGOD
            MUGNA NUMERO row = 1, col
            SAMTANG (row <= 3)
            PUNDOK{
                col = 1
                SAMTANG (col <= row)
                PUNDOK{
                    IPAKITA: "*"
                    col = col + 1
                }
                IPAKITA: $
                row = row + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(program);
        String expected = "*\n**\n***\n";
        assertEquals(expected, output);
    }

    @Test
    @DisplayName("While loop - power calculation")
    public void testWhileLoopPower() {
        String program = """
            SUGOD
            MUGNA NUMERO base = 2, exponent = 4, result = 1
            SAMTANG (exponent > 0)
            PUNDOK{
                result = result * base
                exponent = exponent - 1
            }
            IPAKITA: "2^4 = " & result
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("2^4 = 16", output);
    }

    @Test
    @DisplayName("While loop - finding maximum")
    public void testWhileLoopMaximum() {
        String program = """
            SUGOD
            MUGNA NUMERO count = 0, current = 5, max = 0
            SAMTANG (count < 5)
            PUNDOK{
                KUNG (current > max)
                PUNDOK{
                    max = current
                }
                current = current + 2
                count = count + 1
            }
            IPAKITA: "Max: " & max
            KATAPUSAN
            """;
        String output = runProgram(program);
        assertEquals("Max: 13", output);
    }
}
