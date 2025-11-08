# 🚀 Bisaya++ IDE - Quick Reference

## Starting the IDE

### Option 1: Double-click the batch file
```
launch-ide.bat
```

### Option 2: From command line
```
.\gradlew :app:runIDE
```

### Option 3: Using the JAR directly
```
java -jar app\build\libs\bisaya-ide-1.0.jar
```

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|:---------|:-------|
| `Ctrl+N` | New file |
| `Ctrl+O` | Open file |
| `Ctrl+S` | Save file |
| `Ctrl+Shift+S` | Save As... |
| `Ctrl+R` | **Run program** |
| `Ctrl+L` | Clear output |

---

## 🎨 UI Elements

### 📝 Code Editor (Top)
- Write your Bisaya++ code here
- Shows line and column position in status bar
- Supports syntax from all increments (1-5)

### 📤 Output Area (Bottom)
- **Green text** = Successful output
- **Red text** = Errors
- **Yellow text** = Warnings
- Dark background for better readability

### 📊 Status Bar (Bottom)
Shows:
- Current filename (if saved)
- Line and column position
- Total lines
- Character count

---

## 📂 Sample Programs

Sample programs are located in:
```
app/samples/
```

Quick samples to try:
- `hello.bpp` - Simple hello world
- `increment3_simple_if.bpp` - Conditional example
- `increment4_basic_loop.bpp` - For loop example
- `increment5_basic_while.bpp` - While loop example

---

## ✅ Supported Features

### All Increments (1-5):
- ✅ Variable declaration (`MUGNA`)
- ✅ Data types: NUMERO, LETRA, TINUOD, TIPIK
- ✅ Arithmetic operators: +, -, *, /, %
- ✅ Comparison: >, <, >=, <=, ==, <>
- ✅ Logical: UG (AND), O (OR), DILI (NOT)
- ✅ String concatenation: `&`
- ✅ Newline: `$`
- ✅ Escape sequences: `[...]`
- ✅ Comments: `@@`
- ✅ Output: `IPAKITA`
- ✅ Input: `DAWAT` (CLI only)
- ✅ Conditionals: `KUNG`, `KUNG DILI`, `KUNG WALA`
- ✅ Loops: `ALANG SA` (for), `SAMTANG` (while)
- ✅ Nested structures

---

## 🔧 Troubleshooting

### IDE won't start
1. Check Java 21 is installed: `java -version`
2. Try rebuilding: `.\gradlew clean build ideJar`
3. Use JAR launcher: `launch-ide-jar.bat`

### Code won't run
1. Check for syntax errors (shown in red in output)
2. Ensure code is between `SUGOD` and `KATAPUSAN`
3. Check error message for line number

### Can't save file
1. Ensure you have write permissions
2. Check file path doesn't have special characters
3. Try "Save As" to different location

---

## 💡 Tips

1. **Use shortcuts** - Ctrl+R to run is much faster
2. **Check status bar** - It shows your position in the code
3. **Save often** - Use Ctrl+S frequently
4. **Clear output** - Use Ctrl+L before each run for clarity
5. **Sample programs** - Use File > Open to browse samples

---

## 📝 Example Program

```bisaya
@@ This is a comment
SUGOD
    MUGNA NUMERO x=5, y=10
    MUGNA LETRA msg="Result: "
    
    @@ Output
    IPAKITA: msg & (x + y) & $
    IPAKITA: "x * y = " & (x * y)
KATAPUSAN
```

**Output:**
```
Result: 15
x * y = 50
```

---

## 🎯 Common Patterns

### Conditional
```bisaya
KUNG (x > 5)
PUNDOK{
    IPAKITA: "Greater than 5"
}
```

### For Loop
```bisaya
ALANG SA (i=1, i<=5, i++)
PUNDOK{
    IPAKITA: i & " "
}
```

### While Loop
```bisaya
SAMTANG (x < 10)
PUNDOK{
    x = x + 1
    IPAKITA: x & $
}
```

---

**Version**: 1.0  
**Last Updated**: November 8, 2025  
**Phase**: 1 (MVP) - Complete ✅
