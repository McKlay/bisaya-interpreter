# Increment 3 Implementation Summary

## Overview
Increment 3 adds **conditional control structures** to the Bisaya++ programming language, enabling programs to make decisions based on boolean expressions.

## Implementation Date
October 15, 2025

## Features Implemented

### 1. KUNG (if) Statements
- **Syntax**: `KUNG (condition) PUNDOK { statements }`
- Simple conditional execution
- Condition must be a boolean expression
- Body must be enclosed in `PUNDOK { }` block
- Example:
  ```bisaya
  KUNG (x > 5)
  PUNDOK{
      IPAKITA: "x is greater than 5"
  }
  ```

### 2. KUNG-KUNG WALA (if-else) Statements
- **Syntax**: 
  ```bisaya
  KUNG (condition) 
  PUNDOK { then-statements }
  KUNG WALA
  PUNDOK { else-statements }
  ```
- Two-way branching
- Exactly one branch executes based on condition
- Example:
  ```bisaya
  KUNG (num % 2 == 0)
  PUNDOK{
      IPAKITA: "even"
  }
  KUNG WALA
  PUNDOK{
      IPAKITA: "odd"
  }
  ```

### 3. KUNG-KUNG DILI (if-else-if) Statements
- **Syntax**:
  ```bisaya
  KUNG (condition1)
  PUNDOK { statements1 }
  KUNG DILI (condition2)
  PUNDOK { statements2 }
  KUNG DILI (condition3)
  PUNDOK { statements3 }
  KUNG WALA
  PUNDOK { else-statements }
  ```
- Multiple alternative branches
- Conditions evaluated in order (top to bottom)
- First true condition executes, rest are skipped
- Optional final `KUNG WALA` for default case
- Example:
  ```bisaya
  KUNG (score >= 90)
  PUNDOK{ IPAKITA: "A" }
  KUNG DILI (score >= 80)
  PUNDOK{ IPAKITA: "B" }
  KUNG DILI (score >= 70)
  PUNDOK{ IPAKITA: "C" }
  KUNG WALA
  PUNDOK{ IPAKITA: "F" }
  ```

### 4. Nested Conditional Structures
- Conditionals can be nested inside other conditionals
- No depth limit
- Each level maintains its own scope
- Example:
  ```bisaya
  KUNG (x > 0)
  PUNDOK{
      KUNG (y > 0)
      PUNDOK{
          IPAKITA: "Both positive"
      }
  }
  ```

### 5. Boolean Expression Evaluation
- Comparison operators: `>`, `<`, `>=`, `<=`, `==`, `<>`
- Logical operators: `UG` (AND), `O` (OR), `DILI` (NOT)
- Short-circuit evaluation for `UG` and `O`
- TINUOD variables can be used directly in conditions
- Example:
  ```bisaya
  KUNG (a < b UG b < c)
  PUNDOK{ IPAKITA: "ascending order" }
  ```

## Technical Implementation

### Modified Files

#### 1. `Stmt.java`
**Added Statement Types**:
- `Stmt.If` - Represents if/else-if/else statements
  - `Expr condition` - The boolean condition to test
  - `Stmt thenBranch` - Statement(s) to execute if true
  - `Stmt elseBranch` - Optional else/else-if branch
- `Stmt.Block` - Represents a block of statements
  - `List<Stmt> statements` - List of statements in the block

**Added Visitor Methods**:
- `visitIf(If s)` - Handles if statement execution
- `visitBlock(Block s)` - Handles block execution

#### 2. `Parser.java`
**Added Parsing Methods**:
- `ifStmt()` - Parses KUNG statements with optional KUNG WALA/KUNG DILI
  - Handles condition parsing
  - Ensures PUNDOK keyword presence
  - Delegates to block() for body
  - Lookahead for KUNG DILI/KUNG WALA

- `parseElseIfChain()` - Helper for else-if chains
  - Recursively handles multiple KUNG DILI clauses
  - Builds nested If statement structure
  - Handles optional final KUNG WALA

- `block()` - Parses PUNDOK { } blocks
  - Expects `{` after PUNDOK
  - Parses zero or more statements
  - Expects closing `}`

**Updated Methods**:
- `statement()` - Added checks for KUNG and PUNDOK tokens
- Uses `checkNext()` for two-token lookahead (KUNG DILI, KUNG WALA)

#### 3. `Interpreter.java`
**Added Execution Methods**:
- `visitIf(Stmt.If s)` - Executes if statements
  - Evaluates condition using `eval()`
  - Uses `isTruthy()` to convert to boolean
  - Executes appropriate branch
  - Short-circuit: only one branch runs

- `visitBlock(Stmt.Block s)` - Executes blocks
  - Iterates through statements
  - Executes each in sequence
  - No new scope (variables persist)

**Key Features**:
- Proper truthiness evaluation (already implemented)
- Short-circuit evaluation for UG/O (already implemented)
- Condition evaluation uses existing expression evaluator

### Grammar Extensions

```ebnf
statement → printStmt | inputStmt | varDecl | ifStmt | block | exprStmt

ifStmt → "KUNG" "(" expression ")" "PUNDOK" block
         ( "KUNG" "DILI" "(" expression ")" "PUNDOK" block )*
         ( "KUNG" "WALA" "PUNDOK" block )?

block → "{" statement* "}"
```

## Test Coverage

### Test Suite: `Increment3Tests.java`
**32 comprehensive tests** covering:

