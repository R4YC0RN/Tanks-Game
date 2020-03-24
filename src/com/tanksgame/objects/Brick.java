package com.tanksgame.objects;

import javafx.scene.image.Image;

public class Brick {
    private Image brickImg = new Image("assets/images/brick.jpg");
    public Sprite sprite;

    public Brick() {
        sprite = new Sprite();
        sprite.setImage(brickImg);
    }

    public Image getBrickImg() {
        return brickImg;
    }

}
