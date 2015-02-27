package com.polymtl.jiajing.tp2_localisationmap;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.location.LocationProvider;

import android.location.Address;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectGPSInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectMode;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectNetworkInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.Frequency;
import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerGPS;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerNetwork;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.model.Power;
import com.polymtl.jiajing.tp2_localisationmap.model.ZoomLevel;
import com.polymtl.jiajing.tp2_localisationmap.service.AdjustCamera;
import com.polymtl.jiajing.tp2_localisationmap.service.DetectConnectivity;
import com.polymtl.jiajing.tp2_localisationmap.service.DrawPathAsyncTask;
import com.polymtl.jiajing.tp2_localisationmap.service.DrawTp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.service.Tp2InfoWindowAdapter;
import com.polymtl.jiajing.tp2_localisationmap.service.Tp2PolyLine;
import com.polymtl.jiajing.tp2_localisationmap.tp2Test.TestData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.polymtl.jiajing.tp2_localisationmap.service.Tp2PolyLine.*;


public class MapsActivity extends FragmentActivity {

    private Context context;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ImageButton btn_fromTo, btn_start_stop, btn_power;
    private PopupWindow destinationPopup;
    private View destinationView;
    private String addressTo, addressFrom;
    private LatLng fromLatLng, toLatLng;

    private LocationManager locationManager;
    private Location location;
    private String provider; //

    //private ConnectMode connectMode;
    private Power power;
    private ZoomLevel zoomLevel = new ZoomLevel();
    private Frequency frequency = new Frequency();
    private ConnectMode connectMode = new ConnectMode();

    final Tp2LocationListener tp2Locationlistener = new Tp2LocationListener();



    private Itinerary thisItinerary;
    private Itinerary testItinerary; //used for testing

    private boolean isOpenTracking = false;

    private boolean isConnected; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = getApplicationContext();

        provider = getIntent().getStringExtra("connectMode"); //get provider defined by user

        power = new Power(getApplicationContext());
        zoomLevel.setZoomLevel(getIntent().getIntExtra("zoom", zoomLevel.getZoomLevel()));
        frequency.setTime(getIntent().getIntExtra("frequency", frequency.getFrequency()));

        isOpenTracking = false;

        locationManager =  (LocationManager) context.getSystemService(LOCATION_SERVICE);

        setUpMapIfNeeded();

        setUpButtons();

        setUpPopupDestination();

