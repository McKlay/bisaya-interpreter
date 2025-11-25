# Bisaya++ IDE - Windows MSI Distribution Guide

## Final Solution: Working MSI Installer

**Date:** November 10, 2025  
**Status:** **WORKING** - Successfully packaged and tested

---

## Quick Summary

Successfully created a Windows MSI installer for Bisaya++ IDE using:
- **jpackage** with JavaFX jmods from Gluon
- **WiX Toolset** for professional Windows installer
- **Fat JAR** with all dependencies + JavaFX modules in custom JRE

**Final MSI:**
- File: `app/build/distribution/BisayaIDE-1.0.0.msi`
- Size: ~40 MB
- Includes: Bundled Java 21 JRE + JavaFX modules + Bisaya++ IDE

---

## The Problem We Solved

**Issue:** BisayaIDE MSI installed but wouldn't launch
**Error:** `JavaFX runtime components are missing, and are required to run this application`

**Root Cause:**
- jpackage bundles a custom JRE without JavaFX modules
- JavaFX from Maven Central (JARs) are not modular
- JavaFX requires modules (`javafx.base`, `javafx.graphics`, `javafx.controls`, `javafx.fxml`)
- Native DLLs must be loaded as modules, not from embedded JARs

---

## Failed Approaches (What Didn't Work)

### ❌ Attempt 1: Fat JAR with Manual Classpath
- jpackage split `-cp` across multiple lines in config file
- Result: `ClassNotFoundException`

### ❌ Attempt 2: Separate JARs in libs/ Subdirectory
- jpackage doesn't auto-detect subdirectories
- Result: `ClassNotFoundException`

### ❌ Attempt 3: Separate JARs in Root Directory
- JavaFX JARs have DLLs inside but runtime lacks JavaFX modules
- Result: `JavaFX runtime components are missing`

### ❌ Attempt 4: Fat JAR Only
- Bundled JRE still lacked JavaFX modules
- Result: `JavaFX runtime components are missing`

### ❌ Attempt 5: Badass-Jlink Gradle Plugin
- Circular dependency with module compilation
- Not suitable for non-modular applications
- See: `OPTION5-ASSESSMENT.md`

---

## Working Solution: JavaFX jmods

**What We Did:**

1. **Downloaded JavaFX jmods** (not Maven JARs)
   - Source: https://gluonhq.com/products/javafx/
   - Version: JavaFX 21.0.9 (Windows x64)
   - Extracted to: `C:\javafx-jmods-21.0.9`
   - Contents: `javafx.base.jmod`, `javafx.controls.jmod`, `javafx.fxml.jmod`, `javafx.graphics.jmod`, etc.

2. **Updated build.gradle**
   ```gradle
   task createMSI(type: Exec) {
       def javafxModsPath = 'C:\\javafx-jmods-21.0.9'
       
       commandLine 'jpackage',
           '--type', 'msi',
           '--input', "${buildDir}/jpackage-input",
           '--main-jar', 'bisaya-ide-1.0.jar',
           '--main-class', 'com.bisayapp.ui.BisayaIDE',
           '--module-path', javafxModsPath,           // ← Key: Add jmods to module path
           '--add-modules', 'javafx.controls,javafx.fxml', // ← Add JavaFX modules to JRE
           // ... other options
   }
   ```

3. **Built MSI**
   ```powershell
   .\gradlew createMSI
   ```

**Result:**
- ✅ Bundled JRE includes `javafx.*` modules
- ✅ JavaFX native DLLs in `runtime\bin\`
- ✅ Application launches successfully
- ✅ No Java installation required for users

---

## Additional Fixes

### Fixed: Examples Menu Not Loading

**Issue:** Examples menu items didn't load sample programs in packaged MSI

**Cause:** Code tried to load from filesystem (`app/samples/`), but files are inside JAR

**Solution:**
1. Copy samples to JAR resources in `build.gradle`:
   ```gradle
   processResources {
       from('samples') {
           into 'samples'
       }
   }
   ```

2. Load from classpath in `IDEController.java`:
   ```java
   String resourcePath = "/samples/" + filename;
   InputStream inputStream = getClass().getResourceAsStream(resourcePath);
   if (inputStream != null) {
       String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
       // ... load content
   }
   ```

**Result:** ✅ Examples menu now works in packaged application

---

## Build Instructions

### Prerequisites

1. **Java 21 JDK** - For building the application
2. **WiX Toolset 3.11+** - For creating MSI installer
   - Download: https://wixtoolset.org/releases/
   - Install to: `C:\Program Files (x86)\WiX Toolset v3.11`
   - Add to PATH: `C:\Program Files (x86)\WiX Toolset v3.11\bin`
3. **JavaFX jmods 21** - For bundling JavaFX modules
   - Download: https://gluonhq.com/products/javafx/
   - Extract to: `C:\javafx-jmods-21.0.9`

### Build Commands

```powershell
# Add WiX to PATH for this session
$env:PATH += ";C:\Program Files (x86)\WiX Toolset v3.11\bin"

# Build MSI installer
.\gradlew createMSI

# Output
# app/build/distribution/BisayaIDE-1.0.0.msi (~40 MB)
```

### Gradle Tasks

```bash
# Run IDE from source (development)
.\gradlew runIDE

# Build fat JAR (CLI + IDE)
.\gradlew ideJar
# Output: app/build/libs/bisaya-ide-1.0.jar

# Build CLI-only JAR
.\gradlew cliJar
# Output: app/build/libs/bisaya-cli-1.0.jar

