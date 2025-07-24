package com.example.demo;

import com.example.demo.data.Account;
import com.example.demo.data.AccountManager;
import com.example.demo.data.DataManager;
import com.example.demo.scene.GameScene;
import com.example.demo.scene.HomeScreen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 900;
    private Account currentPlayer;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        currentPlayer = DataManager.loadLastPlayer();
        showHomeScreen();
    }

    private void showHomeScreen() {
        final HomeScreen[] homeRef = new HomeScreen[1];
        if (currentPlayer == null) {
            currentPlayer = AccountManager.findOrCreateAccount("Guest");
        }
        homeRef[0] = new HomeScreen(primaryStage, currentPlayer, () -> startGame(homeRef[0].getCurrentPlayer()));
        homeRef[0].show();
        currentPlayer = homeRef[0].getCurrentPlayer();
        DataManager.saveLastPlayer(currentPlayer);
    }

    private void startGame(Account player) {
        Group gameRoot = new Group();
        Group endgameRoot = new Group();

        Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT, Color.rgb(189, 177, 92));
        Scene endGameScene = new Scene(endgameRoot, WIDTH, HEIGHT, Color.rgb(250, 20, 100, 0.2));

        Rectangle backgroundOfMenu = new Rectangle(240, 120, Color.rgb(120, 120, 120, 0.2));
        backgroundOfMenu.setX(WIDTH / 2 - 120);
        backgroundOfMenu.setY(180);
        gameRoot.getChildren().add(backgroundOfMenu);

        BackgroundFill background_fill = new BackgroundFill(Color.rgb(120, 100, 100), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);

        GameScene game = new GameScene();
        game.game(
                gameScene,
                gameRoot,
                primaryStage,
                endGameScene,
                endgameRoot,
                currentPlayer,
                () -> startGame(currentPlayer),
                this::showHomeScreen
        );

        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
