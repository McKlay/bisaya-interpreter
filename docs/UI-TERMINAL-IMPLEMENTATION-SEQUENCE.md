# UI Terminal Implementation Sequence

## Overview
Step-by-step guide to build a GUI terminal for Bisaya++ interpreter, starting with MVP for demo presentations and optionally expanding to full-featured IDE.

**Target**: Phase 1 MVP (2 weeks) → Demo-ready basic GUI  
**Optional**: Phase 2-3 (4+ weeks) → Professional IDE

---

## Phase 1: MVP - Basic GUI Terminal (2 weeks)

### Week 1: Setup & Core UI

**Day 1-2: Project Setup**
1. Add JavaFX dependencies to `build.gradle`
2. Create new package: `com.bisayapp.ui`
3. Set up main GUI class: `BisayaIDE.java`
4. Configure module path for JavaFX

**Day 3-4: Build UI Layout**
1. Create split-pane layout (editor top, output bottom)
2. Add TextArea for code editor
3. Add TextArea for output display (read-only)
4. Add Run button with basic styling
5. Set window size, title, and icon

**Day 5: Wire Basic Execution**
1. Connect Run button to interpreter
2. Capture output to display in GUI
3. Handle basic error display
4. Test with simple programs (no DAWAT)

### Week 2: Refinement & Integration

**Day 6-7: File Operations**
1. Add "Open File" menu option
2. Add "Save File" menu option
3. Load .bpp files into editor
4. Basic file dialog integration

**Day 8-9: Error Handling**
1. Distinguish errors from normal output
2. Color code output (green) vs errors (red)
3. Display error messages clearly
4. Test with programs that have errors

**Day 10: Polish & Package**
1. Clean up UI appearance
2. Add keyboard shortcuts (Ctrl+O, Ctrl+S, Ctrl+R)
3. Create executable JAR
4. Test on demo machine

**MVP Deliverable**: Working GUI that runs Bisaya++ code with output/error display

---

## Phase 2: Enhanced UX (2 weeks, optional)

### Week 3: Visual Improvements

**Syntax Highlighting**
- Integrate existing Lexer for token recognition
- Color keywords (blue), strings (green), numbers (orange), comments (gray)
- Implement debounced highlighting (avoid lag while typing)

**Line Numbers**
- Add line number column on left side
- Synchronize scrolling with editor
- Highlight current line

**UI Polish**
- Add Clear Output button
- Improve color scheme (dark/light themes)
- Add status bar (line:column position)

### Week 4: User Experience

**Keyboard Shortcuts**
- Ctrl+R: Run program
- Ctrl+L: Clear output
- F5: Reload file
- Ctrl+N: New file

**Sample Programs Menu**
- Add "Examples" menu
- Load sample programs from `/samples` folder
- Quick access to test programs

**Error Formatting**
- Show line numbers in error messages
- Clickable errors (jump to line)
- Better error message formatting

---

## Phase 3: Advanced Features (2+ weeks, optional)

### Week 5: Interactive Input

**DAWAT Command Support**
1. Refactor I/O system (create `IOHandler` interface)
2. Implement `GUIIOHandler` for modal dialogs
3. Show input prompt when DAWAT encountered
4. Validate comma-separated input before passing to interpreter
5. Handle input cancellation gracefully

**Threading**
- Run interpreter in background thread
- Keep UI responsive during execution
- Add Stop button for cancellation

### Week 6+: Advanced Polish

**Error Context Display**
- Show code snippet around error line
- Provide helpful suggestions
- Error categorization (Lexical/Syntax/Runtime)

**Additional Features**
- Undo/Redo support
- Find/Replace functionality
- Export output to file
- Recent files list
- Auto-save functionality

---

## Implementation Order Rationale

### Why MVP First?
1. **Quick wins** - Demo-ready in 2 weeks
2. **Risk mitigation** - Core functionality validated early
3. **Feedback loop** - Get user input before investing more time
4. **Fallback option** - CLI still available if issues arise

### Why This Sequence?
- **UI Layout → Execution**: Can't test without basic UI
- **File I/O → Highlighting**: Need content to highlight
- **Basic errors → Enhanced errors**: Incremental improvement
- **DAWAT last**: Most complex, requires I/O refactoring

---

## Key Technical Tasks

### Core Refactoring Needed

**For Phase 1 (Minimal)**
```java
// Just redirect System.out temporarily
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
System.setOut(new PrintStream(outputStream));
// Run interpreter
// Display output
```

