package com.polymtl.jiajing.tp2_localisationmap.util;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

/**
 * Created by Zoe on 15-02-27.
 */
public class AdjustCamera {

    public static LatLngBounds.Builder bc;
    //Show all the points in the map
    public static void fixZoom(final GoogleMap mMap, List<LatLng> points) {
        bc = new LatLngBounds.Builder();
        for (LatLng item : points) {
            bc.include(item);
        }
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
        } catch (IllegalStateException ise) {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
                }
            });
        }

    }

    public static void moveCamera(GoogleMap mMap, LatLng latLng, int zoomLevel) {
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        Log.i("setUpMap: ", "Zoom out to zoom level 10, animating with a duration of 2 seconds.");
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoomLevel)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


}
