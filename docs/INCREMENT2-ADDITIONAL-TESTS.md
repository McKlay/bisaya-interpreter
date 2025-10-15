# Increment 2 - Proposed Additional Test Cases

This document contains proposed test cases to improve edge case coverage for Increment 2.

---

## 1. Critical Priority Tests

### Test 1.1: Short-Circuit Evaluation for UG (AND)
```java
@Test
@DisplayName("Short-circuit: AND stops on first false, avoids division by zero")
void testShortCircuitAndAvoidsDivisionByZero() {
    String src = """
        SUGOD
        MUGNA NUMERO x=0
        MUGNA TINUOD result
        result = (x == 0) UG (10 / x > 1)
        IPAKITA: result
        KATAPUSAN
        """;
    // Should evaluate to DILI without throwing division by zero error
    assertEquals("DILI\n", runProgram(src));
}
```

**Expected Behavior:** The second operand `(10 / x > 1)` should NOT be evaluated when first is false.

**Current Implementation Status:** ❌ WILL FAIL - both operands evaluated

### Test 1.2: Short-Circuit Evaluation for O (OR)
```java
@Test
@DisplayName("Short-circuit: OR stops on first true, avoids division by zero")
void testShortCircuitOrAvoidsDivisionByZero() {
    String src = """
        SUGOD
        MUGNA NUMERO x=0
        MUGNA TINUOD result
        result = (x == 0) O (10 / x > 1)
        IPAKITA: result
        KATAPUSAN
        """;
    // Should evaluate to OO without evaluating second operand
    assertEquals("OO\n", runProgram(src));
}
```

### Test 1.3: Increment on Uninitialized Variable
```java
@Test
@DisplayName("Increment on uninitialized variable throws error")
void testIncrementUninitializedVariable() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        ++x
        IPAKITA: x
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src),
        "Cannot increment uninitialized variable");
}
```

### Test 1.4: Decrement on Uninitialized Variable
```java
@Test
@DisplayName("Decrement on uninitialized variable throws error")
void testDecrementUninitializedVariable() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        --x
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgram(src),
        "Cannot decrement uninitialized variable");
}
```

---

## 2. High Priority Tests - Arithmetic Edge Cases

### Test 2.1: Negative Number Addition
```java
@Test
@DisplayName("Addition with negative numbers")
void testAdditionWithNegativeNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x=-5, y=-3, z
        z = x + y
        IPAKITA: z
        KATAPUSAN
        """;
    assertEquals("-8\n", runProgram(src));
}
```

### Test 2.2: Negative Number Multiplication
```java
@Test
@DisplayName("Multiplication with negative numbers")
void testMultiplicationWithNegativeNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x=-5, y=-3, z
        z = x * y
        IPAKITA: z
        KATAPUSAN
        """;
    assertEquals("15\n", runProgram(src));
}
```

### Test 2.3: Negative Number Subtraction
```java
@Test
@DisplayName("Subtraction with negative numbers")
void testSubtractionWithNegativeNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x=-5, y=-3, z
        z = x - y
        IPAKITA: z
        KATAPUSAN
        """;
    assertEquals("-2\n", runProgram(src));
}
```

### Test 2.4: Mixed Integer and Decimal Arithmetic
```java
@Test
@DisplayName("Mixed NUMERO and TIPIK addition")
void testMixedNumeroTipikAddition() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5
        MUGNA TIPIK y=2.5, result
        result = x + y
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("7.5\n", runProgram(src));
}
```

### Test 2.5: Integer Division vs Decimal Division
```java
@Test
@DisplayName("Integer division returns integer result")
void testIntegerDivision() {
    String src = """
        SUGOD
        MUGNA NUMERO x=7, y=2, z
        z = x / y
        IPAKITA: z
        KATAPUSAN
        """;
    assertEquals("3\n", runProgram(src));  // Integer division
}
```

