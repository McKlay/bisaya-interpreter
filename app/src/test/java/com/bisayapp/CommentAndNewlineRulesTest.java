package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CommentAndNewlineRulesTest {
    private String run(String src) {
        var tokens = new Lexer(src).scanTokens();
        var stmts = new Parser(tokens).parseProgram();
        var out = new ByteArrayOutputStream();
        new Interpreter(new PrintStream(out)).interpret(stmts);
        return out.toString().replace("\r\n","\n").trim();
    }

    @Test
    void comments_anywhere_and_statement_per_line() {
        String src = """
        SUGOD
          -- before
          IPAKITA: "A" & $
          -- this is now a standalone comment
          IPAKITA: "B"
          -- after
        KATAPUSAN
        """;
        String out = run(src);
        assertEquals("A\nB", out);
    }
}
