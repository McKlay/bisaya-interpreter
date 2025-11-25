package com.bisayapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for IOHandler implementations and DAWAT command with GUI support
 */
public class IOHandlerTest {
    
    @Test
    @DisplayName("ConsoleIOHandler - Basic output")
    public void testConsoleOutput() {
        ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outCapture);
        
        ConsoleIOHandler handler = new ConsoleIOHandler(out, System.err, System.in);
        handler.writeOutput("Hello");
        handler.writeOutput(" World");
        
        assertEquals("Hello World", outCapture.toString());
    }
    
    @Test
    @DisplayName("ConsoleIOHandler - Basic input")
    public void testConsoleInput() {
        ByteArrayInputStream in = new ByteArrayInputStream("42\n".getBytes(StandardCharsets.UTF_8));
        ConsoleIOHandler handler = new ConsoleIOHandler(System.out, System.err, in);
        
        assertTrue(handler.hasInput());
        String input = handler.readInput("Enter a number:");
        assertEquals("42", input);
    }
    
    @Test
    @DisplayName("ConsoleIOHandler - Multiple inputs")
    public void testConsoleMultipleInputs() {
        String inputData = "10, 20, 30\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
        ConsoleIOHandler handler = new ConsoleIOHandler(System.out, System.err, in);
        
        String input = handler.readInput("Enter values:");
        assertEquals("10, 20, 30", input);
    }
    
    @Test
    @DisplayName("DAWAT with ConsoleIOHandler - Simple input")
    public void testDawatWithConsoleHandler() throws Exception {
        String code = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x
                IPAKITA: x
            KATAPUSAN
            """;
        
        ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
        ByteArrayInputStream inData = new ByteArrayInputStream("42\n".getBytes(StandardCharsets.UTF_8));
        
        ConsoleIOHandler handler = new ConsoleIOHandler(
            new PrintStream(outCapture), 
            System.err, 
            inData
        );
        
        Bisaya.runSource(code, handler);
        
        assertEquals("42", outCapture.toString());
    }
    
    @Test
    @DisplayName("DAWAT with ConsoleIOHandler - Multiple variables")
    public void testDawatMultipleVariables() throws Exception {
        String code = """
            SUGOD
                MUGNA NUMERO x, y
                DAWAT: x, y
                IPAKITA: x & " " & y
            KATAPUSAN
            """;
        
        ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
        ByteArrayInputStream inData = new ByteArrayInputStream("10, 20\n".getBytes(StandardCharsets.UTF_8));
        
        ConsoleIOHandler handler = new ConsoleIOHandler(
            new PrintStream(outCapture), 
            System.err, 
            inData
        );
        
        Bisaya.runSource(code, handler);
        
        assertEquals("10 20", outCapture.toString());
    }
    
    @Test
    @DisplayName("DAWAT with ConsoleIOHandler - Different types")
    public void testDawatDifferentTypes() throws Exception {
        String code = """
            SUGOD
                MUGNA NUMERO num
                MUGNA LETRA ch
                MUGNA TINUOD flag
                DAWAT: num, ch, flag
                IPAKITA: num & " " & ch & " " & flag
            KATAPUSAN
            """;
        
        ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
        ByteArrayInputStream inData = new ByteArrayInputStream("42, X, OO\n".getBytes(StandardCharsets.UTF_8));
        
        ConsoleIOHandler handler = new ConsoleIOHandler(
            new PrintStream(outCapture), 
            System.err, 
            inData
        );
        
        Bisaya.runSource(code, handler);
        
        assertEquals("42 X OO", outCapture.toString());
    }
    
    @Test
    @DisplayName("DAWAT error - Input cancelled")
    public void testDawatCancelled() {
        String code = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x
            KATAPUSAN
            """;
        
        // Create a handler that throws on input (simulating cancellation)
        IOHandler handler = new IOHandler() {
            @Override
            public void writeOutput(String text) {}
            
            @Override
            public void writeError(String error) {}
            
            @Override
            public String readInput(String prompt) {
                throw new RuntimeException("Input cancelled by user");
            }
            
            @Override
            public boolean hasInput() {
                return true;
            }
        };
        
        assertThrows(Exception.class, () -> Bisaya.runSource(code, handler));
    }
    
    @Test
    @DisplayName("Backward compatibility - Old constructor still works")
    public void testBackwardCompatibility() throws Exception {
        String code = """
            SUGOD
                MUGNA NUMERO x
                DAWAT: x
                IPAKITA: x
            KATAPUSAN
            """;
        
        ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
        ByteArrayInputStream inData = new ByteArrayInputStream("99\n".getBytes(StandardCharsets.UTF_8));
        
        // Use old-style constructor (should still work)
        Bisaya.runSource(code, new PrintStream(outCapture), inData);
        
        assertEquals("99", outCapture.toString());
    }
}
