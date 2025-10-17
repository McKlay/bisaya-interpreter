# Increment 4: ALANG SA Loop - Quick Reference

## Loop Syntax

### Basic Structure
```bisaya
ALANG SA (initializer, condition, update) PUNDOK{
    -- loop body
}
```

### Components
- **initializer**: Execute once before loop starts (e.g., `ctr=1`)
- **condition**: Checked before each iteration (e.g., `ctr<=10`)
- **update**: Execute after each iteration (e.g., `ctr++`)
- **body**: PUNDOK block with statements to repeat

## Common Patterns

### Count Up
```bisaya
MUGNA NUMERO i
ALANG SA (i=1, i<=10, i++)
PUNDOK{
    IPAKITA: i
}
```

### Count Down
```bisaya
MUGNA NUMERO i
ALANG SA (i=10, i>0, i--)
PUNDOK{
    IPAKITA: i
}
```

### Custom Increment
```bisaya
MUGNA NUMERO i
ALANG SA (i=0, i<100, i=i+5)
PUNDOK{
    IPAKITA: i
}
```

### Nested Loops
```bisaya
MUGNA NUMERO i, j
ALANG SA (i=1, i<=3, i++)
PUNDOK{
    ALANG SA (j=1, j<=3, j++)
    PUNDOK{
        IPAKITA: i & "," & j
    }
}
```

### Loop with Accumulator
```bisaya
MUGNA NUMERO i, sum=0
ALANG SA (i=1, i<=100, i++)
PUNDOK{
    sum = sum + i
}
IPAKITA: sum
```

### Loop with Conditional
```bisaya
MUGNA NUMERO n
ALANG SA (n=1, n<=20, n++)
PUNDOK{
    KUNG (n % 2 == 0)
    PUNDOK{
        IPAKITA: "Even: " & n
    }
    KUNG WALA
    PUNDOK{
        IPAKITA: "Odd: " & n
    }
}
```

## Important Notes

1. **Loop Variable Scope**: Variable persists after loop with last value
   ```bisaya
   ALANG SA (i=1, i<=5, i++)  
   -- After loop, i = 6 (value that failed condition)
   ```

2. **All Three Parts Required**: Must specify init, condition, and update
   ```bisaya
   ALANG SA (i=0, i<10, i++)  -- ✅ Valid
   ALANG SA (i=0, i<10)       -- ❌ Missing update
   ```

3. **PUNDOK Required**: Body must be a PUNDOK block
   ```bisaya
   ALANG SA (...) PUNDOK{ }   -- ✅ Valid
   ALANG SA (...) IPAKITA: x  -- ❌ Missing PUNDOK
   ```

4. **Comma Separators**: Use commas between loop parts
   ```bisaya
   ALANG SA (i=1, i<=10, i++)  -- ✅ Commas required
   ALANG SA (i=1; i<=10; i++)  -- ❌ Semicolons not allowed
   ```

## Tips and Tricks

### Print All on One Line
Use escape sequence `$` for newline control:
```bisaya
ALANG SA (i=1, i<=5, i++)
PUNDOK{
    IPAKITA: i & " "
}
IPAKITA: $  -- Final newline
```

### Multiplication Table
```bisaya
MUGNA NUMERO r, c, p
ALANG SA (r=1, r<=10, r++)
PUNDOK{
    ALANG SA (c=1, c<=10, c++)
    PUNDOK{
        p = r * c
        IPAKITA: p & [&]
    }
    IPAKITA: $
}
```

### Fibonacci Sequence
```bisaya
MUGNA NUMERO i, a=0, b=1, temp
ALANG SA (i=1, i<=10, i++)
PUNDOK{
    IPAKITA: a
    temp = a + b
    a = b
    b = temp
}
```

### Triangle Pattern
```bisaya
MUGNA NUMERO i, j
ALANG SA (i=1, i<=5, i++)
PUNDOK{
    ALANG SA (j=1, j<=i, j++)
    PUNDOK{
        IPAKITA: "*"
    }
    IPAKITA: $
}
```

## Error Prevention

### ❌ Common Mistakes

1. **Forgetting PUNDOK**
   ```bisaya
   ALANG SA (i=1, i<=5, i++)
       IPAKITA: i  -- ERROR: Missing PUNDOK
   ```

2. **Missing Comma**
   ```bisaya
   ALANG SA (i=1 i<=5 i++)  -- ERROR: Missing commas
   ```

3. **Using Semicolons**
   ```bisaya
   ALANG SA (i=1; i<=5; i++)  -- ERROR: Use commas, not semicolons
   ```

4. **Undeclared Variable**
   ```bisaya
   ALANG SA (i=1, i<=5, i++)  -- ERROR: Must declare i first
   ```

### ✅ Correct Versions

1. **With PUNDOK**
   ```bisaya
   ALANG SA (i=1, i<=5, i++)
   PUNDOK{
       IPAKITA: i
   }
   ```

2. **With Commas**
   ```bisaya
   ALANG SA (i=1, i<=5, i++)
   ```

3. **Declared Variable**
   ```bisaya
   MUGNA NUMERO i
   ALANG SA (i=1, i<=5, i++)
   PUNDOK{
       IPAKITA: i
   }
   ```

## Performance Considerations

- Loops are efficient for reasonable iteration counts
- Tested successfully with 1-100,000 iterations
- Nested loops multiply iteration count (outer × inner)
- Complex conditions may slow down large loops

## See Also

- INCREMENT4-FINAL-REPORT.md - Complete implementation details
- INCREMENT4-SUMMARY.md - Quick summary
- Sample programs in `samples/increment4_*.bpp`
