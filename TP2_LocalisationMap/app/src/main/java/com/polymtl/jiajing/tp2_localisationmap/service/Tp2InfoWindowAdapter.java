package com.polymtl.jiajing.tp2_localisationmap.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.polymtl.jiajing.tp2_localisationmap.R;

/**
 * Created by Zoe on 15-02-27.
 */
public class Tp2InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    public Tp2InfoWindowAdapter (Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow (com.google.android.gms.maps.model.Marker marker){
        return null;
    }

    @Override
    public View getInfoContents (com.google.android.gms.maps.model.Marker marker){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.marker, null);

        TextView info = (TextView) v.findViewById(R.id.info);

        info.setText(marker.getPosition().toString()); //Show maker information

        return v;
    }
}