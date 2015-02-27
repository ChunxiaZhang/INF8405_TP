package com.polymtl.jiajing.tp2_localisationmap;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.AsyncTask;

import android.location.Address;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.telephony.TelephonyManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import com.google.android.gms.maps.model.PolylineOptions;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectGPSInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectMode;
import com.polymtl.jiajing.tp2_localisationmap.model.ConnectNetworkInfo;
import com.polymtl.jiajing.tp2_localisationmap.model.Frequency;
import com.polymtl.jiajing.tp2_localisationmap.model.Itinerary;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerGPS;
import com.polymtl.jiajing.tp2_localisationmap.model.MarkerNetwork;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;
import com.polymtl.jiajing.tp2_localisationmap.model.Power;
import com.polymtl.jiajing.tp2_localisationmap.model.ZoomLevel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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
    private MarkerOptions markerOpt;
    private Polyline lineDestination, lineItinerary;

    //private ConnectMode connectMode;
    private Power power;
    private ZoomLevel zoomLevel = new ZoomLevel();
    private Frequency frequency = new Frequency();
    private ConnectMode connectMode = new ConnectMode();

    final Tp2LocationListener listener = new Tp2LocationListener();

    private Tp2InfoWindowAdapter infoWindow = new Tp2InfoWindowAdapter();

   // private Itinerary itinerary;
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

      // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        Log.i("setUpMap: " , "Zoom out to zoom level 10, animating with a duration of 2 seconds.");
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                .zoom(zoomLevel.getZoomLevel())                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.i("setUpMap: " , "newCameraPosition is done");


    }



    private void updateToNewLocation(Location location) {

        Log.i("updatTonewLocation: " , "start");
        markerOpt = new MarkerOptions();
        double dLong = location.getLongitude();
        double dLat = location.getLatitude();

       markerOpt.position(new LatLng(dLat,dLong));
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//set to be center of the picture
        markerOpt.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
        mMap.addMarker(markerOpt);
        mMap.setInfoWindowAdapter(infoWindow);
        Log.i("updatTonewLocation: " , "addMarker");

        //mMap.addMarker(new MarkerOptions().position(new LatLng(dLat,dLong)).anchor(0.5f, 0.5f).title("Marker"));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        Log.i("setUpMap: " , "Zoom out to zoom level 10, animating with a duration of 2 seconds.");
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(dLat, dLong))      // Sets the center of the map to Mountain View
                .zoom(zoomLevel.getZoomLevel())                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                    locationManager.removeUpdates(listener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
                    isOpenTracking = false;

                    //Show battery level
                    Toast.makeText(MapsActivity.this, "Battery level: " + power.getPowerLever(), Toast.LENGTH_LONG);

                    //Show all markers in the map and information about this itinerary
                    /////
                    //The test itinerary
                    setTestItinerary();
                    showTestItinerary(testItinerary);
                }
                else { //start tracking
                    //Show battery level
                    Toast.makeText(context, "Battery level: " + power.getPowerLever(), Toast.LENGTH_LONG);

                    //itinerary = new Itinerary(); //initial itinerary

                    //add the first marker
                   // addTp2Marker();


                    //request listening location changed
                    locationManager.requestLocationUpdates(provider, frequency.getFrequency(), 8, listener);
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


        @Override
        public void onLocationChanged(Location location) {

            updateToNewLocation(location);

            //
            // addTp2Marker(); //add Tp2Marker

            //Need to add marker to database ?????
            ///
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                case LocationProvider.OUT_OF_SERVICE:
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

            location = locationManager.getLastKnownLocation(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

            updateToNewLocation(null);
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

                List<LatLng> points = new ArrayList<LatLng>();

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

                new connectAsyncTask(makeURL(fromLatLng, toLatLng)).execute();

            }
        });

    }


    //Show all the points in the map
    private void fixZoom(List<LatLng> points) {
        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        for (LatLng item : points) {
            bc.include(item);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
    }

    private void drawLineBetweenTwoMarkers(LatLng from, LatLng to) {

        mMap.addPolyline(new PolylineOptions()
            .add(from, to)
            .width(5)
            .color(Color.RED));
    }

    /**
     *
     * @param result
     * @param from
     * @param to
     *
     * Draw path from start to end.
     */
    private void drawPath(String result, LatLng from, LatLng to) {

        List<LatLng> points = new ArrayList<LatLng>();
        points.add(from);
        points.add(to);

        if (lineDestination != null) {
            mMap.clear();
        }

        mMap.addMarker(new MarkerOptions().position(from).snippet("from snippet").title("Marker"));
        mMap.addMarker(new MarkerOptions().position(to).snippet("from snippet").title("Marker"));

        //InfoWindowAdapter show information
        mMap.setInfoWindowAdapter(infoWindow);

        fixZoom(points);

        try {
            // Tranform the string into a json object
            final JSONObject json = new JSONObject(result);

            JSONArray routeArray = json.getJSONArray("routes");

            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng point = list.get(z);

                options.add(point);
            }
            lineDestination = mMap.addPolyline(options);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String makeURL(LatLng from, LatLng to) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin="); //from
        urlString.append(Double.toString(from.latitude) + "," + Double.toString(from.longitude));
        urlString.append("&destination="); //to
        urlString.append(Double.toString(to.latitude) + "," + Double.toString(to.longitude));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }


    private List<LatLng> decodePoly(String encoded) {

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
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    /**
     * Created by Zoe on 15-02-25.
     */
    private class connectAsyncTask extends AsyncTask<Void, Void, String> {

        String url;

        public connectAsyncTask( String urlPass) {

            Log.i("connectAsyncTask", "creating");
            url = urlPass;
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

            if (result != null) {
                Log.i("connectAsyncTask", "drawPath");
                drawPath(result,fromLatLng, toLatLng);
            }
        }

    }

    private class Tp2InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


        @Override
        public View getInfoWindow (com.google.android.gms.maps.model.Marker marker){
            return null;
        }

        @Override
        public View getInfoContents (com.google.android.gms.maps.model.Marker marker){
            View v = getLayoutInflater().inflate(R.layout.marker, null);

            TextView info = (TextView) v.findViewById(R.id.info);

            info.setText(marker.getPosition().toString()); //Show maker information

            return v;
        }
    }


    /**
     * Add information for a test itinerary
     * Only used by testing
     */
    private void setTestItinerary() {
        testItinerary = new Itinerary();

        List<Tp2Marker> markers = new ArrayList<>();

        //add some Location objects
        List<Location> locations = new ArrayList<>();
        Location l1 = new Location(provider);
        l1.setLatitude(45.458144);
        l1.setLongitude(-73.638387);
        l1.setTime(1424924854700l);
        locations.add(l1);

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
            if (p == null) {
                Log.i("test:", "Marker is null");
                return;
            }
            markers.add(p);
            //testItinerary.increaseMarkers(p);
            testItinerary.increaseNbr_sb();
        }
        testItinerary.setTp2Markers(markers);

    }

    private void showTestItinerary(Itinerary testItinerary) {

        List<Tp2Marker> ps = testItinerary.getTp2Markers();
        Log.i("test:", "Tp2Marker are " + ps.size());
        List<LatLng> points = new ArrayList<>();
        Iterator<Tp2Marker> i = ps.iterator();
        int n = 0;
        LatLng from, to;
        from = i.next().getLatLng();
        markerOpt = new MarkerOptions();
        markerOpt.position(from);
        Log.i("test:", "marker LatLng " + from.toString());
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
        mMap.addMarker(markerOpt);
        mMap.setInfoWindowAdapter(infoWindow);

        while (i.hasNext()) {
            Log.i("test:", "has next");

            Tp2Marker p = i.next();

            to = p.getLatLng();



            markerOpt.position(to);
            Log.i("test:", "marker LatLng " + p.getLatLng().toString());
            markerOpt.draggable(false);
            markerOpt.visible(true);
            markerOpt.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
            mMap.addMarker(markerOpt);
            mMap.setInfoWindowAdapter(infoWindow);
            Log.i("test:", "set a marker " + n++);

            from = to;
           // drawLineBetweenTwoMarkers(from, to);
            points.add(p.getLatLng());
        }
        drawLineBetweenTwoMarkers(points.get(0), points.get(points.size()-1));
        fixZoom(points);

    }
}
