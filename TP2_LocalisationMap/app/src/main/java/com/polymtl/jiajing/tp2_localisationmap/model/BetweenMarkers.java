package com.polymtl.jiajing.tp2_localisationmap.model;

import android.location.Location;

/**
 * Created by Zoe on 15-02-23.
 */
public class BetweenMarkers {

    private Marker markerA, markerB;

    private Location locationA, locationB;

    private Power powerA, powerB;
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

        this.powerA = markerA.getNiv_batt();
        this.powerB = markerB.getNiv_batt();

    }

    public float getDrp() {
        return locationB.distanceTo(locationA);
    }

    public float getVm() {
        return getDrp()/(locationB.getTime() - locationA.getTime());
    }

    public float getPowerConsumption() {
        this.powerConsumption = powerA.getPowerLever() - powerB.getPowerLever();
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

}
