package com.example.demo.data;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static final List<Account> accounts = new ArrayList<>();

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

    public static List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public static List<Account> getTopAccounts() {
        List<Account> sorted = new ArrayList<>(accounts);
        sorted.sort(null);
        return sorted;
    }
}
