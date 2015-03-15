package com.polymtl.jiajing.tp2_localisationmap.ui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polymtl.jiajing.tp2_localisationmap.R;
import com.polymtl.jiajing.tp2_localisationmap.model.BaseStation;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.ui.Tp2InfoWindowAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Zoe on 15-02-27.
 */
public class DrawTp2Marker {

    public static void setTp2Marker(GoogleMap mMap, LatLng latLng) {

        MarkerOptions markerOpt = new MarkerOptions();

        markerOpt.position(latLng);
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//set to be center of the picture
        markerOpt.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
        markerOpt.snippet(latLng.toString());
        mMap.addMarker(markerOpt);

    }

    public static void setTp2Marker(Context context, GoogleMap mMap, Tp2Marker tp2Marker, List<Tp2Marker> markers) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getDefault());
        Date now = new Date(tp2Marker.getIm());
        String strDate = sdf.format(now);

        String info = "";
        info += strDate;
        info += "\nlat/lng:(" + tp2Marker.getLatitude() + ", " + tp2Marker.getLongitude()+")";
        info += "\nAlt:" + tp2Marker.getAltitude();
        info += "\nDir_dep:" + tp2Marker.getDir_dep();
        info += "\nDrp:" + tp2Marker.getDrp() + "  Vm:" + tp2Marker.getVm() + " Dt:" + tp2Marker.getDt();
        info += "\nMod_loc: " + tp2Marker.getMod_loc();
        info += "\nNiv_batt:" + tp2Marker.getNiv_batt();
        info += "\nInfo:" + tp2Marker.getInfo();

        MarkerOptions markerOpt = new MarkerOptions();

        markerOpt.position(tp2Marker.getLatLng());
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//set to be center of the picture
        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        markerOpt.title(tp2Marker.getId()+"");
        markerOpt.snippet(info);

        mMap.addMarker(markerOpt);
        mMap.setInfoWindowAdapter(new Tp2InfoWindowAdapter(context, markers));

    }

    public static void setStationMarker(Context context, GoogleMap map, BaseStation station) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(station.getLatitude(),station.getLongitude()));
        markerOptions.draggable(false);
        markerOptions.visible(true);
        markerOptions.title(markerOptions.getPosition().toString());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstation));
        map.addMarker(markerOptions);

    }


}
