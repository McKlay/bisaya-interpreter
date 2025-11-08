package com.bisayapp.ui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 * Line Number Factory for Editor
 * 
 * Provides line numbers synchronized with the code editor
 */
public class LineNumberFactory {
    
    private final TextArea editor;
    private final TextArea lineNumberArea;
    private int lastLineCount = 0;
    
    public LineNumberFactory(TextArea editor) {
        this.editor = editor;
        this.lineNumberArea = new TextArea();
        setupLineNumbers();
        setupListener();
        synchronizeScrolling();
    }
    
    /**
     * Sets up the line number area styling
     */
    private void setupLineNumbers() {
        // Style the line number area to match editor
        lineNumberArea.setEditable(false);
        lineNumberArea.setFocusTraversable(false);
        lineNumberArea.setMouseTransparent(false); // Allow selection in editor behind
        lineNumberArea.setPrefWidth(50);
        lineNumberArea.setMaxWidth(50);
        lineNumberArea.setMinWidth(50);
        
        // Match the editor's font
        lineNumberArea.setFont(Font.font("Consolas", 14));
        
        // Style to look like a line number gutter
        lineNumberArea.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-control-inner-background: #f0f0f0; " +
            "-fx-text-fill: #666666; " +
            "-fx-border-color: #d0d0d0; " +
            "-fx-border-width: 0 1 0 0; " +
            "-fx-padding: 0; " +
            "-fx-background-insets: 0; " +
            "-fx-text-alignment: right;"
        );
        
        // Disable wrapping to match editor
        lineNumberArea.setWrapText(false);
        
        updateLineNumbers();
    }
    
    /**
     * Sets up listener for text changes
     */
    private void setupListener() {
        editor.textProperty().addListener((obs, oldText, newText) -> {
            int currentLineCount = countLines(newText);
            if (currentLineCount != lastLineCount) {
                updateLineNumbers();
                lastLineCount = currentLineCount;
            }
        });
        
        // Highlight current line on caret movement
        editor.caretPositionProperty().addListener((obs, oldPos, newPos) -> {
            highlightCurrentLine();
        });
    }
    
    /**
     * Synchronizes scrolling between line numbers and editor
     */
    private void synchronizeScrolling() {
        // Get the scroll panes (they're created internally by TextArea)
        editor.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            lineNumberArea.setScrollTop(newVal.doubleValue());
        });
        
        editor.scrollLeftProperty().addListener((obs, oldVal, newVal) -> {
            // Line numbers should not scroll horizontally
            lineNumberArea.setScrollLeft(0);
        });
    }
    
    /**
     * Updates the line numbers display
     */
    private void updateLineNumbers() {
        String text = editor.getText();
        int lineCount = Math.max(1, countLines(text));
        
        StringBuilder lineNumbers = new StringBuilder();
        for (int i = 1; i <= lineCount; i++) {
            lineNumbers.append(String.format("%3d", i));
            if (i < lineCount) {
                lineNumbers.append("\n");
            }
        }
        
        lineNumberArea.setText(lineNumbers.toString());
    }
    
    /**
     * Counts the number of lines in the text
     */
    private int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 1;
        }
        return text.split("\n", -1).length;
    }
    
    /**
     * Highlights the current line (visual feedback)
     */
    private void highlightCurrentLine() {
        // Note: Full highlighting would require RichTextFX
        // For now, just ensure line numbers stay synchronized
    }
    
    /**
     * Gets the line number area component
     */
    public TextArea getLineNumberArea() {
        return lineNumberArea;
    }
    
    /**
     * Creates an HBox containing line numbers and editor
     */
    public HBox createEditorWithLineNumbers() {
        HBox container = new HBox(0);
        container.getChildren().addAll(lineNumberArea, editor);
        HBox.setHgrow(editor, Priority.ALWAYS);
        return container;
    }
    
    /**
     * Gets the current line number based on caret position
     */
    public int getCurrentLineNumber() {
        int caretPos = editor.getCaretPosition();
        String text = editor.getText(0, caretPos);
        return countLines(text);
    }
    
    /**
     * Gets the current column number
     */
    public int getCurrentColumnNumber() {
        int caretPos = editor.getCaretPosition();
        String text = editor.getText();
        
        // Find the start of the current line
        int lineStart = caretPos;
        while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
            lineStart--;
        }
        
        return caretPos - lineStart + 1;
    }
}
