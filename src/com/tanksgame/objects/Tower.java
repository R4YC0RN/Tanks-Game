package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.scene.image.Image;

public class Tower {
    private Image towerImg = new Image("assets/images/tower.png");
    public static final int[] towerPos = {7, 14};
    public Sprite sprite;

    public Tower() {
        sprite = new Sprite( towerPos[0], towerPos[1], GameMapView.tileSize);
        sprite.setImage(towerImg);
    }

    public Image getTowerImg() {
        return towerImg;
    }
}
