package com.Box.Attack;

import com.Box.Player;

public class JAttackPlayer extends Player {
    private int angle;
    private String lastXMove;
    private int speedX;

    public JAttackPlayer(int y, int x, int velY, int velX, int height, int length) {
        super(y, x, velY, velX, height, length);
        this.angle = 10;
        this.lastXMove = "right";
        this.speedX = 7;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return this.angle;
    }

    public void setLastXMove(String move) {
        this.lastXMove = move;
    }

    public String getLastXMove() {
        return this.lastXMove;
    }

    public int getSpeedX() {
        return this.speedX;
    }

    public void setSpeedX(int v) {
        this.speedX = v;
    }
}
