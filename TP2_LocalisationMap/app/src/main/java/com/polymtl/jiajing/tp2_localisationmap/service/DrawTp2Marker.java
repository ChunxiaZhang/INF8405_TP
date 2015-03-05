package com.polymtl.jiajing.tp2_localisationmap.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polymtl.jiajing.tp2_localisationmap.R;
import com.polymtl.jiajing.tp2_localisationmap.model.BaseStation;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerGPS;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;

/**
 * Created by Zoe on 15-02-27.
 */
public class DrawTp2Marker {

    public static void setTp2Marker(Context context, GoogleMap mMap, LatLng latLng) {

        MarkerOptions markerOpt = new MarkerOptions();

        markerOpt.position(latLng);
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//set to be center of the picture
        markerOpt.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
        mMap.addMarker(markerOpt);
        mMap.setInfoWindowAdapter(new Tp2InfoWindowAdapter(context));

    }

    public static void setTp2Marker(Context context, GoogleMap mMap, Tp2Marker tp2Marker) {

        MarkerOptions markerOpt = new MarkerOptions();

        markerOpt.position(tp2Marker.getLatLng());
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//set to be center of the picture
        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        mMap.addMarker(markerOpt);
        mMap.setInfoWindowAdapter(new Tp2InfoWindowAdapter(context, tp2Marker));

    }

    public static void setStationMarker(Context context, GoogleMap map, BaseStation station) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(station.getLatitude(),station.getLongitude()));
        markerOptions.draggable(false);
        markerOptions.visible(true);
        markerOptions.title(markerOptions.getPosition().toString());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerStation));
        map.addMarker(markerOptions);

    }
}
