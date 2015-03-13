package com.polymtl.jiajing.tp2_localisationmap.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Zoe on 15-02-22.
 */
public class Itinerary {

    private int id;
    private float Dt; //la distance total
    //private List<Tp2Marker> tp2Markers; //???if it is necessary
    private int Nbr_sb; // count of base stations
    private float startPower, stopPower, allPowerConsumption;
    private String startTime, stopTime;

    public Itinerary() {

        //this.tp2Markers = new ArrayList<>();

        this.Dt = 0;
    }


    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        //this.startTime = this.tp2Markers.get(0).getIm();
        return this.startTime;
    }

    public void setStartTime(String time) {
        this.startTime = time;
    }

    public void setStopTime(String time) {
        this.stopTime = time;
    }
    public String getStopTime() {
        //this.stopTime = this.tp2Markers.get(tp2Markers.size()-1).getIm();
        return this.stopTime;
    }

    public void setNbr_sb(int nbr_sb) {
       this.Nbr_sb = nbr_sb;
    }
    public int getNbr_sb() {
        return this.Nbr_sb;
    }

    //base station changed
    public void increaseNbr_sb() {
        this.Nbr_sb++;
    }

    /*public void increaseMarkers(Tp2Marker tp2Marker) {

        this.tp2Markers.add(tp2Marker);
    }*/

    /*public void setTp2Markers(List<Tp2Marker> tp2Markers) {
        this.tp2Markers = tp2Markers;
    }*/

    /*public List<Tp2Marker> getTp2Markers() {
        return this.tp2Markers;
    }*/

    public void setStartPower(float power) {
        this.startPower = power;
    }

    public float getStartPower() {
        return this.startPower;
    }

    public void setStopPower(float power) {
        this.stopPower = power;
    }
    public float getStopPower() {
        return this.stopPower;
    }

    public float getAllPowerConsumption() {

        return this.startPower - this.stopPower;
        //return tp2Markers.get(0).getNiv_batt() - tp2Markers.get(tp2Markers.size()-1).getNiv_batt();
    }
    public void setAllPowerConsumption(float powerConsumption) {
        this.allPowerConsumption = powerConsumption;
    }

    public float getDt() {

       /* if (tp2Markers.size() <= 0) {
            return 0;
        }
        this.Dt = tp2Markers.get(0).getLocation().distanceTo(tp2Markers.get(tp2Markers.size()-1).getLocation());*/
        return this.Dt;
    }



    public void setDt(float dt) {
        this.Dt = dt;
    }


}
