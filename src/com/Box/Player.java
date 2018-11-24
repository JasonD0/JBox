package com.Box;

import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Color;

public class Player extends JLabel{

    private int playerLength;
    private int playerHeight;
    private int x, y, velY;

    public Player(int y, int x, int velY, int height, int length) {
        this.y = y;
        this.x = x;
        this.velY = velY;
        this.playerHeight = height;
        this.playerLength = length;
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, playerLength, playerHeight);
    }

    public int getPlayerLength() {
        return playerLength;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }

    public void setPlayerHeight(int h) { this.playerHeight = h; }

    public int getXOrd() {
        return x;
    }

    public int getYOrd() {
        return y;
    }

    public int getVelY() { return this.velY; }

    public void setYOrd(int y) {
        this.y = y;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }
}