**For Phase 3 (Proper)**
```java
// Create IOHandler interface
interface IOHandler {
    void writeOutput(String text);
    void writeError(String error);
    String readInput(String prompt);
}

// Inject into Interpreter
Interpreter interpreter = new Interpreter(new GUIIOHandler());
```

### Files to Create

**Phase 1**
- `BisayaIDE.java` - Main GUI application
- `BisayaIDEController.java` - Event handlers (optional)

**Phase 2**
- `SyntaxHighlighter.java` - Keyword coloring
- `LineNumberFactory.java` - Line number display

**Phase 3**
- `IOHandler.java` - I/O abstraction interface
- `GUIIOHandler.java` - GUI implementation
- `ConsoleIOHandler.java` - CLI backward compatibility
- `ErrorContext.java` - Enhanced error formatting

### Files to Modify

**Phase 1**: None (output redirection only)

**Phase 3**: 
- `Interpreter.java` - Accept IOHandler parameter
- `ErrorReporter.java` - Use IOHandler for errors
- `Bisaya.java` - Create ConsoleIOHandler for CLI

---

## Testing Strategy

### Phase 1 Testing
- ✅ Load and display sample programs
- ✅ Run simple arithmetic programs
- ✅ Display correct output
- ✅ Show errors in red
- ✅ Open/save files correctly

### Phase 2 Testing
- ✅ Keywords properly highlighted
- ✅ Line numbers match code
- ✅ Shortcuts work correctly
- ✅ Sample programs load correctly

### Phase 3 Testing
- ✅ DAWAT prompts for input correctly
- ✅ Input validation works
- ✅ Errors show code context
- ✅ Stop button cancels execution
- ✅ UI stays responsive during long runs

---

## Deployment Checklist

### Before Demo
- [ ] Test all sample programs
- [ ] Verify error display works
- [ ] Check file open/save functionality
- [ ] Test on presentation machine
- [ ] Create desktop shortcut
- [ ] Prepare backup (CLI + sample files)

### Packaging
```bash
# Build executable JAR
gradle clean build

# JAR location
app/build/libs/bisaya-ide.jar

# Run command
java -jar bisaya-ide.jar
```

### Demo Tips
1. Pre-load interesting example in editor
2. Keep sample programs ready in menu
3. Show error handling capability
4. Demonstrate file save/load
5. Have CLI ready as backup

---

## Quick Reference: Gradle Dependencies

```gradle
// In build.gradle
dependencies {
    // JavaFX (choose based on OS)
    implementation 'org.openjfx:javafx-controls:17.0.2'
    implementation 'org.openjfx:javafx-fxml:17.0.2'
    
    // Optional: For advanced text editing
    implementation 'org.fxmisc.richtext:richtextfx:0.11.0'
}
```

---

## Success Metrics

### Phase 1 (MVP) Success
- ✅ Runs all increment test programs
- ✅ Displays output correctly with $ and & handling
- ✅ Shows errors in different color
- ✅ Can open/save .bpp files
- ✅ Demo-ready appearance

### Phase 2 Success
- ✅ Professional IDE look
- ✅ Syntax highlighting works smoothly
- ✅ Users can navigate code easily
- ✅ Shortcuts improve workflow

### Phase 3 Success
- ✅ DAWAT works in GUI
- ✅ All language features supported
- ✅ Better than CLI experience
- ✅ Suitable for production use

---

## Fallback Plan

If UI development takes longer than expected:

**Plan B: Enhanced CLI** (1-2 days)
- Add ANSI color codes for output
- Better formatted error messages
- Still acceptable for demo

**Plan C: Web UI** (if Java GUI fails)
- Simple HTML/JS frontend
- Java backend via HTTP
- More portable, simpler to demo

---

## Timeline Summary

| Phase | Duration | Outcome | Priority |
|-------|----------|---------|----------|
| **Phase 1: MVP** | 2 weeks | Demo-ready GUI | **High** |
| **Phase 2: UX** | 2 weeks | Professional look | Medium |
| **Phase 3: Advanced** | 2+ weeks | Full-featured IDE | Low |

**Recommendation**: Complete Phase 1, demo it, then decide if Phase 2-3 needed based on feedback.

---

## Next Steps

1. **Decision**: Confirm Phase 1 MVP approach
2. **Setup**: Add JavaFX to project (Day 1)
3. **Build**: Follow Week 1 sequence
4. **Review**: Test at end of Week 1
5. **Complete**: Finish Phase 1 in Week 2
6. **Demo**: Present to stakeholders
7. **Iterate**: Decide on Phase 2-3 based on feedback

---

**Document Version**: 1.0  
**Last Updated**: November 8, 2025  
**Purpose**: Implementation guide for UI terminal development
