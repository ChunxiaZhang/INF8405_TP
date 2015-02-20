package com.polymtl.jiajing.tp2_localisationmap;

import java.sql.Time;

/**
 * Created by Jiajing on 2015/2/20.
 */
public class Marker {
    private enum Direction{
        EAST,SOUTH,WEST,NORTH,SOUTH_EAST,SOUTH_WEST,NORTH_EAST,NORTH_WEST;
    }
    private GeoPoint coord;
    private Time im;
    private Direction Dir_dep;


}
