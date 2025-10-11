package com.bisayapp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Bisaya {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: bisaya <source.bpp>");
            System.exit(64);
        }

        String source = Files.readString(Path.of(args[0]));
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        System.out.println("=== LEXICAL ANALYSIS ===");
        System.out.println("[Lex] token count = " + tokens.size());
        for (Token t : tokens) {
            System.out.println("  " + t);
        }
        System.out.println();

        if (ErrorReporter.hadError()) System.exit(65);

        Parser parser = new Parser(tokens);
        List<Stmt> program = parser.parseProgram();
        if (ErrorReporter.hadError()) System.exit(65);

        System.out.println("=== PROGRAM OUTPUT ===");
        new Interpreter(System.out).interpret(program);
    }
}
