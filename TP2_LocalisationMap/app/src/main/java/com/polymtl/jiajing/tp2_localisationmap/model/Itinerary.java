package com.polymtl.jiajing.tp2_localisationmap.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Zoe on 15-02-22.
 */
public class Itinerary {

   // private static Itinerary instance = null;
    private float Dt; //la distance total
    private List<Tp2Marker> tp2Markers; //???if it is necessary
    private int Nbr_sb; // count of base stations
    private float allPowerConsumption;
    private long startTime, stopTime;

    public Itinerary() {

        this.tp2Markers = new ArrayList<>();
        this.Dt = 0;
    }


    public long getStartTime() {
        this.startTime = this.tp2Markers.get(0).getIm();
        return this.startTime;
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

    public void setStopTime(long time) {
        this.stopTime = time;
    }
    public long getStopTime() {
        this.stopTime = this.tp2Markers.get(tp2Markers.size()-1).getIm();
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

    public void increaseMarkers(Tp2Marker tp2Marker) {

        this.tp2Markers.add(tp2Marker);
    }

    public void setTp2Markers(List<Tp2Marker> tp2Markers) {
        this.tp2Markers = tp2Markers;
    }

    public List<Tp2Marker> getTp2Markers() {
        return this.tp2Markers;
    }



    public float getAllPowerConsumption() {

        return tp2Markers.get(0).getNiv_batt() - tp2Markers.get(tp2Markers.size()-1).getNiv_batt();
    }
    public void setAllPowerConsumption(float powerConsumption) {
        this.allPowerConsumption = powerConsumption;
    }

    public float getDt() {

        this.Dt = tp2Markers.get(0).getLocation().distanceTo(tp2Markers.get(tp2Markers.size()-1).getLocation());
        return this.Dt;
    }
    public void setDt(float dt) {
        this.Dt = dt;
    }


}
