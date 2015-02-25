package com.polymtl.jiajing.tp2_localisationmap.model;

import java.util.Vector;

/**
 * Created by Zoe on 15-02-22.
 */
public class Itinerary {

   // private static Itinerary instance = null;
    private float Dt; //la distance total
    private Vector<Marker> markers; //???if it is necessary
    private int Nbr_sb; // count of base stations
    private float allPowerConsumption;
    private long startTime;

    public Itinerary() {}

    public Itinerary(long time) {

        this.markers = new Vector<Marker>();
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

    public void increaseMarkers(Marker marker) {

        this.markers.add(marker);
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

        return markers.get(0).getNiv_batt() - markers.get(markers.size()-1).getNiv_batt();
    }
    public void setAllPowerConsumption(float powerConsumption) {
        this.allPowerConsumption = powerConsumption;
    }

    public float getDt() {

        this.Dt = markers.get(0).getLocation().distanceTo(markers.get(markers.size()-1).getLocation());
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
