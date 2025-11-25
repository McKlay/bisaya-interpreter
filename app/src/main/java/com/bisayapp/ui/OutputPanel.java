package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Output Display Panel Component
 * 
 * Provides the output area for displaying program results and errors
 * Supports clickable error messages to jump to error lines
 */
public class OutputPanel extends VBox {
    
    private final StyleClassedTextArea outputArea;
    private EditorPanel editorPanel; // Reference to jump to lines
    private OutputStyle currentStyle = OutputStyle.NORMAL;
    
    // Pattern to detect line numbers in error messages
    private static final Pattern LINE_NUMBER_PATTERN = Pattern.compile("→\\s*(\\d+)\\s*\\|");
    
    private enum OutputStyle {
        NORMAL, ERROR, WARNING
    }
    
    public OutputPanel() {
        super(5);
        setPadding(new Insets(5));
        setStyle("-fx-background-color: #f9f9f9;");
        
        // Create label
        Label outputLabel = new Label("📤 Output:");
        outputLabel.setFont(Font.font("System", 14));
        outputLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Create output area using RichTextFX for clickable text
        outputArea = new StyleClassedTextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        
        // Apply base styling
        applyBaseStyle();
        
        // Add mouse click handler for jumping to error lines
        setupClickHandler();
        
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        getChildren().addAll(outputLabel, outputArea);
    }
    
    /**
     * Applies base styling to output area
     */
    private void applyBaseStyle() {
        outputArea.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: %dpt; -fx-background-color: %s;",
            IDEConfig.OUTPUT_FONT, IDEConfig.OUTPUT_FONT_SIZE, IDEConfig.OUTPUT_BG_COLOR
        ));
    }
    
    /**
     * Sets up click handler to jump to error lines
     */
    private void setupClickHandler() {
        outputArea.setOnMouseClicked(event -> {
            if (editorPanel == null) return;
            
            // Get the paragraph (line) that was clicked
            int clickedParagraph = outputArea.getCurrentParagraph();
            String lineText = outputArea.getText(clickedParagraph);
            
            // Check if this line contains an error line number
            Matcher matcher = LINE_NUMBER_PATTERN.matcher(lineText);
            if (matcher.find()) {
                try {
                    int lineNumber = Integer.parseInt(matcher.group(1));
                    editorPanel.jumpToLine(lineNumber);
                } catch (NumberFormatException e) {
                    // Ignore invalid line numbers
                }
            }
        });
        
        // Change cursor when hovering over error lines
        outputArea.setOnMouseMoved(event -> {
            int paragraph = outputArea.getCurrentParagraph();
            if (paragraph >= 0 && paragraph < outputArea.getParagraphs().size()) {
                String lineText = outputArea.getText(paragraph);
                if (LINE_NUMBER_PATTERN.matcher(lineText).find()) {
                    outputArea.setCursor(Cursor.HAND);
                } else {
                    outputArea.setCursor(Cursor.DEFAULT);
                }
            }
        });
    }
    
    /**
     * Sets the editor panel reference for jumping to lines
     */
    public void setEditorPanel(EditorPanel editorPanel) {
        this.editorPanel = editorPanel;
    }
    
    /**
     * Gets the output text area
     */
    public StyleClassedTextArea getOutputArea() {
        return outputArea;
    }
    
    /**
     * Sets the output text with proper color styling
     */
    public void setText(String text) {
        outputArea.clear();
        appendStyledText(text);
        highlightErrorLines();
    }
    
    /**
     * Appends text to the output with proper styling
     */
    public void appendText(String text) {
        appendStyledText(text);
        highlightErrorLines();
    }
    
    /**
     * Appends text with appropriate style class
     */
    private void appendStyledText(String text) {
        int start = outputArea.getLength();
        outputArea.appendText(text);
        int end = outputArea.getLength();
        
        // Apply style class based on current style
        String styleClass = getCurrentStyleClass();
        if (styleClass != null) {
            outputArea.setStyle(start, end, Collections.singleton(styleClass));
        }
    }
    
    /**
     * Clears the output
     */
    public void clear() {
        outputArea.clear();
        currentStyle = OutputStyle.NORMAL;
    }
    
    /**
     * Highlights error lines in the output
     */
    private void highlightErrorLines() {
        String fullText = outputArea.getText();
        String[] lines = fullText.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            if (LINE_NUMBER_PATTERN.matcher(lines[i]).find()) {
                // Mark this line as clickable
                outputArea.setParagraphStyle(i, Collections.singletonList("error-line"));
            }
        }
    }
    
    /**
     * Gets the style class name for current output style
     */
    private String getCurrentStyleClass() {
        switch (currentStyle) {
            case ERROR:
                return "output-error";
            case WARNING:
                return "output-warning";
            case NORMAL:
            default:
                return "output-normal";
        }
    }
    
    /**
     * Sets normal output style (green text)
     */
    public void setNormalStyle() {
        currentStyle = OutputStyle.NORMAL;
    }
    
    /**
     * Sets error style (red text)
     */
    public void setErrorStyle() {
        currentStyle = OutputStyle.ERROR;
    }
    
    /**
     * Sets warning style (orange text)
     */
    public void setWarningStyle() {
        currentStyle = OutputStyle.WARNING;
    }
}
