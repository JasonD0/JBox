package com.Box.Attack;

import java.awt.Color;
import java.util.Random;

public class EnemyControl {
    private PreAttack enemyPA;
    private JAttackModel jam;
    private JAttackView jav;
    private JAttackPlayer p;
    private Enemy e;
    private Random rand;
    private int currentAttack;
    private boolean falling, collidedMidAir;

    public EnemyControl(JAttackModel jam, JAttackView jav) {
        this.enemyPA = new PreAttack();
        this.jam = jam;
        this.jav = jav;
        this.currentAttack = 0;
        this.p = jam.getPlayer();
        this.e = jam.getEnemy();
        this.rand = new Random();
        this.falling = false;
        this.collidedMidAir = false;
    }

    public void setEnemyLastOrdinates() {
        enemyPA.setLastPlayerXOrd(p.getXOrd());
        enemyPA.setLastPlayerYOrd(p.getYOrd());
        enemyPA.setLastEnemyXOrd(e.getXOrd());
        enemyPA.setLastEnemyYOrd(e.getYOrd());
    }

    private void jumpAttack(int direction) {
        if (falling || collidedMidAir) return;
        e.setColor(Color.BLACK);
        int maxHeight = 50;
        // max height of jump
        if (e.getYOrd() <= maxHeight) {
            e.setVelX(0);
            e.setVelY(30);
            falling = true;

        // moves diagonally above player
        } else {
            // y = mx + b     end pt is enemy position before attacking     start is y=150 (650 for normal axis) x = player position known by enemy at time of attack
            int startX = enemyPA.getLastPlayerXOrd() + p.getPlayerLength() / 2;
            int jumpPeak = (400 - 50) + 400;    // rescale to normal x-y axis
            double gradient = (double)(enemyPA.getLastEnemyYOrd() - jumpPeak) / (double)(enemyPA.getLastEnemyXOrd() - startX);
            double b = jumpPeak - gradient*startX;
            double dY = gradient * e.getXOrd() + b;
            int y = 400 - Math.abs(((int) dY - enemyPA.getLastEnemyYOrd()));
            e.setYOrd(y);
            e.setVelY(0);
            e.setVelX(15 * direction);
        }
    }

    private void rollAttack(int direction) {
        e.setVelX(direction*5);
        e.setRotationAngle((e.getRotationAngle() + 10) % 360);
    }

    public void moveEnemy(boolean collision) {
        if (e.getStatus().compareTo("") != 0) return;
        currentAttack = (currentAttack == 0) ? chooseAttack() : currentAttack;
        performAttack();
        if (currentAttack == jam.ROLL) checkRollAttackEnd(collision);
        if (currentAttack == jam.JUMP) checkJumpAttackEnd(collision);
        preventMoveBelow();
        preventMoveOutLeft();
        preventMoveOutRight();

        e.setXOrd(e.getXOrd() + e.getVelX());
        e.setYOrd(e.getYOrd() + e.getVelY());
    }

    private void performAttack() {
        if (e.getStatus().compareTo("CHARGING...") == 0 || e.getStatus().compareTo("STUNNED") == 0) return;
        if (!e.getAttacking()) {
            System.out.println(p.getXOrd());
            setEnemyLastOrdinates();
        }
        int direction = (enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2 < enemyPA.getLastPlayerXOrd()) ? 1 : -1;
        if (currentAttack == jam.JUMP) jumpAttack(direction);
            //else if (currentAttack == jam.TRIPLE_JUMP);
        else if (currentAttack == jam.ROLL) rollAttack(direction);
        /*else if (currentAttack == jam.RAIN);*/
        e.setAttacking(true);
    }

    private void checkRollAttackEnd(boolean collided) {
        if (!collided) return;
        // hit player
        int direction = (enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2 < enemyPA.getLastPlayerXOrd()) ? -1 : 1;
        e.setXOrd(e.getXOrd() + direction*200);
        e.setVelX(0);
        e.setRotationAngle(0);
        currentAttack = 0;
        e.setAttacking(false);
    }

    private void checkJumpAttackEnd(boolean collided) {
        if (!collided) return;
        collidedMidAir = true;
        falling = false;
        e.setVelX(0);
        e.setVelY(30);
    }

    private void preventMoveBelow() {
        if (e.getYOrd() + e.getPlayerHeight() <= jam.GAME_HEIGHT - 100) return;
        if (currentAttack == jam.JUMP && falling) {
            currentAttack = 0;
            falling = false;
            e.setAttacking(false);
            e.setStatus("STUNNED");
            e.setStunnedStart(jam.getCounter());
        }
        if (currentAttack == jam.JUMP && collidedMidAir) {
            currentAttack = 0;
            collidedMidAir = false;
            e.setAttacking(false);
        }
        e.setRotationAngle(0);
        e.setVelY(0);
        e.setVelX(0);
        e.setYOrd(jam.GAME_HEIGHT - 100 - e.getPlayerHeight());
    }

    private void preventMoveOutRight() {
        if (e.getXOrd() + e.getPlayerLength() <= jam.GAME_LENGTH) return;
        if (currentAttack == jam.JUMP) {
            e.setVelY(20);
            falling = true;
            e.setAttacking(false);
        }
        if (currentAttack == jam.ROLL) {
            e.setStatus("STUNNED");
            e.setStunnedStart(jam.getCounter());
            e.setAttacking(false);
            currentAttack = 0;
        }
        e.setRotationAngle(0);
        e.setVelX(0);
        e.setXOrd(jam.GAME_LENGTH - e.getPlayerLength());
    }

    private void preventMoveOutLeft() {
        if (e.getXOrd() >= 0) return;
        if (currentAttack == jam.JUMP) {
            e.setVelY(20);
            falling = true;
            e.setAttacking(false);
        }
        if (currentAttack == jam.ROLL) {
            e.setStatus("STUNNED");
            e.setStunnedStart(jam.getCounter());
            e.setAttacking(false);
            currentAttack = 0;
        }
        e.setRotationAngle(0);
        e.setVelX(0);
        e.setXOrd(0);
    }

    private int chooseAttack() {
        if (e.getStatus().compareTo("") != 0) return 0;
        int prob = rand.nextInt(100) + 1;
        // if prob == 23 || 1   jump for 10 seconds
        //if (prob > 0 && prob < 13) return jam.TRIPLE_JUMP;
        if (prob >= 13 && prob < 54) {
            setUpJumpAttack();
            return jam.JUMP;
        }
        if (prob >= 55 && prob < 97) {
            setUpRollAttack();
            return jam.ROLL;
        }
        setUpRollAttack();
        return jam.ROLL;
    }

    private void setUpJumpAttack() {
        this.e.setStatus("CHARGING...");
        this.e.setColor(Color.cyan);
        this.e.setStunnedStart(jam.getCounter());
    }

    private void setUpRollAttack() {
        this.e.setStatus("CHARGING...");
        this.e.setColor(Color.pink);
        this.e.setStunnedStart(jam.getCounter());
    }
}