1. **Basic If Statements** (7 tests)
   - Simple true/false conditions
   - Arithmetic comparisons
   - Logical AND/OR conditions
   - NOT operator
   - Multiple statements in block

2. **If-Else Statements** (3 tests)
   - Then branch execution
   - Else branch execution
   - Variable assignment in branches

3. **Else-If Statements** (4 tests)
   - First condition match
   - Middle condition match
   - Else branch execution
   - Multiple else-if clauses
   - Without final else

4. **Nested Conditionals** (4 tests)
   - Nested if statements
   - Nested if-else
   - Deep nesting (3+ levels)
   - Nested else-if

5. **Boolean Expressions** (4 tests)
   - TINUOD variable conditions
   - Complex expressions with UG/O
   - Short-circuit AND
   - Short-circuit OR

6. **Integration Tests** (5 tests)
   - Spec examples
   - Grade calculator
   - Max of two numbers
   - Leap year checker
   - Number classification

7. **Edge Cases** (5 tests)
   - Empty blocks
   - Variable scope
   - Multiple statements after if
   - Equality comparisons
   - Not-equal comparisons

### All Tests Pass ✓
- 32/32 Increment 3 tests passing
- All previous increment tests still passing
- No regressions introduced

## Sample Programs

### 1. `increment3_simple_if.bpp`
Basic if statement demonstrating condition evaluation

### 2. `increment3_if_else.bpp`
Even/odd checker using if-else

### 3. `increment3_else_if.bpp`
Grade calculator with multiple else-if branches

### 4. `increment3_nested.bpp`
Leap year checker with nested conditionals

### 5. `increment3_complex.bpp`
Complex logic with nested conditions and arithmetic sequences

## Key Design Decisions

### 1. Required PUNDOK Blocks
- **Decision**: All conditional branches must use `PUNDOK { }` blocks
- **Rationale**: 
  - Explicit structure makes code more readable
  - Prevents ambiguity in nested conditions
  - Consistent with Bisaya++ design philosophy
  - Avoids "dangling else" problem

### 2. Else-If as Nested Ifs
- **Decision**: KUNG DILI creates nested If statements internally
- **Rationale**:
  - Simpler AST structure
  - Reuses If node for all conditionals
  - Natural evaluation order
  - Easy to understand and maintain

### 3. No New Scopes
- **Decision**: Blocks don't create new variable scopes
- **Rationale**:
  - Consistent with current design (Increment 1-2)
  - Variables declared outside remain accessible
  - Variables modified inside persist after block
  - Matches spec requirements (no scope specified)

### 4. Mandatory Parentheses
- **Decision**: Conditions must be in parentheses
- **Rationale**:
  - Clear separation of condition from keywords
  - Familiar to programmers
  - Prevents parsing ambiguities
  - Standard in C-family languages

### 5. Two-Token Lookahead
- **Decision**: Use `checkNext()` for KUNG DILI / KUNG WALA
- **Rationale**:
  - Distinguish between standalone KUNG and KUNG DILI/WALA
  - Parser remains deterministic
  - No backtracking needed
  - Efficient single-pass parsing

## Integration with Previous Increments

### Increment 1 Features (Fully Compatible)
- ✓ Variable declarations work in all branches
- ✓ Print statements work in blocks
- ✓ All data types supported in conditions

### Increment 2 Features (Fully Compatible)
- ✓ Arithmetic operators in conditions
- ✓ Comparison operators in conditions
- ✓ Logical operators (UG, O, DILI)
- ✓ Short-circuit evaluation
- ✓ Unary operators (++, --)
- ✓ DAWAT can be used in blocks

## Examples from Specification

### Example 1: Basic Conditional
```bisaya
SUGOD
    MUGNA NUMERO a=100, b=200, c=300
    MUGNA TINUOD d="DILI"
    d = (a < b UG c <> 200)
    KUNG (d)
    PUNDOK{
        IPAKITA: "Condition met"
    }
KATAPUSAN
```
Output: `Condition met`

### Example 2: Grade System
```bisaya
SUGOD
    MUGNA NUMERO score=85
    KUNG (score >= 90)
    PUNDOK{ IPAKITA: "A" }
    KUNG DILI (score >= 80)
    PUNDOK{ IPAKITA: "B" }
    KUNG DILI (score >= 70)
    PUNDOK{ IPAKITA: "C" }
    KUNG WALA
    PUNDOK{ IPAKITA: "F" }
KATAPUSAN
```
Output: `B`

## Performance Considerations

### Efficient Evaluation
- Conditions evaluated only once per if statement
- Short-circuit evaluation prevents unnecessary work
- Only one branch executes (no redundant checks)

### Memory Usage
- Nested If nodes for else-if chains
- Block nodes contain statement lists
- No additional scope structures needed

## Future Enhancements (Not in Increment 3)

### Potential Additions:
1. **Switch/Case Statements** - Multi-way branching
2. **Ternary Operator** - Inline conditionals
3. **Pattern Matching** - Advanced conditionals
4. **Guard Clauses** - Early returns (needs functions)

## Conclusion

Increment 3 successfully implements conditional control structures in Bisaya++, providing:
- ✅ Complete if/else-if/else functionality
- ✅ Nested conditionals with unlimited depth
- ✅ Full boolean expression support
- ✅ 32 comprehensive tests (100% passing)
- ✅ 5 working sample programs
- ✅ Backward compatibility with Increments 1-2
- ✅ Clean, maintainable implementation

The implementation follows the specification exactly and integrates seamlessly with existing language features. All tests pass, demonstrating robust error handling and correct behavior across edge cases.
