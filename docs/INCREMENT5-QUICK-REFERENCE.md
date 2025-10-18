# Increment 5 Quick Reference - SAMTANG (While Loop)

## Syntax

```bisaya
SAMTANG (<condition>)
PUNDOK{
    <statements>
}
```

## Basic Example

```bisaya
SUGOD
MUGNA NUMERO ctr = 1
SAMTANG (ctr <= 5)
PUNDOK{
    IPAKITA: ctr & " "
    ctr = ctr + 1
}
KATAPUSAN
```
**Output:** `1 2 3 4 5 `

---

## Common Patterns

### 1. Counter Loop
```bisaya
MUGNA NUMERO i = 0
SAMTANG (i < 10)
PUNDOK{
    IPAKITA: i & $
    i = i + 1
}
```

### 2. Countdown
```bisaya
MUGNA NUMERO count = 5
SAMTANG (count > 0)
PUNDOK{
    IPAKITA: count & " "
    count = count - 1
}
```

### 3. Accumulator
```bisaya
MUGNA NUMERO sum = 0, i = 1
SAMTANG (i <= 10)
PUNDOK{
    sum = sum + i
    i = i + 1
}
IPAKITA: "Sum: " & sum
```

### 4. Conditional Loop
```bisaya
MUGNA NUMERO x = 1
MUGNA TINUOD continue = "OO"
SAMTANG (continue == "OO")
PUNDOK{
    x = x + 1
    KUNG (x >= 5)
    PUNDOK{
        continue = "DILI"
    }
}
```

### 5. Nested While Loops
```bisaya
MUGNA NUMERO row = 1, col
SAMTANG (row <= 3)
PUNDOK{
    col = 1
    SAMTANG (col <= 3)
    PUNDOK{
        IPAKITA: "*"
        col = col + 1
    }
    IPAKITA: $
    row = row + 1
}
```

---

## Important Rules

### ✅ DO

1. **Declare loop variables outside the loop**
   ```bisaya
   MUGNA NUMERO i = 0, j  -- Correct
   SAMTANG (i < 5)
   PUNDOK{
       j = 0
       SAMTANG (j < 3)
       PUNDOK{ ... }
   }
   ```

2. **Update loop variable in body**
   ```bisaya
   SAMTANG (count > 0)
   PUNDOK{
       -- ... code ...
       count = count - 1  -- Update!
   }
   ```

3. **Use meaningful conditions**
   ```bisaya
   SAMTANG (x < max UG y > min)  -- Compound condition
   SAMTANG (running == "OO")      -- Boolean variable
   SAMTANG (count % 2 == 0)       -- Expression condition
   ```

### ❌ DON'T

1. **Don't redeclare variables inside loop**
   ```bisaya
   SAMTANG (i < 5)
   PUNDOK{
       MUGNA NUMERO temp = i  -- ERROR on 2nd iteration!
   }
   ```

2. **Don't forget to update loop variable**
   ```bisaya
   MUGNA NUMERO x = 1
   SAMTANG (x < 5)
   PUNDOK{
       IPAKITA: x
       -- Missing: x = x + 1  (infinite loop!)
   }
   ```

3. **Don't use semicolons**
   ```bisaya
   SAMTANG (x < 5);  -- ERROR: No semicolons in Bisaya++
   ```

---

## Condition Types

### Simple Comparison
```bisaya
SAMTANG (x < 10)
SAMTANG (count >= 5)
SAMTANG (value <> 0)
```

### Compound Conditions
```bisaya
SAMTANG (x < 10 UG y > 0)    -- AND
SAMTANG (done == "DILI" O count < max)  -- OR
SAMTANG (DILI (x == 0))       -- NOT
```

### Boolean Variables
```bisaya
MUGNA TINUOD active = "OO"
SAMTANG (active == "OO")
```

---

## Loop Control Patterns

### Pattern 1: Fixed Iterations (Use FOR instead)
```bisaya
-- Better with ALANG SA, but can use SAMTANG:
MUGNA NUMERO i = 1
SAMTANG (i <= 10)
PUNDOK{
    IPAKITA: i & " "
    i = i + 1
}
```

