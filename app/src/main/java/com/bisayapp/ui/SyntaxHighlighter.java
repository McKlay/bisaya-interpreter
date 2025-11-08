package com.bisayapp.ui;

import com.bisayapp.Lexer;
import com.bisayapp.Token;
import com.bisayapp.TokenType;
import javafx.application.Platform;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.*;

/**
 * Syntax Highlighter for Bisaya++ Code
 * 
 * Integrates with Lexer to recognize tokens and apply color highlighting
 * Uses RichTextFX CodeArea for multi-color text rendering
 */
public class SyntaxHighlighter {
    
    private final CodeArea codeArea;
    
    // CSS style classes for different token types
    private static final String KEYWORD_CLASS = "keyword";
    private static final String STRING_CLASS = "string";
    private static final String NUMBER_CLASS = "number";
    private static final String COMMENT_CLASS = "comment";
    private static final String OPERATOR_CLASS = "operator";
    
    public SyntaxHighlighter(CodeArea codeArea) {
        this.codeArea = codeArea;
        setupStylesheet();
        setupListener();
        setupCurrentLineHighlight();
    }
    
    /**
     * Sets up the CSS stylesheet for syntax highlighting
     */
    private void setupStylesheet() {
        String stylesheet = getClass().getResource("/bisaya-syntax.css").toExternalForm();
        if (stylesheet != null) {
            codeArea.getStylesheets().add(stylesheet);
        } else {
            // Fallback: apply inline styles
            applyInlineStyles();
        }
    }
    
    /**
     * Apply inline styles if CSS file not found
     */
    private void applyInlineStyles() {
        codeArea.setStyle(codeArea.getStyle() + 
            ".keyword { -fx-fill: #0000FF; -fx-font-weight: bold; }" +
            ".string { -fx-fill: #008000; }" +
            ".number { -fx-fill: #FF8C00; }" +
            ".comment { -fx-fill: #808080; -fx-font-style: italic; }" +
            ".operator { -fx-fill: #666666; }" +
            ".current-line { -fx-background-color: #E8F4FF; }"
        );
    }
    
    /**
     * Sets up text change listener with debouncing
     */
    private void setupListener() {
        // Use RichTextFX's multiPlainChanges for efficient highlighting
        codeArea.multiPlainChanges()
            .successionEnds(Duration.ofMillis(300))
            .subscribe(ignore -> highlightSyntax());
    }
    
    /**
     * Sets up current line highlighting
     */
    private void setupCurrentLineHighlight() {
        // Disabled: Paragraph highlighting interferes with text selection
        // Users should be able to select specific text portions, not whole lines
        // The darker background made it hard to see selected text
        
        /* 
        codeArea.currentParagraphProperty().addListener((obs, oldPara, newPara) -> {
            highlightCurrentLine(newPara);
        });
        */
    }
    
    /**
     * Highlights the current line with a background color
     * Currently disabled to allow proper text selection
     */
    private void highlightCurrentLine(int paragraph) {
        /*
        // Clear previous current line highlight
        for (int i = 0; i < codeArea.getParagraphs().size(); i++) {
            codeArea.clearParagraphStyle(i);
        }
        
        // Set current line highlight
        if (paragraph >= 0 && paragraph < codeArea.getParagraphs().size()) {
            codeArea.setParagraphStyle(paragraph, Collections.singletonList("current-line"));
        }
        */
    }
    
    /**
     * Performs syntax highlighting using Lexer for token recognition
     */
    private void highlightSyntax() {
        String code = codeArea.getText();
        if (code == null || code.isEmpty()) {
            return;
        }
        
        try {
            // Use Lexer to tokenize the code
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.scanTokens();
            
            // Build style spans based on tokens
            StyleSpans<Collection<String>> highlighting = computeHighlighting(tokens, code);
            
            // Apply highlighting without moving caret
            int caretPosition = codeArea.getCaretPosition();
            codeArea.setStyleSpans(0, highlighting);
            codeArea.moveTo(caretPosition);
            
        } catch (Exception e) {
            // Print error for debugging
            System.err.println("Syntax highlighting error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Computes style spans for syntax highlighting using regex patterns
     * This is more reliable than token position mapping
     */
    private StyleSpans<Collection<String>> computeHighlighting(List<Token> tokens, String code) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        int codeLength = code.length();
        if (codeLength == 0) {
            spansBuilder.add(Collections.emptyList(), 0);
            return spansBuilder.create();
        }
        
        // Create a character-by-character style map
        String[] styles = new String[codeLength];
        Arrays.fill(styles, ""); // Default: no style
        
        // Define keyword pattern
        String keywordPattern = "\\b(SUGOD|KATAPUSAN|MUGNA|NUMERO|LETRA|TINUOD|TIPIK|IPAKITA|DAWAT|KUNG|WALA|DILI|PUNDOK|ALANG|SA|SAMTANG|UG|O)\\b";
        
        // 1. Mark comments first (@@) - highest priority
        java.util.regex.Pattern commentPattern = java.util.regex.Pattern.compile("@@.*");
        java.util.regex.Matcher commentMatcher = commentPattern.matcher(code);
        while (commentMatcher.find()) {
            for (int i = commentMatcher.start(); i < commentMatcher.end() && i < codeLength; i++) {
                styles[i] = COMMENT_CLASS;
            }
        }
        
        // 2. Mark strings (must handle both single and double quotes)
        java.util.regex.Pattern stringPattern = java.util.regex.Pattern.compile("\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'");
        java.util.regex.Matcher stringMatcher = stringPattern.matcher(code);
        while (stringMatcher.find()) {
            for (int i = stringMatcher.start(); i < stringMatcher.end() && i < codeLength; i++) {
                // Don't override comments
                if (!COMMENT_CLASS.equals(styles[i])) {
                    styles[i] = STRING_CLASS;
                }
            }
        }
        
        // 3. Mark numbers
        java.util.regex.Pattern numberPattern = java.util.regex.Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
        java.util.regex.Matcher numberMatcher = numberPattern.matcher(code);
        while (numberMatcher.find()) {
            for (int i = numberMatcher.start(); i < numberMatcher.end() && i < codeLength; i++) {
                // Don't override comments or strings
                if (!COMMENT_CLASS.equals(styles[i]) && !STRING_CLASS.equals(styles[i])) {
                    styles[i] = NUMBER_CLASS;
                }
            }
        }
        
        // 4. Mark keywords
        java.util.regex.Pattern keyPattern = java.util.regex.Pattern.compile(keywordPattern);
        java.util.regex.Matcher keyMatcher = keyPattern.matcher(code);
        while (keyMatcher.find()) {
            for (int i = keyMatcher.start(); i < keyMatcher.end() && i < codeLength; i++) {
                // Don't override comments, strings, or numbers
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i])) {
                    styles[i] = KEYWORD_CLASS;
                }
            }
        }
        
