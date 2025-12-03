# Bisaya++ IDE Architecture

**Component Diagram and Class Relationships**

---

## System Architecture

```mermaid
graph TB
    subgraph "Main Application"
        MAIN[BisayaIDE<br/>Application Entry]
    end
    
    subgraph "Configuration"
        CONFIG[IDEConfig<br/>Constants]
    end
    
    subgraph "UI Components"
        EDITOR[EditorPanel<br/>Code Editor]
        OUTPUT[OutputPanel<br/>Output Display]
        STATUS[StatusBar<br/>Status Info]
    end
    
    subgraph "UI Builders"
        MENU[MenuBarBuilder<br/>Menu Factory]
        TOOL[ToolBarBuilder<br/>Toolbar Factory]
    end
    
    subgraph "Business Logic"
        CTRL[IDEController<br/>Event Handler]
        FILE[FileManager<br/>File I/O]
    end
    
    subgraph "Interpreter"
        INTERP[Bisaya<br/>Interpreter Core]
    end
    
    MAIN --> EDITOR
    MAIN --> OUTPUT
    MAIN --> STATUS
    MAIN --> CTRL
    MAIN --> MENU
    MAIN --> TOOL
    
    CTRL --> EDITOR
    CTRL --> OUTPUT
    CTRL --> STATUS
    CTRL --> FILE
    CTRL --> INTERP
    
    MENU --> CTRL
    TOOL --> CTRL
    
    EDITOR --> CONFIG
    OUTPUT --> CONFIG
    STATUS --> CONFIG
    FILE --> CONFIG
```

---

## Component Interaction Flow

### File Operations
```mermaid
sequenceDiagram
    participant User
    participant Menu as MenuBarBuilder
    participant Ctrl as IDEController
    participant FM as FileManager
    participant EP as EditorPanel
    participant SB as StatusBar
    
    User->>Menu: Click "Open File"
    Menu->>Ctrl: openFile()
    Ctrl->>FM: showOpenDialog()
    FM-->>Ctrl: selectedFile
    Ctrl->>FM: loadFile(file)
    FM-->>Ctrl: fileContent
    Ctrl->>EP: setCode(content)
    Ctrl->>SB: setStatus("Opened: file.bpp")
```

### Program Execution
```mermaid
sequenceDiagram
    participant User
    participant Tool as ToolBarBuilder
    participant Ctrl as IDEController
    participant EP as EditorPanel
    participant OP as OutputPanel
    participant INT as Bisaya Interpreter
    participant SB as StatusBar
    
    User->>Tool: Click "Run"
    Tool->>Ctrl: runProgram()
    Ctrl->>EP: getCode()
    EP-->>Ctrl: sourceCode
    Ctrl->>OP: clear()
    Ctrl->>SB: setStatus("Running...")
    Ctrl->>INT: runSource(code)
    
    alt Success
        INT-->>Ctrl: output
        Ctrl->>OP: setText(output)
        Ctrl->>OP: setNormalStyle()
        Ctrl->>SB: setStatus("✓ Success")
    else Error
        INT-->>Ctrl: exception
        Ctrl->>OP: setText(error)
        Ctrl->>OP: setErrorStyle()
        Ctrl->>SB: setStatus("✗ Failed")
    end
```

---

## Class Diagram

```mermaid
classDiagram
    class BisayaIDE {
        +start(Stage)
        +main(String[])
        -applyStyles(Scene)
    }
    
    class IDEController {
        -Stage stage
        -EditorPanel editorPanel
        -OutputPanel outputPanel
        -StatusBar statusBar
        -FileManager fileManager
        +newFile()
        +openFile()
        +saveFile()
        +saveFileAs()
        +runProgram()
        +clearOutput()
        +updateStatus()
    }
    
    class EditorPanel {
        -TextArea codeEditor
        +getCodeEditor() TextArea
        +getCode() String
        +setCode(String)
        +clear()
        +getCaretPosition() int
    }
    
    class OutputPanel {
        -TextArea outputArea
        +setText(String)
        +clear()
        +setNormalStyle()
        +setErrorStyle()
        +setWarningStyle()
    }
    
    class StatusBar {
        -Label statusLabel
        -Label versionLabel
        +setStatus(String)
        +getStatus() String
    }
    
    class FileManager {
        -Stage stage
        -File currentFile
        +showOpenDialog() File
        +showSaveDialog() File
        +loadFile(File) String
        +saveFile(File, String)
        +showError(String, String)
    }
    
    class MenuBarBuilder {
        -Stage stage
        -IDEController controller
        +build() MenuBar
        -createFileMenu() Menu
        -createRunMenu() Menu
    }
    
    class ToolBarBuilder {
        -IDEController controller
        +build() ToolBar
        -createRunButton() Button
        -createClearButton() Button
    }
    
    class IDEConfig {
        <<static>>
        +WINDOW_TITLE String
        +WINDOW_WIDTH int
        +WINDOW_HEIGHT int
        +EDITOR_FONT String
        +OUTPUT_BG_COLOR String
        +ERROR_TEXT_COLOR String
    }
    
    BisayaIDE --> IDEController : creates
    BisayaIDE --> EditorPanel : creates
    BisayaIDE --> OutputPanel : creates
    BisayaIDE --> StatusBar : creates
    BisayaIDE --> MenuBarBuilder : uses
    BisayaIDE --> ToolBarBuilder : uses
    
    IDEController --> EditorPanel : controls
    IDEController --> OutputPanel : controls
    IDEController --> StatusBar : updates
    IDEController --> FileManager : uses
    
    MenuBarBuilder --> IDEController : delegates to
    ToolBarBuilder --> IDEController : delegates to
    
    EditorPanel ..> IDEConfig : uses
    OutputPanel ..> IDEConfig : uses
    StatusBar ..> IDEConfig : uses
    FileManager ..> IDEConfig : uses
```

