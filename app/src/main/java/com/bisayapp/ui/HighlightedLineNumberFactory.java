package com.bisayapp.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import org.fxmisc.richtext.CodeArea;

import java.util.function.IntFunction;

/**
 * Custom Line Number Factory with Current Line Highlighting
 * 
 * Highlights the line number of the current line where the cursor is positioned
 */
public class HighlightedLineNumberFactory {
    
    private static final Color NORMAL_LINE_NUMBER_COLOR = Color.web("#666666");
    private static final Color CURRENT_LINE_NUMBER_COLOR = Color.web("#0000FF"); // Blue
    private static final Color CURRENT_LINE_BG_COLOR = Color.web("#E0E0E0"); // Light gray background
    
    /**
     * Creates a line number factory that highlights the current line number
     */
    public static IntFunction<Node> get(CodeArea codeArea) {
        return get(codeArea, "%d");
    }
    
    /**
     * Creates a line number factory with custom format
     */
    public static IntFunction<Node> get(CodeArea codeArea, String format) {
        return lineNumber -> {
            Label lineLabel = new Label(String.format(format, lineNumber + 1));
            lineLabel.setFont(Font.font("Consolas", 11));
            lineLabel.setPadding(new Insets(0, 10, 0, 5));
            lineLabel.setAlignment(Pos.CENTER_RIGHT);
            lineLabel.setPrefWidth(50); // Fixed width to prevent shifting
            lineLabel.setMinWidth(50);
            lineLabel.setMaxWidth(50);
            
            // Always set light gray background for all line numbers
            lineLabel.setStyle("-fx-background-color: #f0f0f0;");
            
            // Check if this is the current line
            int currentParagraph = codeArea.getCurrentParagraph();
            if (lineNumber == currentParagraph) {
                // Highlight current line number (blue text, bold, darker background)
                lineLabel.setTextFill(CURRENT_LINE_NUMBER_COLOR);
                lineLabel.setFont(Font.font("Consolas", FontPosture.REGULAR, 11));
                lineLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #E0E0E0;");
                lineLabel.setBackground(new Background(
                    new BackgroundFill(CURRENT_LINE_BG_COLOR, null, null)
                ));
            } else {
                // Normal line number (gray text, light gray background)
                lineLabel.setTextFill(NORMAL_LINE_NUMBER_COLOR);
                lineLabel.setBackground(new Background(
                    new BackgroundFill(Color.web("#f0f0f0"), null, null)
                ));
            }
            
            // Update when caret moves
            codeArea.currentParagraphProperty().addListener((obs, oldPara, newPara) -> {
                if (lineNumber == newPara) {
                    lineLabel.setTextFill(CURRENT_LINE_NUMBER_COLOR);
                    lineLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #E0E0E0;");
                    lineLabel.setBackground(new Background(
                        new BackgroundFill(CURRENT_LINE_BG_COLOR, null, null)
                    ));
                } else {
                    lineLabel.setTextFill(NORMAL_LINE_NUMBER_COLOR);
                    lineLabel.setStyle("-fx-background-color: #f0f0f0;");
                    lineLabel.setBackground(new Background(
                        new BackgroundFill(Color.web("#f0f0f0"), null, null)
                    ));
                }
            });
            
            return lineLabel;
        };
    }
}
