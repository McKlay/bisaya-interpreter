package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class VarDeclAssignTest {
    private String run(String src) {
        var tokens = new Lexer(src).scanTokens();
        var stmts = new Parser(tokens).parseProgram();
        var out = new ByteArrayOutputStream();
        var interp = new Interpreter(new PrintStream(out));
        interp.interpret(stmts);
        return out.toString().replace("\r\n","\n").trim();
    }

    @Test
    void numero_tipik_letra_tinuod_assignment_and_print() {
        String src = """
        SUGOD
          MUGNA NUMERO n=5
          MUGNA TIPIK d
          MUGNA LETRA ch='c'
          MUGNA TINUOD t="OO"
          d = 3.14
          IPAKITA: n & $ & ch & $ & t
        KATAPUSAN
        """;
        String out = run(src);
        // Expect: "5" newline "c" newline "OO"
        assertEquals("5\nc\nOO", out);
    }

    @Test
    void chained_assignment_right_associative_and_print() {
        String src = """
        SUGOD
          MUGNA NUMERO x, y
          x=y=4
          IPAKITA: x & $ & y
        KATAPUSAN
        """;
        String out = run(src);
        assertEquals("4\n4", out);
    }
}
