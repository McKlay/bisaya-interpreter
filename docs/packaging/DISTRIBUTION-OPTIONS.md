# Bisaya++ Distribution Guide

## Overview

This guide explains all available distribution options for the Bisaya++ IDE. The Windows MSI installer is now fully working and recommended for end users.

---

## Quick Start Commands

```bash
# Build Windows MSI installer (✅ RECOMMENDED for end users)
.\gradlew createMSI
# Output: app/build/distribution/BisayaIDE-1.0.0.msi (~40 MB)

# Build portable package (cross-platform)
.\gradlew createPortablePackage
# Output: app/build/distributions/bisaya-ide-1.0.0-portable.zip

# Build CLI JAR only
.\gradlew cliJar
```

---

## Distribution Options

### Option 1: Windows MSI Installer (✅ RECOMMENDED for Windows Users)

**Best for:** End users, classroom distribution, widest audience

#### Build Command
```bash
.\gradlew createMSI
```

#### Output Location
```
app/build/distribution/BisayaIDE-1.0.0.msi (~40 MB)
```

#### Installation
1. Double-click MSI file
2. Follow wizard
3. Launch from Start Menu

#### Features
✅ **No Java required** - JRE 21 bundled  
✅ **Professional installer** - Native Windows experience  
✅ **Start Menu shortcuts** - Easy access  
✅ **Full IDE** - Syntax highlighting, examples, samples  
✅ **Self-contained** - ~40 MB all-in-one

#### Pros
✅ Easiest for non-technical users  
✅ No Java installation needed  
✅ Professional appearance  
✅ One-click launch  

#### Cons
❌ Windows only (need separate builds for Linux/Mac)  
❌ Larger download size

---

### Option 2: Portable Package (Cross-Platform)

**Best for:** GitHub releases, cross-platform distribution, classroom

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

### For Windows Users (Most Recommended)
**Use:** Windows MSI Installer

**Advantages:**
- Zero setup - no Java installation needed
- Professional one-click install
- Most user-friendly

---

### For Classroom/Academic Use
**Use:** Portable Package (cross-platform) or MSI (Windows labs)

**Workflow:**
1. Windows: Download MSI, double-click to install
2. Other OS: Download portable ZIP, extract, run launcher
3. Provide link to Java 21 (https://adoptium.net/) for non-Windows

---

### For GitHub Releases
**Use:** MSI (Windows) + Portable ZIP (cross-platform) + CLI JAR (advanced)

**Workflow:**
1. Build: `.\gradlew createMSI && .\gradlew createPortablePackage && .\gradlew cliJar`
2. Create GitHub Release
3. Upload assets:
   - `BisayaIDE-1.0.0.msi` - Windows users (recommended, no Java needed)
   - `bisaya-ide-1.0.0-portable.zip` - Cross-platform
   - `bisaya-cli-1.0.0-fat.jar` - Advanced/CLI users

**Release Notes Template:**
```markdown
# Bisaya++ v1.0.0 🎉

## Downloads

### Windows (Recommended)
- [BisayaIDE-1.0.0.msi](link) - Professional installer, no Java required

### Cross-Platform
- [bisaya-ide-1.0.0-portable.zip](link) - Works on Windows/Linux/Mac

### Advanced
- [bisaya-cli-1.0.0-fat.jar](link) - CLI only (requires Java 21+)

## Installation

**Windows:**
Download and run the MSI - automatic installation!

**Other OS / Portable:**
Extract ZIP and run launcher script

## What's New
- ✅ Official MSI installer for Windows
- ✅ No Java installation required
- [List other features]
```

---

### For Production/End Users
**Use:** Windows MSI (bundled JRE) or Portable Package

**Workflow:**
1. Windows: Distribute MSI (~40 MB, all-in-one)
2. Other OS: Distribute portable ZIP (~20 MB)
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
