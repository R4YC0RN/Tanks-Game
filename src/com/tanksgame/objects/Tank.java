package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Tank {

    public static final double tankSize = 35;

    public double currentTankPosX;
    public double currentTankPosY;
    public double speed;
    public int numberOfBullets;
    private String orient;
    private Image tank1UpImg = new Image("assets/images/tank1/tank1Up.png");
    private Image tank1DownImg = new Image("assets/images/tank1/tank1Down.png");
    private Image tank1LeftImg = new Image("assets/images/tank1/tank1Left.png");
    private Image tank1RightImg = new Image("assets/images/tank1/tank1Right.png");
    private Image tank1CurrentOrient;

    public Bullet bullet;
    public Sprite sprite;

    public ArrayList<Bullet> bullets = new ArrayList<>();

    public Tank() {
        orient = "UP";
        currentTankPosX = GameMapView.tankStartPos[0];
        currentTankPosY = GameMapView.tankStartPos[1];
        sprite = new Sprite(GameMapView.tankStartPos[0], GameMapView.tankStartPos[1], tankSize);
        sprite.setImage(tank1UpImg);
        speed = 0.06;
        tank1CurrentOrient = tank1UpImg;
        numberOfBullets = 0;
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
            orient = "UP";
            sprite.setImage(tank1UpImg);
            sprite.render(gc);
        }

        if (input.contains("DOWN") && !input.contains("LEFT") && !input.contains("RIGHT") && !input.contains("UP")) {
            currentTankPosY += speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1DownImg;
            orient = "DOWN";
            sprite.setImage(tank1DownImg);
            sprite.render(gc);
        }

        if (input.contains("LEFT") && !input.contains("RIGHT")) {
            currentTankPosX -= speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1LeftImg;
            orient = "LEFT";
            sprite.setImage(tank1LeftImg);
            sprite.render(gc);
        }

        if (input.contains("RIGHT") && !input.contains("LEFT")) {
            currentTankPosX += speed;
            sprite.setPosition(currentTankPosX, currentTankPosY);
            tank1CurrentOrient = tank1RightImg;
            orient = "RIGHT";
            sprite.setImage(tank1RightImg);
            sprite.render(gc);
        }

    }

    public void addBullet(){
        if("UP".equals(orient)){
            bullet = new Bullet(currentTankPosX + Bullet.bulletWidth / tankSize, currentTankPosY, orient);
        }
        if("DOWN".equals(orient)){
            bullet = new Bullet(currentTankPosX + Bullet.bulletWidth / tankSize, currentTankPosY + tankSize / GameMapView.tileSize, orient);
        }
        if("LEFT".equals(orient)){
            bullet = new Bullet(currentTankPosX, currentTankPosY + Bullet.bulletWidth / tankSize, orient);
        }
        if("RIGHT".equals(orient)){
            bullet = new Bullet(currentTankPosX + tankSize / GameMapView.tileSize , currentTankPosY + Bullet.bulletWidth / tankSize, orient);
        }
        bullets.add(bullet);
        //System.out.println("X: " + bullet.sprite.getX() + " Y: " + bullet.sprite.getY());
    }

    public void shoot(){
        for (Bullet value : bullets) {
            if ("UP".equals(value.orient)) {
                value.bulletPosY -= value.speed;
            }
            if ("DOWN".equals(value.orient)) {
                value.bulletPosY += value.speed;
            }
            if ("LEFT".equals(value.orient)) {
                value.bulletPosX -= value.speed;
            }
            if ("RIGHT".equals(value.orient)) {
                value.bulletPosX += value.speed;
            }
            value.sprite.setPosition(value.bulletPosX, value.bulletPosY);
        }
    }

    public void render(GraphicsContext gc) {
        sprite.render(gc, tank1CurrentOrient, currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public Rectangle2D getBoundary() {
        return sprite.getBoundary(currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public void resetTankOrient(){
        orient = "UP";
        sprite.setImage(tank1UpImg);
    }

}
