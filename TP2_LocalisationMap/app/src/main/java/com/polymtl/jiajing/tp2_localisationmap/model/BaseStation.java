package com.polymtl.jiajing.tp2_localisationmap.model;

/**
 * Created by Zoe on 15-03-03.
 */
public class BaseStation {
    private double latitude, longitude;
    long id, itinerary_id;

    public void setLatitude(double latitude) {
        this.latitude = latitude;

    }
    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return this.longitude;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return this.id;
    }

    public void setItineraryId(long id) {
        this.itinerary_id = id;
    }
    public long getItinerary_id() {
        return this.itinerary_id;
    }
}
