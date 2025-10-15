# Changelog - Increment 3

## Version: Increment 3
**Date:** October 15, 2025  
**Focus:** Conditional Control Structures

---

## New Features

### Conditional Statements
- ✨ **KUNG (if) statements** - Execute code conditionally based on boolean expressions
- ✨ **KUNG WALA (else) statements** - Provide alternative execution path
- ✨ **KUNG DILI (else-if) statements** - Chain multiple conditions with priority ordering
- ✨ **Nested conditionals** - Support unlimited nesting depth for complex logic
- ✨ **PUNDOK blocks** - Structured code blocks with `{ }` delimiters

### Enhanced Boolean Support
- ✅ Full boolean expression evaluation in conditions
- ✅ Integration with comparison operators (`>`, `<`, `>=`, `<=`, `==`, `<>`)
- ✅ Integration with logical operators (`UG`, `O`, `DILI`)
- ✅ Short-circuit evaluation (already implemented in Increment 2)
- ✅ TINUOD variable support in conditions

---

## Modified Components

### Core Language Files

#### `Stmt.java`
```java
+ public static final class If extends Stmt
  - Expr condition
  - Stmt thenBranch
  - Stmt elseBranch (nullable)

+ public static final class Block extends Stmt
  - List<Stmt> statements

+ R visitIf(If s) in Visitor interface
+ R visitBlock(Block s) in Visitor interface
```

#### `Parser.java`
```java
+ private Stmt ifStmt()
  - Parses KUNG statements
  - Handles KUNG WALA and KUNG DILI
  - Ensures PUNDOK blocks

+ private Stmt parseElseIfChain()
  - Recursively handles else-if chains
  - Builds nested If structure

+ private Stmt block()
  - Parses PUNDOK { } blocks
  - Returns Block statement

~ private Stmt statement()
  - Added KUNG and PUNDOK handling
```

#### `Interpreter.java`
```java
+ public Void visitIf(Stmt.If s)
  - Evaluates condition
  - Executes appropriate branch
  - Uses isTruthy() for conversion

+ public Void visitBlock(Stmt.Block s)
  - Executes statements sequentially
  - No new scope creation
```

### No Changes Required
- ✅ `TokenType.java` - KUNG, WALA, DILI, PUNDOK already defined
- ✅ `Lexer.java` - All keywords already recognized
- ✅ `Expr.java` - No expression changes needed
- ✅ `Environment.java` - No scope changes needed

---

## New Test Suite

### `Increment3Tests.java`
**32 comprehensive test cases:**

#### Basic If Tests (7)
- Simple if with true condition
- Simple if with false condition
- Arithmetic comparison
- Logical AND in condition
- Logical OR in condition
- NOT operator in condition
- Multiple statements in if block

#### If-Else Tests (3)
- Then branch execution
- Else branch execution
- Variable assignment in branches

#### Else-If Tests (4)
- First condition match
- Middle condition match
- Else branch in else-if chain
- Multiple else-if clauses
- Else-if without final else

#### Nested Conditionals Tests (4)
- Nested if statements
- Nested if-else
- Deeply nested (3+ levels)
- Nested else-if chains

#### Boolean Expression Tests (4)
- TINUOD variable in condition
- Complex UG/O expressions
- Short-circuit AND
- Short-circuit OR

#### Integration Tests (5)
- Spec example validation
- Grade calculator
- Max of two numbers
- Leap year checker
- Number classification

#### Edge Cases (5)
- Empty PUNDOK blocks
- Variable scope persistence
- Equality operator
- Not-equal operator
- Multiple statements after if

**Result:** ✅ All 32 tests passing

---

## New Sample Programs

### `increment3_simple_if.bpp`
Basic if statement demonstrating condition evaluation
```bisaya
KUNG (x > 5)
PUNDOK{
    IPAKITA: "x is greater than 5"
}
```

### `increment3_if_else.bpp`
Even/odd checker using if-else
```bisaya
KUNG (num % 2 == 0)
PUNDOK{ IPAKITA: num & " is even" }
KUNG WALA
PUNDOK{ IPAKITA: num & " is odd" }
```

### `increment3_else_if.bpp`
Grade calculator with multiple conditions
```bisaya
KUNG (score >= 90)
PUNDOK{ IPAKITA: "Grade: A" }
KUNG DILI (score >= 80)
PUNDOK{ IPAKITA: "Grade: B" }
KUNG DILI (score >= 70)
PUNDOK{ IPAKITA: "Grade: C" }
KUNG WALA
PUNDOK{ IPAKITA: "Grade: F" }
```

