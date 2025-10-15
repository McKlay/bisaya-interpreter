# Increment 3 Quick Reference

## Conditional Statements in Bisaya++

### Simple If Statement
```bisaya
KUNG (condition) PUNDOK { statements }
```

**Example:**
```bisaya
KUNG (x > 10)
PUNDOK{
    IPAKITA: "x is large"
}
```

### If-Else Statement
```bisaya
KUNG (condition)
PUNDOK { then-statements }
KUNG WALA
PUNDOK { else-statements }
```

**Example:**
```bisaya
KUNG (age >= 18)
PUNDOK{
    IPAKITA: "Adult"
}
KUNG WALA
PUNDOK{
    IPAKITA: "Minor"
}
```

### If-Else-If Statement
```bisaya
KUNG (condition1)
PUNDOK { statements1 }
KUNG DILI (condition2)
PUNDOK { statements2 }
KUNG DILI (condition3)
PUNDOK { statements3 }
KUNG WALA
PUNDOK { default-statements }
```

**Example:**
```bisaya
KUNG (temp > 30)
PUNDOK{ IPAKITA: "Hot" }
KUNG DILI (temp > 20)
PUNDOK{ IPAKITA: "Warm" }
KUNG DILI (temp > 10)
PUNDOK{ IPAKITA: "Cool" }
KUNG WALA
PUNDOK{ IPAKITA: "Cold" }
```

### Nested Conditionals
```bisaya
KUNG (outer-condition)
PUNDOK{
    KUNG (inner-condition)
    PUNDOK{
        statements
    }
}
```

**Example:**
```bisaya
KUNG (x > 0)
PUNDOK{
    KUNG (y > 0)
    PUNDOK{
        IPAKITA: "Both positive"
    }
    KUNG WALA
    PUNDOK{
        IPAKITA: "x positive, y non-positive"
    }
}
```

## Conditions

### Comparison Operators
- `>` - greater than
- `<` - less than
- `>=` - greater than or equal
- `<=` - less than or equal
- `==` - equal to
- `<>` - not equal to

### Logical Operators
- `UG` - AND (both conditions must be true)
- `O` - OR (at least one condition must be true)
- `DILI` - NOT (negates the condition)

### Boolean Values
- `"OO"` - true
- `"DILI"` - false (as a value, not operator)

## Important Rules

1. **Parentheses Required**: Conditions MUST be in parentheses
   - ✅ `KUNG (x > 5)`
   - ❌ `KUNG x > 5`

2. **PUNDOK Required**: All conditional bodies MUST use PUNDOK blocks
   - ✅ `KUNG (x > 5) PUNDOK { IPAKITA: x }`
   - ❌ `KUNG (x > 5) IPAKITA: x`

3. **Braces Required**: PUNDOK blocks MUST have { }
   - ✅ `PUNDOK { statements }`
   - ❌ `PUNDOK statements`

4. **Order Matters**: 
   - KUNG must come first
   - KUNG DILI comes after KUNG or another KUNG DILI
   - KUNG WALA comes last (if present)

5. **Variable Scope**: Variables modified inside blocks persist outside
   ```bisaya
   MUGNA NUMERO x=5
   KUNG (x > 0)
   PUNDOK{
       x = 10  -- Changes x permanently
   }
   IPAKITA: x  -- Prints 10
   ```

## Common Patterns

### Max of Two Numbers
```bisaya
KUNG (a > b)
PUNDOK{ max = a }
KUNG WALA
PUNDOK{ max = b }
```

### Range Check
```bisaya
KUNG (x >= min UG x <= max)
PUNDOK{
    IPAKITA: "in range"
}
```

### Multiple Conditions
```bisaya
KUNG (age >= 18 UG hasLicense)
PUNDOK{
    IPAKITA: "Can drive"
}
```

### Truthiness
Any value can be used as a condition:
- Numbers: 0 is false, non-zero is true
- TINUOD: "OO" is true, "DILI" is false
- LETRA/strings: non-empty is true

## Testing Your Conditionals

### Using IPAKITA for Debugging
```bisaya
IPAKITA: "Before if: x = " & x
KUNG (x > 5)
PUNDOK{
    IPAKITA: "Inside if block"
    x = x + 1
}
IPAKITA: "After if: x = " & x
```

### Testing Boolean Variables
```bisaya
MUGNA TINUOD flag="OO"
KUNG (flag)
PUNDOK{ IPAKITA: "flag is true" }
KUNG (DILI flag)
PUNDOK{ IPAKITA: "flag is false" }
```

## Complete Example: Number Classifier

```bisaya
SUGOD
    MUGNA NUMERO num=-5
    
    KUNG (num > 0)
    PUNDOK{
        IPAKITA: num & " is positive"
    }
    KUNG DILI (num < 0)
    PUNDOK{
        IPAKITA: num & " is negative"
    }
    KUNG WALA
    PUNDOK{
        IPAKITA: num & " is zero"
    }
    
    -- Also check even/odd (if not zero)
    KUNG (num <> 0)
    PUNDOK{
        KUNG (num % 2 == 0)
        PUNDOK{
            IPAKITA: "and it is even"
        }
        KUNG WALA
        PUNDOK{
            IPAKITA: "and it is odd"
        }
    }
KATAPUSAN
```

Output:
```
-5 is negative
and it is odd
```
