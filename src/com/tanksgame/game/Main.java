package com.tanksgame.game;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.EnemyTank;
import com.tanksgame.objects.Sprite;
import com.tanksgame.objects.Tank;
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

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Tank tank = map.getTank();
                EnemyTank enemyTank = map.getEnemyTank();
                tank.tankMoveSet(input, gcMain);
                Iterator<Sprite> bricksIter = map.bricksList.iterator();
                Iterator<Sprite> enemyIter = map.enemyTanksList.iterator();
                boolean borderCollision = false;
                if(tank.currentTankPosX < 0 || tank.currentTankPosX > GameMapView.width - Tank.tankSize / GameMapView.tileSize ||
                        tank.currentTankPosY < 0 || tank.currentTankPosY > GameMapView.height - Tank.tankSize / GameMapView.tileSize){
                    borderCollision = true;
                }
                if (borderCollision) {
                    tank.currentTankPosX = posBeforeX;
                    tank.currentTankPosY = posBeforeY;
                }
                while (bricksIter.hasNext()) {
                    Sprite brick = bricksIter.next();
                    if (tank.sprite.intersects(brick)) {
                        tank.currentTankPosX = posBeforeX;
                        tank.currentTankPosY = posBeforeY;
                    }
                }
                while (enemyIter.hasNext()){
                    Sprite enemy = enemyIter.next();
                    if (tank.sprite.intersects(enemy)) {
                        tank.currentTankPosX = posBeforeX;
                        tank.currentTankPosY = posBeforeY;
                    }
                }
                posBeforeX = tank.currentTankPosX;
                posBeforeY = tank.currentTankPosY;
                gcMain.clearRect(0, 0, 720, 720);
                tank.render(gcMain);
                enemyTank.render(gcMain);

                map.getTower().sprite.render(gcMain);
                for (Sprite enemy : map.enemyTanksList){
                    enemy.render(gcMain);
                }

                for (Sprite sprite : map.bricksList) {
                    sprite.render(gcMain);
                }

                String scoreText = "Score: ";
                gcLeftHud.fillText(scoreText, 5,25);

                //gcLeftHud.strokeText(scoreText, 20,20);

            }
        }.start();
    }

}
