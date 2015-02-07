package com.memorygame.example.zoe.tp1_memorygame;

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
