package com.polymtl.jiajing.tp2_localisationmap.tp2Test;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.polymtl.jiajing.tp2_localisationmap.model.ConnectGPSInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerGPS;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zoe on 15-02-27.
 */
public class TestData {

    private Itinerary itinerary;
    private List<Tp2Marker> markers;

    public List<Tp2Marker> getTestMarkers() {

       return this.markers;
    }

    public Itinerary getTestItinerary() {
        return this.itinerary;
    }

    /**
     * Add information for a test itinerary
     * Only used by testing
     */

    public void setTestItinerary(Context context, String provider) {
       Itinerary testItinerary = new Itinerary();

        List<Tp2Marker> markers = new ArrayList<>();

//        testItinerary.setStartTime(1424924854700l);
        testItinerary.setStartTime("2013-08-08 09:26:32 GMT");

        //add some Location objects
        List<Location> locations = new ArrayList<>();
        Location l1 = new Location(provider);
        l1.setLatitude(45.458144);
        l1.setLongitude(-73.638387);
        l1.setTime(1424924854700l);
        locations.add(l1);

//        testItinerary.setStartTime(1424924854700l);
        testItinerary.setStartTime("2013-08-08 09:26:32 GMT");
        testItinerary.setStopPower(60);

        Location l2 = new Location(provider);
        l2.setLatitude(45.456878);
        l2.setLongitude(-73.639999);
        l2.setTime(1424924914700l);
        locations.add(l2);

        Location l3 = new Location(provider);
        l3.setLatitude(45.457992);
        l3.setLongitude(-73.638733);
        l3.setTime(1424924944700l);
        locations.add(l3);

        Location l4 = new Location(provider);
        l4.setLatitude(45.458895);
        l4.setLongitude(-73.637638);
        l4.setTime(1424924974700l);
        locations.add(l4);

        Location l5 = new Location(provider);
        l5.setLatitude(45.459753);
        l5.setLongitude(-73.637016);
        l5.setTime(1424925004700l);
        locations.add(l5);

        Location l6 = new Location(provider);
        l6.setLatitude(45.460633);
        l6.setLongitude(-73.636136);
        l6.setTime(1424925034700l);
        locations.add(l6);

        Location l7 = new Location(provider);
        l7.setLatitude(45.461822);
        l7.setLongitude(-73.635128);
        l7.setTime(1424925064700l);
        locations.add(l7);

        Location l8 = new Location(provider);
        l8.setLatitude(45.465765);
        l8.setLongitude(-73.631544);
        l8.setTime(1424925094700l);
        locations.add(l8);

        Location l9 = new Location(provider);
        l9.setLatitude(45.471905);
        l9.setLongitude(-73.626738);
        l9.setTime(1424925124700l);
        locations.add(l9);

        Location l10 = new Location(provider);
        l10.setLatitude(45.477020);
        l10.setLongitude(-73.621502);
        l10.setTime(1424925154700l);
        locations.add(l10);

        Location l11 = new Location(provider);
        l11.setLatitude(45.479669);
        l11.setLongitude(-73.621588);
        l11.setTime(1424925184700l);
        locations.add(l1);

        Location l12 = new Location(provider);
        l12.setLatitude(45.482136);
        l12.setLongitude(-73.625708);
        l12.setTime(1424925214700l);
        locations.add(l12);

        Location l13 = new Location(provider);
        l13.setLatitude(45.486228);
        l13.setLongitude(-73.626652);
        l13.setTime(1424925244700l);
        locations.add(l13);

        Location l14 = new Location(provider);
        l14.setLatitude(45.490620);
        l14.setLongitude(-73.623047);
        l14.setTime(1424925274700l);
        locations.add(l14);

        Location l15 = new Location(provider);
        l15.setLatitude(45.494652);
        l15.setLongitude(-73.619528);
        l15.setTime(1424925304700l);
        locations.add(l15);

        Location l16 = new Location(provider);
        l16.setLatitude(45.497239);
        l16.setLongitude(-73.618584);
        l16.setTime(1424925334700l);
        locations.add(l16);

        Location l17 = new Location(provider);
        l17.setLatitude(45.499104);
        l17.setLongitude(-73.621588);
        l17.setTime(1424925364700l);
        locations.add(l17);

        Location l18 = new Location(provider);
        l18.setLatitude(45.503676);
        l18.setLongitude(-73.618927);
        l18.setTime(1424925394700l);
        locations.add(l18);

//        testItinerary.setStopTime(1424925394700l);
        testItinerary.setStopTime("2013-08-08 09:26:32 GMT");
        testItinerary.setStopPower(50);


        Iterator<Location> i = locations.iterator();
        float lb = 99;
        Tp2Marker p;
        ConnectInfo info;

        while (i.hasNext()) {

            Location ltemp = i.next();
            Log.i("test:", "ltemp " + ltemp.getLatitude() + "," + ltemp.getLongitude());
            //if (provider == LocationManager.GPS_PROVIDER) {
            info = new ConnectGPSInfo(context);
            p = new MarkerGPS(ltemp,context,(ConnectGPSInfo)info);
            //} else {
            //  info = new ConnectNetworkInfo(context);
            //p = new MarkerNetwork(i.next(),context,(ConnectNetworkInfo)info);
            //}

            p.setLocation(ltemp);
            p.setNiv_batt(lb);
            p.setInfo(new ConnectGPSInfo(context).getInfo());
            p.setMod_loc(provider);
            lb = lb - 1; // level battery

            markers.add(p);

        }
        //testItinerary.setTp2Markers(markers);

        this.markers = markers;

        testItinerary.setDt(l18.distanceTo(l1));

        testItinerary.setNbr_sb(20);

        //testItinerary.setAllPowerConsumption();

        this.itinerary = testItinerary;

        Log.i("testData:", "Nbr_sb  " + this.itinerary.getNbr_sb());
    }



}
