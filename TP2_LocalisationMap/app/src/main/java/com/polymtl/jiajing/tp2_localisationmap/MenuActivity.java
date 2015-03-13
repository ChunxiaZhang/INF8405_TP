package com.polymtl.jiajing.tp2_localisationmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.polymtl.jiajing.tp2_localisationmap.model.ConnectMode;
import com.polymtl.jiajing.tp2_localisationmap.model.Frequency;
import com.polymtl.jiajing.tp2_localisationmap.model.ZoomLevel;
import com.polymtl.jiajing.tp2_localisationmap.util.DetectConnectivity;


public class MenuActivity extends ActionBarActivity {

    private TextView textViewMode, textViewZoom, textViewFrequency;
    private RadioGroup radioGroupMode;
    private RadioButton radioButtonGPS, radioButtonNetwork;
    private Button buttonOpenMap, buttonItineraryHistory, buttonExit;
    private SeekBar seekBarZoom, seekBarFrequency;

    private ConnectMode mode = new ConnectMode();
    private ZoomLevel zoomLevel = new ZoomLevel();
    private Frequency frequency = new Frequency();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        detectEnvirement();
        setUpWidgets();


    }

    public void setUpWidgets() {

        textViewZoom = (TextView) findViewById(R.id.textViewZoom);
        textViewFrequency =  (TextView) findViewById(R.id.textViewFrequency);
        radioGroupMode = (RadioGroup) findViewById(R.id.radioGroupMode);
        radioButtonGPS = (RadioButton) findViewById(R.id.radioBtnGPS);
        radioButtonNetwork = (RadioButton) findViewById(R.id.radioBtnNetwork);
        buttonOpenMap = (Button) findViewById(R.id.btnOpenMap);
        buttonItineraryHistory = (Button) findViewById(R.id.btnItineraryHistory);
        buttonExit = (Button) findViewById(R.id.btnExit);
        seekBarZoom = (SeekBar) findViewById(R.id.seekBarZoom);
        seekBarFrequency = (SeekBar) findViewById(R.id.seekBarFrequency);

        textViewZoom.setText("Zoom: " + seekBarZoom.getProgress());
        textViewFrequency.setText("Frequency: " + seekBarFrequency.getProgress() + "s");


        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (radioGroupMode.getCheckedRadioButtonId()) {
                    case  R.id.radioBtnGPS:
                        mode.setProvider(LocationManager.GPS_PROVIDER);

                        break;
                    case R.id.radioBtnNetwork:
                        mode.setProvider(LocationManager.NETWORK_PROVIDER);

                        break;
                    default:
                        break;
                }
            }
        });

        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency.setTime(seekBarFrequency.getProgress());
                textViewFrequency.setText("Frequency:" + seekBarFrequency.getProgress() + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zoomLevel.setZoomLevel(seekBarZoom.getProgress());
                textViewZoom.setText("Zoom: " + seekBarZoom.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonOpenMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //if GPS or network closed, open it
                if ((mode.getProvider() == LocationManager.GPS_PROVIDER &&
                        !DetectConnectivity.isGpsProviderAccessed(MenuActivity.this)) ||
                        (mode.getProvider() == LocationManager.NETWORK_PROVIDER &&
                        !DetectConnectivity.isNetworkProviderAccessed(MenuActivity.this))) {

                    new AlertDialog.Builder(MenuActivity.this)
                            .setMessage("Google Maps needs access to your location. Please turn on location access.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Location services disabled")
                            .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                                }
                            }).setNegativeButton(R.string.ignore, null).show();

                } else {
                    openMapsActivity();
                }

            }
        });

    }

    private void openMapsActivity() {
        Intent i = new Intent(MenuActivity.this, MapsActivity.class);
        i.putExtra("connectMode", mode.getProvider());
        i.putExtra("frequency", frequency.getFrequency());
        i.putExtra("zoom", zoomLevel.getZoomLevel());
        startActivity(i); //Send two players' name to GameActivity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:

                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        Log.i("MenuActivity:" , "is stopped");

    }
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        // Activity being restarted from stopped state
    }

////
    public void detectEnvirement() {

       /* if (DetectConnectivity.isNetworkAvailable(getApplicationContext())) {
            Log.i("detectConnectivity"," network is available");
        } else {
            Log.i("detectConnectivity"," network is not available");
        }*/

       /* if (DetectConnectivity.isGpsEnabled(getApplicationContext())) {
            Log.i("detectConnectivity"," Gps is enabled");
        } else {
            Log.i("detectConnectivity"," Gps is not enabled");
        }*/

       /* if (DetectConnectivity.isWifiEnabled(getApplicationContext())) {
            Log.i("detectConnectivity"," Wifi is enabled");
        } else {
            Log.i("detectConnectivity"," Wifi is not enabled");
        }*/

        if (DetectConnectivity.isMobileNetworkAvailable(getApplicationContext())) {
            Log.i("detectConnectivity"," Mobile Network is Available");
        } else {
            Log.i("detectConnectivity"," Mobile Network is not Available");
        }

        if (DetectConnectivity.isConnected(getApplicationContext())) {
            Log.i("detectConnectivity"," is Connected");
        } else {
            Log.i("detectConnectivity"," is not Connected");
        }

        if (DetectConnectivity.isConnectedMobile(getApplicationContext())) {
            Log.i("detectConnectivity"," Mobile is connected");
        } else {
            Log.i("detectConnectivity"," Mobile is not connected");
        }

        if (DetectConnectivity.isConnectedWifi(getApplicationContext())) {
            Log.i("detectConnectivity"," Wifi is connected");
        } else {
            Log.i("detectConnectivity"," Wifi is not connected");
        }

        /*if (DetectConnectivity.isGpsConnected(getApplicationContext())) {
            Log.i("detectConnectivity"," GPS is connected");
        } else {
            Log.i("detectConnectivity"," GPS is not connected");
        }*/

        if (DetectConnectivity.isGpsProviderAccessed(getApplicationContext())) {
            Log.i("detectConnectivity"," GPS provider is opened");
        } else {
            Log.i("detectConnectivity"," GPS provider is not opened");
        }
        if (DetectConnectivity.isGpsProviderAccessed(getApplicationContext())) {
            Log.i("detectConnectivity"," Network provider is opened");
        } else {
            Log.i("detectConnectivity"," Network provider is not opened");
        }

    }


}
