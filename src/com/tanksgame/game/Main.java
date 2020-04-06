package com.tanksgame.game;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.System.exit;

public class Main extends Application {
    VBox root = new VBox();
    GameMapView map = new GameMapView();
    Canvas fieldCanvas = new Canvas();
    Canvas leftHudCanvas = new Canvas();
    Canvas rightHudCanvas = new Canvas();

    private GraphicsContext gcMain;
    private GraphicsContext gcLeftHud;
    private GraphicsContext gcRightHud;
    public double posBeforeX = map.getTank().currentTankPosX;
    public double posBeforeY = map.getTank().currentTankPosY;

    public boolean spacePressed;
    public boolean gameOver = false;

    ArrayList<Sprite> bricks;
    ArrayList<Sprite> brokenBricks;
    ArrayList<Bullet> bullets;
    ArrayList<Sprite> enemies;

    public int score = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Tanks");
        showMainMenu(primaryStage);
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    public void showMainMenu(Stage primaryStage) {

        root.setSpacing(10);

        Image startBgImg = new Image("assets/images/startBackground.jpg");
        BackgroundSize bgSize = new BackgroundSize(1280, 720, false, false,
                false, false);
        BackgroundImage backgroundImg = new BackgroundImage(startBgImg, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, bgSize);
        Background background = new Background(backgroundImg);
        root.setBackground(background);

        root.setSpacing(10);
        root.setPadding(new Insets(20, 20, 10, 20));
        root.setAlignment(Pos.TOP_CENTER);

        Button loadBtn = new Button("Load game");
        loadBtn.setPrefSize(200, 70);
        loadBtn.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        root.getChildren().add(loadBtn);

        Button newBtn = new Button("New game");
        newBtn.setPrefSize(200, 70);
        newBtn.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: orange; -fx-text-fill: white;");
        newBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                map.createMap();
                bricks = new ArrayList<>(map.bricksList);
                brokenBricks = new ArrayList<>(map.brokenBricksList);
                enemies = new ArrayList<>(map.enemyTanksList);
                System.out.println("IN this");
                gameStart(primaryStage);
            }
        });
        root.getChildren().add(newBtn);

        Button exitBtn = new Button("Exit");
        exitBtn.setPrefSize(200, 70);
        exitBtn.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exit(0);
            }
        });
        root.getChildren().add(exitBtn);
    }


    public void gameStart(Stage primaryStage) {
        Scene scene = new Scene(map.createMap(), 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
        fieldCanvas = map.getCanvas();
        gcMain = fieldCanvas.getGraphicsContext2D();
        leftHudCanvas = map.getLeftHudCanvas();
        gcLeftHud = leftHudCanvas.getGraphicsContext2D();
        rightHudCanvas = map.getRightHudCanvas();
        gcRightHud = rightHudCanvas.getGraphicsContext2D();

        gcLeftHud.setFill(Color.WHITE);
        gcLeftHud.setFont(new Font(24));
        Tank tank = map.getTank();
        Tower tower = map.getTower();
        //bricks = map.bricksList;

        bullets = new ArrayList<>(tank.bullets);
        bullets = tank.bullets;

        ArrayList<String> input = new ArrayList<>();

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                if (!input.contains(code) && !gameOver) {
                    input.add(code);
                    if(code == "SPACE"){
                        spacePressed = true;
                        tank.addBullet();
                    }
                }
                if(code == "ENTER" && gameOver){
                    gameOver = false;
                }
            }
        });
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                input.remove(code);
            }
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                EnemyTank enemyTank = map.getEnemyTank();
                tank.tankMoveSet(input, gcMain);
                int bulletIndex = 0, enemyIndex = 0, brickIndex = 0, brokenBrickIndex = 0;
                boolean borderCollision = false;
                if(spacePressed && !gameOver){
                    tank.shoot();
                }
                if(tank.currentTankPosX < 0 || tank.currentTankPosX > GameMapView.width - Tank.tankSize / GameMapView.tileSize ||
                        tank.currentTankPosY < 0 || tank.currentTankPosY > GameMapView.height - Tank.tankSize / GameMapView.tileSize){
                    borderCollision = true;
                }
                if (borderCollision) {
                    tank.currentTankPosX = posBeforeX;
                    tank.currentTankPosY = posBeforeY;
                }
                for(bulletIndex = 0; bulletIndex < tank.bullets.size(); bulletIndex++){
                    if(tank.bullets.get(bulletIndex).bulletPosX < 0 || tank.bullets.get(bulletIndex).bulletPosX > GameMapView.width - Bullet.bulletHeight / GameMapView.tileSize ||
                            tank.bullets.get(bulletIndex).bulletPosY < 0 || tank.bullets.get(bulletIndex).bulletPosY > GameMapView.height - Bullet.bulletHeight / GameMapView.tileSize){
                        tank.bullets.remove(bulletIndex);
                        System.out.println("Deleted");
                    }
                }

                for(brickIndex = 0; brickIndex < bricks.size(); brickIndex++) {
                    Sprite brick = bricks.get(brickIndex);
                    if (tank.sprite.intersects(brick)) {
                        tank.currentTankPosX = posBeforeX;
                        tank.currentTankPosY = posBeforeY;
                    }
                    for(bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++){
                        Sprite bullet = bullets.get(bulletIndex).sprite;
                        if(brick.intersects(bullet)){
                            System.out.println("Wall");
                            bricks.remove(brickIndex);
                            bullets.remove(bulletIndex);
                        }
                    }
                }
                for(brokenBrickIndex = 0; brokenBrickIndex < brokenBricks.size(); brokenBrickIndex++) {
                    Sprite brokenBrick = brokenBricks.get(brokenBrickIndex);
                    if (tank.sprite.intersects(brokenBrick)) {
                        tank.currentTankPosX = posBeforeX;
                        tank.currentTankPosY = posBeforeY;
                    }
                    for(bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++){
                        Sprite bullet = bullets.get(bulletIndex).sprite;
                        if(brokenBrick.intersects(bullet)){
                            System.out.println("Wall broken");
                            brokenBricks.remove(brokenBrickIndex);
                            bullets.remove(bulletIndex);
                        }
                    }
                }
                for(enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++){
                    Sprite enemy = enemies.get(enemyIndex);
                    if (tank.sprite.intersects(enemy)) {
                        tank.currentTankPosX = posBeforeX;
                        tank.currentTankPosY = posBeforeY;
                    }
                    for(bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++){
                        Sprite bullet = bullets.get(bulletIndex).sprite;
                        if(enemy.intersects(bullet)){
                            System.out.println("Enemy");
                            enemies.remove(enemyIndex);
                            score += 100;
                            bullets.remove(bulletIndex);
                        }
                    }
                }

                if(tank.sprite.intersects(tower.sprite)){
                    tank.currentTankPosX = posBeforeX;
                    tank.currentTankPosY = posBeforeY;
                }

                for(bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++){
                    Sprite bullet = bullets.get(bulletIndex).sprite;
                    if(tower.sprite.intersects(bullet)){
                        System.out.println("Tower");
                        gameOver = true;
                    }
                }
                posBeforeX = tank.currentTankPosX;
                posBeforeY = tank.currentTankPosY;
                gcMain.clearRect(0, 0, 720, 720);
                if(gameOver){
                    tank.sprite.setPosition(GameMapView.tankStartPos[0], GameMapView.tankStartPos[1]);
                    tank.currentTankPosX = GameMapView.tankStartPos[0];
                    tank.currentTankPosY = GameMapView.tankStartPos[1];
                    tank.resetTankOrient();
                    score = 0;
                    gcMain.setFill(Color.WHITE);
                    gcMain.setFont(new Font(40));
                    gcMain.fillText("Game Over", 250,300);
                    gcMain.fillText("Press Enter to restart", 170, 350);
                    gcLeftHud.clearRect(0,0,280,720);
                    gcRightHud.clearRect(0,0,280,720);
                    bricks = new ArrayList<>(map.bricksList);
                    brokenBricks = new ArrayList<>(map.brokenBricksList);
                    enemies = new ArrayList<>(map.enemyTanksList);
                }
                else{
                    tank.sprite.render(gcMain);

                    map.getTower().sprite.render(gcMain);
                    for (Sprite enemy : enemies){
                        enemy.render(gcMain);
                    }
                    for (Sprite brokenBrick : brokenBricks){
                        brokenBrick.render(gcMain);
                    }
                    for (Sprite sprite : bricks) {
                        sprite.render(gcMain);
                    }
                    for(Bullet bullet : tank.bullets){
                        bullet.sprite.render(gcMain);
                    }
                    gcLeftHud.setStroke(Color.BROWN);
                    gcLeftHud.setLineWidth(2.5);
                    gcLeftHud.strokeRect(0,0, GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
                    gcRightHud.setStroke(Color.BROWN);
                    gcRightHud.setLineWidth(2.5);
                    gcRightHud.strokeRect(0,0, GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
                    gcLeftHud.clearRect(2,2,250,500);
                    String scoreText = "Score: " + score;
                    gcLeftHud.fillText(scoreText, 5,25);
                }
            }
        }.start();
    }

}
