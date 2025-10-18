package com.bisayapp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class DebugTest {
    public static void main(String[] args) {
        String src = """
            SUGOD
            MUGNA NUMERO x=10
            MUGNA LETRA c='A'
            MUGNA TINUOD result
            result = x > c
            KATAPUSAN
            """;
        
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(output);
            
            Lexer lexer = new Lexer(src);
            List<Token> tokens = lexer.scanTokens();
            
            Parser parser = new Parser(tokens);
            List<Stmt> program = parser.parseProgram();
            
            Interpreter interpreter = new Interpreter(out);
            interpreter.interpret(program);
            
            System.out.println("SUCCESS: " + output.toString());
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("Contains 'type': " + e.getMessage().contains("type"));
            System.out.println("Contains 'compar': " + e.getMessage().contains("compar"));
        }
    }
}