        /* TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

       Log.i("Mode initial:" , telephonyManager.getNetworkOperatorName() + " Phone Type:" + telephonyManager.getPhoneType());
        Log.i("Power level: " , " " + power.getPowerLever() );
        Log.i("Location: ", "longitude: " + location.getLongitude() + "  latitude:" + location.getLatitude());
        Log.i("Time: " , " " + location.getTime());
        Log.i("Mode connect: " , " " + connectMode.getProvider());*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView))
                    .getMap();

            // Then enable the My Location layer on the Map
            //The My Location button will be visible on the top right of the map.
            mMap.setMyLocationEnabled(true);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     *
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        // Move the camera instantly to Montreal.
        Log.i("setUpMap: " , "Move the camera instantly to Montreal.");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.508536, -73.597929), 15));

        if (!DetectConnectivity.isConnected(context)) {
            Log.i("setUpMap:", "can not connect ");
            return;
        }
        if (provider == null) {
            Log.i("setUpMap:" , "provider is null");
            return;
        }
        if (locationManager == null) {
            Log.i("setUpMap:" , "locationManager is null");
            return;
        }


        location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.i("setUpMap:", "location is null");
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, latLng);
        AdjustCamera.moveCamera(mMap,latLng, zoomLevel.getZoomLevel());

    }



    private void updateToNewLocation(Location location) {

        Log.i("updatTonewLocation: " , "start");

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, latLng);
        AdjustCamera.moveCamera(mMap, latLng, zoomLevel.getZoomLevel());
    }

    private void setUpButtons() {

        btn_fromTo = (ImageButton) findViewById(R.id.btn_destination);
        btn_start_stop = (ImageButton) findViewById(R.id.btn_start_stop);
        btn_power = (ImageButton) findViewById(R.id.btn_power);

        if(isOpenTracking) {
            btn_start_stop.setBackground(getResources().getDrawable(R.drawable.stop));
        }
        else {
            btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
        }

        btn_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!DetectConnectivity.isConnected(MapsActivity.this)) {
                    new AlertDialog.Builder(MapsActivity.this)
                            .setMessage("Maps is offline. Check your network connection.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Offline")
                            .setNegativeButton(android.R.string.ok, null).show();
                    Log.i("setUpButton:", "not connect");
                    return;
                }

                if(isOpenTracking) { //stop tracking
                    locationManager.removeUpdates(tp2Locationlistener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
                    isOpenTracking = false;

                    //Show battery level
                    Toast.makeText(MapsActivity.this, "Battery level: " + power.getPowerLever(), Toast.LENGTH_LONG);

                    //Show all markers in the map and information about this itinerary
                    /////
                    //The test itinerary
                    testItinerary = TestData.setTestItinerary(MapsActivity.this, provider);
                    showTestItinerary(testItinerary);
                }
                else { //start tracking
                    //Show battery level
                    Toast.makeText(context, "Battery level: " + power.getPowerLever(), Toast.LENGTH_LONG);

                    thisItinerary = new Itinerary();//initial this itinerary

                    //add the first marker
                    thisItinerary.increaseMarkers(new Tp2Marker(location, context));

                    //request listening location changed
                    //minDistance is 10 metres
                    locationManager.requestLocationUpdates(provider, frequency.getFrequency(), 10, tp2Locationlistener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.stop));
                    isOpenTracking = true;
                }
            }
        });

        btn_fromTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DetectConnectivity.isConnected(MapsActivity.this)) {
                    new AlertDialog.Builder(MapsActivity.this)
                            .setMessage("Maps is offline. Check your network connection.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Offline")
                            .setNegativeButton(android.R.string.ok, null).show();
                    Log.i("setUpButton:", "not connect");
                    return;
                }

                destinationPopup.setAnimationStyle(R.style.PopupAnimation);

                destinationPopup.showAtLocation(findViewById(R.id.btn_destination), Gravity.NO_GRAVITY, 0, 0);
                destinationPopup.setFocusable(true);
                destinationPopup.update();
            }
        });

    }

    private void addTp2Marker() {

        Tp2Marker tp2Marker;

        if (provider.equals(LocationManager.GPS_PROVIDER)) {

            ConnectGPSInfo info = new ConnectGPSInfo(context);
            tp2Marker = new MarkerGPS(location, context, info);

        } else {

            ConnectNetworkInfo info = new ConnectNetworkInfo(context);
            tp2Marker = new MarkerNetwork(location,context,info);

        }

       // itinerary.increaseMarkers(tp2Marker);
        //itinerary.increaseNbr_sb();

    }

    private final class Tp2LocationListener implements LocationListener {


        //Called when the location has changed.
        @Override
        public void onLocationChanged(Location location) {

            Log.i("LocationListener: ", "Location changed");
            updateToNewLocation(location);

            //add Tp2Marker
            thisItinerary.increaseMarkers(new Tp2Marker(location, context));

            //Need to add marker to database ?????
            ///
        }

        //Called when the provider status changes.
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                case LocationProvider.OUT_OF_SERVICE:
                    break;
            }
        }


        //Called when the provider is enabled by the user.
        @Override
        public void onProviderEnabled(String provider) {

            Log.i("LocationListener: ", provider + ": Provider Enabled");
            //location = locationManager.getLastKnownLocation(provider);
        }

        //Called when the provider is disabled by the user.
        @Override
        public void onProviderDisabled(String provider) {

            Log.i("LocationListener: ", provider + ": Provider Disabled");
            //updateToNewLocation(null);
        }
    }


    //Need add base station changed listener
    //
    //

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setMessage("Do you want to exit?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit Confirm")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MapsActivity.this.finish();
                            isOpenTracking = false;
                        }
                    }).setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public LatLng getLocationFromAddress(String strAddress) {
        if (strAddress == null || strAddress.isEmpty()) {
            return null;
        }
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        LatLng point = null;

        try {
            addresses = geocoder.getFromLocationName(strAddress, 5);

            if (addresses == null || addresses.size() <= 0) { //didn't find location by this address
                return null;
            }
            Address location = addresses.get(0);

            point = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return point;
    }


    private void setUpPopupDestination() {

        LayoutInflater inflater = LayoutInflater.from(this);
        destinationView = inflater.inflate(R.layout.popup_destination, null);
        destinationPopup = new PopupWindow(destinationView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final EditText to = (EditText) destinationView.findViewById(R.id.to);
        final EditText from = (EditText) destinationView.findViewById(R.id.from);
        final ImageButton btn_search = (ImageButton) destinationView.findViewById(R.id.btn_searchDestination);

        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btn_search.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (to.getText().length() > 0) {
                    btn_search.setEnabled(true);
                    addressTo = to.getText().toString();
                }
            }
        });

        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addressFrom = from.getText().toString();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationPopup.dismiss();
                destinationPopup.setFocusable(false);

                List<LatLng> points = new ArrayList<>();

                fromLatLng = getLocationFromAddress(addressFrom);

                if (fromLatLng == null) {
                    //Set current location as from point
                    Location locationFrom = locationManager.getLastKnownLocation(provider);

                    fromLatLng = new LatLng(locationFrom.getLatitude(), locationFrom.getLongitude());
                }
                toLatLng = getLocationFromAddress(addressTo);

                if (toLatLng == null) {
                    Toast.makeText(getApplicationContext(), "Didn't find location of destination!", Toast.LENGTH_LONG).show();
                    return;
                }
                points.add(fromLatLng);
                points.add(toLatLng);

                new DrawPathAsyncTask(mMap, fromLatLng,toLatLng, makeURL(fromLatLng, toLatLng)).execute();
                DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, fromLatLng);
                DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, toLatLng);

                //InfoWindowAdapter show information
                mMap.setInfoWindowAdapter(new Tp2InfoWindowAdapter(MapsActivity.this));

                Log.i("destination:",points.size() + ": " + points.get(0).toString() + ", " + points.get(1));
                AdjustCamera.fixZoom(mMap, points);

            }
        });

    }


    private void showTestItinerary(Itinerary testItinerary) {

        List<Tp2Marker> ps = testItinerary.getTp2Markers();
        Log.i("test:", "Tp2Marker are " + ps.size());
        List<LatLng> points = new ArrayList<>();
        Iterator<Tp2Marker> i = ps.iterator();
        int n = 0;
        LatLng from, to;
        from = i.next().getLatLng();
        DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, from);


        while (i.hasNext()) {
            Log.i("test:", "has next");

            Tp2Marker p = i.next();

            to = p.getLatLng();

            DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, to);
            Log.i("test:", "set a marker " + n++);

            Tp2PolyLine.drawLineBetweenTwoMarkers(mMap,from, to);
            from = to;

            points.add(p.getLatLng());
        }
        //drawLineBetweenTwoMarkers(points.get(0), points.get(points.size()-1));

        //it doesn't work, maybe use Overlayer

        //new DrawPathAsyncTask(mMap,points.get(0),points.get(points.size() - 1),
         //       makeURL(points.get(0), points.get(points.size() - 1))).execute();
        AdjustCamera.fixZoom(mMap, points);

    }
}
