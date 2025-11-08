# Phase 3: Pretty Print Feature - Analysis & Implementation Plan

**Date**: November 8, 2025  
**Feature**: Code Formatter / Pretty Printer for Bisaya++  
**Status**: 📋 Design Phase

---

## Overview

A code formatter button that automatically formats Bisaya++ source code with proper indentation, spacing, and alignment following consistent style guidelines.

---

## Design Analysis

### Approach Comparison

#### Option 1: Regex-Based Formatting (⭐ RECOMMENDED)
**Pros:**
- Fast and lightweight
- Similar to syntax highlighter (proven pattern)
- No AST building overhead
- Easy to maintain
- Single-pass formatting

**Cons:**
- Less precise for complex nested structures
- Requires careful pattern design

**Estimated Complexity**: Low-Medium  
**Implementation Time**: 4-6 hours

#### Option 2: Token-Based Formatting
**Pros:**
- More accurate (uses Lexer)
- Handles edge cases better
- Structured approach

**Cons:**
- Slower than regex
- More complex state management
- Two-pass process (tokenize + format)

**Estimated Complexity**: Medium  
**Implementation Time**: 8-12 hours

#### Option 3: AST-Based Formatting
**Pros:**
- Most accurate
- Semantic understanding
- Handles all edge cases

**Cons:**
- Requires parsing (expensive)
- Overkill for simple language
- Slowest option
- Fails on syntax errors

**Estimated Complexity**: High  
**Implementation Time**: 16-24 hours

---

## Recommended Solution: Hybrid Approach

**Strategy**: Token-based with regex assistance

### Why Hybrid?
1. **Accuracy**: Tokens identify structure precisely
2. **Speed**: Faster than AST, more accurate than pure regex
3. **Robustness**: Works even with minor syntax errors
4. **Tab Handling**: Proper conversion (1 tab = 8 spaces issue)

### Implementation Architecture

```
User clicks "Format Code"
         ↓
Get code from CodeArea
         ↓
Lexer.scanTokens() → List<Token>
         ↓
PrettyPrinter.format(tokens, code)
         ↓
Build formatted string with proper indentation
         ↓
Replace CodeArea text
         ↓
Preserve caret position (relative)
```

---

## Formatting Rules

### Indentation Rules

**Base Level**: 0 spaces (SUGOD/KATAPUSAN)  
**Block Indent**: 4 spaces per nesting level  
**Tab Conversion**: 1 tab = 4 spaces (NOT 8)

```bisaya
SUGOD                          ← Level 0
    MUGNA NUMERO x             ← Level 1 (4 spaces)
    KUNG (x > 0)               ← Level 1
    PUNDOK{                    ← Level 1
        IPAKITA: x             ← Level 2 (8 spaces)
        ALANG SA (i=1, i<=5, i++)  ← Level 2
        PUNDOK{                ← Level 2
            IPAKITA: i         ← Level 3 (12 spaces)
        }                      ← Level 2
    }                          ← Level 1
KATAPUSAN                      ← Level 0
```

### Block Structure Rules

1. **PUNDOK{** on same line as statement
2. **}** alone on its own line at parent indent level
3. Blank line before and after blocks (optional, configurable)

### Spacing Rules

1. **Operators**: Space before and after (`x = 5`, not `x=5`)
2. **Commas**: Space after, not before (`x, y, z`, not `x,y,z`)
3. **Parentheses**: No inner space (`(x > 5)`, not `( x > 5 )`)
4. **Comments**: Preserve alignment, ensure space after `@@`

### Special Cases

1. **Chained Assignment**: `x = y = z = 5` (spaces around each `=`)
2. **Concatenation**: `"Hello" & name & "!"` (space around `&`)
3. **String Literals**: Preserve internal spacing exactly
4. **Comments**: 
   - Start-of-line: Align with current indent level
   - Inline: Preserve position (or align at column 40)

---

## Implementation Design

### Class Structure

```java
package com.bisayapp.ui;

public class PrettyPrinter {
    
    private static final int INDENT_SIZE = 4;
    private static final String INDENT = " ".repeat(INDENT_SIZE);
    
    // Main formatting method
    public static String format(String sourceCode) {
        // 1. Tokenize
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.scanTokens();
        
        // 2. Format line by line
        return formatTokens(tokens, sourceCode);
    }
    
    private static String formatTokens(List<Token> tokens, String source) {
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
        int tokenIndex = 0;
        
        String[] lines = source.split("\n", -1);
        
        for (String line : lines) {
            // Get tokens for this line
            List<Token> lineTokens = getTokensForLine(tokens, line);
            
            // Determine indent level changes
            if (containsClosingBrace(lineTokens)) {
                indentLevel--;
            }
            
            // Format the line
            String formattedLine = formatLine(lineTokens, indentLevel);
            formatted.append(formattedLine).append("\n");
            
            // Update indent for next line
            if (containsOpeningBrace(lineTokens)) {
                indentLevel++;
            }
        }
        
        return formatted.toString();
    }
    
    private static String formatLine(List<Token> tokens, int indentLevel) {
        if (tokens.isEmpty()) return ""; // Blank line
        
        // Check for comment-only line
        if (tokens.get(0).type == TokenType.COMMENT) {
            return INDENT.repeat(indentLevel) + tokens.get(0).lexeme.trim();
        }
        
        StringBuilder line = new StringBuilder();
        line.append(INDENT.repeat(indentLevel));
        
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token prev = i > 0 ? tokens.get(i-1) : null;
            Token next = i < tokens.size()-1 ? tokens.get(i+1) : null;
            
            // Add spacing based on token type
            String spacing = determineSpacing(token, prev, next);
            line.append(spacing).append(token.lexeme);
        }
        
        return line.toString().trim();
    }
    
    private static String determineSpacing(Token current, Token prev, Token next) {
        // Complex spacing logic here
        // Examples:
        // - Space before/after binary operators
        // - No space after opening paren
        // - Space after comma
        // etc.
    }
}
```

