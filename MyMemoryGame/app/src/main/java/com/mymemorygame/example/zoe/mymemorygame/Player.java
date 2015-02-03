package com.mymemorygame.example.zoe.mymemorygame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoe on 15-01-29.
 */
public interface Player {
    void setScore(int score);
    String getName();
    int getScore();
    void increaseScore();
    void choosePiece();

}
