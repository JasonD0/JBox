package com.Box.Attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class PlayerControl {
    private JAttackModel jam;
    private JAttackView jav;
    private JAttackPlayer p;
    private Enemy e;
    private PreAttack playerPA;
    private boolean attack, attackLock;
    private boolean curvedJump, fallBack, jumped;
    private boolean knockBack, knockBackStun;
    private boolean boost;
    private int knockBackStartX;

    public PlayerControl(JAttackModel jam, JAttackView jav) {
        this.jav = jav;
        this.jam = jam;
        this.playerPA = new PreAttack();
        this.p = jam.getPlayer();
        this.e = jam.getEnemy();
        this.attack = false;
        this.attackLock = false;
        this.fallBack = false;
        this.jumped = false;
        this.knockBackStun = false;
        this.knockBackStartX = -1;
    }

    public void setPlayerLastOrdinates() {
        playerPA.setLastPlayerXOrd(p.getXOrd());
        playerPA.setLastPlayerYOrd(p.getYOrd());
        playerPA.setLastEnemyXOrd(e.getXOrd());
        playerPA.setLastEnemyYOrd(e.getYOrd());
    }

    public void movePlayer(boolean collision) {
        // checkCollision && curvedJumped   rollBack = true;

        // jumpUp  and hit enemy or reach peak
        if ((collision || p.getYOrd() <= 250) && jumped) {
            p.setVelY(15);
            p.setVelX(0);
            jumped = false;
            p.setHealth(p.getHealth() - 10);
        }
        // collide with enemy  not during the player attack
        else if (collision && !fallBack && !knockBack && !attack) {
            p.setHealth(p.getHealth() - 10);
            knockBackSetUp();
        }
        // ABOVE  COLLISION DETECTION (another function  execute before movePlayer())

        stopKnockBack();
        preventMoveBelow();
        preventMoveOutRight();
        preventMoveOutLeft();

        p.setYOrd(p.getYOrd() + p.getVelY());
        p.setXOrd(p.getXOrd() + p.getVelX());
    }

    private void stopKnockBack() {
        if (knockBackStartX == -1) return;
        if (Math.abs(knockBackStartX - p.getXOrd()) < jam.GAME_LENGTH/2) return;
        p.setVelX(0);
        p.setVelY(10);
        knockBackStartX = -1;
        jumped = false;
        curvedJump = false;
        knockBack = false;
    }

    private void knockBackSetUp() {
        if (knockBack) return;
        int direction = (p.getXOrd() + p.getPlayerLength() < e.getXOrd() + e.getPlayerLength()/2) ? -1 : 1;
        if (p.getYOrd() + p.getPlayerHeight() < jam.GAME_HEIGHT - 100) {
            if (p.getXOrd() == 0) direction = 1;
            if (p.getXOrd() + p.getPlayerLength() == jam.GAME_LENGTH) direction = -1;
        }
        p.setVelX(20*direction);
        p.setVelY(0);
        knockBack = true;
        knockBackStartX = p.getXOrd();
    }

    // prevent player from moving below platform
    private void preventMoveBelow() {
        if (p.getYOrd() <= jam.GAME_HEIGHT - p.getPlayerHeight() - 100) return;
        jumped = false;
        fallBack = false;
        curvedJump = false;
        knockBack = false;
        if (knockBackStun) {
            p.setStatus("STUNNED");
            p.setStunnedStart(jam.getCounter());
            knockBackStun = false;
        }
        p.setVelY(0);
        p.setVelX(0);
        p.setAngle(0);
        p.setYOrd(jam.GAME_HEIGHT - p.getPlayerHeight() - 100);
    }

    // prevent player from moving outside the right of the window
    private void preventMoveOutRight() {
        if (p.getXOrd() + p.getPlayerLength() <= jam.GAME_LENGTH) return;
        p.setAngle(0);
        p.setVelX(0);
        if (fallBack) fallBackAgainstWall();
        if (knockBack) knockBackAgainstWall();
        if (curvedJump) curveJumpAgainstWall(-1);
        p.setXOrd(jam.GAME_LENGTH - p.getPlayerLength());
    }

    // prevent player from moving outside the left of the window
    private void preventMoveOutLeft() {
        if (p.getXOrd() >= 0) return;
        p.setAngle(0);
        p.setVelX(0);
        if (fallBack) fallBackAgainstWall();
        if (knockBack) knockBackAgainstWall();
        if (curvedJump) curveJumpAgainstWall(1);
        p.setXOrd(0);
    }

    private void curveJumpAgainstWall(int direction) {
        curvedJump = false;
        p.setVelY(10);
        p.setVelX(direction*5);
    }

    private void fallBackAgainstWall() {
        fallBack = false;
        p.setVelY(7);
    }

    private void knockBackAgainstWall() {
        knockBack = false;
        knockBackStun = true;
        p.setVelY(7);
    }

    public void playerAttack(Graphics g, boolean collision) {
        playerAttackSetUp();
        boolean collided = collision;
        if (p.getYOrd() <= jam.GAME_HEIGHT - 150 && !collided) performPlayerAttack(g);
        else playerAttackFinish(collided); // player missed/hit enemy
    }

    private void playerAttackSetUp() {
        if (attackLock) return;
        setPlayerLastOrdinates();
        int direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? -1 : 1;
        //if (p.getXOrd() == 0) direction = 1;
        //if (p.getXOrd() + p.getPlayerLength() == jam.GAME_LENGTH) direction = -1;
        p.setVelX(direction*p.getSpeedX());
        attackLock = true;
    }

    private void performPlayerAttack(Graphics g) {
        int startX = playerPA.getLastPlayerXOrd();
        int endX = playerPA.getLastEnemyXOrd() + e.getPlayerLength()/2;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 250));
        jav.drawPlayer(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
    }

    private void playerAttackFinish(boolean collided) {
        attackLock = false;
        attack = false;
        p.setAngle(0);
        p.setVelX(0);
        if (collided) {
            if (e.getAttacking() && e.getStatus().compareTo("STUNNED") != 0) {
                knockBackSetUp();
            } else {
                //e.setColor(Color.RED);
                fallBack = true;
                p.setSpeedX(10);
                e.setHealth(e.getHealth() - 10);
            }
        }
    }

    public void curvedJump(Graphics g) {
        int direction = (p.getLastXMove().compareTo("left") == 0) ? -1 : 1;
        int startX = playerPA.getLastPlayerXOrd();
        int endX = playerPA.getLastPlayerXOrd() + direction*500;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 200));
        jav.drawPlayer(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
        p.setVelX(p.getSpeedX()*direction);
    }

    private int calculateY(int startX, int endX, int startY, int vertexH) {
        // y = a(x-h)^2 + k
        double h = (startX + endX)/2;
        double k = vertexH;
        double a = (startY - k)/Math.pow(startX - h, 2);
        double dY = Math.pow(p.getXOrd() - h, 2)*a + k;
        return (int) dY;
    }

    public void fallBack(Graphics g) {
        int direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? 1 : -1;
        int startX = playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2 + direction * 400;
        int endX = playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 250));
        jav.drawPlayer(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
        p.setVelX(p.getSpeedX()*direction);
    }

    public void jumpUpAttack() {
        jumped = true;
        p.setVelY(-17);
        p.setVelX(0);
    }

    public void playerAction(Graphics g, boolean collision) {
        if (knockBack) jav.drawPlayer(g, p);
        else if (fallBack && !attackLock) fallBack(g);
        else if (curvedJump) curvedJump(g);
        else if (!attack) jav.drawPlayer(g, p);
        else playerAttack(g, collision);
    }

    public void keyPressed(KeyEvent e) {
        if (attack || fallBack || curvedJump || knockBack) return;
        if (p.getYOrd() + p.getPlayerHeight() != jam.PLATFORM_YORD) return; // player not on platform
        if (p.getStatus().compareTo("STUNNED") == 0 && jam.getCounter() - p.getStunnedStart() != 2) return;

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            p.setVelX(7);
            p.setLastXMove("right");
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(-7);
            p.setLastXMove("left");
        }
    }

    public void keyReleased(KeyEvent e) {
        if (p.getYOrd() + p.getPlayerHeight() != jam.PLATFORM_YORD) return; // player not on platform
        if (attack || fallBack || curvedJump || knockBack) return;
        if (p.getStatus().compareTo("STUNNED") == 0 && jam.getCounter() - p.getStunnedStart() != 2) return;

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            p.setVelX(0);
            p.setLastXMove("right");
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(0);
            p.setLastXMove("left");
        }

        else if (e.getKeyCode() == KeyEvent.VK_A) {
            if (p.getXOrd() + p.getPlayerLength() > this.e.getXOrd() &&
                    p.getXOrd() < this.e.getXOrd() + this.e.getPlayerLength()) {
                jumpUpAttack();
            } else {
                //this.e.setYOrd(150);
                attack = true;
                p.setSpeedX(7);
            }
        }

        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            curvedJump = true;
            setPlayerLastOrdinates();
            p.setSpeedX(7);
        }

        else if (e.getKeyCode() == KeyEvent.VK_B) {
            boost = true;
        }
    }
}
