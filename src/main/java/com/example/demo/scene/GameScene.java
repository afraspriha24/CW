package com.example.demo.scene;

import com.example.demo.data.Account;
import com.example.demo.data.AccountManager;
import com.example.demo.ui.Cell;
import com.example.demo.ui.TextMaker;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Stack;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Pos;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.layout.HBox;

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

    private Cell[][] cells;
    private final TextMaker textMaker = TextMaker.getSingleInstance();
    private Group root;
    private long score = 0;
    private Text scoreText;
    private Account currentPlayer;
    private boolean hasShownWinAlert = false;
    
    // Board positioning variables
    private double boardX;
    private double boardY;
    
    // Undo functionality
    private Stack<GameState> gameHistory = new Stack<>();
    private static final int MAX_UNDO_STEPS = 10;

    /**
     * Represents a snapshot of the game state for undo functionality.
     */
    private static class GameState {
        private final int[][] board;
        private final long score;
        
        public GameState(int[][] board, long score) {
            this.board = new int[board.length][board[0].length];
            for (int i = 0; i < board.length; i++) {
                System.arraycopy(board[i], 0, this.board[i], 0, board[i].length);
            }
            this.score = score;
        }
        
        public int[][] getBoard() {
            return board;
        }
        
        public long getScore() {
            return score;
        }
    }

    /**
     * Sets the board size (e.g., 4x4, 5x5, 6x6) and recalculates the tile length.
     * Also adjusts the game height for larger boards to ensure proper display.
     *
     * @param number size of the board (number of rows and columns)
     */
    public static void setN(int number) {
        if (number < 4 || number > 6) {
            System.err.println("Invalid board size: " + number + ". Using default 4x4.");
            n = 4;
        } else {
            n = number;
        }
        
        // Adjust game height for larger boards
        if (n == 5) {
            HEIGHT = 750; // Slightly larger for 5x5
        } else if (n == 6) {
            HEIGHT = 800; // Larger for 6x6
        } else {
            HEIGHT = 700; // Default for 4x4
        }
        
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
        
        System.out.println("Board size set to " + n + "x" + n);
        System.out.println("Game height: " + HEIGHT);
        System.out.println("Tile length: " + LENGTH);
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
        this.gameHistory.clear();
        root.getChildren().clear();

        // Make root focusable for key events
        root.setFocusTraversable(true);
        
        // Immediately request focus so arrow keys work right away
        Platform.runLater(() -> {
            root.requestFocus();
            System.out.println("Game started - requesting immediate focus for root");
            
            // Also ensure score is displayed immediately
            if (scoreText != null) {
                scoreText.setText(String.valueOf(score));
                System.out.println("Score immediately set to: " + score);
            }
        });
        
        // Additional focus request after a short delay
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    root.requestFocus();
                    System.out.println("Delayed focus request for root");
                });
            }
        }, 100); // 100ms delay

        // Initialize cells array with correct size
        cells = new Cell[n][n];

        // Calculate board dimensions based on board size
        double boardWidth = n * LENGTH + (n - 1) * distanceBetweenCells;
        double boardHeight = n * LENGTH + (n - 1) * distanceBetweenCells;
        
        // Reduce board size slightly to prevent overlapping with instructions
        double adjustedBoardWidth = boardWidth * 0.9;
        double adjustedBoardHeight = boardHeight * 0.9;
        
        // Center the board on screen with adjusted size
        boardX = (1200 - adjustedBoardWidth) / 2;
        boardY = (700 - adjustedBoardHeight) / 2;

        // Add background grid for better visibility
        Rectangle gameBoardBackground = new Rectangle(
            boardX, 
            boardY, 
            adjustedBoardWidth,
            adjustedBoardHeight
        );
        gameBoardBackground.setFill(Color.rgb(238, 228, 218));
        gameBoardBackground.setArcWidth(15);
        gameBoardBackground.setArcHeight(15);
        root.getChildren().add(gameBoardBackground);
        
        // Initialize cells with proper positioning for any board size
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell(
                    boardX + (j) * LENGTH + (j + 1) * distanceBetweenCells,
                    boardY + (i) * LENGTH + (i + 1) * distanceBetweenCells, 
                    LENGTH, 
                    root
                );
                
                // Add click handler to each cell
                final int row = i;
                final int col = j;
                cells[i][j].setOnMouseClicked(e -> {
                    System.out.println("Clicked cell at [" + row + "][" + col + "] with value: " + cells[row][col].getNumber());
                });
            }
        }

        // Enhanced UI Panel on the right - adjust position for larger boards
        VBox uiPanel = new VBox(20);
        // Ensure panel is positioned far enough to the right to not overlap with any board size
        double minPanelX = 970; // Increased from 950 to move panel slightly right
        double calculatedPanelX = boardX + adjustedBoardWidth + 100; // Increased from 80 to 100px gap from board
        uiPanel.setLayoutX(Math.max(minPanelX, calculatedPanelX));
        uiPanel.setLayoutY(50);
        uiPanel.setPrefWidth(250);
        
        // Add window control buttons at the top-right
        HBox windowControls = new HBox(10);
        windowControls.setAlignment(Pos.TOP_RIGHT);
        windowControls.setLayoutX(1080); // Moved from 1100 to 1080 (20px left)
        windowControls.setLayoutY(20);
        
        Button minimizeBtn = createWindowControlButton("−", Color.rgb(255, 193, 7));
        minimizeBtn.setOnAction(e -> primaryStage.setIconified(true));
        
        Button maximizeBtn = createWindowControlButton("□", Color.rgb(40, 167, 69));
        maximizeBtn.setOnAction(e -> {
            if (primaryStage.isMaximized()) {
                primaryStage.setMaximized(false);
                maximizeBtn.setText("□");
            } else {
                primaryStage.setMaximized(true);
                maximizeBtn.setText("❐");
            }
        });
        
        Button windowCloseBtn = createWindowControlButton("×", Color.rgb(220, 53, 69));
        windowCloseBtn.setOnAction(e -> primaryStage.close());
        
        windowControls.getChildren().addAll(minimizeBtn, maximizeBtn, windowCloseBtn);
        root.getChildren().add(windowControls);

        // Player Information
        VBox playerInfo = new VBox(5);
        Text playerLabel = new Text("Player");
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerLabel.setFill(Color.rgb(80, 80, 100));
        
        Text playerName = new Text(currentPlayer.getUserName());
        playerName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playerName.setFill(Color.rgb(255, 140, 0));
        
        Text totalScoreLabel = new Text("Total Score");
        totalScoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        totalScoreLabel.setFill(Color.rgb(100, 100, 120));
        
        Text totalScore = new Text(String.valueOf(currentPlayer.getScore()));
        totalScore.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalScore.setFill(Color.rgb(100, 100, 120));
        
        playerInfo.getChildren().addAll(playerLabel, playerName, totalScoreLabel, totalScore);

        // Current Game Score
        VBox gameScoreInfo = new VBox(5);
        Text gameScoreLabel = new Text("GAME SCORE");
        gameScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gameScoreLabel.setFill(Color.rgb(80, 80, 100));
        
        scoreText = new Text("0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreText.setFill(Color.rgb(255, 140, 0));
        
        gameScoreInfo.getChildren().addAll(gameScoreLabel, scoreText);

        // Game Controls
        VBox controls = new VBox(10);
        
        Button restartBtn = createGameButton("Restart", Color.rgb(100, 149, 237));
        restartBtn.setOnAction(e -> {
            resetGame();
        });

        Button undoBtn = createGameButton("Undo", Color.rgb(138, 43, 226));
        undoBtn.setOnAction(e -> undoMove());

        Button backBtn = createGameButton("Back", Color.rgb(255, 69, 0));
        backBtn.setOnAction(e -> goHome.run());
        
        Button closeBtn = createGameButton("Close", Color.rgb(220, 53, 69));
        closeBtn.setOnAction(e -> {
            // Save data before closing
            if (currentPlayer != null) {
                currentPlayer.addToScore(score);
                AccountManager.saveAllAccounts();
            }
            System.exit(0);
        });
        
        controls.getChildren().addAll(restartBtn, undoBtn, backBtn, closeBtn);

        // Add all UI elements to the panel
        uiPanel.getChildren().addAll(playerInfo, gameScoreInfo, controls);
        root.getChildren().add(uiPanel);

        // Add game instructions on the left side
        VBox instructionsBox = new VBox(8);
        instructionsBox.setLayoutX(30);
        instructionsBox.setLayoutY(100);
        instructionsBox.setAlignment(Pos.TOP_LEFT);
        instructionsBox.setPrefWidth(200);
        
        Text instructionsTitle = new Text("How to Play:");
        instructionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        instructionsTitle.setFill(Color.rgb(80, 80, 100));
        
        Text instructionsText1 = new Text("Use ARROW KEYS to move tiles");
        instructionsText1.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        instructionsText1.setFill(Color.rgb(100, 100, 120));
        
        Text instructionsText2 = new Text("Combine same numbers to reach 2048!");
        instructionsText2.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        instructionsText2.setFill(Color.rgb(100, 100, 120));
        
        Text instructionsText3 = new Text("");
        instructionsText3.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        instructionsText3.setFill(Color.rgb(100, 100, 120));
        
        Text instructionsText4 = new Text("💡 Tip: Click on the game board");
        instructionsText4.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        instructionsText4.setFill(Color.rgb(100, 100, 120));
        
        Text instructionsText5 = new Text("if arrow keys don't work");
        instructionsText5.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        instructionsText5.setFill(Color.rgb(100, 100, 120));
        
        instructionsBox.getChildren().addAll(instructionsTitle, instructionsText1, instructionsText2, 
                                           instructionsText3, instructionsText4, instructionsText5);
        root.getChildren().add(instructionsBox);

        // Start game with two random tiles
        randomFillNumber();
        randomFillNumber();

        // Initialize score display
        updateScoreDisplay();
        
        // Force a second score update to ensure visibility
        Platform.runLater(() -> {
            updateScoreDisplay();
            System.out.println("Score display initialized and updated");
        });
        
        // Ensure focus is maintained when scene is shown
        primaryStage.setOnShown(e -> {
            System.out.println("Stage shown - requesting focus");
            Platform.runLater(() -> {
                root.requestFocus();
                System.out.println("Focus requested after stage shown");
            });
        });
        
        // Also request focus when the root is added to the scene
        root.setOnMouseClicked(e -> {
            root.requestFocus();
            System.out.println("Mouse clicked - requesting focus");
        });

        // Handle arrow key presses with improved event handling
        gameScene.setOnKeyPressed(key -> {
            System.out.println("Scene Key Event: " + key.getCode() + " received");
            Platform.runLater(() -> {
                boolean moved = false;
                System.out.println("Processing key: " + key.getCode());

                if (key.getCode() == KeyCode.LEFT) {
                    System.out.println("Moving LEFT");
                    moved = moveLeft();
                } else if (key.getCode() == KeyCode.RIGHT) {
                    System.out.println("Moving RIGHT");
                    moved = moveRight();
                } else if (key.getCode() == KeyCode.UP) {
                    System.out.println("Moving UP");
                    moved = moveUp();
                } else if (key.getCode() == KeyCode.DOWN) {
                    System.out.println("Moving DOWN");
                    moved = moveDown();
                }

                System.out.println("Moved: " + moved);
                
                // Always update score display
                updateScoreDisplay();

                if (!hasShownWinAlert && reached2048()) {
                    hasShownWinAlert = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("You Win!");
                    alert.setHeaderText("Congratulations!");
                    alert.setContentText("You created the 2048 tile! Continue playing to set a higher score.");
                    alert.showAndWait();
                }

                if (moved) {
                    saveGameState();
                    randomFillNumber();
                    // Update score display after move
                    updateScoreDisplay();
                }

                if (isFull() && canNotMove()) {
                    EndGame.getInstance().endGameShow(
                            endGameScene, endGameRoot, primaryStage, score, onRestart, goHome, currentPlayer
                    );
                }
            });
        });

        // Also add key event handler to the root group as backup
        root.setOnKeyPressed(key -> {
            System.out.println("Root Key Event: " + key.getCode() + " received");
            Platform.runLater(() -> {
                boolean moved = false;
                System.out.println("Processing root key: " + key.getCode());

                if (key.getCode() == KeyCode.LEFT) {
                    System.out.println("Moving LEFT");
                    moved = moveLeft();
                } else if (key.getCode() == KeyCode.RIGHT) {
                    System.out.println("Moving RIGHT");
                    moved = moveRight();
                } else if (key.getCode() == KeyCode.UP) {
                    System.out.println("Moving UP");
                    moved = moveUp();
                } else if (key.getCode() == KeyCode.DOWN) {
                    System.out.println("Moving DOWN");
                    moved = moveDown();
                }

                System.out.println("Moved: " + moved);
                
                // Always update score display
                updateScoreDisplay();

                if (!hasShownWinAlert && reached2048()) {
                    hasShownWinAlert = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("You Win!");
                    alert.setHeaderText("Congratulations!");
                    alert.setContentText("You created the 2048 tile! Continue playing to set a higher score.");
                    alert.showAndWait();
                }

                if (moved) {
                    saveGameState();
                    randomFillNumber();
                    // Update score display after move
                    updateScoreDisplay();
                }

                if (isFull() && canNotMove()) {
                    EndGame.getInstance().endGameShow(
                            endGameScene, endGameRoot, primaryStage, score, onRestart, goHome, currentPlayer
                    );
                }
            });
        });

        // Handle window focus events to ensure key events work
        primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("Window gained focus - requesting root focus");
                Platform.runLater(() -> {
                    root.requestFocus();
                    System.out.println("Root focus requested after window focus");
                    
                    // Also ensure score is visible
                    if (scoreText != null) {
                        scoreText.setText(String.valueOf(score));
                        System.out.println("Score refreshed after window focus: " + score);
                    }
                });
            }
        });
        
        // Also handle root focus events
        root.focusedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Root focus changed: " + oldValue + " -> " + newValue);
        });
        
        // Add key event listener to the primary stage as additional backup
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            System.out.println("Stage Key Event: " + key.getCode() + " received");
            if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.RIGHT || 
                key.getCode() == KeyCode.UP || key.getCode() == KeyCode.DOWN) {
                
                Platform.runLater(() -> {
                    boolean moved = false;
                    System.out.println("Processing stage key: " + key.getCode());

                    if (key.getCode() == KeyCode.LEFT) {
                        System.out.println("Moving LEFT");
                        moved = moveLeft();
                    } else if (key.getCode() == KeyCode.RIGHT) {
                        System.out.println("Moving RIGHT");
                        moved = moveRight();
                    } else if (key.getCode() == KeyCode.UP) {
                        System.out.println("Moving UP");
                        moved = moveUp();
                    } else if (key.getCode() == KeyCode.DOWN) {
                        System.out.println("Moving DOWN");
                        moved = moveDown();
                    }

                    System.out.println("Stage move result: " + moved);
                    
                    // Always update score display
                    updateScoreDisplay();

                    if (!hasShownWinAlert && reached2048()) {
                        hasShownWinAlert = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("You Win!");
                        alert.setHeaderText("Congratulations!");
                        alert.setContentText("You created the 2048 tile! Continue playing to set a higher score.");
                        alert.showAndWait();
                    }

                    if (moved) {
                        saveGameState();
                        randomFillNumber();
                        // Update score display after move
                        updateScoreDisplay();
                    }

                    if (isFull() && canNotMove()) {
                        EndGame.getInstance().endGameShow(
                                endGameScene, endGameRoot, primaryStage, score, onRestart, goHome, currentPlayer
                        );
                    }
                });
            }
        });
    }

    /**
     * Updates the score display text.
     */
    private void updateScoreDisplay() {
        if (scoreText != null) {
            scoreText.setText(String.valueOf(score));
            System.out.println("Score updated to: " + score);
        }
    }

    /**
     * Creates a styled game button with consistent design.
     *
     * @param text the button text
     * @param backgroundColor the background color
     * @return the styled button
     */
    private Button createGameButton(String text, Color backgroundColor) {
        Button button = new Button(text);
        button.setPrefSize(120, 35);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
        button.setTextFill(Color.WHITE);
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                backgroundColor.deriveColor(0, 1, 1, 0.9), new CornerRadii(6), null)));
        });
        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
        });
        
        return button;
    }

    /**
     * Creates a styled window control button with consistent design.
     *
     * @param text the button text
     * @param backgroundColor the background color
     * @return the styled button
     */
    private Button createWindowControlButton(String text, Color backgroundColor) {
        Button button = new Button(text);
        button.setPrefSize(30, 30);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
        button.setTextFill(Color.WHITE);
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                backgroundColor.deriveColor(0, 1, 1, 0.9), new CornerRadii(6), null)));
        });
        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
        });
        
        return button;
    }

    /**
     * Saves the current game state for undo functionality.
     */
    private void saveGameState() {
        int[][] boardState = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boardState[i][j] = cells[i][j].getNumber();
            }
        }
        
        GameState state = new GameState(boardState, score);
        gameHistory.push(state);
        
        // Limit undo history
        if (gameHistory.size() > MAX_UNDO_STEPS) {
            gameHistory.remove(0);
        }
    }

    /**
     * Undoes the last move by restoring the previous game state.
     */
    private void undoMove() {
        if (gameHistory.isEmpty()) {
            return;
        }
        
        GameState previousState = gameHistory.pop();
        int[][] board = previousState.getBoard();
        
        // Restore board state
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j].setNumber(board[i][j]);
                if (board[i][j] != 0) {
                    Text text = textMaker.madeText(String.valueOf(board[i][j]),
                            cells[i][j].getX(), cells[i][j].getY());
                    cells[i][j].setTextNode(text);
                    cells[i][j].attachTextToRoot();
                } else {
                    cells[i][j].removeText();
                }
            }
        }
        
        // Restore score
        score = previousState.getScore();
        scoreText.setText(score + "");
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

    /**
     * Resets the game to initial state.
     * Clears all cells, resets score, and initializes new tiles.
     */
    private void resetGame() {
        // Reset score
        score = 0;
        
        // Clear all cells and their visual elements from the root
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cells[i][j] != null) {
                    // Remove all visual elements (background and text) from the root
                    cells[i][j].removeAllVisuals();
                    cells[i][j] = null;
                }
            }
        }
        
        // Clear game history
        gameHistory.clear();
        hasShownWinAlert = false;
        
        // Initialize new cells array
        cells = new Cell[n][n];
        
        // Recreate all cells with fresh visual elements
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell(
                    boardX + (j) * LENGTH + (j + 1) * distanceBetweenCells,
                    boardY + (i) * LENGTH + (i + 1) * distanceBetweenCells, 
                    LENGTH, 
                    root
                );
                
                // Add click handler to each cell
                final int row = i;
                final int col = j;
                cells[i][j].setOnMouseClicked(e -> {
                    System.out.println("Clicked cell at [" + row + "][" + col + "] with value: " + cells[row][col].getNumber());
                });
            }
        }
        
        // Add two initial random tiles
        randomFillNumber();
        randomFillNumber();
        
        // Update score display
        updateScoreDisplay();
        
        System.out.println("Game reset - new game started");
    }

    // --- Movement handlers ---

    private boolean moveLeft() {
        boolean moved = false;
        System.out.println("moveLeft() called");
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                if (cells[i][j].getNumber() != 0) {
                    System.out.println("Moving cell [" + i + "][" + j + "] with value " + cells[i][j].getNumber());
                    moved |= moveHorizontally(i, j, passDestination(i, j, 'l'), -1);
                }
            }
            for (int j = 0; j < n; j++) cells[i][j].setModified(false);
        }
        System.out.println("moveLeft() result: " + moved);
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
            updateScoreDisplay();
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
            updateScoreDisplay();
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