        // 5. Mark operators
        java.util.regex.Pattern operatorPattern = java.util.regex.Pattern.compile("[+\\-*/%=<>!&|(){}\\[\\]:,;]");
        java.util.regex.Matcher operatorMatcher = operatorPattern.matcher(code);
        while (operatorMatcher.find()) {
            for (int i = operatorMatcher.start(); i < operatorMatcher.end() && i < codeLength; i++) {
                // Don't override anything else
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i]) && 
                    !KEYWORD_CLASS.equals(styles[i])) {
                    styles[i] = OPERATOR_CLASS;
                }
            }
        }
        
        // Build StyleSpans from the style map
        int i = 0;
        while (i < codeLength) {
            String currentStyle = styles[i];
            int spanStart = i;
            
            // Find the end of this style span
            while (i < codeLength && styles[i].equals(currentStyle)) {
                i++;
            }
            
            int spanLength = i - spanStart;
            if (currentStyle.isEmpty()) {
                spansBuilder.add(Collections.emptyList(), spanLength);
            } else {
                spansBuilder.add(Collections.singleton(currentStyle), spanLength);
            }
        }
        
        return spansBuilder.create();
    }
    
    /**
     * Finds all comment ranges in the code
     */
    private List<int[]> findComments(String code) {
        List<int[]> ranges = new ArrayList<>();
        String[] lines = code.split("\n", -1);
        int pos = 0;
        
        for (String line : lines) {
            int commentStart = line.indexOf("@@");
            if (commentStart >= 0) {
                int absoluteStart = pos + commentStart;
                int absoluteEnd = pos + line.length();
                ranges.add(new int[]{absoluteStart, absoluteEnd});
            }
            pos += line.length() + 1; // +1 for newline
        }
        
        return ranges;
    }
    
    /**
     * Checks if position is within a comment range
     */
    private boolean isInCommentRange(int pos, List<int[]> commentRanges) {
        for (int[] range : commentRanges) {
            if (pos >= range[0] && pos < range[1]) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds styled gap between tokens
     */
    private void addStyledGap(StyleSpansBuilder<Collection<String>> spansBuilder, 
                              String code, int start, int end, List<int[]> commentRanges) {
        int pos = start;
        while (pos < end) {
            // Find next comment boundary
            int nextBoundary = end;
            boolean inComment = false;
            
            for (int[] range : commentRanges) {
                if (pos < range[0] && range[0] < nextBoundary) {
                    nextBoundary = range[0];
                } else if (pos >= range[0] && pos < range[1]) {
                    inComment = true;
                    nextBoundary = Math.min(range[1], end);
                    break;
                }
            }
            
            int length = nextBoundary - pos;
            if (length > 0) {
                if (inComment) {
                    spansBuilder.add(Collections.singleton(COMMENT_CLASS), length);
                } else {
                    spansBuilder.add(Collections.emptyList(), length);
                }
            }
            pos = nextBoundary;
        }
    }
    
    /**
     * Finds the position of a token in the source code
     */
    private int findTokenPosition(String code, Token token, int startFrom) {
        // Search for token lexeme starting from lastPos
        // We need to match line/col from token
        String[] lines = code.split("\n", -1);
        int pos = 0;
        
        for (int i = 0; i < lines.length && i < token.line; i++) {
            if (i == token.line - 1) {
                // Found the line, add column offset
                return pos + token.col - 1;
            }
            pos += lines[i].length() + 1; // +1 for newline
        }
        
        return -1;
    }
    
    /**
     * Gets CSS style class for a token type
     */
    private String getStyleClass(TokenType type) {
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
                return KEYWORD_CLASS;
                
            case STRING:
                return STRING_CLASS;
                
            case NUMBER:
                return NUMBER_CLASS;
                
            case PLUS:
            case MINUS:
            case STAR:
            case SLASH:
            case PERCENT:
            case EQUAL:
            case EQUAL_EQUAL:
            case LT_GT: // <> not equal
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case AMPERSAND:
                return OPERATOR_CLASS;
                
            default:
                return null;
        }
    }
    
    /**
     * Applies immediate highlighting (useful for initial load)
     */
    public void applyHighlighting() {
        Platform.runLater(this::highlightSyntax);
    }
}
