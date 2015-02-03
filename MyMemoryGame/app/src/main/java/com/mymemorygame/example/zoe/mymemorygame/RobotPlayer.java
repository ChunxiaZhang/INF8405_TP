package com.mymemorygame.example.zoe.mymemorygame;

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
    private int selectPieceIndex;
    private List<Integer> listIndex;

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

    public void setListIndex(ArrayList<Integer> listIndex) {
        this.listIndex = listIndex;
    }

    public int getSelectPieceIndex() {
        return this.selectPieceIndex;
    }

    public void choosePiece() {
        Random rand = new Random();
        int index;
        do {
            index = rand.nextInt(MainActivity.ROW_COUNT*MainActivity.COL_COUNT);
        }while (this.listIndex.contains(index));
        this.selectPieceIndex = index;
        setChanged();
        notifyObservers();
    }

}
