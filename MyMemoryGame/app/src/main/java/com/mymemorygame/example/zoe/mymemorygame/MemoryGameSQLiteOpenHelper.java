package com.mymemorygame.example.zoe.mymemorygame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zoe on 15-01-29.
 */
public class MemoryGameSQLiteOpenHelper extends SQLiteOpenHelper {
    public final static int VERSION = 1;
    public final static String TABLE_NAME = "memoryGameData";
    public final static String DATABASE_NAME = "memoryGameData.db";

    public MemoryGameSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
