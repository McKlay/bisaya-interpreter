# Bisaya++ Distribution Guide

## Overview

This guide explains all available distribution options for the Bisaya++ IDE and how to build/deploy them for different use cases.

---

## Quick Start Commands

```bash
# Show all distribution options
.\gradlew distributionInfo

# Build portable package (recommended)
.\gradlew createPortablePackage

# Build all distributions (except native installer)
.\gradlew buildAllDistributions

# Build native installer (Windows/Linux/Mac)
.\gradlew jpackage
```

---

## Distribution Options

### Option 1: Fat JAR (Simple Distribution)

**Best for:** Developers, quick testing, users comfortable with Java

#### Build Commands
```bash
# Build IDE JAR
.\gradlew ideJar

# Build CLI JAR
.\gradlew cliJar

# Build both
.\gradlew ideJar cliJar
```

#### Output Location
```
app/build/libs/
├── bisaya-ide-1.0.0-fat.jar  (~15-20 MB)
└── bisaya-cli-1.0.0-fat.jar  (~15-20 MB)
```

#### Usage
```bash
# Run IDE
java -jar app/build/libs/bisaya-ide-1.0.0-fat.jar

# Run CLI
java -jar app/build/libs/bisaya-cli-1.0.0-fat.jar program.bpp
```

#### Requirements
- Java 21+ must be installed on user's system
- User must know how to use command line

#### Pros
✅ Smallest download size  
✅ Cross-platform (same file for Windows/Linux/Mac)  
✅ Easy to build  
✅ No installer needed

#### Cons
❌ Users must install Java separately  
❌ Requires command-line knowledge  
❌ Less professional appearance

---

### Option 2: Portable Package (Recommended)

**Best for:** Students, classroom distribution, GitHub releases

#### Build Command
```bash
.\gradlew createPortablePackage
```

#### Output Location
```
app/build/distributions/bisaya-ide-1.0.0-portable.zip (~20 MB)
```

#### Package Contents
```
bisaya-ide-1.0.0/
├── bisaya-ide-1.0.0-fat.jar      # IDE executable
├── bisaya-cli-1.0.0-fat.jar      # CLI executable
├── bisaya-ide.bat                # Windows launcher
├── bisaya-ide.sh                 # Linux/Mac launcher
├── README.txt                    # User instructions
├── samples/                      # Example programs
│   ├── hello.bpp
│   ├── arithmetic.bpp
│   ├── conditionals.bpp
│   ├── loops.bpp
│   └── guessing_game.bpp
└── docs/                         # Documentation
    ├── README.md
    ├── INCREMENT*.md
    └── ...
```

#### User Installation
1. Download ZIP file
2. Extract to any location
3. Double-click launcher script (`bisaya-ide.bat` or `bisaya-ide.sh`)

#### Requirements
- Java 21+ must be installed on user's system
- Launcher scripts check Java version automatically

#### Pros
✅ User-friendly (double-click to run)  
✅ Includes samples and documentation  
✅ Professional README with instructions  
✅ Automatic Java version checking  
✅ Works on Windows/Linux/Mac

#### Cons
❌ Users must install Java separately  
❌ Slightly larger download than bare JAR

---

### Option 3: Native Installer (Professional Distribution)

**Best for:** Production releases, non-technical users, app stores

#### Build Command
```bash
.\gradlew jpackage
```

#### Output by Platform

**Windows:**
```
app/build/jpackage/
└── Bisaya-IDE-Installer-1.0.0.msi  (~60-80 MB)
```

**Linux:**
```
app/build/jpackage/
└── bisaya-ide_1.0.0-1_amd64.deb  (~60-80 MB)
```

**Mac:**
```
app/build/jpackage/
└── BisayaIDE-1.0.0.dmg  (~60-80 MB)
```

#### Features
- **Self-contained**: Includes bundled Java runtime (JRE)
- **Professional installer**: Native OS installer with wizard
- **Desktop integration**: Start menu shortcuts, desktop icons
- **Uninstaller**: Proper uninstall through OS settings
- **Auto-updates ready**: Can be configured for update checking

#### User Installation

**Windows:**
1. Download `.msi` file
2. Double-click to run installer
3. Follow installation wizard
4. Launch from Start Menu or Desktop

**Linux:**
```bash
sudo dpkg -i bisaya-ide_1.0.0-1_amd64.deb
```

**Mac:**
1. Download `.dmg` file
2. Double-click to mount
3. Drag to Applications folder
4. Launch from Applications

