package com.tanksgame.game;

import com.tanksgame.maps.GameMap;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    VBox root = new VBox();
    GameMap map = new GameMap();
    Canvas filedCanvas = new Canvas();
    private GraphicsContext gc;
    public double currentTankPosX = map.getTank().tankStartPos[0];
    public double currentTankPosY = map.getTank().tankStartPos[1];
    public double speed = 0.06;


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

    public void showMainMenu(Stage primaryStage){

        root.setSpacing(10);

        Image startBgImg = new Image("Assets/Images/startBackground.jpg");
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
        loadBtn.setStyle("-fx-font-size: 28 arial;");
        root.getChildren().add(loadBtn);

        Button newBtn = new Button("New game");
        newBtn.setPrefSize(200, 70);
        newBtn.setStyle("-fx-font-size: 24 arial");
        newBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameStart(primaryStage);
            }
        });
        root.getChildren().add(newBtn);

        Button exitBtn = new Button("Exit");
        exitBtn.setPrefSize(200, 70);
        exitBtn.setStyle("-fx-font-size: 24 arial");
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
        root.getChildren().add(exitBtn);
    }


    public void gameStart(Stage primaryStage) {
        Scene scene = new Scene(map.createMap(), 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
        filedCanvas = map.getCanvas();
        gc = filedCanvas.getGraphicsContext2D();
        ArrayList<String> input = new ArrayList<>();


        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                if (!input.contains(code)) {
                    input.add(code);

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

        new AnimationTimer(){
            @Override
            public void handle(long now) {

//                if(input.contains("UP") && !input.contains("LEFT") && !input.contains("RIGHT")) {
//                    gc.clearRect(currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                    currentTankPosY -= speed;
//                    gc.drawImage(map.getTank().getTank1UpImg(), currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                }
//
//                if(input.contains("DOWN") && !input.contains("LEFT") && !input.contains("RIGHT")) {
//                    gc.clearRect(currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                    currentTankPosY += speed;
//                    gc.drawImage(map.getTank().getTank1DownImg(), currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                }
//
//                if(input.contains("LEFT") && !input.contains("RIGHT")) {
//                    gc.clearRect(currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                    currentTankPosX -= speed;
//                    gc.drawImage(map.getTank().getTank1LeftImg(), currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                }
//
//                if(input.contains("RIGHT") && !input.contains("LEFT")) {
//                    gc.clearRect(currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                    currentTankPosX += speed;
//                    gc.drawImage(map.getTank().getTank1RightImg(), currentTankPosX * map.tileSize,
//                            currentTankPosY * map.tileSize, map.tileSize, map.tileSize);
//                }
                map.getTank().tankMoveSet(input, gc);
            }
        }.start();
    }
}
