package com.bisayapp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class TestDecrementExpr {
    public static void main(String[] args) {
        String src = """
            SUGOD
            MUGNA NUMERO x=10, y=5
            --(x + y)
            KATAPUSAN
            """;
        
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(output);
            
            Lexer lexer = new Lexer(src);
            List<Token> tokens = lexer.scanTokens();
            
            System.out.println("=== TOKENS ===");
            for (Token t : tokens) {
                System.out.println(t);
            }
            
            Parser parser = new Parser(tokens);
            List<Stmt> program = parser.parseProgram();
            
            System.out.println("\n=== PARSED OK ===");
            System.out.println("Statements: " + program.size());
            
            Interpreter interpreter = new Interpreter(out);
            interpreter.interpret(program);
            
            System.out.println("\n=== EXECUTED OK ===");
            System.out.println("Output: " + output.toString());
            System.out.println("\nERROR: Should have thrown exception!");
            
        } catch (RuntimeException e) {
            System.out.println("\n=== EXCEPTION THROWN (GOOD!) ===");
            System.out.println("Type: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n=== UNEXPECTED EXCEPTION ===");
            System.out.println("Type: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
