package com.example.demo.ui;

import com.example.demo.scene.GameBoard;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Singleton utility for creating and modifying JavaFX Text nodes in the game.
 */
public class TextMaker {

    private static TextMaker instance;

    private TextMaker() {
        // private constructor for singleton
    }

    /**
     * Returns the singleton instance of TextMaker.
     */
    public static TextMaker getSingleInstance() {
        if (instance == null) {
            instance = new TextMaker();
        }
        return instance;
    }

    /**
     * Creates a styled Text node to represent a tile's number.
     *
     * @param value  The number to display
     * @param xCell  X coordinate of the tile
     * @param yCell  Y coordinate of the tile
     * @return Configured Text node
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
     * Swaps the values and positions of two Text nodes.
     *
     * @param first  First Text node
     * @param second Second Text node
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
