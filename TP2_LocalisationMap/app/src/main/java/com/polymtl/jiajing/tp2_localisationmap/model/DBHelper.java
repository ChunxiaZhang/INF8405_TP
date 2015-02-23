package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jiajing on 2015/2/20.
 */
public class DBHelper extends SQLiteOpenHelper {
    //Logcate
    private static final String LOG = "DBHelper";

    //Database name
    private final static String DATABASE_NAME = "itineraryManager.db";

    //Database version
    private final static int VERSION = 1;

    // Three table names
    private static final String TABLE_ITINERARY = "itineraries";
    private static final String TABLE_MARKER = "markers";
    private static final String TABLE_ITINERARY_MARKER = "initerary_markers";

    //Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_CREATED_AT = "created_at";

    //ITINERARY table column names


    //Define battery level table to save levels when track begins and finishes
    private static final String COLUMN_ID = "_id";
    private static final String TABLE_BATTERYLEVEL = "battery level";

    private static final String COLUMN_BATTERYLEVEL = "level";

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
