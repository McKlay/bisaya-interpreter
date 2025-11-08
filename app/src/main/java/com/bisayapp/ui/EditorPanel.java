package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Code Editor Panel Component
 * 
 * Provides the text editor area for writing Bisaya++ code with line numbers
 */
public class EditorPanel extends VBox {
    
    private final TextArea codeEditor;
    private final LineNumberFactory lineNumberFactory;
    
    public EditorPanel() {
        super(5);
        setPadding(new Insets(5));
        setStyle("-fx-background-color: white;");
        
        // Create label
        Label editorLabel = new Label("📝 Code Editor:");
        editorLabel.setFont(Font.font("System", 14));
        editorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Create editor
        codeEditor = new TextArea();
        codeEditor.setFont(Font.font(IDEConfig.EDITOR_FONT, IDEConfig.EDITOR_FONT_SIZE));
        codeEditor.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: %s; -fx-control-inner-background: %s;",
            IDEConfig.EDITOR_BG_COLOR, IDEConfig.EDITOR_TEXT_COLOR, IDEConfig.EDITOR_BG_COLOR
        ));
        codeEditor.setPromptText(IDEConfig.EDITOR_PROMPT);
        codeEditor.setWrapText(false);
        
        // Create line number factory
        lineNumberFactory = new LineNumberFactory(codeEditor);
        
        // Create editor with line numbers
        HBox editorWithLineNumbers = lineNumberFactory.createEditorWithLineNumbers();
        VBox.setVgrow(editorWithLineNumbers, Priority.ALWAYS);
        
        getChildren().addAll(editorLabel, editorWithLineNumbers);
    }
    
    /**
     * Gets the code editor text area
     */
    public TextArea getCodeEditor() {
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
        codeEditor.setText(code);
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
        int line = lineNumberFactory.getCurrentLineNumber();
        int col = lineNumberFactory.getCurrentColumnNumber();
        return "Line " + line + ", Col " + col;
    }
    
    /**
     * Gets the line number factory
     */
    public LineNumberFactory getLineNumberFactory() {
        return lineNumberFactory;
    }
}
