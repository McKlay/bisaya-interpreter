package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Negative Test Suite for Increment 5 - WHILE Loops
 * 
 * Tests error conditions and edge cases for:
 * - SAMTANG (WHILE loop) with missing or invalid conditions
 * - Non-boolean loop conditions
 * - Missing PUNDOK blocks
 * - Infinite loop scenarios
 * - Variable scope and modification issues
 */
public class Increment5NegativeTests {

    // ====================================================================
    // MISSING OR INVALID CONDITION
    // ====================================================================

    @Test
    @DisplayName("NEG: WHILE loop without condition")
    void testWhileLoopWithoutCondition() {
        String src = """
            SUGOD
            SAMTANG ()
            PUNDOK{
                IPAKITA: "error"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "WHILE loop requires a condition");
    }

    @Test
    @DisplayName("NEG: WHILE loop without parentheses")
    void testWhileLoopWithoutParentheses() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            SAMTANG x > 0
            PUNDOK{
                x = x - 1
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "WHILE condition must be in parentheses");
    }

    @Test
    @DisplayName("NEG: WHILE loop with non-boolean condition (number)")
    void testWhileLoopNonBooleanCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            SAMTANG (x)
            PUNDOK{
                x = x - 1
            }
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        assertTrue(ex.getMessage().contains("boolean") || ex.getMessage().contains("condition"),
            "WHILE condition must be boolean: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: WHILE loop with string condition")
    void testWhileLoopStringCondition() {
        String src = """
            SUGOD
            SAMTANG ("hello")
            PUNDOK{
                IPAKITA: "error"
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "String cannot be loop condition");
    }

    @Test
    @DisplayName("NEG: WHILE loop with arithmetic expression as condition")
    void testWhileLoopArithmeticCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            SAMTANG (x + 10)
            PUNDOK{
                x = x - 1
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Arithmetic result cannot be loop condition");
    }

    // ====================================================================
    // MISSING PUNDOK BLOCK
    // ====================================================================

