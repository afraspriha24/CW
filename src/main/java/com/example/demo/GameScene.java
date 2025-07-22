package com.example.demo;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameScene {
    private static final int distanceBetweenCells = 10;
    private static int HEIGHT = 700;
    private static int n = 4;
    private static double LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;

    private Cell[][] cells = new Cell[n][n];
    private final TextMaker textMaker = TextMaker.getSingleInstance();
    private Group root;
    private long score = 0;
    private Text scoreText;

    public static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    public static double getLENGTH() {
        return LENGTH;
    }

    public void game(Scene gameScene, Group root, Stage primaryStage, Scene endGameScene, Group endGameRoot) {
        this.root = root;

        // Create cells
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell((j) * LENGTH + (j + 1) * distanceBetweenCells,
                        (i) * LENGTH + (i + 1) * distanceBetweenCells, LENGTH, root);
            }
        }

        // Add score label
        Text label = new Text("SCORE :");
        label.setFont(Font.font(30));
        label.relocate(750, 100);
        root.getChildren().add(label);

        scoreText = new Text("0");
        scoreText.setFont(Font.font(20));
        scoreText.relocate(750, 150);
        root.getChildren().add(scoreText);

        randomFillNumber();
        randomFillNumber();

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            Platform.runLater(() -> {
                if (key.getCode() == KeyCode.LEFT) moveLeft();
                else if (key.getCode() == KeyCode.RIGHT) moveRight();
                else if (key.getCode() == KeyCode.UP) moveUp();
                else if (key.getCode() == KeyCode.DOWN) moveDown();

                scoreText.setText(score + "");

                int state = checkBoardState();
                if (state == -1 && canNotMove()) {
                    primaryStage.setScene(endGameScene);
                    EndGame.getInstance().endGameShow(endGameScene, endGameRoot, primaryStage, score);
                    root.getChildren().clear();
                    score = 0;
                } else if (state == 1) {
                    randomFillNumber();
                }
            });
        });
    }

    private void randomFillNumber() {
        Cell[][] emptyCells = new Cell[n][n];
        int a = 0, b = 0, aForBound = 0, bForBound = 0;

        outer:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cells[i][j].getNumber() == 0) {
                    emptyCells[a][b] = cells[i][j];
                    if (b < n - 1) {
                        bForBound = b;
                        b++;
                    } else {
                        aForBound = a;
                        a++;
                        b = 0;
                        if (a == n) break outer;
                    }
                }
            }
        }

        int xCell = (int) (Math.random() * (aForBound + 1));
        int yCell = (int) (Math.random() * (bForBound + 1));
        int number = Math.random() < 0.5 ? 2 : 4;

        Text text = textMaker.madeText(String.valueOf(number), emptyCells[xCell][yCell].getX(), emptyCells[xCell][yCell].getY());
        emptyCells[xCell][yCell].setTextClass(text);
        root.getChildren().add(text);
        emptyCells[xCell][yCell].setColorByNumber(number);
    }

    private int checkBoardState() {
        boolean full = true;
        boolean reached = false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int num = cells[i][j].getNumber();
                if (num == 0) full = false;
                if (num == 2048) reached = true;
            }
        }

        if (reached) return 0;
        if (!full) return 1;
        return -1;
    }

    private void moveLeft() {
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                moveHorizontally(i, j, passDestination(i, j, 'l'), -1);
            }
            for (int j = 0; j < n; j++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveRight() {
        for (int i = 0; i < n; i++) {
            for (int j = n - 2; j >= 0; j--) {
                moveHorizontally(i, j, passDestination(i, j, 'r'), 1);
            }
            for (int j = 0; j < n; j++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveUp() {
        for (int j = 0; j < n; j++) {
            for (int i = 1; i < n; i++) {
                moveVertically(i, j, passDestination(i, j, 'u'), -1);
            }
            for (int i = 0; i < n; i++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveDown() {
        for (int j = 0; j < n; j++) {
            for (int i = n - 2; i >= 0; i--) {
                moveVertically(i, j, passDestination(i, j, 'd'), 1);
            }
            for (int i = 0; i < n; i++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private int passDestination(int i, int j, char direction) {
        int coord = (direction == 'l' || direction == 'r') ? j : i;
        int step = (direction == 'l' || direction == 'u') ? -1 : 1;

        while (true) {
            int ni = (direction == 'l' || direction == 'r') ? i : coord + step;
            int nj = (direction == 'l' || direction == 'r') ? coord + step : j;

            if (ni < 0 || ni >= n || nj < 0 || nj >= n) break;
            if (cells[ni][nj].getNumber() != 0) break;

            coord += step;
        }
        return coord;
    }

    private boolean isValidDesH(int i, int j, int des, int sign) {
        int target = des + sign;
        return target >= 0 && target < n &&
                cells[i][target].getNumber() == cells[i][j].getNumber() &&
                !cells[i][target].getModify() &&
                cells[i][target].getNumber() != 0;
    }

    private boolean isValidDesV(int i, int j, int des, int sign) {
        int target = des + sign;
        return target >= 0 && target < n &&
                cells[target][j].getNumber() == cells[i][j].getNumber() &&
                !cells[target][j].getModify() &&
                cells[target][j].getNumber() != 0;
    }

    private void moveHorizontally(int i, int j, int des, int sign) {
        if (isValidDesH(i, j, des, sign)) {
            score += cells[i][j].adder(cells[i][des + sign]);
            cells[i][des].setModify(true);
        } else if (des != j) {
            cells[i][j].changeCell(cells[i][des]);
        }
    }

    private void moveVertically(int i, int j, int des, int sign) {
        if (isValidDesV(i, j, des, sign)) {
            score += cells[i][j].adder(cells[des + sign][j]);
            cells[des][j].setModify(true);
        } else if (des != i) {
            cells[i][j].changeCell(cells[des][j]);
        }
    }

    private boolean canNotMove() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < n - 1 && cells[i][j].getNumber() == cells[i + 1][j].getNumber()) return false;
                if (j < n - 1 && cells[i][j].getNumber() == cells[i][j + 1].getNumber()) return false;
            }
        }
        return true;
    }
}
