package com.tanksgame.game;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.*;
import com.tanksgame.threads.AddEnemyToField;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.tanksgame.maps.GameMapView.*;
import static java.lang.System.exit;

public class Main extends Application {
    VBox root = new VBox();
    private AnchorPane watchRoot = new AnchorPane();
    VBox replayRoot = new VBox();
    VBox pauseRoot = new VBox();
    GameMapView map = new GameMapView();
    Canvas fieldCanvas = new Canvas();
    Canvas leftHudCanvas = new Canvas();
    Canvas rightHudCanvas = new Canvas();
    Scene mainMenuScene;
    Scene gameScene;
    Scene replayMenu;
    Scene watchReplayScene;
    ListView<String> replaysList = new ListView<>();

    public Image startBgImg = new Image("assets/images/startBackground.jpg");
    BackgroundSize bgSize = new BackgroundSize(1280, 720, false, false,
            false, false);
    BackgroundImage backgroundImg = new BackgroundImage(startBgImg, BackgroundRepeat.REPEAT,
            BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, bgSize);
    Background background = new Background(backgroundImg);

    private GraphicsContext gcMain;
    private GraphicsContext gcLeftHud;
    private GraphicsContext gcRightHud;
    public double posBeforeX = map.getTank().currentTankPosX;
    public double posBeforeY = map.getTank().currentTankPosY;

    public AnimationTimer animationTimer;
    public AnimationTimer replayAnimationTimer;

    public boolean spacePressed;
    public boolean gameOver = false;
    public boolean gameWin = false;
    public boolean gamePause = false;

    ArrayList<Sprite> bricks = new ArrayList<>();
    ArrayList<Sprite> brokenBricks = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<EnemyTank> enemies = new ArrayList<>();
    ArrayList<Sprite> metals = new ArrayList<>();
    ArrayList<Sprite> spawnPoints = new ArrayList<>();
    Tank tank = new Tank();
    Tower tower = new Tower();

    public Image live = new Image("assets/images/heart.png");
    public Image enemyImg = new Image("assets/images/enemytank1/tankEnemy1Up.png");
    public Image bulletLeft = new Image("assets/images/bulletLeft.png");
    public Image bulletRight = new Image("assets/images/bulletRight.png");
    public Image bulletUp = new Image("assets/images/bulletUp.png");
    public Image bulletDown = new Image("assets/images/bulletDown.png");

    int pressedTimes = 0;
    boolean animationTimerStarted = false;

    public int score = 0;
    public int kills = 0;
    public int prevLevel;
    public int totalEnemiesLeft = GameMapView.totalNumOfEnemy;
    public int deadEnemies = 0;
    int enemiesLeft;

    int bulletIndex = 0, enemyIndex = 0, brickIndex = 0, brokenBrickIndex = 0;
    int enemyIndexNext = 0;
    int enemyBulletIndex = 0;
    int enemyBulletIndexNext = 0;
    int metalIndex = 0;
    int spawnIndex = 0;

    Timer timer = new Timer();
    public AddEnemyToField addEnemyThread;
    public boolean threadsForGameOverStopped = false;

    FileWriter replayWrite;
    FileReader replayRead;
    File replay;
    File replayFolder = new File("replays");
    BufferedReader reader;
    int currentNumOfReplay;
    ObservableList<String> replays = FXCollections.observableArrayList();
    String replayToWatch;
    private GraphicsContext gcMainWatch;
    private GraphicsContext gcLeftHudWatch;
    private GraphicsContext gcRightHudWatch;

    Sprite bullet = new Sprite();
    EnemyTank enemy = new EnemyTank();

