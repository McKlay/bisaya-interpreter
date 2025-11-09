# JavaFX jpackage Issue - Summary & Potential Fix

## Problem Statement

Cannot create standalone distributable package for Bisaya++ IDE due to JavaFX dependency issues.

---

## What We Tried

### Attempt 1: Fat JAR with JavaFX
**Command:** `.\gradlew ideJar`  
**Result:** ❌ Failed  
**Error:** `Error: JavaFX runtime components are missing, and are required to run this application`

**Why it failed:**  
- Fat JAR bundles JavaFX classes but NOT native libraries
- JavaFX requires platform-specific native DLLs (.dll on Windows, .so on Linux, .dylib on Mac)
- These native libraries cannot be bundled inside a JAR

### Attempt 2: Portable Package with Launcher Scripts
**Command:** `.\gradlew createPortablePackage`  
**Result:** ❌ Failed  
**Error:** Same JavaFX missing error when running `bisaya-ide.bat`

**What we created:**
- Fat JAR with all dependencies
- Launcher scripts (.bat/.sh) with Java version checking
- Attempted to use `--module-path` and `--add-modules` flags

**Why it failed:**  
- Launcher scripts tried: `java --module-path "jar" --add-modules javafx.controls,javafx.fxml -jar app.jar`
- This doesn't work because JAR is not a valid module path
- JavaFX modules need to be separate JARs or in a proper runtime image

### Attempt 3: jpackage with app-image
**Command:** `.\gradlew jpackage` (modified to create portable runtime)  
**Result:** ❌ Failed  
**Output:** Created `BisayaIDE.exe` with bundled JRE (170MB uncompressed)  
**Error:** `Error: JavaFX runtime components are missing`

**What happened:**
```
app/build/jpackage/BisayaIDE/
├── BisayaIDE.exe          ← Launcher executable
├── runtime/               ← Bundled Java 21 JRE (but NO JavaFX modules)
│   ├── bin/
│   ├── lib/
│   └── release
└── app/
    ├── bisaya-ide-1.0.0-fat.jar
    └── bisaya-cli-1.0.0-fat.jar
```

**Why it failed:**  
- jpackage bundled Java 21 runtime but didn't include JavaFX modules
- The bundled JRE only has standard Java modules (java.base, java.desktop, etc.)
- Missing: `javafx.controls`, `javafx.base`, `javafx.graphics`, `javafx.fxml`

**Verification:**
```powershell
PS> Get-Content app\build\jpackage\BisayaIDE\runtime\release
MODULES="java.base java.compiler java.datatransfer ... jdk.xml.dom"
# No javafx.* modules listed!
```

### Attempt 4: jlink plugin with addExtraDependencies
**Command:** Added `org.beryx.jlink` plugin  
**Config:** `addExtraDependencies('javafx.graphics', 'javafx.controls', 'javafx.base', 'javafx.fxml')`  
**Result:** ❌ Failed  
**Error:** 
```
module-info.java:21: error: module not found: javafx.graphics
module-info.java:22: error: module not found: javafx.controls
module-info.java:23: error: module not found: javafx.base
```

**Why it failed:**  
- jlink requires all dependencies to be proper Java modules
- JavaFX from Maven Central may not be recognized as modules by jlink
- Complex module path configuration required

---

## Current Working Solution

**IDE:** Run from source with Gradle
```bash
.\gradlew runIDE
```

**CLI:** Distribute as Fat JAR (no JavaFX, works perfectly)
```bash
.\gradlew cliJar
# Output: app/build/libs/bisaya-cli-1.0.0-fat.jar
# Usage: java -jar bisaya-cli-1.0.0-fat.jar program.bpp
```

---

## Potential Fixes to Explore

### Option 1: Manual jlink with JavaFX jmods
**Complexity:** Medium  
**Requires:** JavaFX jmods (not regular JARs)

**Steps:**
1. Download JavaFX jmods from https://gluonhq.com/products/javafx/
2. Create custom runtime with jlink:
   ```bash
   jlink --module-path "%JAVA_HOME%/jmods;path/to/javafx-jmods" \
         --add-modules javafx.controls,javafx.fxml,java.base \
         --output bisaya-runtime \
         --strip-debug --compress 2 --no-header-files --no-man-pages
   ```
3. Copy application JAR to `bisaya-runtime/`
4. Create launcher script that uses bundled runtime
5. ZIP the entire `bisaya-runtime/` folder

**Pros:** Self-contained, no Java needed  
**Cons:** Manual process, platform-specific builds, large size (~150-200MB)

### Option 2: Use jpackage with proper input directory
**Complexity:** Medium  
**Requires:** Extract JavaFX JARs separately

