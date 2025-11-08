# Pretty Print Feature - Quick Reference

## Before/After Examples

### Example 1: Basic Indentation

**Before:**
```bisaya
SUGOD
MUGNA NUMERO x,y,z
x=5
IPAKITA:x
KATAPUSAN
```

**After:**
```bisaya
SUGOD
    MUGNA NUMERO x, y, z
    x = 5
    IPAKITA: x
KATAPUSAN
```

---

### Example 2: Nested Blocks

**Before:**
```bisaya
SUGOD
KUNG (x>0)
PUNDOK{
ALANG SA (i=1,i<=5,i++)
PUNDOK{
IPAKITA:i
}
}
KATAPUSAN
```

**After:**
```bisaya
SUGOD
    KUNG (x > 0)
    PUNDOK{
        ALANG SA (i=1, i<=5, i++)
        PUNDOK{
            IPAKITA: i
        }
    }
KATAPUSAN
```

---

### Example 3: Comments Preserved

**Before:**
```bisaya
SUGOD
@@ This is a comment
MUGNA NUMERO x  @@ Inline comment
x=5@@No space
KATAPUSAN
```

**After:**
```bisaya
SUGOD
    @@ This is a comment
    MUGNA NUMERO x  @@ Inline comment
    x = 5  @@ No space
KATAPUSAN
```

---

### Example 4: Operator Spacing

**Before:**
```bisaya
x=y+z*2
result="Value: "&x&" units"
flag=(x>5UG y<10)
```

**After:**
```bisaya
x = y + z * 2
result = "Value: " & x & " units"
flag = (x > 5 UG y < 10)
```

---

### Example 5: Tab Conversion

**Before (tabs shown as →):**
```bisaya
SUGOD
→→MUGNA NUMERO x
→→→→IPAKITA: x
KATAPUSAN
```

**After (4 spaces per indent):**
```bisaya
SUGOD
    MUGNA NUMERO x
        IPAKITA: x
KATAPUSAN
```

**Note**: Each tab (→) converts to 4 spaces, NOT 8.

---

## Formatting Rules Summary

| Element | Rule | Example |
|---------|------|---------|
| **Program structure** | No indent | `SUGOD` / `KATAPUSAN` |
| **Statements** | 4 spaces per level | `    MUGNA NUMERO x` |
| **Nested blocks** | +4 spaces each level | 4 → 8 → 12 → 16... |
| **Binary operators** | Space before/after | `x = 5`, `a + b` |
| **Commas** | Space after only | `x, y, z` |
| **Parentheses** | No inner space | `(x > 5)` |
| **Comments (@@)** | Align with indent | `    @@ Comment` |
| **Inline comments** | Preserve position | `x = 5  @@ End` |
| **Strings** | Never modify | `"  spaces  "` kept |
| **Tabs** | Convert to 4 spaces | Tab → `    ` |
| **Blank lines** | Preserve, no indent | Empty line kept |

---

## Keyboard Shortcut

**Windows/Linux**: `Ctrl + Shift + F`  
**Mac**: `Cmd + Shift + F`

---

## Algorithm Flow

```
┌─────────────────┐
│  Click Format   │
└────────┬────────┘
         ↓
┌─────────────────┐
│  Get code text  │
└────────┬────────┘
         ↓
┌─────────────────┐
│ Lexer.scanTokens│  ← Tokenize
└────────┬────────┘
         ↓
┌─────────────────┐
│  Track indent   │  ← Level counter
│  level = 0      │
└────────┬────────┘
         ↓
┌─────────────────┐
│ For each line:  │
│  - Check {/}    │  ← Adjust level
│  - Apply indent │
│  - Add spaces   │  ← Around operators
│  - Convert tabs │  ← 1 tab = 4 spaces
└────────┬────────┘
         ↓
┌─────────────────┐
│ Replace editor  │
│  text content   │
└────────┬────────┘
         ↓
┌─────────────────┐
│  Show status:   │
│ "Code formatted"│
└─────────────────┘
```

---

## Token-Based vs Regex-Based

### Why Token-Based?

```
Source Code:  x=y+z*2  @@comment

Regex sees:   x  =  y  +  z  *  2  @@ comment
              ↑ String matching only

Tokens see:   IDENTIFIER  EQUAL  IDENTIFIER  PLUS  IDENTIFIER  STAR  NUMBER  COMMENT
              ↑ Semantic understanding
```

**Token approach**:
- Knows `@@` inside strings is NOT a comment
- Distinguishes operators from string contents
- Handles escape sequences correctly
- More robust for edge cases

**Regex approach**:
- Faster but less accurate
- Can't handle nested structures well
- String/comment conflicts difficult

---

## Performance Characteristics

```
File Size        Tokens    Time      User Experience
─────────────────────────────────────────────────────
< 100 lines      ~500      < 50ms    Instant
100-500 lines    ~2500     < 200ms   Very fast
500-1000 lines   ~5000     < 500ms   Fast
1000+ lines      ~10000+   < 1000ms  Acceptable
```

**Bottlenecks**:
1. Lexer.scanTokens() - O(n) where n = characters
2. Line processing - O(m) where m = lines
3. String building - O(n) with StringBuilder (efficient)

**Total**: O(n + m) ≈ O(n) linear time

---

## Implementation Architecture

