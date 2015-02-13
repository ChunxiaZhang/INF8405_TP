package com.memorygame.example.zoe.tp1_memorygame;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

/**
 * Created by Zoe on 15-01-29.
 */
public class Piece {
    private int numRow;
    private int numCol;
    private int idx;
    private int imgClass;
    private Drawable imageFront;
    private Drawable imageBack;
    private Button button;


    public Piece(int numRow, int numCol, int idx, int imgClass, Drawable imageFront, Drawable imageBack, Button button) {
        this.numRow = numRow;
        this.numCol = numCol;
        this.idx = idx;
        this.imgClass = imgClass;
        this.imageFront = imageFront;
        this.imageBack = imageBack;
        this.button = button;
    }

    public void setDrawable(Drawable image) {
        this.imageFront = image;
    }
    public void makeButtonVisible(){
        this.button.setVisibility(View.VISIBLE);
    }
    public void enableButton(boolean enable){this.button.setEnabled(enable);}
    public void showBackImage(){this.button.setBackgroundDrawable(this.imageBack);}
    public void showFrontImage(){this.button.setBackgroundDrawable(this.imageFront);}

    public int getImgClass(){return this.imgClass;}
    public int getNumRow(){return this.numRow;}
    public int getNumCol(){return this.numCol;}
    public int getIdx(){return this.idx;}
    public Button getButton(){return this.button;}
}
