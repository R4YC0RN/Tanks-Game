package com.tanksgame.maps;

import com.tanksgame.objects.*;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameMapView {

    public static final int[][] brickPos =
            {
                    {1, 0}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {3, 3}, {3, 4},
                    {5, 2}, {6, 2}, {8, 2}, {9, 2}, {11, 3}, {11, 4},
                    {13, 0}, {13, 1}, {13, 2}, {13, 3}, {13, 4}, {6, 5}, {8, 5}, {2, 7}, {3, 7}, {4, 7},
                    {6, 9}, {6, 10}, {8, 9}, {8, 10}, {10, 7}, {11, 7}, {12, 7},
                    {1, 10}, {1, 11}, {1, 12}, {1, 13}, {3, 10}, {3, 11}, {3, 12}, {3, 13},
                    {11, 10}, {11, 11}, {11, 12}, {11, 13}, {13, 10}, {13, 11}, {13, 12}, {13, 13},
                    {6, 14}, {6, 13}, {7, 13}, {8, 13}, {8, 14}
            };

    public static final int[][] brickPos2 =
            {
                    {1, 2}, {2, 2}, {5, 2}, {6, 2}, {8, 2}, {9, 2}, {12, 2}, {13, 2},
                    {2, 3}, {5, 3}, {9, 3}, {12, 3}, {2, 4}, {5, 4}, {9, 4}, {12, 4}, {2, 5}, {5, 5}, {9, 5}, {12, 5},
                    {2, 6}, {3, 6}, {4, 6}, {5, 6}, {9, 6}, {10, 6}, {11, 6}, {12, 6},
                    {13, 8}, {12, 9}, {2, 9}, {1, 10}, {5, 10}, {6, 10}, {8, 10}, {9, 10},
                    {1, 12}, {2, 12}, {3, 12}, {11, 12}, {12, 12}, {13, 12},
                    {6, 14}, {6, 13}, {7, 13}, {8, 13}, {8, 14}
            };

    public static final int[][] metalPos =
            {
                    {3, 0}, {3, 1}, {3, 2}, {11, 0}, {11, 1}, {11, 2}, {0, 7}, {6, 7}, {6, 8}, {7, 8},
                    {8, 7}, {8, 8}, {14, 7}
            };

    public static final int[][] metalPos2 =
            {
                    {3,2}, {4,2}, {10,2}, {11,2}, {7,10}
            };

    public static final int[][] enemyStartPos = {{6, 0}, {8, 0}, {4, 0}, {10, 0}};

    public static final int[] tankStartPos = {5, 14};

    public int level;

    public static final double width = 15;
    public static final double height = 15;
    public static final double tileSize = 48;
    public static final double hudSizeHalf = 280;

    public static final int totalNumOfEnemy = 14;
    public static final int maxEnemyOnField = 6;

    public ArrayList<Sprite> bricksList = new ArrayList<>();
    public ArrayList<Sprite> metalList = new ArrayList<>();
    public ArrayList<EnemyTank> enemyTanksList = new ArrayList<>();
    public ArrayList<Sprite> brokenBricksList = new ArrayList<>();
    public ArrayList<Sprite> spawnPoints = new ArrayList<>();

    private AnchorPane root;

    private Brick brick;
    private Brick brokenBrick;
    private Tower tower;
    private Tank tank;
    private EnemyTank enemyTank;
    private Metal metal;

    private Canvas canvas;
    private Canvas leftHudCanvas;
    private Canvas rightHudCanvas;

    private GraphicsContext gcMain;
    private GraphicsContext gcLeftHud;
    private GraphicsContext gcRightHud;

    public GameMapView() {
        brick = new Brick();
        brokenBrick = new Brick();
        brokenBrick.sprite.setImage(brokenBrick.getBrokenBrickImg());
        tower = new Tower();
        tank = new Tank();
        enemyTank = new EnemyTank();
        metal = new Metal();
        level = 1;
        canvas = new Canvas(720, 720);
        leftHudCanvas = new Canvas(GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
        rightHudCanvas = new Canvas(GameMapView.hudSizeHalf, GameMapView.tileSize * GameMapView.height);
        root = new AnchorPane();
        root.setPrefSize(width * tileSize + 2 * hudSizeHalf, height * tileSize);


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
        root.getChildren().addAll(gameZone, leftHudPane, rightHudPane);

        gcMain = canvas.getGraphicsContext2D();
        gcLeftHud = leftHudCanvas.getGraphicsContext2D();
        gcRightHud = rightHudCanvas.getGraphicsContext2D();

        gcLeftHud.setStroke(Color.BROWN);
        gcLeftHud.setLineWidth(2.5);
        gcLeftHud.strokeRect(0, 0, hudSizeHalf, tileSize * height);
        gcRightHud.setStroke(Color.BROWN);
        gcRightHud.setLineWidth(2.5);
        gcRightHud.strokeRect(0, 0, hudSizeHalf, tileSize * height);
    }

    public Parent createMap() {
        drawBrokenBricks();
        drawBricks();
        drawMetal();
        spawnTower();
        createSpawnPoints();
        spawnTank();
        spawnEnemyTank();
        return root;
    }

    public void drawBricks() {
        bricksList.clear();
        switch(level){
            case 1:
                for (int bricksIndex = 0; bricksIndex < brickPos.length; bricksIndex++) {
                brick = new Brick();
                brick.sprite.setImage(brick.getBrickImg());
                brick.sprite.setPosition(brickPos[bricksIndex][0], brickPos[bricksIndex][1]);
                bricksList.add(brick.sprite);
            }
                break;
            case 2:
                for(int bricksIndex = 0; bricksIndex < brickPos2.length; bricksIndex++){
                    brick = new Brick();
                    brick.sprite.setImage(brick.getBrickImg());
                    brick.sprite.setPosition(brickPos2[bricksIndex][0], brickPos2[bricksIndex][1]);
                    bricksList.add(brick.sprite);
                }
                break;
        }

    }

    public void drawBrokenBricks() {
        brokenBricksList.clear();
        switch(level){
            case 1:
                for (int brokenBricksIndex = 0; brokenBricksIndex < brickPos.length; brokenBricksIndex++) {
                brokenBrick = new Brick();
                brokenBrick.sprite.setImage(brokenBrick.getBrokenBrickImg());
                brokenBrick.sprite.setPosition(brickPos[brokenBricksIndex][0], brickPos[brokenBricksIndex][1]);
                brokenBricksList.add(brokenBrick.sprite);
            }
                break;
            case 2:
                for (int brokenBricksIndex = 0; brokenBricksIndex < brickPos2.length; brokenBricksIndex++) {
                    brokenBrick = new Brick();
                    brokenBrick.sprite.setImage(brokenBrick.getBrokenBrickImg());
                    brokenBrick.sprite.setPosition(brickPos2[brokenBricksIndex][0], brickPos2[brokenBricksIndex][1]);
                    brokenBricksList.add(brokenBrick.sprite);
                }
                break;
        }
    }

    public void drawMetal() {
        metalList.clear();
        switch(level){
            case 1:
                for (int metalIndex = 0; metalIndex < metalPos.length; metalIndex++) {
                    metal = new Metal();
                    metal.sprite.setImage(metal.getMetalImg());
                    metal.sprite.setPosition(metalPos[metalIndex][0], metalPos[metalIndex][1]);
                    metalList.add(metal.sprite);
                }
                break;
            case 2:
                for (int metalIndex = 0; metalIndex < metalPos2.length; metalIndex++) {
                    metal = new Metal();
                    metal.sprite.setImage(metal.getMetalImg());
                    metal.sprite.setPosition(metalPos2[metalIndex][0], metalPos2[metalIndex][1]);
                    metalList.add(metal.sprite);
                }
                break;
        }

    }

    private void spawnTower() {
        tower.sprite.render(gcMain);
    }

    private void createSpawnPoints() {
        spawnPoints.clear();
        for (int spawnIndex = 0; spawnIndex < enemyStartPos.length; spawnIndex++) {
            Sprite point = new Sprite();
            point.setPosition(enemyStartPos[spawnIndex][0], enemyStartPos[spawnIndex][1]);
            point.setSize(tileSize);
            spawnPoints.add(point);
        }
    }

    private void spawnTank() {
        tank.render(gcMain);
    }

    private void spawnEnemyTank() {
        enemyTanksList.clear();
        for (int enemyTankIndex = 0; enemyTankIndex < enemyStartPos.length; enemyTankIndex++) {
            enemyTank = new EnemyTank(enemyStartPos[enemyTankIndex][0], enemyStartPos[enemyTankIndex][1]);
            enemyTank.sprite.setImage(enemyTank.getTank1DownImg());
            enemyTank.sprite.setPosition(enemyStartPos[enemyTankIndex][0], enemyStartPos[enemyTankIndex][1]);
            enemyTank.sprite.setSize(EnemyTank.tankSize);
            enemyTanksList.add(enemyTank);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Canvas getLeftHudCanvas() {
        return leftHudCanvas;
    }

    public Canvas getRightHudCanvas() {
        return rightHudCanvas;
    }

    public Tank getTank() {
        return tank;
    }

    public Tower getTower() {
        return tower;
    }

    public EnemyTank getEnemyTank() {
        return enemyTank;
    }

    public ArrayList<Sprite> getBricksList() {
        return bricksList;
    }

    public AnchorPane getRoot() {
        return root;
    }
}
