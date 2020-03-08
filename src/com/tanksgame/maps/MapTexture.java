package com.tanksgame.maps;

import java.util.ArrayList;

public class MapTexture {
    private ArrayList<MapValues> maps;

    private static final int[][] tankPos =
            {

            };

    private static final int[][] brickPos =
            {

            };

    private static final int[][] stonePos =
            {

            };

    private static final int[] towerPos =
            {

            };

    private static final int[] playerPos =
            {

            };

    public MapTexture(){
        maps = new ArrayList<MapValues>();
        initMapData();
    }

    private void initMapData(){
        MapValues map = new MapValues(tankPos, brickPos, stonePos, towerPos, playerPos);
        maps.add(map);
    }



}
