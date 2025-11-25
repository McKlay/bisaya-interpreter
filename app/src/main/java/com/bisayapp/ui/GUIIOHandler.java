package com.bisayapp.ui;

import com.bisayapp.IOHandler;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * GUIIOHandler
 * 
 * GUI implementation of IOHandler for JavaFX applications.
 * Used by BisayaIDE to handle DAWAT commands with modal dialogs.
 * 
 * Features:
 * - Displays input dialog when DAWAT is encountered
 * - Thread-safe interaction with JavaFX UI thread
 * - Graceful handling of input cancellation
 * - Output/error delegation to OutputPanel
 */
public class GUIIOHandler implements IOHandler {
    
    private final OutputPanel outputPanel;
    private final StringBuilder outputBuffer;
    
    /**
     * Creates GUI I/O handler
     * 
     * @param outputPanel The output panel to write to
     */
    public GUIIOHandler(OutputPanel outputPanel) {
        this.outputPanel = outputPanel;
        this.outputBuffer = new StringBuilder();
    }
    
    @Override
    public void writeOutput(String text) {
        outputBuffer.append(text);
        // Update UI on JavaFX thread
        Platform.runLater(() -> outputPanel.appendText(text));
    }
    
    @Override
    public void writeError(String error) {
        // Update UI on JavaFX thread
        Platform.runLater(() -> {
            outputPanel.appendText("\n[ERROR] " + error + "\n");
            outputPanel.setErrorStyle();
        });
    }
    
    @Override
    public String readInput(String prompt) {
        // Must run on JavaFX thread - use CompletableFuture for synchronization
        CompletableFuture<String> future = new CompletableFuture<>();
        
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("DAWAT - Input Required");
            dialog.setHeaderText(prompt);
            dialog.setContentText("multiple variables must be comma-separated:");
            
            // Style the dialog
            dialog.getDialogPane().setStyle(
                "-fx-font-family: 'Consolas', 'Monaco', monospace; " +
                "-fx-font-size: 10pt;"
            );
            
            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                future.complete(result.get().trim());
            } else {
                future.completeExceptionally(new RuntimeException("Input cancelled by user"));
            }
        });
        
        try {
            return future.get(); // Wait for user input
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Input interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Input cancelled or failed", e.getCause());
        }
    }
    
    @Override
    public boolean hasInput() {
        // GUI always has "input available" - will show dialog when needed
        return true;
    }
    
    /**
     * Gets the accumulated output buffer
     * 
     * @return The complete output as a string
     */
    public String getOutput() {
        return outputBuffer.toString();
    }
    
    /**
     * Clears the output buffer
     */
    public void clearBuffer() {
        outputBuffer.setLength(0);
    }
}
