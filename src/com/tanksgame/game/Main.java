package com.tanksgame.game;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.*;
import com.tanksgame.threads.AddEnemyToField;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

import static java.lang.System.exit;

public class Main extends Application {
    VBox root = new VBox();
    VBox pauseRoot = new VBox();
    GameMapView map = new GameMapView();
    Canvas fieldCanvas = new Canvas();
    Canvas leftHudCanvas = new Canvas();
    Canvas rightHudCanvas = new Canvas();
    Scene mainMenuScene;
    Scene gameScene;

    private GraphicsContext gcMain;
    private GraphicsContext gcLeftHud;
    private GraphicsContext gcRightHud;
    public double posBeforeX = map.getTank().currentTankPosX;
    public double posBeforeY = map.getTank().currentTankPosY;

    public AnimationTimer animationTimer;

    public boolean spacePressed;
    public boolean gameOver = false;
    public boolean gameWin = false;
    public boolean gamePause = false;

    ArrayList<Sprite> bricks;
    ArrayList<Sprite> brokenBricks;
    ArrayList<Bullet> bullets;
    ArrayList<EnemyTank> enemies;
    ArrayList<Sprite> metals;
    ArrayList<Sprite> spawnPoints;
    Tank tank;
    Tower tower;

    public Image live = new Image("assets/images/heart.png");
    public Image enemyImg = new Image("assets/images/enemytank1/tankEnemy1Up.png");

    public int score = 0;
    public int kills = 0;
    public int totalEnemiesLeft = GameMapView.totalNumOfEnemy;
    public int deadEnemies = 0;

    int bulletIndex = 0, enemyIndex = 0, brickIndex = 0, brokenBrickIndex = 0;
    int enemyIndexNext = 0;
    int enemyBulletIndex = 0;
    int enemyBulletIndexNext = 0;
    int metalIndex = 0;
    int spawnIndex = 0;

