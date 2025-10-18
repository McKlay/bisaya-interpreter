# Comment Syntax Change: From `--` to `@@`

## Overview

The Bisaya++ comment syntax has been changed from `--` (double minus) to `@@` (double at sign) to resolve conflicts with the decrement operator and to support inline comments.

**Date of Change:** October 18, 2025

---

## Motivation

### Previous Issue
The original comment syntax using `--` caused ambiguity problems:
- `--` was used for both comments AND the prefix decrement operator
- Complex heuristics were needed to distinguish `--comment` from `--variable`
- Comments were restricted to start-of-line only to avoid parsing conflicts
- This limitation prevented inline comments (e.g., `x = 5 -- inline comment`)

### Solution
By changing the comment symbol to `@@`:
- **No ambiguity**: `@@` is dedicated to comments only
- **No conflicts**: `--` is now always treated as the decrement operator
- **Inline comments**: Full support for comments after code on the same line
- **Simpler lexer**: Removed complex disambiguation logic

---

## Specification Changes

### Old Specification (Before Change)
```
- Comments start with double minus sign (--)
- Comments can only appear at the start of a line
- Special heuristics distinguish -- comments from -- decrement operator
```

### New Specification (After Change)
```
- Comments start with double at sign (@@)
- Comments can appear at the start of a line OR inline after code
- The decrement operator (--) is always parsed as an operator
```

---

## Implementation Changes

### 1. Lexer Changes (`Lexer.java`)

#### Before:
```java
case '-' -> {
    if (match('-')) {
        // Complex logic to determine if comment or decrement
        if (isAtStartOfLine()) {
            // Check lookahead, spaces, context...
            if (/* various conditions */) {
                lineComment();
            } else {
                add(TokenType.MINUS_MINUS);
            }
        } else {
            add(TokenType.MINUS_MINUS);
        }
    } else {
        add(TokenType.MINUS);
    }
}
```

#### After:
```java
// Simple decrement handling
case '-' -> add(match('-') ? TokenType.MINUS_MINUS : TokenType.MINUS);

// Dedicated comment handling
case '@' -> {
    if (match('@')) {
        lineComment(); // Consume to end of line
    } else {
        ErrorReporter.error(line, col, "Unexpected character: @");
    }
}
```

#### Removed Helper Methods:
- `isAtStartOfLine()` - No longer needed (marked as @Deprecated)
- `hasSpaceAheadInLine()` - No longer needed (marked as @Deprecated)

#### Updated Documentation:
```java
/**
 * COMMENT PROCESSOR - Consume line comment until newline
 * 
 * Handles '@@' style comments by consuming all characters until end of line.
 * Comments can appear both at the start of a line or inline after code.
 */
private void lineComment() { 
    while (!isAtEnd() && peek() != '\n') advance(); 
}
```

---

## Usage Examples

### Start-of-Line Comments
```bisaya
@@ This is a start-of-line comment
SUGOD
@@ Initialize variables
MUGNA NUMERO x = 5
@@ Display result
IPAKITA: x
KATAPUSAN
```

### Inline Comments
```bisaya
SUGOD
MUGNA NUMERO x = 5 @@ declare and initialize x
MUGNA NUMERO y = 10 @@ declare and initialize y
x = x + y @@ add y to x
IPAKITA: x @@ display result (should be 15)
KATAPUSAN
```

### Mixed Comments
```bisaya
@@ Program to demonstrate inline and start-of-line comments
SUGOD
    MUGNA NUMERO counter = 0 @@ initialize counter
    
    @@ Increment counter using prefix operator
    ++counter
    IPAKITA: counter @@ should print 1
    
    @@ Increment using postfix operator
    counter++
    IPAKITA: counter @@ should print 2
    
    @@ Decrement operator (--) no longer conflicts with comments
    --counter
    IPAKITA: counter @@ should print 1
KATAPUSAN
```

---

## Test File Updates

### Files Updated:
1. **Test Files:**
   - `PostfixAndCommentsTest.java` - Updated all comment syntax in test cases
   - `LexerTest.java` - Updated comment test case
   - `InterpreterPrintTest.java` - Updated spec sample test
   - `Increment5NegativeTests.java` - Updated comment in loop test

2. **Sample Programs (.bpp files):**
   - `hello.bpp`
   - `comments_demo.bpp` (enhanced to show inline comments)
   - `increment3_simple_if.bpp`
   - `increment3_if_else.bpp`
   - `increment3_nested.bpp`
   - `increment4_basic_loop.bpp`
   - `increment4_loop_conditional.bpp`
   - `increment4_nested_loops.bpp`
   - All other .bpp files in `app/samples/`

3. **Documentation:**
   - `.github/copilot-instructions.md` - Updated language specification
   - `docs/lexer-specification.md` - Updated comment tokenization flow
   - `docs/postfix-and-comments-summary.md` - Updated implementation summary

---

## Migration Guide

If you have existing Bisaya++ programs using `--` for comments:

### Search and Replace
Use this pattern to update your code:
- **Search:** `^--` (regex: comments at start of line)
- **Replace:** `@@`

### PowerShell Command
```powershell
$files = Get-ChildItem -Path "your/path" -Filter "*.bpp"
foreach ($file in $files) {
    (Get-Content $file.FullName) | ForEach-Object { 
        $_ -replace "^--", "@@" 
    } | Set-Content $file.FullName
}
```

### Manual Update
For inline comments that need to be added:
```bisaya
MUGNA NUMERO x = 5
IPAKITA: x
```

Can now become:
```bisaya
MUGNA NUMERO x = 5 @@ declare variable
IPAKITA: x @@ display value
```

---

## Benefits Summary

✅ **Clearer Syntax**: No ambiguity between comments and operators  
✅ **Inline Comments**: Can now document code on the same line  
✅ **Simpler Lexer**: Removed complex disambiguation logic  
✅ **Better Maintainability**: Easier to understand and extend  
✅ **Consistent Behavior**: `--` always means decrement  
✅ **More Flexible**: Comments work anywhere on a line  

---

## Backward Compatibility

⚠️ **Breaking Change**: Programs using `--` for comments will need to be updated to use `@@`.

The decrement operator `--` continues to work as before (both prefix and postfix).

---

## Testing

All tests have been updated and verified:
- ✅ Lexer correctly tokenizes `@@` comments
- ✅ Inline comments work correctly
- ✅ Start-of-line comments work correctly
- ✅ Decrement operator `--` works without ambiguity
- ✅ All existing functionality preserved
- ✅ All sample programs updated and tested

Run tests with:
```bash
./gradlew test
```

---

## Related Files

**Core Implementation:**
- `app/src/main/java/com/bisayapp/Lexer.java`

**Tests:**
- `app/src/test/java/com/bisayapp/PostfixAndCommentsTest.java`
- `app/src/test/java/com/bisayapp/LexerTest.java`
- `app/src/test/java/com/bisayapp/InterpreterPrintTest.java`
- `app/src/test/java/com/bisayapp/Increment5NegativeTests.java`

**Documentation:**
- `.github/copilot-instructions.md`
- `docs/lexer-specification.md`
- `docs/postfix-and-comments-summary.md`
- `docs/COMMENT-SYNTAX-CHANGE.md` (this file)

**Sample Programs:**
- All `*.bpp` files in `app/samples/`

---

## Future Enhancements

Possible future improvements:
- Multi-line comments (e.g., `@@{ ... }@@`)
- Documentation comments with special syntax
- Comment preservation in AST for documentation generation
