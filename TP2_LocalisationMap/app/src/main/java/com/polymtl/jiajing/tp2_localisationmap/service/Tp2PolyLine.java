package com.polymtl.jiajing.tp2_localisationmap.service;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoe on 15-02-27.
 */
public class Tp2PolyLine {

    public static void drawLineBetweenTwoMarkers(GoogleMap mMap, LatLng from, LatLng to) {

        mMap.addPolyline(new PolylineOptions()
                .add(from, to)
                .width(5)
                .color(Color.RED));
    }

    /**
     * Draw path from start to end.
     */
    public static void drawPath(GoogleMap mMap, String result, LatLng from, LatLng to) {

        Log.i("draw path:", " start");
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(from);
        points.add(to);


        //mMap.clear();



        try {
            // Transform the string into a json object
            final JSONObject json = new JSONObject(result);

            JSONArray routeArray = json.getJSONArray("routes");

            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            Log.i("draw path:", "list number: " + list.size());
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng point = list.get(z);

                options.add(point);
            }
            Log.i("draw path:", "options number: " + points.size());
            mMap.addPolyline(options);
            Log.i("draw path:", "addPolyline");

        } catch (Exception e) {
            Log.i("draw path:", "Exception");
            e.printStackTrace();
        }

    }

    public static String makeURL(LatLng from, LatLng to) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin="); //from
        urlString.append(Double.toString(from.latitude) + "," + Double.toString(from.longitude));
        urlString.append("&destination="); //to
        urlString.append(Double.toString(to.latitude) + "," + Double.toString(to.longitude));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }


    public static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            lat += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            lng += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
