package com.polymtl.jiajing.tp2_localisationmap.model;

/**
 * Created by Zoe on 15-02-21.
 */
public class Frequency {

    private int time; //s

    public Frequency() {

        this.time = 30; //Initial 30s
    }

    public int getFrequency() {
        return  this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
