package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Output Display Panel Component
 * 
 * Provides the output area for displaying program results and errors
 */
public class OutputPanel extends VBox {
    
    private final TextArea outputArea;
    
    public OutputPanel() {
        super(5);
        setPadding(new Insets(5));
        setStyle("-fx-background-color: #f9f9f9;");
        
        // Create label
        Label outputLabel = new Label("📤 Output:");
        outputLabel.setFont(Font.font("System", 14));
        outputLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Create output area
        outputArea = new TextArea();
        outputArea.setFont(Font.font(IDEConfig.OUTPUT_FONT, IDEConfig.OUTPUT_FONT_SIZE));
        outputArea.setEditable(false);
        setNormalStyle();
        outputArea.setPromptText(IDEConfig.OUTPUT_PROMPT);
        outputArea.setWrapText(true);
        
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        getChildren().addAll(outputLabel, outputArea);
    }
    
    /**
     * Gets the output text area
     */
    public TextArea getOutputArea() {
        return outputArea;
    }
    
    /**
     * Sets the output text
     */
    public void setText(String text) {
        outputArea.setText(text);
    }
    
    /**
     * Appends text to the output
     */
    public void appendText(String text) {
        outputArea.appendText(text);
    }
    
    /**
     * Clears the output
     */
    public void clear() {
        outputArea.clear();
        setNormalStyle();
    }
    
    /**
     * Sets normal output style (green text)
     */
    public void setNormalStyle() {
        outputArea.setStyle(String.format(
            "-fx-control-inner-background: %s; -fx-text-fill: %s; -fx-background-color: %s;",
            IDEConfig.OUTPUT_BG_COLOR, IDEConfig.OUTPUT_TEXT_COLOR, IDEConfig.OUTPUT_BG_COLOR
        ));
    }
    
    /**
     * Sets error style (red text)
     */
    public void setErrorStyle() {
        outputArea.setStyle(String.format(
            "-fx-control-inner-background: %s; -fx-text-fill: %s; -fx-background-color: %s;",
            IDEConfig.OUTPUT_BG_COLOR, IDEConfig.ERROR_TEXT_COLOR, IDEConfig.OUTPUT_BG_COLOR
        ));
    }
    
    /**
     * Sets warning style (orange text)
     */
    public void setWarningStyle() {
        outputArea.setStyle(String.format(
            "-fx-control-inner-background: %s; -fx-text-fill: %s; -fx-background-color: %s;",
            IDEConfig.OUTPUT_BG_COLOR, IDEConfig.WARNING_TEXT_COLOR, IDEConfig.OUTPUT_BG_COLOR
        ));
    }
}