### Test 2.6: Large Number Overflow
```java
@Test
@DisplayName("Large number overflow behavior")
void testLargeNumberOverflow() {
    String src = """
        SUGOD
        MUGNA NUMERO x=2147483647
        IPAKITA: x
        x = x + 1
        IPAKITA: x
        KATAPUSAN
        """;
    // This will test Java's integer overflow behavior
    // Might need to document expected behavior
    String output = runProgram(src);
    assertNotNull(output);  // Just ensure it doesn't crash
}
```

### Test 2.7: Modulo with Negative Numbers
```java
@Test
@DisplayName("Modulo with negative numbers")
void testModuloNegativeNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x=-17, y=5, z
        z = x % y
        IPAKITA: z
        KATAPUSAN
        """;
    // Java's modulo with negatives: -17 % 5 = -2
    assertEquals("-2\n", runProgram(src));
}
```

---

## 3. High Priority Tests - Comparison Edge Cases

### Test 3.1: Comparing NUMERO with TIPIK
```java
@Test
@DisplayName("Comparing NUMERO with TIPIK (equal values)")
void testCompareNumeroWithTipikEqual() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5
        MUGNA TIPIK y=5.0
        MUGNA TINUOD result
        result = x == y
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO\n", runProgram(src));
}
```

### Test 3.2: Comparing Negative Numbers
```java
@Test
@DisplayName("Comparing negative numbers")
void testCompareNegativeNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x=-5, y=-10
        MUGNA TINUOD result1, result2
        result1 = x > y
        result2 = x < y
        IPAKITA: result1 & " " & result2
        KATAPUSAN
        """;
    assertEquals("OO DILI\n", runProgram(src));
}
```

### Test 3.3: Floating Point Comparison Precision
```java
@Test
@DisplayName("Floating point comparison precision")
void testFloatingPointComparisonPrecision() {
    String src = """
        SUGOD
        MUGNA TIPIK x=0.1, y=0.2, sum
        sum = x + y
        MUGNA TINUOD result
        result = sum == 0.3
        IPAKITA: sum & " " & result
        KATAPUSAN
        """;
    // This tests the classic 0.1 + 0.2 != 0.3 issue
    String output = runProgram(src);
    assertNotNull(output);  // Document actual behavior
}
```

---

## 4. High Priority Tests - Logical Operations Edge Cases

### Test 4.1: Complex Nested Logical Expression
```java
@Test
@DisplayName("Complex nested logical expression")
void testComplexNestedLogical() {
    String src = """
        SUGOD
        MUGNA TINUOD result
        result = ((10 > 5) UG (20 > 15)) O ((5 > 10) UG (15 > 20))
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO\n", runProgram(src));
}
```

### Test 4.2: DILI with Comparison Result
```java
@Test
@DisplayName("DILI with comparison result")
void testDiliWithComparisonResult() {
    String src = """
        SUGOD
        MUGNA TINUOD result
        result = DILI (5 > 10)
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO\n", runProgram(src));
}
```

### Test 4.3: Double Negation
```java
@Test
@DisplayName("Double negation with DILI")
void testDoubleNegation() {
    String src = """
        SUGOD
        MUGNA TINUOD result
        result = DILI (DILI (5 > 3))
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO\n", runProgram(src));
}
```

---

## 5. High Priority Tests - DAWAT Edge Cases

### Test 5.1: DAWAT with Empty Input
```java
@Test
@DisplayName("DAWAT with empty input throws error")
void testDawatEmptyInput() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        DAWAT: x
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgramWithInput(src, ""),
        "Empty input should throw error");
}
```

### Test 5.2: DAWAT with Only Commas
```java
@Test
@DisplayName("DAWAT with only commas throws error")
void testDawatOnlyCommas() {
    String src = """
        SUGOD
        MUGNA NUMERO x, y
        DAWAT: x, y
        KATAPUSAN
        """;
    assertThrows(RuntimeException.class, () -> runProgramWithInput(src, ","),
        "Only commas should throw error");
}
```

