package com.Box;

import java.awt.Rectangle;

public class Obstacle {

    private int length, height, x, y, y2, topHeight, botHeight;
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

    public Obstacle(int x, int y1, int y2, int obstacleVel, int length, int topHeight, int botHeight) {
        this.x = x;
        this.y = y1;
        this.y2 = y2;
        this.obstacleVel = obstacleVel;
        this.length = length;
        this.topHeight = topHeight;
        this.botHeight = botHeight;
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

    public int getY2() { return this.y2; }

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

    public int getTopH() { return this.topHeight; }

    public int getBotH() { return this.botHeight; }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, length, height);
    }
}
