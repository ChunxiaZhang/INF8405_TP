package com.polymtl.jiajing.tp2_localisationmap.util;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoe on 15-02-02.
 */
public class AgentApplication extends Application {
    private List<Activity> activityList = new ArrayList<>();
    private static AgentApplication instance;

    //Make sure there is only on instance of this class
    public static AgentApplication getInstance() {
        if(instance == null) {
            instance = new AgentApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //Turn all the activities let exit application perfectly
        for(Activity activity : activityList) {
            activity.finish();
        }
        onDestroy();
        System.exit(0);
    }

    public void onDestroy() {
        //Do some thing before exit
    }
}
