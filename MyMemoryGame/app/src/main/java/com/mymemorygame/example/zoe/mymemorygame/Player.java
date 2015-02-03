package com.mymemorygame.example.zoe.mymemorygame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoe on 15-01-29.
 */
public class Player {
    private String name;

    private int score;
    private List<Integer> steps;

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public Player() {
        this.name = "You";
        this.score = 0;
    }

    public void setName(String name) {
        this.name = name;
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
}
