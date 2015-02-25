package com.polymtl.jiajing.tp2_localisationmap.model;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoe on 15-02-23.
 */
public class BetweenMarkers {

    private Marker markerA, markerB;

    private Location locationA, locationB;

    private float powerLevelA, powerLevelB;
    private float powerConsumption;

    private enum Direction{
        EAST,SOUTH,WEST,NORTH,SOUTH_EAST,SOUTH_WEST,NORTH_EAST,NORTH_WEST;
    }
    private float Drp; //la distance relative parcourue
    private float Vm; //la vitesse moyeene

    private Direction Dir_dep;

    public BetweenMarkers(Marker markerA, Marker markerB) {
        this.markerA = markerA;
        this.markerB = markerB;

        this.locationA = markerA.getLocation();
        this.locationB = markerB.getLocation();

        this.powerLevelA = markerA.getNiv_batt();
        this.powerLevelB = markerB.getNiv_batt();

    }

    public float getDrp() {
        return locationB.distanceTo(locationA);
    }

    public float getVm() {
        return getDrp()/(locationB.getTime() - locationA.getTime());
    }

    public float getPowerConsumption() {
        this.powerConsumption = powerLevelA - powerLevelB;
        return this.powerConsumption;
    }

    //calculate direction from locationB to locationA
    public Direction getDir_dep() {
        Direction direction = Direction.EAST;
        double latitudeA = locationA.getLatitude();
        double longitudeA = locationA.getLongitude();
        double latitudeB = locationB.getLatitude();
        double longitudeB = locationB.getLongitude();

        boolean BNothA, BEastA;

        if ((latitudeB - latitudeA) > 0) {
            BNothA = true;
        } else {
            BNothA = false;
        }

        if((longitudeB >= 0 && latitudeA >= 0) || (longitudeB <= 0 && longitudeA <= 0)) { //A,B are all in East longitude or are all in West longitude
            if(longitudeB >= longitudeA)
                BEastA = true;
            else
                BEastA = false;
        } else if(longitudeB < 0) { //B is in West longitude, A is in East longitude
            if (longitudeA - longitudeB >= 180) {
                BEastA = true;
            } else {
                BEastA = false;
            }

        } else { //B is in East longitude, A is in West longitude
            if(longitudeB - longitudeA <= 180) {
                BEastA = true;
            } else {
                BEastA = false;
            }

        }

        if(latitudeA == latitudeB && longitudeA == longitudeB) { //A, B are same position
            direction = null;
        }
        if (longitudeB == longitudeA) { //A and B are same longitude
            if (BNothA) {
                direction = Direction.NORTH;
            } else {
                direction = Direction.SOUTH;
            }
        }
        if (latitudeA == latitudeB) { //A and B are same latitude
            if (BEastA) {
                direction = Direction.EAST;
            } else {
                direction = Direction.WEST;
            }
        }

        if(latitudeA != latitudeB && longitudeA != longitudeB) {
            if(BNothA && BEastA) {
                direction = Direction.NORTH_EAST;
            }
            if(BNothA && !BEastA) {
                direction = Direction.NORTH_WEST;
            }
            if (!BNothA && BEastA) {
                direction = Direction.SOUTH_EAST;
            }
            if(!BNothA && !BEastA) {
                direction = Direction.SOUTH_WEST;
            }
        }

        return direction;
    }

    ////????????????this method put here well or not??????
    /*public void drawLineBetweenTwoMarkers(GoogleMap map, LatLng from, LatLng to) {
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(from);
        points.add(to);

        map.addPolyline(new PolylineOptions()
             .add(from, to)
             .width(5)
             .color(Color.RED));
    }*/

}
