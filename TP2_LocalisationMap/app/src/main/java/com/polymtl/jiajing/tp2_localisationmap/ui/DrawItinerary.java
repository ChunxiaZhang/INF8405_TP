package com.polymtl.jiajing.tp2_localisationmap.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.polymtl.jiajing.tp2_localisationmap.database.DBHelper;
import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zoe on 15-03-03.
 */
public class DrawItinerary {

    public static void drawCurrentItinerary(Context context, GoogleMap mMap, Itinerary itinerary, DBHelper dbHelper) {

        List<Tp2Marker> markers = dbHelper.getMarkersOfItinerary(itinerary.getStartTime());
        if (markers.size() <= 0) {
            return;
        }

        Iterator<Tp2Marker> iterator = markers.iterator();
        Tp2Marker markerA = iterator.next();

        DrawTp2Marker.setTp2Marker(context, mMap, markerA, markers);
        Tp2Marker markerB = new Tp2Marker();



        while (iterator.hasNext()) {

            markerB = iterator.next();

            DrawTp2Marker.setTp2Marker(context, mMap, markerB, markers);

            Tp2PolyLine.drawLineBetweenTwoMarkers(mMap, markerA.getLatLng(), markerB.getLatLng(), Color.RED);

            markerA = markerB;
        }

        List<LatLng> points = new ArrayList<>();
        points.add(markers.get(0).getLatLng());
        points.add(markers.get(markers.size() - 1).getLatLng());
        AdjustCamera.fixZoom(mMap, points);
    }

    public static void drawAllItineraries(Context context, GoogleMap mMap, DBHelper dbHelper) {

        List<Itinerary> itineraries = dbHelper.getAllItineraries();

        List<LatLng> points = new ArrayList<>();

        if (itineraries.size() < 0) {
            return;
        }
        Log.i("itineraries size", "" + itineraries.size());
        Iterator<Itinerary> iterator = itineraries.iterator();
        int[] color = {Color.RED, Color.YELLOW, Color.GREEN};
        int colorIndex = 0;

        while (iterator.hasNext()) {

            List<Tp2Marker> markers = dbHelper.getMarkersOfItinerary(iterator.next().getStartTime());


            if (markers.size() > 0) {


                Iterator<Tp2Marker> i = markers.iterator();
                Tp2Marker markerA = i.next();
                Log.i("draw", "time " + markerA.getIm());
                DrawTp2Marker.setTp2Marker(context, mMap, markerA, markers);

                Tp2Marker markerB = new Tp2Marker();
                while (i.hasNext()) {


                    markerB = i.next();

                    DrawTp2Marker.setTp2Marker(context, mMap, markerB, markers);

                    Tp2PolyLine.drawLineBetweenTwoMarkers(mMap, markerA.getLatLng(), markerB.getLatLng(), color[colorIndex]);

                    markerA = markerB;
                }

                points.add(markers.get(0).getLatLng());
                points.add(markers.get(markers.size() - 1).getLatLng());
            }
            colorIndex++;

        }
        if (points.size() > 0) {
            AdjustCamera.fixZoom(mMap, points);
        }

    }
}
