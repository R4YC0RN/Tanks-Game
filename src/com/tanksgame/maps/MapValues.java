package com.tanksgame.maps;

public class MapValues {
    public int[][] tankPos, brickPos, stonePos;
    public int[] homePos, playerPos;

    public MapValues(int[][] tankPos, int[][] brickPos, int[][] stonePos, int[] towerPos, int[] playerPos){
        this.tankPos = tankPos;
        this.brickPos = brickPos;
        this.stonePos = stonePos;
        this.homePos = homePos;
        this.playerPos = playerPos;
    }
}
