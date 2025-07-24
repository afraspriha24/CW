package com.example.demo.scene;

import com.example.demo.data.Account;
import com.example.demo.data.AccountManager;
import com.example.demo.data.DataManager;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

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

        // Game Over Title
        Text title = new Text("GAME OVER");
        title.setFont(Font.font(80));
        title.setFill(Color.DARKRED);
        title.relocate(250, 200);
        root.getChildren().add(title);

        // Final Score Display
        Text scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font(40));
        scoreText.setFill(Color.BLACK);
        scoreText.relocate(250, 300);
        root.getChildren().add(scoreText);

        // Save score to player's profile
        if (currentPlayer != null) {
            currentPlayer.addToScore(score);
            DataManager.saveLastPlayer(currentPlayer);
        }

        // Restart Button
        Button restartButton = new Button("RESTART");
        restartButton.setPrefSize(150, 40);
        restartButton.setTextFill(Color.BLUE);
        restartButton.relocate(250, 400);
        restartButton.setOnAction(e -> onRestart.run());
        root.getChildren().add(restartButton);

        // Back to Home Button
        Button backButton = new Button("BACK");
        backButton.setPrefSize(150, 40);
        backButton.setTextFill(Color.DARKGREEN);
        backButton.relocate(250, 460);
        backButton.setOnAction(e -> goHome.run());
        root.getChildren().add(backButton);

        // High Score List Display
        List<Account> topAccounts = AccountManager.getTopAccounts();
        int y = 530;

        Text highScoreTitle = new Text("Top Scores:");
        highScoreTitle.setFont(Font.font(30));
        highScoreTitle.setFill(Color.BLACK);
        highScoreTitle.relocate(250, y);
        root.getChildren().add(highScoreTitle);

        y += 40;
        for (int i = 0; i < Math.min(3, topAccounts.size()); i++) {
            Account acc = topAccounts.get(i);
            Text t = new Text((i + 1) + ". " + acc.getUserName() + " - " + acc.getScore());
            t.setFont(Font.font(24));
            t.setFill(Color.DARKSLATEGRAY);
            t.relocate(250, y);
            y += 30;
            root.getChildren().add(t);
        }

        // Set the scene
        primaryStage.setScene(endGameScene);
    }
}
