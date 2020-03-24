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
                    {1, 0}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {3, 0}, {3, 1}, {3, 2}, {3, 3}, {3, 4},
                    {5, 2}, {6, 2}, {8, 2}, {9, 2}, {11, 0}, {11, 1}, {11, 2}, {11, 3}, {11, 4},
                    {13, 0}, {13, 1}, {13, 2}, {13, 3}, {13, 4}, {6, 5}, {8, 5}, {0, 7}, {2, 7}, {3, 7}, {4, 7},
                    {6, 7}, {6, 8}, {6, 9}, {6, 10}, {7, 8}, {8, 7}, {8, 8}, {8, 9}, {8, 10}, {10, 7}, {11, 7}, {12, 7}, {14, 7},
                    {1, 10}, {1, 11}, {1, 12}, {1, 13}, {3, 10}, {3, 11}, {3, 12}, {3, 13},
                    {11, 10}, {11, 11}, {11, 12}, {11, 13}, {13, 10}, {13, 11}, {13, 12}, {13, 13},
                    {6, 14}, {6, 13}, {7, 13}, {8, 13}, {8, 14}
            };

    public static final int[][] enemyStartPos = {{6, 0}, {8, 0}};

    public static final double width = 15;
    public static final double height = 15;
    public static final double tileSize = 48;
    public static final double hudSizeHalf = 280;
    private int numOfBricks;
    private int numOfEnemyTanks;

    public ArrayList<Sprite> bricksList = new ArrayList<>();
    public ArrayList<Sprite> enemyTanksList = new ArrayList<>();

    private AnchorPane root;

    private Brick brick;
    private Tower tower;
    private Tank tank;
    private EnemyTank enemyTank;

    private Canvas canvas;
    private Canvas leftHudCanvas;
    private Canvas rightHudCanvas;

    private GraphicsContext gcMain;
    private GraphicsContext gcLeftHud;
    private GraphicsContext gcRightHud;

    public GameMapView() {
        numOfBricks = brickPos.length;
        numOfEnemyTanks = enemyStartPos.length;

        brick = new Brick();
        tower = new Tower();
        tank = new Tank();
        enemyTank = new EnemyTank();
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
        gcLeftHud.strokeRect(0,0, hudSizeHalf, tileSize * height);
        gcRightHud.setStroke(Color.BROWN);
        gcRightHud.setLineWidth(2.5);
        gcRightHud.strokeRect(0,0, hudSizeHalf, tileSize * height);
    }

    public Parent createMap() {
        drawBricks();
        spawnTower();
        spawnTank();
        spawnEnemyTank();
        return root;
    }

    private void drawBricks() {
        for (int bricksIndex = 0; bricksIndex < numOfBricks; bricksIndex++) {
            brick = new Brick();
            brick.sprite.setImage(brick.getBrickImg());
            brick.sprite.setPosition(brickPos[bricksIndex][0], brickPos[bricksIndex][1]);
            bricksList.add(brick.sprite);
        }
    }

    private void spawnTower() {
        tower.sprite.render(gcMain);
    }

    private void spawnTank() {
        tank.render(gcMain);
    }

    private void spawnEnemyTank() {
        for(int enemyTankIndex = 0; enemyTankIndex < numOfEnemyTanks; enemyTankIndex++){
            enemyTank = new EnemyTank(enemyStartPos[enemyTankIndex][0], enemyStartPos[enemyTankIndex][1]);
            enemyTank.sprite.setImage(enemyTank.getTank1DownImg());
            enemyTank.sprite.setPosition(enemyStartPos[enemyTankIndex][0], enemyStartPos[enemyTankIndex][1]);
            enemyTank.sprite.setSize(EnemyTank.tankSize);
            enemyTanksList.add(enemyTank.sprite);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Canvas getLeftHudCanvas(){
        return leftHudCanvas;
    }

    public Canvas getRightHudCanvas(){
        return rightHudCanvas;
    }

    public Tank getTank() {
        return tank;
    }

    public Tower getTower() {
        return tower;
    }

    public EnemyTank getEnemyTank(){
        return enemyTank;
    }

    public ArrayList<Sprite> getBricksList() {
        return bricksList;
    }

    public AnchorPane getRoot(){
        return root;
    }
}
