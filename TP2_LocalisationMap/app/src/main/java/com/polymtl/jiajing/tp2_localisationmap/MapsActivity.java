package com.polymtl.jiajing.tp2_localisationmap;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polymtl.jiajing.tp2_localisationmap.model.Frequency;
import com.polymtl.jiajing.tp2_localisationmap.model.Power;
import com.polymtl.jiajing.tp2_localisationmap.model.ZoomLevel;

import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ImageButton btn_destination, btn_start_stop, btn_power;
    private PopupWindow destinationPopup;
    private View destinationView;

    private LocationManager locationManager;
    private Location location;
    private String provider = LocationManager.GPS_PROVIDER;
    private MarkerOptions markerOpt;

    private Power power;
    private ZoomLevel zoomLevel = new ZoomLevel();
    private Frequency frequency = new Frequency();

    final Tp2LocationListener listener = new Tp2LocationListener();

    private boolean isOpenTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        power = new Power(getApplicationContext());
        zoomLevel.setZoomLevel(getIntent().getIntExtra("zoom", zoomLevel.getZoomLevel()));
        frequency.setTime(getIntent().getIntExtra("frequency", frequency.getTime()));

        isOpenTracking = false;

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

        //Create LoacationManager and get Provider
        initProvider();

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView))
                    .getMap();

            // Set current location
            mMap.setMyLocationEnabled(true);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        //Initial Camera to the current location
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), zoomLevel.getZoomLevel()));

        // Move the camera instantly to Montreal.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.527593, -73.597179), 15));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                .zoom(zoomLevel.getZoomLevel())                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void initProvider() {
        //Create LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //List all providers
        //List<String> providers = locationManager.getAllProviders();
        //Criteria criteria = new Criteria();
        //bestProvider = locationManager.getBestProvider(criteria,false);
        location = locationManager.getLastKnownLocation(provider);
    }

    private void updateToNewLocation(Location location) {

        markerOpt = new MarkerOptions();
        double dLong = location.getLongitude();
        double dLat = location.getLatitude();

        markerOpt.position(new LatLng(dLat,dLong));
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//set to be center of the picture
        markerOpt.icon(BitmapDescriptorFactory.fromResource((R.drawable.marker)));
        mMap.addMarker(markerOpt);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(dLat, -dLong))      // Sets the center of the map to Mountain View
                .zoom(zoomLevel.getZoomLevel())                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setUpButtons() {

        btn_destination = (ImageButton) findViewById(R.id.btn_destination);
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

                if(isOpenTracking) {
                    locationManager.removeUpdates(listener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.start));
                    isOpenTracking = false;
                }
                else {
                    locationManager.requestLocationUpdates(provider, frequency.getTime(), 8, listener);
                    btn_start_stop.setBackground(getResources().getDrawable(R.drawable.stop));
                    isOpenTracking = true;
                }
            }
        });

    }


    private void setUpPopupDestination() {

        /*LayoutInflater inflater = LayoutInflater.from(this);
        destinationView = inflater.inflate(R.layout.popup_destination, null);
        destinationPopup = new PopupWindow(destinationView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final EditText destinationText = (EditText) destinationView.findViewById(R.id.destinationEdit);
        final ImageButton btn_search = (ImageButton) destinationView.findViewById(R.id.btn_searchDestination);

        destinationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btn_search.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(destinationText.getText().length() > 0) {
                    btn_search.setEnabled(true);
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationPopup.dismiss();
                destinationPopup.setFocusable(false);
            }
        });*/

    }


    private final class Tp2LocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            updateToNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

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

}
