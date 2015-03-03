package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiajing on 2015/2/20.
 */
public class Tp2Marker implements ConnectInfo {

    private int id;
    private int itineraryId;
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

    private Geocoder geocoder;
    private String address;

    public Tp2Marker() {};

    public Tp2Marker(Location location, Context context) {
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

        this.geocoder = new Geocoder(context);
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getItineraryId() {
        return this.itineraryId;
    }
    public void setItineraryId(int id) {
        this.itineraryId = itineraryId;
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
        return this.longitude;
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

    public String getAddress() {

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {

                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    this.address += addresses.get(0).getAddressLine(i) + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return this.address;
    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    @Override
    public String getInfo() {
        return null;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}