### Test 5.3: DAWAT with Extra Whitespace
```java
@Test
@DisplayName("DAWAT handles extra whitespace correctly")
void testDawatExtraWhitespace() {
    String src = """
        SUGOD
        MUGNA NUMERO x, y
        DAWAT: x, y
        IPAKITA: x & " " & y
        KATAPUSAN
        """;
    assertEquals("10 20\n", runProgramWithInput(src, "  10  ,  20  "));
}
```

### Test 5.4: DAWAT with Scientific Notation
```java
@Test
@DisplayName("DAWAT with scientific notation for TIPIK")
void testDawatScientificNotation() {
    String src = """
        SUGOD
        MUGNA TIPIK x
        DAWAT: x
        IPAKITA: x
        KATAPUSAN
        """;
    // Scientific notation should work with Double.valueOf()
    String output = runProgramWithInput(src, "1e10");
    assertTrue(output.contains("1.0E10") || output.contains("10000000000"));
}
```

### Test 5.5: DAWAT Repeated Input
```java
@Test
@DisplayName("DAWAT can read same variable multiple times")
void testDawatRepeatedInput() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        DAWAT: x
        IPAKITA: x
        DAWAT: x
        IPAKITA: x
        KATAPUSAN
        """;
    String output = runProgramWithInput(src, "10\n20");
    assertEquals("10\n20\n", output);
}
```

### Test 5.6: DAWAT with Negative Numbers
```java
@Test
@DisplayName("DAWAT accepts negative numbers")
void testDawatNegativeNumbers() {
    String src = """
        SUGOD
        MUGNA NUMERO x
        DAWAT: x
        IPAKITA: x
        KATAPUSAN
        """;
    assertEquals("-42\n", runProgramWithInput(src, "-42"));
}
```

---

## 6. Medium Priority Tests - Unary Operators

### Test 6.1: Double Unary Minus
```java
@Test
@DisplayName("Double unary minus")
void testDoubleUnaryMinus() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5, y
        y = --x
        IPAKITA: y
        KATAPUSAN
        """;
    // --x should be -(-x) = -(-5) = 5
    assertEquals("5\n", runProgram(src));
}
```

### Test 6.2: Unary Plus on Negative
```java
@Test
@DisplayName("Unary plus on negative number")
void testUnaryPlusOnNegative() {
    String src = """
        SUGOD
        MUGNA NUMERO x=-10, y
        y = +x
        IPAKITA: y
        KATAPUSAN
        """;
    assertEquals("-10\n", runProgram(src));
}
```

### Test 6.3: Postfix on TIPIK Precision
```java
@Test
@DisplayName("Postfix increment on TIPIK maintains precision")
void testPostfixTipikPrecision() {
    String src = """
        SUGOD
        MUGNA TIPIK x=0.1
        x++
        x++
        x++
        IPAKITA: x
        KATAPUSAN
        """;
    assertEquals("3.1\n", runProgram(src));
}
```

---

## 7. Medium Priority Tests - Expression Complexity

### Test 7.1: Deeply Nested Parentheses
```java
@Test
@DisplayName("Deeply nested parentheses")
void testDeeplyNestedParentheses() {
    String src = """
        SUGOD
        MUGNA NUMERO result
        result = ((((5 + 3) * 2) - 4) / 2)
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("6\n", runProgram(src));
}
```

### Test 7.2: Long Expression Chain
```java
@Test
@DisplayName("Long expression chain")
void testLongExpressionChain() {
    String src = """
        SUGOD
        MUGNA NUMERO result
        result = 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("55\n", runProgram(src));
}
```

### Test 7.3: Mixed Operators with All Precedence Levels
```java
@Test
@DisplayName("Mixed operators verifying precedence")
void testMixedOperatorsPrecedence() {
    String src = """
        SUGOD
        MUGNA NUMERO result
        result = 10 + 5 * 2 - 8 / 4
        IPAKITA: result
        KATAPUSAN
        """;
    // 10 + (5 * 2) - (8 / 4) = 10 + 10 - 2 = 18
    assertEquals("18\n", runProgram(src));
}
```

