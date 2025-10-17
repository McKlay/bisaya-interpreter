package com.bisayapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

/**
 * Test Suite for Increment 4: Loop Implementation (ALANG SA - FOR loops)
 * 
 * Tests cover:
 * 1. Basic for loop functionality
 * 2. Loop with counter increments
 * 3. Loop with decrements
 * 4. Nested loops
 * 5. Loops with multiple operations in body
 * 6. Edge cases (zero iterations, single iteration)
 * 7. Loop variable scope
 */
public class Increment4Tests {

    /**
     * Helper method to run a Bisaya++ program and capture output
     */
    private String runProgram(String source) {
        return runProgram(source, "");
    }

    /**
     * Helper method to run a Bisaya++ program with input and capture output
     */
    private String runProgram(String source, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        
        try {
            Lexer lexer = new Lexer(source);
            var tokens = lexer.scanTokens();
            Parser parser = new Parser(tokens);
            var program = parser.parseProgram();
            Interpreter interp = new Interpreter(new PrintStream(output), inputStream);
            interp.interpret(program);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
        
        return output.toString();
    }

    // ========================================================================================
    // BASIC FOR LOOP TESTS
    // ========================================================================================

    @Test
    public void testBasicForLoop() {
        String source = """
            SUGOD
            MUGNA NUMERO ctr
            ALANG SA (ctr=1, ctr<=5, ctr++)
            PUNDOK{
                IPAKITA: ctr & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 3 4 5 ", output);
    }

    @Test
    public void testForLoopWithDecrement() {
        String source = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=5, i>0, i--)
            PUNDOK{
                IPAKITA: i & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("5 4 3 2 1 ", output);
    }

    @Test
    public void testForLoopZeroIterations() {
        String source = """
            SUGOD
            MUGNA NUMERO x
            IPAKITA: "before" & $
            ALANG SA (x=10, x<5, x++)
            PUNDOK{
                IPAKITA: "inside"
            }
            IPAKITA: "after"
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("before\nafter", output);
    }

    @Test
    public void testForLoopSingleIteration() {
        String source = """
            SUGOD
            MUGNA NUMERO n
            ALANG SA (n=1, n<=1, n++)
            PUNDOK{
                IPAKITA: "once"
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("once", output);
    }

    // ========================================================================================
    // LOOP WITH MULTIPLE OPERATIONS
    // ========================================================================================

    @Test
    public void testForLoopWithMultipleStatements() {
        String source = """
            SUGOD
            MUGNA NUMERO i, sum=0
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
                sum = sum + i
                IPAKITA: "i=" & i & " sum=" & sum & $
            }
            IPAKITA: "Final: " & sum
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        String expected = "i=1 sum=1\ni=2 sum=3\ni=3 sum=6\ni=4 sum=10\ni=5 sum=15\nFinal: 15";
        assertEquals(expected, output);
    }

