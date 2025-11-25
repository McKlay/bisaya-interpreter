package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class InterpreterPrintTest {
    private String run(String src) {
        var tokens = new Lexer(src).scanTokens();
        var stmts = new Parser(tokens).parseProgram();
        var out = new ByteArrayOutputStream();
        var ps = new PrintStream(out);
        var interp = new Interpreter(ps);
        interp.interpret(stmts);
        return out.toString().replace("\r\n","\n");
    }

    @Test
    void spec_sample_runs_and_matches() {
        String src = """
        @@ this is a sample program in Bisaya++
        SUGOD
        MUGNA NUMERO x, y, z=5
        MUGNA LETRA a_1='n'
        MUGNA TINUOD t="OO"

        x=y=4
        a_1='c'
        @@ this is a comment

        IPAKITA: x & t & z & $ & a_1 & [&] & "last"
        KATAPUSAN
        """;
        String out = run(src).trim();
        // Expected (from spec): "4OO5" then new line "c&last"
        // Runtime may print without extra spaces:
        assertEquals("4OO5\nc&last", out);
    }

    @Test
    void basic_print_concat_and_dollar() {
        String src = """
        SUGOD
          IPAKITA: "num=" & 7 & $
          IPAKITA: "ok"
        KATAPUSAN
        """;
        String out = run(src).trim();
        assertEquals("num=7\nok", out);
    }
}
