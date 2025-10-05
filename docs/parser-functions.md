# Parser — Function Reference (placeholder)

Purpose: A function-level reference for the Parser implementation (to be added). This file is a placeholder that should include:

- Top-level call graph (parse() -> declaration/statement/expression handlers)
- Per-function signature, purpose, inputs/outputs, side-effects, and quick debug notes
- Common parsing failure modes and how to step through them
- How the parser uses tokens produced by the Lexer

Suggested content to populate here (examples):
- parse(): entry point, returns AST or error
- declaration(): handles MUGNA, variable declarations
- statement(): dispatches to IPAKITA, DAWAT, control flow (KUNG, PUNDOK)
- expression(): precedence climbing or recursive descent helpers
- match(), consume(), check(), advance() helpers

Links:
- Parser spec (TODO): ./parser-specification.md
- Lexer functions: ./lexer-functions.md

I can auto-generate the initial function skeletons by scanning `app/src/main/java/com/bisayapp/Parser.java` if you want; tell me if you'd like that.
