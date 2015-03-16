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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polymtl.jiajing.tp2_localisationmap.database.DBHelper;
import com.polymtl.jiajing.tp2_localisationmap.model.BaseStation;
import com.polymtl.jiajing.tp2_localisationmap.model.BetweenMarkers;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectMode;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectNetworkInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.Frequency;
import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.model.Power;
import com.polymtl.jiajing.tp2_localisationmap.model.ZoomLevel;
import com.polymtl.jiajing.tp2_localisationmap.util.AdjustCamera;
import com.polymtl.jiajing.tp2_localisationmap.util.AgentApplication;
import com.polymtl.jiajing.tp2_localisationmap.util.DetectConnectivity;
import com.polymtl.jiajing.tp2_localisationmap.ui.DrawItinerary;
import com.polymtl.jiajing.tp2_localisationmap.ui.DrawPathAsyncTask;
import com.polymtl.jiajing.tp2_localisationmap.ui.DrawTp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.util.GetGsmCellLocation;

import static com.polymtl.jiajing.tp2_localisationmap.ui.Tp2PolyLine.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.util.Log;

public class MapsActivity extends FragmentActivity implements ImageCaptureFragment.PictureTakenListener{

    private Context context;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ImageButton btn_fromTo, btn_start_stop, btn_clean;
    private TextView info_itineraries;

    private PopupWindow destinationPopup;
    private View destinationView;
    private String addressTo, addressFrom;
    private LatLng fromLatLng, toLatLng;

    TelephonyManager telephonyManager;
    PhoneStateListener phoneStateListener;

    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiver;
    private WifiManager wifiManager;
    private Location currentLocation;
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
    private long itinerary_id;


    private Tp2Marker currentMarker;
    private List<Tp2Marker> markers;
    private List<BaseStation> stations;

    private boolean isOpenTracking = false;

    private DBHelper dbHelper;

    private boolean alertImgCapIsShowing = false;

