package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Tank {
    public static final int[] tankStartPos = {5, 14};
    public static final double tankSize = 35;
    public double currentTankPosX;
    public double currentTankPosY;
    public double speed;
    public boolean up, left, right, down;
    private Image tank1UpImg = new Image("assets/images/tank1/tank1Up.png");
    private Image tank1DownImg = new Image("assets/images/tank1/tank1Down.png");
    private Image tank1LeftImg = new Image("assets/images/tank1/tank1Left.png");
    private Image tank1RightImg = new Image("assets/images/tank1/tank1Right.png");
    private Image tank1CurrentOrient;
    public Sprite sprite;

    public Tank() {
        up = true;
        down = left = right = false;
        currentTankPosX = tankStartPos[0];
        currentTankPosY = tankStartPos[1];
        sprite = new Sprite(tankStartPos[0], tankStartPos[1], tankSize);
        sprite.setImage(tank1UpImg);
        speed = 0.06;
        tank1CurrentOrient = tank1UpImg;
    }

    public Image getTank1DownImg() {
        return tank1DownImg;
    }

    public Image getTank1UpImg() {
        return tank1UpImg;
    }

    public Image getTank1LeftImg() {
        return tank1LeftImg;
    }

    public Image getTank1RightImg() {
        return tank1RightImg;
    }

    public void tankMoveSet(ArrayList<String> input, GraphicsContext gc) {
        if (input.contains("UP") && !input.contains("LEFT") && !input.contains("RIGHT") && !input.contains("DOWN")) {
            currentTankPosY -= speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1UpImg;
            sprite.setImage(tank1UpImg);
            render(gc);
        }

        if (input.contains("DOWN") && !input.contains("LEFT") && !input.contains("RIGHT") && !input.contains("UP")) {
            currentTankPosY += speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1DownImg;
            sprite.setImage(tank1DownImg);
            render(gc);
        }

        if (input.contains("LEFT") && !input.contains("RIGHT")) {
            currentTankPosX -= speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1LeftImg;
            sprite.setImage(tank1LeftImg);
            render(gc);
        }

        if (input.contains("RIGHT") && !input.contains("LEFT")) {
            currentTankPosX += speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1RightImg;
            sprite.setImage(tank1RightImg);
            render(gc);
        }
    }

    public void render(GraphicsContext gc) {
        sprite.render(gc, tank1CurrentOrient, currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public Rectangle2D getBoundary() {
        return sprite.getBoundary(currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

}
