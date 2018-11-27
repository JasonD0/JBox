package com.Box.Attack;

public class PreAttack {
    private int lastEnemyXOrd, lastEnemyYOrd;
    private int lastPlayerXOrd, lastPlayerYOrd;

    public void setLastEnemyXOrd(int x) {
        this.lastEnemyXOrd = x;
    }

    public void setLastPlayerXOrd(int x) {
        this.lastPlayerXOrd = x;
    }

    public void setLastEnemyYOrd(int y) {
        this.lastEnemyYOrd = y;
    }

    public void setLastPlayerYOrd(int y) {
        this.lastPlayerYOrd = y;
    }

    public int getLastEnemyXOrd() {
        return this.lastEnemyXOrd;
    }

    public int getLastPlayerXOrd() {
        return this.lastPlayerXOrd;
    }

    public int getLastEnemyYOrd() {
        return this.lastEnemyYOrd;
    }

    public int getLastPlayerYOrd() {
        return this.lastPlayerYOrd;
    }
}
