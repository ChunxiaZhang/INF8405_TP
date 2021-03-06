package com.polymtl.jiajing.tp2_localisationmap.model;

import android.location.Location;

/**
 * Created by Zoe on 15-02-23.
 */
public class BetweenMarkers {

    private Tp2Marker tp2MarkerA, tp2MarkerB;

    private Location locationA, locationB;

    private float powerLevelA, powerLevelB;
    private float powerConsumption;


    private float Drp; //la distance relative parcourue
    private float Vm; //la vitesse moyeene

    private Direction Dir_dep;

    public BetweenMarkers(Tp2Marker tp2MarkerA, Tp2Marker tp2MarkerB) {
        this.tp2MarkerA = tp2MarkerA;
        this.tp2MarkerB = tp2MarkerB;

        this.locationA = tp2MarkerA.getLocation();
        this.locationB = tp2MarkerB.getLocation();

        this.powerLevelA = tp2MarkerA.getNiv_batt();
        this.powerLevelB = tp2MarkerB.getNiv_batt();

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

        boolean BNothA = false;
        boolean BEastA = false;

        if ((latitudeB - latitudeA) > 0) {
            BNothA = true;
        } else {
            BNothA = false;
        }

        /*if((longitudeB >= 0 && latitudeA >= 0) || (longitudeB <= 0 && longitudeA <= 0)) { //A,B are all in East longitude or are all in West longitude
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

        }*/

        //B is in West longitude, A is in East longitude
        if (longitudeA > 0 && latitudeB < 0) {
            if (longitudeA - longitudeB >= 180) {
                BEastA = true;
            } else {
                BEastA = false;
            }
        }

        //B is in East longitude, A is in West longitude
        if (latitudeA < 0 && latitudeB >0)
        {
            if(longitudeB - longitudeA <= 180) {
                BEastA = true;
            } else {
                BEastA = false;
            }

        }

        //if A and B are both in the East longitude
        if ((longitudeB >= 0 && latitudeA >= 0)) {
            if(longitudeB >= longitudeA)
                BEastA = true;
            else
                BEastA = false;
        }

        //if A and B are both in the West longitude
        if ((longitudeB <= 0 && longitudeA <= 0)) {
            if (longitudeB >= longitudeA) {
                BEastA = false;
            } else {
                BEastA = true;
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


    public String getStringDir_dep() {
        String result = "";
        switch (getDir_dep()) {
            case NORTH:
                result = "North";
                break;
            case NORTH_EAST:
                result = "North east";
                break;
            case NORTH_WEST:
                result = "North west";
                break;
            case SOUTH:
                result = "South";
                break;
            case SOUTH_EAST:
                result = "South east";
                break;
            case SOUTH_WEST:
                result = "South west";
                break;
            default:
                break;

        }
        return result;
    }

}
