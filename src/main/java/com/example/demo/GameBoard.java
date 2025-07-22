package com.example.demo;

import javafx.scene.Group;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * Manages the 2048 game grid and logic (cell creation, movement, merging).
 */
public class GameBoard {
    private static final int distanceBetweenCells = 10;
    private static int HEIGHT = 700;
    private static int n = 4;
    private static double LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;

    private final Cell[][] cells = new Cell[n][n];
    private final Group root;
    private final TextMaker textMaker = TextMaker.getSingleInstance();

    public GameBoard(Group root) {
        this.root = root;
        initializeGrid();
    }

    public static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    public static double getLENGTH() {
        return LENGTH;
    }

    public Cell[][] getCells() {
        return cells;
    }

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

    public void randomFillNumber() {
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
                        if (a == n)
                            break outer;
                    }
                }
            }
        }

        Random random = new Random();
        int xCell = random.nextInt(Math.max(1, aForBound + 1));
        int yCell = random.nextInt(Math.max(1, bForBound + 1));
        boolean putTwo = random.nextInt() % 2 == 0;
        int number = putTwo ? 2 : 4;

        Text text = textMaker.madeText(String.valueOf(number),
                emptyCells[xCell][yCell].getX(),
                emptyCells[xCell][yCell].getY());
        emptyCells[xCell][yCell].setTextClass(text);
        root.getChildren().add(text);
        emptyCells[xCell][yCell].setColorByNumber(number);
    }

    public boolean isFull() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getNumber() == 0) return false;
            }
        }
        return true;
    }

    public boolean reached2048() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.getNumber() == 2048) return true;
            }
        }
        return false;
    }

    public boolean canNotMove() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < n - 1 && cells[i][j].getNumber() == cells[i + 1][j].getNumber())
                    return false;
                if (j < n - 1 && cells[i][j].getNumber() == cells[i][j + 1].getNumber())
                    return false;
            }
        }
        return true;
    }
}
