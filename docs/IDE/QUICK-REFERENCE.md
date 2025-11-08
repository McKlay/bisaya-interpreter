# IDE Refactoring Quick Reference

**Quick guide to the refactored Bisaya++ IDE structure**

---

## 📁 File Overview

| File | Lines | Purpose |
|------|-------|---------|
| `BisayaIDE.java` | 95 | Main application entry point |
| `IDEController.java` | 215 | Business logic & event handling |
| `EditorPanel.java` | 80 | Code editor component |
| `OutputPanel.java` | 95 | Output display component |
| `StatusBar.java` | 45 | Status bar component |
| `MenuBarBuilder.java` | 90 | Menu factory |
| `ToolBarBuilder.java` | 60 | Toolbar factory |
| `FileManager.java` | 115 | File I/O operations |
| `IDEConfig.java` | 55 | Configuration constants |

**Total**: 9 files, ~850 lines (vs 1 file, 513 lines before)

---

## 🎯 Quick Navigation

### Need to change...

**Window title or size?**  
→ `IDEConfig.java` → `WINDOW_TITLE`, `WINDOW_WIDTH`, `WINDOW_HEIGHT`

**Editor font or colors?**  
→ `IDEConfig.java` → `EDITOR_FONT`, `EDITOR_BG_COLOR`

**Output colors?**  
→ `IDEConfig.java` → `OUTPUT_TEXT_COLOR`, `ERROR_TEXT_COLOR`

**Add menu item?**  
→ `MenuBarBuilder.java` → `createFileMenu()` or `createRunMenu()`

**Add toolbar button?**  
→ `ToolBarBuilder.java` → `build()`

**Modify file operations?**  
→ `FileManager.java`

**Add new feature?**  
→ `IDEController.java` → add method, wire in builder

**Change status display?**  
→ `StatusBar.java` or `IDEController.updateStatus()`

---

## 🏗️ Component Relationships

```
BisayaIDE (Main)
    ├── creates → EditorPanel
    ├── creates → OutputPanel
    ├── creates → StatusBar
    ├── creates → IDEController
    │       ├── uses → EditorPanel
    │       ├── uses → OutputPanel
    │       ├── uses → StatusBar
    │       └── uses → FileManager
    ├── uses → MenuBarBuilder
    │       └── delegates to → IDEController
    └── uses → ToolBarBuilder
            └── delegates to → IDEController

All components use → IDEConfig
```

---

## 📝 Common Tasks

### Adding a New Menu Item

1. **Modify** `MenuBarBuilder.java`
2. **Add** to appropriate menu:
```java
MenuItem newItem = new MenuItem("My Action");
newItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
newItem.setOnAction(e -> controller.myAction());
fileMenu.getItems().add(newItem);
```
3. **Implement** in `IDEController.java`:
```java
public void myAction() {
    // Your logic here
}
```

### Adding a New Panel

1. **Create** new component (extend `VBox` or `HBox`)
2. **Add** to `BisayaIDE.start()`:
```java
MyPanel myPanel = new MyPanel();
splitPane.getItems().add(myPanel);
```
3. **Wire** to controller if needed

### Changing Colors

**Editor background**: `IDEConfig.EDITOR_BG_COLOR`  
**Editor text**: `IDEConfig.EDITOR_TEXT_COLOR`  
**Output background**: `IDEConfig.OUTPUT_BG_COLOR`  
**Output text**: `IDEConfig.OUTPUT_TEXT_COLOR`  
**Error text**: `IDEConfig.ERROR_TEXT_COLOR`  
**Warning text**: `IDEConfig.WARNING_TEXT_COLOR`

### Changing Messages

All UI messages in `IDEConfig`:
- `EDITOR_PROMPT`
- `OUTPUT_PROMPT`
- `SUCCESS_MESSAGE`
- `ERROR_PREFIX`

---

## 🔄 Event Flow

