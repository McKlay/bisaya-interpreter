package com.bisayapp.ui;

/**
 * Configuration constants for Bisaya++ IDE
 * 
 * Centralizes all configuration values for easy maintenance
 */
public class IDEConfig {
    
    // Window Configuration
    public static final String WINDOW_TITLE = "Bisaya++ IDE";
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final double DIVIDER_POSITION = 0.6; // 60% editor, 40% output
    
    // Font Configuration
    public static final String EDITOR_FONT = "Consolas";
    public static final int EDITOR_FONT_SIZE = 10;
    public static final String OUTPUT_FONT = "Consolas";
    public static final int OUTPUT_FONT_SIZE = 10;
    
    // Color Scheme
    public static final String EDITOR_BG_COLOR = "#ffffff";
    public static final String EDITOR_TEXT_COLOR = "#000000";
    public static final String OUTPUT_BG_COLOR = "#2b2b2b";
    public static final String OUTPUT_TEXT_COLOR = "#00ff00";
    public static final String ERROR_TEXT_COLOR = "#ff4444";
    public static final String WARNING_TEXT_COLOR = "#ffaa00";
    
    // UI Messages
    public static final String EDITOR_PROMPT = "Write your Bisaya++ code here...\n\nExample:\nSUGOD\n    MUGNA NUMERO x=5\n    IPAKITA: \"Hello, Bisaya++\" & $\n    IPAKITA: \"x = \" & x\nKATAPUSAN";
    public static final String OUTPUT_PROMPT = "Program output will appear here...";
    public static final String SUCCESS_MESSAGE = "✓ Program executed successfully (no output).";
    public static final String ERROR_PREFIX = "❌ Error:\n\n";
    
    // File Configuration
    public static final String FILE_EXTENSION = "*.bpp";
    public static final String FILE_DESCRIPTION = "Bisaya++ Files";
    public static final String SAMPLES_DIR = "samples";
    
    // Version
    public static final String VERSION = "Bisaya++ v1.0";
    
    private IDEConfig() {
        // Prevent instantiation
    }
}
