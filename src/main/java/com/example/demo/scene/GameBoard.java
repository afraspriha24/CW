package com.example.demo.scene;

import com.example.demo.ui.Cell;
import com.example.demo.ui.TextMaker;
import javafx.scene.Group;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * Represents the game board grid for the 2048 game.
 * <p>
 * Manages cell initialization, random tile generation, and basic board state checking
 * (e.g., full board, game over, reached 2048).
 */
public class GameBoard {
    private static final int distanceBetweenCells = 10;
    private static int HEIGHT = 700;
    private static int n = 4;
    private static double LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;

    private final Cell[][] cells = new Cell[n][n];
    private final Group root;
    private final TextMaker textMaker = TextMaker.getSingleInstance();

    /**
     * Constructs a new {@code GameBoard} and initializes the cell grid.
     *
     * @param root the JavaFX Group to which cell graphics will be added
     */
    public GameBoard(Group root) {
        this.root = root;
        initializeGrid();
    }

    /**
     * Sets the board size (n x n) and recalculates cell length.
     *
     * @param number the number of rows and columns for the board
     */
    public static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    /**
     * Returns the current computed tile length.
     *
     * @return length of one tile cell (in pixels)
     */
    public static double getLENGTH() {
        return LENGTH;
    }

    /**
     * Returns the internal 2D array of {@link Cell} objects.
     *
     * @return 2D Cell array representing the grid
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Initializes the board with empty cells and fills two random tiles.
     */
    public void initializeGrid() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell((j) * LENGTH + (j + 1) * distanceBetweenCells,
                        (i) * LENGTH + (i + 1) * distanceBetweenCells, LENGTH, root);
            }
        }
        randomFillNumber();
        randomFillNumber();
    }

    /**
     * Fills a random empty cell with either a 2 or 4.
     */
    public void randomFillNumber() {
        Cell[][] emptyCells = new Cell[n][n];
        int a = 0, b = 0, aForBound = 0, bForBound = 0;

        // Collect all empty cells
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

        // Randomly select one empty cell and assign a new value
        Random random = new Random();
        int xCell = random.nextInt(Math.max(1, aForBound + 1));
        int yCell = random.nextInt(Math.max(1, bForBound + 1));
        int number = random.nextBoolean() ? 2 : 4;

        Text text = textMaker.madeText(String.valueOf(number),
                emptyCells[xCell][yCell].getX(),
                emptyCells[xCell][yCell].getY());

        emptyCells[xCell][yCell].setTextNode(text);
        emptyCells[xCell][yCell].attachTextToRoot();
        emptyCells[xCell][yCell].applyNewValue(number);
    }

    /**
     * Checks whether all cells on the board are filled.
     *
     * @return {@code true} if the board is full; {@code false} otherwise
     */
    public boolean isFull() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getNumber() == 0) return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the board contains a tile with value 2048.
     *
     * @return {@code true} if 2048 tile exists; {@code false} otherwise
     */
    public boolean reached2048() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getNumber() == 2048) return true;
            }
        }
        return false;
    }

    /**
     * Determines if the player has no more valid moves left.
     *
     * @return {@code true} if no moves can be made; {@code false} otherwise
     */
    public boolean canNotMove() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < n - 1 && cells[i][j].getNumber() == cells[i + 1][j].getNumber()) return false;
                if (j < n - 1 && cells[i][j].getNumber() == cells[i][j + 1].getNumber()) return false;
            }
        }
        return true;
    }
}
