package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Cell {
    private final Rectangle background;
    private final Group root;
    private Text textNode;
    private boolean modified = false;

    public Cell(double x, double y, double size, Group root) {
        this.root = root;
        this.background = new Rectangle(x, y, size, size);
        this.background.setArcWidth(20); // Rounded corners
        this.background.setArcHeight(20);
        this.background.setFill(getColorForNumber(0));

        this.textNode = TextMaker.getSingleInstance().madeText("0", x, y);
        this.textNode.setFont(Font.font("Arial", FontWeight.BOLD, (size / 2.5)));
        centerText();
        root.getChildren().add(background);
    }

    private void centerText() {
        double centerX = background.getX() + background.getWidth() / 2;
        double centerY = background.getY() + background.getHeight() / 2;
        textNode.setX(centerX - textNode.getLayoutBounds().getWidth() / 2);
        textNode.setY(centerY + textNode.getLayoutBounds().getHeight() / 4);
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isModified() {
        return modified;
    }

    public void setTextNode(Text newText) {
        this.textNode = newText;
    }

    public int getNumber() {
        return Integer.parseInt(textNode.getText());
    }

    public double getX() {
        return background.getX();
    }

    public double getY() {
        return background.getY();
    }

    public void updateVisuals() {
        updateColor();
        updateTextVisibility();
    }

    public void swapContent(Cell target) {
        TextMaker.changeTwoText(this.textNode, target.textNode);

        root.getChildren().removeAll(this.textNode, target.textNode);
        if (!this.isEmpty()) root.getChildren().add(this.textNode);
        if (!target.isEmpty()) root.getChildren().add(target.textNode);

        this.updateColor();
        target.updateColor();
    }

    public int mergeInto(Cell target) {
        int mergedValue = this.getNumber() + target.getNumber();
        target.textNode.setText(String.valueOf(mergedValue));
        this.textNode.setText("0");

        root.getChildren().remove(this.textNode);
        target.updateColor();
        this.updateColor();

        return mergedValue;
    }

    public void applyNewValue(int value) {
        textNode.setText(String.valueOf(value));
        updateVisuals();
    }

    public void attachTextToRoot() {
        root.getChildren().add(textNode);
    }

    private void updateTextVisibility() {
        if (!root.getChildren().contains(textNode) && !isEmpty()) {
            root.getChildren().add(textNode);
        }
    }

    private void updateColor() {
        background.setFill(getColorForNumber(getNumber()));
    }

    private boolean isEmpty() {
        return getNumber() == 0;
    }

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