    private boolean withHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AgentApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_maps);

        context = getApplicationContext();
        power = new Power(getApplicationContext());

        provider = getIntent().getStringExtra("connectMode"); //get provider defined by user
        zoomLevel.setZoomLevel(getIntent().getIntExtra("zoom", zoomLevel.getZoomLevel()));
        frequency.setTime(getIntent().getIntExtra("frequency", frequency.getFrequency()));
        withHistory = getIntent().getBooleanExtra("withHistory", false);

        info_itineraries = (TextView) findViewById(R.id.info_itineraries);

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

    public String getFormatTime(Long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getDefault());
        Date now = new Date(time);
        return sdf.format(now);
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
            } else {
                DrawItinerary.drawAllItineraries(MapsActivity.this, mMap, dbHelper);
                String text = "Itineraries:\n";
                List<Itinerary> itineraries = dbHelper.getAllItineraries();

                if (itineraries.size() > 0) {
                    for (int i = 0; i < itineraries.size(); i++) {
                        text += "Start:" + getFormatTime(itineraries.get(0).getStartTime())
                                + " Stop:" + getFormatTime(itineraries.get(0).getStopTime())
                                + " Markers:" + itineraries.get(0).getNbr_markers()
                                + " Distance: " + itineraries.get(0).getDt()
                                + " Speed:" + itineraries.get(0).getDt()/(itineraries.get(0).getStopTime()-itineraries.get(0).getStartTime())
                                + " Power consumed" + itineraries.get(0).getAllPowerConsumption() + "\n";
                    }
                }
                info_itineraries.setText(text);
            }

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

            currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation == null) {
                Log.i("setUpMap:", "location is null");
                return;
            }

            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            DrawTp2Marker.setTp2Marker(mMap, latLng);
            AdjustCamera.moveCamera(mMap, latLng, zoomLevel.getZoomLevel());

        }

    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public void setCurrentMarker(Tp2Marker marker) {
        this.currentMarker = marker;
    }

    private final class Tp2LocationListener implements LocationListener {
        //Called when the location has changed.
        @Override
        public void onLocationChanged(Location location) {

            Toast.makeText(MapsActivity.this, "Location changed",
                    Toast.LENGTH_LONG).show();


            setCurrentLocation(location);

            Tp2Marker tp2Marker = new Tp2Marker(location, MapsActivity.this);

            setCurrentMarker(tp2Marker);

            setConnectInfo();

            initialCurrentMarker();
            updateToNewLocation();

            alertOpenCamera();

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

    private void setConnectInfo() {

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
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        //draw current marker
        DrawTp2Marker.setTp2Marker(MapsActivity.this, mMap, currentMarker, markers);

        AdjustCamera.moveCamera(mMap, latLng, zoomLevel.getZoomLevel());

    }

    /**
     * launch after a picture is taken and saved
     * @param picturePath
     */
    @Override
    public void pictureTaken(String picturePath) {
        //markers.get(markers.size()-1).setPicturePath(picturePath);

        if (picturePath != null) {
            currentMarker.setPicturePath(picturePath);
            Log.i("setPicturePath", picturePath);
        }
    }

    private void setUpButtons() {
        btn_fromTo = (ImageButton) findViewById(R.id.btn_destination);
        btn_start_stop = (ImageButton) findViewById(R.id.btn_start_stop);
        btn_clean = (ImageButton) findViewById(R.id.btn_clean);

        if (withHistory) {
            btn_fromTo.setEnabled(false);
            btn_start_stop.setEnabled(false);
            btn_clean.setEnabled(false);
        } else {
            info_itineraries.setEnabled(false);
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
                    thisItinerary.setStartPower(markers.get(0).getNiv_batt());

                    thisItinerary.setStopTime(markers.get(markers.size()-1).getIm());
                    thisItinerary.setStopPower(markers.get(markers.size()-1).getNiv_batt());

                    thisItinerary.setDt(markers.get(markers.size()-1).getLocation().distanceTo(markers.get(0).getLocation()));

                    thisItinerary.setNbr_markers(markers.size());

                    //save thisItinerary, stations and markers in DB
                    itinerary_id = dbHelper.createItinerary(thisItinerary);


                    for (Tp2Marker marker: markers) {
                        dbHelper.createMarker(marker, itinerary_id);
                    }


                    for (BaseStation station: stations) {
                        dbHelper.createStation(station, itinerary_id);
                    }

                    //Show all markers in the map and information about this itinerary
                    DrawItinerary.drawCurrentItinerary(MapsActivity.this, mMap, thisItinerary, dbHelper);

                    //Show all base stations
                    //TODO

                } else { //start tracking
                    //clear the map
                    mMap.clear();
                    if(currentLocation == null) { //if not no location, add the default marker
                        Toast.makeText(MapsActivity.this, "Cannot get location, please make sure there is location serveice.",
                                Toast.LENGTH_LONG).show();

                        mMap.addMarker(new MarkerOptions().position(new LatLng(45.508536, -73.597929)).title("Marker"));
                        // Move the camera instantly to Montreal.
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.508536, -73.597929), 15));
                        return;
                    }

                    markers = new ArrayList<>();
                    stations = new ArrayList<>();

                    thisItinerary = new Itinerary();//initial this itinerary

                    //create the first marker
                    currentMarker = new Tp2Marker(currentLocation, context);

                    //set id of the marker, the id from 0
                    currentMarker.setId(markers.size());

                    markers.add(currentMarker);


                    //draw the first current marker
                    DrawTp2Marker.setTp2Marker(mMap, new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                    //request listening location changed
                    //minDistance is 10 metres
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
                DrawTp2Marker.setTp2Marker(mMap, fromLatLng);
                DrawTp2Marker.setTp2Marker(mMap, toLatLng);

                AdjustCamera.fixZoom(mMap, points);
            }
        });

    }

    public LatLng getGsmCellLocation() {
        LatLng result = null;

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(MapsActivity.this.TELEPHONY_SERVICE);

        GsmCellLocation gsmCellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

        String networkOperator = telephonyManager.getNetworkOperator();
        Log.i("networkOperator",networkOperator);
        String mcc = networkOperator.substring(0, 3);
        Log.i("getGsmCellLocation mcc",mcc);
        String mnc = networkOperator.substring(3);
        Log.i("getGsmCellLocation mnc",mnc);

        int cid = gsmCellLocation.getCid();
        Log.i("getGsmCellLocation cid", "" +cid);
        int lac = gsmCellLocation.getLac();
        Log.i("getGsmCellLocation lac", "" +lac);
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

}
