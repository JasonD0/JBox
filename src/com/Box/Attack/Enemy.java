package com.Box.Attack;

import com.Box.Player;
import java.awt.Color;

public class Enemy extends Player {
    private int rotationAngle;
    private Color c;
    private String status;

    public Enemy(int y, int x, int velY, int velX, int height, int length) {
        super(y, x, velY, velX, height, length);
        this.c = Color.BLACK;
        this.status = "";
    }

    public void prepareRollAttack(Player p) {
        this.rotationAngle = 10;
        this.c = Color.pink;
        this.status = "CHARGING...";
    }

    public Color getColor() {
        return this.c;
    }

    public String getStatus() {
        return this.status;
    }

    public void reset() {
        this.c = Color.BLACK;
        this.status = "";
    }
}
