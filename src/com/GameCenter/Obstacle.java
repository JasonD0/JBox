package com.GameCenter;

import java.awt.Rectangle;

public class Obstacle {

    private int length, height, x, y;
    private boolean inFrame;
    private int obstacleVel;

    public Obstacle(int x, int y, int obstacleVel, int length, int height) {
        this.x = x;
        this.y = y;
        this.obstacleVel = obstacleVel;
        this.length = length;
        this.height = height;
        inFrame = true;
    }

    public void move() {
        if (x + length < 0) {
            inFrame = false;
        }
        x -= obstacleVel;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLength() {
        return this.length;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean inFrame() {
        return this.inFrame;
    }

    public void setVel(int speed) {
        obstacleVel = speed;
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, length, height);
    }
}