```
┌──────────────────────────────────────┐
│          BisayaIDE.java              │
│  (Main application window)           │
└──────────────┬───────────────────────┘
               │
               ↓
┌──────────────────────────────────────┐
│       MenuBarBuilder.java            │
│  Edit → Format Code (Ctrl+Shift+F)  │
└──────────────┬───────────────────────┘
               │ onClick
               ↓
┌──────────────────────────────────────┐
│       IDEController.java             │
│   formatCode() {                     │
│     formatted = PrettyPrinter.format()│
│     editorPanel.setCode(formatted)   │
│   }                                  │
└──────────────┬───────────────────────┘
               │ calls
               ↓
┌──────────────────────────────────────┐
│       PrettyPrinter.java             │
│   static format(String code) {       │
│     tokens = Lexer.scanTokens()      │
│     return formatTokens(tokens)      │
│   }                                  │
└──────────────┬───────────────────────┘
               │ uses
               ↓
┌──────────────────────────────────────┐
│          Lexer.java                  │
│   scanTokens() → List<Token>         │
└──────────────────────────────────────┘
```

---

## Testing Approach

### 1. Unit Tests (Isolated)
```java
@Test
void testBasicIndent() {
    String input = "SUGOD\nMUGNA NUMERO x\nKATAPUSAN";
    String expected = "SUGOD\n    MUGNA NUMERO x\nKATAPUSAN";
    assertEquals(expected, PrettyPrinter.format(input));
}
```

### 2. Integration Tests (Full Programs)
```java
@Test
void testFormatSampleProgram() {
    String original = loadFile("samples/increment3_nested.bpp");
    String formatted = PrettyPrinter.format(original);
    
    // Should compile and run identically
    assertEquals(runProgram(original), runProgram(formatted));
}
```

### 3. Property-Based Tests
```java
@Test
void testSemanticPreservation() {
    // Format should never change program behavior
    for (String sample : allSamples) {
        String formatted = PrettyPrinter.format(sample);
        assertEquals(execute(sample), execute(formatted));
    }
}
```

---

## Edge Case Handling

### 1. Malformed Code
```bisaya
SUGOD
    KUNG (x > 0
    IPAKITA: x     ← Missing PUNDOK{
```

**Behavior**: Format best-effort, don't crash  
**Result**: Indentation may be imperfect but code unchanged

### 2. Mixed Tabs/Spaces
```bisaya
SUGOD
    MUGNA NUMERO x    ← 4 spaces
→MUGNA NUMERO y       ← 1 tab
```

**Behavior**: Convert all to 4 spaces  
**Result**: Consistent spacing

### 3. String with Operators
```bisaya
IPAKITA: "x=y+z"    ← Not real operators!
```

**Behavior**: Recognize as STRING token, don't format inside  
**Result**: String preserved exactly

### 4. Comment with Code-Like Text
```bisaya
@@ This looks like code: x=5
```

**Behavior**: Recognize as COMMENT token, preserve  
**Result**: Comment kept as-is

---

## Common Questions

**Q: Will formatting change my program's behavior?**  
A: No. Formatting only changes whitespace, never logic.

**Q: Can I undo formatting?**  
A: Yes. Use Ctrl+Z immediately after formatting.

**Q: What if my code has syntax errors?**  
A: Formatter works best-effort. It may not be perfect but won't crash.

**Q: Does it work with Cebuano characters (ñ, é)?**  
A: Yes. Unicode characters are fully preserved.

**Q: Why 4 spaces instead of tabs?**  
A: Consistent display across all editors. Tabs can be 2, 4, or 8 spaces depending on settings.

**Q: Can I configure the indent size?**  
A: Not in initial version. Future enhancement planned.

**Q: Will it remove blank lines?**  
A: No. Blank lines are preserved for readability.

---

## Future Enhancement Ideas

1. **Custom indent size** (2, 4, or 8 spaces)
2. **Format on save** (automatic)
3. **Format selection** (partial format)
4. **Style presets** (compact/readable/verbose)
5. **Preview mode** (show diff before applying)
6. **Align inline comments** (at specific column)
7. **Blank line rules** (before/after blocks)
8. **Max line length** (wrap long lines)

---

## Comparison to Other Languages

| Feature | Bisaya++ | Java (Eclipse) | Python (Black) |
|---------|----------|----------------|----------------|
| **Indent** | 4 spaces | 1 tab (4 visual) | 4 spaces |
| **Operators** | Space around | Space around | Space around |
| **Braces** | Same line | New line | N/A |
| **Shortcut** | Ctrl+Shift+F | Ctrl+Shift+F | Save auto |
| **Config** | Future | Yes | No (opinionated) |

---

## Developer Notes

### Adding New Spacing Rules

```java
// In PrettyPrinter.java
private static boolean needsSpaceBefore(Token current, Token prev) {
    // Add new rule here
    if (current.type == TokenType.NEW_OPERATOR) {
        return true;
    }
    return false;
}
```

### Debugging Formatter Issues

1. **Check token stream**: Print tokens to see what Lexer produces
2. **Verify indent tracking**: Log indent level changes
3. **Test isolated**: Format single statement at a time
4. **Compare before/after**: Use diff tool to see exact changes

---

**Document Version**: 1.0  
**Last Updated**: November 8, 2025  
**Status**: Ready for implementation
