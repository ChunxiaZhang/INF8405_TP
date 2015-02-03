package com.mymemorygame.example.zoe.mymemorygame;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Zoe on 15-02-02.
 */
public class GameService {
    public int[][] getPiecesIndex(int row, int col) {
        int[][] index = new int [row][col];

        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < row*col/2; i++) {
            list.add(new Integer(i));
        }
        list.addAll(list);

        Collections.shuffle(list);

        Iterator<Integer> iterator = list.iterator();
        for(int x = 0; x < row; x++){
            for(int y = 0; y < col; y++){
                index[x][y] = iterator.next();
                Log.i("index: " , " "+index[x][y]);
            }
            Log.i("index:", "the next");
        }
        return index;
    }

}
