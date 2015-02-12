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
    private int imgClass;
    private Drawable image;
    private Button button;


    public Piece(int numRow, int numCol, int imgClass, Drawable imageFront, Button button) {
        this.numRow = numRow;
        this.numCol = numCol;
        this.imgClass = imgClass;
        this.image = imageFront;
        this.button = button;
    }

    public void setDrawable(Drawable image) {
        this.image = image;
    }
    public void makeButtonVisible(){
        this.button.setVisibility(View.VISIBLE);
    }
    public void enableButton(boolean enable){this.button.setEnabled(enable);}
    public void showBackImage(Drawable backImage){this.button.setBackgroundDrawable(backImage);}
    public void showFrontImage(){this.button.setBackgroundDrawable(this.image);}

    public int getImgClass(){return this.imgClass;}
    public int getNumRow(){return this.numRow;}
    public int getNumCol(){return this.numCol;}
    public Button getButton(){return this.button;}
}
