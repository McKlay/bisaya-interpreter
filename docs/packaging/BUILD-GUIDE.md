# Bisaya++ Build & Packaging Guide

Developer guide for building and distributing Bisaya++ IDE.

---

## Build System Overview

**Technology:** Gradle 8.5 + JavaFX 21  
**Build file:** `app/build.gradle`  
**Java requirement:** JDK 21+

---

## Development Commands

```bash
# Run IDE (recommended for development and users)
.\gradlew runIDE

# Run CLI
.\gradlew run

# Run tests
.\gradlew test

# Build CLI JAR (for distribution)
.\gradlew cliJar
```

---

## Distribution Options

### 1. Windows MSI Installer (✅ NOW AVAILABLE!)
**What:** Self-contained Windows installer with bundled Java 21 JRE + JavaFX  
**Size:** ~40 MB  
**Requires:** Nothing - JRE included  
**Best for:** End users, easiest installation

```bash
.\gradlew createMSI
```

Output: `app/build/distribution/BisayaIDE-1.0.0.msi`

**Features:**
- ✅ No Java installation required
- ✅ Professional installer with Start Menu shortcuts
- ✅ Double-click to install and run
- ✅ Full IDE with syntax highlighting

### 2. Run from Source (RECOMMENDED for IDE developers)
**Best for:** Developers, teachers, source-code distribution

Users clone the repository and run:
```bash
.\gradlew runIDE
```

**Why:** Most reliable for development and teaching.

### 3. CLI Fat JAR
**What:** Executable JAR for CLI interpreter  
**Size:** ~10 MB  
**Requires:** Java 21+  
**Best for:** Command-line, automation, scripting

```bash
.\gradlew cliJar
```

Output: `app/build/libs/bisaya-cli-1.0.0-fat.jar`

---

## Quick Build Commands

```bash
# Run IDE from source (development)
.\gradlew runIDE

# Build Windows MSI installer
.\gradlew createMSI
# Output: app\build\distribution\BisayaIDE-1.0.0.msi

# Build CLI JAR
.\gradlew cliJar

# Run tests
.\gradlew test
```

---

## Portable Package Contents

```
bisaya-ide-1.0.0-portable.zip
└── bisaya-ide-1.0.0/
    ├── bisaya-ide-1.0.0-fat.jar     # GUI executable
    ├── bisaya-cli-1.0.0-fat.jar     # CLI executable
    ├── bisaya-ide.bat               # Windows launcher (auto Java check)
    ├── bisaya-ide.sh                # Linux/Mac launcher (auto Java check)
    ├── README.txt                   # Auto-generated user instructions
    ├── samples/                     # Example .bpp programs
    └── docs/                        # Full documentation
```

---

## Launcher Script Features

Auto-generated scripts (`createIDELaunchers` task) provide:
- Java installation detection
- Java version verification (requires 21+)
- Helpful error messages with download links
- Proper JavaFX module arguments
- Cross-platform compatibility

---

## Version Management

**Update version:**
```gradle
// In app/build.gradle
version = '1.1.0'  // Change this
```

All generated files automatically use this version:
- `bisaya-ide-1.1.0-fat.jar`
- `bisaya-ide-1.1.0-portable.zip`
- Installer version metadata

---

## GitHub Release Workflow

```bash
# 1. Update version in app/build.gradle
# 2. Commit changes
git commit -am "Release v1.0.0"
git tag v1.0.0

# 3. Build CLI JAR
.\gradlew cliJar

# 4. Create GitHub release and upload:
# - bisaya-cli-1.0.0-fat.jar (~10 MB) - for CLI users
# - Installation instructions: "Clone repo and run .\gradlew runIDE"

# 5. Push
git push origin main --tags
```

---

## Release Notes Template

```markdown
# Bisaya++ v1.0.0

## Installation

### IDE (GUI)
```bash
git clone https://github.com/McKlay/bisaya-interpreter.git
cd bisaya-interpreter
.\gradlew runIDE
```

**Requirements:** JDK 21+ from https://adoptium.net/

### CLI (Command-line)
Download `bisaya-cli-1.0.0-fat.jar`

```bash
java -jar bisaya-cli-1.0.0-fat.jar program.bpp
```

**Requirements:** Java 21+ from https://adoptium.net/

## What's New
- [List features here]
```

---

## CI/CD Integration

Example GitHub Actions workflow:

```yaml
name: Release Build
on:
  push:
    tags: ['v*']

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build
        run: ./gradlew buildAllDistributions
      - name: Upload Release Assets
        uses: softprops/action-gh-release@v1
        with:
          files: |
            app/build/distributions/*.zip
            app/build/libs/*-fat.jar
```

---

## Build Configuration Details

### Key Gradle Tasks

Defined in `app/build.gradle`:

- **`ideJar`** - Creates fat JAR for GUI IDE
- **`cliJar`** - Creates fat JAR for CLI interpreter
- **`createIDELaunchers`** - Generates launcher scripts
- **`createPortablePackage`** - Builds complete portable package
- **`buildAllDistributions`** - Builds all except native installer
- **`jlink`** - Creates custom runtime image (for jpackage)
- **`jpackage`** - Creates native installer

### Dependencies

```gradle
dependencies {
    implementation 'org.openjfx:javafx-controls:21'
    implementation 'org.openjfx:javafx-fxml:21'
    implementation 'org.fxmisc.richtext:richtextfx:0.11.2'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
}
```

### Plugins

- `java` - Java compilation
- `application` - Application packaging
- `org.openjfx.javafxplugin` - JavaFX support
- `org.beryx.jlink` - jlink/jpackage integration

---

## Testing Builds

```bash
# Build portable package
.\gradlew createPortablePackage

# Extract and test
cd app/build/distributions
unzip bisaya-ide-1.0.0-portable.zip
cd bisaya-ide-1.0.0

# Windows
bisaya-ide.bat

# Linux/Mac
./bisaya-ide.sh
```

---

## Troubleshooting

**"JavaFX not found"**
- Check internet connection (downloads from Maven Central)
- Run: `.\gradlew clean build`

**"jpackage not found"**
- Requires JDK 14+ (you have JDK 21 ✓)
- Verify: `java -version`

**Build fails with module errors**
- Clean: `.\gradlew cleanAll`
- Rebuild: `.\gradlew build`

---

## File Size Reference

| Package | Size | Download Time* |
|---------|------|----------------|
| Fat JAR | ~10 MB | < 1 min |
| Portable ZIP | ~18 MB | 1-2 min |
| Native Installer | ~60-80 MB | 5-10 min |

*Assuming 10-20 Mbps connection

---

## Distribution Strategy

**For classroom/students:**  
→ Use portable package (easy to share, works everywhere)

**For GitHub releases:**  
→ Provide portable + fat JARs (choice for users)

**For production/app stores:**  
→ Use native installer (best UX, no Java needed)

---

## Related Documentation

- **User Guide:** `/release/README.md`
- **Quick Reference:** `/QUICK-START.md` (root)
- **Language Spec:** `/docs/README.md`
- **Build Config:** `/app/build.gradle`

---

**Last Updated:** November 9, 2025  
**Gradle Version:** 8.5  
**Java Version:** 21
