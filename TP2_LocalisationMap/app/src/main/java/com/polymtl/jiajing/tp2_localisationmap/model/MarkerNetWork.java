package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.location.Location;
import android.net.NetworkInfo;

/**
 * Created by Jiajing on 2015/3/5.
 */
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
