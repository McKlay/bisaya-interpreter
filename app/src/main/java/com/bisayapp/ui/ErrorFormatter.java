package com.bisayapp.ui;

/**
 * Error Formatter Utility
 * 
 * Formats error messages with improved readability and context
 */
public class ErrorFormatter {
    
    /**
     * Formats an error message with line number highlighting
     */
    public static String formatError(String errorMessage, String sourceCode) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return "Unknown error occurred";
        }
        
        // Extract line number if present in error message
        int lineNumber = extractLineNumber(errorMessage);
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("═══════════════════════════════════════════\n");
        formatted.append("❌ ERROR DETECTED\n");
        formatted.append("═══════════════════════════════════════════\n\n");
        formatted.append(errorMessage).append("\n\n");
        
        // Add code context if line number is available
        if (lineNumber > 0 && sourceCode != null && !sourceCode.isEmpty()) {
            String context = getCodeContext(sourceCode, lineNumber);
            if (!context.isEmpty()) {
                formatted.append("Code Context:\n");
                formatted.append("───────────────────────────────────────────\n");
                formatted.append(context);
                formatted.append("───────────────────────────────────────────\n");
            }
        }
        
        return formatted.toString();
    }
    
    /**
     * Extracts line number from error message
     */
    private static int extractLineNumber(String errorMessage) {
        // Look for pattern "[line X col Y]" first (Bisaya++ format)
        if (errorMessage.contains("[line")) {
            try {
                int lineStart = errorMessage.indexOf("[line") + 5;
                int lineEnd = errorMessage.indexOf("col", lineStart);
                if (lineEnd > lineStart) {
                    String numStr = errorMessage.substring(lineStart, lineEnd).trim();
                    return Integer.parseInt(numStr);
                }
            } catch (Exception e) {
                // Fall through to other patterns
            }
        }
        
        // Look for patterns like "Line 5" or "line 5"
        String[] words = errorMessage.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].toLowerCase().equals("line")) {
                try {
                    // Remove any trailing punctuation
                    String numStr = words[i + 1].replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        return Integer.parseInt(numStr);
                    }
                } catch (NumberFormatException e) {
                    // Continue searching
                }
            }
        }
        return -1;
    }
    
    /**
     * Gets code context around the error line
     */
    private static String getCodeContext(String sourceCode, int lineNumber) {
        if (sourceCode == null || sourceCode.isEmpty()) {
            return "";
        }
        
        String[] lines = sourceCode.split("\n", -1);
        
        if (lineNumber < 1 || lineNumber > lines.length) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        int start = Math.max(1, lineNumber - 2);
        int end = Math.min(lines.length, lineNumber + 2);
        
        for (int i = start; i <= end; i++) {
            String lineContent = (i <= lines.length && i > 0) ? lines[i - 1] : "";
            
            if (i == lineNumber) {
                // Show the error line with arrow
                context.append(String.format("→ %3d | %s\n", i, lineContent));
                // Add caret line pointing to the error
                int caretLength = Math.max(1, Math.min(lineContent.trim().length(), 50));
                context.append("       | ");
                context.append("^".repeat(caretLength));
                context.append("\n");
            } else {
                context.append(String.format("  %3d | %s\n", i, lineContent));
            }
        }
        
        return context.toString();
    }
    
    /**
     * Formats a runtime error with stack trace
     */
    public static String formatRuntimeError(String message, String details) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("═══════════════════════════════════════════\n");
        formatted.append("⚠️  RUNTIME ERROR\n");
        formatted.append("═══════════════════════════════════════════\n\n");
        formatted.append(message).append("\n");
        
        if (details != null && !details.isEmpty()) {
            formatted.append("\nDetails:\n");
            formatted.append(details).append("\n");
        }
        
        return formatted.toString();
    }
    
    /**
     * Formats a success message
     */
    public static String formatSuccess(String output) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("═══════════════════════════════════════════\n");
        formatted.append("✅ EXECUTION SUCCESSFUL\n");
        formatted.append("═══════════════════════════════════════════\n\n");
        formatted.append(output);
        return formatted.toString();
    }
}
