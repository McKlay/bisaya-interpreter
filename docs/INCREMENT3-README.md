# Increment 3: Conditional Control Structures

## 🎯 Overview

Increment 3 adds **conditional control structures** to Bisaya++, enabling programs to make decisions and execute code based on boolean conditions. This increment implements if-else-if chains, nested conditionals, and proper boolean expression evaluation.

---

## ✨ New Features

### 1. KUNG (If) Statements
Execute code only when a condition is true.

```bisaya
KUNG (x > 5)
PUNDOK{
    IPAKITA: "x is greater than 5"
}
```

### 2. KUNG WALA (Else) Statements
Provide an alternative path when the condition is false.

```bisaya
KUNG (age >= 18)
PUNDOK{
    IPAKITA: "Adult"
}
KUNG WALA
PUNDOK{
    IPAKITA: "Minor"
}
```

### 3. KUNG DILI (Else-If) Statements
Chain multiple conditions with priority ordering.

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

### 4. Nested Conditionals
Unlimited nesting depth for complex decision logic.

```bisaya
KUNG (x > 0)
PUNDOK{
    KUNG (y > 0)
    PUNDOK{
        IPAKITA: "Both positive"
    }
    KUNG WALA
    PUNDOK{
        IPAKITA: "x positive, y non-positive"
    }
}
```

### 5. PUNDOK Blocks
Structured code blocks using `{ }` delimiters.

```bisaya
PUNDOK{
    statement1
    statement2
    statement3
}
```

---

## 📋 Syntax

### Grammar
```ebnf
ifStmt → "KUNG" "(" expression ")" "PUNDOK" block
         ( "KUNG" "DILI" "(" expression ")" "PUNDOK" block )*
         ( "KUNG" "WALA" "PUNDOK" block )?

block → "{" statement* "}"
```

### Keywords
- `KUNG` - if
- `KUNG WALA` - else
- `KUNG DILI` - else if
- `PUNDOK` - block

### Rules
1. ✅ Conditions must be in parentheses: `(condition)`
2. ✅ All bodies must use PUNDOK blocks: `PUNDOK { }`
3. ✅ Braces are required: `{ statements }`
4. ✅ Order: KUNG → KUNG DILI* → KUNG WALA?

---

## 🔧 Implementation Details

### Modified Files

| File | Changes | Description |
|------|---------|-------------|
| `Stmt.java` | Added `If` and `Block` classes | AST nodes for conditionals |
| `Parser.java` | Added `ifStmt()`, `parseElseIfChain()`, `block()` | Parsing logic |
| `Interpreter.java` | Added `visitIf()`, `visitBlock()` | Execution logic |

### No Changes Required
- ✅ `TokenType.java` - Keywords already defined
- ✅ `Lexer.java` - Already tokenizes keywords
- ✅ `Expr.java` - No expression changes needed
- ✅ `Environment.java` - No scope changes needed

---

## 🧪 Testing

### Test Suite: `Increment3Tests.java`
**32 comprehensive tests** covering:

| Category | Tests | Description |
|----------|-------|-------------|
| Basic If | 7 | Simple conditions, operators, multiple statements |
| If-Else | 3 | Then/else branch execution, assignments |
| Else-If | 4 | Chain evaluation, multiple conditions |
| Nested | 4 | Nested ifs, deep nesting, nested else-if |
| Boolean | 4 | TINUOD vars, complex expressions, short-circuit |
| Integration | 5 | Real-world examples, spec validation |
| Edge Cases | 5 | Empty blocks, scope, equality operators |

**Result:** ✅ **32/32 tests passing (100%)**

### Running Tests
```powershell
# Run all Increment 3 tests
.\gradlew test --tests Increment3Tests

# Run all tests
.\gradlew test

# Clean and test
.\gradlew clean test
```

---

## 📂 Sample Programs

All sample programs are in `app/samples/`:

### 1. `increment3_simple_if.bpp`
Basic if statement
```bisaya
KUNG (x > 5)
PUNDOK{
    IPAKITA: "x is greater than 5"
}
```

### 2. `increment3_if_else.bpp`
Even/odd checker
```bisaya
KUNG (num % 2 == 0)
PUNDOK{ IPAKITA: num & " is even" }
KUNG WALA
PUNDOK{ IPAKITA: num & " is odd" }
```

### 3. `increment3_else_if.bpp`
Grade calculator
```bisaya
KUNG (score >= 90) PUNDOK{ IPAKITA: "A" }
KUNG DILI (score >= 80) PUNDOK{ IPAKITA: "B" }
KUNG DILI (score >= 70) PUNDOK{ IPAKITA: "C" }
KUNG WALA PUNDOK{ IPAKITA: "F" }
```

### 4. `increment3_nested.bpp`
Leap year checker with nested conditions

### 5. `increment3_complex.bpp`
Complex logic with arithmetic sequences

### Running Samples
```powershell
# Example: Run grade calculator
.\gradlew run --args="C:\Users\Clay\Desktop\MCS\bisaya-interpreter\app\samples\increment3_else_if.bpp" --quiet
```

---

## 🎓 Usage Examples

### Example 1: Number Classification
```bisaya
SUGOD
    MUGNA NUMERO num=-5
    
    KUNG (num > 0)
    PUNDOK{ IPAKITA: "positive" }
    KUNG DILI (num < 0)
    PUNDOK{ IPAKITA: "negative" }
    KUNG WALA
    PUNDOK{ IPAKITA: "zero" }
KATAPUSAN
```