**Steps:**
1. Extract JavaFX JARs from Maven dependencies
2. Place in `input/` directory alongside application JAR
3. Run jpackage:
   ```bash
   jpackage --input input/ \
            --name BisayaIDE \
            --main-jar bisaya-ide-1.0.0.jar \
            --main-class com.bisayapp.ui.BisayaIDE \
            --type app-image \
            --module-path "input/javafx-controls-21.jar;input/javafx-fxml-21.jar;..." \
            --add-modules javafx.controls,javafx.fxml
   ```

**Pros:** Uses official jpackage tool  
**Cons:** Requires extracting and managing JavaFX JARs separately

### Option 3: GraalVM Native Image
**Complexity:** High  
**Requires:** GraalVM, native-image tool, JavaFX native-image metadata

**Steps:**
1. Install GraalVM
2. Install native-image: `gu install native-image`
3. Add JavaFX native-image configuration
4. Build native executable:
   ```bash
   native-image --no-fallback \
                -H:+ReportExceptionStackTraces \
                --initialize-at-build-time \
                -jar bisaya-ide-1.0.0-fat.jar \
                bisaya-ide
   ```

**Pros:** Single executable, fast startup, small size  
**Cons:** Very complex, JavaFX support limited, requires extensive configuration

### Option 4: Install WiX Toolset for Windows MSI
**Complexity:** Low  
**Platform:** Windows only

**Steps:**
1. Download and install WiX Toolset 3.11+: https://wixtoolset.org
2. Add WiX to PATH
3. Run: `.\gradlew jpackage --type msi`

**Current error when attempted:**
```
[21:43:52.035] Can not find WiX tools (light.exe, candle.exe)
[21:43:52.035] Download WiX 3.0 or later from https://wixtoolset.org and add it to the PATH.
Error: Invalid or unsupported type: [msi]
```

**Pros:** Creates professional Windows installer  
**Cons:** Windows-only, requires WiX installation, need separate builds for Linux/Mac

### Option 5: Badass-Jlink Gradle Plugin (Recommended)
**Complexity:** Medium  
**Requires:** Proper plugin configuration

**Steps:**
1. Keep `org.beryx.jlink` plugin (version 3.0.1)
2. Fix configuration in build.gradle:
   ```gradle
   jlink {
       forceMerge('javafx')
       
       options = [
           '--strip-debug',
           '--compress', '2',
           '--no-header-files',
           '--no-man-pages'
       ]
       
       launcher {
           name = 'bisaya-ide'
       }
       
       jpackage {
           installerType = 'msi'  // or 'exe', 'deb', 'dmg'
           installerOptions = ['--win-dir-chooser', '--win-menu', '--win-shortcut']
       }
   }
   ```
3. Ensure JavaFX modules are properly detected
4. Run: `.\gradlew jpackage`

**Pros:** Gradle integration, handles JavaFX properly  
**Cons:** Requires correct configuration, may need troubleshooting

---

## Recommended Next Steps

### For Quick Testing:
1. Try **Option 4** (WiX Toolset) if on Windows - simplest solution
2. Install WiX from https://wixtoolset.org/releases/
3. Add to PATH: `C:\Program Files (x86)\WiX Toolset v3.11\bin`
4. Run: `.\gradlew jpackage`

### For Production:
1. Try **Option 5** (Badass-Jlink) - most maintainable
2. Review plugin documentation: https://badass-jlink-plugin.beryx.org/
3. Study JavaFX examples in the plugin repo
4. Configure properly for each target platform

### For Cross-Platform:
1. Use **Option 1** (Manual jlink) with CI/CD
2. Set up GitHub Actions to build on Windows/Linux/Mac
3. Upload platform-specific packages to releases

---

## References

- **JavaFX jmods:** https://gluonhq.com/products/javafx/
- **jpackage docs:** https://docs.oracle.com/en/java/javase/21/jpackage/
- **Badass-Jlink plugin:** https://badass-jlink-plugin.beryx.org/
- **WiX Toolset:** https://wixtoolset.org/
- **GraalVM Native Image:** https://www.graalvm.org/

---

## Build Configuration Context

**Current setup:**
- Gradle 8.5
- Java 21 (OpenJDK)
- JavaFX 21 (from Maven Central via org.openjfx.javafxplugin)
- Application plugin
- jlink plugin (removed due to errors)

**Dependencies:**
```gradle
implementation 'org.openjfx:javafx-controls:21'
implementation 'org.openjfx:javafx-fxml:21'
implementation 'org.fxmisc.richtext:richtextfx:0.11.2'
```

**What works:**
- ✅ `.\gradlew runIDE` - runs perfectly with JavaFX
- ✅ `.\gradlew cliJar` - creates working CLI Fat JAR (no JavaFX)
- ❌ Any attempt to package IDE as standalone

---

**Date:** November 9, 2025  
**Status:** Unresolved - IDE runs from source only  
**Priority:** Medium (users can clone and run, but not ideal for distribution)
