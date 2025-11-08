package com.bisayapp.ui;

import com.bisayapp.Bisaya;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * IDE Controller
 * 
 * Handles all business logic and coordinates between UI components
 */
public class IDEController {
    
    private final Stage stage;
    private final EditorPanel editorPanel;
    private final OutputPanel outputPanel;
    private final StatusBar statusBar;
    private final FileManager fileManager;
    
    public IDEController(Stage stage, EditorPanel editorPanel, OutputPanel outputPanel, StatusBar statusBar) {
        this.stage = stage;
        this.editorPanel = editorPanel;
        this.outputPanel = outputPanel;
        this.statusBar = statusBar;
        this.fileManager = new FileManager(stage);
        
        // Connect output panel to editor for jump-to-line functionality
        outputPanel.setEditorPanel(editorPanel);
        
        // Setup listeners
        setupListeners();
    }
    
    /**
     * Sets up event listeners for UI components
     */
    private void setupListeners() {
        editorPanel.getCodeEditor().textProperty().addListener((obs, oldText, newText) -> updateStatus());
        editorPanel.getCodeEditor().caretPositionProperty().addListener((obs, oldPos, newPos) -> updateStatus());
    }
    
    /**
     * Creates a new file
     */
    public void newFile() {
        if (!editorPanel.getCode().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("New File");
            confirm.setHeaderText("Create new file?");
            confirm.setContentText("Current code will be lost. Continue?");
            
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }
        
        editorPanel.clear();
        fileManager.setCurrentFile(null);
        stage.setTitle(IDEConfig.WINDOW_TITLE);
        statusBar.setStatus("New file created");
        updateStatus();
    }
    
    /**
     * Opens a file
     */
    public void openFile() {
        File file = fileManager.showOpenDialog();
        if (file != null) {
            try {
                String content = fileManager.loadFile(file);
                editorPanel.setCode(content);
                fileManager.setCurrentFile(file);
                stage.setTitle(IDEConfig.WINDOW_TITLE + " - " + file.getName());
                statusBar.setStatus("Opened: " + file.getName());
                updateStatus();
            } catch (IOException e) {
                fileManager.showError("Error loading file", e.getMessage());
                statusBar.setStatus("Failed to open file");
            }
        }
    }
    
    /**
     * Reloads the current file from disk
     */
    public void reloadFile() {
        File currentFile = fileManager.getCurrentFile();
        if (currentFile != null && currentFile.exists()) {
            try {
                String content = fileManager.loadFile(currentFile);
                editorPanel.setCode(content);
                statusBar.setStatus("Reloaded: " + currentFile.getName());
                updateStatus();
            } catch (IOException e) {
                fileManager.showError("Error reloading file", e.getMessage());
                statusBar.setStatus("Failed to reload file");
            }
        } else {
            statusBar.setStatus("No file to reload");
        }
    }
    
    /**
     * Loads an example file from the samples folder
     */
    public void loadExample(String filename) {
        try {
            // Construct path to samples folder
            File samplesDir = new File("app/samples");
            if (!samplesDir.exists()) {
                samplesDir = new File("samples");
            }
            
            File exampleFile = new File(samplesDir, filename);
            
            if (exampleFile.exists()) {
                String content = fileManager.loadFile(exampleFile);
                editorPanel.setCode(content);
                fileManager.setCurrentFile(null); // Don't set as current file
                stage.setTitle(IDEConfig.WINDOW_TITLE + " - " + filename + " (Example)");
                statusBar.setStatus("Loaded example: " + filename);
                updateStatus();
            } else {
                fileManager.showError("Example not found", "Could not find: " + filename);
                statusBar.setStatus("Example not found");
            }
        } catch (IOException e) {
            fileManager.showError("Error loading example", e.getMessage());
            statusBar.setStatus("Failed to load example");
        }
    }
    
    /**
     * Saves the current file
     */
    public void saveFile() {
        File currentFile = fileManager.getCurrentFile();
        if (currentFile == null) {
            saveFileAs();
        } else {
            saveToFile(currentFile);
        }
    }
    
    /**
     * Shows save dialog and saves to a new file
     */
    public void saveFileAs() {
        File file = fileManager.showSaveDialog();
        if (file != null) {
            saveToFile(file);
        }
    }
    
    /**
     * Saves content to the specified file
     */
    private void saveToFile(File file) {
        try {
            fileManager.saveFile(file, editorPanel.getCode());
            fileManager.setCurrentFile(file);
            stage.setTitle(IDEConfig.WINDOW_TITLE + " - " + file.getName());
            statusBar.setStatus("Saved: " + file.getName());
            updateStatus();
        } catch (IOException e) {
            fileManager.showError("Error saving file", e.getMessage());
            statusBar.setStatus("Failed to save file");
        }
    }
    
    /**
     * Runs the Bisaya++ program
     */
    public void runProgram() {
        String code = editorPanel.getCode();
        
        if (code.trim().isEmpty()) {
            outputPanel.setText("No code to run.");
            outputPanel.setWarningStyle();
            statusBar.setStatus("No code to execute");
            return;
        }
        
        // Update status
        statusBar.setStatus("Running program...");
        
        // Clear previous output
        outputPanel.clear();
        
        // Capture System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        
        try {
            // Redirect output
            PrintStream captureStream = new PrintStream(outputStream);
            System.setOut(captureStream);
            System.setErr(captureStream);
            
            // Run interpreter directly with source code
            Bisaya.runSource(code, captureStream, System.in);
            
            // Display output
            String output = outputStream.toString();
            if (output.isEmpty()) {
                outputPanel.setText(ErrorFormatter.formatSuccess("(Program completed with no output)"));
                outputPanel.setNormalStyle();
            } else {
                outputPanel.setText(ErrorFormatter.formatSuccess(output));
                outputPanel.setNormalStyle();
            }
            
            statusBar.setStatus("✓ Execution completed successfully");
            
        } catch (Exception e) {
            // Display error with formatting
            outputPanel.setErrorStyle();
            
            // Get captured error output
            String errorOutput = outputStream.toString();
            String formattedError;
            
            if (!errorOutput.isEmpty()) {
                formattedError = ErrorFormatter.formatError(errorOutput, code);
            } else {
                formattedError = ErrorFormatter.formatError(e.getMessage(), code);
            }
            
            outputPanel.setText(formattedError);
            
            statusBar.setStatus("✗ Execution failed");
            
        } finally {
            // Restore original streams
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
    
    /**
     * Clears the output area
     */
    public void clearOutput() {
        outputPanel.clear();
        statusBar.setStatus("Output cleared");
    }
    
    /**
     * Updates the status bar with current editor information
     */
    public void updateStatus() {
        try {
            String text = editorPanel.getCode();
            
            // Get position from editor
            String position = editorPanel.getLineColumnPosition();
            
            // Count total lines
            int totalLines = (int) text.chars().filter(ch -> ch == '\n').count() + 1;
            
            String status = String.format("%s | Total Lines: %d | Characters: %d", 
                                         position, totalLines, text.length());
            
            File currentFile = fileManager.getCurrentFile();
            if (currentFile != null) {
                status = currentFile.getName() + " - " + status;
            }
            
            statusBar.setStatus(status);
        } catch (Exception e) {
            statusBar.setStatus("Ready");
        }
    }
}
