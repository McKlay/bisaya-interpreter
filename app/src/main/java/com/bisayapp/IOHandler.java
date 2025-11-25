package com.bisayapp;

/**
 * IOHandler Interface
 * 
 * Abstracts input/output operations for the Bisaya++ interpreter.
 * This allows the interpreter to work with different I/O backends:
 * - Console (CLI)
 * - GUI dialogs
 * - Test mocks
 * 
 * Benefits:
 * - Decouples interpreter from specific I/O implementation
 * - Enables GUI support for DAWAT command
 * - Simplifies testing with mock implementations
 */
public interface IOHandler {
    
    /**
     * Writes normal program output
     * 
     * @param text The text to output (handles $ for newlines, & for concatenation)
     */
    void writeOutput(String text);
    
    /**
     * Writes error messages
     * 
     * @param error The error message to display
     */
    void writeError(String error);
    
    /**
     * Reads input from user
     * 
     * @param prompt The prompt message to show (e.g., "Enter values for: x, y")
     * @return The user's input as a string (comma-separated for multiple values)
     * @throws RuntimeException if input is cancelled or unavailable
     */
    String readInput(String prompt);
    
    /**
     * Checks if input is available
     * 
     * @return true if input can be read, false otherwise
     */
    boolean hasInput();
}
