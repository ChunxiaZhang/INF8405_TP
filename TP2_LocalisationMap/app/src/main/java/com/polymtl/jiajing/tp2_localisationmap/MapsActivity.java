package com.polymtl.jiajing.tp2_localisationmap;

import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationProvider;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import static android.telephony.PhoneStateListener.LISTEN_CELL_LOCATION;
import android.text.Editable;
import android.text.TextWatcher;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.polymtl.jiajing.tp2_localisationmap.database.DBHelper;
import com.polymtl.jiajing.tp2_localisationmap.model.BaseStation;
import com.polymtl.jiajing.tp2_localisationmap.model.BetweenMarkers;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectGPSInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectMode;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectNetworkInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.Frequency;
import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerGPS;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.model.Power;
import com.polymtl.jiajing.tp2_localisationmap.model.ZoomLevel;
import com.polymtl.jiajing.tp2_localisationmap.util.AdjustCamera;
import com.polymtl.jiajing.tp2_localisationmap.util.DetectConnectivity;
import com.polymtl.jiajing.tp2_localisationmap.util.DrawItinerary;
import com.polymtl.jiajing.tp2_localisationmap.util.DrawPathAsyncTask;
import com.polymtl.jiajing.tp2_localisationmap.util.DrawTp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.util.GetGsmCellLocation;
import com.polymtl.jiajing.tp2_localisationmap.util.Tp2PolyLine;
import com.polymtl.jiajing.tp2_localisationmap.tp2Test.TestData;

import static android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
import static com.polymtl.jiajing.tp2_localisationmap.util.Tp2PolyLine.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.util.Log;

public class MapsActivity extends FragmentActivity implements ImageCaptureFragment.PictureTakenListener{

    private Context context;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ImageButton btn_fromTo, btn_start_stop, btn_clean;
    private PopupWindow destinationPopup;
    private View destinationView;
    private String addressTo, addressFrom;
    private LatLng fromLatLng, toLatLng;

   // private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;//test
    //private GoogleApiClient googleApiClient;


    TelephonyManager telephonyManager;
    PhoneStateListener phoneStateListener;

    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiver;
    private WifiManager wifiManager;
    private Location location;
    private String provider;

    //private ConnectInfo connectInfo;

    private Power power;
    private ZoomLevel zoomLevel = new ZoomLevel();
    private Frequency frequency = new Frequency();
    private ConnectMode connectMode = new ConnectMode();
    private String connectInfo;
    private int cellSignalLevel;

    final Tp2LocationListener tp2Locationlistener = new Tp2LocationListener();

    private Itinerary thisItinerary;
    long itinerary_id;
    private Itinerary testItinerary; //used for testing


    private Tp2Marker currentMarker;
    private List<Tp2Marker> markers;
    private List<Tp2Marker> testMarkers;
    private List<BaseStation> stations;

    private boolean isOpenTracking = false;

    private DBHelper dbHelper;

    private boolean alertImgCapIsShowing = false;

    private boolean withHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = getApplicationContext();
        power = new Power(getApplicationContext());

        provider = getIntent().getStringExtra("connectMode"); //get provider defined by user
        zoomLevel.setZoomLevel(getIntent().getIntExtra("zoom", zoomLevel.getZoomLevel()));
        frequency.setTime(getIntent().getIntExtra("frequency", frequency.getFrequency()));
        withHistory = getIntent().getBooleanExtra("withHistory", false);
        Log.i("withHistory", "" + withHistory);

        isOpenTracking = false;

