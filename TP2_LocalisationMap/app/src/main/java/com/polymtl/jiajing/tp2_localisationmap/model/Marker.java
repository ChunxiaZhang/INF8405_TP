package com.polymtl.jiajing.tp2_localisationmap.model;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

/**
 * Created by Jiajing on 2015/2/20.
 */
public class Marker {

    //Coord
    private double latitude;
    private double longitude;
    private double altitude;

    private long Im; // the UTC time of this fix, in milliseconds since January 1, 1970.

    private String Mod_loc; //le mode de localisation: gps or network

    private Power Niv_batt;
    private Location location;

    public Marker(Location location) {
        this.location = location;

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        if(location.hasAltitude()) {
            this.altitude = location.getAltitude();
        }

        this.Im = location.getTime();

        this.Mod_loc =  location.getProvider();
    }

    public double getAltitude() {

        return this.altitude;
    }


}
