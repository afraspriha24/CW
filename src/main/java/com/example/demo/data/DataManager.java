package com.example.demo.data;

import java.io.*;

public class DataManager {
    private static final String FILE_PATH = "last_player.txt";

    public static void saveLastPlayer(Account account) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(account.getUserName());
        } catch (IOException e) {
            System.err.println("Failed to save last player.");
        }
    }

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