    ArrayList<String> input = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Tanks");
        gameScene = new Scene(map.createMap(), 1280, 720);
        watchReplayScene = new Scene(watchRoot, 1280, 720);
        mainMenuScene = new Scene(root, 1280, 720);
        replayMenu = new Scene(replayRoot, 1280, 720);
        replayFolder.mkdir();
        createReplayMenu(primaryStage);
        showMainMenu(primaryStage);
        createAnimationTimerForReplay(primaryStage);
        createAnimationTimer(primaryStage);
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public void showMainMenu(Stage primaryStage) {
        root.setSpacing(10);
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
                map.level = 1;
                restartGame();
                score = 0;
                primaryStage.setScene(gameScene);
                primaryStage.show();
                fieldCanvas = map.getCanvas();
                gcMain = fieldCanvas.getGraphicsContext2D();
                leftHudCanvas = map.getLeftHudCanvas();
                gcLeftHud = leftHudCanvas.getGraphicsContext2D();
                rightHudCanvas = map.getRightHudCanvas();
                gcRightHud = rightHudCanvas.getGraphicsContext2D();
                File[] files = replayFolder.listFiles();
                int maxRep = 0;
                int result = 0;
                for (int i = 0; i < files.length; i++) {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(files[i].getName());
                    int start = 0;
                    matcher.find(start);
                    String value = files[i].getName().substring(matcher.start(), matcher.end());
                    result = Integer.parseInt(value);
                    if (result > maxRep) {
                        maxRep = result;
                    }
                    System.out.println(result);
                    System.out.println(files[i].getName());
                }
                System.out.println("Max replay: " + result);
                currentNumOfReplay = maxRep + 1;

                replay = new File(replayFolder + "\\replay" + String.valueOf(currentNumOfReplay) + ".txt");

                try {
                    replay.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    replayWrite = new FileWriter("replays/replay" + String.valueOf(currentNumOfReplay) + ".txt", false);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                gcLeftHud.setFill(Color.WHITE);
                gcLeftHud.setFont(new Font(24));
                gcRightHud.setFill(Color.WHITE);
                gcRightHud.setFont(new Font(24));
                gameStart(primaryStage);
            }
        });
        root.getChildren().add(newBtn);

        Button replayBtn = new Button("Replays");
        replayBtn.setPrefSize(200, 70);
        replayBtn.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        replayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(replayMenu);
                File replayFolder = new File("replays");
                replays.clear();
                File[] files = replayFolder.listFiles();
                for (int i = 0; i < files.length; i++) {
                    System.out.println(files[i].getName());
                    replays.add(files[i].getName());
                }

                replaysList.setItems(replays);
            }
        });
        root.getChildren().add(replayBtn);

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

    public void createReplayMenu(Stage primaryStage) {

        replayRoot.setBackground(background);

        replayRoot.setSpacing(10);
        replayRoot.setPadding(new Insets(20, 20, 10, 20));
        replayRoot.setAlignment(Pos.TOP_CENTER);
        replaysList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        replaysList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                replayToWatch = newValue;
            }
        });

        replayRoot.getChildren().add(replaysList);

        Button watchBtn = new Button("Watch");
        watchBtn.setPrefSize(350, 70);
        watchBtn.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        watchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                restartGame();
                //primaryStage.setScene(gameScene);
                primaryStage.setScene(watchReplayScene);
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
                watchReplay(replayToWatch);
            }
        });
        replayRoot.getChildren().add(watchBtn);

        Button returnBtn = new Button("Return to main menu");
        returnBtn.setPrefSize(350, 70);
        returnBtn.setStyle("-fx-font-size: 28 arial;" +
                "-fx-background-color: grey; -fx-text-fill: white;");
        returnBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(mainMenuScene);
            }
        });

        replayRoot.getChildren().add(returnBtn);

        Canvas canvas = new Canvas(720, 720);
        Canvas leftHudCanvas = new Canvas(GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
        Canvas rightHudCanvas = new Canvas(GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);

        watchRoot.setPrefSize(width * tileSize + 2 * hudSizeHalf, height * tileSize);


        StackPane gameZone = new StackPane();
        AnchorPane leftHudPane = new AnchorPane();
        AnchorPane rightHudPane = new AnchorPane();
        AnchorPane.setLeftAnchor(gameZone, Double.valueOf(hudSizeHalf));


        AnchorPane.setLeftAnchor(leftHudPane, 0.0);
        AnchorPane.setLeftAnchor(rightHudPane, Double.valueOf(hudSizeHalf + (tileSize * width)));
        gameZone.getChildren().add(canvas);
        gameZone.setStyle("-fx-background-color: black;");
        leftHudPane.getChildren().add(leftHudCanvas);
        leftHudPane.setStyle("-fx-background-color: black;");
        rightHudPane.getChildren().add(rightHudCanvas);
        rightHudPane.setStyle("-fx-background-color: black;");
        watchRoot.getChildren().addAll(gameZone, leftHudPane, rightHudPane);

        gcMainWatch = canvas.getGraphicsContext2D();
        gcLeftHudWatch = leftHudCanvas.getGraphicsContext2D();
        gcRightHudWatch = rightHudCanvas.getGraphicsContext2D();

        gcLeftHudWatch.setStroke(Color.BROWN);
        gcLeftHudWatch.setLineWidth(2.5);
        gcLeftHudWatch.strokeRect(0, 0, hudSizeHalf, tileSize * height);
        gcLeftHudWatch.setFill(Color.WHITE);
        gcLeftHudWatch.setFont(new Font(24));
        gcRightHudWatch.setStroke(Color.BROWN);
        gcRightHudWatch.setLineWidth(2.5);
        gcRightHudWatch.strokeRect(0, 0, hudSizeHalf, tileSize * height);
        gcRightHudWatch.setFill(Color.WHITE);
        gcRightHudWatch.setFont(new Font(24));

        EventHandler<KeyEvent> watchEventsPress = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                if ("ESCAPE".equals(code)) {
                    replayAnimationTimer.stop();
                    primaryStage.setScene(replayMenu);
                }
            }
        };

        EventHandler<KeyEvent> watchEventRelease = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                input.remove(code);
            }
        };

        watchReplayScene.addEventHandler(KeyEvent.KEY_PRESSED, watchEventsPress);
        watchReplayScene.addEventHandler(KeyEvent.KEY_RELEASED, watchEventRelease);
    }

    public void gameStart(Stage primaryStage) {

        EventHandler<KeyEvent> inGameEventsPress = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                if (!input.contains(code) && !gameOver) {
                    input.add(code);
                    if ("SPACE".equals(code)) {
                        spacePressed = true;
                        pressedTimes++;
                        System.out.println("Pressed: " + pressedTimes);
                        tank.addBullet();
                    }
                }
                if ("ENTER".equals(code) && (gameOver || gameWin)) {
                    totalEnemiesLeft = GameMapView.totalNumOfEnemy - enemies.size();
                    if (gameOver) {
                        score = 0;
                        map.level = 1;
                        currentNumOfReplay++;
                        replay = new File(replayFolder + "\\replay" + String.valueOf(currentNumOfReplay) + ".txt");
                        try {
                            replay.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            replayWrite = new FileWriter("replays/replay" + String.valueOf(currentNumOfReplay) + ".txt", false);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    if (gameWin) {
                        map.level++;
                        if (map.level > 2) {
                            map.level = 1;
                        }
                    }
                    restartGame();
                    threadsForGameOverStopped = false;
                    gameOver = false;
                    gameWin = false;
                }
                if ("ESCAPE".equals(code)) {
                    gamePause = true;
                    animationTimer.stop();
                    addEnemyThread.pause = true;
                    for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                        enemies.get(enemyIndex).shootPause = true;
                    }
                    Parent rootCopy = new StackPane();
                    Scene pauseScene = new Scene(pauseMenu(primaryStage), 1280, 720);
                    primaryStage.setScene(pauseScene);
                }
            }
        };

        EventHandler<KeyEvent> inGameEventRelease = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                input.remove(code);
            }
        };

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, inGameEventsPress);
        gameScene.addEventHandler(KeyEvent.KEY_RELEASED, inGameEventRelease);

        animationTimer.start();
    }

    void createAnimationTimer(Stage primaryStage) {

        totalEnemiesLeft -= enemies.size();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                animationTimerStarted = true;
                tank.tankMoveSet(input, gcMain);
                bullets = tank.bullets;

                boolean borderCollision = false;
                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    enemies.get(enemyIndex).enemyCollides = false;
                    enemies.get(enemyIndex).borderCollision = false;
                }

                for (spawnIndex = 0; spawnIndex < spawnPoints.size(); spawnIndex++) {
                    addEnemyThread.setCanSpawn(true, spawnIndex);
                }
                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    enemies.get(enemyIndex).shootPause = false;
                }

                if (spacePressed && !gameOver) {
                    tank.shoot();
                }

                if (tank.currentTankPosX < 0 || tank.currentTankPosX > width - Tank.tankSize / tileSize ||
                        tank.currentTankPosY < 0 || tank.currentTankPosY > GameMapView.height - Tank.tankSize / tileSize) {
                    borderCollision = true;
                }
                if (borderCollision) {
                    tank.currentTankPosX = posBeforeX;
                    tank.currentTankPosY = posBeforeY;
                }

                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    if (enemies.get(enemyIndex).currentTankPosX < 0 || enemies.get(enemyIndex).currentTankPosX > width - EnemyTank.tankSize / tileSize ||
                            enemies.get(enemyIndex).currentTankPosY < 0 || enemies.get(enemyIndex).currentTankPosY > GameMapView.height - EnemyTank.tankSize / tileSize) {
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
                    if (tank.bullets.get(bulletIndex).bulletPosX < 0 || tank.bullets.get(bulletIndex).bulletPosX > width - Bullet.bulletHeight / tileSize ||
                            tank.bullets.get(bulletIndex).bulletPosY < 0 || tank.bullets.get(bulletIndex).bulletPosY > GameMapView.height - Bullet.bulletHeight / tileSize) {
                        tank.bullets.remove(bulletIndex);
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

                    writeInfo();

                    tank.sprite.render(gcMain);

                    map.getTower().sprite.render(gcMain);
                    for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                        enemies.get(enemyIndex).move();
                        enemies.get(enemyIndex).sprite.render(gcMain);
                    }
                    for (Sprite brokenBrick : brokenBricks) {
                        brokenBrick.render(gcMain);
                    }
                    for (Sprite brick : bricks) {
                        brick.render(gcMain);
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
                    gcLeftHud.strokeRect(0, 0, hudSizeHalf, tileSize * GameMapView.height);
                    gcRightHud.setStroke(Color.BROWN);
                    gcRightHud.setLineWidth(2.5);
                    gcRightHud.strokeRect(0, 0, hudSizeHalf, tileSize * GameMapView.height);
                    gcLeftHud.clearRect(2, 2, 258, 715);
                    gcRightHud.clearRect(2, 2, 258, 715);
                    String scoreText = "Score: " + score;
                    gcLeftHud.fillText(scoreText, 5, 25);
                    gcLeftHud.fillText("Lives: ", 5, 70);
                    enemiesLeft = GameMapView.totalNumOfEnemy - deadEnemies;
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
                if (enemyBullet.bulletPosX < 0 || enemyBullet.bulletPosX > width - Bullet.bulletHeight / tileSize ||
                        enemyBullet.bulletPosY < 0 || enemyBullet.bulletPosY > GameMapView.height - Bullet.bulletHeight / tileSize) {
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
                        bricks.remove(brickIndex);
                        if (!enemies.get(enemyIndex).bullets.isEmpty()) {
                            enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                        }
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
                        if (!enemies.get(enemyIndex).bullets.isEmpty()) {
                            enemies.get(enemyIndex).bullets.remove(enemyBulletIndex);
                        }
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
            enemyTank.id = enemyIndex;
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
        map.drawBricks();
        bricks = new ArrayList<>(map.bricksList);
        map.drawMetal();
        metals = new ArrayList<>(map.metalList);
        spawnPoints = new ArrayList<>(map.spawnPoints);
        map.drawBrokenBricks();
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

    Parent pauseMenu(Stage primaryStage) {
        pauseRoot = new VBox();
        Canvas pauseCanvas = new Canvas(1280, 720);
        pauseRoot.setStyle("-fx-background-color: black");
        pauseRoot.setAlignment(Pos.TOP_CENTER);
        pauseRoot.setPadding(new Insets(20, 100, 100, 100));
        pauseRoot.setSpacing(40);
        pauseRoot.setPrefSize(1280, 720);
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
                addEnemyThread.pause = false;
                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    enemies.get(enemyIndex).shootPause = true;
                }
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
                animationTimer.stop();
                addEnemyThread.stop();
                for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                    enemies.get(enemyIndex).shootThread.stop();
                    //enemies.get(enemyIndex).shootThread.interrupt();
                }
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

    public void writeInfo() {
        try {
            replayWrite.write("/////Level/////\n");
            replayWrite.write(String.valueOf(map.level) + '\n');
            replayWrite.write("/////Score/////\n");
            replayWrite.write(String.valueOf(score) + '\n');
            replayWrite.write("/////Lives/////\n");
            replayWrite.write(String.valueOf(tank.lives) + '\n');
            replayWrite.write("/////Enemies Left/////\n");
            replayWrite.write(String.valueOf(enemiesLeft) + '\n');
            replayWrite.write("/////Tank Pos/////\n");
            replayWrite.write(tank.orient + '\n');
            replayWrite.write(String.valueOf(tank.currentTankPosX) + '\n' + String.valueOf(tank.currentTankPosY) + '\n');
            replayWrite.write("/////Bullets/////\n");
            if (tank.bullets.isEmpty()) {
                replayWrite.write("Empty\n");
            } else {
                for (bulletIndex = 0; bulletIndex < bullets.size(); bulletIndex++) {
                    replayWrite.write(bullets.get(bulletIndex).orient + '\n');
                    replayWrite.write(String.valueOf(bullets.get(bulletIndex).bulletPosX) + '\n'
                            + String.valueOf(bullets.get(bulletIndex).bulletPosY) + '\n');
                }
            }
            replayWrite.write("/////Enemies/////\n");
            for (enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                replayWrite.write("Enemy" + String.valueOf(enemies.get(enemyIndex).id) + '\n');
                replayWrite.write(enemies.get(enemyIndex).orient + '\n');
                replayWrite.write(String.valueOf(enemies.get(enemyIndex).currentTankPosX) + '\n');
                replayWrite.write(String.valueOf(enemies.get(enemyIndex).currentTankPosY) + '\n');
                replayWrite.write("//Bullets//\n");
                if (enemies.get(enemyIndex).bullets.isEmpty()) {
                    replayWrite.write("Empty\n");
                } else {
                    for (enemyBulletIndex = 0; enemyBulletIndex < enemies.get(enemyIndex).bullets.size(); enemyBulletIndex++) {
                        replayWrite.write(enemies.get(enemyIndex).bullets.get(enemyBulletIndex).orient + '\n');
                        replayWrite.write(String.valueOf(enemies.get(enemyIndex).bullets.get(enemyBulletIndex).bulletPosX) + '\n' +
                                String.valueOf(enemies.get(enemyIndex).bullets.get(enemyBulletIndex).bulletPosY) + '\n');
                    }
                }
            }
            replayWrite.write("/////Broken Bricks/////\n");
            for (brokenBrickIndex = 0; brokenBrickIndex < brokenBricks.size(); brokenBrickIndex++) {
                replayWrite.write(String.valueOf(brokenBricks.get(brokenBrickIndex).id) + '\n');
            }
            replayWrite.write("/////Bricks/////\n");
            for (brickIndex = 0; brickIndex < bricks.size(); brickIndex++) {
                replayWrite.write(String.valueOf(bricks.get(brickIndex).id) + '\n');
            }
            replayWrite.write("//////\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void watchReplay(String replayName) {
        try {

            replayRead = new FileReader("replays/" + replayToWatch);
            reader = new BufferedReader(replayRead);
            replayAnimationTimer.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createAnimationTimerForReplay(Stage primaryStage) {

        replayAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        stop();
                    }
                    gcMainWatch.clearRect(0, 0, 720, 720);
                    gcLeftHudWatch.clearRect(2, 2, 258, 715);
                    gcRightHudWatch.clearRect(2, 2, 258, 715);
                    line = reader.readLine();
                    map.level = Integer.parseInt(line);
                    if (map.level - prevLevel != 0) {
                        map.drawBricks();
                        bricks = new ArrayList<>(map.bricksList);
                        map.drawBrokenBricks();
                        brokenBricks = new ArrayList<>(map.brokenBricksList);
                        map.drawMetal();
                        metals = new ArrayList<>(map.metalList);
                    }
                    prevLevel = Integer.parseInt(line);
                    line = reader.readLine();
                    line = reader.readLine();
                    gcLeftHudWatch.fillText("Score: " + line, 5, 25);
                    line = reader.readLine();
                    line = reader.readLine();
                    gcLeftHudWatch.fillText("Lives", 5, 70);
                    for (int liveIndex = 0; liveIndex < Integer.parseInt(line); liveIndex++) {
                        gcLeftHudWatch.drawImage(live, 70 + (liveIndex * 45), 40, 45, 45);
                    }
                    line = reader.readLine();
                    line = reader.readLine();
                    gcRightHudWatch.fillText("Enemies left", 15, 25);
                    for (int enemiesLeftIndex = 0; enemiesLeftIndex < Integer.parseInt(line); enemiesLeftIndex++) {
                        if (enemiesLeftIndex % 2 == 0) {
                            gcRightHudWatch.drawImage(enemyImg, 20, 50 + (enemiesLeftIndex * 30), 40, 40);
                        } else {
                            gcRightHudWatch.drawImage(enemyImg, 100, 50 + ((enemiesLeftIndex - 1) * 30), 40, 40);
                        }
                    }

                    line = reader.readLine();
                    line = reader.readLine();
                    tank.changeTankOrient(line);
                    line = reader.readLine();
                    tank.currentTankPosX = Double.parseDouble(line);
                    line = reader.readLine();
                    tank.currentTankPosY = Double.parseDouble(line);
                    tank.sprite.setPosition(tank.currentTankPosX, tank.currentTankPosY);
                    tank.sprite.render(gcMainWatch);
                    line = reader.readLine();
                    while (!line.equals("/////Enemies/////")) {
                        line = reader.readLine();
                        if (line.equals("/////Enemies/////")) {
                            break;
                        }
                        if (line.equals("Empty")) {
                            line = reader.readLine();               //Enemies
                            break;
                        }

                        String orient = line;
                        if (orient.equals("UP")) {
                            bullet.setImage(bulletUp);
                            bullet.setSize(Bullet.bulletWidth, Bullet.bulletHeight);
                        }
                        if (orient.equals("DOWN")) {
                            bullet.setImage(bulletDown);
                            bullet.setSize(Bullet.bulletWidth, Bullet.bulletHeight);
                        }
                        if (orient.equals("LEFT")) {
                            bullet.setImage(bulletLeft);
                            bullet.setSize(Bullet.bulletHeight, Bullet.bulletWidth);
                        }
                        if (orient.equals("RIGHT")) {
                            bullet.setImage(bulletRight);
                            bullet.setSize(Bullet.bulletHeight, Bullet.bulletWidth);
                        }

                        line = reader.readLine();
                        double posX = Double.parseDouble(line);
                        line = reader.readLine();
                        double posY = Double.parseDouble(line);
                        bullet.setPosition(posX, posY);
                        bullet.render(gcMainWatch);
                    }                                           //Enemies
                    line = reader.readLine();                   //Enemy0
                    if (!line.equals("/////Broken Bricks/////")) {
                        while (!line.equals("/////Broken Bricks/////")) {         //Enemy0

                            line = reader.readLine();                           //orient
                            if (line.equals("/////Broken Bricks/////")) {
                                break;
                            }

                            enemy.changeOrient(line);
                            line = reader.readLine();
                            enemy.currentTankPosX = Double.parseDouble(line);
                            line = reader.readLine();
                            enemy.currentTankPosY = Double.parseDouble(line);
                            enemy.sprite.setPosition(enemy.currentTankPosX, enemy.currentTankPosY);
                            enemy.sprite.setSize(EnemyTank.tankSize);
                            enemy.sprite.render(gcMainWatch);
                            line = reader.readLine();                   //EnemyBullets
                            line = reader.readLine();
                            if (line.equals("Empty")) {
                                line = reader.readLine();
                            } else {
                                while (line.equals("UP") || line.equals("DOWN") || line.equals("LEFT") || line.equals("RIGHT")) {
                                    String orient = line;
                                    if (orient.equals("UP")) {
                                        bullet.setImage(bulletUp);
                                        bullet.setSize(Bullet.bulletWidth, Bullet.bulletHeight);
                                    }
                                    if (orient.equals("DOWN")) {
                                        bullet.setImage(bulletDown);
                                        bullet.setSize(Bullet.bulletWidth, Bullet.bulletHeight);
                                    }
                                    if (orient.equals("LEFT")) {
                                        bullet.setImage(bulletLeft);
                                        bullet.setSize(Bullet.bulletHeight, Bullet.bulletWidth);
                                    }
                                    if (orient.equals("RIGHT")) {
                                        bullet.setImage(bulletRight);
                                        bullet.setSize(Bullet.bulletHeight, Bullet.bulletWidth);
                                    }
                                    line = reader.readLine();
                                    double posX = Double.parseDouble(line);
                                    line = reader.readLine();
                                    double posY = Double.parseDouble(line);
                                    bullet.setPosition(posX, posY);
                                    bullet.render(gcMainWatch);
                                    line = reader.readLine();
                                }
                            }
                        }
                    }
                    //BrokenBricks
                    line = reader.readLine();       //Start ID
                    for (brokenBrickIndex = 0; brokenBrickIndex < brokenBricks.size(); brokenBrickIndex++) {
                        if (line.equals("/////Bricks/////")) {
                            break;
                        }
                        while (Integer.parseInt(line) != brokenBricks.get(brokenBrickIndex).id) {
                            brokenBrickIndex++;

                        }
                        brokenBricks.get(brokenBrickIndex).render(gcMainWatch);
                        line = reader.readLine();
                    }
                    line = reader.readLine();
                    for (brickIndex = 0; brickIndex < bricks.size(); brickIndex++) {
                        if (line.equals("//////")) {
                            break;
                        }
                        while (Integer.parseInt(line) != bricks.get(brickIndex).id) {
                            brickIndex++;
                        }
                        bricks.get(brickIndex).render(gcMainWatch);
                        line = reader.readLine();
                    }

                    for (metalIndex = 0; metalIndex < metals.size(); metalIndex++) {
                        metals.get(metalIndex).render(gcMainWatch);
                    }
                    map.getTower().sprite.render(gcMainWatch);

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Exception catched");
                    replayAnimationTimer.stop();
                    primaryStage.setScene(replayMenu);
                } catch (NullPointerException e) {
                    System.out.println("Exception catched");
                    replayAnimationTimer.stop();
                    primaryStage.setScene(replayMenu);
                }
            }
        };
    }
}