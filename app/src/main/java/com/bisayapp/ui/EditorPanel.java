package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.fxmisc.richtext.CodeArea;

/**
 * Code Editor Panel Component
 * 
 * Provides the text editor area for writing Bisaya++ code with syntax highlighting
 * Uses RichTextFX CodeArea for advanced text editing features
 */
public class EditorPanel extends VBox {
    
    private final CodeArea codeEditor;
    private final SyntaxHighlighter syntaxHighlighter;
    
    public EditorPanel() {
        super(5);
        setPadding(new Insets(5));
        setStyle("-fx-background-color: white;");
        
        // Create label
        Label editorLabel = new Label("📝 Code Editor:");
        editorLabel.setFont(Font.font("System", 14));
        editorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Create CodeArea (RichTextFX) instead of TextArea
        codeEditor = new CodeArea();
        codeEditor.getStyleClass().add("code-area");
        codeEditor.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: %dpt; -fx-background-color: %s;",
            IDEConfig.EDITOR_FONT, IDEConfig.EDITOR_FONT_SIZE, IDEConfig.EDITOR_BG_COLOR
        ));
        // Note: CodeArea doesn't support setPromptText(), placeholder handled by label
        
        // Add line numbers with current line highlighting
        codeEditor.setParagraphGraphicFactory(HighlightedLineNumberFactory.get(codeEditor));
        
        // Initialize syntax highlighter
        syntaxHighlighter = new SyntaxHighlighter(codeEditor);
        
        // Apply initial highlighting if there's any text
        if (!codeEditor.getText().isEmpty()) {
            syntaxHighlighter.applyHighlighting();
        }
        
        VBox.setVgrow(codeEditor, Priority.ALWAYS);
        getChildren().addAll(editorLabel, codeEditor);
    }
    
    /**
     * Gets the code editor (CodeArea)
     */
    public CodeArea getCodeEditor() {
        return codeEditor;
    }
    
    /**
     * Gets the current code text
     */
    public String getCode() {
        return codeEditor.getText();
    }
    
    /**
     * Sets the code text
     */
    public void setCode(String code) {
        codeEditor.clear();
        codeEditor.replaceText(0, 0, code);
        syntaxHighlighter.applyHighlighting();
    }
    
    /**
     * Clears the editor
     */
    public void clear() {
        codeEditor.clear();
    }
    
    /**
     * Gets the caret position
     */
    public int getCaretPosition() {
        return codeEditor.getCaretPosition();
    }
    
    /**
     * Gets current line and column position
     */
    public String getLineColumnPosition() {
        int line = getCurrentLineNumber();
        int col = getCurrentColumnNumber();
        return "Line " + line + ", Col " + col;
    }
    
    /**
     * Gets the current line number (1-based)
     */
    public int getCurrentLineNumber() {
        return codeEditor.getCurrentParagraph() + 1;
    }
    
    /**
     * Gets the current column number (1-based)
     */
    public int getCurrentColumnNumber() {
        return codeEditor.getCaretColumn() + 1;
    }
    
    /**
     * Jumps to a specific line number
     */
    public void jumpToLine(int lineNumber) {
        if (lineNumber < 1 || lineNumber > codeEditor.getParagraphs().size()) {
            return;
        }
        int paragraphIndex = lineNumber - 1;
        codeEditor.moveTo(paragraphIndex, 0);
        codeEditor.requestFollowCaret();
        codeEditor.requestFocus();
    }
    
    /**
     * Gets the syntax highlighter
     */
    public SyntaxHighlighter getSyntaxHighlighter() {
        return syntaxHighlighter;
    }
}