### Integration with IDE

**MenuBarBuilder.java** - Add Format button
```java
MenuItem formatItem = new MenuItem("Format Code");
formatItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+F"));
formatItem.setOnAction(e -> controller.formatCode());
```

**IDEController.java** - Add format method
```java
public void formatCode() {
    String original = editorPanel.getCode();
    try {
        String formatted = PrettyPrinter.format(original);
        
        // Save caret position (percentage)
        int caretPos = editorPanel.getCodeEditor().getCaretPosition();
        double caretPercentage = (double) caretPos / original.length();
        
        // Replace text
        editorPanel.setCode(formatted);
        
        // Restore caret (approximate position)
        int newCaretPos = (int) (formatted.length() * caretPercentage);
        editorPanel.getCodeEditor().moveTo(newCaretPos);
        
        statusBar.setStatus("Code formatted successfully");
        
    } catch (Exception e) {
        statusBar.setStatus("Format failed: " + e.getMessage());
    }
}
```

---

## Tab Handling Strategy

### Problem
Current editor uses tabs, but 1 tab = 8 character display width, making code look overly indented.

### Solution Options

**Option A: Convert tabs to spaces on format** (⭐ RECOMMENDED)
- Replace all tabs with 4 spaces
- Consistent rendering across all editors
- Standard practice in modern IDEs

**Option B: Configure CodeArea tab size**
```java
codeArea.setStyle("-fx-tab-size: 4;");
```
- Keeps tabs in source
- Display width = 4 characters
- Simpler implementation

**Option C: Hybrid (use spaces for indentation, preserve tabs in strings)**
- Best of both worlds
- More complex to implement

### Recommended: Option A
Replace tabs with spaces during formatting for maximum compatibility.

---

## Algorithm Pseudocode

```
FUNCTION formatCode(sourceCode):
    tokens ← Lexer.scanTokens(sourceCode)
    lines ← splitIntoLines(sourceCode)
    formatted ← []
    indentLevel ← 0
    
    FOR EACH line IN lines:
        lineTokens ← getTokensInLine(tokens, line)
        
        // Adjust indent for closing braces
        IF lineStartsWith(lineTokens, '}'):
            indentLevel ← indentLevel - 1
        END IF
        
        // Format the line
        IF isEmpty(lineTokens):
            formatted.add("")  // Preserve blank lines
        ELSE IF isComment(lineTokens):
            formatted.add(indent(indentLevel) + formatComment(lineTokens))
        ELSE:
            formatted.add(indent(indentLevel) + formatStatement(lineTokens))
        END IF
        
        // Adjust indent for opening braces
        IF lineEndsWith(lineTokens, '{'):
            indentLevel ← indentLevel + 1
        END IF
    END FOR
    
    RETURN join(formatted, "\n")
END FUNCTION

FUNCTION formatStatement(tokens):
    result ← ""
    FOR i ← 0 TO tokens.length - 1:
        token ← tokens[i]
        prev ← tokens[i-1] (if exists)
        next ← tokens[i+1] (if exists)
        
        // Add spacing
        IF needsSpaceBefore(token, prev):
            result ← result + " "
        END IF
        
        result ← result + token.lexeme
        
        IF needsSpaceAfter(token, next):
            result ← result + " "
        END IF
    END FOR
    RETURN result.trim()
END FUNCTION
```

---

## Edge Cases to Handle

### 1. Comments
```bisaya
@@ Start of line comment
    @@ Indented comment
x = 5  @@ Inline comment
```

**Handling**: 
- Start-of-line: Match current indent
- Inline: Preserve at end (or align to column 40)

### 2. Empty Lines
```bisaya
SUGOD

    MUGNA NUMERO x
```

