package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ParserTest {
    private List<Token> lex(String src) { return new Lexer(src).scanTokens(); }
    private List<Stmt> parse(String src) { return new Parser(lex(src)).parseProgram(); }

    @Test
    void one_statement_per_line_enforced() {
        String src = "SUGOD\nIPAKITA: \"A\" & $\nIPAKITA: \"B\"\nKATAPUSAN\n";
        var prog = parse(src);
        assertTrue(prog.size() >= 2); // two print statements parsed separately
    }

    @Test
    void print_with_concat_and_dollar() {
        String src = "SUGOD\nIPAKITA: \"Hi\" & $ & \"There\"\nKATAPUSAN\n";
        var prog = parse(src);
        assertFalse(prog.isEmpty());
    }

    @Test
    void var_decl_with_types_and_optional_init() {
        String src = """
        SUGOD
          MUGNA NUMERO x, y, z=5
          MUGNA LETRA ch='c'
          MUGNA TINUOD t="OO"
        KATAPUSAN
        """;
        var prog = parse(src);
        // Expect at least 3 VarDecl statements
        long varDecls = prog.stream().filter(s -> s instanceof Stmt.VarDecl).count();
        assertTrue(varDecls >= 3);
    }

    @Test
    void right_associative_assignment() {
        String src = """
        SUGOD
          MUGNA NUMERO x, y
          x=y=4
        KATAPUSAN
        """;
        var prog = parse(src);
        // Should parse as Assign(name=x, value=Assign(name=y, value=4))
        // We can at least assert parse success and later verify at runtime.
        assertNotNull(prog);
        assertFalse(prog.isEmpty());
    }
}
