package com.tanksgame.hud;

import com.tanksgame.maps.GameMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HudLeft extends Rectangle {

    public HudLeft(){
        setWidth(GameMap.hudSizeHalf);
        setHeight(GameMap.tileSize * GameMap.height);
        setStroke(Color.BROWN);
        setFill(Color.BLACK);
    }

}
