package com.Box.Attack;

import com.Box.Player;

public class JAttackPlayer extends Player {
    private int angle;
    private String lastXMove;
    private String status;
    private int speedX;
    private int stunnedStart;
    private int health;

    public JAttackPlayer(int y, int x, int velY, int velX, int height, int length) {
        super(y, x, velY, velX, height, length);
        this.angle = 0;
        this.lastXMove = "right";
        this.speedX = 7;
        this.status = "";
        this.health = 100;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public int getStunnedStart() {
        return this.stunnedStart;
    }

    public void setStunnedStart(int counter) {
        this.stunnedStart = counter;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}

