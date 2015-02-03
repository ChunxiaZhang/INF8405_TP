package com.mymemorygame.example.zoe.mymemorygame;

import java.util.Observable;

/**
 * Created by Zoe on 15-02-03.
 */
public class HumanPlayer extends Observable implements Player{
    private String name;
    private int score;

    public HumanPlayer(String name) {
        this.name = name;
        this.score = 0;
    }

    public HumanPlayer() {
        this.name = "You";
        this.score = 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public void increaseScore() {
        this.score++;
    }

    public void choosePiece() {}

}
