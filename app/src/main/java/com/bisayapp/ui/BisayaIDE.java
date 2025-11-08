package com.bisayapp.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import com.bisayapp.Bisaya;

import java.io.*;
import java.nio.file.Files;

/**
 * BisayaIDE - GUI Terminal for Bisaya++ Interpreter
 * 
 * Phase 1 MVP: Basic GUI with editor, output display, and run functionality
 * Features:
 * - Split-pane layout (editor top, output bottom)
 * - Run button to execute Bisaya++ code
 * - File open/save operations
 * - Error display in red, output in green
 */
public class BisayaIDE extends Application {
    
    // UI Components
    private TextArea codeEditor;
    private TextArea outputArea;
    private Stage primaryStage;
    private File currentFile;
    private Label statusLabel;
    
    // UI Configuration
    private static final String WINDOW_TITLE = "Bisaya++ IDE";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final double DIVIDER_POSITION = 0.6; // 60% editor, 40% output
    
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle(WINDOW_TITLE);
        
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        
        // Create split pane with editor and output
        SplitPane splitPane = createSplitPane();
        root.setCenter(splitPane);
        
        // Create toolbar with Run button
        ToolBar toolBar = createToolBar();
        VBox topContainer = new VBox(menuBar, toolBar);
        root.setTop(topContainer);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        // Create scene and show stage
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Apply CSS styling
        applyStyles(scene);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Set initial focus to editor
        codeEditor.requestFocus();
        
        // Update status on editor changes
        codeEditor.textProperty().addListener((obs, oldText, newText) -> updateStatus());
        codeEditor.caretPositionProperty().addListener((obs, oldPos, newPos) -> updateStatus());
        
