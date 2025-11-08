# Pretty Print Accessibility Features - Testing Guide

**Date**: November 9, 2025  
**Purpose**: Verify all new formatting features work correctly

---

## 🧪 Manual Testing Checklist

### Setup
1. ✅ Launch IDE: `.\gradlew :app:runIDE --no-daemon`
2. ✅ Open or create a messy .bpp file

### Test Sample Code

```bisaya
SUGOD
MUGNA NUMERO x,y,z
x=5
y=10
z=x+y
KUNG (x>0)
PUNDOK{
IPAKITA:x&y&z
}
KATAPUSAN
```

---

## Test 1: Toolbar Button Visibility

**Steps:**
1. Look at toolbar (top of window)
2. Locate buttons: `[▶ Run] [🗑 Clear Output] [⚌ Format]`

**Expected:**
- ✅ "⚌ Format" button visible after Clear Output
- ✅ Button has tooltip: "Format code (Ctrl+Shift+F)"

**Actual:** _________________

---

## Test 2: Toolbar Button - Full Document Format

**Steps:**
1. Paste messy code (above) into editor
2. **Don't select anything**
3. Click **⚌ Format** button

**Expected:**
- ✅ All code is formatted with proper indentation
- ✅ Status bar: "✓ Code formatted successfully (XXms)"
- ✅ Code should look like:
```bisaya
SUGOD
    MUGNA NUMERO x, y, z
    x = 5
    y = 10
    z = x + y
    KUNG (x > 0)
    PUNDOK{
        IPAKITA: x & y & z
    }
KATAPUSAN
```

**Actual:** _________________

---

## Test 3: Toolbar Button - Selection Format

**Steps:**
1. Undo (Ctrl+Z) to restore messy code
2. **Select lines 3-5** (`x=5` through `z=x+y`)
3. Click **⚌ Format** button

**Expected:**
- ✅ Only selected lines formatted
- ✅ Lines 1-2 and 6-9 unchanged
- ✅ Status bar: "✓ Selection formatted (XXms)"
- ✅ Result:
```bisaya
SUGOD
MUGNA NUMERO x,y,z              ← Unchanged
    x = 5                        ← Formatted
    y = 10                       ← Formatted
    z = x + y                    ← Formatted
KUNG (x>0)                       ← Unchanged
PUNDOK{
IPAKITA:x&y&z
}
KATAPUSAN
```

**Actual:** _________________

---

## Test 4: Context Menu - No Selection

**Steps:**
1. Undo to restore messy code
2. **Right-click** anywhere in editor (no selection)
3. Look at context menu label

**Expected:**
- ✅ Menu item says: **"Format Document"**
- ✅ Shortcut shown: "Ctrl+Shift+F"

**Actual:** _________________

**Steps (continued):**
4. Click "Format Document"

**Expected:**
- ✅ Entire document formatted
- ✅ Status: "✓ Code formatted successfully"

**Actual:** _________________

---

## Test 5: Context Menu - With Selection

**Steps:**
1. Undo to restore messy code
2. **Select lines 3-5**
3. **Right-click** on selected text
4. Look at context menu label

**Expected:**
- ✅ Menu item says: **"Format Selection"** (not "Format Document")
- ✅ Shortcut shown: "Ctrl+Shift+F"

**Actual:** _________________

**Steps (continued):**
5. Click "Format Selection"

**Expected:**
- ✅ Only selected lines formatted
- ✅ Status: "✓ Selection formatted"
- ✅ Other lines unchanged

**Actual:** _________________

---

## Test 6: Keyboard Shortcut - Full Document

**Steps:**
1. Undo to restore messy code
2. **Don't select anything**
3. Press **Ctrl + Shift + F**

**Expected:**
- ✅ Entire document formatted
- ✅ Status: "✓ Code formatted successfully"

**Actual:** _________________

---

## Test 7: Keyboard Shortcut - Selection

**Steps:**
1. Undo to restore messy code
2. **Select lines 6-9** (KUNG block)
3. Press **Ctrl + Shift + F**

**Expected:**
- ✅ Only KUNG block formatted
- ✅ Status: "✓ Selection formatted"
- ✅ Lines 1-5 unchanged

**Actual:** _________________

---

## Test 8: Menu Bar - Full Document

**Steps:**
1. Undo to restore messy code
2. **Don't select anything**
3. Click **Edit** menu → **Format Code**

**Expected:**
- ✅ Entire document formatted
- ✅ Status: "✓ Code formatted successfully"

**Actual:** _________________

---

## Test 9: Menu Bar - Selection

**Steps:**
1. Undo to restore messy code
2. **Select any lines**
3. Click **Edit** menu → **Format Code**