### `increment3_nested.bpp`
Leap year checker with nested conditionals
```bisaya
KUNG (year % 4 == 0)
PUNDOK{
    KUNG (year % 100 == 0)
    PUNDOK{
        KUNG (year % 400 == 0)
        PUNDOK{ isLeap = "OO" }
    }
    KUNG WALA
    PUNDOK{ isLeap = "OO" }
}
```

### `increment3_complex.bpp`
Complex conditional logic with arithmetic sequences
```bisaya
KUNG (a < b UG b < c)
PUNDOK{
    KUNG (diff1 == diff2)
    PUNDOK{
        IPAKITA: "Arithmetic sequence"
    }
}
```

---

## Documentation Added

### Main Documents
- `INCREMENT3-SUMMARY.md` - Complete implementation documentation
- `INCREMENT3-QUICK-REFERENCE.md` - Syntax quick reference guide
- `CHANGELOG-INCREMENT3.md` - This changelog

### Documentation Sections
1. Overview and features
2. Syntax and grammar
3. Implementation details
4. Test coverage
5. Sample programs
6. Design decisions
7. Integration notes
8. Performance considerations

---

## Backward Compatibility

### ✅ Full Compatibility with Previous Increments

#### Increment 1 (Basic Features)
- ✅ All variable declarations work in conditionals
- ✅ All data types supported in conditions
- ✅ IPAKITA works in all blocks
- ✅ String concatenation in conditions

#### Increment 2 (Operators & Input)
- ✅ All arithmetic operators in conditions
- ✅ All comparison operators work correctly
- ✅ Logical operators (UG, O, DILI) integrated
- ✅ Short-circuit evaluation preserved
- ✅ DAWAT can be used in conditional blocks
- ✅ Unary operators (++, --) work in blocks

### No Breaking Changes
- All existing programs continue to work
- No syntax changes to existing features
- All previous tests still pass (100%)

---

## Known Limitations

### Intentional Design Choices
1. **No standalone blocks** - PUNDOK only used with KUNG
2. **No variable scoping** - Variables in blocks are global
3. **Mandatory PUNDOK** - All conditional bodies require blocks
4. **Mandatory parentheses** - Conditions must be in ( )

### Not Implemented (Future Increments)
- Switch/case statements
- Ternary conditional operator
- Pattern matching
- Block-level variable scoping

---

## Performance Notes

### Optimizations
- Single-pass parsing
- No backtracking required
- Short-circuit evaluation prevents unnecessary computation
- Only one branch executes (efficient)

### Memory Usage
- Minimal overhead per If node
- Block nodes use standard ArrayList
- No additional scope structures

---

## Testing Summary

### Test Execution
```
✅ Increment 1 Tests: PASSING
✅ Increment 2 Tests: PASSING
✅ Increment 3 Tests: PASSING (32/32)
✅ All Other Tests: PASSING

Total: 100% pass rate
```

### Coverage Areas
- ✅ Syntax validation
- ✅ Semantic correctness
- ✅ Boolean evaluation
- ✅ Control flow
- ✅ Nesting behavior
- ✅ Edge cases
- ✅ Integration with all features

---

## Usage Examples

### Basic Pattern
```bisaya
SUGOD
    MUGNA NUMERO x=10
    KUNG (x > 5)
    PUNDOK{
        IPAKITA: "x is large"
    }
KATAPUSAN
```

### Complete Program
```bisaya
SUGOD
    MUGNA NUMERO age=20
    
    KUNG (age >= 18)
    PUNDOK{
        IPAKITA: "Adult"
        KUNG (age >= 65)
        PUNDOK{
            IPAKITA: "Senior citizen"
        }
    }
    KUNG WALA
    PUNDOK{
        IPAKITA: "Minor"
    }
KATAPUSAN
```

---

## Migration Guide

### For Users
No migration needed - this is a new feature addition. Existing programs work unchanged.

### For Developers
1. Import new `Stmt.If` and `Stmt.Block` classes
2. Implement `visitIf` and `visitBlock` in any Stmt.Visitor
3. Use existing boolean evaluation infrastructure

---

## Next Steps (Future Increments)

### Potential Future Features
- **Increment 4**: Loop structures (SULOD, BUHATA)
- **Increment 5**: Functions/procedures
- **Increment 6**: Arrays and collections
- **Increment 7**: Advanced control flow (break, continue)

---

## Contributors
- Implementation Date: October 15, 2025
- Tested with: Gradle 8.5, JDK 21
- All tests passing ✓

---

## Summary

Increment 3 successfully adds **full conditional control flow** to Bisaya++:
- ✅ If/Else-If/Else statements
- ✅ Nested conditionals
- ✅ Full boolean expression support
- ✅ 32 comprehensive tests (100% passing)
- ✅ 5 working sample programs
- ✅ Complete backward compatibility
- ✅ Production-ready implementation

**Status: COMPLETE ✓**
