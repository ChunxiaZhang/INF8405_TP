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
    private long startTime;

    public Itinerary() {}

    public Itinerary(long time) {

        this.tp2Markers = new ArrayList<>();
        this.Nbr_sb = 0;
        this.Dt = 0;
        this.startTime = time;

    }
   /* public static Itinerary getInstance(ZoomLevel zoom) {
        if (instance == null) {
            instance = new Itinerary(zoom);
        }
        return instance;
    }*/


     public void increaseMarkers(Tp2Marker tp2Marker) {

        this.tp2Markers.add(tp2Marker);
    }

    public void setTp2Markers(List<Tp2Marker> tp2Markers) {
        this.tp2Markers = tp2Markers;
    }

    public List<Tp2Marker> getTp2Markers() {
        return this.tp2Markers;
    }

    public void increaseNbr_sb() {

        this.Nbr_sb++;
    }

    public int getNbr_sb() {
        return this.Nbr_sb;
    }
    public void setNbr_sb(int nbr_sb) {
        this.Nbr_sb = nbr_sb;
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

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

}