### Pattern 2: Condition-Based (Perfect for SAMTANG)
```bisaya
MUGNA NUMERO num = 100
SAMTANG (num > 1)
PUNDOK{
    KUNG (num % 2 == 0)
    PUNDOK{
        num = num / 2
    }
    KUNG WALA
    PUNDOK{
        num = (num * 3) + 1
    }
}
```

### Pattern 3: Sentinel-Controlled
```bisaya
MUGNA NUMERO input = 0, sum = 0
SAMTANG (input <> -1)
PUNDOK{
    DAWAT: input
    KUNG (input <> -1)
    PUNDOK{
        sum = sum + input
    }
}
```

---

## ALANG SA vs SAMTANG

| When to Use | ALANG SA (For) | SAMTANG (While) |
|-------------|----------------|-----------------|
| **Known iterations** | ✅ Best choice | ⚠️ Works but verbose |
| **Condition-based** | ⚠️ Can work | ✅ Best choice |
| **Unknown iterations** | ❌ Not ideal | ✅ Best choice |
| **Counter pattern** | ✅ Clean syntax | ⚠️ Manual counter |

---

## Common Algorithms

### Calculate Factorial
```bisaya
MUGNA NUMERO n = 5, factorial = 1
SAMTANG (n > 0)
PUNDOK{
    factorial = factorial * n
    n = n - 1
}
IPAKITA: "Factorial: " & factorial
```

### Find Power
```bisaya
MUGNA NUMERO base = 2, exp = 8, result = 1
SAMTANG (exp > 0)
PUNDOK{
    result = result * base
    exp = exp - 1
}
IPAKITA: "Power: " & result
```

### Print Pattern
```bisaya
MUGNA NUMERO row = 1, col
SAMTANG (row <= 5)
PUNDOK{
    col = 1
    SAMTANG (col <= row)
    PUNDOK{
        IPAKITA: "*"
        col = col + 1
    }
    IPAKITA: $
    row = row + 1
}
```

### Sum Input Until Zero
```bisaya
MUGNA NUMERO num = 1, sum = 0
SAMTANG (num <> 0)
PUNDOK{
    DAWAT: num
    sum = sum + num
}
IPAKITA: "Total: " & sum
```

---

## Troubleshooting

### Problem: Infinite Loop
**Symptom:** Program never stops
**Solution:** Make sure loop variable is updated and condition will eventually become false
```bisaya
MUGNA NUMERO x = 1
SAMTANG (x < 5)
PUNDOK{
    IPAKITA: x
    x = x + 1  -- Don't forget this!
}
```

### Problem: Zero Iterations
**Symptom:** Loop body never executes
**Solution:** Check initial condition
```bisaya
MUGNA NUMERO x = 10
SAMTANG (x < 5)  -- False from start!
PUNDOK{
    -- Never executed
}
```

### Problem: Variable Redeclaration Error
**Symptom:** "Variable 'x' is already declared"
**Solution:** Declare variables outside loop
```bisaya
MUGNA NUMERO i = 1, temp  -- Declare here
SAMTANG (i < 5)
PUNDOK{
    temp = i * 2  -- Assign here (no MUGNA)
    IPAKITA: temp
    i = i + 1
}
```

---

## Quick Tips

1. **Use ALANG SA when you know the iteration count**
2. **Use SAMTANG when condition determines when to stop**
3. **Always declare loop variables before the loop**
4. **Make sure the condition can eventually become false**
5. **Update loop control variables inside the body**
6. **Use meaningful variable names (not just i, j, k)**
7. **Test with small values first to avoid long-running loops**

---

## Sample Programs Location

- `samples/increment5_basic_while.bpp` - Basic counter
- `samples/increment5_nested_while.bpp` - Multiplication table
- `samples/increment5_while_conditional.bpp` - Even numbers
- `samples/increment5_pattern.bpp` - Triangle pattern
- `samples/increment5_arithmetic.bpp` - Sum and factorial

Run with: `.\gradlew run --args="samples/filename.bpp"`

---

**Quick Reference for SAMTANG (While Loop) - Increment 5**  
**Bisaya++ Programming Language**
