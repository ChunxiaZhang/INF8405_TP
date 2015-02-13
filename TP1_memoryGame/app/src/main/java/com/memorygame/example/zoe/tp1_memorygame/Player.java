package com.memorygame.example.zoe.tp1_memorygame;

import java.util.List;
import android.os.Handler;

/**
 * Created by Zoe on 15-01-29.
 */
public class Player extends Thread{
    protected String playerName;
    protected int score;
    protected Piece firstPiece = null;
    protected Piece secondPiece = null;
    protected Handler playerHandler = null;
    protected GameActivity gameActivity;

    public Player(){}
    public void setScore(int score) {
        this.score = score;
    }
    public String getPlayerName() {
        return this.playerName;
    }
    public int getScore() {
        return this.score;
    }
    public void increaseScore() {
        this.score++;
    }
    public boolean checkPieces(){
        boolean isMatch;
        if(firstPiece.getImgClass() == secondPiece.getImgClass()){
            increaseScore();
            isMatch = true;
        }
        else {
            isMatch = false;
        }
        return isMatch;
    }
}
