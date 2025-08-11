package com.example.demo.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Handles persistent storage and retrieval of player data.
 * Uses system-independent file paths and provides robust error handling.
 * Saves both the last logged-in player and all account data.
 * Implements both object serialization and text-based persistence for robustness.
 */
public class DataManager {
    private static final Logger LOGGER = Logger.getLogger(DataManager.class.getName());
    
    /** Directory for storing game data in user's home directory */
    private static final String GAME_DIR = "2048game";
    
    /** File names for different data types */
    private static final String LAST_PLAYER_FILE = "last_player.txt";
    private static final String ACCOUNTS_FILE = "accounts.dat";
    private static final String ACCOUNTS_SERIALIZED_FILE = "accounts.ser";
    
    /** Path to the game data directory */
    private static Path gameDataDir;
    
    static {
        // Initialize the game data directory
        String userHome = System.getProperty("user.home");
        gameDataDir = Paths.get(userHome, GAME_DIR);
        
        // Create the directory if it doesn't exist
        try {
            if (!Files.exists(gameDataDir)) {
                Files.createDirectories(gameDataDir);
                LOGGER.info("Created game data directory: " + gameDataDir);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create game data directory", e);
        }
    }

    /**
     * Saves the username of the last active player to a file.
     *
     * @param account the {@link Account} to persist
     */
    public static void saveLastPlayer(Account account) {
        Path filePath = gameDataDir.resolve(LAST_PLAYER_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(account.getUserName());
            LOGGER.info("Saved last player: " + account.getUserName());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save last player: " + account.getUserName(), e);
        }
    }

    /**
     * Loads the last active player from the file.
     * If no valid data is found, returns {@code null}.
     *
     * @return the last used {@link Account} or {@code null} if not found
     */
    public static Account loadLastPlayer() {
        Path filePath = gameDataDir.resolve(LAST_PLAYER_FILE);
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String name = reader.readLine();
            if (name != null && !name.trim().isEmpty()) {
                LOGGER.info("Loaded last player: " + name.trim());
                return AccountManager.findOrCreateAccount(name.trim());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load last player (file may not exist)", e);
        }
        return null;
    }
    
    /**
     * Saves all accounts using object serialization (preferred method).
     * Falls back to text-based format if serialization fails.
     *
     * @param accounts the list of accounts to save
     */
    public static void saveAllAccounts(List<Account> accounts) {
        // Try object serialization first
        if (saveAccountsSerialized(accounts)) {
            LOGGER.info("Saved " + accounts.size() + " accounts using serialization");
            return;
        }
        
        // Fall back to text-based format
        saveAccountsText(accounts);
    }
    
    /**
     * Saves accounts using object serialization.
     *
     * @param accounts the list of accounts to save
     * @return true if successful, false otherwise
     */
    private static boolean saveAccountsSerialized(List<Account> accounts) {
        Path filePath = gameDataDir.resolve(ACCOUNTS_SERIALIZED_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(accounts);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Serialization failed, falling back to text format", e);
            return false;
        }
    }
    
    /**
     * Saves accounts using text format (fallback method).
     *
     * @param accounts the list of accounts to save
     */
    private static void saveAccountsText(List<Account> accounts) {
        Path filePath = gameDataDir.resolve(ACCOUNTS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Account account : accounts) {
                // Format: username,score,avatar,score1,score2,score3...
                StringBuilder line = new StringBuilder();
                line.append(account.getUserName()).append(",").append(account.getScore())
                    .append(",").append(account.getAvatar());
                
                for (Long recentScore : account.getRecentScores()) {
                    line.append(",").append(recentScore);
                }
                
                writer.write(line.toString());
                writer.newLine();
            }
            LOGGER.info("Saved " + accounts.size() + " accounts using text format");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save accounts", e);
        }
    }
    
    /**
     * Loads all accounts from persistent storage.
     * Tries object serialization first, then falls back to text format.
     * Creates Account objects and adds them to AccountManager.
     */
    public static void loadAllAccounts() {
        // Try loading from serialized file first
        if (loadAccountsSerialized()) {
            LOGGER.info("Loaded accounts from serialized file");
            return;
        }
        
        // Fall back to text format
        loadAccountsText();
    }
    
    /**
     * Loads accounts from serialized file.
     *
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("unchecked")
    private static boolean loadAccountsSerialized() {
        Path filePath = gameDataDir.resolve(ACCOUNTS_SERIALIZED_FILE);
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            List<Account> accounts = (List<Account>) ois.readObject();
            
            // Clear existing accounts and add loaded ones
            AccountManager.getAllAccounts().clear();
            for (Account account : accounts) {
                AccountManager.findOrCreateAccount(account.getUserName());
            }
            
            return true;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Failed to load serialized accounts, trying text format", e);
            return false;
        }
    }
    
    /**
     * Loads accounts from text file (fallback method).
     */
    private static void loadAccountsText() {
        Path filePath = gameDataDir.resolve(ACCOUNTS_FILE);
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String username = parts[0];
                        long totalScore = Long.parseLong(parts[1]);
                        String avatar = parts[2];
                        
                        Account account = AccountManager.findOrCreateAccount(username);
                        // Set the total score and avatar
                        account.setTotalScore(totalScore);
                        account.setAvatar(avatar);
                        
                        // Load recent scores (if any)
                        for (int i = 3; i < parts.length; i++) {
                            try {
                                long recentScore = Long.parseLong(parts[i]);
                                account.addRecentScore(recentScore);
                            } catch (NumberFormatException e) {
                                LOGGER.log(Level.WARNING, "Invalid score format in line: " + line, e);
                            }
                        }
                    }
                }
            }
            LOGGER.info("Loaded accounts from text file");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load accounts (file may not exist)", e);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Corrupted accounts file", e);
        }
    }
    
    /**
     * Gets the path to the game data directory.
     *
     * @return the Path to the game data directory
     */
    public static Path getGameDataDir() {
        return gameDataDir;
    }
}