        updateStatus();
    }
    
    /**
     * Creates the split pane containing editor and output areas
     */
    private SplitPane createSplitPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(DIVIDER_POSITION);
        
        // Create editor area
        VBox editorContainer = createEditorArea();
        
        // Create output area
        VBox outputContainer = createOutputArea();
        
        splitPane.getItems().addAll(editorContainer, outputContainer);
        
        return splitPane;
    }
    
    /**
     * Creates the code editor area with label
     */
    private VBox createEditorArea() {
        VBox container = new VBox(5);
        container.setPadding(new Insets(5));
        container.setStyle("-fx-background-color: white;");
        
        Label editorLabel = new Label("📝 Code Editor:");
        editorLabel.setFont(Font.font("System", 14));
        editorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        codeEditor = new TextArea();
        codeEditor.setFont(Font.font("Consolas", 14));
        codeEditor.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-control-inner-background: #ffffff;");
        codeEditor.setPromptText("Write your Bisaya++ code here...\n\nExample:\nSUGOD\n    MUGNA NUMERO x=5\n    IPAKITA: \"Hello, Bisaya++\" & $\n    IPAKITA: \"x = \" & x\nKATAPUSAN");
        codeEditor.setWrapText(false);
        
        VBox.setVgrow(codeEditor, Priority.ALWAYS);
        container.getChildren().addAll(editorLabel, codeEditor);
        
        return container;
    }
    
    /**
     * Creates the output display area with label
     */
    private VBox createOutputArea() {
        VBox container = new VBox(5);
        container.setPadding(new Insets(5));
        container.setStyle("-fx-background-color: #f9f9f9;");
        
        Label outputLabel = new Label("📤 Output:");
        outputLabel.setFont(Font.font("System", 14));
        outputLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        outputArea = new TextArea();
        outputArea.setFont(Font.font("Consolas", 13));
        outputArea.setEditable(false);
        outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #00ff00; -fx-background-color: #2b2b2b;");
        outputArea.setPromptText("Program output will appear here...");
        outputArea.setWrapText(true);
        
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        container.getChildren().addAll(outputLabel, outputArea);
        
        return container;
    }
    
    /**
     * Creates the menu bar with File menu
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        
        MenuItem newItem = new MenuItem("New");
        newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newItem.setOnAction(e -> newFile());
        
        MenuItem openItem = new MenuItem("Open...");
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openItem.setOnAction(e -> openFile());
        
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveItem.setOnAction(e -> saveFile());
        
        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsItem.setOnAction(e -> saveFileAs());
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, 
                                    new SeparatorMenuItem(), exitItem);
        
        // Run Menu
        Menu runMenu = new Menu("Run");
        
        MenuItem runItem = new MenuItem("Run Program");
        runItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        runItem.setOnAction(e -> runProgram());
        
        MenuItem clearItem = new MenuItem("Clear Output");
        clearItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        clearItem.setOnAction(e -> clearOutput());
        
        runMenu.getItems().addAll(runItem, clearItem);
        
        menuBar.getMenus().addAll(fileMenu, runMenu);
        
        return menuBar;
    }
    
    /**
     * Creates the toolbar with Run and Clear buttons
     */
    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5;");
        
        Button runButton = new Button("▶ Run");
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        runButton.setOnAction(e -> runProgram());
        runButton.setTooltip(new Tooltip("Run the program (Ctrl+R)"));
        
        Button clearButton = new Button("🗑 Clear Output");
        clearButton.setStyle("-fx-padding: 8 20 8 20; -fx-cursor: hand;");
        clearButton.setOnAction(e -> clearOutput());
        clearButton.setTooltip(new Tooltip("Clear the output area (Ctrl+L)"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolBar.getItems().addAll(runButton, clearButton, spacer);
        
        return toolBar;
    }
    
    /**
     * Creates the status bar at the bottom
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #e8e8e8; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #333333;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("Bisaya++ v1.0");
        versionLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        
        return statusBar;
    }
    
    /**
     * Applies CSS styling to the scene
     */
    private void applyStyles(Scene scene) {
        // Add custom CSS styles
        String css = """
            .text-area {
                -fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
            }
            .menu-bar {
                -fx-background-color: #f8f8f8;
            }
            .button:hover {
                -fx-opacity: 0.8;
            }
            """;
        
        scene.getRoot().setStyle(css);
    }
    
    /**
     * Updates the status bar with current line/column information
     */
    private void updateStatus() {
        try {
            String text = codeEditor.getText();
            int caretPos = codeEditor.getCaretPosition();
            
            // Calculate line and column
            int line = 1;
            int column = 1;
            
            for (int i = 0; i < caretPos && i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
            }
            
            // Count total lines
            int totalLines = (int) text.chars().filter(ch -> ch == '\n').count() + 1;
            
            String status = String.format("Line %d, Column %d | Total Lines: %d | Characters: %d", 
                                         line, column, totalLines, text.length());
            
            if (currentFile != null) {
                status = currentFile.getName() + " - " + status;
            }
            
            statusLabel.setText(status);
        } catch (Exception e) {
            statusLabel.setText("Ready");
        }
    }
    
    /**
     * Creates a new empty file
     */
    private void newFile() {
        // Check if user wants to save current file
        if (!codeEditor.getText().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("New File");
            confirm.setHeaderText("Create new file?");
            confirm.setContentText("Current code will be lost. Continue?");
            
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }
        
        codeEditor.clear();
        currentFile = null;
        primaryStage.setTitle(WINDOW_TITLE);
        statusLabel.setText("New file created");
        updateStatus();
    }
    
    /**
     * Opens a file dialog and loads a .bpp file
     */
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Bisaya++ File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Bisaya++ Files", "*.bpp")
        );
        
        // Set initial directory to samples folder if it exists
        File samplesDir = new File("samples");
        if (samplesDir.exists() && samplesDir.isDirectory()) {
            fileChooser.setInitialDirectory(samplesDir);
        }
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            loadFile(file);
        }
    }
    
    /**
     * Loads a file into the editor
     */
    private void loadFile(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            codeEditor.setText(content);
            currentFile = file;
            primaryStage.setTitle(WINDOW_TITLE + " - " + file.getName());
            statusLabel.setText("Opened: " + file.getName());
            updateStatus();
        } catch (IOException e) {
            showError("Error loading file", e.getMessage());
            statusLabel.setText("Failed to open file");
        }
    }
    
    /**
     * Saves the current file
     */
    private void saveFile() {
        if (currentFile == null) {
            saveFileAs();
        } else {
            saveToFile(currentFile);
        }
    }
    
    /**
     * Shows save dialog and saves to a new file
     */
    private void saveFileAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Bisaya++ File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Bisaya++ Files", "*.bpp")
        );
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            // Add .bpp extension if not present
            if (!file.getName().endsWith(".bpp")) {
                file = new File(file.getAbsolutePath() + ".bpp");
            }
            saveToFile(file);
        }
    }
    
    /**
     * Saves content to the specified file
     */
    private void saveToFile(File file) {
        try {
            Files.write(file.toPath(), codeEditor.getText().getBytes());
            currentFile = file;
            primaryStage.setTitle(WINDOW_TITLE + " - " + file.getName());
            statusLabel.setText("Saved: " + file.getName());
            updateStatus();
        } catch (IOException e) {
            showError("Error saving file", e.getMessage());
            statusLabel.setText("Failed to save file");
        }
    }
    
    /**
     * Runs the Bisaya++ program in the editor
     */
    private void runProgram() {
        String code = codeEditor.getText();
        
        if (code.trim().isEmpty()) {
            outputArea.setText("No code to run.");
            outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #ffaa00; -fx-background-color: #2b2b2b;");
            statusLabel.setText("No code to execute");
            return;
        }
        
        // Update status
        statusLabel.setText("Running program...");
        
        // Clear previous output
        outputArea.clear();
        outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #00ff00; -fx-background-color: #2b2b2b;"); // Green for output
        
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
                outputArea.setText("✓ Program executed successfully (no output).");
                outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #00ff00; -fx-background-color: #2b2b2b;");
            } else {
                outputArea.setText(output);
                outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #00ff00; -fx-background-color: #2b2b2b;");
            }
            
            statusLabel.setText("✓ Execution completed successfully");
            
        } catch (Exception e) {
            // Display error in red
            outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #ff4444; -fx-background-color: #2b2b2b;"); // Red for errors
            
            // Get captured error output
            String errorOutput = outputStream.toString();
            if (!errorOutput.isEmpty()) {
                outputArea.setText("❌ Error:\n\n" + errorOutput);
            } else {
                outputArea.setText("❌ Error:\n\n" + e.getMessage());
            }
            
            statusLabel.setText("✗ Execution failed");
            
        } finally {
            // Restore original streams
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
    
    /**
     * Clears the output area
     */
    private void clearOutput() {
        outputArea.clear();
        outputArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #00ff00; -fx-background-color: #2b2b2b;");
        statusLabel.setText("Output cleared");
    }
    
    /**
     * Shows an error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Main entry point for GUI application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