### Test 7.4: Parentheses Override Precedence
```java
@Test
@DisplayName("Parentheses override precedence")
void testParenthesesOverridePrecedence() {
    String src = """
        SUGOD
        MUGNA NUMERO result
        result = (10 + 5) * (2 - 8) / 4
        IPAKITA: result
        KATAPUSAN
        """;
    // (10 + 5) * (2 - 8) / 4 = 15 * -6 / 4 = -90 / 4 = -22
    assertEquals("-22\n", runProgram(src));
}
```

---

## 8. Low Priority Tests - Stress Tests

### Test 8.1: Very Long Variable Name
```java
@Test
@DisplayName("Very long variable name")
void testVeryLongVariableName() {
    String src = """
        SUGOD
        MUGNA NUMERO this_is_a_very_long_variable_name_that_should_still_work=42
        IPAKITA: this_is_a_very_long_variable_name_that_should_still_work
        KATAPUSAN
        """;
    assertEquals("42\n", runProgram(src));
}
```

### Test 8.2: Many Variables in Single Declaration
```java
@Test
@DisplayName("Many variables in single declaration")
void testManyVariablesInDeclaration() {
    String src = """
        SUGOD
        MUGNA NUMERO a=1, b=2, c=3, d=4, e=5, f=6, g=7, h=8, i=9, j=10
        IPAKITA: a + b + c + d + e + f + g + h + i + j
        KATAPUSAN
        """;
    assertEquals("55\n", runProgram(src));
}
```

---

## 9. Integration Tests - Real World Scenarios

### Test 9.1: Calculator Simulation
```java
@Test
@DisplayName("Calculator simulation with multiple operations")
void testCalculatorSimulation() {
    String src = """
        SUGOD
        MUGNA NUMERO a, b, sum, diff, prod, quot
        DAWAT: a, b
        sum = a + b
        diff = a - b
        prod = a * b
        quot = a / b
        IPAKITA: "Sum: " & sum & $
        IPAKITA: "Diff: " & diff & $
        IPAKITA: "Prod: " & prod & $
        IPAKITA: "Quot: " & quot
        KATAPUSAN
        """;
    String expected = "Sum: 30\nDiff: 10\nProd: 200\nQuot: 2\n";
    assertEquals(expected, runProgramWithInput(src, "20, 10"));
}
```

### Test 9.2: Boolean Logic Evaluation
```java
@Test
@DisplayName("Boolean logic evaluation system")
void testBooleanLogicEvaluation() {
    String src = """
        SUGOD
        MUGNA NUMERO x, y
        DAWAT: x, y
        MUGNA TINUOD isGreater, isEqual, isLessOrEqual, complexResult
        isGreater = x > y
        isEqual = x == y
        isLessOrEqual = x <= y
        complexResult = (isGreater O isEqual) UG (DILI isLessOrEqual O isEqual)
        IPAKITA: "Greater: " & isGreater & $
        IPAKITA: "Equal: " & isEqual & $
        IPAKITA: "LessOrEqual: " & isLessOrEqual & $
        IPAKITA: "Complex: " & complexResult
        KATAPUSAN
        """;
    String output = runProgramWithInput(src, "10, 5");
    assertTrue(output.contains("Greater: OO"));
    assertTrue(output.contains("Equal: DILI"));
}
```

---

## Summary

**Total Proposed Tests: 42**

| Priority | Category | Count |
|----------|----------|-------|
| Critical | Short-circuit, Uninitialized | 4 |
| High | Arithmetic Edge Cases | 7 |
| High | Comparison Edge Cases | 3 |
| High | Logical Edge Cases | 3 |
| High | DAWAT Edge Cases | 6 |
| Medium | Unary Operators | 3 |
| Medium | Expression Complexity | 4 |
| Low | Stress Tests | 2 |
| Integration | Real World | 2 |

**Recommendation:** Implement Critical and High priority tests first (23 tests), then add others as time permits.

---

**Document Version:** 1.0  
**Last Updated:** October 15, 2025
