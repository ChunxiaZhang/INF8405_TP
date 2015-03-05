package com.polymtl.jiajing.tp2_localisationmap.service;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.polymtl.jiajing.tp2_localisationmap.R;
import com.polymtl.jiajing.tp2_localisationmap.model.Direction;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Zoe on 15-02-27.
 */
public class Tp2InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private Tp2Marker tp2Marker;

    public Tp2InfoWindowAdapter (Context context) {
        this.context = context;
    }
    public Tp2InfoWindowAdapter (Context context, Tp2Marker tp2Marker) {
        this.context = context;
        this.tp2Marker = tp2Marker;
    }

    @Override
    public View getInfoWindow (com.google.android.gms.maps.model.Marker marker){
        return null;
    }

    @Override
    public View getInfoContents (com.google.android.gms.maps.model.Marker marker){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.marker, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.picture);
        imageView.setImageResource(R.drawable.picture);

        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(timeFromUTCSecs(context, tp2Marker.getIm()));//set tile with time

        TextView info = (TextView) v.findViewById(R.id.info);

        info.setText(marker.getPosition().toString()); //Show maker information

        return v;
    }

    public String getInfo() {
        String info = "";
        info += "lat/lng:(" + tp2Marker.getLatitude() + ", " + tp2Marker.getLongitude();
        info += "\nAlt:" + tp2Marker.getAltitude();
        info += "\nDir_dep:" + tp2Marker.getDir_dep();
        info += "\nDrp:" + tp2Marker.getDrp() + "  Vm:" + tp2Marker.getVm() + " Dt:" + tp2Marker.getDt();;
        info += "\nMod_loc: " + tp2Marker.getMod_loc();
        info += "\nNiv_batt:" + tp2Marker.getNiv_batt();
        info += "\nInfo:" + tp2Marker.getInfo();

        return info;
    }

    public static String timeFromUTCSecs(Context ctx, long secs) {
        return DateUtils.formatDateTime(ctx, secs * 1000,
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_NUMERIC_DATE);
    }


    /*private String getDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mmaa");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        String dt = dateFormatter.format(value);

        return dt;
    }*/

}