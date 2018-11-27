package com.Box.Attack;

import com.Box.Player;
import java.awt.Color;

public class Enemy extends Player {
    private int rotationAngle;
    private int rotationIncrease;
    private Color c;
    private String status;

    public Enemy(int y, int x, int velY, int velX, int height, int length) {
        super(y, x, velY, velX, height, length);
        this.c = Color.BLACK;
        this.status = "";
        this.rotationAngle = 0;
        this.rotationIncrease = -3;
    }

    public void prepareRollAttack(Player p) {
        this.rotationIncrease = 3;
        this.c = Color.pink;
        this.status = "CHARGING...";
    }

    public Color getColor() {
        return this.c;
    }

    public void setColor(Color c) {
        this.c = c;
    }

    public String getStatus() {
        return this.status;
    }

    public void reset() {
        this.c = Color.BLACK;
        this.status = "";
    }

    public int getRotationAngle() {
        return this.rotationAngle;
    }

    public void setRotationAngle() {
        this.rotationAngle = (this.rotationAngle + this.rotationIncrease) % 360;
    }
}
