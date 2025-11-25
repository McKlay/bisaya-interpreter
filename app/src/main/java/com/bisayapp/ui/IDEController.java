package com.bisayapp.ui;

import com.bisayapp.Bisaya;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

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
            // First try to load from classpath (JAR resources)
            String resourcePath = "/samples/" + filename;
            java.io.InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            
            if (inputStream != null) {
                // Load from JAR resources
                String content = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                inputStream.close();
                editorPanel.setCode(content);
                fileManager.setCurrentFile(null); // Don't set as current file
                stage.setTitle(IDEConfig.WINDOW_TITLE + " - " + filename + " (Example)");
                statusBar.setStatus("Loaded example: " + filename);
                updateStatus();
            } else {
                // Fallback: try to load from filesystem (for development)
                File samplesDir = new File("app/samples");
                if (!samplesDir.exists()) {
                    samplesDir = new File("samples");
                }
                
                File exampleFile = new File(samplesDir, filename);
                
                if (exampleFile.exists()) {
                    String content = fileManager.loadFile(exampleFile);
                    editorPanel.setCode(content);
                    fileManager.setCurrentFile(null);
                    stage.setTitle(IDEConfig.WINDOW_TITLE + " - " + filename + " (Example)");
                    statusBar.setStatus("Loaded example: " + filename);
                    updateStatus();
                } else {
                    fileManager.showError("Example not found", "Could not find: " + filename);
                    statusBar.setStatus("Example not found");
                }
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
     * Runs the Bisaya++ program with threading and DAWAT support
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
        
        // Create GUI I/O handler for interactive input support
        GUIIOHandler ioHandler = new GUIIOHandler(outputPanel);
        
        // Run in background thread to keep UI responsive
        Thread executionThread = new Thread(() -> {
            try {
                // Run interpreter with GUI I/O handler
                Bisaya.runSource(code, ioHandler);
                
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    String output = ioHandler.getOutput();
                    if (output.isEmpty()) {
                        outputPanel.appendText("\n" + ErrorFormatter.formatSuccess("(Program completed with no output)"));
                    }
                    outputPanel.setNormalStyle();
                    statusBar.setStatus("✓ Execution completed successfully");
                });
                
            } catch (Exception e) {
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    outputPanel.setErrorStyle();
                    
                    // Get any output that was generated before the error
                    String output = ioHandler.getOutput();
                    
                    String formattedError;
                    if (!output.isEmpty()) {
                        formattedError = ErrorFormatter.formatError(e.getMessage(), code);
                    } else {
                        formattedError = ErrorFormatter.formatError(e.getMessage(), code);
                    }
                    
                    outputPanel.appendText("\n" + formattedError);
                    statusBar.setStatus("✗ Execution failed");
                });
            }
        });
        
        executionThread.setDaemon(true);
        executionThread.setName("Bisaya-Execution-Thread");
        executionThread.start();
    }
    
    /**
     * Clears the output area
     */
    public void clearOutput() {
        outputPanel.clear();
        statusBar.setStatus("Output cleared");
    }
    
    /**
     * Formats the current code with consistent indentation and spacing
     * Supports both full document and selection formatting
     */
    public void formatCode() {
        String originalCode = editorPanel.getCode();
        
        if (originalCode.trim().isEmpty()) {
            statusBar.setStatus("No code to format");
            return;
        }
        
        try {
            boolean hasSelection = editorPanel.hasSelection();
            String formattedCode;
            long startTime = System.currentTimeMillis();
            
            if (hasSelection) {
                // Format only selected text
                int selStart = editorPanel.getSelectionStart();
                int selEnd = editorPanel.getSelectionEnd();
                
                // Save selection bounds for restoration
                int originalSelStart = selStart;
                int originalSelEnd = selEnd;
                
                // Format the selection
                formattedCode = PrettyPrinter.formatSelection(originalCode, selStart, selEnd);
                
                // Check if code actually changed
                if (formattedCode.equals(originalCode)) {
                    statusBar.setStatus("Selection already formatted");
                    return;
                }
                
                // Replace entire code
                editorPanel.setCode(formattedCode);
                
                // Try to restore selection (may shift due to formatting)
                int lengthDiff = formattedCode.length() - originalCode.length();
                editorPanel.selectRange(originalSelStart, originalSelEnd + lengthDiff);
                
                long duration = System.currentTimeMillis() - startTime;
                statusBar.setStatus(String.format("✓ Selection formatted (%dms)", duration));
                
            } else {
                // Format entire document
                // Save caret position (as percentage of total length)
                int caretPos = editorPanel.getCodeEditor().getCaretPosition();
                double caretPercentage = originalCode.length() > 0 
                    ? (double) caretPos / originalCode.length() 
                    : 0.0;
                
                // Format the code
                formattedCode = PrettyPrinter.format(originalCode);
                
                // Check if code actually changed
                if (formattedCode.equals(originalCode)) {
                    statusBar.setStatus("Code already formatted");
                    return;
                }
                
                // Replace code in editor
                editorPanel.setCode(formattedCode);
                
                // Restore caret position (approximate)
                int newCaretPos = (int) (formattedCode.length() * caretPercentage);
                newCaretPos = Math.max(0, Math.min(newCaretPos, formattedCode.length()));
                editorPanel.getCodeEditor().moveTo(newCaretPos);
                
                long duration = System.currentTimeMillis() - startTime;
                statusBar.setStatus(String.format("✓ Code formatted successfully (%dms)", duration));
            }
            
        } catch (Exception e) {
            // On error, keep original code
            statusBar.setStatus("Format failed: " + e.getMessage());
            System.err.println("Format error: " + e.getMessage());
            e.printStackTrace();
        }
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
    
    /**
     * Shows the Language Reference dialog
     */
    public void showLanguageReference() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bisaya++ Language Reference");
        alert.setHeaderText("Bisaya++ Programming Language");
        
        String content = """
            BASIC STRUCTURE:
            • SUGOD ... KATAPUSAN - Program boundaries
            • @@ - Comments (start of line or inline)
            • $ - Newline character
            • & - String concatenation
            • [] - Escape characters
            
            DATA TYPES:
            • NUMERO - Integer (4 bytes)
            • LETRA - Single character
            • TINUOD - Boolean ("OO"=true, "DILI"=false)
            • TIPIK - Floating point number
            
            DECLARATION:
            • MUGNA <type> <var>[=<value>][,<var>[=<value>]]*
            
            I/O OPERATIONS:
            • IPAKITA: <expression> - Output
            • DAWAT: <var>[,<var>]* - Input
            
            OPERATORS:
            • Arithmetic: +, -, *, /, %
            • Comparison: >, <, >=, <=, ==, <>
            • Logical: UG (AND), O (OR), DILI (NOT)
            • Unary: ++, --, +, -
            
            CONTROL STRUCTURES:
            • KUNG (<condition>) PUNDOK{ } - If
            • KUNG WALA PUNDOK{ } - Else
            • KUNG DILI (<condition>) PUNDOK{ } - Else if
            • ALANG SA (<init>, <cond>, <update>) PUNDOK{ } - For loop
            • SAMTANG (<condition>) PUNDOK{ } - While loop
            
            For detailed documentation, visit:
            https://github.com/McKlay/bisaya-interpreter
            """;
        
        alert.setContentText(content);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(600);
        alert.showAndWait();
    }
    
    /**
     * Shows the Keyboard Shortcuts dialog
     */
    public void showKeyboardShortcuts() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Keyboard Shortcuts");
        alert.setHeaderText("Bisaya++ IDE Shortcuts");
        
        String content = """
            FILE OPERATIONS:
            • Ctrl+N - New File
            • Ctrl+O - Open File
            • Ctrl+S - Save File
            • Ctrl+Shift+S - Save As
            
            EDITING:
            • Ctrl+Shift+F - Format Code
            • Ctrl+Z - Undo
            • Ctrl+Y - Redo
            • Ctrl+A - Select All
            • Ctrl+C - Copy
            • Ctrl+X - Cut
            • Ctrl+V - Paste
            
            RUNNING:
            • Ctrl+R - Run Program
            • F5 - Reload File
            • Ctrl+L - Clear Output
            
            HELP:
            • F1 - Language Reference
            • Ctrl+Shift+K - Keyboard Shortcuts
            """;
        
        alert.setContentText(content);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }
    
    /**
     * Shows the About dialog with version and creator information
     */
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Bisaya++");
        alert.setHeaderText("Bisaya++ Programming Language");
        
        String content = """
            Version: 1.0.0
            
            A Cebuano-based educational programming language designed
            to teach programming fundamentals using native keywords.
            
            CREATOR:
            Clay Mark Sarte
            
            LINKS:
            • GitHub: https://github.com/McKlay/bisaya-interpreter
            • LinkedIn: https://www.linkedin.com/in/clay-mark-sarte-283855147/
            
            FEATURES:
            ✓ Native Cebuano syntax
            ✓ Strong typing system
            ✓ Control structures (IF, FOR, WHILE)
            ✓ Interactive IDE with syntax highlighting
            ✓ Real-time error detection
            ✓ Code formatting
            
            © 2025 Clay Mark Sarte. All rights reserved.
            
            Licensed for educational purposes.
            """;
        
        alert.setContentText(content);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(550);
        alert.showAndWait();
    }
}

