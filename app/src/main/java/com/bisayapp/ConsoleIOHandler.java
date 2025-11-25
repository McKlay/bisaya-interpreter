package com.bisayapp;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * ConsoleIOHandler
 * 
 * CLI implementation of IOHandler for backward compatibility.
 * Used by the command-line version of Bisaya++ interpreter.
 * 
 * Features:
 * - Writes to System.out/System.err
 * - Reads from System.in using Scanner
 * - Maintains existing CLI behavior
 */
public class ConsoleIOHandler implements IOHandler {
    
    private final PrintStream out;
    private final PrintStream err;
    private final Scanner scanner;
    
    /**
     * Creates handler with default streams
     */
    public ConsoleIOHandler() {
        this(System.out, System.err, System.in);
    }
    
    /**
     * Creates handler with custom streams (for testing)
     * 
     * @param out Output stream for normal output
     * @param err Output stream for errors
     * @param in Input stream for reading input
     */
    public ConsoleIOHandler(PrintStream out, PrintStream err, InputStream in) {
        this.out = out;
        this.err = err;
        this.scanner = new Scanner(in);
    }
    
    @Override
    public void writeOutput(String text) {
        out.print(text);
    }
    
    @Override
    public void writeError(String error) {
        err.println(error);
    }
    
    @Override
    public String readInput(String prompt) {
        if (!scanner.hasNextLine()) {
            throw new RuntimeException("No input available");
        }
        return scanner.nextLine().trim();
    }
    
    @Override
    public boolean hasInput() {
        return scanner.hasNextLine();
    }
}
