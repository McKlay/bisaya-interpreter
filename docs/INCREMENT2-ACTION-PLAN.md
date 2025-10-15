# Increment 2 - Action Plan

## Priority 1: CRITICAL FIX REQUIRED ⚠️

### Issue: No Short-Circuit Evaluation for Logical Operators

**File:** `Interpreter.java`  
**Lines:** ~179-186  
**Severity:** HIGH

#### Current Implementation (INCORRECT):
```java
@Override
public Object visitBinary(Expr.Binary e) {
    Object left = eval(e.left);
    Object right = eval(e.right);  // ⚠️ ALWAYS EVALUATED
    
    switch (e.operator.type) {
        // ...
        case UG: // AND
            return isTruthy(left) && isTruthy(right);
        case O: // OR
            return isTruthy(left) || isTruthy(right);
```

#### Problem:
Both operands are evaluated **before** checking the operator. This causes:
```bisaya
MUGNA NUMERO x=0
result = (x <> 0) UG (10 / x > 1)  -- CRASHES with division by zero!
```

#### Required Fix:
```java
@Override
public Object visitBinary(Expr.Binary e) {
    Object left = eval(e.left);
    
    // Handle short-circuit operators FIRST, before evaluating right
    if (e.operator.type == TokenType.UG) { // AND
        if (!isTruthy(left)) return false;  // Short-circuit
        return isTruthy(eval(e.right));     // Only eval right if left is true
    }
    
    if (e.operator.type == TokenType.O) { // OR
        if (isTruthy(left)) return true;    // Short-circuit
        return isTruthy(eval(e.right));     // Only eval right if left is false
    }
    
    // For all other operators, evaluate right normally
    Object right = eval(e.right);
    
    switch (e.operator.type) {
        case AMPERSAND:
            return stringify(left) + stringify(right);
        // ... rest of operators
```

---

## Priority 2: Code Cleanup

### 1. Remove Completed TODO Comments
- [x] `Environment.java:44` - "TODO: Fixed - NUMERO should reject decimal values"
- [x] `Environment.java:63` - "TODO: Fixed - LETRA must be exactly 1 character"
- [x] `Lexer.java:365` - "TODO: Fixed - Only allow specific escape sequences"
- [x] `Lexer.java:438` - "TODO: Fixed - Better detection of unclosed character literals"

### 2. Remove Unused Code
- [x] `Parser.java:520` - `peekAhead(int n)` method (never used)

---

## Priority 3: Add Critical Edge Case Tests

### Tests to Add (in order):
1. ✅ `testShortCircuitAndAvoidsDivisionByZero()`
2. ✅ `testShortCircuitOrAvoidsDivisionByZero()`
3. ✅ `testIncrementUninitializedVariable()`
4. ✅ `testDecrementUninitializedVariable()`
5. ✅ `testAdditionWithNegativeNumbers()`
6. ✅ `testMixedNumeroTipikAddition()`
7. ✅ `testCompareNumeroWithTipikEqual()`
8. ✅ `testDawatEmptyInput()`

---

## Priority 4: Optional Refactoring

### Refactor Arithmetic Helper Methods
**Impact:** Medium - improves maintainability
**Effort:** Medium
**Current:** 5 similar methods with duplicated structure
**Proposed:** Functional interface approach

---

## Priority 5: Documentation Updates

### Update Specification Documents
- [ ] Document operator precedence for `&` (concatenation)
- [ ] Document operator precedence for `UG`, `O`, `DILI`
- [ ] Clarify short-circuit evaluation behavior
- [ ] Document mixed NUMERO/TIPIK arithmetic behavior

---

## Execution Timeline

### Immediate (< 1 hour):
1. ✅ Fix short-circuit evaluation
2. ✅ Add 2-3 tests for short-circuit
3. ✅ Test the fix

### Short-term (< 2 hours):
4. ✅ Remove TODO comments
5. ✅ Remove unused code
6. ✅ Add 5-8 critical edge case tests
7. ✅ Run full test suite

### Medium-term (< 4 hours):
8. ✅ Add remaining high-priority tests
9. ✅ Improve error messages
10. ✅ Document specification updates

### Optional (if time permits):
11. ✅ Refactor arithmetic methods
12. ✅ Add stress tests
13. ✅ Add integration tests

---

## Testing Checklist

After implementing fixes:
- [ ] Run `./gradlew test --tests Increment2Tests`
- [ ] Run `./gradlew test` (all tests)
- [ ] Verify all new tests pass
- [ ] Check for any regressions
- [ ] Update test coverage metrics

---

## Risk Assessment

| Issue | Current Risk | After Fix | Priority |
|-------|--------------|-----------|----------|
| Short-circuit | 🔴 HIGH | 🟢 LOW | P1 |
| Uninitialized vars | 🟡 MEDIUM | 🟢 LOW | P3 |
| Edge cases | 🟡 MEDIUM | 🟢 LOW | P3 |
| Code cleanup | 🟢 LOW | 🟢 LOW | P2 |

---

**Created:** October 15, 2025  
**Status:** Ready for Implementation
