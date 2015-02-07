package com.memorygame.example.zoe.tp1_memorygame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

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
        String sql="Create table "+TABLE_NAME+" ( player text, score integer );";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(String player, Integer score){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO "+TABLE_NAME+" (player,score) VALUES (?,?)", new Object[]{player,score});
        db.close();
    }

    public List<Pair<String,Integer>> findBestScore(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY score DESC LIMIT 5", null);
        List<Pair<String,Integer>> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            String player = cursor.getString(cursor.getColumnIndex("player"));
            Integer score = cursor.getInt(cursor.getColumnIndex("score"));
            result.add(new Pair(player,score));
        }
        return result;
    }
}
