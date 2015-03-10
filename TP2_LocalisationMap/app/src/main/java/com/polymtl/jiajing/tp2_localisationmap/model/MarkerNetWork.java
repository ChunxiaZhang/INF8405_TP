package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Location;
import android.net.NetworkInfo;


public class MarkerNetwork extends Tp2Marker{
    private ConnectNetworkInfo connectInfo;
    private String info;
    private NetworkInfo networkInfo;

    public MarkerNetwork(Location location, Context context, ConnectNetworkInfo connectInfo){
        super(location,context);
        this.connectInfo = connectInfo;
    }
    public ConnectNetworkInfo getConnectInfo(){
        return this.connectInfo;
    }
    public String getInfo(){
        return connectInfo.getInfo();
    }
}
