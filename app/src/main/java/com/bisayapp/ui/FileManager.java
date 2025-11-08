package com.bisayapp.ui;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * File Manager for IDE
 * 
 * Handles all file operations: open, save, save as
 */
public class FileManager {
    
    private final Stage stage;
    private File currentFile;
    
    public FileManager(Stage stage) {
        this.stage = stage;
        this.currentFile = null;
    }
    
    /**
     * Gets the current file
     */
    public File getCurrentFile() {
        return currentFile;
    }
    
    /**
     * Sets the current file
     */
    public void setCurrentFile(File file) {
        this.currentFile = file;
    }
    
    /**
     * Shows file chooser for opening a file
     * 
     * @return Selected file or null if cancelled
     */
    public File showOpenDialog() {
        FileChooser fileChooser = createFileChooser("Open Bisaya++ File");
        return fileChooser.showOpenDialog(stage);
    }
    
    /**
     * Shows file chooser for saving a file
     * 
     * @return Selected file or null if cancelled
     */
    public File showSaveDialog() {
        FileChooser fileChooser = createFileChooser("Save Bisaya++ File");
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null && !file.getName().endsWith(".bpp")) {
            file = new File(file.getAbsolutePath() + ".bpp");
        }
        
        return file;
    }
    
    /**
     * Loads content from a file
     * 
     * @param file File to load
     * @return File content as string
     * @throws IOException If file cannot be read
     */
    public String loadFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
    
    /**
     * Saves content to a file
     * 
     * @param file File to save to
     * @param content Content to save
     * @throws IOException If file cannot be written
     */
    public void saveFile(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes());
    }
    
    /**
     * Shows an error alert
     * 
     * @param title Alert title
     * @param message Error message
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Creates a configured file chooser
     */
    private FileChooser createFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(IDEConfig.FILE_DESCRIPTION, IDEConfig.FILE_EXTENSION)
        );
        
        // Set initial directory to samples folder if it exists
        File samplesDir = new File(IDEConfig.SAMPLES_DIR);
        if (samplesDir.exists() && samplesDir.isDirectory()) {
            fileChooser.setInitialDirectory(samplesDir);
        }
        
        return fileChooser;
    }
}
