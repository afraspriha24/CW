package com.example.demo.data;

import java.util.ArrayList;
import java.util.List;

public class Account implements Comparable<Account> {
    private long score;
    private final String userName;
    private final List<Long> recentScores = new ArrayList<>();

    public Account(String userName) {
        this.userName = userName;
        this.score = 0;
    }

    public void addToScore(long score) {
        this.score += score;
        addRecentScore(score);
    }

    public long getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }

    public void addRecentScore(long score) {
        if (score > 0) {
            recentScores.add(score);
            if (recentScores.size() > 10) {
                recentScores.remove(0); // Keep only recent 10 scores
            }
        }
    }

    public List<Long> getRecentScores() {
        return new ArrayList<>(recentScores);
    }

    @Override
    public int compareTo(Account other) {
        return Long.compare(other.getScore(), this.score); // Descending
    }

    @Override
    public String toString() {
        return userName + " - " + score;
    }
}
