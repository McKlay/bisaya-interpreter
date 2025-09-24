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

    // ---- Program structure & comments -------------------------------------
    @Test
    void programMarkersAndComments() {
        String src = """
            -- header comment
            SUGOD
              -- inside
              IPAKITA: "A" & $
              IPAKITA: "B"
              -- trailer
            KATAPUSAN
            """;
        assertEquals("A\nB", runSource(src));
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

    // ---- Declarations with optional init (NUMERO, LETRA, TINUOD) ----------
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

    // ---- Escape codes with [] incl. [[, ]], [&], and $ mixing -------------
    @Test
    void bracketEscapes_exactSpec() {
        String src = """
            SUGOD
            IPAKITA: [[ & $
            IPAKITA: []]
            IPAKITA: [&]
            KATAPUSAN
            """;
        assertEquals("[\n]\n&", runSource(src));
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

    // ---- TINUOD storage/print behavior (OO/DILI ↔ true/false) -------------
    @Test
    void tinuodPrintsAsStringsButStoresBoolean() {
        String src = """
            SUGOD
              MUGNA TINUOD t="DILI"
              IPAKITA: t
              t="OO"
              IPAKITA: t
            KATAPUSAN
            """;
        assertEquals("DILI\nOO", runSource(src));
    }

    // ---- Integration: real sample file ------------------------------------
    @Test
    void sampleHelloFile_runsEndToEnd() throws Exception {
        // Adjust if your sample path differs:
        String out = runFile("app/samples/hello.bpp");
        assertEquals("Hi, Bisaya++\nBisaya++ Interpreter", out);
    }
}