    @Test
    public void testForLoopWithArithmetic() {
        String source = """
            SUGOD
            MUGNA NUMERO x, result
            ALANG SA (x=1, x<=3, x++)
            PUNDOK{
                result = x * 2
                IPAKITA: result & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("2 4 6 ", output);
    }

    // ========================================================================================
    // NESTED LOOPS
    // ========================================================================================

    @Test
    public void testNestedForLoops() {
        String source = """
            SUGOD
            MUGNA NUMERO i, j
            ALANG SA (i=1, i<=3, i++)
            PUNDOK{
                ALANG SA (j=1, j<=2, j++)
                PUNDOK{
                    IPAKITA: i & "," & j & " "
                }
                IPAKITA: $
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1,1 1,2 \n2,1 2,2 \n3,1 3,2 \n", output);
    }

    @Test
    public void testDeeplyNestedLoops() {
        String source = """
            SUGOD
            MUGNA NUMERO a, b, c
            ALANG SA (a=1, a<=2, a++)
            PUNDOK{
                ALANG SA (b=1, b<=2, b++)
                PUNDOK{
                    ALANG SA (c=1, c<=2, c++)
                    PUNDOK{
                        IPAKITA: a & b & c & ' '
                    }
                }
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("111 112 121 122 211 212 221 222 ", output);
    }

    @Test
    public void testNestedLoopWithDifferentCounters() {
        String source = """
            SUGOD
            MUGNA NUMERO outer, inner, product
            ALANG SA (outer=1, outer<=3, outer++)
            PUNDOK{
                ALANG SA (inner=1, inner<=3, inner++)
                PUNDOK{
                    product = outer * inner
                    IPAKITA: product & ' '
                }
                IPAKITA: $
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 3 \n2 4 6 \n3 6 9 \n", output);
    }

    // ========================================================================================
    // LOOP WITH CONDITIONALS
    // ========================================================================================

    @Test
    public void testForLoopWithIfStatement() {
        String source = """
            SUGOD
            MUGNA NUMERO n
            ALANG SA (n=1, n<=10, n++)
            PUNDOK{
                KUNG (n % 2 == 0)
                PUNDOK{
                    IPAKITA: n & ' '
                }
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("2 4 6 8 10 ", output);
    }

    @Test
    public void testForLoopWithIfElse() {
        String source = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
                KUNG (i % 2 == 0)
                PUNDOK{
                    IPAKITA: "even "
                }
                KUNG WALA
                PUNDOK{
                    IPAKITA: "odd "
                }
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("odd even odd even odd ", output);
    }

    // ========================================================================================
    // COMPLEX LOOP SCENARIOS
    // ========================================================================================

    @Test
    public void testLoopWithStringConcatenation() {
        String source = """
            SUGOD
            MUGNA NUMERO i
            MUGNA LETRA ch='A'
            ALANG SA (i=1, i<=3, i++)
            PUNDOK{
                IPAKITA: ch & i & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("A1 A2 A3 ", output);
    }

    @Test
    public void testLoopWithLargeRange() {
        String source = """
            SUGOD
            MUGNA NUMERO count, sum=0
            ALANG SA (count=1, count<=100, count++)
            PUNDOK{
                sum = sum + count
            }
            IPAKITA: sum
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("5050", output); // Sum of 1 to 100 = 5050
    }

    @Test
    public void testLoopWithNonStandardIncrement() {
        String source = """
            SUGOD
            MUGNA NUMERO x
            ALANG SA (x=0, x<10, x=x+2)
            PUNDOK{
                IPAKITA: x & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("0 2 4 6 8 ", output);
    }

    // ========================================================================================
    // LOOP VARIABLE SCOPE TESTS
    // ========================================================================================

    @Test
    public void testLoopVariableAccessAfterLoop() {
        String source = """
            SUGOD
            MUGNA NUMERO ctr
            ALANG SA (ctr=1, ctr<=5, ctr++)
            PUNDOK{
                IPAKITA: ctr & ' '
            }
            IPAKITA: $ & "Final value: " & ctr
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 3 4 5 \nFinal value: 6", output);
    }

    @Test
    public void testLoopVariableModificationInBody() {
        String source = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
                IPAKITA: i & ' '
                i = i + 1
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        // i starts at 1, prints 1, becomes 2, update makes it 3
        // i=3, prints 3, becomes 4, update makes it 5
        // i=5, prints 5, becomes 6, update makes it 7 (exceeds condition)
        assertEquals("1 3 5 ", output);
    }

    // ========================================================================================
    // EXAMPLE FROM SPECIFICATION
    // ========================================================================================

    @Test
    public void testSpecificationExample() {
        String source = """
            SUGOD
            MUGNA NUMERO ctr
            ALANG SA (ctr=1, ctr<=10, ctr++)
            PUNDOK{
                IPAKITA: ctr & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 3 4 5 6 7 8 9 10 ", output);
    }

    // ========================================================================================
    // EDGE CASES AND ERROR HANDLING
    // ========================================================================================

    @Test
    public void testForLoopWithComplexCondition() {
        String source = """
            SUGOD
            MUGNA NUMERO x
            ALANG SA (x=1, x<=5 UG x<>3, x++)
            PUNDOK{
                IPAKITA: x & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 ", output); // Stops at 3 because x<>3 becomes false
    }

    @Test
    public void testLoopWithBooleanCondition() {
        String source = """
            SUGOD
            MUGNA NUMERO n
            MUGNA TINUOD flag="OO"
            ALANG SA (n=1, flag=="OO" UG n<=3, n++)
            PUNDOK{
                IPAKITA: n & ' '
                KUNG (n==2)
                PUNDOK{
                    flag="DILI"
                }
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 ", output);
    }

    @Test
    public void testMultipleSequentialLoops() {
        String source = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=3, i++)
            PUNDOK{
                IPAKITA: i & ' '
            }
            IPAKITA: $
            ALANG SA (i=1, i<=3, i++)
            PUNDOK{
                IPAKITA: i & ' '
            }
            KATAPUSAN
            """;
        
        String output = runProgram(source);
        assertEquals("1 2 3 \n1 2 3 ", output);
    }
}
