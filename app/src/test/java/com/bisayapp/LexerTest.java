package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class LexerTest {
    private List<Token> lex(String src) {
        return new Lexer(src).scanTokens();
    }

    @Test
    void programMarkers_and_Newline() {
        var toks = lex("SUGOD\nKATAPUSAN\n");
        assertEquals(TokenType.SUGOD, toks.get(0).type);
        assertEquals(TokenType.KATAPUSAN, toks.get(2).type);
        assertEquals(TokenType.EOF, toks.get(toks.size()-1).type);
    }

    @Test
    void comments_are_ignored_until_eol() {
        var toks = lex("SUGOD\n-- comment here\nKATAPUSAN\n");
        assertTrue(toks.stream().anyMatch(t -> t.type == TokenType.SUGOD));
        assertTrue(toks.stream().anyMatch(t -> t.type == TokenType.KATAPUSAN));
        // No token produced for comment body
    }

    @Test
    void concat_and_dollar_as_operand() {
        var toks = lex("IPAKITA: \"a\" & $ & \"b\"\n");
        assertTrue(toks.stream().anyMatch(t -> t.type == TokenType.DOLLAR));
        assertTrue(toks.stream().anyMatch(t -> t.type == TokenType.AMPERSAND));
    }

    @Test
    void bracket_escapes_to_string_token() {
        var toks = lex("IPAKITA: [&] & [[] & []]\n");
        // Expect three STRING tokens with lexemes "[&]" "[[" "]]" (or literal values "&", "[", "]")
        long strCount = toks.stream().filter(t -> t.type == TokenType.STRING).count();
        assertTrue(strCount >= 3);
    }

    @Test
    void identifiers_follow_spec_rule() {
        var ok = lex("MUGNA NUMERO a_1, _x, y9\n");
        // Should have IDENTIFIER tokens a_1, _x, y9
        assertTrue(ok.stream().filter(t -> t.type == TokenType.IDENTIFIER).count() >= 3);

        var bad = lex("MUGNA NUMERO 9abc\n");
        // Depending on your lexer, this may split into NUMBER + IDENTIFIER or error-report.
        // We at least ensure it does NOT become a single IDENTIFIER:
        long ident = bad.stream().filter(t -> t.type == TokenType.IDENTIFIER && "9abc".equals(t.lexeme)).count();
        assertEquals(0, ident);
    }
}
