package com.Box.Attack;

import com.Box.Player;
import java.awt.Color;

public class Enemy extends Player {
    private int rotationAngle;
    private Color c;
    private String status;
    private int stunnedStart;

    public Enemy(int y, int x, int velY, int velX, int height, int length) {
        super(y, x, velY, velX, height, length);
        this.c = Color.BLACK;
        this.status = "";
        this.rotationAngle = 0;
    }

    public Color getColor() {
        return this.c;
    }

    public void setColor(Color c) {
        this.c = c;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public int getRotationAngle() {
        return this.rotationAngle;
    }

    public void setRotationAngle(int angle) {
        this.rotationAngle = angle;
    }

    public int getStunnedStart() {
        return this.stunnedStart;
    }

    public void setStunnedStart(int counter) {
        this.stunnedStart = counter;
    }
}
