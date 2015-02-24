package com.polymtl.jiajing.tp2_localisationmap.model;

import android.location.Location;
import android.net.NetworkInfo;

/**
 * Created by Zoe on 15-02-23.
 */
public class MarkerNetwork extends Marker {

    private ConnectNetworkInfo connectInfo;

    private NetworkInfo networkInfo;

    public MarkerNetwork(Location location, ConnectNetworkInfo connectInfo) {

        super(location);
        this.connectInfo = connectInfo;
    }

    public ConnectNetworkInfo getConnectInfo() {
        return this.connectInfo;
    }

}
