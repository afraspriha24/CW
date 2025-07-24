package com.example.demo.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Represents a single tile or "cell" on the 2048 game grid.
 * <p>
 * This class manages both the visual and logical representation of a tile:
 * - Displaying and updating number values
 * - Handling merges and swaps with other cells
 * - Dynamically adjusting color based on value
 */
public class Cell {
    private final Rectangle background;
    private final Group root;
    private Text textNode;
    private boolean modified = false;

    /**
     * Constructs a new Cell at a given position and size on the game board.
     *
     * @param x    the X coordinate of the cell
     * @param y    the Y coordinate of the cell
     * @param size the width/height of the square tile
     * @param root the JavaFX Group to attach visual elements
     */
    public Cell(double x, double y, double size, Group root) {
        this.root = root;
        this.background = new Rectangle(x, y, size, size);
        this.background.setArcWidth(20);
        this.background.setArcHeight(20);
        this.background.setFill(getColorForNumber(0));

        this.textNode = TextMaker.getSingleInstance().madeText("0", x, y);
        this.textNode.setFont(Font.font("Arial", FontWeight.BOLD, (size / 2.5)));
        centerText();
        root.getChildren().add(background);
    }

    /**
     * Centers the textNode inside the background rectangle.
     */
    private void centerText() {
        double centerX = background.getX() + background.getWidth() / 2;
        double centerY = background.getY() + background.getHeight() / 2;
        textNode.setX(centerX - textNode.getLayoutBounds().getWidth() / 2);
        textNode.setY(centerY + textNode.getLayoutBounds().getHeight() / 4);
    }

    /**
     * Marks the cell as modified during a move (used to prevent double merges).
     *
     * @param modified true if the cell has already merged
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * Returns whether this cell was modified during a move.
     *
     * @return true if modified, false otherwise
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Replaces the current text node (used for reassigning during generation).
     *
     * @param newText the new text node to attach
     */
    public void setTextNode(Text newText) {
        this.textNode = newText;
    }

    /**
     * Returns the numerical value currently displayed in this cell.
     *
     * @return the number as an integer
     */
    public int getNumber() {
        return Integer.parseInt(textNode.getText());
    }

    /**
     * Gets the X coordinate of this cell.
     *
     * @return X position
     */
    public double getX() {
        return background.getX();
    }

    /**
     * Gets the Y coordinate of this cell.
     *
     * @return Y position
     */
    public double getY() {
        return background.getY();
    }

    /**
     * Updates the cell visuals — background color and text visibility.
     */
    public void updateVisuals() {
        updateColor();
        updateTextVisibility();
    }

    /**
     * Swaps the content (value and visuals) between this cell and another.
     *
     * @param target the other cell to swap with
     */
    public void swapContent(Cell target) {
        TextMaker.changeTwoText(this.textNode, target.textNode);

        root.getChildren().removeAll(this.textNode, target.textNode);
        if (!this.isEmpty()) root.getChildren().add(this.textNode);
        if (!target.isEmpty()) root.getChildren().add(target.textNode);

        this.updateColor();
        target.updateColor();
    }

    /**
     * Merges this cell into the target cell and returns the resulting value.
     * This cell becomes 0 after merging.
     *
     * @param target the cell to merge into
     * @return the new merged value
     */
    public int mergeInto(Cell target) {
        int mergedValue = this.getNumber() + target.getNumber();
        target.textNode.setText(String.valueOf(mergedValue));
        this.textNode.setText("0");

        root.getChildren().remove(this.textNode);
        target.updateColor();
        this.updateColor();

        return mergedValue;
    }

    /**
     * Sets the cell's number and updates its visuals accordingly.
     *
     * @param value the number to assign
     */
    public void applyNewValue(int value) {
        textNode.setText(String.valueOf(value));
        updateVisuals();
    }

    /**
     * Adds this cell's text node to the root group.
     */
    public void attachTextToRoot() {
        root.getChildren().add(textNode);
    }

    /**
     * Ensures the text node is visible if the number is non-zero.
     */
    private void updateTextVisibility() {
        if (!root.getChildren().contains(textNode) && !isEmpty()) {
            root.getChildren().add(textNode);
        }
    }

    /**
     * Updates the cell's background color based on its number.
     */
    private void updateColor() {
        background.setFill(getColorForNumber(getNumber()));
    }

    /**
     * Checks if this cell is empty (i.e., value is 0).
     *
     * @return true if empty, false otherwise
     */
    private boolean isEmpty() {
        return getNumber() == 0;
    }

    /**
     * Returns a color based on the cell's number for display styling.
     *
     * @param number the tile number
     * @return the corresponding color
     */
    private Color getColorForNumber(int number) {
        return switch (number) {
            case 0 -> Color.rgb(224, 226, 226, 0.5);
            case 2 -> Color.rgb(232, 255, 100, 0.5);
            case 4 -> Color.rgb(232, 220, 50, 0.5);
            case 8 -> Color.rgb(232, 200, 44, 0.8);
            case 16 -> Color.rgb(232, 170, 44, 0.8);
            case 32 -> Color.rgb(180, 120, 44, 0.7);
            case 64 -> Color.rgb(180, 100, 44, 0.7);
            case 128 -> Color.rgb(180, 80, 44, 0.7);
            case 256 -> Color.rgb(180, 60, 44, 0.8);
            case 512 -> Color.rgb(180, 30, 44, 0.8);
            case 1024 -> Color.rgb(250, 0, 44, 0.8);
            case 2048 -> Color.rgb(250, 0, 0, 1);
            default -> Color.BLACK;
        };
    }
}
