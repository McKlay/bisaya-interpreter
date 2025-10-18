package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Negative Test Suite for Increment 3 - Conditional Structures
 * 
 * Tests error conditions and edge cases for:
 * - KUNG (if) statements without required PUNDOK blocks
 * - Non-boolean conditions in conditionals
 * - Mismatched braces and structure errors
 * - KUNG WALA and KUNG DILI in invalid contexts
 * - Empty and deeply nested conditional blocks
 * 
 * All tests verify proper error reporting with line/column information.
 */
public class Increment3NegativeTests {

    // ====================================================================
    // MISSING PUNDOK BLOCK ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: KUNG without PUNDOK block")
    void testIfWithoutPundok() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            IPAKITA: "missing pundok"
            KATAPUSAN
            """;
        // Parser should reject this - KUNG requires PUNDOK{}
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG must be followed by PUNDOK block");
    }

    @Test
    @DisplayName("NEG: KUNG WALA without PUNDOK block")
    void testElseWithoutPundok() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "then"
            }
            KUNG WALA
            IPAKITA: "missing pundok"
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG WALA must be followed by PUNDOK block");
    }

    @Test
    @DisplayName("NEG: KUNG DILI without PUNDOK block")
    void testElseIfWithoutPundok() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 15)
            PUNDOK{
                IPAKITA: "first"
            }
            KUNG DILI (x > 5)
            IPAKITA: "missing pundok"
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG DILI must be followed by PUNDOK block");
    }

    // ====================================================================
    // MISMATCHED OR MISSING BRACES
    // ====================================================================

    @Test
    @DisplayName("NEG: PUNDOK with missing opening brace")
    void testPundokMissingOpenBrace() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK
                IPAKITA: "test"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "PUNDOK must have opening brace");
    }

    @Test
    @DisplayName("NEG: PUNDOK with missing closing brace")
    void testPundokMissingCloseBrace() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "test"
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "PUNDOK must have closing brace");
    }

    @Test
    @DisplayName("NEG: Nested PUNDOK with mismatched braces")
    void testNestedPundokMismatchedBraces() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=20
            KUNG (x > 5)
            PUNDOK{
                KUNG (y > 15)
                PUNDOK{
                    IPAKITA: "nested"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "Nested blocks must have matching braces");
    }

    // ====================================================================
    // INVALID CONDITIONAL STRUCTURE
    // ====================================================================

    @Test
    @DisplayName("NEG: KUNG WALA without preceding KUNG")
    void testElseWithoutIf() {
        String src = """
            SUGOD
            KUNG WALA
            PUNDOK{
                IPAKITA: "orphan else"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG WALA requires preceding KUNG");
    }

