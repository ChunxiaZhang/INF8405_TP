package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by Zoe on 15-02-21.
 */
public class Power {

    private Context context;

    public Power(Context context) {

        this.context = context.getApplicationContext();
    }

    public float getPowerLever() {

        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if(scale > 0 && level >= 0)
            return (level * 100.0f)/scale;

        else
            return 0.0f;
        //return batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)/batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,-1) * 100.0f;
    }

    public float getConsumption(float level1, float level2) {

        return level1 - level2;
    }
}