        locationManager =  (LocationManager) context.getSystemService(LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        dbHelper = new DBHelper(getApplicationContext());

        setUpMapIfNeeded();
        setUpButtons();
        setUpPopupDestination();

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
     *
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     *
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

        if (withHistory) { //look the history
            if (dbHelper.getAllItineraries().size() <= 0) {
                Toast.makeText(MapsActivity.this, "There is no itinerary history.",
                        Toast.LENGTH_LONG).show();
            }
            DrawItinerary.drawAllItineraries(MapsActivity.this, mMap, dbHelper);

        } else {

            // Move the camera instantly to Montreal.
            Log.i("setUpMap: ", "Move the camera instantly to Montreal.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.508536, -73.597929), 15));

            if (provider == null) {
                Log.i("setUpMap:", "provider is null");
                return;
            }
            if (locationManager == null) {
                Log.i("setUpMap:", "locationManager is null");
                return;
            }

            location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                Log.i("setUpMap:", "location is null");
                return;
            }

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            DrawTp2Marker.setTp2Marker(mMap, latLng);
            AdjustCamera.moveCamera(mMap, latLng, zoomLevel.getZoomLevel());

        }

    }


    private void setConnectInfo() {

        Log.i("setConnectInfo", "start");
        if (provider == LocationManager.GPS_PROVIDER) {
            //get best wifi info
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int bestLevel = 0;
                    ScanResult bestResult = null;
                    List<ScanResult> wifiScanList = wifiManager.getScanResults();
                    if (wifiScanList.size() > 0) {
                        for (int i = 0; i < wifiScanList.size(); i++) {
                            if (wifiScanList.get(i).level > bestLevel) {
                                bestLevel = wifiScanList.get(i).level;
                                bestResult = wifiScanList.get(i);
                                connectInfo = "PA_Wifi(" + bestResult.SSID + ", " + bestResult.level
                                        + ", " + bestResult.BSSID + ")";
                                Log.i("connectInfo", connectInfo);
                            }
                            bestLevel = wifiScanList.get(i).level > bestLevel ? wifiScanList.get(i).level : bestLevel;
                        }
                    }
                    unregisterReceiver(broadcastReceiver);
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        }

        if (provider == LocationManager.NETWORK_PROVIDER) {

            ConnectNetworkInfo networkInfo = new ConnectNetworkInfo(MapsActivity.this);
            networkInfo.setNiv_sig_sb(cellSignalLevel);
            connectInfo = networkInfo.getInfo();

            Log.i("connectInfo", connectInfo);
        }

    }

    private void initialCurrentMarker() {

        currentMarker = new Tp2Marker(location, MapsActivity.this);
        if (markers.size() > 0) {
            BetweenMarkers betweenMarkers = new BetweenMarkers(markers.get(markers.size()-1), currentMarker);
            currentMarker.setDir_dep(betweenMarkers.getStringDir_dep());
            currentMarker.setDrp(betweenMarkers.getDrp());
            currentMarker.setVm(betweenMarkers.getVm());
            //Dt is distance from the first marker to current marker
            currentMarker.setDt(new BetweenMarkers(markers.get(0), currentMarker).getDrp());
            currentMarker.setInfo(connectInfo);
        }

        //set id of the marker
        currentMarker.setId(markers.size());


        //add Tp2Marker
        markers.add(currentMarker);

    }

