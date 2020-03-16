package com.tanksgame.objects;

import com.tanksgame.maps.GameMap;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle {
    //public static final int size = 30;
    private Image brickImg = new Image("Assets/Images/brick.jpg     ");
    private int numOfBricks;

    //x & y max - 23;

    public static final int[][] brickPos =
            {
                    {1, 0}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {3, 0}, {3, 1}, {3,2},{3,3},{3,4},
                    {5,2}, {6,2}, {8,2}, {9,2}, {11, 0}, {11, 1}, {11, 2}, {11, 3}, {11, 4},
                    {13, 0}, {13, 1}, {13,2},{13,3},{13,4},{6,5},{8,5},{0,7},{2,7},{3,7},{4,7},
                    {6,7},{6,8},{6,9},{6,10},{7,8},{8,7},{8,8},{8,9},{8,10},{10,7},{11,7},{12,7},{14,7},
                    {1, 10}, {1, 11}, {1, 12}, {1, 13}, {3, 10}, {3, 11}, {3,12},{3,13},
                    {11, 10}, {11, 11}, {11, 12}, {11, 13}, {13, 10}, {13, 11}, {13,12},{13,13},
                    {6, 14}, {6, 13}, {7, 13}, {8, 13}, {8, 14}
            };

    public int getNumOfBricks() {
        numOfBricks = brickPos.length;
        return numOfBricks;
    }

    public Brick() {
        setWidth(GameMap.tileSize);
        setHeight(GameMap.tileSize);
        //setFill(Color.ORANGE);
        //setStroke(Color.BLACK);
        setFill(new ImagePattern(brickImg));
    }

    public Image getBrickImg(){
        return brickImg;
    }

}
