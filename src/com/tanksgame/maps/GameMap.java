package com.tanksgame.maps;

public class GameMap {
    private int width, height;
    //private ArrayList<Sprite> elements;
    private MapTexture texture;
    private MapValues map;

    public GameMap(int width, int height){
        this.width = width;
        this.height = height;
    }

    public void init(){
        texture = new MapTexture();
    }

    private void createMap(MapValues map){
        this.map = map;


    }
}
