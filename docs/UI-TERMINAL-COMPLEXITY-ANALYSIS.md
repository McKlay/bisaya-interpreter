# UI Terminal for Bisaya++ Interpreter - Complexity Analysis

## Executive Summary

Creating a UI terminal similar to OnlineGDB for the Bisaya++ interpreter would significantly enhance demo presentations by providing:
- **Real-time code editing** with syntax awareness
- **Immediate execution** with formatted output
- **Clear error reporting** with line numbers and descriptions
- **Professional presentation** suitable for academic demonstrations

**Overall Complexity**: **Medium to High** (3-6 weeks development time)

---

## 1. Architecture Overview

### Current State
```
CLI Interface (Bisaya.java)
    ↓
File Input → Lexer → Parser → Interpreter → Console Output
```

### Proposed UI Terminal Architecture
```
JavaFX/Swing GUI
    ↓
┌─────────────────────────────────────────┐
│  Editor Panel (Top)                     │
│  - Syntax highlighting                  │
│  - Line numbers                         │
│  - Code input area                      │
└─────────────────────────────────────────┘
         ↓ [Run Button]
┌─────────────────────────────────────────┐
│  Output Panel (Bottom)                  │
│  - Program output                       │
│  - Error messages with line numbers     │
│  - Input prompts (for DAWAT)            │
└─────────────────────────────────────────┘
```

---

## 2. Component Breakdown

### 2.1 Editor Component (Medium Complexity)
**Required Features:**
- Multi-line text area with scrolling
- Line numbering display
- Basic syntax highlighting for Bisaya++ keywords
- Tab/indentation support
- Copy/paste/undo functionality

**Technology Options:**
- **JavaFX TextArea** (Recommended): Native, modern, good documentation
- **Swing JTextPane**: Mature but older, more complex for syntax highlighting
- **RSyntaxTextArea**: Third-party library with built-in syntax highlighting

**Complexity**: **Medium** (1-2 weeks)

**Implementation Challenges:**
- Custom syntax highlighting for Bisaya++ keywords (SUGOD, KATAPUSAN, MUGNA, etc.)
- Performance with large code files
- Custom lexer integration for real-time syntax checking

---

### 2.2 Output Panel (Low-Medium Complexity)
**Required Features:**
- Read-only text display area
- Separate sections for:
  - Standard output (IPAKITA results)
  - Error messages (styled in red/different color)
  - Input prompts (for DAWAT command)
- Auto-scroll to bottom
- Clear output functionality

**Technology Options:**
- **JavaFX TextArea/TextFlow**: For styled output
- **WebView**: For HTML-formatted output (more flexible styling)

**Complexity**: **Low-Medium** (3-5 days)

**Implementation Challenges:**
- Distinguishing between output types (normal vs error)
- Formatting special characters ($, &, escape sequences)
- Managing output buffer for large programs

---

### 2.3 Interactive Input Handling (High Complexity)
**Required Features:**
- Modal input dialog for DAWAT command
- Real-time input validation (comma-separated values)
- Type checking before accepting input
- Cancel/timeout handling

**Current Challenge:**
```java
// Current implementation in Interpreter.java uses Scanner
Scanner scanner = new Scanner(System.in);
String input = scanner.nextLine();
```

**Proposed Solution:**
```java
// Need to abstract input/output to support GUI
interface IOHandler {
    String readInput(String prompt);
    void writeOutput(String text);
    void writeError(String error);
}

// GUI Implementation
class GUIIOHandler implements IOHandler {
    @Override
    public String readInput(String prompt) {
        // Show modal dialog with prompt
        // Block until user provides input
        return userInputFromDialog;
    }
}
```

**Complexity**: **High** (1-2 weeks)

**Implementation Challenges:**
- Thread synchronization (GUI thread vs interpreter thread)
- Blocking GUI while waiting for input without freezing UI
- Handling multiple inputs in one DAWAT statement
- Input validation before passing to interpreter

---

### 2.4 Error Reporting Enhancement (Medium Complexity)
**Current State:**
```
Error messages go to System.err with line numbers
Example: "[Line 5] Error: Undefined variable 'x'"
```

**Enhanced Error Display Required:**
```
╔════════════════════════════════════════╗
║ ERROR                                  ║
╠════════════════════════════════════════╣
║ Line 5: Undefined variable 'x'        ║
║                                        ║
║ Code Context:                          ║
║   4 | MUGNA NUMERO y = 10              ║
║ > 5 | IPAKITA: x + y                   ║
║   6 | KATAPUSAN                        ║
║                                        ║
║ Suggestion: Declare 'x' using MUGNA   ║
╚════════════════════════════════════════╝
```

