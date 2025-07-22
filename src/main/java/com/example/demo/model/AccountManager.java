package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all player accounts.
 */
public class AccountManager {
    private static final List<Account> accounts = new ArrayList<>();

    /**
     * Finds an account by username.
     * @param userName the username
     * @return the account if exists, otherwise null
     */
    public static Account findAccount(String userName) {
        for (Account account : accounts) {
            if (account.getUserName().equals(userName)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Creates and stores a new account.
     * @param userName the username
     * @return the newly created account
     */
    public static Account createAccount(String userName) {
        Account newAccount = new Account(userName);
        accounts.add(newAccount);
        return newAccount;
    }

    public static List<Account> getAllAccounts() {
        return new ArrayList<>(accounts); // Return copy for safety
    }
}
