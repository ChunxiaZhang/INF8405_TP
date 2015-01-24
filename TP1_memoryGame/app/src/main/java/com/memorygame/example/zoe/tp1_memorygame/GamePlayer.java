package com.memorygame.example.zoe.tp1_memorygame;

/**
 * Created by Zoe on 15-01-23.
 */
public class GamePlayer {
    private String playerName;
    private int gameScore;
    void setPlayerName(String name)
    {
        playerName = name;
    }
    void setGameScore(int score)
    {
        gameScore = score;
    }

    String getPlayerName()
    {
        return playerName;
    }

    int getGameScore()
    {
        return gameScore;
    }
}
