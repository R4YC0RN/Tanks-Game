package com.tanksgame.objects;

import com.tanksgame.maps.GameMap;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Tower extends Rectangle {
    private Image towerImg = new Image("Assets/Images/tower.png");
    public static final int[] towerPos = {7, 14};
    public Tower (){
        setWidth(GameMap.tileSize);
        setHeight(GameMap.tileSize);
        setFill(new ImagePattern(towerImg));
        //setFill(Color.BLUE);

    }

    public Image getTowerImg(){
        return towerImg;
    }
}
