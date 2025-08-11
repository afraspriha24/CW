package com.example.demo.scene;

import com.example.demo.data.Account;
import com.example.demo.data.AccountManager;
import com.example.demo.data.DataManager;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

/**
 * Represents the main home screen for the 2048 game.
 * <p>
 * This screen provides the player with options to:
 * <ul>
 *   <li>Start a new game</li>
 *   <li>Create a new user profile</li>
 *   <li>Edit and switch between existing profiles</li>
 *   <li>View recent scores</li>
 *   <li>Read game rules</li>
 *   <li>Exit the application</li>
 * </ul>
 */
public class HomeScreen {
    private final Stage stage;
    private final Runnable onStartNewGame;
    private Account currentPlayer;
    private int selectedBoardSize = 6; // Default board size - changed to 6x6

    /**
     * Constructs the HomeScreen with a player and a callback for starting a new game.
     *
     * @param stage           the primary stage of the application
     * @param currentPlayer   the currently logged-in player
     * @param onStartNewGame  callback to launch the game scene
     */
    public HomeScreen(Stage stage, Account currentPlayer, Runnable onStartNewGame) {
        this.stage = stage;
        this.currentPlayer = currentPlayer;
        this.onStartNewGame = onStartNewGame;
        
        // Set default board size to 6x6
        GameScene.setN(6);
    }