    private void alertOpenCamera() {
        //alert for taking pictures
        if(isOpenTracking && !alertImgCapIsShowing){
            alertImgCapIsShowing = true;
            new AlertDialog.Builder(MapsActivity.this).setMessage("Do you want to take a picture of the current location?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertImgCapIsShowing = false;
                        }
                    })
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            DialogFragment imgCapFrag = new ImageCaptureFragment();
                            imgCapFrag.show(ft, "dialog");
                        }
                    }).show();
        }
    }

    private void updateToNewLocation() {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //draw current marker
        DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, currentMarker);

        AdjustCamera.moveCamera(mMap, latLng, zoomLevel.getZoomLevel());

    }

    /**
     * launch after a picture is taken and saved
     * @param picturePath
     */
    @Override
    public void pictureTaken(String picturePath) {
        markers.get(markers.size()-1).setPicturePath(picturePath);
        //TODO set pictures for windows adapter
    }

    private void setUpButtons() {
        btn_fromTo = (ImageButton) findViewById(R.id.btn_destination);
        btn_start_stop = (ImageButton) findViewById(R.id.btn_start_stop);
        btn_clean = (ImageButton) findViewById(R.id.btn_clean);

        if (withHistory) {
            btn_fromTo.setEnabled(false);
            btn_start_stop.setEnabled(false);
            btn_clean.setEnabled(false);
        }

        if(isOpenTracking) {
            btn_start_stop.setBackground(getResources().getDrawable(R.drawable.stop));
        }else {
            btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
        }

        btn_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpenTracking) { //stop tracking
                    locationManager.removeUpdates(tp2Locationlistener);
                    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
                    isOpenTracking = false;

                    //set thisItinerary
                    thisItinerary.setStartTime(markers.get(0).getIm());
                    Log.i("save start time", " " + markers.get(0).getIm());
                    thisItinerary.setStartPower(markers.get(0).getNiv_batt());

                    thisItinerary.setStopTime(markers.get(markers.size()-1).getIm());
                    thisItinerary.setStopPower(markers.get(markers.size()-1).getNiv_batt());

                    thisItinerary.setDt(markers.get(markers.size()-1).getLocation().distanceTo(markers.get(0).getLocation()));

                    //save thisItinerary, stations and markers in DB
                    itinerary_id = dbHelper.createItinerary(thisItinerary);


                    for (Tp2Marker marker: markers) {
                        dbHelper.createMarker(marker, itinerary_id);
                    }

                    for (BaseStation station: stations) {
                        dbHelper.createStation(station, itinerary_id);
                    }

                    Log.i("after start time", "" + dbHelper.getItinerary(markers.get(0).getIm()).getStartTime());
                    //Show all markers in the map and information about this itinerary
                    DrawItinerary.drawCurrentItinerary(MapsActivity.this, mMap, thisItinerary, dbHelper);
                    //Show all base stations
                    //TODO

                } else { //start tracking
                    //clear the map
                    mMap.clear();

                    markers = new ArrayList<>();
                    stations = new ArrayList<>();

                    thisItinerary = new Itinerary();//initial this itinerary

                    //add the first marker
                    if(location == null) {
                        Log.i("Start tracking:", "location can't be fined");
                        Toast.makeText(MapsActivity.this, "Cannot get location, please make sure there is location serveice.",
                                Toast.LENGTH_LONG).show();

                        mMap.addMarker(new MarkerOptions().position(new LatLng(45.508536, -73.597929)).title("Marker"));
                        // Move the camera instantly to Montreal.
                        Log.i("Start tracking: " , "Move the camera instantly to Montreal.");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.508536, -73.597929), 15));
                        return;
                    }

                    //create the first marker
                    currentMarker = new Tp2Marker(location, context);

                    //set id of the marker, the id from 0
                    currentMarker.setId(markers.size());

                    markers.add(currentMarker);


                    //draw the first current marker
                    //DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, currentMarker);
                    DrawTp2Marker.setTp2Marker(mMap, new LatLng(location.getLatitude(), location.getLongitude()));

                    //request listening location changed
                    //minDistance is 10 metres
                    Log.i("frequency", "" + frequency.getFrequency());
                    locationManager.requestLocationUpdates(provider, frequency.getFrequency(), 10, tp2Locationlistener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.stop));
                    isOpenTracking = true;

                    //start listening cell location changed
                    if (provider == LocationManager.NETWORK_PROVIDER) {
                        setUpPhoneStateListener();
                    }

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

        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });

    }

    private final class Tp2LocationListener implements LocationListener {
        //Called when the location has changed.
        @Override
        public void onLocationChanged(Location location) {
            Log.i("LocationListener: ", "Location changed");
            setConnectInfo();
            initialCurrentMarker();
            updateToNewLocation();
            alertOpenCamera();


            //For Test
            BaseStation bs = new BaseStation();
            if (getGsmCellLocation() != null) {
                bs.setLatitude(getGsmCellLocation().latitude);
                bs.setLongitude(getGsmCellLocation().longitude);
                DrawTp2Marker.setStationMarker(MapsActivity.this, mMap, bs);
            }
            //need to add marker in database
            //??????????????????????
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

        Log.i("setUpPhoneStateListener","start");
        phoneStateListener = new PhoneStateListener() {
            //Cell location changed listener
            @Override
            public void onCellLocationChanged(CellLocation cellLocation) {
                Log.i("setUpPhoneStateListener","cellLocation change");
                //number station increase
                thisItinerary.increaseNbr_sb();

                BaseStation station = new BaseStation();
                if (cellLocation instanceof GsmCellLocation) {
                    Log.i("setUpPhoneStateListener","gsm");
                    if (getGsmCellLocation() != null) {
                        Log.i("setUpPhoneStateListener","not null");
                        station.setLatitude(getGsmCellLocation().latitude);

                        station.setLongitude(getGsmCellLocation().longitude);
                        Log.i("station location", ""+getGsmCellLocation().latitude+getGsmCellLocation().longitude);
                    } else {
                        Log.i("setUpPhoneStateListener","null");
                    }
                }
                if (cellLocation instanceof CdmaCellLocation) {
                    Log.i("setUpPhoneStateListener","cdma");
                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
                    if (cdmaCellLocation != null) {
                        station.setLatitude(cdmaCellLocation.getBaseStationLatitude());
                        station.setLongitude(cdmaCellLocation.getBaseStationLongitude());
                    }
                }

                //Draw station marker
                DrawTp2Marker.setStationMarker(MapsActivity.this, mMap, station);
                //add station to list
                stations.add(station);

                //save station to database
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {

                if (signalStrength.isGsm()) {
                    cellSignalLevel = signalStrength.getGsmSignalStrength();
                } else if (signalStrength.getCdmaDbm() > 0) {
                    cellSignalLevel = signalStrength.getCdmaDbm();
                } else {
                    cellSignalLevel = signalStrength.getEvdoDbm();
                }

            }
        };
        telephonyManager.listen(phoneStateListener, LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
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
                    Log.i("to address:", addressTo);
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
                DrawTp2Marker.setTp2Marker(mMap, fromLatLng);
                DrawTp2Marker.setTp2Marker(mMap, toLatLng);

                Log.i("destination:",points.size() + ": " + points.get(0).toString() + ", " + points.get(1));
                AdjustCamera.fixZoom(mMap, points);
            }
        });

    }

    public LatLng getGsmCellLocation() {
        LatLng result = null;

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(MapsActivity.this.TELEPHONY_SERVICE);

        GsmCellLocation gsmCellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

        String networkOperator = telephonyManager.getNetworkOperator();
        String mcc = networkOperator.substring(0, 3);
        String mnc = networkOperator.substring(3);

        int cid = gsmCellLocation.getCid();
        int lac = gsmCellLocation.getLac();
        GetGsmCellLocation getLocation = new GetGsmCellLocation();
        getLocation.setMcc(mcc);
        getLocation.setMnc(mnc);
        getLocation.setCallID(cid);
        getLocation.setCallLac(lac);

        try {
            getLocation.GetOpenCellID();
            Log.i("getLocation", getLocation.getLocation());
            if(!getLocation.isError()){
                result = getLocation.getLocationLatLng();
            }else{
                Log.i("getLocation", "Error");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("Exception: ",   e.toString());
        }
        return result;
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
        DrawTp2Marker.setTp2Marker(mMap, from);

        while (i.hasNext()) {
            Log.i("test:", "has next");
            Tp2Marker p = i.next();
            to = p.getLatLng();

            DrawTp2Marker.setTp2Marker(mMap, to);
            Log.i("test:", "set a marker " + n++);
            Tp2PolyLine.drawLineBetweenTwoMarkers(mMap, from, to);
            from = to;
            points.add(p.getLatLng());
        }
        AdjustCamera.fixZoom(mMap, points);
    }

}
