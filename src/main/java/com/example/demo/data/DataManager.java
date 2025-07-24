package com.example.demo.data;

import java.io.*;

/**
 * Handles persistent storage and retrieval of the last logged-in player.
 * Uses a simple text file to save and load the username of the last active account.
 */
public class DataManager {

    /** Path to the local file where the last username is stored */
    private static final String FILE_PATH = "last_player.txt";

    /**
     * Saves the username of the last active player to a file.
     *
     * @param account the {@link Account} to persist
     */
    public static void saveLastPlayer(Account account) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(account.getUserName());
        } catch (IOException e) {
            System.err.println("Failed to save last player.");
        }
    }

    /**
     * Loads the last active player from the file.
     * If no valid data is found, returns {@code null}.
     *
     * @return the last used {@link Account} or {@code null} if not found
     */
    public static Account loadLastPlayer() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String name = reader.readLine();
            if (name != null) {
                return AccountManager.findOrCreateAccount(name.trim());
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
