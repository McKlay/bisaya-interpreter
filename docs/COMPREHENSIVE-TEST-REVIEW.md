# Comprehensive Test Review for Bisaya++ Interpreter
## Increments 2-5: Negative Cases, Edge Cases & Error Reporting

**Date:** October 17, 2025  
**Status:** Test Coverage Analysis & Enhancement Plan

---

## Executive Summary

This document provides a comprehensive review of test cases for Increments 2-5, with focus on:
1. **Negative test cases** - Testing error conditions
2. **Edge cases** - Boundary conditions and unusual inputs
3. **Error reporting** - Proper line/column information in error messages

### Current State Analysis

**Strengths:**
- Good coverage of positive test cases
- Tests for division/modulo by zero exist
- Short-circuit evaluation tests present

**Gaps Identified:**
1. **Missing line/column information in runtime errors**
2. **Insufficient negative test cases** for:
   - Type mismatches
   - Undeclared variables
   - Invalid operator usage
   - Malformed loop structures
   - Invalid conditional expressions
3. **Missing edge cases** for:
   - Numeric overflow
   - Empty blocks
   - Deeply nested structures
   - Variable scope issues

---

## Issue 1: Runtime Errors Lack Line/Column Information

### Problem
Runtime errors in `Interpreter.java` throw `RuntimeException` without location information:
```java
throw new RuntimeException("Division by zero.");
```

### Solution Required
Errors need to include line and column information from the token that caused the error:
```java
throw new RuntimeException("[line " + operator.line + " col " + operator.col + "] Division by zero.");
```

### Affected Components
- `Interpreter.java` - All runtime errors
- `Environment.java` - Type checking errors
- Test cases need to verify error messages contain line/column info

---

## Increment 2: Enhanced Test Cases Needed

### Negative Test Cases Required

#### 1. Type Mismatch Errors
```java
@Test
@DisplayName("NEG: String arithmetic should fail")
void testStringArithmeticError() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        x = "hello" + 5
        KATAPUSAN
        """;
    RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
    assertTrue(ex.getMessage().contains("line"));
    assertTrue(ex.getMessage().contains("col"));
}
```

#### 2. Undeclared Variable Usage
```java
@Test
@DisplayName("NEG: Using undeclared variable in arithmetic")
void testUndeclaredVariableArithmetic() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        x = x + undeclared
        KATAPUSAN
        """;
    RuntimeException ex = assertThrows(RuntimeException.class, () -> runProgram(src));
    assertTrue(ex.getMessage().contains("Undefined variable"));
    assertTrue(ex.getMessage().contains("line"));
}
```

