package com.memorygame.example.zoe.tp1_memorygame;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Created by Zoe on 15-02-03.
 */
public class RobotPlayer extends Observable implements Player{
    private String name;
    private int score;

    public RobotPlayer() {
        this.name = "Robot";
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

    public List choosePiece(){

    }

}
