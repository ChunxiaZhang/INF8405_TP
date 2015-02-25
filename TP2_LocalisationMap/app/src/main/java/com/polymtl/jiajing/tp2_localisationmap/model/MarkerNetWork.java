package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Location;
import android.net.NetworkInfo;

/**
 * Created by Zoe on 15-02-23.
 */
public class MarkerNetwork extends Marker {

    private ConnectNetworkInfo connectInfo;
    private String info;

    private NetworkInfo networkInfo;

    public MarkerNetwork(Location location, Context context, ConnectNetworkInfo connectInfo) {

        super(location, context);
        this.connectInfo = connectInfo;
    }

    public ConnectNetworkInfo getConnectInfo() {
        return this.connectInfo;
    }

    public String getInfo() {
        return connectInfo.getInfo();
    }

}