**Handling**: Preserve blank lines (don't indent them)

### 3. String Literals with Special Chars
```bisaya
IPAKITA: "Value: " & x & " units"
```

**Handling**: Never modify string contents

### 4. Nested Blocks
```bisaya
KUNG (x > 0)
PUNDOK{
    ALANG SA (i=1, i<=5, i++)
    PUNDOK{
        IPAKITA: i
    }
}
```

**Handling**: Track indent level with counter

### 5. Malformed Code
```bisaya
SUGOD
    MUGNA NUMERO x
    KUNG (x > 0
    IPAKITA: x
```

**Handling**: Format best-effort, don't crash

### 6. Unicode/Special Characters
```bisaya
@@ Cebuano characters: ñ, é, etc.
MUGNA LETRA niño='ñ'
```

**Handling**: Preserve exactly as-is

---

## Performance Considerations

### Expected Performance
- **Small files** (<100 lines): < 50ms
- **Medium files** (100-500 lines): < 200ms
- **Large files** (500+ lines): < 1000ms

### Optimization Strategies
1. **Single-pass formatting**: Don't rebuild token list
2. **StringBuilder**: Avoid string concatenation
3. **Lazy formatting**: Only format visible region (future enhancement)
4. **Caching**: Cache formatted result until next edit

---

## Testing Strategy

### Unit Tests

```java
@Test
void testBasicIndentation() {
    String input = "SUGOD\nMUGNA NUMERO x\nKATAPUSAN";
    String expected = "SUGOD\n    MUGNA NUMERO x\nKATAPUSAN";
    assertEquals(expected, PrettyPrinter.format(input));
}

@Test
void testNestedBlocks() {
    String input = "SUGOD\nKUNG (x>0)\nPUNDOK{\nIPAKITA: x\n}\nKATAPUSAN";
    String expected = "SUGOD\n    KUNG (x > 0)\n    PUNDOK{\n        IPAKITA: x\n    }\nKATAPUSAN";
    assertEquals(expected, PrettyPrinter.format(input));
}

@Test
void testOperatorSpacing() {
    String input = "x=y+z*2";
    String expected = "x = y + z * 2";
    // (in context of full program)
}

@Test
void testCommentPreservation() {
    // Test both inline and start-of-line comments
}

@Test
void testStringLiteralPreservation() {
    // Ensure strings are not modified
}

@Test
void testTabConversion() {
    String input = "SUGOD\n\t\tMUGNA NUMERO x"; // 2 tabs
    String expected = "SUGOD\n        MUGNA NUMERO x"; // 8 spaces (2*4)
    assertEquals(expected, PrettyPrinter.format(input));
}
```

### Integration Tests
- Format all sample programs
- Verify output matches expected style
- Ensure no semantic changes

---

## UI/UX Design

### Button Placement
**Toolbar**: Add "Format" button next to Run/Clear  
**Menu**: Edit → Format Code (Ctrl+Shift+F)  
**Icon**: 🎨 or ⚙️ formatting icon

### User Feedback
- Status bar: "Code formatted successfully"
- If formatting fails: Show error message
- Undo support: User can Ctrl+Z to revert

### Configuration (Future)
```
Preferences → Bisaya++ Formatting
☐ Indent size: [4] spaces
☐ Space around operators: [✓]
☐ Align inline comments at column: [40]
☐ Blank lines between blocks: [1]
```

---

## Implementation Checklist

- [ ] Create `PrettyPrinter.java` class
- [ ] Implement `format()` main method
- [ ] Implement indentation tracking logic
- [ ] Implement operator spacing logic
- [ ] Implement comment handling
- [ ] Handle tab-to-space conversion
- [ ] Add unit tests (6+ test cases)
- [ ] Integrate with `IDEController`
- [ ] Add Format button to toolbar
- [ ] Add Edit menu item with Ctrl+Shift+F
- [ ] Test with all sample programs
- [ ] Add undo/redo support check
- [ ] Update documentation

---

## Estimated Timeline

**Day 1 (4 hours)**
- Implement `PrettyPrinter` core logic
- Basic indentation tracking
- Unit tests for simple cases

**Day 2 (3 hours)**
- Operator spacing logic
- Comment handling
- Tab conversion

**Day 3 (2 hours)**
- UI integration
- Testing with samples
- Bug fixes and polish

**Total**: ~9 hours (1.5 days)

---

## Success Criteria

✅ **Functionality**
- Formats all valid Bisaya++ programs correctly
- Handles nested blocks properly
- Preserves comments and strings

✅ **Performance**
- Formats 100-line program in < 200ms
- No UI freezing

✅ **Quality**
- Passes all unit tests
- No semantic changes to code
- Consistent style output

✅ **UX**
- Keyboard shortcut works
- Status feedback clear
- Undo-able action

---

## Future Enhancements

1. **Configurable styles**: Let users choose indent size, spacing rules
2. **Format on save**: Auto-format when saving file
3. **Format selection**: Format only selected code region
4. **Style presets**: "Compact", "Readable", "Microsoft", etc.
5. **Format on paste**: Auto-format pasted code
6. **Diff preview**: Show before/after comparison before applying

---

## References

**Similar Features in Other IDEs**
- Eclipse: Ctrl+Shift+F
- VS Code: Shift+Alt+F
- IntelliJ: Ctrl+Alt+L
- Prettier (JS): Opinionated formatter

**Code Style Guides**
- Google Java Style
- Microsoft C# Conventions
- PEP 8 (Python)

---

**Document Status**: Ready for Implementation  
**Recommended Approach**: Token-based with 4-space indentation  
**Priority**: Medium (Nice-to-have for demo, essential for production)