# Create MSI installer
.\gradlew createMSI
# Output: app/build/distribution/BisayaIDE-1.0.0.msi
```

---

## Distribution

### For End Users

**Download:** `BisayaIDE-1.0.0.msi` (~40 MB)

**Installation:**
1. Double-click the MSI file
2. Follow installation wizard
3. Choose installation directory (default: `C:\Program Files\BisayaIDE`)
4. Complete installation

**Launch:**
- Start Menu → "BisayaIDE"
- Desktop shortcut (if created during install)
- Or run: `C:\Program Files\BisayaIDE\BisayaIDE.exe`

**Uninstall:**
- Settings → Apps → BisayaIDE → Uninstall

**System Requirements:**
- Windows 10 or later (64-bit)
- ~100 MB disk space
- No Java installation required (bundled)

---

## What's Included in the MSI

### Application Files
```
C:\Program Files\BisayaIDE\
├── BisayaIDE.exe              ← Launcher executable
├── app/
│   ├── bisaya-ide-1.0.jar     ← Fat JAR (10 MB) with Bisaya++ code + dependencies
│   └── BisayaIDE.cfg          ← Launcher configuration
└── runtime/                    ← Bundled Java 21 JRE
    ├── bin/
    │   ├── java.exe
    │   ├── javaw.exe
    │   ├── glass.dll          ← JavaFX native libraries
    │   ├── prism_*.dll        ← JavaFX rendering
    │   └── javafx_*.dll       ← JavaFX natives
    ├── lib/
    │   └── modules            ← Java + JavaFX modules
    └── release                ← Module list including javafx.*
```

### Bundled Resources
- ✅ Java 21 JRE (custom runtime with only needed modules)
- ✅ JavaFX 21 modules (`javafx.base`, `javafx.controls`, `javafx.fxml`, `javafx.graphics`)
- ✅ Bisaya++ Interpreter and IDE
- ✅ RichTextFX for syntax highlighting
- ✅ Sample programs (accessible from Examples menu)

---

## Technical Details

### JavaFX Module Loading

**Configuration in `BisayaIDE.cfg`:**
```ini
[Application]
app.classpath=$APPDIR\bisaya-ide-1.0.jar
app.mainclass=com.bisayapp.ui.BisayaIDE

[JavaOptions]
java-options=-Djpackage.app-version=1.0.0
```

**Bundled Modules (in `runtime/release`):**
```
MODULES="java.base java.desktop java.logging ... 
         javafx.base javafx.controls javafx.fxml javafx.graphics
         jdk.crypto.ec jdk.zipfs ..."
```

**Key Points:**
- JavaFX modules loaded from custom JRE, not from classpath
- Native DLLs in `runtime/bin/` loaded automatically
- No `--module-path` or `--add-modules` needed at runtime (baked into JRE)

---

## Lessons Learned

### What Works with jpackage

✅ **DO:**
- Use JavaFX jmods from Gluon (not Maven JARs)
- Include jmods with `--module-path` and `--add-modules`
- Bundle resources in JAR and load from classpath
- Use fat JAR for non-modular dependencies (RichTextFX, etc.)

❌ **DON'T:**
- Use Maven JavaFX JARs for jpackage (not modular)
- Try to load JavaFX from embedded JARs (won't work)
- Assume jpackage auto-detects JavaFX
- Put resources only in filesystem (won't be in JAR)

### Why JavaFX is Special

1. **Requires Native Libraries:** glass.dll, prism*.dll, javafx_*.dll
2. **Must be Modules:** Can't load from regular JARs
3. **Platform-Specific:** Different natives for Windows/Linux/Mac
4. **Module System:** Needs proper JPMS modules in runtime

### jpackage Behavior

- Creates minimal custom JRE with only specified modules
- Doesn't include JavaFX unless explicitly added with `--add-modules`
- Can't use non-modular JARs as modules
- Requires jmods (not regular JARs) for custom modules

---

## Future Improvements

### Potential Enhancements

1. **Code Signing** - Sign the MSI for Windows SmartScreen
2. **Auto-Update** - Implement update checker in IDE
3. **Cross-Platform** - Create DEB (Linux) and DMG (macOS) packages
4. **Smaller Size** - Use jlink to create minimal JRE (exclude unused modules)
5. **Custom Icons** - Add custom application icon
6. **File Associations** - Register .bpp file extension

### Known Limitations

- **Windows Only** - MSI installer only for Windows
  - For Linux: Create DEB/RPM with jpackage
  - For macOS: Create DMG/PKG with jpackage
- **Size** - 40 MB installer (could be smaller with custom jlink runtime)
- **JavaFX jmods** - Must be downloaded separately (not in Maven)

---

## References

- **JavaFX jmods:** https://gluonhq.com/products/javafx/
- **jpackage docs:** https://docs.oracle.com/en/java/javase/21/jpackage/
- **WiX Toolset:** https://wixtoolset.org/
- **RichTextFX:** https://github.com/FXMisc/RichTextFX

---

## Summary

**Problem:** JavaFX applications can't be packaged with standard jpackage + Maven JARs

**Solution:** Use JavaFX jmods with `--module-path` and `--add-modules`

**Result:** ✅ Self-contained Windows MSI installer with bundled JRE + JavaFX

**MSI Details:**
- **File:** `app/build/distribution/BisayaIDE-1.0.0.msi`
- **Size:** ~40 MB
- **Tested:** ✅ Working on Windows 10/11
- **Features:** Full IDE with syntax highlighting, examples, and interpreter

---

**Build Date:** November 10, 2025  
**Version:** 1.0.0  
**Status:** Production Ready ✅
