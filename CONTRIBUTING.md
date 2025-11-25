# Contributing to Bisaya++

## Setup

```bash
# Clone
git clone <repo-url>
cd bisaya-interpreter

# Build
.\gradlew build

# Run IDE
.\gradlew runIDE

# Run tests
.\gradlew test
```

## Project Structure

```
app/src/main/java/com/bisayapp/
├── Bisaya.java          # CLI entry
├── Lexer.java           # Tokenization
├── Parser.java          # Syntax analysis
├── Interpreter.java     # Execution
└── ui/BisayaIDE.java   # GUI IDE
```

## Development Workflow

1. **Fork** the repository
2. **Create branch:** `git checkout -b feature/your-feature`
3. **Make changes** and add tests
4. **Test:** `.\gradlew test`
5. **Commit:** Clear, descriptive messages
6. **Push** and create Pull Request

## Building Distributions

```bash
# CLI JAR (works, ~10MB, requires Java 21+)
.\gradlew cliJar
# Output: app/build/libs/bisaya-cli-1.0.0-fat.jar
```

**Note:** IDE cannot be packaged as standalone JAR due to JavaFX. Users run from source with `.\gradlew runIDE`.

See `/docs/packaging/BUILD-GUIDE.md` for details.

## Testing

- Write tests in `app/src/test/java/`
- Follow existing test patterns (Increment1Test, etc.)
- Run full suite: `.\gradlew test`
- Run specific: `.\gradlew test --tests ClassName`

## Code Style

- Follow existing code conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and concise

## Documentation

- **User docs:** `/release/README.md`
- **Developer docs:** `/docs/`
- **Build/packaging:** `/docs/packaging/`

Update relevant docs when adding features.

## Questions?

- **Language spec:** `/docs/README.md`
- **Architecture:** `/docs/*-specification.md`
- **Build system:** `/docs/packaging/BUILD-GUIDE.md`
