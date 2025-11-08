package com.bisayapp;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Bisaya {
    /**
     * Main CLI entry point
     */
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

        System.out.println("Tokenized & Parsed Successfully");
        System.out.println("=== PROGRAM OUTPUT ===");
        new Interpreter(System.out, System.in).interpret(program);
    }
    
    /**
     * Run a Bisaya++ file programmatically (used by GUI)
     * @param filePath Path to the .bpp file
     * @param out Output stream for program output
     * @param in Input stream for program input
     * @throws Exception if there are errors during execution
     */
    public static void runFile(String filePath, PrintStream out, InputStream in) throws Exception {
        // Reset error state
        ErrorReporter.reset();
        
        String source = Files.readString(Path.of(filePath));
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        if (ErrorReporter.hadError()) {
            throw new RuntimeException("Lexical errors found");
        }

        Parser parser = new Parser(tokens);
        List<Stmt> program = parser.parseProgram();
        
        if (ErrorReporter.hadError()) {
            throw new RuntimeException("Syntax errors found");
        }

        new Interpreter(out, in).interpret(program);
    }
    
    /**
     * Run Bisaya++ source code directly (used by GUI)
     * @param source The Bisaya++ source code
     * @param out Output stream for program output
     * @param in Input stream for program input
     * @throws Exception if there are errors during execution
     */
    public static void runSource(String source, PrintStream out, InputStream in) throws Exception {
        // Reset error state
        ErrorReporter.reset();
        
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        if (ErrorReporter.hadError()) {
            throw new RuntimeException("Lexical errors found");
        }

        Parser parser = new Parser(tokens);
        List<Stmt> program = parser.parseProgram();
        
        if (ErrorReporter.hadError()) {
            throw new RuntimeException("Syntax errors found");
        }

        new Interpreter(out, in).interpret(program);
    }
}
