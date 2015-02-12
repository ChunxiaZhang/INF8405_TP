package com.memorygame.example.zoe.tp1_memorygame;

import java.util.List;

/**
 * Created by Zoe on 15-01-29.
 */
public class Player {
    protected String name;
    protected int score;
    public Player(){}
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
    public List<Piece> choosePiece(List<Piece> piecesLeft,List<Piece> piecesTurned){return null;}
}