    Timer timer = new Timer();
    public AddEnemyToField addEnemyThread;
    public boolean threadsForGameOverStopped = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Tanks");
        showMainMenu(primaryStage);
        mainMenuScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainMenuScene);
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
                spawnPoints = new ArrayList<>(map.spawnPoints);
                enemies = new ArrayList<>(map.enemyTanksList);
                metals = new ArrayList<>(map.metalList);
                tank = map.getTank();
                tower = map.getTower();
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
        gameScene = new Scene(map.createMap(), 1280, 720);
        primaryStage.setScene(gameScene);
        primaryStage.show();
        addEnemyThread = new AddEnemyToField(enemies);
        addEnemyThread.start();
        fieldCanvas = map.getCanvas();
        gcMain = fieldCanvas.getGraphicsContext2D();
        leftHudCanvas = map.getLeftHudCanvas();
        gcLeftHud = leftHudCanvas.getGraphicsContext2D();
        rightHudCanvas = map.getRightHudCanvas();
        gcRightHud = rightHudCanvas.getGraphicsContext2D();

        gcLeftHud.setFill(Color.WHITE);
        gcLeftHud.setFont(new Font(24));
        gcRightHud.setFill(Color.WHITE);
        gcRightHud.setFont(new Font(24));

        bullets = tank.bullets;
        totalEnemiesLeft -= enemies.size();

        ArrayList<String> input = new ArrayList<>();

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                if (!input.contains(code) && !gameOver) {
                    input.add(code);
                    if ("SPACE".equals(code)) {
                        spacePressed = true;
                        tank.addBullet();
                    }
                }
                if ("ENTER".equals(code) && (gameOver || gameWin)) {
                    totalEnemiesLeft = GameMapView.totalNumOfEnemy - enemies.size();
                    restartGame();
                    if (gameOver) {
                        score = 0;
                    }
                    threadsForGameOverStopped = false;
                    gameOver = false;
                    gameWin = false;
                }
                if ("ESCAPE".equals(code)) {
                    gamePause = true;
                    animationTimer.stop();
                    Parent rootCopy = new StackPane();
                    Scene pauseScene = new Scene(pauseMenu(primaryStage), 1280,720);
                    primaryStage.setScene(pauseScene);
                }
                if("ENTER".equals(code) && gamePause){
                    gamePause = false;
                    primaryStage.setScene(gameScene);
                    animationTimer.start();
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


         animationTimer = new AnimationTimer(){
            @Override
            public void handle(long now) {
                tank.tankMoveSet(input, gcMain);

                boolean borderCollision = false;
                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    enemies.get(enemyIndex).enemyCollides = false;
                    enemies.get(enemyIndex).borderCollision = false;
                }

                for (spawnIndex = 0; spawnIndex < spawnPoints.size(); spawnIndex++) {
                    addEnemyThread.setCanSpawn(true, spawnIndex);
                }

                if (spacePressed && !gameOver) {
                    tank.shoot();
                }

                if (tank.currentTankPosX < 0 || tank.currentTankPosX > GameMapView.width - Tank.tankSize / GameMapView.tileSize ||
                        tank.currentTankPosY < 0 || tank.currentTankPosY > GameMapView.height - Tank.tankSize / GameMapView.tileSize) {
                    borderCollision = true;
                }
                if (borderCollision) {
                    tank.currentTankPosX = posBeforeX;
                    tank.currentTankPosY = posBeforeY;
                }

                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    if (enemies.get(enemyIndex).currentTankPosX < 0 || enemies.get(enemyIndex).currentTankPosX > GameMapView.width - EnemyTank.tankSize / GameMapView.tileSize ||
                            enemies.get(enemyIndex).currentTankPosY < 0 || enemies.get(enemyIndex).currentTankPosY > GameMapView.height - EnemyTank.tankSize / GameMapView.tileSize) {
                        enemies.get(enemyIndex).borderCollision = true;
                    }
                }

                for (spawnIndex = 0; spawnIndex < spawnPoints.size(); spawnIndex++) {
                    Sprite spawnPoint = spawnPoints.get(spawnIndex);
                    for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                        if (enemies.get(enemyIndex).sprite.intersects(spawnPoint)) {
                            addEnemyThread.setCanSpawn(false, spawnIndex);
                        }
                    }
                    if (tank.sprite.intersects(spawnPoint)) {
                        addEnemyThread.setCanSpawn(false, spawnIndex);
                    }
                }

                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    if (enemies.get(enemyIndex).borderCollision) {
                        enemies.get(enemyIndex).currentTankPosX = enemies.get(enemyIndex).posBeforeX;
                        enemies.get(enemyIndex).currentTankPosY = enemies.get(enemyIndex).posBeforeY;
                        enemies.get(enemyIndex).sprite.setPosition(enemies.get(enemyIndex).posBeforeX, enemies.get(enemyIndex).posBeforeY);
                        enemies.get(enemyIndex).changeOrient();
                    }
                }

                for (bulletIndex = 0; bulletIndex < tank.bullets.size(); bulletIndex++) {
                    if (tank.bullets.get(bulletIndex).bulletPosX < 0 || tank.bullets.get(bulletIndex).bulletPosX > GameMapView.width - Bullet.bulletHeight / GameMapView.tileSize ||
                            tank.bullets.get(bulletIndex).bulletPosY < 0 || tank.bullets.get(bulletIndex).bulletPosY > GameMapView.height - Bullet.bulletHeight / GameMapView.tileSize) {
                        tank.bullets.remove(bulletIndex);
                        System.out.println("Deleted");
                    }
                }

                checkBricksCollision();
                checkBrokenBricksCollision();
                checkEnemyTanksCollision();
                checkMetalCollision();
                checkEnemyBulletsCollision();

                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    //enemies.get(enemyIndex).shoot(enemyIndex);
                    if (!enemies.get(enemyIndex).threadCreated) {
                        enemies.get(enemyIndex).createAddBulletThread();
                        enemies.get(enemyIndex).threadCreated = true;
                    }
                    enemies.get(enemyIndex).shoot();
                }

                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    enemies.get(enemyIndex).shoot();
                }

                if (tank.sprite.intersects(tower.sprite)) {
                    tank.currentTankPosX = posBeforeX;
                    tank.currentTankPosY = posBeforeY;
                }

                for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                    Sprite bullet = bullets.get(bulletIndex).sprite;
                    if (tower.sprite.intersects(bullet)) {
                        System.out.println("Tower");
                        gameOver = true;
                    }
                }
                posBeforeX = tank.currentTankPosX;
                posBeforeY = tank.currentTankPosY;
                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    if (!enemies.get(enemyIndex).enemyCollides) {
                        enemies.get(enemyIndex).posBeforeX = enemies.get(enemyIndex).currentTankPosX;
                        enemies.get(enemyIndex).posBeforeY = enemies.get(enemyIndex).currentTankPosY;
                    }
                }
                if (GameMapView.totalNumOfEnemy == deadEnemies) {
                    gameWin = true;
                }
                gcMain.clearRect(0, 0, 720, 720);
                if (gameOver) {
                    if (!threadsForGameOverStopped) {
                        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                            if (!threadsForGameOverStopped) {
                                enemies.get(enemyIndex).shootThread.stop();
                                //enemies.get(enemyIndex).shootThread.interrupt();
                            }
                        }
                        threadsForGameOverStopped = true;
                    }
                    addEnemyThread.stop();
                    //addEnemyThread.interrupt();

                    gcMain.setFill(Color.WHITE);
                    gcMain.setFont(new Font(40));
                    gcMain.fillText("Game Over", 250, 300);
                    gcMain.fillText("Press Enter to restart", 170, 350);
                    gcLeftHud.clearRect(0, 0, 280, 720);
                    gcRightHud.clearRect(0, 0, 280, 720);

                } else if (gameWin) {
                    if (!threadsForGameOverStopped) {
                        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                            if (!threadsForGameOverStopped) {
                                 enemies.get(enemyIndex).shootThread.stop();
                                 //enemies.get(enemyIndex).shootThread.interrupt();
                            }
                        }
                        threadsForGameOverStopped = true;
                        addEnemyThread.stop();
                        //addEnemyThread.interrupt();
                    }

                    gcMain.setFill(Color.WHITE);
                    gcMain.setFont(new Font(40));
                    gcMain.fillText("You win", 250, 300);
                    gcMain.fillText("Your score: " + score, 200, 350);
                    gcMain.fillText("Press Enter to continue", 160, 400);
                    gcLeftHud.clearRect(0, 0, 280, 720);
                    gcRightHud.clearRect(0, 0, 280, 720);
                } else {
                    tank.sprite.render(gcMain);

                    map.getTower().sprite.render(gcMain);
                    for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                        enemies.get(enemyIndex).move();
                        enemies.get(enemyIndex).sprite.render(gcMain);
                    }
                    for (Sprite brokenBrick : brokenBricks) {
                        brokenBrick.render(gcMain);
                    }
                    for (Sprite sprite : bricks) {
                        sprite.render(gcMain);
                    }
                    for (Sprite metal : metals) {
                        metal.render(gcMain);
                    }
                    for (Bullet bullet : tank.bullets) {
                        bullet.sprite.render(gcMain);
                    }
                    for (EnemyTank enemy : enemies) {
                        for (Bullet bullet : enemy.bullets) {
                            bullet.sprite.render(gcMain);
                        }
                    }
                    gcLeftHud.setStroke(Color.BROWN);
                    gcLeftHud.setLineWidth(2.5);
                    gcLeftHud.strokeRect(0, 0, GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
                    gcRightHud.setStroke(Color.BROWN);
                    gcRightHud.setLineWidth(2.5);
                    gcRightHud.strokeRect(0, 0, GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
                    gcLeftHud.clearRect(2, 2, 258, 718);
                    gcRightHud.clearRect(2, 2, 258, 718);
                    String scoreText = "Score: " + score;
                    gcLeftHud.fillText(scoreText, 5, 25);
                    gcLeftHud.fillText("Lives: ", 5, 70);
                    int enemiesLeft = GameMapView.totalNumOfEnemy - deadEnemies;
                    gcRightHud.fillText("Enemies left", 15, 25);
                    for (int enemiesLeftIndex = 0; enemiesLeftIndex < enemiesLeft; enemiesLeftIndex++) {
                        if (enemiesLeftIndex % 2 == 0) {
                            gcRightHud.drawImage(enemyImg, 20, 50 + (enemiesLeftIndex * 30), 40, 40);
                        } else {
                            gcRightHud.drawImage(enemyImg, 100, 50 + ((enemiesLeftIndex - 1) * 30), 40, 40);
                        }
                    }
                    gcLeftHud.fillText("Dead enemies : " + deadEnemies, 5, 120);
                    for (int liveIndex = 0; liveIndex < tank.lives; liveIndex++) {
                        gcLeftHud.drawImage(live, 70 + (liveIndex * 45), 40, 45, 45);
                    }
                }
            }
        };
         animationTimer.start();
    }

    void checkBricksCollision() {
        for (brickIndex = 0; brickIndex < bricks.size(); brickIndex++) {
            Sprite brick = bricks.get(brickIndex);
            if (tank.sprite.intersects(brick)) {
                tank.currentTankPosX = posBeforeX;
                tank.currentTankPosY = posBeforeY;
            }
            for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                Sprite bullet = bullets.get(bulletIndex).sprite;
                if (brick.intersects(bullet)) {
                    System.out.println("Wall");
                    bricks.remove(brickIndex);
                    bullets.remove(bulletIndex);
                }
            }
        }
    }

    void checkMetalCollision() {
        for (metalIndex = 0; metalIndex < metals.size(); metalIndex++) {
            Sprite metal = metals.get(metalIndex);
            if (tank.sprite.intersects(metal)) {
                tank.currentTankPosX = posBeforeX;
                tank.currentTankPosY = posBeforeY;
            }
            for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                Sprite bullet = bullets.get(bulletIndex).sprite;
                if (metal.intersects(bullet)) {
                    System.out.println("Metal");
                    bullets.remove(bulletIndex);
                }
            }
        }
    }

    void checkBrokenBricksCollision() {
        for (brokenBrickIndex = 0; brokenBrickIndex < brokenBricks.size(); brokenBrickIndex++) {
            Sprite brokenBrick = brokenBricks.get(brokenBrickIndex);
            if (tank.sprite.intersects(brokenBrick)) {
                tank.currentTankPosX = posBeforeX;
                tank.currentTankPosY = posBeforeY;
            }
            for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                Sprite bullet = bullets.get(bulletIndex).sprite;
                if (brokenBrick.intersects(bullet)) {
                    System.out.println("Wall broken");
                    brokenBricks.remove(brokenBrickIndex);
                    bullets.remove(bulletIndex);
                }
            }
        }
    }

    void checkEnemyBulletsCollision() {
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                if (enemyBullet.bulletPosX < 0 || enemyBullet.bulletPosX > GameMapView.width - Bullet.bulletHeight / GameMapView.tileSize ||
                        enemyBullet.bulletPosY < 0 || enemyBullet.bulletPosY > GameMapView.height - Bullet.bulletHeight / GameMapView.tileSize) {
                    enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                }
            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);

                if (tower.sprite.intersects(enemyBullet.sprite)) {
                    gameOver = true;
                }
                for (brickIndex = 0; brickIndex < bricks.size(); brickIndex++) {
                    Sprite brick = bricks.get(brickIndex);
                    if (brick.intersects(enemyBullet.sprite)) {
                        System.out.println("Wall");
                        bricks.remove(brickIndex);
                        enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                    }
                }
            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                if (tank.sprite.intersects(enemyBullet.sprite)) {
                    enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                    tank.lives--;
                    if (tank.lives == 0) {
                        gameOver = true;
                    }
                }
            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                for (metalIndex = 0; metalIndex < metals.size(); metalIndex++) {
                    Sprite metal = metals.get(metalIndex);
                    if (metal.intersects(enemyBullet.sprite)) {
                        System.out.println("Metal");
                        enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                    }
                }
            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                for (brokenBrickIndex = 0; brokenBrickIndex < brokenBricks.size(); brokenBrickIndex++) {
                    Sprite brokenBrick = brokenBricks.get(brokenBrickIndex);
                    if (brokenBrick.intersects(enemyBullet.sprite)) {
                        System.out.println("Wall broke");
                        brokenBricks.remove(brokenBrickIndex);
                        enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                    }
                }
            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                    Sprite bullet = bullets.get(bulletIndex).sprite;
                    if (bullet.intersects(enemyBullet.sprite)) {
                        System.out.println("Bullet");
                        bullets.remove(bulletIndex);
                        //tank.bullets.remove(bulletIndex);
                        enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                    }
                }
            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                for (enemyIndexNext = 0; enemyIndexNext < enemies.size(); enemyIndexNext++) {
                    if (enemyIndex == enemyIndexNext) {
                        continue;
                    }
                    for (enemyBulletIndexNext = 0; enemyBulletIndexNext < enemies.get(enemyIndexNext).bullets.size(); enemyBulletIndexNext++) {
                        Sprite enemyBulletNext = enemies.get(enemyIndexNext).bullets.get(enemyBulletIndexNext).sprite;
                        if (enemyBulletNext.intersects(enemyBullet.sprite)) {
                            System.out.println("Enemy Bullet");             //тут может крашиться
                            enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                            enemies.get(enemyIndexNext).bullets.remove(enemyBulletIndexNext);
                        }
                    }
                }
            }
        }
        boolean flag = false;
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            for (enemyIndexNext = 0; enemyIndexNext < enemies.size(); enemyIndexNext++) {
                if (enemyIndex == enemyIndexNext) {
                    continue;
                }
                for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                    Bullet enemyBullet = enemies.get(enemyIndex).bullets.get(enemyBulletIndex);
                    Sprite enemyTank = enemies.get(enemyIndexNext).sprite;
                    if (enemyTank.intersects(enemyBullet.sprite)) {
                        System.out.println("Enemy");
                        enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                        if (enemies.get(enemyIndexNext).shootThread != null) {
                            enemies.get(enemyIndexNext).shootThread.stop();
                            //enemies.get(enemyIndexNext).shootThread.interrupt();
                        }
                        enemies.remove(enemyIndexNext);
                        deadEnemies++;
                        flag = true;
                    }
                    if (flag) {
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
    }

    void checkEnemyTanksCollision() {
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            EnemyTank enemy = enemies.get(enemyIndex);
            if (tank.sprite.intersects(enemy.sprite)) {
                tank.currentTankPosX = posBeforeX;
                tank.currentTankPosY = posBeforeY;
                tank.lives--;
                if (enemies.get(enemyIndex).shootThread != null) {
                    enemies.get(enemyIndex).shootThread.stop();
                    //enemies.get(enemyIndex).shootThread.interrupt();
                }
                if (tank.lives == 0) {
                    gameOver = true;
                }
                enemies.remove(enemyIndex);
                score += 100;
                deadEnemies++;

            }
        }
        for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
            EnemyTank enemy = enemies.get(enemyIndex);

            for (enemyIndexNext = 0; enemyIndexNext < enemies.size(); enemyIndexNext++) {
                if (enemyIndex == enemyIndexNext) {
                    continue;
                }
                if (enemies.get(enemyIndexNext).sprite.intersects(enemies.get(enemyIndex).sprite)) {
                    enemies.get(enemyIndexNext).currentTankPosX = enemies.get(enemyIndexNext).posBeforeX;
                    enemies.get(enemyIndexNext).currentTankPosY = enemies.get(enemyIndexNext).posBeforeY;
                    enemies.get(enemyIndexNext).sprite.setPosition(enemies.get(enemyIndexNext).posBeforeX, enemies.get(enemyIndexNext).posBeforeY);
                    enemies.get(enemyIndexNext).enemyCollides = true;
                    enemies.get(enemyIndexNext).changeOrient();
                    setEnemyCollisionPos();
                    enemies.get(enemyIndex).changeOrient();
                }
            }

            if (enemies.get(enemyIndex).sprite.intersects(tower.sprite)) {
                setEnemyCollisionPos();
                enemies.get(enemyIndex).changeOrient();
            }

            for (brokenBrickIndex = 0; brokenBrickIndex < brokenBricks.size(); brokenBrickIndex++) {
                Sprite brokenBrick = brokenBricks.get(brokenBrickIndex);
                if (enemies.get(enemyIndex).sprite.intersects(brokenBrick)) {
                    setEnemyCollisionPos();
                    enemies.get(enemyIndex).changeOrient();
                }
            }

            for (metalIndex = 0; metalIndex < metals.size(); metalIndex++) {
                Sprite metal = metals.get(metalIndex);
                if (enemies.get(enemyIndex).sprite.intersects(metal)) {
                    setEnemyCollisionPos();
                    enemies.get(enemyIndex).changeOrient();
                }
            }

            for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                Sprite bullet = bullets.get(bulletIndex).sprite;
                if (enemy.sprite.intersects(bullet)) {
                    System.out.println("Enemy");
                    if (enemies.get(enemyIndex).shootThread != null) {
                        enemies.get(enemyIndex).shootThread.stop();
                        //enemies.get(enemyIndex).shootThread.interrupt();
                    }
                    ;
                    enemies.remove(enemyIndex);
                    score += 100;
                    deadEnemies++;
                    kills++;
                    bullets.remove(bulletIndex);
                }
            }
        }
    }

    void resetEnemies() {
        enemies = new ArrayList<>(map.enemyTanksList);
        enemies = new ArrayList<>();
        for (enemyIndex = 0; enemyIndex < map.enemyTanksList.size(); enemyIndex++) {
            EnemyTank enemyTank = new EnemyTank();
            enemyTank.currentTankPosX = GameMapView.enemyStartPos[enemyIndex][0];
            enemyTank.currentTankPosY = GameMapView.enemyStartPos[enemyIndex][1];
            enemyTank.sprite.setPosition(GameMapView.enemyStartPos[enemyIndex][0], GameMapView.enemyStartPos[enemyIndex][1]);
            enemyTank.sprite.setSize(EnemyTank.tankSize);
            enemyTank.resetOrient();
            enemies.add(enemyTank);
        }
    }

    void restartGame() {
        deadEnemies = 0;
        tank.sprite.setPosition(GameMapView.tankStartPos[0], GameMapView.tankStartPos[1]);
        tank.currentTankPosX = GameMapView.tankStartPos[0];
        tank.currentTankPosY = GameMapView.tankStartPos[1];
        tank.lives = 3;
        tank.resetTankOrient();
        bullets.clear();
        enemies.clear();
        bricks = new ArrayList<>(map.bricksList);
        metals = new ArrayList<>(map.metalList);
        brokenBricks = new ArrayList<>(map.brokenBricksList);
        resetEnemies();
        addEnemyThread = new AddEnemyToField(enemies);
        addEnemyThread.start();
    }

    void setEnemyCollisionPos() {
        enemies.get(enemyIndex).currentTankPosX = enemies.get(enemyIndex).posBeforeX;
        enemies.get(enemyIndex).currentTankPosY = enemies.get(enemyIndex).posBeforeY;
        enemies.get(enemyIndex).sprite.setPosition(enemies.get(enemyIndex).posBeforeX, enemies.get(enemyIndex).posBeforeY);
        enemies.get(enemyIndex).enemyCollides = true;
    }

    Parent pauseMenu(Stage primaryStage){
        pauseRoot = new VBox();
        Canvas pauseCanvas = new Canvas(1280,720);
        pauseRoot.setStyle("-fx-background-color: black");
        pauseRoot.setAlignment(Pos.TOP_CENTER);
        pauseRoot.setPadding(new Insets(20, 100, 100, 100));
        pauseRoot.setSpacing(40);
        pauseRoot.setPrefSize(1280,720);
        Text pauseText = new Text("Pause");
        pauseText.setFont(Font.font("Arial", 100));
        pauseText.setFill(Color.WHITE);

        Button continueButton = new Button("Continue");
        continueButton.setPrefSize(200, 70);
        continueButton.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        continueButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gamePause = false;
                primaryStage.setScene(gameScene);
                animationTimer.start();
            }
        });

        Button returnMainMenu = new Button("Return to\nmain menu");
        returnMainMenu.setPrefSize(200, 70);
        returnMainMenu.setStyle("-fx-font-size: 21 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white; -fx-text-alignment: center");
        returnMainMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(mainMenuScene);
            }
        });


        Button exitButton = new Button("Exit");
        exitButton.setPrefSize(200, 70);
        exitButton.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exit(0);
            }
        });

        pauseRoot.getChildren().addAll(pauseText, continueButton, returnMainMenu, exitButton);
        return pauseRoot;
    }
}