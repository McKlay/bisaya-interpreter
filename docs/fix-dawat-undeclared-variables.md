# Fix: Undeclared Variable Detection in DAWAT Statement

## Problem Description

Previously, when a DAWAT (input) statement referenced an undeclared variable, the interpreter would:
1. First prompt the user for input
2. Only then throw a runtime error when trying to assign to the undeclared variable

This was problematic because users would input values before discovering their code had an error.

### Example of the Problem:
```bisaya
SUGOD
    MUGNA TIPIK result = 5.8
    DAWAT: x              -- 'x' is NOT declared!
    IPAKITA: result
KATAPUSAN
```

**Old Behavior:**
- User prompted to enter value
- User enters value
- Error thrown: "Undefined variable 'x'"

**Expected Behavior:**
- Error thrown immediately during parsing (before execution)
- User never prompted for input

## Solution

The fix adds **parse-time validation** for variable declarations in DAWAT statements.

### Implementation Changes

#### 1. Added Variable Tracking to Parser (`Parser.java`)

Added a new field to track declared variables during parsing:

```java
/** Tracks variables declared during parsing to catch undeclared variable usage early */
private final Set<String> declaredVariables = new HashSet<>();
```

#### 2. Updated `varDecl()` Method

Modified the variable declaration parser to register each declared variable:

```java
// Add to global declared variables tracking
declaredVariables.add(name);
```

This ensures that every time a variable is declared with MUGNA, it's added to our tracking set.

#### 3. Enhanced `inputStmt()` Method

Updated the DAWAT statement parser to validate variables before creating the AST node:

```java
Token firstVar = consume(TokenType.IDENTIFIER, "Expect variable name.");

// Validate that the variable is declared
if (!declaredVariables.contains(firstVar.lexeme)) {
    throw error(firstVar, "Undefined variable '" + firstVar.lexeme + 
        "'. Variables must be declared with MUGNA before using in DAWAT.");
}
```

This check happens for every variable in the DAWAT statement (including comma-separated lists).

## Benefits

1. **Early Error Detection**: Errors are caught during parsing, not at runtime
2. **Better User Experience**: Users don't waste time entering input for broken programs
3. **Clearer Error Messages**: Errors point to the exact variable that's undeclared
4. **Consistent with Language Design**: Bisaya++ is described as "strongly-typed", so requiring declaration before use aligns with this

## Test Coverage

Added comprehensive test suite (`UndeclaredVariableInDawatTest.java`) covering:

✅ Single undeclared variable in DAWAT
✅ Properly declared variable in DAWAT (should work)
✅ Multiple variables all declared (should work)
✅ Multiple variables with one undeclared (should fail)
✅ DAWAT before declaration (should fail - order matters)

All tests pass, and existing tests remain unaffected.

## Example Scenarios

### ❌ Scenario 1: Undeclared Variable (Now Caught at Parse Time)
```bisaya
SUGOD
    MUGNA TIPIK result = 5.8
    DAWAT: x
    IPAKITA: result
KATAPUSAN
```
**Error**: `Undefined variable 'x'. Variables must be declared with MUGNA before using in DAWAT.`

### ✅ Scenario 2: Properly Declared Variable (Works Correctly)
```bisaya
SUGOD
    MUGNA TIPIK x
    DAWAT: x
    IPAKITA: x
KATAPUSAN
```
**Result**: Program parses successfully, user prompted for input at execution time.

### ❌ Scenario 3: Partial Declaration (Now Caught at Parse Time)
```bisaya
SUGOD
    MUGNA NUMERO x, y
    DAWAT: x, y, z
    IPAKITA: x
KATAPUSAN
```
**Error**: `Undefined variable 'z'. Variables must be declared with MUGNA before using in DAWAT.`

### ❌ Scenario 4: Forward Reference (Now Caught at Parse Time)
```bisaya
SUGOD
    DAWAT: x
    MUGNA NUMERO x
    IPAKITA: x
KATAPUSAN
```
**Error**: `Undefined variable 'x'. Variables must be declared with MUGNA before using in DAWAT.`

## Technical Notes

- The fix is implemented entirely in the Parser, maintaining separation of concerns
- No changes required to the Interpreter, Lexer, or AST classes
- The validation uses a HashSet for O(1) lookup performance
- The approach is extensible for future features (e.g., validating variables in other contexts)

## Date Implemented
October 15, 2025
