package com.polymtl.jiajing.tp2_localisationmap.model;

import android.location.LocationManager;

/**
 * Created by Zoe on 15-02-21.
 */
public class ConnectMode {

    private String provider;

    public ConnectMode() {

        provider = LocationManager.GPS_PROVIDER;

    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return this.provider;
    }


}