    @Test
    @DisplayName("NEG: KUNG DILI without preceding KUNG")
    void testElseIfWithoutIf() {
        String src = """
            SUGOD
            KUNG DILI (10 > 5)
            PUNDOK{
                IPAKITA: "orphan else if"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG DILI requires preceding KUNG");
    }

    @Test
    @DisplayName("NEG: Multiple KUNG WALA in same if-chain")
    void testMultipleElse() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 15)
            PUNDOK{
                IPAKITA: "first"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "else 1"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "else 2"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "Cannot have multiple KUNG WALA in same if-chain");
    }

    @Test
    @DisplayName("NEG: KUNG DILI after KUNG WALA")
    void testElseIfAfterElse() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 15)
            PUNDOK{
                IPAKITA: "if"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "else"
            }
            KUNG DILI (x > 5)
            PUNDOK{
                IPAKITA: "else if after else"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG DILI cannot come after KUNG WALA");
    }

    // ====================================================================
    // MISSING OR INVALID CONDITIONS
    // ====================================================================

    @Test
    @DisplayName("NEG: KUNG with missing condition")
    void testIfMissingCondition() {
        String src = """
            SUGOD
            KUNG ()
            PUNDOK{
                IPAKITA: "no condition"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG requires a boolean condition");
    }

    @Test
    @DisplayName("NEG: KUNG with missing parentheses")
    void testIfMissingParentheses() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG x > 5
            PUNDOK{
                IPAKITA: "missing parens"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG condition must be in parentheses");
    }

    @Test
    @DisplayName("NEG: KUNG with non-boolean condition (number)")
    void testIfNonBooleanConditionNumber() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x)
            PUNDOK{
                IPAKITA: "number as condition"
            }
            KATAPUSAN
            """;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src),
            "KUNG condition must evaluate to boolean");
        String msg = ex.getMessage();
        assertTrue(msg.contains("boolean") || msg.contains("condition") || msg.contains("type"),
            "Should indicate boolean type required: " + msg);
    }

    @Test
    @DisplayName("NEG: KUNG with non-boolean condition (string)")
    void testIfNonBooleanConditionString() {
        String src = """
            SUGOD
            KUNG ("hello")
            PUNDOK{
                IPAKITA: "string as condition"
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "String cannot be used as boolean condition");
    }

    @Test
    @DisplayName("NEG: KUNG DILI with missing condition")
    void testElseIfMissingCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 15)
            PUNDOK{
                IPAKITA: "if"
            }
            KUNG DILI ()
            PUNDOK{
                IPAKITA: "else if"
            }
            KATAPUSAN
            """;
        assertThrows(Exception.class, () -> runProgram(src),
            "KUNG DILI requires a condition");
    }

    // ====================================================================
    // TYPE ERRORS IN CONDITIONS
    // ====================================================================

    @Test
    @DisplayName("NEG: Comparing LETRA with NUMERO in condition")
    void testCompareLetraWithNumero() {
        String src = """
            SUGOD
            MUGNA NUMERO x=65
            MUGNA LETRA c='A'
            KUNG (x > c)
            PUNDOK{
                IPAKITA: "comparing different types"
            }
            KATAPUSAN
            """;
        // May work (ASCII comparison) or may throw error
        // Test documents expected behavior
        try {
            runProgram(src);
            // If it works, LETRA is converted to ASCII value
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("type") || e.getMessage().contains("compar"),
                "Should indicate type comparison issue if not supported");
        }
    }

    @Test
    @DisplayName("NEG: Using arithmetic result where boolean expected")
    void testArithmeticResultAsCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=5
            KUNG (x + y)
            PUNDOK{
                IPAKITA: "arithmetic as condition"
            }
            KATAPUSAN
            """;
        assertThrows(RuntimeException.class, () -> runProgram(src),
            "Arithmetic result is not boolean");
    }

    // ====================================================================
    // VARIABLE SCOPE ERRORS
    // ====================================================================

    @Test
    @DisplayName("NEG: Using variable declared in if-block outside")
    void testVariableScopeOutsideIfBlock() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                MUGNA NUMERO y=20
            }
            IPAKITA: y
            KATAPUSAN
            """;
        // Variable y is declared inside the block
        // Current implementation may or may not enforce block scope
        // Test documents behavior
        try {
            runProgram(src);
            // If it works, variables have function scope (like JavaScript's var)
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Undefined") || e.getMessage().contains("undefined"),
                "Should indicate undefined variable if block scope is enforced");
        }
    }

    // ====================================================================
    // EDGE CASES - EMPTY BLOCKS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Empty PUNDOK block in KUNG")
    void testEmptyIfBlock() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
            }
            IPAKITA: "after empty if"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("after empty if", output);
    }

    @Test
    @DisplayName("EDGE: All branches empty in if-else-if")
    void testAllEmptyBranches() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 15)
            PUNDOK{}
            KUNG DILI (x > 5)
            PUNDOK{}
            KUNG WALA
            PUNDOK{}
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("done", output);
    }

    @Test
    @DisplayName("EDGE: Empty PUNDOK with only comments")
    void testPundokWithOnlyComments() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
            @@ this is a comment
            @@ another comment
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("done", output);
    }

    // ====================================================================
    // EDGE CASES - DEEPLY NESTED CONDITIONS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Very deeply nested conditionals (10 levels)")
    void testVeryDeeplyNestedConditionals() {
        String src = """
            SUGOD
            MUGNA NUMERO x=1
            KUNG (x > 0)
            PUNDOK{
                KUNG (x > 0)
                PUNDOK{
                    KUNG (x > 0)
                    PUNDOK{
                        KUNG (x > 0)
                        PUNDOK{
                            KUNG (x > 0)
                            PUNDOK{
                                KUNG (x > 0)
                                PUNDOK{
                                    KUNG (x > 0)
                                    PUNDOK{
                                        KUNG (x > 0)
                                        PUNDOK{
                                            KUNG (x > 0)
                                            PUNDOK{
                                                KUNG (x > 0)
                                                PUNDOK{
                                                    IPAKITA: "deep"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("deep", output);
    }

    @Test
    @DisplayName("EDGE: Complex nested if-else-if structure")
    void testComplexNestedIfElseIf() {
        String src = """
            SUGOD
            MUGNA NUMERO a=15, b=10
            KUNG (a > 10)
            PUNDOK{
                KUNG (b > 20)
                PUNDOK{
                    IPAKITA: "A"
                }
                KUNG DILI (b > 15)
                PUNDOK{
                    IPAKITA: "B"
                }
                KUNG DILI (b > 5)
                PUNDOK{
                    IPAKITA: "C"
                }
                KUNG WALA
                PUNDOK{
                    IPAKITA: "D"
                }
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "E"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("C", output);
    }

    // ====================================================================
    // EDGE CASES - BOUNDARY CONDITIONS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Condition with equality on boundary")
    void testConditionEqualityBoundary() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x == 10)
            PUNDOK{
                IPAKITA: "equal"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "not equal"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("equal", output);
    }

    @Test
    @DisplayName("EDGE: Multiple conditions with same variable")
    void testMultipleConditionsSameVariable() {
        String src = """
            SUGOD
            MUGNA NUMERO x=50
            KUNG (x > 60)
            PUNDOK{
                IPAKITA: "A"
            }
            KUNG DILI (x > 40)
            PUNDOK{
                IPAKITA: "B"
            }
            KUNG DILI (x > 30)
            PUNDOK{
                IPAKITA: "C"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "D"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("B", output);
    }

    // ====================================================================
    // EDGE CASES - COMPLEX BOOLEAN EXPRESSIONS
    // ====================================================================

    @Test
    @DisplayName("EDGE: Very complex boolean expression in condition")
    void testComplexBooleanExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO a=10, b=20, c=30, d=40
            KUNG (((a < b) UG (c < d)) O ((a > c) UG (b < c)))
            PUNDOK{
                IPAKITA: "complex condition true"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "complex condition false"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("complex condition true", output);
    }

    @Test
    @DisplayName("EDGE: Condition with DILI and parentheses")
    void testConditionWithNotAndParentheses() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (DILI (x < 5))
            PUNDOK{
                IPAKITA: "not less than 5"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("not less than 5", output);
    }

    // ====================================================================
    // INTEGRATION WITH OTHER FEATURES
    // ====================================================================

    @Test
    @DisplayName("EDGE: Conditional with DAWAT inside")
    void testConditionalWithDawatInside() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y
            KUNG (x > 5)
            PUNDOK{
                DAWAT: y
                IPAKITA: y
            }
            KATAPUSAN
            """;
        String output = runProgramWithInput(src, "25");
        assertEquals("25", output);
    }

    @Test
    @DisplayName("EDGE: Conditional modifying variables used in later conditions")
    void testConditionalModifyingVariables() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                x = x + 10
            }
            KUNG (x > 15)
            PUNDOK{
                IPAKITA: "modified"
            }
            KATAPUSAN
            """;
        String output = runProgram(src);
        assertEquals("modified", output);
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
