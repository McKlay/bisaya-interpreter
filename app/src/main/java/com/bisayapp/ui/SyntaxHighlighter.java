package com.bisayapp.ui;

import com.bisayapp.Lexer;
import com.bisayapp.Token;
import com.bisayapp.TokenType;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Syntax Highlighter for Bisaya++ Code
 * 
 * Integrates with Lexer to recognize tokens and apply color highlighting
 * Uses debouncing to avoid lag while typing
 */
public class SyntaxHighlighter {
    
    private final TextArea textArea;
    private Timer debounceTimer;
    private static final long DEBOUNCE_DELAY = 300; // milliseconds
    
    // ANSI-like color codes (hex colors)
    private static final String KEYWORD_COLOR = "#0000FF";      // Blue
    private static final String STRING_COLOR = "#008000";       // Green
    private static final String NUMBER_COLOR = "#FF8C00";       // Orange
    private static final String COMMENT_COLOR = "#808080";      // Gray
    private static final String DEFAULT_COLOR = "#000000";      // Black
    
    public SyntaxHighlighter(TextArea textArea) {
        this.textArea = textArea;
        setupListener();
    }
    
    /**
     * Sets up text change listener with debouncing
     */
    private void setupListener() {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            debounceHighlight();
        });
    }
    
    /**
     * Debounces the highlighting to avoid lag while typing
     */
    private void debounceHighlight() {
        if (debounceTimer != null) {
            debounceTimer.cancel();
        }
        
        debounceTimer = new Timer(true);
        debounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> highlightSyntax());
            }
        }, DEBOUNCE_DELAY);
    }
    
    /**
     * Performs syntax highlighting using Lexer for token recognition
     * Note: TextArea doesn't support native multi-color text.
     * This is a placeholder for future RichTextFX integration.
     */
    private void highlightSyntax() {
        String code = textArea.getText();
        if (code == null || code.isEmpty()) {
            return;
        }
        
        try {
            // Use Lexer to tokenize the code
            Lexer lexer = new Lexer(code);
            @SuppressWarnings("unused")
            List<Token> tokens = lexer.scanTokens();
            
            // For now, we can't actually color TextArea text differently
            // This would require RichTextFX or similar library
            // This method is here as a foundation for future enhancement
            
            // TODO: Integrate RichTextFX for actual multi-color highlighting
            // For now, this validates syntax in background
            
        } catch (Exception e) {
            // Silently fail - don't interrupt user typing
        }
    }
    
    /**
     * Gets color for a token type
     */
    private String getColorForTokenType(TokenType type) {
        switch (type) {
            case SUGOD:
            case KATAPUSAN:
            case MUGNA:
            case NUMERO:
            case LETRA:
            case TINUOD:
            case TIPIK:
            case IPAKITA:
            case DAWAT:
            case KUNG:
            case WALA:
            case DILI:
            case PUNDOK:
            case ALANG:
            case SA:
            case SAMTANG:
            case UG:
            case O:
                return KEYWORD_COLOR;
                
            case STRING:
                return STRING_COLOR;
                
            case NUMBER:
                return NUMBER_COLOR;
                
            default:
                return DEFAULT_COLOR;
        }
    }
    
    /**
     * Applies immediate highlighting (useful for initial load)
     */
    public void applyHighlighting() {
        highlightSyntax();
    }
    
    /**
     * Cancels any pending highlight operations
     */
    public void cancel() {
        if (debounceTimer != null) {
            debounceTimer.cancel();
        }
    }
}
