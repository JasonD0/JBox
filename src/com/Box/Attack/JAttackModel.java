package com.Box.Attack;

import java.awt.Color;

public class JAttackModel {
    public final static Color AQUA = new Color(127, 255, 212);
    public final static Color LIGHT_GRAY = new Color(51, 51, 51);
    public final static Color DARK_GRAY = new Color(45, 45, 45);
    public final static int GAME_LENGTH = 1250;
    public final static int GAME_HEIGHT = 700;
    public final static int PLATFORM_YORD = 600;
    public final static int JUMP = 1;
    public final static int TRIPLE_JUMP = 2;
    public final static int HYPER = 3;
    public final static int ROLL = 4;
    public final static int HIGH_JUMP = 5;
    private JAttackPlayer p;
    private Enemy e;
    private int counter;

    public JAttackModel(JAttackPlayer p, Enemy e) {
        this.p = p;
        this.e = e;
        this.counter = 0;
    }

    public int getCounter() {
        return this.counter;
    }

    public void updateCounter() {
        this.counter ++;
    }

    public JAttackPlayer getPlayer() {
        return this.p;
    }

    public Enemy getEnemy() {
        return this.e;
    }

}