    /**
     * Displays the home screen with title, buttons, and user interaction controls.
     */
    public void show() {
        // Main layout
        VBox layout = new VBox(0); // No spacing between main sections
        layout.setAlignment(Pos.TOP_CENTER);
        // Modern gradient-like background
        layout.setBackground(new Background(new BackgroundFill(
            Color.rgb(245, 245, 250), CornerRadii.EMPTY, null)));
        
        // Top section with window controls and game controls
        VBox topSection = new VBox(15);
        topSection.setAlignment(Pos.TOP_CENTER);
        topSection.setPadding(new Insets(20, 0, 30, 0));
        
        // Window control panel at the top-right
        HBox windowControls = new HBox(10);
        windowControls.setAlignment(Pos.TOP_RIGHT);
        windowControls.setPadding(new Insets(0, 20, 0, 0));
        
        // Create window control buttons
        Button minimizeBtn = createWindowControlButton("−", Color.rgb(255, 193, 7));
        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        
        Button maximizeBtn = createWindowControlButton("□", Color.rgb(40, 167, 69));
        maximizeBtn.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                maximizeBtn.setText("□");
            } else {
                stage.setMaximized(true);
                maximizeBtn.setText("❐");
            }
        });
        
        Button closeBtn = createWindowControlButton("×", Color.rgb(220, 53, 69));
        closeBtn.setOnAction(e -> stage.close());
        
        windowControls.getChildren().addAll(minimizeBtn, maximizeBtn, closeBtn);
        
        // Game control buttons in the top section
        HBox gameControls = new HBox(15);
        gameControls.setAlignment(Pos.CENTER);
        
        Button createButton = createStyledButton("Create New Profile", Color.rgb(100, 149, 237), Color.rgb(255, 255, 255));
        createButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create New Profile");
            dialog.setHeaderText("Enter new username:");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    currentPlayer = AccountManager.findOrCreateAccount(name.trim());
                    DataManager.saveLastPlayer(currentPlayer);
                    // Update the display - we'll need to refresh this
                }
            });
        });

        Button editProfileButton = createStyledButton("Switch Profile", Color.rgb(138, 43, 226), Color.rgb(255, 255, 255));
        editProfileButton.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(currentPlayer.getUserName(),
                    AccountManager.getAllAccounts().stream().map(Account::getUserName).toList());
            dialog.setTitle("Select Profile");
            dialog.setHeaderText("Choose an existing profile:");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(name -> {
                currentPlayer = AccountManager.findOrCreateAccount(name.trim());
                DataManager.saveLastPlayer(currentPlayer);
                // Update the display - we'll need to refresh this
            });
        });

        Button highScoresButton = createStyledButton("High Scores", Color.rgb(255, 69, 0), Color.rgb(255, 255, 255));
        highScoresButton.setOnAction(e -> showRecentScores(currentPlayer));

        Button rulesButton = createStyledButton("Game Rules", Color.rgb(34, 139, 34), Color.rgb(255, 255, 255));
        rulesButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Rules");
            alert.setHeaderText("How to Play 2048");
            alert.setContentText("Use arrow keys to slide tiles.\n"
                    + "When two tiles with the same number touch, they merge!\n"
                    + "Reach 2048 to win!\n\n"
                    + "Board Sizes:\n"
                    + "• 4x4: Classic mode (recommended for beginners)\n"
                    + "• 5x5: More challenging with more tiles\n"
                    + "• 6x6: Expert mode with maximum complexity");
            alert.showAndWait();
        });

        Button exitButton = createStyledButton("Exit Game", Color.rgb(220, 20, 60), Color.rgb(255, 255, 255));
        exitButton.setOnAction(e -> stage.close());
        
        gameControls.getChildren().addAll(createButton, editProfileButton, highScoresButton, rulesButton, exitButton);
        
        // Add both control sections to top section
        topSection.getChildren().addAll(windowControls, gameControls);
        
        // Center section with main content
        VBox centerSection = new VBox(25);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(0, 0, 30, 0));
        
        // Title with enhanced typography
        Text title = new Text("2048");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.rgb(70, 70, 90));
        centerSection.getChildren().add(title);

        Text subtitle = new Text("GAME");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitle.setFill(Color.rgb(100, 100, 120));
        centerSection.getChildren().add(subtitle);

        // Enhanced player profile display
        VBox playerInfoBox = new VBox(5);
        playerInfoBox.setAlignment(Pos.CENTER);
        
        Text playerLabel = new Text("Current Player");
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerLabel.setFill(Color.rgb(80, 80, 100));
        
        Text usernameDisplay = new Text(currentPlayer.getUserName());
        usernameDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        usernameDisplay.setFill(Color.rgb(255, 140, 0));
        
        // Show player stats
        Text statsText = new Text("Total Score: " + currentPlayer.getScore());
        statsText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statsText.setFill(Color.rgb(100, 100, 120));
        
        playerInfoBox.getChildren().addAll(playerLabel, usernameDisplay, statsText);
        centerSection.getChildren().add(playerInfoBox);

        // Board Size Selection with better styling
        VBox boardSizeBox = new VBox(8);
        boardSizeBox.setAlignment(Pos.CENTER);
        
        Text boardSizeLabel = new Text("Board Size");
        boardSizeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        boardSizeLabel.setFill(Color.rgb(80, 80, 100));
        
        ComboBox<String> boardSizeCombo = new ComboBox<>();
        boardSizeCombo.getItems().addAll("4x4 (Classic)", "5x5 (Challenging)", "6x6 (Expert)");
        boardSizeCombo.setValue("6x6 (Expert)");
        boardSizeCombo.setPrefWidth(200);
        boardSizeCombo.setOnAction(e -> {
            String selected = boardSizeCombo.getValue();
            selectedBoardSize = Integer.parseInt(selected.split("x")[0]);
            GameScene.setN(selectedBoardSize);
        });
        
        boardSizeBox.getChildren().addAll(boardSizeLabel, boardSizeCombo);
        centerSection.getChildren().add(boardSizeBox);

        // Avatar Selection with better styling
        VBox avatarBox = new VBox(8);
        avatarBox.setAlignment(Pos.CENTER);
        
        Text avatarLabel = new Text("Avatar");
        avatarLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        avatarLabel.setFill(Color.rgb(80, 80, 100));
        
        ComboBox<String> avatarCombo = new ComboBox<>();
        avatarCombo.getItems().addAll("👤 Player", "🎮 Gamer", "🏆 Champion", "⭐ Star", "🎯 Target");
        avatarCombo.setValue("👤 Player");
        avatarCombo.setPrefWidth(200);
        avatarCombo.setOnAction(e -> {
            String selected = avatarCombo.getValue();
            String avatarId = selected.split(" ")[1].toLowerCase();
            currentPlayer.setAvatar(avatarId);
            AccountManager.saveAllAccounts();
        });
        
        avatarBox.getChildren().addAll(avatarLabel, avatarCombo);
        centerSection.getChildren().add(avatarBox);

        // Enhanced button styling for START button
        Button startButton = createStyledButton("START NEW GAME", Color.rgb(255, 140, 0), Color.rgb(255, 255, 255));
        startButton.setOnAction(e -> onStartNewGame.run());
        centerSection.getChildren().add(startButton);

        // Add both sections to main layout
        layout.getChildren().addAll(topSection, centerSection);

        // Set and show the scene
        Scene scene = new Scene(layout, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays a dialog with the most recent scores of the current player.
     *
     * @param account the player whose scores are to be shown
     */
    private void showRecentScores(Account account) {
        StringBuilder sb = new StringBuilder();
        List<Long> scores = account.getRecentScores();

        if (scores.isEmpty()) {
            sb.append("No games played yet.");
        } else {
            for (int i = scores.size() - 1; i >= 0; i--) {
                sb.append(scores.size() - i).append(". ").append(scores.get(i)).append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recent Scores");
        alert.setHeaderText("Scores for: " + account.getUserName());
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /**
     * Returns the currently selected player profile.
     *
     * @return the active Account object
     */
    public Account getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Returns the currently selected board size.
     *
     * @return the board size (4, 5, or 6)
     */
    public int getSelectedBoardSize() {
        return selectedBoardSize;
    }

    /**
     * Creates a styled button with consistent design.
     *
     * @param text the button text
     * @param backgroundColor the background color
     * @param textColor the text color
     * @return the styled button
     */
    private Button createStyledButton(String text, Color backgroundColor, Color textColor) {
        Button button = new Button(text);
        button.setPrefSize(250, 45);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(8), null)));
        button.setTextFill(textColor);
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                backgroundColor.deriveColor(0, 1, 1, 0.9), new CornerRadii(8), null)));
        });
        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(8), null)));
        });
        
        return button;
    }
    
    /**
     * Creates a window control button (minimize, maximize, close).
     *
     * @param text the button text (symbol)
     * @param backgroundColor the background color
     * @return the styled window control button
     */
    private Button createWindowControlButton(String text, Color backgroundColor) {
        Button button = new Button(text);
        button.setPrefSize(30, 30);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(15), null)));
        button.setTextFill(Color.WHITE);
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                backgroundColor.deriveColor(0, 1, 1, 0.8), new CornerRadii(15), null)));
        });
        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(15), null)));
        });
        
        return button;
    }
}
