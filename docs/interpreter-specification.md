# Interpreter Specification (placeholder)

Purpose: Describe runtime semantics, environment model, value types, and evaluation rules for Bisaya++.

Status: TODO — placeholder sections to be completed.

Suggested sections to add:
- Runtime environment (Environment.java responsibilities)
- Value model (Value.java): types and internal representation (NUMERO, TIPIK, LETRA, TINUOD)
- Expression evaluation rules (binary, unary, grouping)
- Statement execution semantics (variable declaration, assignment, IPAKITA, DAWAT)
- Control flow semantics (KUNG / KUNG-KUNG / PUNDOK blocks, loops)
- Error handling at runtime (type errors, undefined variables)
- I/O behavior and newline/escape handling
- Examples: sample program, expected runtime trace

Links:
- Parser spec: ./parser-specification.md
- Lexer spec: ./lexer-specification.md

Next steps: I can extract class responsibilities from `app/src/main/java/com/bisayapp` and populate the initial spec automatically if you want.
