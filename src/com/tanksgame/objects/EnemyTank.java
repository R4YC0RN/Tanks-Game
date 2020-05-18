package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.threads.EnemyAddBulletThread;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;

public class EnemyTank{
    //public static final int[] tankStartPos = {6, 0};
    ArrayList<EnemyTank> enemyTanks;

    public static final double tankSize = 35;
    public double currentTankPosX;
    public double currentTankPosY;
    public double speed;
    public double posBeforeX;
    public double posBeforeY;

    private Image tank1UpImg = new Image("assets/images/enemytank1/tankEnemy1Up.png");
    private Image tank1DownImg = new Image("assets/images/enemytank1/tankEnemy1Down.png");
    private Image tank1LeftImg = new Image("assets/images/enemytank1/tankEnemy1Left.png");
    private Image tank1RightImg = new Image("assets/images/enemytank1/tankEnemy1Right.png");
    private Image tank1CurrentOrient;
    public Sprite sprite;

    public boolean alive;
    public boolean enemyCollides;
    public boolean borderCollision;

    public ArrayList<Bullet> bullets;
    public int timeToStartShoot;
    public int shootKD;
    public EnemyAddBulletThread shootThread;
    public int tankIndex;
    public boolean threadCreated = false;
    public boolean shootPause = false;

    public int id;

    public String orient;
    private List<String> orients = Arrays.asList("UP", "DOWN", "LEFT", "RIGHT");
    Random rand = new Random();

    public EnemyTank(){
        this.currentTankPosX = 0;
        this.currentTankPosY = 0;
        alive = true;
        enemyCollides = false;
        borderCollision = false;
        sprite = new Sprite();
        sprite.setImage(tank1UpImg);
        speed = 0.04;
        tank1CurrentOrient = tank1UpImg;
        orient = "DOWN";
        timeToStartShoot = new Random(5000).nextInt();
        shootKD = 2000;
        bullets = new ArrayList<>();
    }

    public EnemyTank(double currentTankPosX, double currentTankPosY){
        this.currentTankPosX = currentTankPosX;
        this.currentTankPosY = currentTankPosY;
        posBeforeX = currentTankPosX;
        posBeforeY = currentTankPosY;
        sprite = new Sprite();
        alive = true;
        enemyCollides = false;
        borderCollision = false;
        sprite = new Sprite(currentTankPosX, currentTankPosY, tankSize);
        sprite.setImage(tank1DownImg);
        speed = 0.04;
        tank1CurrentOrient = tank1DownImg;
        orient = "DOWN";
        timeToStartShoot = new Random(5000).nextInt();
        shootKD = 2000;
        bullets = new ArrayList<>();
    }

    public void move(){
        if(alive || !enemyCollides){
            if("DOWN".equals(orient)){
                currentTankPosY += speed;
                sprite.setPosition(currentTankPosX, currentTankPosY);
                sprite.setImage(tank1DownImg);
            }
            if("UP".equals(orient)){
                currentTankPosY -= speed;
                sprite.setPosition(currentTankPosX, currentTankPosY);
                sprite.setImage(tank1UpImg);
            }
            if("LEFT".equals(orient)){
                currentTankPosX -= speed;
                sprite.setPosition(currentTankPosX, currentTankPosY);
                sprite.setImage(tank1LeftImg);
            }
            if("RIGHT".equals(orient)){
                currentTankPosX += speed;
                sprite.setPosition(currentTankPosX, currentTankPosY);
                sprite.setImage(tank1RightImg);
            }
        }
    }

    public void createAddBulletThread(){
        shootThread = new EnemyAddBulletThread(this);
        shootThread.start();
    }

    public void shoot(){
        for (Bullet bullet : bullets) {
            if ("UP".equals(bullet.orient)) {
                bullet.bulletPosY -= bullet.speed;
            }
            if ("DOWN".equals(bullet.orient)) {
                bullet.bulletPosY += bullet.speed;
            }
            if ("LEFT".equals(bullet.orient)) {
                bullet.bulletPosX -= bullet.speed;
            }
            if ("RIGHT".equals(bullet.orient)) {
                bullet.bulletPosX += bullet.speed;
            }
            bullet.sprite.setPosition(bullet.bulletPosX, bullet.bulletPosY);
        }
    }

    public void render(GraphicsContext gc) {
        sprite.render(gc, tank1CurrentOrient, currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public Rectangle2D getBoundary() {
        return sprite.getBoundary(currentTankPosX * GameMapView.tileSize, currentTankPosY * GameMapView.tileSize, tankSize);
    }

    public void changeOrient(){
        boolean flag = true;
        while(flag){
            int randomIndex = rand.nextInt(orients.size());
            if(!orient.equals(orients.get(randomIndex))){
                orient = orients.get(randomIndex);
                flag = false;
            }
        }
    }

    public void changeOrient(String orient){
        this.orient = orient;
        switch (orient){
            case "UP":
                sprite.setImage(tank1UpImg);
                break;
            case "DOWN":
                sprite.setImage(tank1DownImg);
                break;
            case "LEFT":
                sprite.setImage(tank1LeftImg);
                break;
            case "RIGHT":
                sprite.setImage(tank1RightImg);
                break;
        }
    }

    public Image getTank1DownImg(){
        return tank1DownImg;
    }

    public Image getTank1UpImg(){
        return tank1UpImg;
    }

    public void resetOrient(){
        orient = "DOWN";
    }
}
