package com.polymtl.jiajing.tp2_localisationmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.Marker;

import java.util.ArrayList;
import java.util.List;

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

    // We can use two tables, put Itinerary ID in the TABLE_MARKER as an identification of associated itineray
    private static final String TABLE_ITINERARY = "itineraries";

    //?????????????
    //private static final String TABLE_MARKER = "markers";

    /* ????????????
   // TABLE_MARKER's column names
    private static final String KEY_ID_ITINERARY = "_id_itinerary";
    //Coord includes latitude, longitude, altitude
    private static final String  KEY_LATITUDE = "latitude"; // latitude
    private static final String  KEY_LONGITUDE = "longitude"; // longitude
    private static final String  KEY_ALTITUDE = "altitude"; // altitude

    private static final String  KEY_IM = "im"; // the UTC time of this fix, in milliseconds since January 1, 1970.
    private static final String KEY_MOD_LOC = "mod_loc"; // le mode de localisation: gps or network
    private static final String KEY_NIV_BATT = "niv_batt"; // power level
    private static final String KEY_INFO = "info"; //information of connection
    ////*/

    /*?????????????
    private static final String CREATE_TABLE_MARKER = "CREATE TABLE " + TABLE_MARKER + " (" + KEY_ID +
            " INTEGER PRIMARY KEY," + KEY_ID_ITINERARY + " INTEGER PRIMARY KEY," + KEY_LATITUDE + " DOUBLE," +
            KEY_LONGITUDE + " DOUBLE," + KEY_ALTITUDE + " DOUBLE," + KEY_IM + " LONG," + KEY_MOD_LOC +
            " STRING," + KEY_NIV_BATT + " FLOAT," + KEY_INFO + " STRING," + KEY_CREATED_AT + " DATETIME" + ")";*/

    //Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_CREATED_AT = "created_at";

    //ITINERARY table column names
    private static final String KEY_DT = "dt"; //la distance total
    private static final String  KEY_TIME = "time"; // zoom level
    private static final String  KEY_NBR_SB = "nbr_sb"; //count of base stations
    private static final String  KEY_POWER_CONSUMPTION = "powerConsumption"; // power consumption of itinerary

    //Table create statements
    private static final String CREATE_TABLE_ITINERARY = "CREATE TABLE " + TABLE_ITINERARY +
            " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DT + " FLOAT," + KEY_TIME + " LONG," +
            KEY_NBR_SB + " INTEGER, " + KEY_POWER_CONSUMPTION + " FLOAT," + KEY_CREATED_AT + " DATETIME" + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables
        db.execSQL(CREATE_TABLE_ITINERARY);

        //?????If it is necessary to have markers stored in data base
        //db.execSQL(CREATE_TABLE_MARKER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY);

        //??????
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);

        //create new tables
        onCreate(db);
    }


    // Create a itinerary
    public long createItinerary(Itinerary itinerary) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DT, itinerary.getDt());
        values.put(KEY_TIME, itinerary.getStartTime());
        values.put(KEY_NBR_SB, itinerary.getNbr_sb());
        values.put(KEY_POWER_CONSUMPTION, itinerary.getAllPowerConsumption());

        //insert row
        long itinerary_id = db.insert(TABLE_ITINERARY, null, values);

        db.close();
        return itinerary_id;
    }

    //Get all 3 itineraries of one itinerary
    public List<Itinerary> getAllItineraries() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITINERARY + " ORDER BY time DESC LIMIT 3", null);
        List<Itinerary> result = new ArrayList<>();
        while ((cursor.moveToNext())) {

            Itinerary itinerary = new Itinerary();
            itinerary.setDt(cursor.getLong(cursor.getColumnIndex(KEY_DT)));
            itinerary.setAllPowerConsumption(cursor.getFloat(cursor.getColumnIndex(KEY_POWER_CONSUMPTION)));
            itinerary.setNbr_sb(cursor.getInt(cursor.getColumnIndex(KEY_NBR_SB)));
            itinerary.setStartTime(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));

            result.add(itinerary);
        }

        return result;
    }

    /* ??????????????????????
    //Create a Marker
    public long createMarker(Marker marker, long itinerary_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_ITINERARY, itinerary_id);
        values.put(KEY_LATITUDE, marker.getLatitude());
        values.put(KEY_LONGITUDE, marker.getLongitude());
        values.put(KEY_ALTITUDE, marker.getAltitude());
        values.put(KEY_IM, marker.getIm());
        values.put(KEY_MOD_LOC, marker.getMod_loc());
        values.put(KEY_NIV_BATT, marker.getNiv_batt());
        values.put(KEY_INFO, marker.getInfo());

        //insert row
        long marker_id = db.insert(TABLE_MARKER, null, values);

        db.close();
        return marker_id;

    }*/

/*
    //get a marker
    public Marker getMarker(long marker_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MARKER + " WHERE " + KEY_ID + " = " + marker_id;

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null) {
            cursor.moveToFirst();  //??????
        }


        Marker marker = new Marker();
        marker.setAltitude(cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)));
        marker.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
        marker.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
        marker.setIm(cursor.getLong(cursor.getColumnIndex(KEY_IM)));
        marker.setMod_loc(cursor.getString(cursor.getColumnIndex(KEY_MOD_LOC)));
        marker.setNiv_batt(cursor.getFloat(cursor.getColumnIndex(KEY_NIV_BATT)));
        marker.setInfo(cursor.getString(cursor.getColumnIndex(KEY_INFO)));

        return marker;
    }
*/


}
