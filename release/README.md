# Bisaya++ IDE - User Guide

## Installation

### Option 1: Windows MSI Installer (Easiest)
**Download:** `BisayaIDE-1.0.0.msi` from releases (~40 MB)

1. Download and run the MSI file
2. Follow installation wizard
3. Launch from Start Menu or Desktop shortcut

**No Java installation required** - JRE is bundled!

### Option 2: Run from Source
**Requirements:** JDK 21+ from https://adoptium.net/

```bash
git clone https://github.com/McKlay/bisaya-interpreter.git
cd bisaya-interpreter
.\gradlew runIDE
```

### Option 3: CLI Only
**Download:** `bisaya-cli-1.0.0-fat.jar` from releases

**Requirements:** Java 21+ from https://adoptium.net/

```bash
java -jar bisaya-cli-1.0.0-fat.jar program.bpp
```

---

## Your First Program

1. The IDE opens with a blank editor
2. Type this code:
   ```bisaya
   SUGOD
       IPAKITA: "Hello, Bisaya++!"
   KATAPUSAN
   ```
3. Press **Ctrl+R** or click **Run**
4. See output: `Hello, Bisaya++!`

---

## Sample Programs

The `samples/` folder includes:

- **hello.bpp** - Basic output
- **arithmetic.bpp** - Math operations  
- **conditionals.bpp** - If/else statements
- **loops.bpp** - For and while loops
- **guessing_game.bpp** - Interactive game with input

Open any sample: **File → Open** or **Ctrl+O**

---

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `Ctrl+N` | New file |
| `Ctrl+O` | Open file |
| `Ctrl+S` | Save file |
| `Ctrl+R` | Run program |
| `Ctrl+L` | Clear output |

---

## Language Quick Reference

### Variables
```bisaya
MUGNA NUMERO x=10        @@ Integer
MUGNA LETRA c='A'        @@ Character
MUGNA TINUOD b="OO"      @@ Boolean (OO=true, DILI=false)
MUGNA TIPIK pi=3.14      @@ Decimal
```

### Input/Output
```bisaya
IPAKITA: "Hello"         @@ Print
IPAKITA: x & " = " & y   @@ Concatenate with &
IPAKITA: "Line" & $      @@ $ = newline

DAWAT: x                 @@ Input (dialog appears)
DAWAT: x, y              @@ Multiple inputs (comma-separated)
```

### Conditionals
```bisaya
KUNG (x > 10)
PUNDOK{
    IPAKITA: "Greater than 10"
}
KUNG WALA
PUNDOK{
    IPAKITA: "Not greater than 10"
}
```

### Loops
```bisaya
@@ For loop
ALANG SA (i=1, i<=5, i++)
PUNDOK{
    IPAKITA: i & $
}

@@ While loop
SAMTANG (x < 10)
PUNDOK{
    x++
}
```

### Operators
- Arithmetic: `+` `-` `*` `/` `%`
- Comparison: `>` `<` `>=` `<=` `==` `<>`
- Logical: `UG` (AND), `O` (OR), `DILI` (NOT)
- Unary: `++` `--` `+` `-`

---

## Troubleshooting

**IDE won't start?**
- Check Java version: `java -version` (must be 21+)
- Install Java from https://adoptium.net/
- Use launcher script, don't double-click JAR

**Can't input values with DAWAT?**
- A dialog box should appear automatically
- Enter values separated by commas if multiple
- Example: for `DAWAT: x, y` enter `10, 20`

**Errors in red?**
- Check syntax (keywords must be UPPERCASE)
- Ensure code is between `SUGOD` and `KATAPUSAN`
- Check for typos in variable names

---

## Complete Language Specification

See `docs/` folder for detailed language specification including:
- Lexer specification
- Parser specification  
- Interpreter specification
- Increment documentation (features by version)

---

**Version:** 1.0.0  
**License:** See LICENSE file  
**Support:** GitHub Issues
