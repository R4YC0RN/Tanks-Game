package com.tanksgame.objects;

import javafx.scene.image.Image;

public class Metal {
    private Image metalImg = new Image("assets/images/metalBlock.png");
    public Sprite sprite;

    public Metal() {
        sprite = new Sprite();
        sprite.setImage(metalImg);
    }

    public Image getMetalImg(){
        return metalImg;
    }
}
