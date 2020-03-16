package com.tanksgame.maps;

import com.tanksgame.hud.HudLeft;
import com.tanksgame.hud.HudRight;
import com.tanksgame.objects.Brick;
import com.tanksgame.objects.EmptyTile;
import com.tanksgame.objects.Tank;
import com.tanksgame.objects.Tower;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sun.plugin2.message.GetAppletMessage;


public class GameMap {
    //private ArrayList<Sprite> elements;
    public static final int width = 15;
    public static final int height= 15;
    public static final int tileSize = 48;
    public static final int hudSizeHalf = 280;

    private MapTexture texture;
    //private MapValues map;

    private AnchorPane tileGroup;
    private AnchorPane hudLeftPane;
    private AnchorPane hudRightPane;
    private AnchorPane root;

    private EmptyTile emptyTile;
    private Brick brick;
    private Tower tower;
    private Tank tank;

    private Canvas canvas;
    private GraphicsContext gc;

    public GameMap(){
        //tileGroup = new AnchorPane();
        hudLeftPane = new AnchorPane();
        hudRightPane = new AnchorPane();

        emptyTile = new EmptyTile();
        brick = new Brick();
        tower = new Tower();
        tank = new Tank();
        canvas = new Canvas(720, 720);
        root = new AnchorPane();
        root.setPrefSize(width * tileSize + 2 * hudSizeHalf, height * tileSize);
        StackPane gameZone = new StackPane();
        AnchorPane.setLeftAnchor(gameZone, Double.valueOf(hudSizeHalf));
        AnchorPane.setLeftAnchor(hudLeftPane, 0.0);
        AnchorPane.setLeftAnchor(hudRightPane, Double.valueOf(hudSizeHalf + (tileSize * width)));
        gameZone.getChildren().add(canvas);
        gameZone.setStyle("-fx-background-color: black;");
        root.getChildren().addAll(gameZone, hudLeftPane);
        root.getChildren().addAll(hudRightPane);

        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
    }


    public void init(){
        texture = new MapTexture();
    }

    public Parent createMap(){
        drawBricks();
        spawnTower();
        spawnTank();
        HudLeft hudLeft = new HudLeft();
        hudLeftPane.getChildren().add(hudLeft);
        HudRight hudRight = new HudRight();
        hudRightPane.getChildren().add(hudRight);

        return root;
    }

    private void drawEmptyBricks(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                emptyTile = new EmptyTile();
                emptyTile.setTranslateX(j * tileSize);
                emptyTile.setTranslateY(i * tileSize);
                tileGroup.getChildren().add(emptyTile);
            }
        }
    }

    private void drawBricks(){
        for(int i = 0; i < brick.getNumOfBricks(); i++){
            gc.drawImage(brick.getBrickImg(), brick.brickPos[i][0] * tileSize, brick.brickPos[i][1] * tileSize,
                    tileSize, tileSize);
        }
    }

    private void spawnTower(){
        gc.drawImage(tower.getTowerImg(), tower.towerPos[0] * tileSize, tower.towerPos[1] * tileSize,
                tileSize, tileSize);
    }

    private void spawnTank(){
        gc.drawImage(tank.getTank1UpImg(),tank.tankStartPos[0] * tileSize, tank.tankStartPos[1] * tileSize,
                tileSize, tileSize);
    }

    public Canvas getCanvas(){
        return canvas;
    }

    public Tank getTank(){
        return tank;
    }
}
