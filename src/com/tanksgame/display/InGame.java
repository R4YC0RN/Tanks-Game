package com.tanksgame.display;

import com.tanksgame.maps.GameMap;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.awt.*;

public class InGame {
    private int width, height;
    private Scene myScene;
    private GraphicsContext gc;

    private GameMap map;

    public Scene init(int width, int height){
        this.width = width;
        this.height = height;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        map = new GameMap();

        gc = initGraphicsContext(root);
        myScene = new Scene(root, width, height, Color.BLACK);

        return myScene;

    }

    private GraphicsContext initGraphicsContext(BorderPane root){
        Canvas canvas = new Canvas(width, height);
        canvas.setStyle("-fx-background-color: black;");
        root.setCenter(canvas);
        return canvas.getGraphicsContext2D();
    }

}
