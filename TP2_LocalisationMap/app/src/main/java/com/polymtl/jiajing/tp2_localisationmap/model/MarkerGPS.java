package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Location;

/**
 * Created by Zoe on 15-02-23.
 */
public class MarkerGPS extends Marker{

    private ConnectGPSInfo connectInfo;

    public MarkerGPS(Location location, ConnectGPSInfo connectInfo) {

        super(location);
        this.connectInfo = connectInfo;
    }

    public ConnectGPSInfo getConnectInfo() {
        return this.connectInfo;
    }

}