**Required Modifications:**
- Enhance `ErrorReporter.java` to include:
  - Error categorization (Lexical, Syntax, Runtime)
  - Code context (lines before/after error)
  - Helpful suggestions
  - Severity levels (Error, Warning, Info)

**Complexity**: **Medium** (1 week)

**Implementation Challenges:**
- Storing source code for context display
- Mapping error positions to exact code lines
- Generating helpful suggestions programmatically
- Formatting multi-line error messages

---

## 3. Technical Implementation Details

### 3.1 Threading Model (Critical)

**Problem**: GUI must remain responsive while interpreter executes

**Solution**: Separate execution thread
```java
class InterpreterRunner extends Task<String> {
    @Override
    protected String call() throws Exception {
        // Run interpreter in background
        String output = interpreter.execute(code);
        return output;
    }
}

// In GUI
Task<String> task = new InterpreterRunner(code);
task.setOnSucceeded(e -> {
    outputArea.setText(task.getValue());
});
new Thread(task).start();
```

**Complexity**: **High** (threading bugs are hard to debug)

---

### 3.2 Syntax Highlighting (Medium Complexity)

**Approach**: Real-time lexical analysis
```java
class SyntaxHighlighter {
    void highlightCode(String code) {
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.scanTokens();
        
        for (Token token : tokens) {
            switch (token.type) {
                case KEYWORD -> applyStyle("keyword-blue");
                case STRING -> applyStyle("string-green");
                case NUMBER -> applyStyle("number-orange");
                case COMMENT -> applyStyle("comment-gray");
            }
        }
    }
}
```

**Complexity**: **Medium** (leverage existing Lexer)

**Challenge**: Performance optimization for real-time highlighting

---

### 3.3 UI Layout (Low-Medium Complexity)

**JavaFX Example Structure:**
```java
public class BisayaIDEApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Top: Menu bar (File, Edit, Run, Help)
        MenuBar menuBar = createMenuBar();
        
        // Center: Split pane (Editor top, Output bottom)
        CodeArea editor = new CodeArea();
        TextArea output = new TextArea();
        SplitPane splitPane = new SplitPane(editor, output);
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.6); // 60% editor, 40% output
        
        // Bottom: Status bar (line:col, ready/running)
        HBox statusBar = createStatusBar();
        
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(splitPane);
        root.setBottom(statusBar);
        
        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setTitle("Bisaya++ IDE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
```

**Complexity**: **Low-Medium** (standard UI patterns)

---

## 4. Required Code Modifications

### 4.1 Refactor I/O System (High Priority)

**Current Problem**: Hardcoded `System.out` and `System.in`

**Files to Modify:**
1. **Interpreter.java** (~200 lines affected)
   - Replace all `System.out.print()` calls
   - Replace `Scanner(System.in)` usage
   - Add `IOHandler` interface dependency injection

2. **ErrorReporter.java** (~50 lines affected)
   - Route errors through `IOHandler`
   - Add structured error objects (not just strings)

3. **Bisaya.java** (CLI entry point)
   - Create `ConsoleIOHandler` for backward compatibility
   - Keep existing CLI functionality

**Estimated Effort**: **1 week**

---

### 4.2 Add Error Context Tracking (Medium Priority)

**New Class Required:**
```java
public class ErrorContext {
    private final int line;
    private final int column;
    private final String errorType;
    private final String message;
    private final String suggestion;
    private final List<String> codeContext; // 3 lines before/after
    
    public String formatForDisplay() {
        // Generate formatted error message with context
    }
}
```

**Files to Modify:**
- `ErrorReporter.java`: Store source code lines
- `Lexer.java`, `Parser.java`, `Interpreter.java`: Pass context info

**Estimated Effort**: **3-5 days**

---

### 4.3 Thread-Safe Interpreter (Critical Priority)

**Current Issue**: Interpreter has mutable state (Environment)

**Required Changes:**
- Ensure `Environment.java` is thread-safe
- Create new interpreter instance per execution
- Implement cancellation mechanism for infinite loops

**Estimated Effort**: **5-7 days**

---

## 5. Feature Prioritization

### Phase 1: MVP (Minimum Viable Product) - 2 weeks
✅ Basic GUI with editor and output panels  
✅ Run button that executes code  
✅ Display output and basic errors  
✅ File open/save functionality  

**Goal**: Replace CLI with basic GUI for demos

---

### Phase 2: Enhanced UX - 2 weeks
✅ Syntax highlighting for Bisaya++ keywords  
✅ Line numbers  
✅ Better error formatting with line references  
✅ Clear output button  
✅ Keyboard shortcuts (Ctrl+R to run)  

**Goal**: Professional-looking IDE

---

