package com.example.demo.scene;

import com.example.demo.data.Account;
import com.example.demo.ui.Cell;
import com.example.demo.ui.TextMaker;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Handles the main 2048 gameplay scene, including tile movement, scoring,
 * and UI updates.
 * <p>
 * Responsible for rendering the board, reacting to key events, detecting game
 * over or victory, and triggering restarts or home screen transitions.
 */
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
    private Account currentPlayer;
    private boolean hasShownWinAlert = false;

    /**
     * Sets the board size (e.g., 4x4) and recalculates the tile length.
     *
     * @param number size of the board (number of rows and columns)
     */
    public static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    /**
     * Returns the current calculated cell length.
     *
     * @return tile width/height in pixels
     */
    public static double getLENGTH() {
        return LENGTH;
    }

    /**
     * Initializes the game scene, sets up key handlers, score display, buttons,
     * and begins gameplay.
     *
     * @param gameScene    main scene object
     * @param root         root node (Group) for UI elements
     * @param primaryStage stage reference
     * @param endGameScene scene to show when game ends
     * @param endGameRoot  root of the endgame screen
     * @param player       current player's account
     * @param onRestart    callback to restart the game
     * @param goHome       callback to return to home screen
     */
    public void game(Scene gameScene, Group root, Stage primaryStage, Scene endGameScene, Group endGameRoot,
                     Account player, Runnable onRestart, Runnable goHome) {

        this.root = root;
        this.currentPlayer = player;
        this.score = 0;
        this.hasShownWinAlert = false;
        root.getChildren().clear();

        // Initialize cells
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell((j) * LENGTH + (j + 1) * distanceBetweenCells,
                        (i) * LENGTH + (i + 1) * distanceBetweenCells, LENGTH, root);
            }
        }

        // Score label and value
        Text label = new Text("SCORE:");
        label.setFont(Font.font(30));
        label.relocate(750, 100);
        root.getChildren().add(label);

        scoreText = new Text("0");
        scoreText.setFont(Font.font(20));
        scoreText.relocate(750, 150);
        root.getChildren().add(scoreText);

        // Restart button
        Button restartBtn = new Button("Restart");
        restartBtn.setPrefSize(100, 30);
        restartBtn.setLayoutX(750);
        restartBtn.setLayoutY(200);
        restartBtn.setOnAction(e -> onRestart.run());
        restartBtn.setFocusTraversable(false);
        root.getChildren().add(restartBtn);

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setPrefSize(100, 30);
        backBtn.setLayoutX(750);
        backBtn.setLayoutY(240);
        backBtn.setOnAction(e -> goHome.run());
        backBtn.setFocusTraversable(false);
        root.getChildren().add(backBtn);

        // Start game with two random tiles
        randomFillNumber();
        randomFillNumber();

        // Handle arrow key presses
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            Platform.runLater(() -> {
                boolean moved = false;

                if (key.getCode() == KeyCode.LEFT) moved = moveLeft();
                else if (key.getCode() == KeyCode.RIGHT) moved = moveRight();
                else if (key.getCode() == KeyCode.UP) moved = moveUp();
                else if (key.getCode() == KeyCode.DOWN) moved = moveDown();

                scoreText.setText(score + "");

                if (!hasShownWinAlert && reached2048()) {
                    hasShownWinAlert = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("You Win!");
                    alert.setHeaderText("Congratulations!");
                    alert.setContentText("You created the 2048 tile! Continue playing to set a higher score.");
                    alert.showAndWait();
                }

                if (moved) randomFillNumber();

                if (isFull() && canNotMove()) {
                    EndGame.getInstance().endGameShow(
                            endGameScene, endGameRoot, primaryStage, score, onRestart, goHome, currentPlayer
                    );
                }
            });
        });
    }

    /**
     * Places a new tile (2 or 4) at a random empty position.
     */
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

        Text text = textMaker.madeText(String.valueOf(number),
                emptyCells[xCell][yCell].getX(),
                emptyCells[xCell][yCell].getY());

        emptyCells[xCell][yCell].setTextNode(text);
        emptyCells[xCell][yCell].attachTextToRoot();
        emptyCells[xCell][yCell].applyNewValue(number);
    }

    /**
     * @return true if a tile with value 2048 exists
     */
    private boolean reached2048() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getNumber() == 2048) return true;
            }
        }
        return false;
    }

    /**
     * @return true if the board has no empty tiles
     */
    private boolean isFull() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getNumber() == 0) return false;
            }
        }
        return true;
    }

    // --- Movement handlers ---

    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                moved |= moveHorizontally(i, j, passDestination(i, j, 'l'), -1);
            }
            for (int j = 0; j < n; j++) cells[i][j].setModified(false);
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < n; i++) {
            for (int j = n - 2; j >= 0; j--) {
                moved |= moveHorizontally(i, j, passDestination(i, j, 'r'), 1);
            }
            for (int j = 0; j < n; j++) cells[i][j].setModified(false);
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int j = 0; j < n; j++) {
            for (int i = 1; i < n; i++) {
                moved |= moveVertically(i, j, passDestination(i, j, 'u'), -1);
            }
            for (int i = 0; i < n; i++) cells[i][j].setModified(false);
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int j = 0; j < n; j++) {
            for (int i = n - 2; i >= 0; i--) {
                moved |= moveVertically(i, j, passDestination(i, j, 'd'), 1);
            }
            for (int i = 0; i < n; i++) cells[i][j].setModified(false);
        }
        return moved;
    }

    /**
     * Determines how far a tile can travel in a direction until it hits another tile.
     */
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

    // --- Merge and move validation ---

    private boolean isValidDesH(int i, int j, int des, int sign) {
        int target = des + sign;
        return target >= 0 && target < n &&
                cells[i][target].getNumber() == cells[i][j].getNumber() &&
                !cells[i][target].isModified() &&
                cells[i][target].getNumber() != 0;
    }

    private boolean isValidDesV(int i, int j, int des, int sign) {
        int target = des + sign;
        return target >= 0 && target < n &&
                cells[target][j].getNumber() == cells[i][j].getNumber() &&
                !cells[target][j].isModified() &&
                cells[target][j].getNumber() != 0;
    }

    private boolean moveHorizontally(int i, int j, int des, int sign) {
        if (isValidDesH(i, j, des, sign)) {
            score += cells[i][j].mergeInto(cells[i][des + sign]);
            cells[i][des].setModified(true);
            return true;
        } else if (des != j) {
            cells[i][j].swapContent(cells[i][des]);
            return true;
        }
        return false;
    }

    private boolean moveVertically(int i, int j, int des, int sign) {
        if (isValidDesV(i, j, des, sign)) {
            score += cells[i][j].mergeInto(cells[des + sign][j]);
            cells[des][j].setModified(true);
            return true;
        } else if (des != i) {
            cells[i][j].swapContent(cells[des][j]);
            return true;
        }
        return false;
    }

    /**
     * Checks if the board is full and no valid moves remain.
     *
     * @return {@code true} if no moves possible
     */
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
