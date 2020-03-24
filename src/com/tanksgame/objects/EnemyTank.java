package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class EnemyTank {
    //public static final int[] tankStartPos = {6, 0};
    ArrayList<EnemyTank> enemyTanks;

    public static final double tankSize = 35;
    public double currentTankPosX;
    public double currentTankPosY;
    public double speed;

    private Image tank1UpImg = new Image("assets/images/enemytank1/tankEnemy1Up.png");
    private Image tank1DownImg = new Image("assets/images/enemytank1/tankEnemy1Down.png");
    private Image tank1LeftImg = new Image("assets/images/enemytank1/tankEnemy1Left.png");
    private Image tank1RightImg = new Image("assets/images/enemytank1/tankEnemy1Right.png");
    private Image tank1CurrentOrient;
    public Sprite sprite;

    public EnemyTank(){
        this.currentTankPosX = 0;
        this.currentTankPosY = 0;
        sprite = new Sprite();
        sprite.setImage(tank1UpImg);
        speed = 0.06;
        tank1CurrentOrient = tank1UpImg;
    }

    public EnemyTank(double currentTankPosX, double currentTankPosY){
        this.currentTankPosX = currentTankPosX;
        this.currentTankPosY = currentTankPosY;
        sprite = new Sprite();
        //sprite = new Sprite(currentTankPosX, currentTankPosY, tankSize);
        sprite.setImage(tank1DownImg);
        speed = 0.06;
        tank1CurrentOrient = tank1DownImg;
    }

    public void render(GraphicsContext gc) {
        sprite.render(gc, tank1CurrentOrient, currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public Rectangle2D getBoundary() {
        return sprite.getBoundary(currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public Image getTank1DownImg(){
        return tank1DownImg;
    }
}
