package com.bisayapp.ui;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * Menu Bar Builder
 * 
 * Creates and configures the IDE menu bar
 */
public class MenuBarBuilder {
    
    private final Stage stage;
    private final IDEController controller;
    
    public MenuBarBuilder(Stage stage, IDEController controller) {
        this.stage = stage;
        this.controller = controller;
    }
    
    /**
     * Builds and returns the configured menu bar
     */
    public MenuBar build() {
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = createFileMenu();
        Menu editMenu = createEditMenu();
        Menu examplesMenu = createExamplesMenu();
        Menu runMenu = createRunMenu();
        
        menuBar.getMenus().addAll(fileMenu, editMenu, examplesMenu, runMenu);
        
        return menuBar;
    }
    
    /**
     * Creates the File menu
     */
    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");
        
        MenuItem newItem = new MenuItem("New");
        newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newItem.setOnAction(e -> controller.newFile());
        
        MenuItem openItem = new MenuItem("Open...");
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openItem.setOnAction(e -> controller.openFile());
        
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveItem.setOnAction(e -> controller.saveFile());
        
        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsItem.setOnAction(e -> controller.saveFileAs());
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, 
                                    new SeparatorMenuItem(), exitItem);
        
        return fileMenu;
    }
    
    /**
     * Creates the Edit menu
     */
    private Menu createEditMenu() {
        Menu editMenu = new Menu("Edit");
        
        MenuItem formatItem = new MenuItem("Format Code");
        formatItem.setAccelerator(new KeyCodeCombination(KeyCode.F, 
                                   KeyCombination.CONTROL_DOWN, 
                                   KeyCombination.SHIFT_DOWN));
        formatItem.setOnAction(e -> controller.formatCode());
        
        editMenu.getItems().add(formatItem);
        
        return editMenu;
    }
    
    /**
     * Creates the Examples menu with sample programs
     */
    private Menu createExamplesMenu() {
        Menu examplesMenu = new Menu("Examples");
        
        // Define sample programs grouped by category
        Menu basicExamples = new Menu("Basic");
        addExampleItem(basicExamples, "hello.bpp", "Hello World");
        addExampleItem(basicExamples, "hello_bisaya.bpp", "Hello Bisaya");
        addExampleItem(basicExamples, "simple.bpp", "Simple Program");
        addExampleItem(basicExamples, "comments_demo.bpp", "Comments Demo");
        
        Menu inputExamples = new Menu("Input (DAWAT)");
        addExampleItem(inputExamples, "simple-dawat.bpp", "Simple Input");
        addExampleItem(inputExamples, "test-dawat-gui.bpp", "Multiple Inputs");
        
        Menu increment3Examples = new Menu("Conditionals (Inc 3)");
        addExampleItem(increment3Examples, "increment3_simple_if.bpp", "Simple IF");
        addExampleItem(increment3Examples, "increment3_if_else.bpp", "IF-ELSE");
        addExampleItem(increment3Examples, "increment3_else_if.bpp", "ELSE-IF");
        addExampleItem(increment3Examples, "increment3_nested.bpp", "Nested IF");
        addExampleItem(increment3Examples, "increment3_complex.bpp", "Complex");
        
        Menu increment4Examples = new Menu("For Loops (Inc 4)");
        addExampleItem(increment4Examples, "increment4_basic_loop.bpp", "Basic Loop");
        addExampleItem(increment4Examples, "increment4_sum.bpp", "Sum");
        addExampleItem(increment4Examples, "increment4_pattern.bpp", "Pattern");
        addExampleItem(increment4Examples, "increment4_nested_loops.bpp", "Nested Loops");
        addExampleItem(increment4Examples, "increment4_loop_conditional.bpp", "Loop with IF");
        
        Menu increment5Examples = new Menu("While Loops (Inc 5)");
        addExampleItem(increment5Examples, "increment5_basic_while.bpp", "Basic While");
        addExampleItem(increment5Examples, "increment5_arithmetic.bpp", "Arithmetic");
        addExampleItem(increment5Examples, "increment5_pattern.bpp", "Pattern");
        addExampleItem(increment5Examples, "increment5_nested_while.bpp", "Nested While");
        addExampleItem(increment5Examples, "increment5_while_conditional.bpp", "While with IF");
        
        examplesMenu.getItems().addAll(basicExamples, inputExamples, increment3Examples, 
                                       increment4Examples, increment5Examples);
        
        return examplesMenu;
    }
    
    /**
     * Adds an example item to a menu
     */
    private void addExampleItem(Menu menu, String filename, String displayName) {
        MenuItem item = new MenuItem(displayName);
        item.setOnAction(e -> controller.loadExample(filename));
        menu.getItems().add(item);
    }
    
    /**
     * Creates the Run menu
     */
    private Menu createRunMenu() {
        Menu runMenu = new Menu("Run");
        
        MenuItem runItem = new MenuItem("Run Program");
        runItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        runItem.setOnAction(e -> controller.runProgram());
        
        MenuItem reloadItem = new MenuItem("Reload File");
        reloadItem.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        reloadItem.setOnAction(e -> controller.reloadFile());
        
        MenuItem clearItem = new MenuItem("Clear Output");
        clearItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        clearItem.setOnAction(e -> controller.clearOutput());
        
        runMenu.getItems().addAll(runItem, reloadItem, new SeparatorMenuItem(), clearItem);
        
        return runMenu;
    }
}
