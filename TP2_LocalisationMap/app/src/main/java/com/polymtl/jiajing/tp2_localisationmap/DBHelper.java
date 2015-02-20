package com.polymtl.jiajing.tp2_localisationmap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jiajing on 2015/2/20.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "Itinerary.db";
    private final static int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
