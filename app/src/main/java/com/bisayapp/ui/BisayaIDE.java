package com.bisayapp.ui;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * BisayaIDE - GUI Terminal for Bisaya++ Interpreter
 * 
 * Main application class that assembles all UI components
 * 
 * Architecture:
 * - IDEController: Handles all business logic and event coordination
 * - EditorPanel: Code editor component
 * - OutputPanel: Output display component
 * - MenuBarBuilder: Creates menu bar
 * - ToolBarBuilder: Creates toolbar
 * - StatusBar: Status information display
 * - FileManager: File I/O operations
 * - IDEConfig: Configuration constants
 */
public class BisayaIDE extends Application {
    
    @Override
    public void start(Stage stage) {
        stage.setTitle(IDEConfig.WINDOW_TITLE);
        
        // Create UI components
        EditorPanel editorPanel = new EditorPanel();
        OutputPanel outputPanel = new OutputPanel();
        StatusBar statusBar = new StatusBar();
        
        // Create controller
        IDEController controller = new IDEController(stage, editorPanel, outputPanel, statusBar);
        
        // Create menu bar and toolbar
        MenuBarBuilder menuBarBuilder = new MenuBarBuilder(stage, controller);
        ToolBarBuilder toolBarBuilder = new ToolBarBuilder(controller);
        
        MenuBar menuBar = menuBarBuilder.build();
        ToolBar toolBar = toolBarBuilder.build();
        
        // Create split pane
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(IDEConfig.DIVIDER_POSITION);
        splitPane.getItems().addAll(editorPanel, outputPanel);
        
        // Assemble main layout
        BorderPane root = new BorderPane();
        VBox topContainer = new VBox(menuBar, toolBar);
        root.setTop(topContainer);
        root.setCenter(splitPane);
        root.setBottom(statusBar);
        
        // Create scene
        Scene scene = new Scene(root, IDEConfig.WINDOW_WIDTH, IDEConfig.WINDOW_HEIGHT);
        applyStyles(scene);
        
        stage.setScene(scene);
        stage.show();
        
        // Set initial focus to editor
        editorPanel.getCodeEditor().requestFocus();
        
        // Initial status update
        controller.updateStatus();
    }
    
    /**
     * Applies CSS styling to the scene
     */
    private void applyStyles(Scene scene) {
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
     * Main entry point for GUI application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
