package com.bisayapp.ui;

import com.bisayapp.Lexer;
import com.bisayapp.Token;
import com.bisayapp.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Pretty Printer for Bisaya++ Code
 * 
 * Formats source code with consistent indentation and spacing.
 * Uses token-based approach for accuracy and performance.
 * 
 * Features:
 * - 4-space indentation per nesting level
 * - Consistent operator spacing
 * - Tab-to-space conversion (1 tab = 4 spaces)
 * - Comment and string preservation
 * - Fast single-pass formatting
 * 
 * Usage:
 *   String formatted = PrettyPrinter.format(sourceCode);
 */
public class PrettyPrinter {
    
    private static final int INDENT_SIZE = 4;
    private static final String INDENT = " ".repeat(INDENT_SIZE);
    
    /**
     * Formats Bisaya++ source code with consistent style
     * 
     * @param sourceCode The raw source code to format
     * @return Formatted code with proper indentation and spacing
     */
    public static String format(String sourceCode) {
        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            return sourceCode;
        }
        
        try {
            // Step 1: Tokenize the source code
            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.scanTokens();
            
            // Step 2: Format using tokens
            return formatWithTokens(tokens, sourceCode);
            
        } catch (Exception e) {
            // On error, return original code unchanged
            System.err.println("Pretty printer error: " + e.getMessage());
            return sourceCode;
        }
    }
    
    /**
     * Formats only selected lines of code
     * 
     * @param fullCode The complete source code
     * @param selectionStart Start position of selection
     * @param selectionEnd End position of selection
     * @return Formatted code with only selected lines reformatted
     */
    public static String formatSelection(String fullCode, int selectionStart, int selectionEnd) {
        if (fullCode == null || fullCode.isEmpty() || selectionStart >= selectionEnd) {
            return fullCode;
        }
        
        try {
            // Find line boundaries for the selection
            String[] lines = fullCode.split("\n", -1);
            int currentPos = 0;
            int startLine = 0;
            int endLine = lines.length - 1;
            
            // Find which lines are affected by the selection
            for (int i = 0; i < lines.length; i++) {
                int lineStart = currentPos;
                int lineEnd = currentPos + lines[i].length() + 1; // +1 for newline
                
                if (selectionStart >= lineStart && selectionStart < lineEnd) {
                    startLine = i;
                }
                if (selectionEnd > lineStart && selectionEnd <= lineEnd) {
                    endLine = i;
                }
                
                currentPos = lineEnd;
            }
            
            // Extract the selected lines
            StringBuilder selectedText = new StringBuilder();
            for (int i = startLine; i <= endLine; i++) {
                selectedText.append(lines[i]);
                if (i < endLine) {
                    selectedText.append("\n");
                }
            }
            
            // Format only the selected portion
            String formatted = format(selectedText.toString());
            
            // Reconstruct the full code with formatted selection
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (i < startLine) {
                    // Before selection - keep as is
                    result.append(lines[i]);
                } else if (i == startLine) {
                    // First line of selection - append formatted text
                    result.append(formatted);
                } else if (i > startLine && i <= endLine) {
                    // Skip - already included in formatted text
                    continue;
                } else {
                    // After selection - keep as is
                    result.append(lines[i]);
                }
                
                if (i < lines.length - 1) {
                    result.append("\n");
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            System.err.println("Selection format error: " + e.getMessage());
            return fullCode;
        }
    }
    
    /**
     * Formats code using token information
     */
    private static String formatWithTokens(List<Token> tokens, String sourceCode) {
        StringBuilder formatted = new StringBuilder();
        String[] lines = sourceCode.split("\n", -1);
        
        int indentLevel = 0;
        
        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum].replace("\t", INDENT); // Convert tabs to spaces
            
            // Skip empty lines (preserve them without indentation)
            if (line.trim().isEmpty()) {
                // Only add newline if not the last line, or if original had trailing newline
                if (lineNum < lines.length - 1) {
                    formatted.append("\n");
                }
                continue;
            }
            
            // Check if line is comment-only
            if (line.trim().startsWith("@@")) {
                formatted.append(INDENT.repeat(Math.max(0, indentLevel))).append(line.trim());
                if (lineNum < lines.length - 1) {
                    formatted.append("\n");
                }
                continue;
            }
            
            // Get tokens for this line
            List<Token> lineTokens = getTokensForLine(tokens, lineNum + 1);
            
            // Check special structure keywords that affect indentation
            boolean hasSUGOD = lineContainsToken(lineTokens, TokenType.SUGOD);
            boolean hasKATAPUSAN = lineContainsToken(lineTokens, TokenType.KATAPUSAN);
            
            // KATAPUSAN decreases indent (back to 0)
            if (hasKATAPUSAN) {
                indentLevel = 0;
            }
            
            // Check if line starts with closing brace (decrease indent before)
            if (startsWithCloseBrace(line)) {
                indentLevel = Math.max(0, indentLevel - 1);
            }
            
            // Format the line with current indent
            String formattedLine = formatLine(lineTokens, indentLevel, line);
            formatted.append(formattedLine);
            
            // Add newline after each line except the last (unless original had trailing newline)
            if (lineNum < lines.length - 1) {
                formatted.append("\n");
            }
            
            // SUGOD increases indent (enter program body)
            if (hasSUGOD) {
                indentLevel = 1;
            }
            
            // Check if line ends with opening brace (increase indent after)
            if (endsWithOpenBrace(line)) {
                indentLevel++;
            }
        }
        
        return formatted.toString();
    }
    
    /**
     * Checks if line contains a specific token type
     */
    private static boolean lineContainsToken(List<Token> tokens, TokenType type) {
        for (Token token : tokens) {
            if (token.type == type) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets all tokens that belong to a specific line
     */
    private static List<Token> getTokensForLine(List<Token> allTokens, int lineNumber) {
        List<Token> lineTokens = new ArrayList<>();
        
        for (Token token : allTokens) {
            if (token.line == lineNumber && token.type != TokenType.EOF) {
                lineTokens.add(token);
            }
        }
        
        return lineTokens;
    }
    
    /**
     * Formats a single line with proper indentation and spacing
     */
    private static String formatLine(List<Token> tokens, int indentLevel, String originalLine) {
        if (tokens.isEmpty()) {
            return "";
        }
        
        // Extract inline comment if present (@@)
        String inlineComment = "";
        int commentIndex = originalLine.indexOf("@@");
        if (commentIndex >= 0) {
            inlineComment = "  " + originalLine.substring(commentIndex).trim();
        }
        
        StringBuilder line = new StringBuilder();
        
        // Add indentation
        line.append(INDENT.repeat(Math.max(0, indentLevel)));
        
        // Handle comment-only lines specially
        if (tokens.size() == 1 && isComment(tokens.get(0))) {
            line.append(tokens.get(0).lexeme.trim());
            return line.toString();
        }
        
        // Format tokens with spacing
        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            Token prev = i > 0 ? tokens.get(i - 1) : null;
            
            // Add space before token if needed
            if (i > 0 && needsSpaceBefore(current, prev)) {
                line.append(" ");
            }
            
            // Add the token itself
            line.append(current.lexeme);
        }
        
        // Add inline comment if it existed
        if (!inlineComment.isEmpty()) {
            line.append(inlineComment);
        }
        
        return line.toString().replaceAll("\\s+$", ""); // Trim trailing spaces
    }
    
    /**
     * Determines if space is needed before a token
     */
    private static boolean needsSpaceBefore(Token current, Token prev) {
        if (prev == null) {
            return false; // First token on line
        }
        
        TokenType curr = current.type;
        TokenType prv = prev.type;
        
        // No space after opening paren/brace/bracket
        if (prv == TokenType.LEFT_PAREN || prv == TokenType.LEFT_BRACE || prv == TokenType.LEFT_BRACKET) {
            return false;
        }
        
        // No space before closing paren/brace/bracket
        if (curr == TokenType.RIGHT_PAREN || curr == TokenType.RIGHT_BRACE || curr == TokenType.RIGHT_BRACKET) {
            return false;
        }
        
        // No space before comma, colon (but space after)
        if (curr == TokenType.COMMA || curr == TokenType.COLON) {
            return false;
        }
        
        // Space after comma
        if (prv == TokenType.COMMA) {
            return true;
        }
        
        // Space after colon (IPAKITA: x)
        if (prv == TokenType.COLON) {
            return true;
        }
        
        // Space before opening paren after keyword (KUNG (x), IPAKITA: requires colon first)
        if (curr == TokenType.LEFT_PAREN && isKeyword(prv)) {
            return true;
        }
        
        // Space before binary operators
        if (isBinaryOperator(curr)) {
            return true;
        }
        
        // Space after binary operators
        if (isBinaryOperator(prv)) {
            return true;
        }
        
        // Space after keywords (MUGNA NUMERO, ALANG SA)
        if (isKeyword(prv) && !isSpecialPunctuation(curr)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if token is a binary operator that needs spacing
     */
    private static boolean isBinaryOperator(TokenType type) {
        return type == TokenType.EQUAL ||
               type == TokenType.PLUS ||
               type == TokenType.MINUS ||
               type == TokenType.STAR ||
               type == TokenType.SLASH ||
               type == TokenType.PERCENT ||
               type == TokenType.AMPERSAND ||
               type == TokenType.EQUAL_EQUAL ||
               type == TokenType.BANG_EQUAL ||
               type == TokenType.LT_GT ||
               type == TokenType.GREATER ||
               type == TokenType.GREATER_EQUAL ||
               type == TokenType.LESS ||
               type == TokenType.LESS_EQUAL ||
               type == TokenType.UG ||  // AND
               type == TokenType.O;     // OR
    }
    
    /**
     * Checks if token is a keyword
     */
    private static boolean isKeyword(TokenType type) {
        return type == TokenType.SUGOD ||
               type == TokenType.KATAPUSAN ||
               type == TokenType.MUGNA ||
               type == TokenType.IPAKITA ||
               type == TokenType.DAWAT ||
               type == TokenType.KUNG ||
               type == TokenType.WALA ||
               type == TokenType.DILI ||
               type == TokenType.PUNDOK ||
               type == TokenType.ALANG ||
               type == TokenType.SA ||
               type == TokenType.SAMTANG ||
               type == TokenType.NUMERO ||
               type == TokenType.LETRA ||
               type == TokenType.TINUOD ||
               type == TokenType.TIPIK ||
               type == TokenType.UG ||
               type == TokenType.O;
    }
    
    /**
     * Checks if token is special punctuation (colon, brace, paren)
     */
    private static boolean isSpecialPunctuation(TokenType type) {
        return type == TokenType.COLON ||
               type == TokenType.LEFT_BRACE ||
               type == TokenType.RIGHT_BRACE ||
               type == TokenType.LEFT_PAREN ||
               type == TokenType.RIGHT_PAREN;
    }
    
    /**
     * Checks if token is a comment
     */
    private static boolean isComment(Token token) {
        // Comments are detected by lexeme starting with @@
        return token.lexeme != null && token.lexeme.trim().startsWith("@@");
    }
    
    /**
     * Checks if line starts with closing brace (string version)
     */
    private static boolean startsWithCloseBrace(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("}");
    }
    
    /**
     * Checks if line ends with opening brace (string version)
     */
    private static boolean endsWithOpenBrace(String line) {
        // Check for inline comment first
        int commentIndex = line.indexOf("@@");
        String codepart = commentIndex >= 0 ? line.substring(0, commentIndex) : line;
        return codepart.trim().endsWith("{");
    }
    
}
