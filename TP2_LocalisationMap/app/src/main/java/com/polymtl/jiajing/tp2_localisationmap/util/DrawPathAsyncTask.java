package com.polymtl.jiajing.tp2_localisationmap.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

/**
 * Created by Zoe on 15-02-27.
 */
public class DrawPathAsyncTask extends AsyncTask<Void, Void, String> {

    private GoogleMap mMap;
    private Polyline polyline;
    private LatLng from, to;
    private String url;

    public DrawPathAsyncTask(GoogleMap mMap, LatLng from, LatLng to, String urlPass) {

        Log.i("DrawPathAsyncTask", "creating");
        url = urlPass;
        this.mMap = mMap;
        this.from = from;
        this.to = to;

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

     }


    @Override
    protected String doInBackground(Void... params) {
        JSONParser jsonParser = new JSONParser();

        String json = jsonParser.getJSONFromUrl(url);
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.i("DrawPathAsyncTask", "onPostExecute");

        if (result != null) {
        Log.i("DrawPathAsyncTask", "drawPath");
        Tp2PolyLine.drawPath(mMap, result, from, to);
    }
}

}
