package com.tanksgame.game;

import com.tanksgame.maps.GameMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Tanks");

        VBox root = new VBox();
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

        primaryStage.setScene(new Scene(root, 1280, 720));


        //root.getChildren().add(backgroundView);

        primaryStage.show();
    }

    public void gameStart(Stage primaryStage) {
        GameMap map = new GameMap();
        //AnchorPane root = new AnchorPane(map.createMap());
        //AnchorPane.setTopAnchor(map.createMap(), 40.0);
        Scene scene = new Scene(map.createMap());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