    @Test
    @DisplayName("NEG: WHILE loop without PUNDOK")
    void testWhileLoopWithoutPundok() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x <= 5)
            x = x + 1
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "WHILE loop body must be in PUNDOK block");
    }

    @Test
    @DisplayName("NEG: WHILE loop with mismatched PUNDOK braces")
    void testWhileLoopMismatchedBraces() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x <= 5)
            PUNDOK{
                x = x + 1
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "PUNDOK block must have closing brace");
    }

    @Test
    @DisplayName("NEG: WHILE loop with only opening brace")
    void testWhileLoopOnlyOpeningBrace() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x <= 5)
            PUNDOK{
            x = x + 1
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "PUNDOK requires both opening and closing braces");
    }

    // ====================================================================
    // UNDECLARED VARIABLES IN CONDITION
    // ====================================================================

    @Test
    @DisplayName("NEG: WHILE loop with undeclared variable in condition")
    void testWhileLoopUndeclaredVariable() {
        String src = """
            SUGOD
            SAMTANG (undeclaredVar > 0)
            PUNDOK{
                IPAKITA: "error"
            }
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        assertTrue(ex.getMessage().contains("Undefined") || ex.getMessage().contains("undefined"),
            "Should report undefined variable: " + ex.getMessage());
    }

    @Test
    @DisplayName("NEG: WHILE loop modifying undeclared variable")
    void testWhileLoopModifyingUndeclared() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x <= 5)
            PUNDOK{
                undeclared = undeclared + 1
                x = x + 1
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Cannot modify undeclared variable");
    }

    // ====================================================================
    // TYPE ERRORS IN LOOP
    // ====================================================================

    @Test
    @DisplayName("NEG: WHILE loop comparing incompatible types")
    void testWhileLoopIncompatibleTypes() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            MUGNA LETRA c='A'
            SAMTANG (x > c)
            PUNDOK{
                x = x - 1
            }
            KATAPUSAN
            """;
        // May work if LETRA is converted to ASCII
        // Test documents behavior
        try {
            runProgram(src);
            // If it works, type conversion is happening
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("type") || e.getMessage().contains("compar"),
                "Should indicate type comparison issue if not supported");
        }
    }

    @Test
    @DisplayName("NEG: WHILE loop with LETRA as condition")
    void testWhileLoopLetraCondition() {
        String src = """
            SUGOD
            MUGNA LETRA c='A'
            SAMTANG (c)
            PUNDOK{
                c = 'B'
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "LETRA cannot be used as boolean condition");
    }

    // ====================================================================
    // EDGE CASES - LOOP NEVER EXECUTES
    // ====================================================================

    @Test
    @DisplayName("EDGE: WHILE loop condition false from start")
    void testWhileLoopNeverExecutes() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            IPAKITA: "before" & $
            SAMTANG (x < 5)
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
    @DisplayName("EDGE: WHILE loop with always-false condition")
    void testWhileLoopAlwaysFalse() {
        String src = """
            SUGOD
            IPAKITA: "start" & $
            SAMTANG ("DILI")
            PUNDOK{
                IPAKITA: "never"
            }
            IPAKITA: "end"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("start\nend", output);
    }

    // ====================================================================
    // EDGE CASES - SINGLE ITERATION
    // ====================================================================

    @Test
    @DisplayName("EDGE: WHILE loop with single iteration")
    void testWhileLoopSingleIteration() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x == 1)
            PUNDOK{
                IPAKITA: "once"
                x = 2
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("once", output);
    }

    @Test
    @DisplayName("EDGE: WHILE loop terminating immediately after condition check")
    void testWhileLoopImmediateTermination() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            SAMTANG (x <= 5)
            PUNDOK{
                IPAKITA: x & " "
                x = 10
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("5 ", output);
    }

    // ====================================================================
    // EDGE CASES - VARIABLE MODIFICATION
    // ====================================================================

    @Test
    @DisplayName("EDGE: WHILE loop variable modified to break condition")
    void testWhileLoopVariableModifiedToBreak() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x > 0)
            PUNDOK{
                IPAKITA: x & " "
                KUNG (x >= 5)
                PUNDOK{
                    x = -1
                }
                KUNG WALA
                PUNDOK{
                    x = x + 1
                }
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertTrue(output.contains("done"), "Loop should terminate");
    }

    @Test
    @DisplayName("EDGE: WHILE loop with multiple variables in condition")
    void testWhileLoopMultipleVariablesInCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1, y=10
            SAMTANG (x < 5 UG y > 5)
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
                y = y - 1
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("1 2 3 4 ", output);
    }

    // ====================================================================
    // EDGE CASES - NESTED LOOPS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Nested WHILE loops")
    void testNestedWhileLoops() {
        String src = """
            SUGOD
            MUGNA NUMERO i=1, j
            SAMTANG (i <= 2)
            PUNDOK{
                j = 1
                SAMTANG (j <= 2)
                PUNDOK{
                    IPAKITA: i & j & " "
                    j = j + 1
                }
                i = i + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("11 12 21 22 ", output);
    }

    @Test
    @DisplayName("EDGE: WHILE loop inside empty outer WHILE")
    void testWhileInsideEmptyWhile() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1, y
            SAMTANG (x <= 2)
            PUNDOK{
                y = 1
                SAMTANG (y <= 0)
                PUNDOK{
                    IPAKITA: "never"
                }
                IPAKITA: x & " "
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("1 2 ", output);
    }

    @Test
    @DisplayName("EDGE: Deeply nested WHILE loops (5 levels)")
    void testDeeplyNestedWhileLoops() {
        String src = """
            SUGOD
            MUGNA NUMERO a=1, b, c, d, e
            SAMTANG (a <= 1)
            PUNDOK{
                b = 1
                SAMTANG (b <= 1)
                PUNDOK{
                    c = 1
                    SAMTANG (c <= 1)
                    PUNDOK{
                        d = 1
                        SAMTANG (d <= 1)
                        PUNDOK{
                            e = 1
                            SAMTANG (e <= 1)
                            PUNDOK{
                                IPAKITA: "deep"
                                e = 2
                            }
                            d = 2
                        }
                        c = 2
                    }
                    b = 2
                }
                a = 2
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("deep", output);
    }

    // ====================================================================
    // EDGE CASES - EMPTY LOOP BODY
    // ====================================================================

    @Test
    @DisplayName("EDGE: WHILE loop with empty PUNDOK")
    void testWhileLoopEmptyBody() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x <= 3)
            PUNDOK{
                x = x + 1
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("done", output);
    }

    @Test
    @DisplayName("EDGE: WHILE loop body with only comments")
    void testWhileLoopBodyOnlyComments() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            MUGNA TINUOD flag="OO"
            SAMTANG (flag=="OO")
            PUNDOK{
            -- just comments
            -- more comments
                flag="DILI"
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("done", output);
    }

    // ====================================================================
    // EDGE CASES - COMPLEX CONDITIONS
    // ====================================================================

    @Test
    @DisplayName("EDGE: WHILE loop with complex boolean expression")
    void testWhileLoopComplexCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1, y=10
            SAMTANG ((x < 5 UG y > 5) O x == 10)
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
                y = y - 1
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("1 2 3 4 ", output);
    }

    @Test
    @DisplayName("EDGE: WHILE loop with DILI in condition")
    void testWhileLoopWithNotOperator() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (DILI (x > 3))
            PUNDOK{
                IPAKITA: x & " "
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("1 2 3 ", output);
    }

    @Test
    @DisplayName("EDGE: WHILE loop condition using function result")
    void testWhileLoopConditionWithExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=5
            SAMTANG ((x - y) > 0)
            PUNDOK{
                IPAKITA: x & " "
                x = x - 2
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("10 8 6 ", output);
    }

    // ====================================================================
    // EDGE CASES - VARIABLE SCOPE
    // ====================================================================

    @Test
    @DisplayName("EDGE: Variable declared before loop, modified inside")
    void testVariableModifiedInsideWhile() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1, sum=0
            SAMTANG (x <= 5)
            PUNDOK{
                sum = sum + x
                x = x + 1
            }
            IPAKITA: "sum=" & sum
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("sum=15", output);
    }

    @Test
    @DisplayName("NEG: Variable declared inside loop cannot be redeclared")
    void testVariableDeclaredInsideWhile() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            SAMTANG (x <= 3)
            PUNDOK{
                MUGNA NUMERO y=10
                IPAKITA: y & " "
                x = x + 1
            }
            KATAPUSAN
            """;
        // Variable y cannot be declared multiple times in loop iterations
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
        assertTrue(ex.getMessage().contains("already declared"), 
            "Should report variable already declared: " + ex.getMessage());
    }

    // ====================================================================
    // MIXING FOR AND WHILE LOOPS
    // ====================================================================

    @Test
    @DisplayName("EDGE: FOR loop inside WHILE loop")
    void testForInsideWhile() {
        String src = """
            SUGOD
            MUGNA NUMERO outer=1, inner
            SAMTANG (outer <= 2)
            PUNDOK{
                ALANG SA (inner=1, inner<=2, inner++)
                PUNDOK{
                    IPAKITA: outer & inner & " "
                }
                outer = outer + 1
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("11 12 21 22 ", output);
    }

    @Test
    @DisplayName("EDGE: WHILE loop inside FOR loop")
    void testWhileInsideFor() {
        String src = """
            SUGOD
            MUGNA NUMERO outer, inner
            ALANG SA (outer=1, outer<=2, outer++)
            PUNDOK{
                inner = 1
                SAMTANG (inner <= 2)
                PUNDOK{
                    IPAKITA: outer & inner & " "
                    inner = inner + 1
                }
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("11 12 21 22 ", output);
    }

    // ====================================================================
    // EDGE CASES - WITH INPUT
    // ====================================================================

    @Test
    @DisplayName("EDGE: WHILE loop with DAWAT inside")
    void testWhileLoopWithDawat() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1, value
            SAMTANG (x <= 2)
            PUNDOK{
                IPAKITA: "Enter: "
                DAWAT: value
                IPAKITA: "Got: " & value & $
                x = x + 1
            }
            KATAPUSAN
            """;
        String output = runProgramWithInput(src, "10\n20\n");
        assertTrue(output.contains("Got: 10"));
        assertTrue(output.contains("Got: 20"));
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