### Example 2: Max of Two Numbers
```bisaya
SUGOD
    MUGNA NUMERO a=15, b=25, max
    
    KUNG (a > b)
    PUNDOK{ max = a }
    KUNG WALA
    PUNDOK{ max = b }
    
    IPAKITA: "Max is: " & max
KATAPUSAN
```

### Example 3: Range Check
```bisaya
SUGOD
    MUGNA NUMERO x=50, min=0, max=100
    
    KUNG (x >= min UG x <= max)
    PUNDOK{
        IPAKITA: x & " is in range [" & min & ", " & max & "]"
    }
    KUNG WALA
    PUNDOK{
        IPAKITA: x & " is out of range"
    }
KATAPUSAN
```

---

## 🔍 Boolean Expressions

### Operators
```bisaya
-- Comparison
x > 5      -- greater than
x < 10     -- less than
x >= 5     -- greater or equal
x <= 10    -- less or equal
x == 5     -- equal
x <> 5     -- not equal

-- Logical
a UG b     -- AND (both must be true)
a O b      -- OR (at least one true)
DILI a     -- NOT (negates)

-- Complex
(x > 0 UG y > 0) O (x == 0 UG y == 0)
```

### Truthiness
- Numbers: `0` is false, non-zero is true
- TINUOD: `"OO"` is true, `"DILI"` is false
- Strings: non-empty is true

### Short-Circuit Evaluation
```bisaya
-- UG: right side not evaluated if left is false
"DILI" UG (1/0 > 0)  -- No division by zero error!

-- O: right side not evaluated if left is true
"OO" O (1/0 > 0)     -- No division by zero error!
```

---

## 💡 Design Decisions

### 1. Mandatory PUNDOK Blocks
**Why?** 
- Prevents ambiguity in nested conditions
- Makes code structure explicit
- Avoids "dangling else" problem
- Consistent with Bisaya++ philosophy

### 2. Else-If as Nested Ifs
**Why?**
- Simpler AST structure (reuse If node)
- Natural evaluation order
- Easy to understand and maintain
- No special case handling needed

### 3. No Variable Scoping in Blocks
**Why?**
- Consistent with Increments 1-2
- Spec doesn't require scoping
- Simpler implementation
- Variables modified in blocks persist

### 4. Two-Token Lookahead
**Why?**
- Distinguish KUNG DILI from standalone KUNG
- Deterministic parsing (no backtracking)
- Efficient single-pass
- Standard parser technique

---

## ✅ Backward Compatibility

### Full Compatibility with Previous Increments

#### Increment 1 ✓
- All variable declarations work in conditionals
- All data types supported
- IPAKITA works in blocks
- String concatenation in conditions

#### Increment 2 ✓
- Arithmetic operators in conditions
- Comparison operators work correctly
- Logical operators integrated
- Short-circuit evaluation preserved
- DAWAT in conditional blocks
- Unary operators in blocks

### No Breaking Changes
- ✅ All existing programs work unchanged
- ✅ All previous tests pass (100%)
- ✅ No syntax changes to existing features

---

## 📊 Performance

### Optimizations
- ✅ Single-pass parsing (no backtracking)
- ✅ Short-circuit evaluation (skip unnecessary work)
- ✅ Only one branch executes
- ✅ Minimal memory overhead

### Complexity
- **Parsing:** O(n) where n = number of tokens
- **Execution:** O(1) per conditional (only one branch runs)
- **Memory:** O(d) where d = nesting depth

---

## 📚 Documentation

### Available Documents
1. **INCREMENT3-SUMMARY.md** - Complete implementation details
2. **INCREMENT3-QUICK-REFERENCE.md** - Syntax quick reference
3. **CHANGELOG-INCREMENT3.md** - Detailed changelog
4. **INCREMENT3-README.md** - This file (overview)

### Code Documentation
- All methods have comprehensive JavaDoc comments
- Parser grammar documented in comments
- Test cases have descriptive names

---

## 🚀 Next Steps

### Potential Future Features (Not in Increment 3)
- Loop structures (WHILE, FOR)
- Switch/case statements
- Functions and procedures
- Arrays and collections
- Break/continue statements
- Ternary operator

---

## 📝 Summary

### What's New
✅ KUNG (if) statements  
✅ KUNG WALA (else) statements  
✅ KUNG DILI (else-if) chains  
✅ Nested conditionals  
✅ PUNDOK blocks  
✅ Full boolean expression support  

### Test Results
✅ 32/32 tests passing (100%)  
✅ All previous tests still passing  
✅ 5 working sample programs  

### Status
🎉 **COMPLETE AND PRODUCTION-READY**

---

## 🤝 Contributing

### Testing Your Code
1. Write tests first (TDD approach)
2. Run tests: `.\gradlew test`
3. Verify samples work
4. Check backward compatibility

### Code Quality
- Follow existing code style
- Add JavaDoc comments
- Handle edge cases
- Update documentation

---

## 📞 Support

### Getting Help
1. Check documentation in `docs/`
2. Review sample programs in `app/samples/`
3. Run tests to see examples: `Increment3Tests.java`
4. Read inline code comments

### Reporting Issues
Include:
- Bisaya++ code that fails
- Expected vs actual behavior
- Error messages
- Java version and OS

---

**Increment 3 Implementation Complete! 🎉**

*Date: October 15, 2025*  
*All tests passing ✓*  
*Fully backward compatible ✓*  
*Production ready ✓*
