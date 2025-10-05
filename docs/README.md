# Bisaya++ Documentation

This folder contains technical documentation for the Bisaya++ interpreter. Use these documents as a quick reference while developing, debugging, or extending the language components.

## Current documents

- `lexer-specification.md` — Comprehensive technical specification for the Lexer component (architecture, token types, examples, error handling).
- `lexer-functions.md` — Function-level reference and debugging notes for `app/src/main/java/com/bisayapp/Lexer.java` (call-graph, per-function inputs/outputs, quick trace recipes).

## Planned documents (placeholders)

- `parser-specification.md` — (TODO) Syntax and grammar, AST structure, and parser behavior.
- `parser-functions.md` — (TODO) Function-level reference and debug notes for the Parser implementation.
- `interpreter-specification.md` — (TODO) Runtime semantics, environment, and evaluation rules.

## How to use

- Start with `lexer-specification.md` to understand the lexer's overall design and tokenization rules.
- Use `lexer-functions.md` when tracing a specific tokenization bug; it maps inputs to the functions you should inspect.
- When you add parser or interpreter docs, link them here to keep a single entry point.

## Quick links

- Lexer spec: ./lexer-specification.md
- Lexer functions: ./lexer-functions.md

## Next steps (recommended)

- Create `parser-specification.md` with grammar and sample parses.
- Add `README.md` links in project root if you want docs surfaced in the repository root.
- Optionally add a small `docs/index.html` or GitHub Pages site later for nicer browsing.

---

File: `docs/README.md` — central doc index for the Bisaya++ component documentation.
