package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Status Bar Component
 * 
 * Displays status information at the bottom of the IDE
 */
public class StatusBar extends HBox {
    
    private final Label statusLabel;
    private final Label versionLabel;
    
    public StatusBar() {
        super(10);
        setPadding(new Insets(5, 10, 5, 10));
        setStyle("-fx-background-color: #e8e8e8; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #333333;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        versionLabel = new Label(IDEConfig.VERSION);
        versionLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");
        
        getChildren().addAll(statusLabel, spacer, versionLabel);
    }
    
    /**
     * Updates the status message
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    /**
     * Gets the current status message
     */
    public String getStatus() {
        return statusLabel.getText();
    }
}
