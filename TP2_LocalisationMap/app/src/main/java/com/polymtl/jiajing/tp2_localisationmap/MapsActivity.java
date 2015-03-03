package com.polymtl.jiajing.tp2_localisationmap;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.drawable.BitmapDrawable;
import android.location.LocationProvider;

import android.location.Address;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;

import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.polymtl.jiajing.tp2_localisationmap.database.DBHelper;
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

import static android.telephony.PhoneStateListener.LISTEN_CELL_LOCATION;
import static com.polymtl.jiajing.tp2_localisationmap.service.Tp2PolyLine.*;


public class MapsActivity extends FragmentActivity {

    private Context context;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ImageButton btn_fromTo, btn_start_stop, btn_state_info, btn_camera, btn_clean;
    private PopupWindow destinationPopup;
    private View destinationView;
    private String addressTo, addressFrom;
    private LatLng fromLatLng, toLatLng;

   // private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;//test
    //private GoogleApiClient googleApiClient;


    TelephonyManager telephonyManager;
    PhoneStateListener phoneStateListener;

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

    long itinerary_id;

    private List<Tp2Marker> markers;

    private List<Tp2Marker> testMarkers;

    private boolean isOpenTracking = false;

    private boolean isConnected; //

    private DBHelper dbHelper;

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

        //test
       /* googleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/

        markers = new ArrayList<>();

        setUpMapIfNeeded();

        setUpButtons();

        setUpPopupDestination();

         telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

       /*Log.i("Mode initial:" , telephonyManager.getNetworkOperatorName() + " Phone Type:" + telephonyManager.getPhoneType());
        Log.i("Power level: " , " " + power.getPowerLever() );
        Log.i("Location: ", "longitude: " + location.getLongitude() + "  latitude:" + location.getLatitude());
        Log.i("Time: " , " " + location.getTime());
        Log.i("Mode connect: " , " " + connectMode.getProvider());*/

