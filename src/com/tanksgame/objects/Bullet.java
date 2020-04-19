package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet {
    public static final double bulletWidth = 9.5;
    public static final double bulletHeight = 15;
    public double bulletPosX;
    public double bulletPosY;
    public double speed;
    public String orient;
    private Image bulletUp = new Image("assets/images/bulletUp.png");
    private Image bulletDown = new Image("assets/images/bulletDown.png");;
    private Image bulletLeft = new Image("assets/images/bulletLeft.png");;
    private Image bulletRight = new Image("assets/images/bulletRight.png");
    private Image currentPos;
    public Sprite sprite;

    public Bullet(double tankPosX, double tankPosY, String orient){
        bulletPosX = tankPosX;
        bulletPosY = tankPosY;
        speed = 0.2;
        this.orient = orient;
        sprite = new Sprite();
        sprite.setPosition(bulletPosX, bulletPosY);
        if(orient == "UP"){
            sprite.setImage(bulletUp);
            sprite.setSize(bulletWidth, bulletHeight);
            currentPos = bulletUp;
        }
        if(orient == "DOWN"){
            sprite.setImage(bulletDown);
            sprite.setSize(bulletWidth, bulletHeight);
            currentPos = bulletDown;
        }
        if(orient == "LEFT"){
            sprite.setImage(bulletLeft);
            sprite.setSize(bulletHeight, bulletWidth);
            currentPos = bulletLeft;
        }
        if(orient == "RIGHT"){
            sprite.setImage(bulletRight);
            sprite.setSize(bulletHeight, bulletWidth);
            currentPos = bulletRight;
        }

    }

    public void render(GraphicsContext gc){
        sprite.render(gc, currentPos, bulletPosX * GameMapView.tileSize, bulletPosY * GameMapView.tileSize, bulletWidth, bulletHeight);
    }

    public Rectangle2D getBoundary() {
        return sprite.getBoundary(bulletPosX * GameMapView.tileSize, bulletPosY * GameMapView.tileSize, bulletWidth, bulletHeight);
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }
}
