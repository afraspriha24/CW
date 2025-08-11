package com.example.demo.scene;

import com.example.demo.data.Account;
import com.example.demo.data.AccountManager;
import com.example.demo.data.DataManager;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;
import javafx.geometry.Pos;

/**
 * Singleton class that displays the end game screen with score summary,
 * high scores, and navigation options.
 * <p>
 * It includes UI elements like "Restart", "Back", and shows the top 3 scores.
 */
public class EndGame {
    private static EndGame singleInstance = null;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private EndGame() {}

    /**
     * Returns the singleton instance of {@code EndGame}.
     *
     * @return the shared {@code EndGame} instance
     */
    public static EndGame getInstance() {
        if (singleInstance == null)
            singleInstance = new EndGame();
        return singleInstance;
    }

    /**
     * Displays the end game screen with the final score, restart and back buttons,
     * and a list of top scores.
     *
     * @param endGameScene the scene to render the end game UI
     * @param root the group root node for UI elements
     * @param primaryStage the application stage to set the scene
     * @param score the final score achieved by the player
     * @param onRestart a {@code Runnable} to restart the game when the Restart button is clicked
     * @param goHome a {@code Runnable} to go back to the home screen
     * @param currentPlayer the current player whose score will be saved
     */
    public void endGameShow(Scene endGameScene, Group root, Stage primaryStage, long score,
                            Runnable onRestart, Runnable goHome, Account currentPlayer) {
        root.getChildren().clear();

        // Add window control buttons at the top-right
        HBox windowControls = new HBox(10);
        windowControls.setAlignment(Pos.TOP_RIGHT);
        windowControls.setLayoutX(1100);
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
        
        Button closeBtn = createWindowControlButton("×", Color.rgb(220, 53, 69));
        closeBtn.setOnAction(e -> primaryStage.close());
        
        windowControls.getChildren().addAll(minimizeBtn, maximizeBtn, closeBtn);
        root.getChildren().add(windowControls);

        // Check if this is a new high score
        boolean isNewHighScore = false;
        long previousHighScore = currentPlayer.getScore() - score;
        if (score > previousHighScore) {
            isNewHighScore = true;
        }

        // Save score to player's profile
        if (currentPlayer != null) {
            AccountManager.addScoreAndSave(currentPlayer, score);
            DataManager.saveLastPlayer(currentPlayer);
        }

        // Main container
        VBox mainContainer = new VBox(30);
        mainContainer.setLayoutX(200);
        mainContainer.setLayoutY(100);
        mainContainer.setAlignment(Pos.CENTER);

        // Game Over Title with enhanced styling
        VBox titleContainer = new VBox(10);
        titleContainer.setAlignment(Pos.CENTER);
        
        Text gameOverTitle = new Text("GAME OVER");
        gameOverTitle.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        gameOverTitle.setFill(Color.rgb(119, 110, 101));
        
        if (isNewHighScore) {
            Text newHighScoreText = new Text("🎉 NEW HIGH SCORE! 🎉");
            newHighScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            newHighScoreText.setFill(Color.rgb(237, 194, 46));
            titleContainer.getChildren().addAll(gameOverTitle, newHighScoreText);
        } else {
            titleContainer.getChildren().add(gameOverTitle);
        }
        
        mainContainer.getChildren().add(titleContainer);

        // Score Display with enhanced styling
        VBox scoreContainer = new VBox(10);
        scoreContainer.setAlignment(Pos.CENTER);
        
        Text scoreLabel = new Text("Final Score");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreLabel.setFill(Color.rgb(119, 110, 101));
        
        Text scoreText = new Text(String.valueOf(score));
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        scoreText.setFill(Color.rgb(237, 194, 46));
        
        scoreContainer.getChildren().addAll(scoreLabel, scoreText);
        mainContainer.getChildren().add(scoreContainer);

        // Player Information
        VBox playerContainer = new VBox(5);
        playerContainer.setAlignment(Pos.CENTER);
        
        Text playerLabel = new Text("Player");
        playerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        playerLabel.setFill(Color.rgb(119, 110, 101));
        
        Text playerName = new Text(currentPlayer.getUserName());
        playerName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        playerName.setFill(Color.rgb(119, 110, 101));
        
        Text totalScoreLabel = new Text("Total Score: " + currentPlayer.getScore());
        totalScoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        totalScoreLabel.setFill(Color.rgb(119, 110, 101));
        
        playerContainer.getChildren().addAll(playerLabel, playerName, totalScoreLabel);
        mainContainer.getChildren().add(playerContainer);

        // Action Buttons with enhanced styling
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button restartButton = createEndGameButton("RESTART", Color.rgb(242, 177, 121));
        restartButton.setOnAction(e -> onRestart.run());
        
        Button backButton = createEndGameButton("BACK TO MENU", Color.rgb(245, 149, 99));
        backButton.setOnAction(e -> goHome.run());
        
        buttonContainer.getChildren().addAll(restartButton, backButton);
        mainContainer.getChildren().add(buttonContainer);

        // High Score List Display with enhanced styling
        VBox highScoreContainer = new VBox(15);
        highScoreContainer.setAlignment(Pos.CENTER);
        
        Text highScoreTitle = new Text("🏆 TOP SCORES");
        highScoreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        highScoreTitle.setFill(Color.rgb(119, 110, 101));
        highScoreContainer.getChildren().add(highScoreTitle);

        List<Account> topAccounts = AccountManager.getTopAccounts();
        for (int i = 0; i < Math.min(5, topAccounts.size()); i++) {
            Account acc = topAccounts.get(i);
            HBox scoreRow = new HBox(10);
            scoreRow.setAlignment(Pos.CENTER);
            
            Text rank = new Text((i + 1) + ".");
            rank.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            rank.setFill(Color.rgb(119, 110, 101));
            
            Text name = new Text(acc.getUserName());
            name.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            name.setFill(Color.rgb(119, 110, 101));
            
            Text scoreValue = new Text(String.valueOf(acc.getScore()));
            scoreValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            scoreValue.setFill(Color.rgb(237, 194, 46));
            
            scoreRow.getChildren().addAll(rank, name, scoreValue);
            highScoreContainer.getChildren().add(scoreRow);
        }
        
        mainContainer.getChildren().add(highScoreContainer);

        // Add main container to root
        root.getChildren().add(mainContainer);

        // Set the scene
        primaryStage.setScene(endGameScene);
    }

    /**
     * Creates a styled end game button with consistent design.
     *
     * @param text the button text
     * @param backgroundColor the background color
     * @return the styled button
     */
    private Button createEndGameButton(String text, Color backgroundColor) {
        Button button = new Button(text);
        button.setPrefSize(160, 45);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(8), null)));
        button.setTextFill(Color.rgb(119, 110, 101));
        
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
     * Creates a styled window control button with consistent design.
     *
     * @param text the button text
     * @param backgroundColor the background color
     * @return the styled button
     */
    private Button createWindowControlButton(String text, Color backgroundColor) {
        Button button = new Button(text);
        button.setPrefSize(40, 40);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(8), null)));
        button.setTextFill(Color.rgb(119, 110, 101));
        
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
}
