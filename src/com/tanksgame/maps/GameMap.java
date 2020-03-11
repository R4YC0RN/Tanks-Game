package com.tanksgame.maps;

import com.tanksgame.hud.HudLeft;
import com.tanksgame.hud.HudRight;
import com.tanksgame.objects.Brick;
import com.tanksgame.objects.EmptyTile;
import com.tanksgame.objects.Tank;
import com.tanksgame.objects.Tower;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class GameMap {
    //private ArrayList<Sprite> elements;
    public static final int width = 15;
    public static final int height= 15;
    public static final int tileSize = 48;
    public static final int hudSizeHalf = 280;

    private MapTexture texture;
    //private MapValues map;

    private AnchorPane tileGroup = new AnchorPane();
    private AnchorPane hudLeftPane = new AnchorPane();
    private AnchorPane hudRightPane = new AnchorPane();

    private EmptyTile emptyTile = new EmptyTile();
    private Brick brick = new Brick();
    private Tower tower = new Tower();
    private Tank tank = new Tank();

    public void init(){
        texture = new MapTexture();
    }

    public Parent createMap(){
        AnchorPane root = new AnchorPane();
        root.setPrefSize(width * tileSize + 2 * hudSizeHalf, height * tileSize);
        AnchorPane.setLeftAnchor(tileGroup, Double.valueOf(hudSizeHalf));
        AnchorPane.setLeftAnchor(hudLeftPane, 0.0);
        AnchorPane.setLeftAnchor(hudRightPane, Double.valueOf(hudSizeHalf + (tileSize * width)));
        root.getChildren().addAll(tileGroup, hudLeftPane);
        root.getChildren().addAll(hudRightPane);

        drawEmptyBricks();
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
        //int[][]brickPos = brick.brickPos;
        for(int i = 0; i < brick.getNumOfBricks(); i++){
            brick = new Brick();
            brick.setTranslateX(brick.brickPos[i][0] * tileSize);
            brick.setTranslateY(brick.brickPos[i][1] * tileSize);
            tileGroup.getChildren().addAll(brick);
        }
    }

    private void spawnTower(){
        tower = new Tower();
        tower.setTranslateX(tower.towerPos[0] * tileSize);
        tower.setTranslateY(tower.towerPos[1] * tileSize);
        tileGroup.getChildren().add(tower);
    }

    private void spawnTank(){
        tank.setTranslateX(tank.tankStartPos[0] * tileSize);
        tank.setTranslateY(tank.tankStartPos[1] * tileSize);
        tileGroup.getChildren().add(tank);
    }
}
