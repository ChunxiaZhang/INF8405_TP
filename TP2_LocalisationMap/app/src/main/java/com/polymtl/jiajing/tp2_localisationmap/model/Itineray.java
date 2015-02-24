package com.polymtl.jiajing.tp2_localisationmap.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by Zoe on 15-02-22.
 * We think this class should have only one instance,
 * so we used Singleton pattern
 */
public class Itineray {

    private static Itineray instance = null;
    private float Dt; //la distance total
    private Vector<Marker> markers;
    private ZoomLevel zoom;
    private int Nbr_sb; // count of base stations
    private float allPowerConsumption;

    protected Itineray(ZoomLevel zoom) {

        this.markers = new Vector<Marker>();
        this.Nbr_sb = 0;
        this.Dt = 0;
        this.zoom = zoom;
    }
    public static Itineray getInstance(ZoomLevel zoom) {
        if (instance == null) {
            instance = new Itineray(zoom);
        }
        return instance;
    }

    public void increaseMarkers(Marker marker) {

        this.markers.add(marker);
    }

    public void increaseNbr_sb() {

        this.Nbr_sb++;
    }

    public float getAllPowerConsumption() {

        return markers.get(0).getNiv_batt().getPowerLever() - markers.get(markers.size()-1).getNiv_batt().getPowerLever();
    }

    public float getDt() {

        this.Dt = markers.get(0).getLocation().distanceTo(markers.get(markers.size()-1).getLocation());
        return this.Dt;
    }

}
