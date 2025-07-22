package com.example.demo;

/**
 * Represents a player account with username and score.
 */
public class Account implements Comparable<Account> {
    private long score;
    private final String userName;

    public Account(String userName) {
        this.userName = userName;
        this.score = 0;
    }

    /**
     * Adds points to the current score.
     *
     * @param score Amount to add
     */
    public void addToScore(long score) {
        this.score += score;
    }

    public long getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int compareTo(Account other) {
        return Long.compare(other.getScore(), this.score); // Descending order
    }

    @Override
    public String toString() {
        return "Account{" +
                "userName='" + userName + '\'' +
                ", score=" + score +
                '}';
    }
}
