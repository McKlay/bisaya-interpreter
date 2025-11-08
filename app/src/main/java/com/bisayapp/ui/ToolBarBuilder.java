package com.bisayapp.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Toolbar Builder
 * 
 * Creates and configures the IDE toolbar
 */
public class ToolBarBuilder {
    
    private final IDEController controller;
    
    public ToolBarBuilder(IDEController controller) {
        this.controller = controller;
    }
    
    /**
     * Builds and returns the configured toolbar
     */
    public ToolBar build() {
        ToolBar toolBar = new ToolBar();
        toolBar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5;");
        
        Button runButton = createRunButton();
        Button clearButton = createClearButton();
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolBar.getItems().addAll(runButton, clearButton, spacer);
        
        return toolBar;
    }
    
    /**
     * Creates the Run button
     */
    private Button createRunButton() {
        Button runButton = new Button("▶ Run");
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        runButton.setOnAction(e -> controller.runProgram());
        runButton.setTooltip(new Tooltip("Run the program (Ctrl+R)"));
        return runButton;
    }
    
    /**
     * Creates the Clear Output button
     */
    private Button createClearButton() {
        Button clearButton = new Button("🗑 Clear Output");
        clearButton.setStyle("-fx-padding: 8 20 8 20; -fx-cursor: hand;");
        clearButton.setOnAction(e -> controller.clearOutput());
        clearButton.setTooltip(new Tooltip("Clear the output area (Ctrl+L)"));
        return clearButton;
    }
}
