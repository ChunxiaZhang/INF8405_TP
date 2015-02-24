package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Zoe on 15-02-23.
 */
public class MarkerNetwork extends Marker {
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public MarkerNetwork(Location location) {
        super(location);
    }


}
