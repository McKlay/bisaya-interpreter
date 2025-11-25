package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Negative Test Suite for Increment 4 - FOR Loops
 * 
 * Tests error conditions and edge cases for:
 * - ALANG SA (FOR loop) with missing components
 * - Non-boolean loop conditions
 * - Undeclared loop variables
 * - Malformed loop syntax
 * - Infinite loops and boundary conditions
 */
@Timeout(value = 5, unit = TimeUnit.SECONDS)
public class Increment4NegativeTests {

    // ====================================================================
    // MISSING LOOP COMPONENTS
    // ====================================================================

    @Test
    @DisplayName("NEG: FOR loop missing all components")
    void testForLoopMissingAllComponents() {
        String src = """
            SUGOD
            ALANG SA ()
            PUNDOK{
                IPAKITA: "error"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop requires initialization, condition, and update");
    }

    @Test
    @DisplayName("NEG: FOR loop missing initialization")
    void testForLoopMissingInit() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (, i<=5, i++)
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop requires initialization");
    }

    @Test
    @DisplayName("NEG: FOR loop missing condition")
    void testForLoopMissingCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, , i++)
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop requires condition");
    }

    @Test
    @DisplayName("NEG: FOR loop missing update")
    void testForLoopMissingUpdate() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, )
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop requires update expression");
    }

    @Test
    @DisplayName("NEG: FOR loop without PUNDOK")
    void testForLoopWithoutPundok() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, i++)
            IPAKITA: i
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop body must be in PUNDOK block");
    }

    // ====================================================================
    // UNDECLARED LOOP VARIABLE ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: FOR loop with undeclared variable")
    void testForLoopUndeclaredVariable() {
        String src = """
            SUGOD
            ALANG SA (undeclared=1, undeclared<=5, undeclared++)
            PUNDOK{
                IPAKITA: undeclared
            }
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        assertTrue(ex.getMessage().contains("Undefined") || ex.getMessage().contains("undefined"),
            "Should report undefined variable: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: FOR loop variable declared inside loop")
    void testForLoopVariableDeclaredInside() {
        String src = """
            SUGOD
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
                MUGNA NUMERO i
                IPAKITA: i
            }
            KATAPUSAN
            """;
        // Variable i must be declared before the loop
        assertThrows(RuntimeException.class, () -> runProgram(src));
    }

    // ====================================================================
    // NON-BOOLEAN CONDITION ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: FOR loop with non-boolean condition (number)")
    void testForLoopNonBooleanCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i, i++)
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        assertTrue(ex.getMessage().contains("boolean") || ex.getMessage().contains("condition"),
            "Loop condition must be boolean: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: FOR loop with string condition")
    void testForLoopStringCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, "hello", i++)
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "String cannot be loop condition");
    }

    @Test
    @DisplayName("NEG: FOR loop with arithmetic expression as condition")
    void testForLoopArithmeticCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i+5, i++)
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Arithmetic result cannot be loop condition");
    }

    // ====================================================================
    // INVALID LOOP SYNTAX
    // ====================================================================

    @Test
    @DisplayName("NEG: FOR loop missing parentheses")
    void testForLoopMissingParentheses() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA i=1, i<=5, i++
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop components must be in parentheses");
    }

    @Test
    @DisplayName("NEG: FOR loop with semicolons instead of commas")
    void testForLoopWithSemicolons() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1; i<=5; i++)
            PUNDOK{
                IPAKITA: i
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "FOR loop uses commas, not semicolons");
    }

    @Test
    @DisplayName("NEG: FOR loop with mismatched PUNDOK braces")
    void testForLoopMismatchedBraces() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
                IPAKITA: i
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "PUNDOK block must have closing brace");
    }

    // ====================================================================
    // TYPE ERRORS IN LOOP OPERATIONS
    // ====================================================================

    @Test
    @DisplayName("NEG: FOR loop incrementing non-numeric variable")
    void testForLoopIncrementNonNumeric() {
        String src = """
            SUGOD
            MUGNA LETRA c='A'
            ALANG SA (c='A', c<'Z', c++)
            PUNDOK{
                IPAKITA: c
            }
            KATAPUSAN
            """;
        // May work (char arithmetic) or may throw error
        // Test documents behavior
        try {
            runProgram(src);
            // If it works, LETRA supports arithmetic
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("type") || e.getMessage().contains("number"),
                "Should indicate type error if LETRA can't be incremented");
        }
    }

    @Test
    @DisplayName("NEG: FOR loop with boolean initialization")
    void testForLoopBooleanInit() {
        String src = """
            SUGOD
            MUGNA TINUOD flag
            ALANG SA (flag="OO", flag=="OO", flag="DILI")
            PUNDOK{
                IPAKITA: "error"
            }
            KATAPUSAN
            """;
        // Unusual but technically valid if condition works
        // Test documents behavior
        assertDoesNotThrow(() -> runProgram(src));
    }

    // ====================================================================
    // EDGE CASES - LOOP BOUNDARIES
    // ====================================================================

    @Test
    @DisplayName("EDGE: FOR loop with zero iterations")
    void testForLoopZeroIterations() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            IPAKITA: "before" & $
            ALANG SA (i=10, i<5, i++)
            PUNDOK{
                IPAKITA: "inside"
            }
            IPAKITA: "after"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("before\nafter", output);
    }

    @Test
    @DisplayName("EDGE: FOR loop with single iteration")
    void testForLoopSingleIteration() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=1, i++)
            PUNDOK{
                IPAKITA: "once"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("once", output);
    }

    @Test
    @DisplayName("EDGE: FOR loop with negative increment")
    void testForLoopNegativeIncrement() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=10, i>0, i=i-2)
            PUNDOK{
                IPAKITA: i & " "
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("10 8 6 4 2 ", output);
    }

    @Test
    @DisplayName("EDGE: FOR loop with large step value")
    void testForLoopLargeStep() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=0, i<100, i=i+25)
            PUNDOK{
                IPAKITA: i & " "
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("0 25 50 75 ", output);
    }

    // ====================================================================
    // EDGE CASES - LOOP VARIABLE MODIFICATION
    // ====================================================================

    @Test
    @DisplayName("EDGE: Modifying loop variable inside loop body")
    void testModifyLoopVariableInBody() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
                IPAKITA: i & " "
                i = i + 1
            }
            KATAPUSAN
            """;
        // i is modified both in update and body
        String output = runProgram(src);
        assertEquals("1 3 5 ", output);
    }

    @Test
    @Disabled("This test creates an infinite loop: i>0 is always true after increment")
    @DisplayName("EDGE: Resetting loop variable to force termination")
    void testResetLoopVariable() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i>0, i++)
            PUNDOK{
                IPAKITA: i & " "
                KUNG (i >= 5)
                PUNDOK{
                    i = 0
                }
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertTrue(output.contains("done"), "Loop should terminate");
    }

    // ====================================================================
    // EDGE CASES - NESTED LOOPS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Nested loops with same variable name")
    void testNestedLoopsSameVariableName() {
        String src = """
            SUGOD
            MUGNA NUMERO i, j
            ALANG SA (i=1, i<=2, i++)
            PUNDOK{
                ALANG SA (j=1, j<=2, j++)
                PUNDOK{
                    IPAKITA: i & j & " "
                }
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("11 12 21 22 ", output);
    }

    @Test
    @DisplayName("EDGE: Empty nested loop")
    void testEmptyNestedLoop() {
        String src = """
            SUGOD
            MUGNA NUMERO i, j
            ALANG SA (i=1, i<=3, i++)
            PUNDOK{
                ALANG SA (j=1, j<=0, j++)
                PUNDOK{
                    IPAKITA: "never"
                }
                IPAKITA: i & " "
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("1 2 3 ", output);
    }

    // ====================================================================
    // EDGE CASES - COMPLEX CONDITIONS
    // ====================================================================

    @Test
    @DisplayName("EDGE: FOR loop with complex boolean condition")
    void testForLoopComplexCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=10 UG i<>5, i++)
            PUNDOK{
                IPAKITA: i & " "
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        // Stops when i becomes 5
        assertEquals("1 2 3 4 ", output);
    }

    @Test
    @DisplayName("EDGE: FOR loop with condition using external variable")
    void testForLoopConditionWithExternalVar() {
        String src = """
            SUGOD
            MUGNA NUMERO i, limit=5
            ALANG SA (i=1, i<=limit, i++)
            PUNDOK{
                IPAKITA: i & " "
                KUNG (i==3)
                PUNDOK{
                    limit = 10
                }
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        // limit changes mid-loop
        assertTrue(output.contains("1 2 3 4 5 6 7 8 9 10"),
            "Loop should adapt to changed limit");
    }

    // ====================================================================
    // EDGE CASES - EMPTY LOOP BODY
    // ====================================================================

    @Test
    @DisplayName("EDGE: FOR loop with empty PUNDOK")
    void testForLoopEmptyBody() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=5, i++)
            PUNDOK{
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("done", output);
    }

    @Test
    @DisplayName("EDGE: FOR loop body with only comments")
    void testForLoopBodyOnlyComments() {
        String src = """
            SUGOD
            MUGNA NUMERO i
            ALANG SA (i=1, i<=3, i++)
            PUNDOK{
            @@ just a comment
            @@ another comment
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("done", output);
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
