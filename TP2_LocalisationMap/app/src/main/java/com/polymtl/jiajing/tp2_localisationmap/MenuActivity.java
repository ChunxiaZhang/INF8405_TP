package com.polymtl.jiajing.tp2_localisationmap;

import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class MenuActivity extends ActionBarActivity {

    TextView textViewMode, textViewZoom, textViewFrequency;
    RadioGroup radioGroupMode;
    RadioButton radioButtonGPS, radioButtonNetwork;
    Button buttonOpenMap, buttonItineraryHistory, buttonExit;
    SeekBar seekBarZoom, seekBarFrequency;

    ConnectMode mode = new ConnectMode();
    ZoomLevel zoomLevel = new ZoomLevel();
    Frequency frequency = new Frequency();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
                Intent i = new Intent(MenuActivity.this, MapsActivity.class);
                i.putExtra("connectMode", mode.getProvider());
                i.putExtra("frequency", frequency.getFrequency());
                i.putExtra("zoom", zoomLevel.getZoomLevel());
                startActivity(i); //Send two players' name to GameActivity
            }
        });

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
}