#### Requirements
- **No Java required** - bundled in installer
- Must build on target OS (Windows installer on Windows, etc.)

#### Pros
✅ No Java installation required  
✅ Professional user experience  
✅ Native OS integration  
✅ Easiest for non-technical users  
✅ Can be distributed through app stores

#### Cons
❌ Largest download size (60-80 MB)  
❌ Must build separately for each OS  
❌ Requires jpackage tool (included in JDK 14+)  
❌ More complex build process

---

## Distribution Strategy Recommendations

### For Classroom/Academic Use
**Use:** Portable Package

**Workflow:**
1. Build: `.\gradlew createPortablePackage`
2. Upload to course website or Google Drive
3. Students download and extract
4. Provide link to Java 21 download (https://adoptium.net/)
5. Students use launcher scripts

**Benefits:**
- One package for all OS types
- Easy to update (just upload new ZIP)
- Students learn about Java environment
- Smaller download for limited bandwidth

---

### For GitHub Releases
**Use:** Portable Package + Fat JARs

**Workflow:**
1. Build all: `.\gradlew buildAllDistributions`
2. Create GitHub Release with tags
3. Upload assets:
   - `bisaya-ide-1.0.0-portable.zip` (main download)
   - `bisaya-ide-1.0.0-fat.jar` (advanced users)
   - `bisaya-cli-1.0.0-fat.jar` (CLI users)

**Release Notes Template:**
```markdown
# Bisaya++ v1.0.0

## Downloads

### For Most Users (Recommended)
- [bisaya-ide-1.0.0-portable.zip](link) - Complete package with launcher scripts

### Advanced Users
- [bisaya-ide-1.0.0-fat.jar](link) - IDE only (requires Java 21+)
- [bisaya-cli-1.0.0-fat.jar](link) - CLI only (requires Java 21+)

## Installation

**Portable Package:**
1. Download and extract ZIP
2. Install Java 21+ from https://adoptium.net/
3. Run launcher script (bisaya-ide.bat or bisaya-ide.sh)

**JAR Files:**
```bash
java -jar bisaya-ide-1.0.0-fat.jar
```

## What's New
- [List changes here]
```

---

### For Production/End Users
**Use:** Native Installer

**Workflow:**
1. Build installer on each OS:
   - Windows: `.\gradlew jpackage` (produces .msi)
   - Linux: `./gradlew jpackage` (produces .deb)
   - Mac: `./gradlew jpackage` (produces .dmg)
2. Upload to website or distribute via:
   - Direct download
   - Windows Store (requires signing)
   - Mac App Store (requires signing)
   - Linux repositories (Debian/Ubuntu PPA)

**Benefits:**
- Professional user experience
- No Java installation required
- Easier support (consistent environment)

---

## Build Matrix

| Task | Windows | Linux | Mac | Output |
|------|---------|-------|-----|--------|
| `ideJar` | ✅ | ✅ | ✅ | JAR (cross-platform) |
| `cliJar` | ✅ | ✅ | ✅ | JAR (cross-platform) |
| `createPortablePackage` | ✅ | ✅ | ✅ | ZIP (cross-platform) |
| `jpackage` (Windows) | ✅ | ❌ | ❌ | .msi |
| `jpackage` (Linux) | ❌ | ✅ | ❌ | .deb |
| `jpackage` (Mac) | ❌ | ❌ | ✅ | .dmg |

---

## Building for Different Platforms

### Cross-Platform Build (JAR/Portable)
Can be built on any OS:
```bash
.\gradlew createPortablePackage
```

### Windows Installer
Must build on Windows:
```powershell
.\gradlew jpackage
```

### Linux Package
Must build on Linux:
```bash
./gradlew jpackage
```

### Mac Installer
Must build on macOS:
```bash
./gradlew jpackage
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build-portable:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build portable package
        run: ./gradlew createPortablePackage
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: portable-package
          path: app/build/distributions/*.zip

  build-windows:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build Windows installer
        run: .\gradlew jpackage
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: windows-installer
          path: app/build/jpackage/*.msi

  build-linux:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build Linux package
        run: ./gradlew jpackage
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: linux-package
          path: app/build/jpackage/*.deb

  build-mac:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build Mac installer
        run: ./gradlew jpackage
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: mac-installer
          path: app/build/jpackage/*.dmg

  release:
    needs: [build-portable, build-windows, build-linux, build-mac]
    runs-on: ubuntu-latest
    steps:
      - name: Download all artifacts
        uses: actions/download-artifact@v3
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            portable-package/*
            windows-installer/*
            linux-package/*
            mac-installer/*
```

---

## File Size Reference

| Package Type | Approximate Size | Contents |
|--------------|-----------------|----------|
| Fat JAR | 15-20 MB | App + JavaFX dependencies |
| Portable ZIP | 20-25 MB | JAR + scripts + samples + docs |
| Native Installer | 60-80 MB | App + bundled JRE + JavaFX |

---

## Testing Distributions

### Before Release Checklist

**Fat JAR:**
- [ ] Runs on Windows with `java -jar`
- [ ] Runs on Linux with `java -jar`
- [ ] Runs on Mac with `java -jar`
- [ ] Can open sample files
- [ ] Can save/load .bpp files
- [ ] Syntax highlighting works
- [ ] Run button executes code
- [ ] Errors display correctly

**Portable Package:**
- [ ] ZIP extracts correctly
- [ ] README is clear and accurate
- [ ] Launcher scripts work on Windows
- [ ] Launcher scripts work on Linux/Mac
- [ ] Java version check works
- [ ] Sample programs included and runnable
- [ ] Documentation accessible

**Native Installer:**
- [ ] Installer runs without errors
- [ ] Desktop shortcut created
- [ ] Start menu entry created (Windows)
- [ ] Application launches successfully
- [ ] Can be uninstalled cleanly
- [ ] Bundled JRE works correctly

---

## Troubleshooting

### Build Errors

**"JavaFX not found"**
- Ensure JavaFX plugin is loaded
- Check internet connection (Maven Central)

**"jpackage not found"**
- Requires JDK 14+ (you have JDK 21, so this should work)
- Ensure using JDK, not JRE
- Check `java -version` shows JDK

**"Module not found" errors**
- Clean build: `.\gradlew clean`
- Rebuild: `.\gradlew build`

### Runtime Errors

**JAR won't launch:**
```bash
# Check Java version
java -version

# Should show Java 21+
# If not, install from https://adoptium.net/
```

**"Main class not found"**
- Check JAR manifest: `jar tf bisaya-ide-1.0.0-fat.jar | findstr Main`
- Rebuild JAR: `.\gradlew clean ideJar`

**JavaFX initialization error:**
- Don't double-click JAR, use launcher script
- Or run with: `java -jar bisaya-ide-1.0.0-fat.jar`

---

## Version Management

Update version in `app/build.gradle`:

```gradle
version = '1.0.0'  // Change this
```

This affects:
- JAR filenames
- Installer version
- Package metadata
- README content

---

## Distribution Checklist

### Pre-Release
- [ ] Update version number
- [ ] Update CHANGELOG
- [ ] Test on all target platforms
- [ ] Update README.txt in portable package
- [ ] Verify sample programs work
- [ ] Check documentation is current

### Build
- [ ] Clean build: `.\gradlew cleanAll`
- [ ] Build distributions: `.\gradlew buildAllDistributions`
- [ ] Build native installers (if applicable)
- [ ] Verify file sizes are reasonable

### Post-Build
- [ ] Test each distribution package
- [ ] Scan for viruses/malware (for user trust)
- [ ] Create checksums (SHA-256)
- [ ] Write release notes

### Distribution
- [ ] Upload to GitHub Releases
- [ ] Update website download links
- [ ] Announce on social media/forums
- [ ] Update documentation

---

## Support Resources

### User Documentation
- Include link to Java download: https://adoptium.net/
- Troubleshooting guide: [Link to your docs]
- Sample programs: Included in `samples/` folder
- Language reference: [Link to grammar docs]

### Developer Documentation
- Build instructions: This file
- API documentation: [Generate with Javadoc]
- Contributing guide: CONTRIBUTING.md
- Issue tracker: GitHub Issues

---

## Future Enhancements

### Planned Features
- [ ] Auto-update mechanism
- [ ] Digitally signed installers (for Windows/Mac)
- [ ] Flatpak package (Linux)
- [ ] Snap package (Linux)
- [ ] Windows Store submission
- [ ] Mac App Store submission
- [ ] Portable Windows .exe (using jlink + jpackage)

### Build Improvements
- [ ] Automated version bumping
- [ ] Automated changelog generation
- [ ] Code signing integration
- [ ] Notarization for Mac
- [ ] Multi-platform build in single command

---

**Document Version:** 1.0  
**Last Updated:** November 9, 2025  
**Maintained by:** Bisaya++ Team
