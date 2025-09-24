package com.bisayapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ReservedWordsAndIdentifiersTest {
    private Exception parseError(String src) {
        try {
            var tokens = new Lexer(src).scanTokens();
            new Parser(tokens).parseProgram();
            return null;
        } catch (RuntimeException ex) {
            return ex;
        }
    }

    @Test
    void reserved_words_not_identifiers() {
        String src = """
        SUGOD
          MUGNA NUMERO IPAKITA=1
        KATAPUSAN
        """;
        var err = parseError(src);
        assertNotNull(err); // should fail to parse/lex
    }
}
