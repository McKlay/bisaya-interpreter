# Pretty Print - Quick Start Guide

**Feature**: Code Formatter for Bisaya++  
**Status**: ✅ Ready to Use  
**Shortcut**: `Ctrl + Shift + F`

---

## How to Format Code

### Option 1: Keyboard (Fastest) ⌨️
1. Type or load your code
2. Press `Ctrl + Shift + F`
3. Done! Code is formatted

### Option 2: Menu 📋
1. Click **Edit** in menu bar
2. Click **Format Code**
3. Done!

---

## What It Does

✅ **Indents code** - 4 spaces per level  
✅ **Spaces operators** - `x=5` becomes `x = 5`  
✅ **Spaces commas** - `x,y,z` becomes `x, y, z`  
✅ **Converts tabs** - Tabs become 4 spaces  
✅ **Preserves comments** - `@@` comments unchanged  
✅ **Keeps blank lines** - Empty lines maintained  

---

## Example

### Before
```bisaya
SUGOD
MUGNA NUMERO x,y
x=5
y=x+10
IPAKITA:x&" + "&y&" = "&(x+y)
KATAPUSAN
```

### After (Ctrl+Shift+F)
```bisaya
SUGOD
    MUGNA NUMERO x, y
    x = 5
    y = x + 10
    IPAKITA: x & " + " & y & " = " & (x + y)
KATAPUSAN
```

---

## Tips

💡 **Undo**: Press `Ctrl + Z` if you don't like the result  
💡 **Status**: Check bottom status bar for confirmation  
💡 **Safe**: Never changes program logic, only spacing  
💡 **Fast**: Formats in milliseconds  

---

## Common Issues

**Q: My code didn't change**  
A: It's already formatted! Status bar says "Code already formatted"

**Q: Some lines look wrong**  
A: Known limitation with complex nested structures. Press Ctrl+Z and format manually.

**Q: Can I change indent size?**  
A: Not yet - fixed at 4 spaces. Future enhancement.

**Q: Will it break my code?**  
A: No! Formatting only changes whitespace, never logic.

---

## What Gets Formatted

| Element | Before | After |
|---------|--------|-------|
| Assignment | `x=5` | `x = 5` |
| Arithmetic | `a+b*c` | `a + b * c` |
| Comparison | `x>5` | `x > 5` |
| Logical | `aUGb` | `a UG b` |
| Concatenation | `"a"&"b"` | `"a" & "b"` |
| Commas | `x,y,z` | `x, y, z` |
| Indentation | None | 4 spaces |
| Tabs | 1 tab (8 chars) | 4 spaces |

---

## Keyboard Shortcuts

| Action | Windows/Linux | Mac |
|--------|---------------|-----|
| **Format Code** | `Ctrl + Shift + F` | `Cmd + Shift + F` |
| **Undo** | `Ctrl + Z` | `Cmd + Z` |
| **Redo** | `Ctrl + Y` | `Cmd + Y` |

---

## When to Use

✅ **Before saving** - Clean up code before committing  
✅ **After pasting** - Format copied code  
✅ **During demo** - Make code look professional  
✅ **While learning** - See proper formatting examples  
✅ **After editing** - Restore consistent style  

---

## When NOT to Use

❌ **On error-prone code** - Fix syntax errors first  
❌ **Mid-editing** - Finish your thought first  
❌ **On carefully aligned code** - If you manually aligned, skip it  

---

## Performance

- **Small files** (<100 lines): Instant (<50ms)
- **Medium files** (100-500 lines): Very fast (<200ms)
- **Large files** (500+ lines): Fast (<1000ms)

Status bar shows actual time: "✓ Code formatted successfully (Xms)"

---

## Need Help?

**Documentation**: See `docs/IDE/PRETTY-PRINT-FINAL-REPORT.md`  
**Examples**: See `docs/IDE/PRETTY-PRINT-QUICK-REFERENCE.md`  
**Issues**: Use GitHub Issues to report problems

---

**Version**: 1.0  
**Last Updated**: November 8, 2025  
**Ready to use!** Press `Ctrl + Shift + F` now!
