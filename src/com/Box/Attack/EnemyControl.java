package com.Box.Attack;

import java.awt.Graphics;
import java.util.Random;

public class EnemyControl {
    private PreAttack enemyPA;
    private JAttackModel jam;
    private JAttackView jav;
    private JAttackPlayer p;
    private Enemy e;
    private Random rand;
    private int currentAttack;
    private boolean jumpAttackLock;

    public EnemyControl(JAttackModel jam, JAttackView jav) {
        this.enemyPA = new PreAttack();
        this.jam = jam;
        this.jav = jav;
        this.currentAttack = 0;
        this.p = jam.getPlayer();
        this.e = jam.getEnemy();
        this.rand = new Random();
        this.jumpAttackLock = false;
    }

    public void setEnemyLastOrdinates() {
        enemyPA.setLastPlayerXOrd(p.getXOrd());
        enemyPA.setLastPlayerYOrd(p.getYOrd());
        enemyPA.setLastEnemyXOrd(e.getXOrd());
        enemyPA.setLastEnemyYOrd(e.getYOrd());
    }

    public void enemyAction(Graphics g, boolean collision) {
        int direction = (enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2 < enemyPA.getLastPlayerXOrd()) ? 1 : -1;
        if (currentAttack == jam.JUMP) jumpAttack(g, direction);
        //else if (currentAttack == jam.TRIPLE_JUMP);
        else if (currentAttack == jam.ROLL) rollAttack(g, direction);
        /*else if (currentAttack == jam.RAIN);*/
        else jav.drawEnemy(g, e);
    }

    private void jumpAttack(Graphics g, int direction) {
        jav.drawEnemy(g, e);
        if (jumpAttackLock) return;
        int maxHeight = 150;
        // hit a wall or is at max height of jump
        if (e.getYOrd() <= maxHeight /*|| hit wall*/) {
            e.setVelX(0);
            e.setVelY(20);
            jumpAttackLock = true;

        // moves diagonally above player
        } else {
            // y = mx + b     end pt is enemy position before attacking     start is y=150 (650 for normal axis) x = player position known by enemy at time of attack
            int startX = enemyPA.getLastPlayerXOrd() + p.getPlayerLength() / 2;
            double gradient = (double)(enemyPA.getLastEnemyYOrd() - 650) / (double)(enemyPA.getLastEnemyXOrd() - startX);
            double b = 650 - gradient*startX;
            double dY = gradient * e.getXOrd() + b;
            int y = 400 - ((int) dY - enemyPA.getLastEnemyYOrd());
            e.setYOrd(y);
            e.setVelY(0);
            e.setVelX(10 * direction);
        }
    }

    private void rollAttack(Graphics g, int direction) {
        jav.enemyRollAttack(g, e);
        e.setVelX(direction*5);
        e.setRotationAngle((e.getRotationAngle() + 10) % 360);
    }

    public void moveEnemy(boolean collision) {
        if (e.getStatus().compareTo("STUNNED") == 0) return;
        currentAttack = (currentAttack == 0) ? chooseAttack() : currentAttack;
        preventMoveBelow();
        preventMoveOutLeft();
        preventMoveOutRight();
        //if (currentAttack == jam.ROLL && (collision || e.getXOrd() + e.getPlayerLength() == jam.GAME_LENGTH || e.getXOrd() == 0)) checkRollAttackEnd(collision);
        //if (currentAttack == jam.JUMP) checkJumpAttackEnd(collision);
        e.setXOrd(e.getXOrd() + e.getVelX());
        e.setYOrd(e.getYOrd() + e.getVelY());
    }

    private void checkRollAttackEnd(boolean collided) {
        e.setVelX(0);
        e.setRotationAngle(0);
        currentAttack = 0;
        // hit a wall or enemy
        if (collided) {
            int direction = (enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2 < enemyPA.getLastPlayerXOrd()) ? 1 : -1;
            e.setXOrd(e.getXOrd() + direction*250);
        } else if (e.getXOrd() + e.getPlayerLength() == jam.GAME_LENGTH || e.getXOrd() == 0) {
            e.setStatus("STUNNED");
            if (e.getXOrd() + e.getPlayerLength() == jam.GAME_LENGTH) e.setXOrd(e.getXOrd() - 5);
            if (e.getXOrd() == 0) e.setXOrd(5);
            e.setStunnedStart(jam.getCounter());
        }
    }

    private void checkJumpAttackEnd(boolean collided) {
        if (collided || e.getYOrd() + e.getPlayerHeight() >= jam.GAME_HEIGHT - 100) return;
        e.setStatus("STUNNED");
        e.setStunnedStart(jam.getCounter());
    }

    private void preventMoveBelow() {
        if (e.getYOrd() + e.getPlayerHeight() <= jam.GAME_HEIGHT - 100) return;
        if (currentAttack == jam.JUMP) {
            currentAttack = 0;
        }
        e.setRotationAngle(0);
        e.setVelY(0);
        e.setVelX(0);
        e.setYOrd(jam.GAME_HEIGHT - 100 - e.getPlayerHeight());
    }

    private void preventMoveOutRight() {
        if (e.getXOrd() + e.getPlayerLength() <= jam.GAME_LENGTH) return;
        e.setRotationAngle(0);
        e.setVelX(0);
        e.setXOrd(jam.GAME_LENGTH - e.getPlayerLength());
    }

    private void preventMoveOutLeft() {
        if (e.getXOrd() >= 0) return;
        e.setRotationAngle(0);
        e.setVelX(0);
        e.setXOrd(0);
    }

    private int chooseAttack() {
        if (e.getStatus().compareTo("STUNNED") == 0) return 0;
        setEnemyLastOrdinates();
        jumpAttackLock = false;
        return jam.JUMP;
        /*int prob = rand.nextInt(100) + 1;
        if (prob > 0 && prob < 13) return jam.TRIPLE_JUMP;
        if (prob >= 13 && prob < 54) return jam.JUMP;
        if (prob >= 55 && prob < 97) return jam.ROLL;
        return jam.RAIN;*/
    }
}
