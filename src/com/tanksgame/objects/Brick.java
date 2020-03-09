package com.tanksgame.objects;

import com.tanksgame.maps.GameMap;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle {
    //public static final int size = 30;
    private Image brickImg = new Image("Assets/Images/brick.jpg");
    private Image brickImage;
    private int numOfBricks;

    //x & y max - 23;

    public static final int[][] brickPos =
            {
                    {0, 0}, {0,1}, {0,2}, {1,0}, {10, 0}, {23,23}, {14, 9}
            };

    public int getNumOfBricks() {
        numOfBricks = brickPos.length;
        return numOfBricks;
    }

    public Brick(int x, int y) {
        setWidth(GameMap.tileSize);
        setHeight(GameMap.tileSize);
        setFill(Color.ORANGE);
        setStroke(Color.BLACK);
    }

}
