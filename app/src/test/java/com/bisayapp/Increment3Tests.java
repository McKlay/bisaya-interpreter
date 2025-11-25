package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Comprehensive test suite for Increment 3 features:
 * - KUNG (if) statements with PUNDOK{} blocks
 * - KUNG-KUNG WALA (if-else) statements
 * - KUNG-KUNG DILI (if-else if) with multiple alternatives
 * - Nested conditional structures
 * - Proper boolean expression evaluation
 */
public class Increment3Tests {

    // ====================================================================
    // BASIC IF STATEMENT TESTS
    // ====================================================================

    @Test
    @DisplayName("Simple KUNG statement - condition true")
    void testSimpleIfTrue() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "x is greater than 5"
            }
            KATAPUSAN
            """;
        assertEquals("x is greater than 5", runProgram(src));
    }

    @Test
    @DisplayName("Simple KUNG statement - condition false")
    void testSimpleIfFalse() {
        String src = """
            SUGOD
            MUGNA NUMERO x=3
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "x is greater than 5"
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        assertEquals("done", runProgram(src));
    }

    @Test
    @DisplayName("KUNG statement with arithmetic comparison")
    void testIfWithArithmeticComparison() {
        String src = """
            SUGOD
            MUGNA NUMERO a=100, b=200, c=300
            KUNG (a < b)
            PUNDOK{
                IPAKITA: "a is less than b"
            }
            KATAPUSAN
            """;
        assertEquals("a is less than b", runProgram(src));
    }

    @Test
    @DisplayName("KUNG statement with logical AND")
    void testIfWithLogicalAnd() {
        String src = """
            SUGOD
            MUGNA NUMERO a=100, b=200, c=300
            KUNG (a < b UG c <> 200)
            PUNDOK{
                IPAKITA: "condition is true"
            }
            KATAPUSAN
            """;
        assertEquals("condition is true", runProgram(src));
    }

    @Test
    @DisplayName("KUNG statement with logical OR")
    void testIfWithLogicalOr() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            KUNG (x > 10 O x < 10)
            PUNDOK{
                IPAKITA: "x is not 10"
            }
            KATAPUSAN
            """;
        assertEquals("x is not 10", runProgram(src));
    }

    @Test
    @DisplayName("KUNG statement with DILI (NOT) operator")
    void testIfWithNot() {
        String src = """
            SUGOD
            MUGNA TINUOD flag="DILI"
            KUNG (DILI flag)
            PUNDOK{
                IPAKITA: "flag is false"
            }
            KATAPUSAN
            """;
        assertEquals("flag is false", runProgram(src));
    }

    @Test
    @DisplayName("KUNG statement with multiple statements in block")
    void testIfWithMultipleStatements() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=20
            KUNG (x < y)
            PUNDOK{
                IPAKITA: "First line" & $
                IPAKITA: "Second line" & $
                x = x + 5
                IPAKITA: x
            }
            KATAPUSAN
            """;
        assertEquals("First line\nSecond line\n15", runProgram(src));
    }

    // ====================================================================
    // IF-ELSE (KUNG-KUNG WALA) TESTS
    // ====================================================================

    @Test
    @DisplayName("KUNG-KUNG WALA - then branch executes")
    void testIfElseThenBranch() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "then branch"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "else branch"
            }
            KATAPUSAN
            """;
        assertEquals("then branch", runProgram(src));
    }

    @Test
    @DisplayName("KUNG-KUNG WALA - else branch executes")
    void testIfElseElseBranch() {
        String src = """
            SUGOD
            MUGNA NUMERO x=3
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "then branch"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "else branch"
            }
            KATAPUSAN
            """;
        assertEquals("else branch", runProgram(src));
    }

    @Test
    @DisplayName("KUNG-KUNG WALA with variable assignment")
    void testIfElseWithAssignment() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, result
            KUNG (x > 5)
            PUNDOK{
                result = 100
            }
            KUNG WALA
            PUNDOK{
                result = 200
            }
            IPAKITA: result
            KATAPUSAN
            """;
        assertEquals("100", runProgram(src));
    }

    // ====================================================================
    // ELSE-IF (KUNG-KUNG DILI) TESTS
    // ====================================================================

    @Test
    @DisplayName("KUNG-KUNG DILI - first condition true")
    void testElseIfFirstCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 15)
            PUNDOK{
                IPAKITA: "greater than 15"
            }
            KUNG DILI (x > 5)
            PUNDOK{
                IPAKITA: "greater than 5"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "5 or less"
            }
            KATAPUSAN
            """;
        assertEquals("greater than 5", runProgram(src));
    }

    @Test
    @DisplayName("KUNG-KUNG DILI - else branch executes")
    void testElseIfElseBranch() {
        String src = """
            SUGOD
            MUGNA NUMERO x=3
            KUNG (x > 10)
            PUNDOK{
                IPAKITA: "greater than 10"
            }
            KUNG DILI (x > 5)
            PUNDOK{
                IPAKITA: "greater than 5"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "5 or less"
            }
            KATAPUSAN
            """;
        assertEquals("5 or less", runProgram(src));
    }

    @Test
    @DisplayName("KUNG-KUNG DILI with multiple else-if clauses")
    void testMultipleElseIf() {
        String src = """
            SUGOD
            MUGNA NUMERO score=75
            KUNG (score >= 90)
            PUNDOK{
                IPAKITA: "A"
            }
            KUNG DILI (score >= 80)
            PUNDOK{
                IPAKITA: "B"
            }
            KUNG DILI (score >= 70)
            PUNDOK{
                IPAKITA: "C"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "F"
            }
            KATAPUSAN
            """;
        assertEquals("C", runProgram(src));
    }

    @Test
    @DisplayName("KUNG-KUNG DILI without final else")
    void testElseIfWithoutElse() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            KUNG (x > 10)
            PUNDOK{
                IPAKITA: "greater than 10"
            }
            KUNG DILI (x > 5)
            PUNDOK{
                IPAKITA: "greater than 5"
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        assertEquals("done", runProgram(src));
    }

    // ====================================================================
    // NESTED CONDITIONALS TESTS
    // ====================================================================

    @Test
    @DisplayName("Nested KUNG statements")
    void testNestedIf() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=20
            KUNG (x > 5)
            PUNDOK{
                KUNG (y > 15)
                PUNDOK{
                    IPAKITA: "both conditions true"
                }
            }
            KATAPUSAN
            """;
        assertEquals("both conditions true", runProgram(src));
    }

    @Test
    @DisplayName("Nested KUNG with else branches")
    void testNestedIfElse() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=5
            KUNG (x > 5)
            PUNDOK{
                KUNG (y > 10)
                PUNDOK{
                    IPAKITA: "y > 10"
                }
                KUNG WALA
                PUNDOK{
                    IPAKITA: "y <= 10"
                }
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "x <= 5"
            }
            KATAPUSAN
            """;
        assertEquals("y <= 10", runProgram(src));
    }

    @Test
    @DisplayName("Deeply nested conditionals")
    void testDeeplyNestedConditionals() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            KUNG (x > 0)
            PUNDOK{
                KUNG (x > 3)
                PUNDOK{
                    KUNG (x > 4)
                    PUNDOK{
                        IPAKITA: "x is greater than 4"
                    }
                }
            }
            KATAPUSAN
            """;
        assertEquals("x is greater than 4", runProgram(src));
    }

    @Test
    @DisplayName("Nested if-else-if")
    void testNestedElseIf() {
        String src = """
            SUGOD
            MUGNA NUMERO a=15, b=10
            KUNG (a > 10)
            PUNDOK{
                KUNG (b > 20)
                PUNDOK{
                    IPAKITA: "Case 1"
                }
                KUNG DILI (b > 5)
                PUNDOK{
                    IPAKITA: "Case 2"
                }
                KUNG WALA
                PUNDOK{
                    IPAKITA: "Case 3"
                }
            }
            KATAPUSAN
            """;
        assertEquals("Case 2", runProgram(src));
    }

    // ====================================================================
    // BOOLEAN EXPRESSION EVALUATION TESTS
    // ====================================================================

    @Test
    @DisplayName("Boolean expression with TINUOD variable")
    void testBooleanVariable() {
        String src = """
            SUGOD
            MUGNA TINUOD flag="OO"
            KUNG (flag)
            PUNDOK{
                IPAKITA: "flag is true"
            }
            KATAPUSAN
            """;
        assertEquals("flag is true", runProgram(src));
    }

    @Test
    @DisplayName("Complex boolean expression with UG and O")
    void testComplexBooleanExpression() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=20, z=30
            KUNG ((x < y UG y < z) O x > 100)
            PUNDOK{
                IPAKITA: "complex condition is true"
            }
            KATAPUSAN
            """;
        assertEquals("complex condition is true", runProgram(src));
    }

    @Test
    @DisplayName("Short-circuit evaluation - AND with false first")
    void testShortCircuitAndFalse() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5, y=10
            KUNG ("DILI" UG x > 0)
            PUNDOK{
                IPAKITA: "should not print"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "short circuit worked"
            }
            KATAPUSAN
            """;
        assertEquals("short circuit worked", runProgram(src));
    }

    @Test
    @DisplayName("Short-circuit evaluation - OR with true first")
    void testShortCircuitOrTrue() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            KUNG ("OO" O x > 100)
            PUNDOK{
                IPAKITA: "short circuit worked"
            }
            KATAPUSAN
            """;
        assertEquals("short circuit worked", runProgram(src));
    }

    // ====================================================================
    // INTEGRATION TESTS - SPEC EXAMPLES
    // ====================================================================

    @Test
    @DisplayName("Spec Example 1: Simple arithmetic comparison from spec")
    void testSpecExample1() {
        String src = """
            SUGOD
            MUGNA NUMERO a=100, b=200, c=300
            MUGNA TINUOD d="DILI"
            d = (a < b UG c <> 200)
            KUNG (d)
            PUNDOK{
                IPAKITA: "Condition met"
            }
            KATAPUSAN
            """;
        assertEquals("Condition met", runProgram(src));
    }

    @Test
    @DisplayName("Grade calculation with if-else-if")
    void testGradeCalculation() {
        String src = """
            SUGOD
            MUGNA NUMERO score=85
            MUGNA LETRA grade
            KUNG (score >= 90)
            PUNDOK{
                grade = 'A'
            }
            KUNG DILI (score >= 80)
            PUNDOK{
                grade = 'B'
            }
            KUNG DILI (score >= 70)
            PUNDOK{
                grade = 'C'
            }
            KUNG WALA
            PUNDOK{
                grade = 'F'
            }
            IPAKITA: "Grade: " & grade
            KATAPUSAN
            """;
        assertEquals("Grade: B", runProgram(src));
    }

    @Test
    @DisplayName("Max of two numbers using if-else")
    void testMaxOfTwoNumbers() {
        String src = """
            SUGOD
            MUGNA NUMERO a=15, b=25, max
            KUNG (a > b)
            PUNDOK{
                max = a
            }
            KUNG WALA
            PUNDOK{
                max = b
            }
            IPAKITA: "Max is: " & max
            KATAPUSAN
            """;
        assertEquals("Max is: 25", runProgram(src));
    }

    @Test
    @DisplayName("Leap year check")
    void testLeapYear() {
        String src = """
            SUGOD
            MUGNA NUMERO year=2020
            MUGNA TINUOD isLeap="DILI"
            KUNG (year % 4 == 0)
            PUNDOK{
                KUNG (year % 100 == 0)
                PUNDOK{
                    KUNG (year % 400 == 0)
                    PUNDOK{
                        isLeap = "OO"
                    }
                }
                KUNG WALA
                PUNDOK{
                    isLeap = "OO"
                }
            }
            IPAKITA: isLeap
            KATAPUSAN
            """;
        assertEquals("OO", runProgram(src));
    }

    @Test
    @DisplayName("Number classification (positive/negative/zero)")
    void testNumberClassification() {
        String src = """
            SUGOD
            MUGNA NUMERO num=0
            KUNG (num > 0)
            PUNDOK{
                IPAKITA: "Positive"
            }
            KUNG DILI (num < 0)
            PUNDOK{
                IPAKITA: "Negative"
            }
            KUNG WALA
            PUNDOK{
                IPAKITA: "Zero"
            }
            KATAPUSAN
            """;
        assertEquals("Zero", runProgram(src));
    }

    // ====================================================================
    // EDGE CASES AND ERROR HANDLING
    // ====================================================================

    @Test
    @DisplayName("Empty PUNDOK block")
    void testEmptyBlock() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            KUNG (x > 0)
            PUNDOK{
            }
            IPAKITA: "done"
            KATAPUSAN
            """;
        assertEquals("done", runProgram(src));
    }

    @Test
    @DisplayName("Multiple statements after if without else")
    void testMultipleStatementsAfterIf() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x > 5)
            PUNDOK{
                IPAKITA: "inside if" & $
            }
            IPAKITA: "after if" & $
            IPAKITA: "still after if"
            KATAPUSAN
            """;
        assertEquals("inside if\nafter if\nstill after if", runProgram(src));
    }

    @Test
    @DisplayName("Variables modified inside if block are visible outside")
    void testVariableScopeInIfBlock() {
        String src = """
            SUGOD
            MUGNA NUMERO x=5
            KUNG (x > 0)
            PUNDOK{
                x = x + 10
            }
            IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("15", runProgram(src));
    }

    @Test
    @DisplayName("Comparison with equality operator")
    void testEqualityInCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x == 10)
            PUNDOK{
                IPAKITA: "x equals 10"
            }
            KATAPUSAN
            """;
        assertEquals("x equals 10", runProgram(src));
    }

    @Test
    @DisplayName("Comparison with not-equal operator")
    void testNotEqualInCondition() {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            KUNG (x <> 5)
            PUNDOK{
                IPAKITA: "x is not 5"
            }
            KATAPUSAN
            """;
        assertEquals("x is not 5", runProgram(src));
    }

    // ====================================================================
    // HELPER METHODS
    // ====================================================================

    private String runProgram(String source) {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outContent);
        
        try {
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            Parser parser = new Parser(tokens);
            List<Stmt> stmts = parser.parseProgram();
            Interpreter interp = new Interpreter(ps);
            interp.interpret(stmts);
            return outContent.toString();
        } catch (Exception e) {
            fail("Program execution failed: " + e.getMessage());
            return "";
        }
    }
}
