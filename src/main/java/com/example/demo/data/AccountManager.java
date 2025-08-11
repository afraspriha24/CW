package com.example.demo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all player accounts used in the 2048 game.
 * Provides functionality to create, retrieve, and rank player accounts.
 * Stores accounts in memory using a static list and persists them to disk.
 */
public class AccountManager {

    /** In-memory list of all created accounts */
    private static final List<Account> accounts = new ArrayList<>();

    /**
     * Finds an existing account by username or creates a new one if not found.
     * Username matching is case-insensitive.
     *
     * @param userName the username to search or create
     * @return the found or newly created {@link Account}
     */
    public static Account findOrCreateAccount(String userName) {
        for (Account account : accounts) {
            if (account.getUserName().equalsIgnoreCase(userName)) {
                return account;
            }
        }
        Account newAccount = new Account(userName);
        accounts.add(newAccount);
        return newAccount;
    }

    /**
     * Returns a list of all existing accounts.
     *
     * @return a new list containing all {@link Account} objects
     */
    public static List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    /**
     * Returns a list of accounts sorted by score in descending order.
     *
     * @return a list of top accounts sorted by score
     */
    public static List<Account> getTopAccounts() {
        List<Account> sorted = new ArrayList<>(accounts);
        sorted.sort(null);
        return sorted;
    }
    
    /**
     * Saves all accounts to persistent storage.
     * Should be called when the application is shutting down or when accounts are modified.
     */
    public static void saveAllAccounts() {
        DataManager.saveAllAccounts(accounts);
    }
    
    /**
     * Loads all accounts from persistent storage.
     * Should be called when the application starts up.
     */
    public static void loadAllAccounts() {
        DataManager.loadAllAccounts();
    }
    
    /**
     * Adds a score to an account and saves all accounts to persistent storage.
     * This ensures that score changes are immediately persisted.
     *
     * @param account the account to update
     * @param score the score to add
     */
    public static void addScoreAndSave(Account account, long score) {
        account.addToScore(score);
        saveAllAccounts();
    }
}
