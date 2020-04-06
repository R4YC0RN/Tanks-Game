package com.tanksgame.objects;

import com.tanksgame.maps.GameMapView;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Sprite {
    private Image image;
    private double positionX;
    private double positionY;
    //private double size;
    private double width;
    private double height;

    public Sprite() {
        //size = GameMapView.tileSize;
        width = GameMapView.tileSize;
        height = GameMapView.tileSize;
        positionX = 0;
        positionY = 0;
    }

    public Sprite(double positionX, double positionY, double size){
        //this.size = size;
        this.width = size;
        this.height = size;
        this.positionX = positionX * GameMapView.tileSize;
        this.positionY = positionY * GameMapView.tileSize;
    }

    public Sprite(double positionX, double positionY, double width, double height){
        this.width = width;
        this.height = height;
        this.positionX = positionX * GameMapView.tileSize;
        this.positionY = positionY * GameMapView.tileSize;
    }

    public void setSize(double size) {
        //this.size = size;
        this.width = size;
        this.height = size;
    }

    public void setSize(double width, double height){
        this.width = width;
        this.height = height;
    }

    public void setPosition(double x, double y) {
        //positionX = x * size;
        //positionY = y * size;
        positionX = x * GameMapView.tileSize;
        positionY = y * GameMapView.tileSize;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY, width, height);
    }

    public void render(GraphicsContext gc, Image image, double positionX, double positionY, double size){
        this.image = image;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = size;
        this.height = size;
        gc.drawImage(image, positionX, positionY, size, size);
    }

    public void render(GraphicsContext gc, Image image, double positionX, double positionY, double width, double height){
        this.image = image;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        gc.drawImage(image, positionX, positionY, width, height);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    public Rectangle2D getBoundary(double positionX, double positionY, double size){
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = size;
        this.height = size;
        return new Rectangle2D(positionX, positionY, size, size);
    }

    public Rectangle2D getBoundary(double positionX, double positionY, double width, double height){
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        return new Rectangle2D(positionX, positionY, width, height);
    }

    public boolean intersects(Sprite sprite) {
        return sprite.getBoundary().intersects(this.getBoundary());
    }

    public double getX(){
        return positionX;
    }

    public double getY(){
        return positionY;
    }
}
