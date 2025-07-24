package com.example.demo.ui;

import com.example.demo.scene.GameBoard;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Singleton utility class for creating and managing {@link javafx.scene.text.Text} nodes
 * used to display numbers on 2048 game tiles.
 * <p>
 * Provides standardized styling, positioning, and swapping of Text nodes.
 */
public class TextMaker {

    private static TextMaker instance;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private TextMaker() {
        // Prevent external instantiation
    }

    /**
     * Returns the single shared instance of TextMaker.
     *
     * @return the singleton instance
     */
    public static TextMaker getSingleInstance() {
        if (instance == null) {
            instance = new TextMaker();
        }
        return instance;
    }

    /**
     * Creates a styled and positioned {@link Text} node representing a tile's number.
     * Font size and alignment are computed based on tile size from {@link GameBoard#getLENGTH()}.
     *
     * @param value the number to display (e.g., "2", "64", "1024")
     * @param xCell the X coordinate of the cell
     * @param yCell the Y coordinate of the cell
     * @return a styled and positioned Text node
     */
    public Text madeText(String value, double xCell, double yCell) {
        double length = GameBoard.getLENGTH();
        double fontSize = (3 * length) / 7.0;
        Text text = new Text(value);
        text.setFont(Font.font(fontSize));
        text.relocate(xCell + (1.2 * length / 7.0), yCell + (2 * length / 7.0));
        text.setFill(Color.WHITE);
        return text;
    }

    /**
     * Swaps the contents (text and position) of two {@link Text} nodes.
     * <p>
     * This is used during tile movement to reflect visual changes
     * without needing to recreate nodes.
     *
     * @param first  the first Text node
     * @param second the second Text node
     */
    public static void changeTwoText(Text first, Text second) {
        String temp = first.getText();
        first.setText(second.getText());
        second.setText(temp);

        double tempX = first.getX();
        double tempY = first.getY();

        first.setX(second.getX());
        first.setY(second.getY());

        second.setX(tempX);
        second.setY(tempY);
    }
}
