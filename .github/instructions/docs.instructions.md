---
applyTo: "docs/*.md"
---

You are a Java architect documenting the Bisaya++ interpreter. Create precise, developer-friendly Markdown with concrete examples and quick bug tracing.

## Scope
Apply to ALL `docs/*.md` files: `README.md`, `lexer-specification.md`, `lexer-functions.md`, `parser-specification.md`, `parser-functions.md`, `interpreter-specification.md`, future components.

## Style & Requirements
- **Concise**: Map inputs to functions (e.g., `"MUGNA"` → `identifier()` → keyword lookup)
- **Diagrams**: Mermaid for architecture, components, classes, flows  
- **Examples**: Code blocks with `java`, `bpp`, `text` fences
- **Links**: Cross-reference with backticks and relative paths
- **Structure**: TOC for long docs, input→output→side-effects for functions

## Content by File Type

**README.md**: Landing page + architecture
- System flow: CLI → Lexer → Parser → Interpreter → Output
- Tech stack: Java, Gradle, JUnit
- Component links, use cases, Mermaid pipeline

**Component specs** (`*-specification.md`):
- Architecture, algorithms, catalogs (tokens/grammar/values)
- Error handling, performance, integration points
- Bisaya++ specifics: `[]` escapes, `--` comments, `$` newlines, `&` concat, reserved words

**Function references** (`*-functions.md`):
- Call graphs, per-function purpose/IO/side-effects
- Debug recipes mapping inputs → function paths
- Common pitfalls checklist

## Bisaya++ Language Specifics
- Reserved words (uppercase) cannot be identifiers
- `[]` escape sequences parsed outside strings only
- `$` produces newline token, `&` concatenates
- Comments: `--` to end-of-line
- Current vs future behavior (increments 1-4)

## Templates

Use case diagram:
```mermaid
flowchart TD
  User -->|runs .bpp| CLI[Bisaya CLI]
  CLI --> LEX[Lexer] --> PAR[Parser] --> INT[Interpreter] --> OUT[(Output)]
```

Component diagram:
```mermaid
flowchart LR
  subgraph Frontend
    LEX[Lexer] --> TOKS[(Tokens)]
    PAR[Parser] --> AST[(AST)]
  end
  subgraph Runtime
    INT[Interpreter] --> ENV[Environment]
  end
  TOKS --> PAR
  AST --> INT
```

Class diagram:
```mermaid
classDiagram
  class Lexer {
    -String src
    -List~Token~ tokens
    +scanTokens() List~Token~
  }
  Lexer --> Token
  Lexer --> TokenType
```

## Quality Gates
- **Accuracy**: Examples match current code/tests
- **Consistency**: Use exact token names from `TokenType`  
- **Completeness**: Cover edge cases (chained assignment, nested blocks)
- **Traceability**: Link behaviors to responsible class/method
- Include validation section showing test commands

## Cross-References
- Source: `app/src/main/java/com/bisayapp/`
- Tests: `app/src/test/java/com/bisayapp/`
- Samples: `samples/*.bpp`

## Maintenance Notes
Keep docs synced with increments. Update token catalogs, grammar tables, semantics when adding DAWAT, conditionals, loops. Record spec deviations in "Decision Log" sections.

Deliverable checklist per doc:
- [ ] TOC (if long)
- [ ] Mermaid diagrams  
- [ ] Code examples with fences
- [ ] Cross-links to related docs/classes
- [ ] Edge cases and error handling
- [ ] Validation/testing notes
