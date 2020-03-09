package com.tanksgame.maps;

import com.tanksgame.objects.Brick;
import com.tanksgame.objects.EmptyTile;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class GameMap {
    //private ArrayList<Sprite> elements;
    public static final int width = 24;
    public static final int height= 24;
    public static final int tileSize = 30;
    public static final int hudSizeHalf = 280;

    private MapTexture texture;
    //private MapValues map;

    private Group tileGroup = new Group();
    private Group brickGroup = new Group();


    public void init(){
        texture = new MapTexture();
    }

    public Parent createMap(){
        StackPane root = new StackPane();
        root.setPrefSize(width * tileSize + 2 * hudSizeHalf, height * tileSize);
        AnchorPane.setTopAnchor(tileGroup, 0.0);
        root.getChildren().addAll(tileGroup);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){

                EmptyTile tile = new EmptyTile(tileSize, tileSize);
                tile.setTranslateX(j * tileSize);
                tile.setTranslateY(i * tileSize);
                tileGroup.getChildren().add(tile);
            }
        }

//        root.getChildren().add(brickGroup);
//
//        MapTexture mapTxt = new MapTexture();
//        //int length = mapTxt.getNumOfBricks();
//        Brick brick = new Brick(tileSize,tileSize);
//        int length = brick.getNumOfBricks();
//        int[][] brickPos = brick.brickPos;
//        for(int i = 0; i < height; i++){
//            for(int j = 0; j < width; j++){
//                for(int k = 0; k < length; k++){
//                    if(brickPos[k][1] == i){
//                        if(brickPos[k][0] == j){
//                            brick = new Brick(tileSize,tileSize);
//                            brick.setTranslateX(j * tileSize);
//                            brick.setTranslateY(i * tileSize);
//                            brickGroup.getChildren().add(brick);
//                        }else{
//                            EmptyTile tile = new EmptyTile(tileSize, tileSize);
//                            tile.setTranslateX(j * tileSize);
//                            tile.setTranslateY(i * tileSize);
//                            tileGroup.getChildren().add(tile);
//                        }
//                    }else{
//                        EmptyTile tile = new EmptyTile(tileSize, tileSize);
//                        tile.setTranslateX(j * tileSize);
//                        tile.setTranslateY(i * tileSize);
//                        tileGroup.getChildren().add(tile);
//                    }
//                }
//            }
//        }

        Brick brick = new Brick(tileSize, tileSize);
        int[][]brickPos = brick.brickPos;
        for(int i = 0; i < brick.getNumOfBricks(); i++){
            brick = new Brick(tileSize, tileSize);
            brick.setTranslateX(brickPos[i][0] * tileSize);
            brick.setTranslateY(brickPos[i][1] * tileSize);
            tileGroup.getChildren().addAll(brick);
        }


        return root;
    }
}
