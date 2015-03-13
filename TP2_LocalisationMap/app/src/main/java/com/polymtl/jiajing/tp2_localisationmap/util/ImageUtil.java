package com.polymtl.jiajing.tp2_localisationmap.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;


/**
 * Created by Jiajing on 2015/3/12.
 */
public class ImageUtil {
    /**
     * rotate bipmap
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
        Matrix matrix = new Matrix();
        matrix.postRotate((float)rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }
}
