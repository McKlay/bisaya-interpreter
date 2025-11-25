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
    private static final String KEYWORD_CLASS = "keyword";       // Control flow (KUNG, SAMTANG, ALANG, etc.)
    private static final String BUILTIN_CLASS = "builtin";       // Built-in functions (IPAKITA, DAWAT, MUGNA)
    private static final String DATATYPE_CLASS = "datatype";     // Data types (NUMERO, LETRA, TINUOD, TIPIK)
    private static final String STRUCTURE_CLASS = "structure";   // Program structure (SUGOD, KATAPUSAN)
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
            ".builtin { -fx-fill: #8B008B; -fx-font-weight: bold; }" +
            ".datatype { -fx-fill: #008080; -fx-font-weight: bold; }" +
            ".structure { -fx-fill: #000080; -fx-font-weight: bold; }" +
            ".string { -fx-fill: #006400; }" +
            ".number { -fx-fill: #A52A2A; }" +
            ".comment { -fx-fill: #6A9955; -fx-font-style: italic; }" +
            ".operator { -fx-fill: #FF8C00; }" +
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
        
        // Define patterns for different keyword categories
        String structurePattern = "\\b(SUGOD|KATAPUSAN)\\b";
        String builtinPattern = "\\b(IPAKITA|DAWAT|MUGNA)\\b";
        String datatypePattern = "\\b(NUMERO|LETRA|TINUOD|TIPIK)\\b";
        String keywordPattern = "\\b(KUNG|WALA|DILI|PUNDOK|ALANG|SA|SAMTANG|UG|O)\\b";
        
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
        
        // 4. Mark structure keywords (SUGOD, KATAPUSAN)
        java.util.regex.Pattern structurePatternCompiled = java.util.regex.Pattern.compile(structurePattern);
        java.util.regex.Matcher structureMatcher = structurePatternCompiled.matcher(code);
        while (structureMatcher.find()) {
            for (int i = structureMatcher.start(); i < structureMatcher.end() && i < codeLength; i++) {
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i])) {
                    styles[i] = STRUCTURE_CLASS;
                }
            }
        }
        
        // 5. Mark built-in functions (IPAKITA, DAWAT, MUGNA)
        java.util.regex.Pattern builtinPatternCompiled = java.util.regex.Pattern.compile(builtinPattern);
        java.util.regex.Matcher builtinMatcher = builtinPatternCompiled.matcher(code);
        while (builtinMatcher.find()) {
            for (int i = builtinMatcher.start(); i < builtinMatcher.end() && i < codeLength; i++) {
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i])) {
                    styles[i] = BUILTIN_CLASS;
                }
            }
        }
        
        // 6. Mark data type keywords (NUMERO, LETRA, TINUOD, TIPIK)
        java.util.regex.Pattern datatypePatternCompiled = java.util.regex.Pattern.compile(datatypePattern);
        java.util.regex.Matcher datatypeMatcher = datatypePatternCompiled.matcher(code);
        while (datatypeMatcher.find()) {
            for (int i = datatypeMatcher.start(); i < datatypeMatcher.end() && i < codeLength; i++) {
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i])) {
                    styles[i] = DATATYPE_CLASS;
                }
            }
        }
        
        // 7. Mark control flow keywords (KUNG, SAMTANG, ALANG, etc.)
        java.util.regex.Pattern keyPattern = java.util.regex.Pattern.compile(keywordPattern);
        java.util.regex.Matcher keyMatcher = keyPattern.matcher(code);
        while (keyMatcher.find()) {
            for (int i = keyMatcher.start(); i < keyMatcher.end() && i < codeLength; i++) {
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i])) {
                    styles[i] = KEYWORD_CLASS;
                }
            }
        }
        
        // 8. Mark operators
        java.util.regex.Pattern operatorPattern = java.util.regex.Pattern.compile("[+\\-*/%=<>!&|(){}\\[\\]:,;]");
        java.util.regex.Matcher operatorMatcher = operatorPattern.matcher(code);
        while (operatorMatcher.find()) {
            for (int i = operatorMatcher.start(); i < operatorMatcher.end() && i < codeLength; i++) {
                // Don't override anything else
                if (!COMMENT_CLASS.equals(styles[i]) && 
                    !STRING_CLASS.equals(styles[i]) && 
                    !NUMBER_CLASS.equals(styles[i]) && 
                    !KEYWORD_CLASS.equals(styles[i]) &&
                    !BUILTIN_CLASS.equals(styles[i]) &&
                    !DATATYPE_CLASS.equals(styles[i]) &&
                    !STRUCTURE_CLASS.equals(styles[i])) {
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
            // Program structure
            case SUGOD:
            case KATAPUSAN:
                return STRUCTURE_CLASS;
            
            // Built-in functions
            case IPAKITA:
            case DAWAT:
            case MUGNA:
                return BUILTIN_CLASS;
            
            // Data types
            case NUMERO:
            case LETRA:
            case TINUOD:
            case TIPIK:
                return DATATYPE_CLASS;
            
            // Control flow keywords
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
