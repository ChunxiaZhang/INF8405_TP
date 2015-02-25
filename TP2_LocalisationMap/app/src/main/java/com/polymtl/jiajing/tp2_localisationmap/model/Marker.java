package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;

/**
 * Created by Jiajing on 2015/2/20.
 */
public class Marker implements ConnectInfo {

    //Coord
    private double latitude;
    private double longitude;
    private double altitude;

    private long Im; // the UTC time of this fix, in milliseconds since January 1, 1970.

    private String Mod_loc; //le mode de localisation: gps or network

    private Power power;
    private float Niv_batt;

    private String info;

    private Location location;

    public Marker() {};

    public Marker(Location location, Context context) {
        this.location = location;
        power = new Power(context);

        this.Niv_batt = power.getPowerLever();

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
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.getLongitude();
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getIm() {
        return this.Im;
    }
    public void setIm(long im) {
        this.Im = im;
    }

    public String getMod_loc() {
        return this.Mod_loc;
    }
    public void setMod_loc(String mode) {
        this.Mod_loc = mode;
    }

    public float getNiv_batt() {
        return this.Niv_batt;
    }
    public void setNiv_batt(float niv_batt) {
        this.Niv_batt = niv_batt;
    }

    public Location getLocation() {
        return this.location;
    }



    @Override
    public String getInfo() {
        return null;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
