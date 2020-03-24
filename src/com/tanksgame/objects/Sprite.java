package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Sprite {
    private Image image;
    private double positionX;
    private double positionY;
    private double size;

    public Sprite() {
        size = GameMapView.tileSize;
        positionX = 0;
        positionY = 0;
    }

    public Sprite(double positionX, double positionY, double size){
        this.size = size;
        this.positionX = positionX * size;
        this.positionY = positionY * size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setPosition(double x, double y) {
        positionX = x * size;
        positionY = y * size;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY, size, size);
    }

    public void render(GraphicsContext gc, Image image, double positionX, double positionY, double size){
        this.image = image;
        this.positionX = positionX;
        this.positionY = positionY;
        this.size = size;
        gc.drawImage(image, positionX, positionY, size, size);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, size, size);
    }

    public Rectangle2D getBoundary(double positionX, double positionY, double size){
        this.positionX = positionX;
        this.positionY = positionY;
        this.size = size;
        return new Rectangle2D(positionX, positionY, size, size);
    }

    public boolean intersects(Sprite sprite) {
        return sprite.getBoundary().intersects(this.getBoundary());
    }
}
