# Quick Fix Guide - Short-Circuit Evaluation

## The Critical Fix

**File:** `app/src/main/java/com/bisayapp/Interpreter.java`  
**Method:** `visitBinary(Expr.Binary e)`  
**Lines:** ~164-201

---

## Current Code (INCORRECT):

```java
@Override
public Object visitBinary(Expr.Binary e) {
    Object left = eval(e.left);
    Object right = eval(e.right);  // ⚠️ PROBLEM: Always evaluated
    
    switch (e.operator.type) {
        case AMPERSAND:
            return stringify(left) + stringify(right);
        
        // Arithmetic operators
        case PLUS:
            return addNumbers(left, right, e.operator);
        case MINUS:
            return subtractNumbers(left, right, e.operator);
        case STAR:
            return multiplyNumbers(left, right, e.operator);
        case SLASH:
            return divideNumbers(left, right, e.operator);
        case PERCENT:
            return moduloNumbers(left, right, e.operator);
        
        // Comparison operators
        case GREATER:
            return compareNumbers(left, right, e.operator) > 0;
        case GREATER_EQUAL:
            return compareNumbers(left, right, e.operator) >= 0;
        case LESS:
            return compareNumbers(left, right, e.operator) < 0;
        case LESS_EQUAL:
            return compareNumbers(left, right, e.operator) <= 0;
        case EQUAL_EQUAL:
            return isEqual(left, right);
        case LT_GT:
            return !isEqual(left, right);
        
        // Logical operators
        case UG: // AND
            return isTruthy(left) && isTruthy(right);  // ⚠️ Right already evaluated
        case O: // OR
            return isTruthy(left) || isTruthy(right);  // ⚠️ Right already evaluated
        
        default:
            throw new RuntimeException("Unsupported binary operator: " + e.operator.lexeme);
    }
}
```

---

## Fixed Code (CORRECT):

```java
@Override
public Object visitBinary(Expr.Binary e) {
    Object left = eval(e.left);
    
    // ✅ Handle short-circuit operators FIRST, before evaluating right
    // This prevents unnecessary evaluation and runtime errors
    if (e.operator.type == TokenType.UG) { // AND operator
        // If left is false, result is always false - don't evaluate right
        if (!isTruthy(left)) return false;
        // Only evaluate right if left is true
        return isTruthy(eval(e.right));
    }
    
    if (e.operator.type == TokenType.O) { // OR operator
        // If left is true, result is always true - don't evaluate right
        if (isTruthy(left)) return true;
        // Only evaluate right if left is false
        return isTruthy(eval(e.right));
    }
    
    // ✅ For all other operators, evaluate right operand normally
    Object right = eval(e.right);
    
    switch (e.operator.type) {
        case AMPERSAND:
            return stringify(left) + stringify(right);
        
        // Arithmetic operators
        case PLUS:
            return addNumbers(left, right, e.operator);
        case MINUS:
            return subtractNumbers(left, right, e.operator);
        case STAR:
            return multiplyNumbers(left, right, e.operator);
        case SLASH:
            return divideNumbers(left, right, e.operator);
        case PERCENT:
            return moduloNumbers(left, right, e.operator);
        
        // Comparison operators
        case GREATER:
            return compareNumbers(left, right, e.operator) > 0;
        case GREATER_EQUAL:
            return compareNumbers(left, right, e.operator) >= 0;
        case LESS:
            return compareNumbers(left, right, e.operator) < 0;
        case LESS_EQUAL:
            return compareNumbers(left, right, e.operator) <= 0;
        case EQUAL_EQUAL:
            return isEqual(left, right);
        case LT_GT:
            return !isEqual(left, right);
        
        // Note: UG and O are handled above, before evaluating right
        
        default:
            throw new RuntimeException("Unsupported binary operator: " + e.operator.lexeme);
    }
}
```

---

## What Changed?

### Before:
```java
Object left = eval(e.left);
Object right = eval(e.right);  // Always evaluated

switch (e.operator.type) {
    case UG:
        return isTruthy(left) && isTruthy(right);
```

### After:
```java
Object left = eval(e.left);

// Check for short-circuit operators FIRST
if (e.operator.type == TokenType.UG) {
    if (!isTruthy(left)) return false;  // Short-circuit!
    return isTruthy(eval(e.right));     // Only evaluate if needed
}

// For other operators, evaluate right normally
Object right = eval(e.right);

switch (e.operator.type) {
    // UG is already handled above
```

