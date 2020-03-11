package com.tanksgame.objects;

import com.tanksgame.maps.GameMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EmptyTile extends Rectangle {


    public EmptyTile(){
        setWidth(GameMap.tileSize);
        setHeight(GameMap.tileSize);

        setFill(Color.BLACK);
        setStroke(Color.BLACK);
    }

}
