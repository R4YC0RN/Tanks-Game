package com.tanksgame.objects;

import com.tanksgame.maps.GameMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Tank extends Rectangle{
    public static final int[] tankStartPos = {5, 14};
    public boolean up, left, right, down;
    private Image tank1UpImg = new Image("Assets/Images/Tank1/tank1Up.png");
    private Image tank1DownImg = new Image("Assets/Images/Tank1/tank1Down.png");
    private Image tank1LeftImg = new Image("Assets/Images/Tank1/tank1Left.png");
    private Image tank1RightImg = new Image("Assets/Images/Tank1/tank1Right.png");
    public Tank(){
        setWidth(GameMap.tileSize);
        setHeight(GameMap.tileSize);
        up = true;
        down = left = right = false;
        //setFill(Color.GREEN);
        setFill(new ImagePattern(tank1UpImg));
    }
}