**User clicks Run button**
```
ToolBarBuilder (button click)
    ↓
IDEController.runProgram()
    ↓
EditorPanel.getCode()
    ↓
Bisaya.runSource(code)
    ↓
OutputPanel.setText(result)
    ↓
StatusBar.setStatus("Success")
```

**User clicks Open File**
```
MenuBarBuilder (menu click)
    ↓
IDEController.openFile()
    ↓
FileManager.showOpenDialog()
    ↓
FileManager.loadFile(file)
    ↓
EditorPanel.setCode(content)
    ↓
StatusBar.setStatus("Opened: file.bpp")
```

---

## 🧪 Testing

**Build & Test**
```powershell
.\gradlew clean build
```

**Run IDE**
```powershell
.\gradlew :app:runIDE
```

**Quick JAR**
```powershell
.\gradlew :app:jar
java -jar app\build\libs\bisaya-ide.jar
```

---

## 🐛 Debugging Tips

**Issue**: Component not displaying  
**Check**: `BisayaIDE.start()` - is component added to scene?

**Issue**: Button not working  
**Check**: Builder → Controller wiring, Controller method implementation

**Issue**: Style not applying  
**Check**: IDEConfig constants, component constructor

**Issue**: File operations failing  
**Check**: FileManager methods, exception handling

**Issue**: Status not updating  
**Check**: IDEController.updateStatus() listeners

---

## 📊 Class Responsibilities

| Class | Primary Job | Secondary Job |
|-------|-------------|---------------|
| **BisayaIDE** | Assemble UI | Apply styles |
| **IDEController** | Handle events | Coordinate components |
| **EditorPanel** | Display code | Provide editor API |
| **OutputPanel** | Display output | Style output (colors) |
| **StatusBar** | Show status | Show version |
| **MenuBarBuilder** | Create menus | Set shortcuts |
| **ToolBarBuilder** | Create toolbar | Style buttons |
| **FileManager** | File I/O | Show dialogs |
| **IDEConfig** | Store constants | - |

---

## 🎨 Styling Guide

**Colors** (in `IDEConfig`)
```java
EDITOR_BG_COLOR = "#ffffff"      // White
EDITOR_TEXT_COLOR = "#000000"    // Black
OUTPUT_BG_COLOR = "#2b2b2b"      // Dark gray
OUTPUT_TEXT_COLOR = "#00ff00"    // Green
ERROR_TEXT_COLOR = "#ff4444"     // Red
WARNING_TEXT_COLOR = "#ffaa00"   // Orange
```

**Fonts** (in `IDEConfig`)
```java
EDITOR_FONT = "Consolas"
EDITOR_FONT_SIZE = 14
OUTPUT_FONT = "Consolas"
OUTPUT_FONT_SIZE = 13
```

**Apply custom style**
```java
// In component constructor
setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
```

---

## 🚀 Next Steps (Phase 2)

### Syntax Highlighting
- Create `SyntaxHighlighter.java`
- Extend `EditorPanel` or create new `HighlightedEditorPanel`
- Use Lexer for token recognition

### Line Numbers
- Create `LineNumberFactory.java`
- Integrate with `EditorPanel`

### DAWAT Support (Input)
- Create `IOHandler` interface
- Implement `GUIIOHandler` for dialogs
- Modify `IDEController.runProgram()`

### Themes
- Add theme configs to `IDEConfig`
- Create `ThemeManager.java`
- Apply themes in components

---

## 📚 Documentation

- **This file**: Quick reference for common tasks
- **REFACTORING-SUMMARY.md**: Detailed refactoring report
- **ARCHITECTURE.md**: System architecture & diagrams

---

## ✅ Checklist for Changes

Before making changes:
- [ ] Identify correct file (see "Need to change..." section)
- [ ] Read class javadoc
- [ ] Check IDEConfig for related constants

After making changes:
- [ ] Run `.\gradlew clean build`
- [ ] Run `.\gradlew :app:runIDE`
- [ ] Test changed functionality
- [ ] Verify no regressions

---

**Last Updated**: November 8, 2025  
**Status**: Phase 1 Complete ✅