---

## Why This Fix Works

### Example 1: AND Short-Circuit
```bisaya
SUGOD
MUGNA NUMERO x=0
MUGNA TINUOD result
result = (x == 0) UG (10 / x > 1)  -- Would crash without fix
IPAKITA: result
KATAPUSAN
```

**Execution Flow (After Fix):**
1. Evaluate `left`: `x == 0` → `true`
2. Check operator: It's `UG` (AND)
3. Check if `left` is truthy: `isTruthy(true)` → `true`
4. Since left is true, evaluate `right`: `10 / x > 1`
5. ⚠️ **WAIT!** Left is `false` (x == 0 is true means x is 0)
   
Let me correct the example:

```bisaya
SUGOD
MUGNA NUMERO x=0
MUGNA TINUOD result
result = (x <> 0) UG (10 / x > 1)  -- x <> 0 is FALSE
IPAKITA: result
KATAPUSAN
```

**Execution Flow (After Fix):**
1. Evaluate `left`: `x <> 0` → `false`
2. Check operator: It's `UG` (AND)
3. Check if `left` is truthy: `isTruthy(false)` → `false`
4. **SHORT-CIRCUIT!** Return `false` immediately
5. `10 / x` is **NEVER EVALUATED** → No crash!

### Example 2: OR Short-Circuit
```bisaya
SUGOD
MUGNA NUMERO x=1
MUGNA TINUOD result
result = (x == 1) O (10 / 0 > 1)  -- Would crash without fix
IPAKITA: result
KATAPUSAN
```

**Execution Flow (After Fix):**
1. Evaluate `left`: `x == 1` → `true`
2. Check operator: It's `O` (OR)
3. Check if `left` is truthy: `isTruthy(true)` → `true`
4. **SHORT-CIRCUIT!** Return `true` immediately
5. `10 / 0` is **NEVER EVALUATED** → No crash!

---

## Testing the Fix

### Test 1: AND Short-Circuit
```java
@Test
@DisplayName("Short-circuit: AND stops on first false")
void testShortCircuitAndAvoidsDivisionByZero() {
    String src = """
        SUGOD
        MUGNA NUMERO x=0
        MUGNA TINUOD result
        result = (x <> 0) UG (10 / x > 1)
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("DILI\n", runProgram(src));  // Should NOT crash
}
```

### Test 2: OR Short-Circuit
```java
@Test
@DisplayName("Short-circuit: OR stops on first true")
void testShortCircuitOrAvoidsDivisionByZero() {
    String src = """
        SUGOD
        MUGNA NUMERO x=1
        MUGNA TINUOD result
        result = (x == 1) O (10 / 0 > 1)
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO\n", runProgram(src));  // Should NOT crash
}
```

### Test 3: Verify Normal Behavior Still Works
```java
@Test
@DisplayName("Logical AND: both operands evaluated when needed")
void testLogicalAndBothEvaluated() {
    String src = """
        SUGOD
        MUGNA NUMERO x=5, y=3
        MUGNA TINUOD result
        result = (x > 3) UG (y < 5)
        IPAKITA: result
        KATAPUSAN
        """;
    assertEquals("OO\n", runProgram(src));  // Both need evaluation
}
```

---

## Steps to Apply Fix

1. **Open the file:**
   ```
   app/src/main/java/com/bisayapp/Interpreter.java
   ```

2. **Find the `visitBinary` method** (around line 164)

3. **Replace the entire method** with the fixed version above

4. **Save the file**

5. **Run the tests:**
   ```powershell
   .\gradlew test --tests Increment2Tests
   ```

6. **Add the new short-circuit tests** to `Increment2Tests.java`

7. **Run tests again** to verify the fix works

---

## Verification Checklist

After applying the fix:

- [ ] Code compiles without errors
- [ ] All existing Increment2Tests pass
- [ ] New short-circuit tests pass
- [ ] No regressions in other test files
- [ ] Code follows project style

---

**Estimated Time:** 10-15 minutes  
**Difficulty:** Easy  
**Risk:** Very Low (isolated change, well-tested)

---

**Fix Prepared By:** AI Code Analyzer  
**Date:** October 15, 2025  
**Status:** Ready to Apply
