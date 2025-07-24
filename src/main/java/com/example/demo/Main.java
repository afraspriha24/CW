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

/**
 * The entry point of the 2048 game application.
 * <p>
 * This class manages the startup sequence, user session handling,
 * and transitions between the home screen and the game screen.
 */
public class Main extends Application {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 900;

    private Account currentPlayer;
    private Stage primaryStage;

    /**
     * JavaFX entry method.
     * Initializes the application window and loads the last active player profile.
     *
     * @param primaryStage the main window stage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        currentPlayer = DataManager.loadLastPlayer();
        showHomeScreen();
    }

    /**
     * Displays the home screen UI.
     * If no previously saved account is found, a default "Guest" account is used.
     * The `HomeScreen` component allows the user to manage profiles and start a game.
     */
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

    /**
     * Starts a new game session with the specified player account.
     * Initializes both the game scene and the endgame screen.
     *
     * @param player the player account to use for the session
     */
    private void startGame(Account player) {
        Group gameRoot = new Group();
        Group endgameRoot = new Group();

        Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT, Color.rgb(189, 177, 92));
        Scene endGameScene = new Scene(endgameRoot, WIDTH, HEIGHT, Color.rgb(250, 20, 100, 0.2));

        // Optional UI decoration (menu background)
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
                () -> startGame(currentPlayer), // Restart handler
                this::showHomeScreen            // Back to home handler
        );

        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
