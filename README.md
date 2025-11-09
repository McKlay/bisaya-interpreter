# Bisaya++ Programming Language

A Cebuano-based educational programming language with IDE.

## Quick Start

**IDE:** Clone and run with Gradle
```bash
git clone https://github.com/McKlay/bisaya-interpreter.git
cd bisaya-interpreter
.\gradlew runIDE
```

**CLI:** Download JAR from [releases](../../releases)
```bash
java -jar bisaya-cli-1.0.0-fat.jar program.bpp
```

**Requirements:** JDK 21+ from https://adoptium.net/  
**Documentation:** `/release/README.md` (users) | `/docs/` (developers)

## Example

```bisaya
SUGOD
    MUGNA NUMERO x=10, y=20
    IPAKITA: "Sum: " & (x + y)
KATAPUSAN
```

## Features

- Cebuano keywords (SUGOD, MUGNA, KUNG, ALANG SA, SAMTANG, etc.)
- Variables, operators, conditionals, loops
- Interactive input/output
- IDE with syntax highlighting

## Language Quick Reference

**Data Types:** NUMERO (int), LETRA (char), TINUOD (bool), TIPIK (float)  
**I/O:** IPAKITA (output), DAWAT (input)  
**Control:** KUNG (if), ALANG SA (for), SAMTANG (while)

Full spec: `/docs/README.md`

## Requirements

**Users:** Java 21+  
**Developers:** JDK 21 + Gradle 8.5 (included)
