package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Zoe on 15-02-23.
 */
public class ConnectGPSInfo implements ConnectInfo {
    private Context context;
    private String GPSInfo;

    public ConnectGPSInfo(Context context) {
        this.context = context;
    }

    @Override
    public String getInfo() {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (!wifiManager.isWifiEnabled() || wifiInfo.getBSSID() == null) {
            return null;
        } else {
            this.GPSInfo = "PA_Wifi(" + wifiInfo.getSSID() + ", " + wifiInfo.getRssi()
                    + ", " + wifiInfo.getBSSID() + ")";
        }

        return this.GPSInfo;
    }
}