**Expected:**
- ✅ Only selected lines formatted
- ✅ Status: "✓ Selection formatted"

**Actual:** _________________

---

## Test 10: Already Formatted - No Changes

**Steps:**
1. Format entire document (any method)
2. **Without making changes**, format again

**Expected:**
- ✅ No changes to code
- ✅ Status: "Code already formatted"

**Actual:** _________________

---

## Test 11: Already Formatted Selection

**Steps:**
1. Format entire document
2. Select some lines
3. Format selection

**Expected:**
- ✅ No changes to code
- ✅ Status: "Selection already formatted"

**Actual:** _________________

---

## Test 12: Undo Formatting

**Steps:**
1. Restore messy code
2. Format document (any method)
3. Press **Ctrl + Z**

**Expected:**
- ✅ Code reverts to messy state
- ✅ All changes undone

**Actual:** _________________

---

## Test 13: Empty File

**Steps:**
1. Clear all code (Ctrl+A, Delete)
2. Click ⚌ Format button

**Expected:**
- ✅ No error
- ✅ Status: "No code to format"
- ✅ File remains empty

**Actual:** _________________

---

## Test 14: Single Line Selection

**Steps:**
1. Restore messy code
2. **Select only line 3** (`x=5`)
3. Format selection

**Expected:**
- ✅ Only line 3 formatted: `    x = 5`
- ✅ Status: "✓ Selection formatted"
- ✅ All other lines unchanged

**Actual:** _________________

---

## Test 15: Partial Line Selection

**Steps:**
1. Restore messy code
2. **Select from middle of line 3 to middle of line 5**
   (e.g., `=5` through `z=x`)
3. Format selection

**Expected:**
- ✅ **Full lines 3-5** formatted (not partial)
- ✅ Status: "✓ Selection formatted"
- ✅ Lines 1-2 and 6+ unchanged

**Note:** Selection formatting is line-based, not character-based

**Actual:** _________________

---

## Test 16: Complex Example - Mixed Formatting

**Test Code:**
```bisaya
SUGOD
MUGNA NUMERO x,y,z
x=5
    y = 10              ← Already formatted
z=15
KUNG (x>0)
PUNDOK{
        IPAKITA:x       ← Over-indented
}
KATAPUSAN
```

**Steps:**
1. Paste above code
2. Select lines 3, 5, and 8 (x=5, z=15, IPAKITA)
   (Hold Ctrl while clicking lines)
3. Format selection

**Expected:**
- ✅ Selected lines formatted to correct indentation
- ✅ Line 4 (already formatted) unchanged
- ✅ Line 8 corrected from over-indent

**Note:** Multi-selection may not be supported in current implementation.  
Alternative: Select lines 3-8 together.

**Actual:** _________________

---

## Test 17: Performance Test

**Test Code:** Create a large file (~200 lines)

**Steps:**
1. Copy-paste test code 20 times
2. Format entire document
3. Note time in status bar

**Expected:**
- ✅ Formats in <200ms
- ✅ Status: "✓ Code formatted successfully (XXms)"
- ✅ No freezing or lag

**Actual Time:** _________ ms

---

## Test 18: Selection Performance

**Steps:**
1. Using large file (200 lines)
2. Select 10 lines in middle
3. Format selection
4. Note time

**Expected:**
- ✅ Faster than full format
- ✅ Status: "✓ Selection formatted (XXms)"
- ✅ Time < 100ms

**Actual Time:** _________ ms

---

## 🎯 Success Criteria

**Critical (Must Pass):**
- [ ] Toolbar button visible and works
- [ ] Context menu appears on right-click
- [ ] Context menu label changes (Document/Selection)
- [ ] Selection formatting works correctly
- [ ] Full document formatting still works
- [ ] All 4 methods produce same result
- [ ] Status bar messages accurate
- [ ] Undo works

**Important (Should Pass):**
- [ ] No crashes or errors
- [ ] Performance <200ms for typical files
- [ ] Tooltip on toolbar button
- [ ] Keyboard shortcut works

**Nice to Have:**
- [ ] Performance <50ms for small selections
- [ ] Graceful handling of edge cases

---

## 🐛 Bug Report Template

If you find issues, document them here:

### Bug 1: ___________________

**What Happened:**


**Expected:**


**Steps to Reproduce:**
1. 
2. 
3. 

**Severity:** Critical / High / Medium / Low

---

## ✅ Final Verification

**All Tests Passed:** ☐ Yes  ☐ No  

**Ready for Production:** ☐ Yes  ☐ No  

**Notes:**




---

**Tester:** _________________  
**Date:** _________________  
**Build:** gradle build (November 9, 2025)
