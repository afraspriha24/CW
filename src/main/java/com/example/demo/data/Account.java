package com.example.demo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents player account in the 2048 game,.
 * Each account hold a username, total score, and recent game score(s).
 */
public class Account implements Comparable<Account> {
    private long score;
    private final String userName;
    private final List<Long> recentScores = new ArrayList<>();

    /**
     * Constructs a new Account with given username.
     *
     * @param userName the name of the player
     */
    public Account(String userName) {
        this.userName = userName;
        this.score = 0;
    }

    /**
     * Adds to the total score and updates the recent scores list.
     *
     * @param score the score to add to this player's total
     */
    public void addToScore(long score) {
        this.score += score;
        addRecentScore(score);
    }

    /**
     * Returns the total accumulated score of this account.
     *
     * @return the current score
     */
    public long getScore() {
        return score;
    }

    /**
     * Returns the username of this account.
     *
     * @return the player's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Adds a new score to the list of recent scores.
     * Keeps only the most recent 10 scores.
     *
     * @param score the score to record
     */
    public void addRecentScore(long score) {
        if (score > 0) {
            recentScores.add(score);
            if (recentScores.size() > 10) {
                recentScores.remove(0); // Keep only recent 10 scores
            }
        }
    }

    /**
     * Returns a copy of the list of recent game scores.
     *
     * @return a list of recent scores (max size: 10)
     */
    public List<Long> getRecentScores() {
        return new ArrayList<>(recentScores);
    }

    /**
     * Compares this account with another based on score.
     *
     * @param other the other Account to compare to
     * @return negative if this score is less, positive if greater, 0 if equal
     */
    @Override
    public int compareTo(Account other) {
        return Long.compare(other.getScore(), this.score); // Descending
    }

    /**
     * Returns a string representation of this account.
     *
     * @return a string in the format "username - score"
     */
    @Override
    public String toString() {
        return userName + " - " + score;
    }
}
