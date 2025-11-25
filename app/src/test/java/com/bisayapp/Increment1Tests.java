package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Increment1Tests {

    private PrintStream origOut;
    private ByteArrayOutputStream buf;

    @BeforeEach
    void setUp() {
        origOut = System.out;
        buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf));
    }

    @AfterEach
    void tearDown() {
        System.setOut(origOut);
    }

    // ---- helpers -----------------------------------------------------------
    private String runSource(String src) {
        List<Token> toks = new Lexer(src).scanTokens();
        List<Stmt> prog = new Parser(toks).parseProgram();

        // If your Interpreter has (PrintStream) ctor, use it; else use no-arg.
        Interpreter interp = new Interpreter(new PrintStream(buf));
        interp.interpret(prog);
        return buf.toString().replace("\r\n", "\n").trim();
    }

    private String runFile(String rel) throws IOException {
        String src = Files.readString(Paths.get(rel));
        return runSource(src);
    }

    private Exception expectError(String src) {
        try {
            List<Token> toks = new Lexer(src).scanTokens();
            List<Stmt> prog = new Parser(toks).parseProgram();
            Interpreter interp = new Interpreter(new PrintStream(buf));
            interp.interpret(prog);
            return null;
        } catch (Exception ex) {
            return ex;
        }
    }

    // ========================================================================
    // POSITIVE TEST CASES
    // ========================================================================

    // ---- Program structure & comments -------------------------------------
    @Test
    void programMarkersAndComments() {
        String src = """
            @@ header comment
            SUGOD
              @@ inside
              IPAKITA: "A" & $
              IPAKITA: "B"
              @@ trailer
            KATAPUSAN
            """;
        assertEquals("A\nB", runSource(src));
    }

    @Test
    void commentsCanAppearAnywhere() {
        // Updated: comments can be inline or at start of line
        String src = """
            @@ before SUGOD
            SUGOD
              MUGNA NUMERO x=1
              @@ standalone comment
              IPAKITA: x
            KATAPUSAN
            @@ after program
            """;
        assertEquals("1", runSource(src));
    }

    @Test
    void emptyProgramWithOnlyComments() {
        String src = """
            @@ only comments
            SUGOD
              @@ nothing here
            KATAPUSAN
            """;
        assertEquals("", runSource(src));
    }

    // ---- IPAKITA with concatenation & $ -----------------------------------
    @Test
    void printConcatAndDollar() {
        String src = """
            SUGOD
              IPAKITA: "num=" & 7 & $
              IPAKITA: "ok"
            KATAPUSAN
            """;
        assertEquals("num=7\nok", runSource(src));
    }

    @Test
    void printStringLiteralsOnly() {
        String src = """
            SUGOD
              IPAKITA: "Hello" & $
              IPAKITA: "World"
            KATAPUSAN
            """;
        assertEquals("Hello\nWorld", runSource(src));
    }

    @Test
    void printNumbersOnly() {
        String src = """
            SUGOD
              IPAKITA: 42 & $
              IPAKITA: 100
            KATAPUSAN
            """;
        assertEquals("42\n100", runSource(src));
    }

    @Test
    void printMultipleDollarsForNewlines() {
        String src = """
            SUGOD
              IPAKITA: "A" & $ & $ & "B"
            KATAPUSAN
            """;
        assertEquals("A\n\nB", runSource(src));
    }

    @Test
    void printComplexConcatenation() {
        String src = """
            SUGOD
              MUGNA NUMERO x=10
              MUGNA LETRA ch='Z'
              IPAKITA: "x=" & x & " ch=" & ch
            KATAPUSAN
            """;
        assertEquals("x=10 ch=Z", runSource(src));
    }

    // ---- Declarations with optional init (NUMERO, LETRA, TINUOD, TIPIK) ---
    @Test
    void declarationsAndInit() {
        String src = """
            SUGOD
              MUGNA NUMERO x, y, z=5
              MUGNA LETRA ch='c'
              MUGNA TINUOD t="OO"
              IPAKITA: z & $ & ch & $ & t
            KATAPUSAN
            """;
        assertEquals("5\nc\nOO", runSource(src));
    }

    @Test
    void declareTipikWithDecimalValues() {
        String src = """
            SUGOD
              MUGNA TIPIK pi=3.14, e=2.71
              IPAKITA: pi & $ & e
            KATAPUSAN
            """;
        assertEquals("3.14\n2.71", runSource(src));
    }

    @Test
    void declareMultipleVariablesWithoutInit() {
        String src = """
            SUGOD
              MUGNA NUMERO a, b, c
              a=1
              b=2
              c=3
              IPAKITA: a & b & c
            KATAPUSAN
            """;
        assertEquals("123", runSource(src));
    }

    @Test
    void declareAndInitAllDataTypes() {
        String src = """
            SUGOD
              MUGNA NUMERO n=100
              MUGNA LETRA l='X'
              MUGNA TINUOD b="OO"
              MUGNA TIPIK d=99.99
              IPAKITA: n & $ & l & $ & b & $ & d
            KATAPUSAN
            """;
        assertEquals("100\nX\nOO\n99.99", runSource(src));
    }

    @Test
    void declareMixedInitAndUninit() {
        String src = """
            SUGOD
              MUGNA NUMERO x=1, y, z=3
              y=2
              IPAKITA: x & y & z
            KATAPUSAN
            """;
        assertEquals("123", runSource(src));
    }

    // ---- Assignment & right-associative chained assignment ----------------
    @Test
    void chainedAssignment() {
        String src = """
            SUGOD
              MUGNA NUMERO x, y
              x=y=4
              IPAKITA: x & $ & y
            KATAPUSAN
            """;
        assertEquals("4\n4", runSource(src));
    }

    @Test
    void tripleChainedAssignment() {
        String src = """
            SUGOD
              MUGNA NUMERO a, b, c
              a=b=c=7
              IPAKITA: a & $ & b & $ & c
            KATAPUSAN
            """;
        assertEquals("7\n7\n7", runSource(src));
    }

    @Test
    void reassignmentAfterDeclaration() {
        String src = """
            SUGOD
              MUGNA NUMERO x=10
              IPAKITA: x & $
              x=20
              IPAKITA: x
            KATAPUSAN
            """;
        assertEquals("10\n20", runSource(src));
    }

    @Test
    void assignLetraNewValue() {
        String src = """
            SUGOD
              MUGNA LETRA ch='A'
              IPAKITA: ch & $
              ch='B'
              IPAKITA: ch
            KATAPUSAN
            """;
        assertEquals("A\nB", runSource(src));
    }

    @Test
    void assignTinuodToggle() {
        String src = """
            SUGOD
              MUGNA TINUOD flag="OO"
              IPAKITA: flag & $
              flag="DILI"
              IPAKITA: flag
            KATAPUSAN
            """;
        assertEquals("OO\nDILI", runSource(src));
    }

    // ---- Escape codes with [] incl. [[, ]], [&], and $ mixing -------------
    @Test
    void bracketEscapes_exactSpec() {
        String src = """
            SUGOD
            IPAKITA: [[] & $
            IPAKITA: []] & $
            IPAKITA: [&]
            KATAPUSAN
            """;
        assertEquals("[\n]\n&", runSource(src));
    }

    @Test
    void bracketEscapes_leftBracket() {
        String src = """
            SUGOD
              IPAKITA: "open" & [[]
            KATAPUSAN
            """;
        assertEquals("open[", runSource(src));
    }

    @Test
    void bracketEscapes_rightBracket() {
        String src = """
            SUGOD
              IPAKITA: []] & "close"
            KATAPUSAN
            """;
        assertEquals("]close", runSource(src));
    }

    @Test
    void bracketEscapes_ampersand() {
        String src = """
            SUGOD
              IPAKITA: "A" & [&] & "B"
            KATAPUSAN
            """;
        assertEquals("A&B", runSource(src));
    }

    @Test
    void bracketEscapes_multipleBrackets() {
        String src = """
            SUGOD
              IPAKITA: [[] & []] & [[] & []]
            KATAPUSAN
            """;
        assertEquals("[][]", runSource(src));
    }

    @Test
    void bracketEscapes_withVariables() {
        String src = """
            SUGOD
              MUGNA NUMERO x=5
              IPAKITA: [[] & x & []]
            KATAPUSAN
            """;
        assertEquals("[5]", runSource(src));
    }

    // ---- Identifier rules: start letter/_; then letter/_/digit ------------
    @Test
    void identifierRules() {
        String src = """
            SUGOD
              MUGNA NUMERO a_1, _x, y9
              a_1=1
              _x=2
              y9=3
              IPAKITA: a_1 & $ & _x & $ & y9
            KATAPUSAN
            """;
        assertEquals("1\n2\n3", runSource(src));
    }

    @Test
    void identifierStartsWithUnderscore() {
        String src = """
            SUGOD
              MUGNA NUMERO _var=42
              IPAKITA: _var
            KATAPUSAN
            """;
        assertEquals("42", runSource(src));
    }

    @Test
    void identifierWithMultipleUnderscores() {
        String src = """
            SUGOD
              MUGNA NUMERO var_name_123=99
              IPAKITA: var_name_123
            KATAPUSAN
            """;
        assertEquals("99", runSource(src));
    }

    @Test
    void identifierCaseSensitive() {
        String src = """
            SUGOD
              MUGNA NUMERO var=1, Var=2, VAR=3
              IPAKITA: var & Var & VAR
            KATAPUSAN
            """;
        assertEquals("123", runSource(src));
    }

    @Test
    void identifierAllLetters() {
        String src = """
            SUGOD
              MUGNA NUMERO abcdefg=7
              IPAKITA: abcdefg
            KATAPUSAN
            """;
        assertEquals("7", runSource(src));
    }

    @Test
    void identifierMixedCase() {
        String src = """
            SUGOD
              MUGNA NUMERO myVar=10, MyVar=20
              IPAKITA: myVar & $ & MyVar
            KATAPUSAN
            """;
        assertEquals("10\n20", runSource(src));
    }

    // ---- TINUOD storage/print behavior (OO/DILI ↔ true/false) -------------
    @Test
    void tinuodPrintsAsStringsButStoresBoolean() {
        String src = """
            SUGOD
              MUGNA TINUOD t="DILI"
              IPAKITA: t & $
              t="OO"
              IPAKITA: t
            KATAPUSAN
            """;
        assertEquals("DILI\nOO", runSource(src));
    }

    @Test
    void tinuodInitWithOO() {
        String src = """
            SUGOD
              MUGNA TINUOD flag="OO"
              IPAKITA: flag
            KATAPUSAN
            """;
        assertEquals("OO", runSource(src));
    }

    @Test
    void tinuodInitWithDILI() {
        String src = """
            SUGOD
              MUGNA TINUOD flag="DILI"
              IPAKITA: flag
            KATAPUSAN
            """;
        assertEquals("DILI", runSource(src));
    }

    // ---- String concatenation with & operator -----------------------------
    @Test
    void stringConcatenationMultipleStrings() {
        String src = """
            SUGOD
              IPAKITA: "Hello" & " " & "World"
            KATAPUSAN
            """;
        assertEquals("Hello World", runSource(src));
    }

    @Test
    void stringConcatenationWithNumbers() {
        String src = """
            SUGOD
              IPAKITA: "Result: " & 42
            KATAPUSAN
            """;
        assertEquals("Result: 42", runSource(src));
    }

    @Test
    void stringConcatenationWithVariables() {
        String src = """
            SUGOD
              MUGNA NUMERO age=25
              MUGNA LETRA grade='A'
              IPAKITA: "Age: " & age & ", Grade: " & grade
            KATAPUSAN
            """;
        assertEquals("Age: 25, Grade: A", runSource(src));
    }

    // ---- Integration: real sample files ------------------------------------
    @Test
    void sampleHelloFile_runsEndToEnd() throws Exception {
        // Test hello.bpp sample
        String out = runFile("samples/hello.bpp");
        assertEquals("Hi, Bisaya++\nBisaya++ Interpreter", out);
    }

    @Test
    void specificationSampleFile_runsEndToEnd() throws Exception {
        // Test the specification sample program (simple.bpp)
        String out = runFile("samples/simple.bpp");
        assertEquals("4OO5\nc&last", out);
    }

    // ========================================================================
    // NEGATIVE TEST CASES - Error Handling
    // ========================================================================
    // NOTE: All error handling has been implemented! These tests now validate
    // proper error detection and reporting for invalid Bisaya++ code.
    // All 6 previously failing tests now pass with proper error messages.
    // ========================================================================

    @Test
    void error_missingSUGOD() {
        String src = """
            MUGNA NUMERO x=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Should fail without SUGOD");
    }

    @Test
    void error_missingKATAPUSAN() {
        String src = """
            SUGOD
              MUGNA NUMERO x=1
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Should fail without KATAPUSAN");
    }

    @Test
    void error_reservedWordAsIdentifier_MUGNA() {
        String src = """
            SUGOD
              MUGNA NUMERO MUGNA=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use MUGNA as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_IPAKITA() {
        String src = """
            SUGOD
              MUGNA NUMERO IPAKITA=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use IPAKITA as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_SUGOD() {
        String src = """
            SUGOD
              MUGNA NUMERO SUGOD=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use SUGOD as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_KATAPUSAN() {
        String src = """
            SUGOD
              MUGNA NUMERO KATAPUSAN=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use KATAPUSAN as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_NUMERO() {
        String src = """
            SUGOD
              MUGNA NUMERO NUMERO=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use NUMERO as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_LETRA() {
        String src = """
            SUGOD
              MUGNA NUMERO LETRA=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use LETRA as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_TINUOD() {
        String src = """
            SUGOD
              MUGNA NUMERO TINUOD=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use TINUOD as variable name");
    }

    @Test
    void error_reservedWordAsIdentifier_TIPIK() {
        String src = """
            SUGOD
              MUGNA NUMERO TIPIK=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot use TIPIK as variable name");
    }

    @Test
    void error_identifierStartsWithDigit() {
        String src = """
            SUGOD
              MUGNA NUMERO 9var=1
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Identifier cannot start with digit");
    }

    @Test
    void error_undeclaredVariable() {
        String src = """
            SUGOD
              IPAKITA: undefined_var
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Should fail when using undeclared variable");
    }

    @Test
    // FIXED: Implemented runtime check for undeclared variables
    void error_assignToUndeclaredVariable() {
        String src = """
            SUGOD
              x=5
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Should fail when assigning to undeclared variable");
    }

    @Test
    void error_declarationWithoutType() {
        String src = """
            SUGOD
              MUGNA x=5
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Declaration must specify type");
    }

    @Test
    // FIXED: Implemented duplicate variable detection in same declaration
    void error_duplicateDeclarationSameLine() {
        String src = """
            SUGOD
              MUGNA NUMERO x=1, x=2
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Cannot declare same variable twice in one statement");
    }

    @Test
    void error_letraWithMultipleCharacters() {
        String src = """
            SUGOD
              MUGNA LETRA ch='abc'
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "LETRA should only accept single character");
    }

    @Test
    // FIXED: Implemented LETRA validation (must be exactly 1 character)
    void error_letraWithEmptyString() {
        String src = """
            SUGOD
              MUGNA LETRA ch=''
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "LETRA cannot be empty");
    }

    @Test
    void error_tinuodWithInvalidValue() {
        String src = """
            SUGOD
              MUGNA TINUOD flag="YES"
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "TINUOD must be OO or DILI");
    }

    @Test
    // FIXED: Implemented type validation - NUMERO rejects decimal values
    void error_numeroWithDecimalValue() {
        String src = """
            SUGOD
              MUGNA NUMERO n=3.14
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "NUMERO cannot have decimal values");
    }

    @Test
    void error_unclosedStringLiteral() {
        String src = """
            SUGOD
              IPAKITA: "Hello
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "String literal must be closed");
    }

    @Test
    // FIXED: Implemented lexer check for unclosed character literals
    void error_unclosedCharLiteral() {
        String src = """
            SUGOD
              MUGNA LETRA ch='A
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Character literal must be closed");
    }

    @Test
    // FIXED: Implemented validation for escape sequences (only [[, ]], [&] allowed)
    void error_invalidEscapeSequence() {
        String src = """
            SUGOD
              IPAKITA: [X]
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "Invalid escape sequence [X]");
    }

    @Test
    void error_missingSemicolonAfterIPAKITA() {
        // Note: Bisaya++ has "one statement per line" so this might not apply
        // but testing if IPAKITA requires proper syntax
        String src = """
            SUGOD
              IPAKITA "test"
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "IPAKITA requires colon");
    }

    @Test
    void error_emptyIPAKITA() {
        String src = """
            SUGOD
              IPAKITA:
            KATAPUSAN
            """;
        Exception ex = expectError(src);
        assertNotNull(ex, "IPAKITA requires expression");
    }
}
