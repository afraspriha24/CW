package com.example.demo.scene;

import com.example.demo.data.Account;
import com.example.demo.data.AccountManager;
import com.example.demo.data.DataManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    }

    /**
     * Displays the home screen with title, buttons, and user interaction controls.
     */
    public void show() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, null)));

        Text title = new Text("2048 GAME");
        title.setFont(Font.font(50));
        layout.getChildren().add(title);

        Text usernameDisplay = new Text("Player: " + currentPlayer.getUserName());
        usernameDisplay.setFont(Font.font(24));
        layout.getChildren().add(usernameDisplay);

        // Button: Start New Game
        Button startButton = new Button("Start New Game");
        startButton.setPrefSize(200, 40);
        startButton.setOnAction(e -> onStartNewGame.run());

        // Button: Create New Profile
        Button createButton = new Button("Create New Profile");
        createButton.setPrefSize(200, 40);
        createButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create New Profile");
            dialog.setHeaderText("Enter new username:");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    currentPlayer = AccountManager.findOrCreateAccount(name.trim());
                    DataManager.saveLastPlayer(currentPlayer);
                    usernameDisplay.setText("Player: " + currentPlayer.getUserName());
                }
            });
        });

        // Button: Edit Profile (choose existing profile)
        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.setPrefSize(200, 40);
        editProfileButton.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(currentPlayer.getUserName(),
                    AccountManager.getAllAccounts().stream().map(Account::getUserName).toList());
            dialog.setTitle("Select Profile");
            dialog.setHeaderText("Choose an existing profile:");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(name -> {
                currentPlayer = AccountManager.findOrCreateAccount(name.trim());
                DataManager.saveLastPlayer(currentPlayer);
                usernameDisplay.setText("Player: " + currentPlayer.getUserName());
            });
        });

        // Button: Show Recent Scores
        Button highScoresButton = new Button("High Scores");
        highScoresButton.setPrefSize(200, 40);
        highScoresButton.setOnAction(e -> showRecentScores(currentPlayer));

        // Button: Game Rules
        Button rulesButton = new Button("Rules");
        rulesButton.setPrefSize(200, 40);
        rulesButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Rules");
            alert.setHeaderText("How to Play");
            alert.setContentText("Use arrow keys to slide tiles.\n"
                    + "When two tiles with the same number touch, they merge!\n"
                    + "Reach 2048 to win!");
            alert.showAndWait();
        });

        // Button: Exit Game
        Button exitButton = new Button("Exit");
        exitButton.setPrefSize(200, 40);
        exitButton.setOnAction(e -> stage.close());

        // Add all buttons to the layout
        layout.getChildren().addAll(
                startButton, createButton, editProfileButton,
                highScoresButton, rulesButton, exitButton
        );

        // Set and show the scene
        Scene scene = new Scene(layout, 900, 900);
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
}