#### 3. Invalid Unary Operator Usage
```java
@Test
@DisplayName("NEG: Increment on non-variable (literal)")
void testIncrementOnLiteral() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        x = ++5
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 4. Type Errors in Comparison
```java
@Test
@DisplayName("NEG: Comparing incompatible types")
void testIncompatibleTypeComparison() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        MUGNA LETRA c='a'
        MUGNA TINUOD result
        result = x > c
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 5. Logical Operators on Non-Boolean
```java
@Test
@DisplayName("NEG: UG operator on numbers")
void testLogicalAndOnNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5, y=10
        MUGNA TINUOD result
        result = x UG y
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

### Edge Cases Required

#### 1. Integer Overflow
```java
@Test
@DisplayName("EDGE: Very large integer arithmetic")
void testLargeIntegerArithmetic() {
    String src = """
        SUGOD
        MUGNA NUMERO x=2147483647
        x = x + 1
        IPAKITA: x
        KATAPUSAN
        """;
    // Test behavior (wrap around or error)
    assertDoesNotThrow(() -> runProgram(src));
}
```

#### 2. Double Negation
```java
@Test
@DisplayName("EDGE: Double unary minus")
void testDoubleNegation() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5, y
        y = --x
        IPAKITA: y
        KATAPUSAN
        """;
    assertEquals("5", runProgram(src));
}
```

#### 3. Complex Logical Expression
```java
@Test
@DisplayName("EDGE: Deeply nested logical operations")
void testDeeplyNestedLogical() {
    String src = """
        SUGOD
        MUGNA TINUOD result
        result = ((10 > 5) UG (20 > 15)) O ((5 < 3) UG (8 > 2))
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO", runProgram(src));
}
```

#### 4. DAWAT Edge Cases
```java
@Test
@DisplayName("EDGE: DAWAT with whitespace in input")
void testDawatWhitespace() {
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
@DisplayName("NEG: DAWAT empty input")
void testDawatEmptyInput() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        DAWAT: x
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgramWithInput(src, ""));
}
```

---

## Increment 3: Enhanced Test Cases Needed

### Negative Test Cases Required

#### 1. Missing PUNDOK
```java
@Test
@DisplayName("NEG: IF without PUNDOK should fail")
void testIfWithoutPundok() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        KUNG (x > 5)
        IPAKITA: "missing pundok"
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 2. Non-Boolean Condition
```java
@Test
@DisplayName("NEG: IF with non-boolean condition")
void testIfNonBooleanCondition() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        KUNG (x)
        PUNDOK{
            IPAKITA: "should fail"
        }
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 3. Mismatched PUNDOK Braces
```java
@Test
@DisplayName("NEG: Mismatched PUNDOK braces")
void testMismatchedPundok() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        KUNG (x > 5)
        PUNDOK{
            IPAKITA: "test"
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 4. KUNG WALA Without KUNG
```java
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
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

### Edge Cases Required

#### 1. Empty PUNDOK Block
```java
@Test
@DisplayName("EDGE: Empty PUNDOK block")
void testEmptyPundokBlock() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        KUNG (x > 5)
        PUNDOK{
        }
        IPAKITA: "done"
        KATAPUSAN
        """;
    assertEquals("done", runProgram(src));
}
```

#### 2. Deeply Nested Conditionals (10+ levels)
```java
@Test
@DisplayName("EDGE: Very deeply nested IF statements")
void testVeryDeeplyNestedIf() {
    // Test 10+ levels of nesting
}
```

#### 3. All Branches Empty
```java
@Test
@DisplayName("EDGE: IF-ELSE-IF with all empty blocks")
void testAllEmptyBranches() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5
        KUNG (x > 10)
        PUNDOK{}
        KUNG DILI (x > 5)
        PUNDOK{}
        KUNG WALA
        PUNDOK{}
        IPAKITA: "done"
        KATAPUSAN
        """;
    assertEquals("done", runProgram(src));
}
```

---

## Increment 4: Enhanced Test Cases Needed

### Negative Test Cases Required

#### 1. Missing Loop Components
```java
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
    assertThrows(RuntimeException.class, () -> runProgram(src));
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
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 2. Non-Boolean Loop Condition
```java
@Test
@DisplayName("NEG: FOR loop with non-boolean condition")
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
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 3. Undeclared Loop Variable
```java
@Test
@DisplayName("NEG: FOR loop with undeclared variable")
void testForLoopUndeclaredVar() {
    String src = """
        SUGOD
        ALANG SA (undeclared=1, undeclared<=5, undeclared++)
        PUNDOK{
            IPAKITA: undeclared
        }
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

### Edge Cases Required

#### 1. Loop Counter Modification in Body
```java
@Test
@DisplayName("EDGE: Modifying loop counter inside loop")
void testLoopCounterModification() {
    // Already exists but ensure proper behavior is tested
}
```

#### 2. Infinite Loop Detection
```java
@Test
@DisplayName("EDGE: Potentially infinite loop")
void testPotentiallyInfiniteLoop() {
    String src = """
        SUGOD
        MUGNA NUMERO i
        ALANG SA (i=1, i>0, i++)
        PUNDOK{
            KUNG (i > 100)
            PUNDOK{
                i = 0
            }
        }
        IPAKITA: "done"
        KATAPUSAN
        """;
    assertEquals("done", runProgram(src));
}
```

#### 3. Negative Step Value
```java
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
    assertEquals("10 8 6 4 2 ", runProgram(src));
}
```

---

## Increment 5: Enhanced Test Cases Needed

### Negative Test Cases Required

#### 1. WHILE Missing Condition
```java
@Test
@DisplayName("NEG: WHILE loop without condition")
void testWhileWithoutCondition() {
    String src = """
        SUGOD
        SAMTANG ()
        PUNDOK{
            IPAKITA: "error"
        }
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 2. WHILE Non-Boolean Condition
```java
@Test
@DisplayName("NEG: WHILE loop with non-boolean condition")
void testWhileNonBooleanCondition() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5
        SAMTANG (x)
        PUNDOK{
            x = x - 1
        }
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

#### 3. WHILE Without PUNDOK
```java
@Test
@DisplayName("NEG: WHILE loop without PUNDOK")
void testWhileWithoutPundok() {
    String src = """
        SUGOD
        MUGNA NUMERO x=1
        SAMTANG (x <= 5)
        x = x + 1
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src));
}
```

### Edge Cases Required

#### 1. Condition Never True
```java
@Test
@DisplayName("EDGE: WHILE condition never true")
void testWhileNeverExecutes() {
    String src = """
        SUGOD
        MUGNA NUMERO x=10
        IPAKITA: "before"
        SAMTANG (x < 5)
        PUNDOK{
            IPAKITA: "inside"
        }
        IPAKITA: "after"
        KATAPUSAN
        """;
    assertEquals("beforeafter", runProgram(src));
}
```

#### 2. Variable Scope in WHILE
```java
@Test
@DisplayName("EDGE: Variable scope in WHILE loop")
void testWhileVariableScope() {
    String src = """
        SUGOD
        MUGNA NUMERO x=1, y
        SAMTANG (x <= 3)
        PUNDOK{
            y = x * 2
            x = x + 1
        }
        IPAKITA: y
        KATAPUSAN
        """;
    assertEquals("6", runProgram(src));
}
```

---

## Implementation Plan

### Phase 1: Enhanced Error Reporting (CRITICAL)
1. Modify `Interpreter.java` to include line/column in all runtime errors
2. Modify `Environment.java` to pass token information for type errors
3. Update error messages to use format: `[line X col Y] Error: message`

### Phase 2: Add Negative Test Cases
1. Create `Increment2NegativeTests.java`
2. Create `Increment3NegativeTests.java`
3. Create `Increment4NegativeTests.java`
4. Create `Increment5NegativeTests.java`

### Phase 3: Add Edge Case Tests
1. Enhance existing test files with edge cases
2. Create `IntegrationEdgeCasesTests.java` for cross-increment scenarios

### Phase 4: Verify Error Messages
All negative tests must verify:
- Exception is thrown
- Message contains `[line X col Y]`
- Message describes the error clearly

---

## Test Template

```java
@Test
@DisplayName("NEG: <description>")
void test<Name>() {
    String src = """
        SUGOD
        <error-producing code>
        KATAPUSAN
        """;
    RuntimeException ex = assertThrows(RuntimeException.class, 
        () -> runProgram(src),
        "<Expected error type>");
    
    // Verify error message has location
    String msg = ex.getMessage();
    assertTrue(msg.matches(".*\\[line \\d+ col \\d+\\].*"), 
        "Error message should contain line and column: " + msg);
    assertTrue(msg.contains("<expected error phrase>"),
        "Error message should describe the problem: " + msg);
}
```

---

## Summary of Required Changes

### Code Changes
1. **Interpreter.java**: Add line/column to all runtime errors
2. **Environment.java**: Include token info in type errors
3. **Parser.java**: Ensure all parse errors use ErrorReporter

### New Test Files
1. `Increment2NegativeTests.java` (~25 tests)
2. `Increment3NegativeTests.java` (~20 tests)
3. `Increment4NegativeTests.java` (~15 tests)
4. `Increment5NegativeTests.java` (~15 tests)
5. `EdgeCaseIntegrationTests.java` (~20 tests)

### Total New Tests: ~95 additional test cases

---

## Success Criteria
✅ All runtime errors include `[line X col Y]` format  
✅ Every feature has at least 3 negative test cases  
✅ Edge cases cover boundary conditions  
✅ Error messages are clear and actionable  
✅ Test coverage > 90% for all increment features  

