package com.tanksgame.objects;

import com.tanksgame.maps.GameMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Tank extends Rectangle{
    public static final int[] tankStartPos = {5, 14};
    public double currentTankPosX;
    public double currentTankPosY;
    public double speed;
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
        currentTankPosX = tankStartPos[0];
        currentTankPosY = tankStartPos[1];
        speed = 0.06;
    }

    public Image getTank1DownImg(){
        return tank1DownImg;
    }

    public Image getTank1UpImg(){
        return tank1UpImg;
    }

    public Image getTank1LeftImg(){
        return tank1LeftImg;
    }

    public Image getTank1RightImg(){
        return tank1RightImg;
    }

    public void tankMoveSet(ArrayList<String> input, GraphicsContext gc){
        if(input.contains("UP") && !input.contains("LEFT") && !input.contains("RIGHT") && !input.contains("DOWN")) {
            gc.clearRect(currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
            currentTankPosY -= speed;
            gc.drawImage(tank1UpImg, currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
        }

        if(input.contains("DOWN") && !input.contains("LEFT") && !input.contains("RIGHT") && !input.contains("UP")) {
            gc.clearRect(currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
            currentTankPosY += speed;
            gc.drawImage(tank1DownImg, currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
        }

        if(input.contains("LEFT") && !input.contains("RIGHT")) {
            gc.clearRect(currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
            currentTankPosX -= speed;
            gc.drawImage(tank1LeftImg, currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
        }

        if(input.contains("RIGHT") && !input.contains("LEFT")) {
            gc.clearRect(currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
            currentTankPosX += speed;
            gc.drawImage(tank1RightImg, currentTankPosX * GameMap.tileSize,
                    currentTankPosY * GameMap.tileSize, GameMap.tileSize, GameMap.tileSize);
        }
    }
}
