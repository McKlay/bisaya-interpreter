You are tasked with developing a comprehensive Bisaya++ Interpreter in Java that processes and executes programs written in the Bisaya++ programming language, a Cebuano-based educational programming language.

**Project Objective:**
Build a fully functional interpreter that can parse, analyze, and execute Bisaya++ source code according to the provided language specification. The interpreter must handle all language features including variables, operators, control structures, and I/O operations.

**Implementation Requirements:**

**Increment 1 (Due: October 11, 2025)**
Core language recognition and basic operations:
- Parse program structure (SUGOD/KATAPUSAN keywords)
- Handle comments (-- prefix) correctly
- Recognize all reserved words and prevent their use as variable names
- Implement variable declaration with MUGNA keyword
- Support variable assignment operations (including chained assignments like x=y=4)
- Implement string concatenation using & operator
- Execute IPAKITA command for output display
- Handle escape sequences with square brackets []

**Increment 2 (Due: October 18, 2025)**
Operators and input functionality:
- Implement unary operators: +, -, ++, --
- Add DAWAT command for user input (comma-separated values)
- Support arithmetic operations: +, -, *, /, %
- Implement comparison operators: >, <, >=, <=, ==, <>
- Support logical operations: UG (AND), O (OR), DILI (NOT)
- Handle boolean literals: "OO" (true), "DILI" (false)

**Increment 3 (Due: November 8, 2025)**
Conditional control structures:
- Implement KUNG (if) statements with PUNDOK{} blocks
- Execute KUNG-KUNG WALA (if-else) statements
- Support KUNG-KUNG DILI (if-else if) with multiple alternatives
- Handle nested conditional structures
- Ensure proper boolean expression evaluation

**Increment 4 (Due: November 15, 2025)**
Loop implementation:
- Execute WHILE loops (syntax to be clarified based on specification)
- Support nested WHILE loops
- Ensure proper loop termination and variable scope

**Required Project Structure:**
```
bisaya++/
├── build.gradle (or pom.xml)
└── src/
    ├── main/java/com/bisayapp/
    │   ├── Bisaya.java              // CLI entry point
    │   ├── ErrorReporter.java       // Error handling
    │   ├── Token.java               // Token representation
    │   ├── TokenType.java           // Token type enumeration
    │   ├── Lexer.java               // Lexical analysis
    │   ├── Expr.java                // Expression AST nodes
    │   ├── Stmt.java                // Statement AST nodes
    │   ├── Parser.java              // Syntax analysis
    │   ├── Environment.java         // Variable scope management
    │   ├── Interpreter.java         // Runtime execution
    │   └── Value.java               // Runtime value representation
    └── test/java/com/bisayapp/
        └── // JUnit tests for each increment
```

**Deliverables Required:**
1. Complete working Bisaya++ Interpreter implementing all increment features
2. Comprehensive test cases for each increment demonstrating functionality
3. Error handling for syntax errors, runtime errors, and type mismatches
4. Documentation explaining interpreter architecture and usage

**Success Criteria:**
- Correctly parse and execute all provided sample programs
- Handle edge cases and error conditions gracefully
- Maintain backward compatibility across increments
- Pass all test cases for each increment
- Follow Java best practices and clean code principles

**Testing Requirements:**
Create test cases that verify:
- Correct parsing of language constructs
- Proper variable declaration and assignment
- Accurate arithmetic and logical operations
- Correct control flow execution
- Proper I/O handling
- Error detection and reporting

Each increment must build upon previous functionality while maintaining full compatibility with earlier features.

**Language Specification of BISAYA++ Programming Language**
**I. Introduction**
Bisaya++ is a strongly–typed high–level interpreted Cebuano-based programming language developed to teach Cebuanos
the basics of programming. Its simple syntax and native keywords make programming easy to learn.

