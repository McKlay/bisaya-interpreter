package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
    
    /**
     * Sets up the context menu with format options
     * @param controller The IDE controller to handle actions
     */
    public void setupContextMenu(IDEController controller) {
        ContextMenu contextMenu = new ContextMenu();
        
        // Format menu item - label changes based on selection
        MenuItem formatItem = new MenuItem("Format Document");
        formatItem.setAccelerator(new KeyCodeCombination(KeyCode.F, 
                                   KeyCombination.CONTROL_DOWN, 
                                   KeyCombination.SHIFT_DOWN));
        formatItem.setOnAction(e -> controller.formatCode());
        
        // Update label dynamically based on selection
        contextMenu.setOnShowing(e -> {
            if (hasSelection()) {
                formatItem.setText("Format Selection");
            } else {
                formatItem.setText("Format Document");
            }
        });
        
        contextMenu.getItems().add(formatItem);
        codeEditor.setContextMenu(contextMenu);
    }
    
    /**
     * Gets the selected text (empty string if no selection)
     */
    public String getSelectedText() {
        return codeEditor.getSelectedText();
    }
    
    /**
     * Checks if there is any text selected
     */
    public boolean hasSelection() {
        return codeEditor.getSelection().getLength() > 0;
    }
    
    /**
     * Gets the start position of current selection
     */
    public int getSelectionStart() {
        return codeEditor.getSelection().getStart();
    }
    
    /**
     * Gets the end position of current selection
     */
    public int getSelectionEnd() {
        return codeEditor.getSelection().getEnd();
    }
    
    /**
     * Replaces the selected text with new text
     */
    public void replaceSelection(String replacement) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        codeEditor.replaceText(start, end, replacement);
        syntaxHighlighter.applyHighlighting();
    }
    
    /**
     * Selects text from start to end position
     */
    public void selectRange(int start, int end) {
        codeEditor.selectRange(start, end);
    }
}