### Phase 3: Advanced Features - 2 weeks
✅ Interactive input (DAWAT) with modal dialogs  
✅ Error context with code snippets  
✅ Execution cancellation (stop button)  
✅ Sample programs menu  
✅ Syntax error highlighting in editor  

**Goal**: Feature parity with online compilers

---

## 6. Technology Stack Recommendation

### Recommended: JavaFX
**Pros:**
- Modern, actively maintained
- Good documentation and community
- Built-in CSS styling support
- Scene Builder for visual design
- Native look and feel

**Cons:**
- Requires Java 11+ with JavaFX SDK
- Larger learning curve than Swing
- Additional dependencies

**Verdict**: **Best choice for modern, professional UI**

---

### Alternative: Swing
**Pros:**
- Included in JDK (no extra dependencies)
- Mature, stable
- More examples available

**Cons:**
- Older technology
- Less modern look
- More code for syntax highlighting

**Verdict**: **Acceptable for quick prototype**

---

### Not Recommended: Web-based (Electron/Web UI)
**Why not:**
- Requires rewriting interpreter in JavaScript or using JNI
- Overhead of running web server
- More complex deployment
- Overkill for academic demo

---

## 7. Deployment Considerations

### Packaging Options

1. **Executable JAR with JavaFX**
   ```bash
   # Include JavaFX libraries
   gradle build
   # Creates: bisaya-ide.jar (15-20 MB)
   ```

2. **Native Installer (jpackage)**
   ```bash
   # Create Windows .exe installer
   jpackage --input build/libs \
            --name "Bisaya++ IDE" \
            --main-jar bisaya-ide.jar \
            --type exe
   ```

3. **Portable Package**
   - Bundle JRE with application
   - Size: ~60-80 MB
   - No Java installation required

**Recommendation for Demo**: Executable JAR (simplest)

---

## 8. Risk Assessment

### High Risks 🔴
1. **Threading issues** causing UI freezes
   - Mitigation: Thorough testing, proper thread separation
   
2. **Input handling deadlocks** during DAWAT
   - Mitigation: Use JavaFX modal dialogs properly

3. **Performance** with large programs
   - Mitigation: Implement execution timeout

### Medium Risks 🟡
1. **Syntax highlighting lag** during typing
   - Mitigation: Debounce highlighting (wait 200ms after typing stops)

2. **Cross-platform compatibility** issues
   - Mitigation: Test on Windows, Mac, Linux

3. **Learning curve** for team members
   - Mitigation: Clear documentation, code examples

### Low Risks 🟢
1. UI layout complexity
2. File I/O operations
3. Basic error display

---

## 9. Development Timeline

### Optimistic (With experienced JavaFX developer)
- **Week 1-2**: Core UI + basic execution
- **Week 3-4**: Error handling + syntax highlighting
- **Week 5**: Interactive input + polish
- **Week 6**: Testing + bug fixes

**Total**: **6 weeks**

---

### Realistic (Learning while building)
- **Week 1-3**: JavaFX learning + basic UI
- **Week 4-6**: Execution engine integration
- **Week 7-9**: Error handling + input dialogs
- **Week 10-12**: Testing + refinement

**Total**: **12 weeks (3 months)**

---

### Minimal (For urgent demo)
- **Week 1**: Basic editor + output (no highlighting)
- **Week 2**: Execution + simple error display
- **Week 3**: Bug fixes + packaging

**Total**: **3 weeks** (bare minimum, sacrifices polish)

---

## 10. Cost-Benefit Analysis

### Benefits for Demo Presentation ✅
1. **Professional appearance** - Looks like a real IDE
2. **Interactive demonstration** - Edit and run code live
3. **Clear error messages** - Easier to explain language concepts
4. **No file management** - No need to manage .bisaya files
5. **Repeatable** - Easy to re-run examples
6. **Impressive** - Shows technical capability

### Costs ⚠️
1. **Development time**: 3-12 weeks depending on scope
2. **Maintenance**: UI bugs to fix, cross-platform issues
3. **Complexity**: More code to maintain (UI + interpreter)
4. **Learning curve**: Team needs JavaFX knowledge
5. **Deployment**: Larger package size, more dependencies

---

## 11. Alternative: Quick Win Solution

### Option A: Enhanced CLI with Colors (1-2 days)
```
Use ANSI color codes in terminal:
- Green for output
- Red for errors
- Blue for prompts
- Syntax: Wrap existing System.out calls
```

**Pros**: Minimal effort, improves demo significantly  
**Cons**: Terminal-only, not as impressive as GUI

---

### Option B: Simple Swing Dialog (3-5 days)
```java
// Minimal GUI wrapper
JTextArea editor = new JTextArea();
JTextArea output = new JTextArea();
JButton runButton = new JButton("Run");

// No syntax highlighting, just basic functionality
```