Sample Program:
-- this is a sample program in Bisaya++
SUGOD
MUGNA NUMERO x, y, z=5
MUGNA LETRA a_1='n'
MUGNA TINUOD t="OO"
x=y=4
a_1='c'
-- this is a comment
IPAKITA: x & t & z & $ & a_1 & [&] & "last"
KATAPUSAN

Output of the sample program:
4OO5
c&last

**II. Language Grammar**

**Program Structure:**
- all codes are placed inside SUGOD and KATAPUSAN
- all variable declaration starts with MUGNA
- all variable names are case sensitive and starts with letter or an underscore (_) and followed by a letter,
underscore or digits.
- every line contains a single statement
- comments starts with double minus sign(--) and it can be placed anywhere in the program
- all reserved words are in capital letters and cannot be used as variable names
- dollar sign($) signifies next line or carriage return
- ampersand(&) serves as a concatenator
- the square braces([]) are as escape code

**Data Types:**
1. NUMERO – an ordinary number with no decimal part. It occupies 4 bytes in the memory.
2. LETRA – a single symbol.
3. TINUOD – represents the literals true or false.
4. TIPIK – a number with decimal part.

**Operators:**
**Arithmetic operators**
( ) - parenthesis
*, /, % - multiplication, division, modulo
+, - - addition, subtraction
>, < - greater than, lesser than
>=, <= - greater than or equal to, lesser than or equal to
==, <> - equal, not equal

**Logical operators** (<BOOL expression><LogicalOperator><BOOL expression>)
UG - AND, needs the two BOOL expression to be true to result to true, else false
O - OR, if one of the BOOL expressions evaluates to true, returns true, else false
DILI - NOT, the reverse value of the BOOL value
Boolean values (enclosed with a double quote)
OO - TRUE
DILI - FALSE

**Unary operator**
+ - positive
- - negative
++ - increment
-- - decrement

**Sample Programs**
1. A program with arithmetic operation
SUGOD
    MUGNA NUMERO xyz, abc=100
    xyz= ((abc *5)/10 + 10) * -1
    IPAKITA: [[] & xyz & []]
KATAPUSAN

Output of the sample program:
[-60]

2. A program with logical operation
SUGOD
    MUGNA NUMERO a=100, b=200, c=300
    MUGNA TINUOD d=”DILI”
    d = (a < b UG c <>200)
    IPAKITA: d
KATAPUSAN

Output of the sample program:
OO

**Code output statement:**
    IPAKITA - writes formatted output to the output device
**Code input statement:**
    DAWAT – allow the user to input a value to a data type.
    Syntax:
        DAWAT: <variableName>[,<variableName>]*
    Sample use:
        DAWAT: x, y
        It means in the screen you have to input two values separated by comma(,)
        
**CODE control flow structures:**
1. Conditional
    a. KUNG (if selection)
        KUNG (<BOOL expression>)
        PUNDOK{
            <statement>
            …
            <statement>
        }

    b. KUNG-KUNG WALA (if-else selection)
        KUNG (<BOOL expression>)
        PUNDOK{
            <statement>
            …
            <statement>
        }
        KUNG WALA
        PUNDOK{
            <statement>
            …
            <statement>
        }

    c. KUNG-KUNG DILI (if-else with multiple alternatives)
        KUNG (<BOOL expression>)
        PUNDOK{
            <statement>
            …
            <statement>
        }
        KUNG DILI (<BOOL expression>)
        PUNDOK{
            <statement>
            …
            <statement>
        }
        KUNG WALA
        PUNDOK{
            <statement>
            …
            <statement>
        }

    PUNDOK{ } – group a block of codes. Statements inside conditions and loops are enclosed PUNDOK{ }.

2. Loop Control Flow Structures

    a. ALANG SA (initialization, condition, update) - (FOR LOOP)
        PUNDOK{
            <statement>
            …
            <statement>
        }

        Example:
        ALANG SA (ctr=1, ctr<=10, ctr++)
        PUNDOK{
            IPAKITA: ctr & ' '
        }
        Output:
        1 2 3 4 5 6 7 8 9 10
        