---

## Dependency Graph

```mermaid
graph LR
    BisayaIDE --> IDEController
    BisayaIDE --> EditorPanel
    BisayaIDE --> OutputPanel
    BisayaIDE --> StatusBar
    BisayaIDE --> MenuBarBuilder
    BisayaIDE --> ToolBarBuilder
    
    IDEController --> EditorPanel
    IDEController --> OutputPanel
    IDEController --> StatusBar
    IDEController --> FileManager
    IDEController --> Bisaya[Bisaya Interpreter]
    
    MenuBarBuilder --> IDEController
    ToolBarBuilder --> IDEController
    
    EditorPanel -.-> IDEConfig
    OutputPanel -.-> IDEConfig
    StatusBar -.-> IDEConfig
    FileManager -.-> IDEConfig
    
    style BisayaIDE fill:#4CAF50,color:#fff
    style IDEController fill:#2196F3,color:#fff
    style IDEConfig fill:#FF9800,color:#fff
    style Bisaya fill:#9C27B0,color:#fff
```

---

## Layered Architecture

```
┌─────────────────────────────────────────────────────┐
│              Presentation Layer                     │
│  (BisayaIDE, EditorPanel, OutputPanel, StatusBar)  │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│              UI Builder Layer                       │
│        (MenuBarBuilder, ToolBarBuilder)             │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│              Controller Layer                       │
│             (IDEController)                         │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│              Service Layer                          │
│              (FileManager)                          │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│              Core Layer                             │
│           (Bisaya Interpreter)                      │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│              Configuration Layer                    │
│               (IDEConfig)                           │
└─────────────────────────────────────────────────────┘
```

---

## Data Flow

### User Input → Output
```
User Action
    ↓
MenuBarBuilder / ToolBarBuilder
    ↓
IDEController (Event Handler)
    ↓
EditorPanel / FileManager (Get Data)
    ↓
Bisaya Interpreter (Process)
    ↓
OutputPanel (Display Results)
    ↓
StatusBar (Update Status)
```

### Configuration Flow
```
IDEConfig (Static Constants)
    ↓
EditorPanel    OutputPanel    StatusBar    FileManager
    ↓               ↓              ↓            ↓
  (Applies fonts, colors, messages, paths)
```

---

## Responsibility Matrix

| Component | Responsibility | Depends On | Used By |
|-----------|---------------|------------|---------|
| **BisayaIDE** | Application entry, component assembly | All UI components | JavaFX runtime |
| **IDEController** | Business logic, event coordination | EditorPanel, OutputPanel, StatusBar, FileManager, Bisaya | Builders |
| **EditorPanel** | Code editor UI | IDEConfig | BisayaIDE, IDEController |
| **OutputPanel** | Output display UI | IDEConfig | BisayaIDE, IDEController |
| **StatusBar** | Status display UI | IDEConfig | BisayaIDE, IDEController |
| **MenuBarBuilder** | Menu creation | IDEController | BisayaIDE |
| **ToolBarBuilder** | Toolbar creation | IDEController | BisayaIDE |
| **FileManager** | File I/O operations | IDEConfig | IDEController |
| **IDEConfig** | Configuration constants | None | All UI components |

---

## Component Coupling

### Low Coupling
- **IDEConfig** → Used by all, depends on none
- **EditorPanel** → Self-contained UI component
- **OutputPanel** → Self-contained UI component
- **StatusBar** → Self-contained UI component

### Medium Coupling
- **FileManager** → Depends on Stage (for dialogs)
- **Builders** → Depend on IDEController

### High Coupling (Coordinator)
- **IDEController** → Central coordinator (by design)
- **BisayaIDE** → Main assembler (by design)

---

## Extension Points

### Adding New Features

**1. New Menu Item**
```java
// Modify MenuBarBuilder.createFileMenu()
MenuItem exportItem = new MenuItem("Export...");
exportItem.setOnAction(e -> controller.exportFile());
```

**2. New Panel**
```java
// Create new component
public class ConsolePanel extends VBox { ... }

// Wire in BisayaIDE
ConsolePanel consolePanel = new ConsolePanel();
splitPane.getItems().add(consolePanel);
```

**3. New Controller Action**
```java
// Add to IDEController
public void formatCode() {
    String code = editorPanel.getCode();
    String formatted = CodeFormatter.format(code);
    editorPanel.setCode(formatted);
}
```

---

## Testing Strategy

### Unit Tests (Per Component)
- ✅ IDEController logic methods
- ✅ FileManager I/O operations
- ✅ EditorPanel/OutputPanel API methods
- ✅ IDEConfig constant values

### Integration Tests
- ✅ BisayaIDE component assembly
- ✅ Controller ↔ Panel interactions
- ✅ Menu/Toolbar → Controller delegation

### UI Tests (TestFX)
- ✅ Button clicks trigger correct actions
- ✅ Menu items invoke controller methods
- ✅ File dialogs open correctly
- ✅ Editor/Output update as expected

---

## Summary

**Architecture Pattern**: MVC-like with UI Builders  
**Component Count**: 9 classes  
**Total Complexity**: Low (well-separated concerns)  
**Maintainability**: High (single responsibility)  
**Extensibility**: High (clear extension points)  
**Testability**: High (dependency injection)

---

**Document Version**: 1.0  
**Last Updated**: November 8, 2025  
**Status**: Active Architecture