**Pros**: Fast to build, achieves core goal  
**Cons**: Looks basic, missing advanced features

---

## 12. Recommendation

### For Academic Demo Presentation

**Best Approach**: **Phase 1 MVP with Enhanced CLI Colors**

**Rationale:**
1. **Time-efficient**: 1 week total effort
2. **Low-risk**: Minimal code changes
3. **Good impression**: Professional enough for demo
4. **Fallback**: CLI still works if GUI has issues

**Implementation Plan:**
```
Day 1-2: Enhanced CLI with ANSI colors + better error formatting
Day 3-5: Basic JavaFX GUI (editor + output, no highlighting)
Day 6-7: Testing + packaging

Result: Working GUI with CLI backup
```

### If More Time Available (>6 weeks)
**Go for Full UI Terminal** - Follow Phase 1-3 plan for production-quality IDE

---

## 13. Sample Code Structure

### Minimal GUI Example (JavaFX)
```java
public class BisayaIDE extends Application {
    private TextArea editor;
    private TextArea output;
    private Interpreter interpreter;
    
    @Override
    public void start(Stage stage) {
        editor = new TextArea();
        editor.setPromptText("Enter Bisaya++ code here...");
        
        output = new TextArea();
        output.setEditable(false);
        output.setStyle("-fx-text-fill: #00ff00; -fx-background-color: #1e1e1e;");
        
        Button runButton = new Button("▶ Run");
        runButton.setOnAction(e -> executeCode());
        
        VBox layout = new VBox(10, 
            new Label("Bisaya++ Code:"), 
            editor, 
            runButton,
            new Label("Output:"), 
            output
        );
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setTitle("Bisaya++ Interpreter");
        stage.setScene(scene);
        stage.show();
    }
    
    private void executeCode() {
        String code = editor.getText();
        output.clear();
        
        try {
            // Redirect output to GUI
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            System.setOut(ps);
            
            // Run interpreter
            Bisaya.runCode(code);
            
            // Display output
            output.setText(baos.toString());
        } catch (Exception ex) {
            output.setStyle("-fx-text-fill: #ff0000;");
            output.setText("ERROR: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

**Lines of Code**: ~100 lines for basic functionality

---

## 14. Conclusion

### Summary Matrix

| Approach | Time | Complexity | Demo Impact | Maintainability |
|----------|------|------------|-------------|-----------------|
| **Enhanced CLI** | 2 days | Low | Medium | Easy |
| **Basic Swing GUI** | 1 week | Medium | Medium-High | Medium |
| **JavaFX MVP** | 2-3 weeks | Medium | High | Medium |
| **Full IDE (Phase 1-3)** | 6-12 weeks | High | Very High | Complex |

### Final Verdict

**For immediate demo needs**: Start with **Enhanced CLI + Basic JavaFX** (1 week total)

**For long-term project**: Invest in **Full JavaFX IDE** (12 weeks for polished product)

**Biggest Challenge**: Interactive input (DAWAT) - requires careful threading

**Biggest Benefit**: Professional appearance significantly improves demo credibility

---

## 15. Next Steps (If Proceeding)

### Week 1 Action Items
1. ✅ Set up JavaFX in Gradle build file
2. ✅ Create basic window with editor/output split
3. ✅ Implement run button with simple execution
4. ✅ Add file open/save dialogs
5. ✅ Test on target demo machine

### Week 2 Action Items
1. ✅ Refactor Interpreter for IOHandler abstraction
2. ✅ Implement GUIIOHandler
3. ✅ Add basic error formatting
4. ✅ Create sample programs menu
5. ✅ Polish UI appearance

### Week 3+ (Optional)
1. ✅ Syntax highlighting implementation
2. ✅ Interactive input dialogs
3. ✅ Advanced error context
4. ✅ Execution cancellation
5. ✅ Comprehensive testing

---

## Appendix: Useful Resources

### JavaFX Documentation
- Official Guide: https://openjfx.io/
- Scene Builder: https://gluonhq.com/products/scene-builder/
- CSS Reference: https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/doc-files/cssref.html

### Libraries to Consider
- **RichTextFX**: Advanced text editing (syntax highlighting)
  - GitHub: https://github.com/FXMisc/RichTextFX
- **ControlsFX**: Enhanced UI controls
  - Website: https://controlsfx.github.io/

### Similar Projects for Reference
- **JReplica**: Simple Java REPL with GUI
- **BlueJ**: Educational Java IDE (good UI patterns to study)

---

**Document Version**: 1.0  
**Last Updated**: November 7, 2025  
**Author**: Bisaya++ Development Team  
**Purpose**: Feasibility analysis for UI terminal implementation
