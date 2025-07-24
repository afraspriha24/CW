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

public class EndGame {
    private static EndGame singleInstance = null;

    private EndGame() {}

    public static EndGame getInstance() {
        if (singleInstance == null)
            singleInstance = new EndGame();
        return singleInstance;
    }

    public void endGameShow(Scene endGameScene, Group root, Stage primaryStage, long score,
                            Runnable onRestart, Runnable goHome, Account currentPlayer) {
        root.getChildren().clear();

        Text title = new Text("GAME OVER");
        title.setFont(Font.font(80));
        title.setFill(Color.DARKRED);
        title.relocate(250, 200);
        root.getChildren().add(title);

        Text scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font(40));
        scoreText.setFill(Color.BLACK);
        scoreText.relocate(250, 300);
        root.getChildren().add(scoreText);

        // Save the score
        if (currentPlayer != null) {
            currentPlayer.addToScore(score); // Accumulate session
            DataManager.saveLastPlayer(currentPlayer);
        }

        // Restart Button
        Button restartButton = new Button("RESTART");
        restartButton.setPrefSize(150, 40);
        restartButton.setTextFill(Color.BLUE);
        restartButton.relocate(250, 400);
        restartButton.setOnAction(e -> onRestart.run());
        root.getChildren().add(restartButton);

        // Back Button
        Button backButton = new Button("BACK");
        backButton.setPrefSize(150, 40);
        backButton.setTextFill(Color.DARKGREEN);
        backButton.relocate(250, 460);
        backButton.setOnAction(e -> goHome.run());
        root.getChildren().add(backButton);

        // High Scores Display
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

        primaryStage.setScene(endGameScene);
    }
}