        dbHelper = new DBHelper(getApplicationContext());



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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        // Move the camera instantly to Montreal.
        Log.i("setUpMap: " , "Move the camera instantly to Montreal.");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.508536, -73.597929), 15));

        /*if (!DetectConnectivity.isConnected(context)) {
            Log.i("setUpMap:", "can not connect ");
            return;
        }*/
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

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.i("onMyLocationButtonClick", "click");
                DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap,
                        new LatLng(location.getLatitude(),location.getLongitude()));

                return false;
            }
        });
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
        btn_state_info = (ImageButton) findViewById(R.id.btn_state_info);
        btn_camera = (ImageButton) findViewById(R.id.btn_camera);
        btn_clean = (ImageButton) findViewById(R.id.btn_clean);

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
                    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
                    isOpenTracking = false;

                    //Show battery level
                    Toast.makeText(MapsActivity.this, "Battery level: " + power.getPowerLever(), Toast.LENGTH_LONG);

                    //set thisItinerary
                    thisItinerary.setStartTime(markers.get(0).getIm());
                    thisItinerary.setStartPower(markers.get(0).getNiv_batt());

                    thisItinerary.setStopTime(markers.get(markers.size()-1).getIm());
                    thisItinerary.setStopPower(markers.get(markers.size()-1).getNiv_batt());

                    thisItinerary.setDt(markers.get(markers.size()-1).getLocation().distanceTo(markers.get(0).getLocation()));

                    //save thisItinerary and markers in DB
                   /* itinerary_id = dbHelper.createItinerary(thisItinerary);
                    for (int i = 0; i < markers.size(); i++) {
                        dbHelper.createMarker(markers.get(i), itinerary_id);
                    }*/

                    //Show all markers in the map and information about this itinerary
                    /////


                }
                else { //start tracking
                    //Show battery level
                    Toast.makeText(context, "Battery level: " + power.getPowerLever(), Toast.LENGTH_LONG);

                    thisItinerary = new Itinerary();//initial this itinerary

                    //add the first marker
                    markers.add(new Tp2Marker(location, context));

                    //request listening location changed
                    //minDistance is 10 metres
                    locationManager.requestLocationUpdates(provider, frequency.getFrequency(), 10, tp2Locationlistener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.stop));
                    isOpenTracking = true;

                    //start listening cell location changed
                    setUpPhoneStateListener();


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

        btn_state_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });

    }

    /////////test
   /* @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }*/
    //////////////////

    private final class Tp2LocationListener implements LocationListener {


        //Called when the location has changed.
        @Override
        public void onLocationChanged(Location location) {

            Log.i("LocationListener: ", "Location changed");
            updateToNewLocation(location);

            //add Tp2Marker
            markers.add(new Tp2Marker(location, context));

        }

        //Called when the provider status changes.
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("onStatusChanged:", "onStatusChanged");
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


    //set up base station changed listener
    public void setUpPhoneStateListener() {
        //if (telephonyManager != null && !telephonyManager.getNetworkOperator().equals(""))
        phoneStateListener = new PhoneStateListener() {
            //Cell location changed listener
            @Override
            public void onCellLocationChanged(CellLocation cellLocation) {
                String LOG_TAG = "PhoneStateListener";

                //number station increase
                thisItinerary.increaseNbr_sb();

                if (cellLocation instanceof GsmCellLocation) {
                    GsmCellLocation gcLoc = (GsmCellLocation) cellLocation;
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: GsmCellLocation "
                                    + gcLoc.toString());
                    Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getCid "
                            + gcLoc.getCid());
                    Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getLac "
                            + gcLoc.getLac());
                    Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getPsc"
                            + gcLoc.getPsc()); // Requires min API 9

                    //for test
                    Toast.makeText(MapsActivity.this,"Gsm: Cid" + gcLoc.getCid() +
                            "Lac:" + gcLoc.getLac() + "Psc:" + gcLoc.getPsc(), Toast.LENGTH_LONG);
                } else if (cellLocation instanceof CdmaCellLocation) {

                    CdmaCellLocation ccLoc = (CdmaCellLocation) cellLocation;
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: CdmaCellLocation "
                                    + ccLoc.toString());
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: CdmaCellLocation getBaseStationId "
                                    + ccLoc.getBaseStationId());
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
                                    + ccLoc.getBaseStationLatitude());
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
                                    + ccLoc.getBaseStationLongitude());
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: CdmaCellLocation getNetworkId "
                                    + ccLoc.getNetworkId());
                    Log.i(LOG_TAG,
                            "onCellLocationChanged: CdmaCellLocation getSystemId "
                                    + ccLoc.getSystemId());
                    //for test
                    Toast.makeText(MapsActivity.this,"ccLoc: CdmaCellLocation" + ccLoc.toString() +
                            "Latitude:" + ccLoc.getBaseStationLatitude() +
                            "Longitude:" + ccLoc.getBaseStationLongitude(), Toast.LENGTH_LONG);
                } else {
                    Log.i(LOG_TAG, "onCellLocationChanged: " + location.toString());
                }


            }
        };

        telephonyManager.listen(phoneStateListener, LISTEN_CELL_LOCATION);
    }





    @Override
    public void onBackPressed() {
        Log.i("onBackPressed", "start");
        if (destinationPopup != null && destinationPopup.isShowing()) {
            Log.i("onBackPressed", "dismiss");
            destinationPopup.dismiss();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setMessage("Do you want to close the map?")
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
        destinationPopup.setBackgroundDrawable(new BitmapDrawable());
        destinationPopup.setOutsideTouchable(true);

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


    private void prepareTestData() {
        //The test itinerary
        //Create itinerary
        TestData testData = new TestData();
        testData.setTestItinerary(getApplicationContext(), provider);
        testItinerary = testData.getTestItinerary();
        //Log.i("test get itinerary:", " Nbr_sb: "+testItinerary.getNbr_sb());
        testMarkers = testData.getTestMarkers();

        //Insert testItinerary in db
        long testitinerary_id = dbHelper.createItinerary(testItinerary);
        //Insert markers in db
        for (int i = 0; i < testMarkers.size(); i++) {
            dbHelper.createMarker(testMarkers.get(i), testitinerary_id);
        }

        Itinerary test = dbHelper.getItinerary(1424924854700l);

        if (test == null) {
            Log.i("test get itinerary", " " + "test is null");
        }

        Log.i("test get itinerary", " getDt " + test.getDt());
        Log.i("test get itinerary", "getStartTime " + test.getStartTime());
        Log.i("test get itinerary", " getNbr_sb " + test.getNbr_sb()); //????why it changed to 0


        showTestItinerary();
    }


    private void showTestItinerary() {

        TestData testData = new TestData();
        testData.setTestItinerary(getApplicationContext(), provider);
        testItinerary = testData.getTestItinerary();
        //Log.i("test get itinerary:", " Nbr_sb: "+testItinerary.getNbr_sb());
        testMarkers = testData.getTestMarkers();

        //Insert testItinerary in db
        long testItinerary_id = dbHelper.createItinerary(testItinerary);
        //Insert markers in db
        for (int i = 0; i < testMarkers.size(); i++) {
            dbHelper.createMarker(testMarkers.get(i), testItinerary_id);
        }

        Itinerary test = dbHelper.getItinerary(1424924854700l);

        if (test == null) {
            Log.i("test get itinerary", " " + "test is null");
        }

        Log.i("test get itinerary", " getDt " + test.getDt());
        Log.i("test get itinerary", "getStartTime " + test.getStartTime());
        Log.i("test get itinerary", " getNbr_sb " + test.getNbr_sb()); //????why it changed to 0

        ///////draw
        List<LatLng> points = new ArrayList<>();
        Iterator<Tp2Marker> i = testMarkers.iterator();
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

            Tp2PolyLine.drawLineBetweenTwoMarkers(mMap, from, to);
            from = to;

            points.add(p.getLatLng());
        }

        AdjustCamera.fixZoom(mMap, points);

    }
}
