package com.polymtl.jiajing.tp2_localisationmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;

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


    private static final String TABLE_MARKER = "markers";

    //Common column names
    private static final String KEY_ID = "_id";

    //TABLE_MARKER's column names
    private static final String KEY_ID_ITINERARY = "_id_itinerary";
    //Coord includes latitude, longitude, altitude
    private static final String  KEY_LATITUDE = "latitude"; // latitude
    private static final String  KEY_LONGITUDE = "longitude"; // longitude
    private static final String  KEY_ALTITUDE = "altitude"; // altitude

    private static final String  KEY_IM = "im"; // the UTC time of this fix, in milliseconds since January 1, 1970.
    private static final String KEY_MOD_LOC = "mod_loc"; // le mode de localisation: gps or network
    private static final String KEY_NIV_BATT = "niv_batt"; // power level
    private static final String KEY_INFO = "info"; //information of connection


    private static final String CREATE_TABLE_MARKER = "CREATE TABLE " + TABLE_MARKER + " (" + KEY_ID +
            " INTEGER PRIMARY KEY," + KEY_ID_ITINERARY + " INTEGER KEY," + KEY_LATITUDE + " DOUBLE," +
            KEY_LONGITUDE + " DOUBLE," + KEY_ALTITUDE + " DOUBLE," + KEY_IM + " LONG," + KEY_MOD_LOC +
            " STRING," + KEY_NIV_BATT + " FLOAT," + KEY_INFO + " STRING" + ")";



    //ITINERARY table column names
    private static final String KEY_DT = "dt"; //la distance total
    private static final String KEY_START_TIME = "start_time"; // start time
    private static final String KEY_STOP_TIME = "stop_time";
    private static final String KEY_NBR_SB = "nbr_sb"; //count of base stations
    private static final String KEY_POWER_CONSUMPTION = "powerConsumption"; // power consumption of itinerary

    //Table create statements
    private static final String CREATE_TABLE_ITINERARY = "CREATE TABLE " + TABLE_ITINERARY +
            " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DT + " FLOAT," + KEY_START_TIME + " LONG," + KEY_STOP_TIME + " LONG," +
            KEY_NBR_SB + " INTEGER, " + KEY_POWER_CONSUMPTION + " FLOAT" + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables
        Log.e(LOG, CREATE_TABLE_ITINERARY);
        db.execSQL(CREATE_TABLE_ITINERARY);

        Log.e(LOG, CREATE_TABLE_MARKER);
        db.execSQL(CREATE_TABLE_MARKER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);

        //create new tables
        onCreate(db);
    }


    // Create a itinerary
    public long createItinerary(Itinerary itinerary) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DT, itinerary.getDt());
        values.put(KEY_START_TIME, itinerary.getStartTime());
        values.put(KEY_STOP_TIME, itinerary.getStopTime());
        values.put(KEY_NBR_SB, itinerary.getNbr_sb());
        Log.e(LOG, "put getNbr_sb " + itinerary.getNbr_sb());
        values.put(KEY_POWER_CONSUMPTION, itinerary.getAllPowerConsumption());

        //insert row
        long itinerary_id = db.insert(TABLE_ITINERARY, null, values);

        db.close();
        //this.closeDB();

        return itinerary_id;
    }

    //Get an itinerary
    public Itinerary getItinerary(long time) {

        Itinerary itinerary = new Itinerary();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_ITINERARY + " WHERE " + KEY_START_TIME + " = " + time;

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null) {
            Log.e(LOG, "cursor is not null");
            cursor.moveToFirst();  //??????
        }

        itinerary.setDt(cursor.getFloat(cursor.getColumnIndex(KEY_DT)));
        itinerary.setStartTime(cursor.getLong(cursor.getColumnIndex(KEY_START_TIME)));
        itinerary.setStopTime(cursor.getLong(cursor.getColumnIndex(KEY_STOP_TIME)));
        itinerary.setNbr_sb(cursor.getInt(cursor.getColumnIndex(KEY_NBR_SB)));
        Log.e(LOG, "setNbr_sb " + cursor.getInt(cursor.getColumnIndex(KEY_NBR_SB))); //????why it changed to 0
        itinerary.setAllPowerConsumption(cursor.getFloat(cursor.getColumnIndex(KEY_POWER_CONSUMPTION)));


        if (itinerary == null) {
            Log.e(LOG, "itinerary is null");
            return null;
        }
        return itinerary;
    }

    //Get all itineraries
    public List<Itinerary> getAllItineraries() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITINERARY + " ORDER BY time DESC LIMIT 3", null);
        List<Itinerary> result = new ArrayList<>();
        while ((cursor.moveToNext())) {

            Itinerary itinerary = new Itinerary();
            itinerary.setDt(cursor.getLong(cursor.getColumnIndex(KEY_DT)));
            itinerary.setAllPowerConsumption(cursor.getFloat(cursor.getColumnIndex(KEY_POWER_CONSUMPTION)));
            itinerary.setNbr_sb(cursor.getInt(cursor.getColumnIndex(KEY_NBR_SB)));
            itinerary.setStartTime(cursor.getLong(cursor.getColumnIndex(KEY_START_TIME)));
            itinerary.setStopTime(cursor.getLong(cursor.getColumnIndex(KEY_STOP_TIME)));

            result.add(itinerary);
        }

        return result;
    }



    //Create a Marker
    public long createMarker(Tp2Marker marker, long itinerary_id) {

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

        //db.close();
        this.closeDB();

        return marker_id;

    }

    //get a marker
    public Tp2Marker getMarker(long marker_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MARKER + " WHERE " + KEY_ID + " = " + marker_id;

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null) {
            cursor.moveToFirst();  //??????
        }


        Tp2Marker marker = new Tp2Marker();
        marker.setAltitude(cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)));
        marker.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
        marker.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
        marker.setIm(cursor.getLong(cursor.getColumnIndex(KEY_IM)));
        marker.setMod_loc(cursor.getString(cursor.getColumnIndex(KEY_MOD_LOC)));
        marker.setNiv_batt(cursor.getFloat(cursor.getColumnIndex(KEY_NIV_BATT)));
        marker.setInfo(cursor.getString(cursor.getColumnIndex(KEY_INFO)));

        return marker;
    }

    public void deleteMarker(long _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MARKER, KEY_ID + " = ? ", new String[] {String.valueOf(_id)});
    }

    //SELECT * FROM markers marker, itineraries itinerary WHERE itinerary.start_time = "" AND itinerary.id = marker._id_itinerary
    //get all markers of an itinerary
    public List<Tp2Marker> getMarkersOfItinerary(long startTime) {

        List<Tp2Marker> markers = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_MARKER + " marker, "
                + TABLE_ITINERARY + " itinerary WHERE itinerary. " + KEY_START_TIME + " = '" +
                startTime + "'" + " AND itinerary." + KEY_ID + " = " + "marker." + KEY_ID_ITINERARY;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if(c.moveToFirst()) {
            do {
                Tp2Marker marker = new Tp2Marker();
                marker.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                marker.setItineraryId(c.getInt(c.getColumnIndex(KEY_ID_ITINERARY)));
                marker.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                marker.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                marker.setAltitude(c.getDouble(c.getColumnIndex(KEY_ALTITUDE)));
                marker.setIm(c.getLong(c.getColumnIndex(KEY_IM)));
                marker.setMod_loc(c.getString(c.getColumnIndex(KEY_MOD_LOC)));
                marker.setNiv_batt(c.getFloat(c.getColumnIndex(KEY_NIV_BATT)));
                marker.setInfo(c.getString(c.getColumnIndex(KEY_INFO)));

                //ade to markers list
                markers.add(marker);
            } while (c.moveToNext());
        }

        return markers;
    }


    //Delete an itinerary and all markers with the start time of this itinerary
    public void deleteItinerary(long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get all markers under this itinerary
        List<Tp2Marker> markers = getMarkersOfItinerary(time);

        //delete all these markers
        for (Tp2Marker marker : markers) {
            //delete marker
            deleteMarker(marker.getId());
        }

        //delete itinerary
        db.delete(TABLE_ITINERARY, KEY_START_TIME + "  ?", new String[]{String.valueOf(time)});

    }



    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
