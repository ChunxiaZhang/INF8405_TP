package com.memorygame.example.zoe.tp1_memorygame;

import android.graphics.drawable.Drawable;
import android.widget.Button;

/**
 * Created by Zoe on 15-01-29.
 */
public class Piece {
    public int x;
    public int y;
    public int index;
    public Drawable image;
    public Button button;
    public boolean isMatched;


    public Piece(Button button, int x, int y) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.isMatched = false;
    }

    public void setDrawable(Drawable image) {
        this.image = image;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static int getLocation(int x, int y) {
        return x + y * MainActivity.COL_COUNT;
    }

}
