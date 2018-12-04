package com.Box.Attack;

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
    private int knockBackStartX;

    /**
     * Constructor
     * @param jam    JAttack data
     * @param jav    interface that draws components of the game
     */
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

    /**
     * Saves co-ordinates before player attack
     */
    public void setPlayerLastOrdinates() {
        playerPA.setLastPlayerXOrd(p.getXOrd());
        playerPA.setLastPlayerYOrd(p.getYOrd());
        playerPA.setLastEnemyXOrd(e.getXOrd());
        playerPA.setLastEnemyYOrd(e.getYOrd());
    }

    /**
     * Checks collision with player
     * @param collision
     */
    private void enemyCollision(boolean collision) {
        // collision during jump or jump reaches peak  drop player down
        if ((collision || p.getYOrd() <= 250) && jumped) {
            p.setVelY(15);
            p.setVelX(0);
            jumped = false;
            if (!e.getAttacking()) p.setHealth(p.getHealth() - 10);
        }
        // knock back player when collided with enemy
        else if (collision && !fallBack && !knockBack && !attack) {
            if (!e.getAttacking()) p.setHealth(p.getHealth() - 10);
            knockBackSetUp();
        }
    }

    /**
     * Move player
     * @param collision    true if collided with enemy
     */
    public void movePlayer(boolean collision) {
        if (p.getHealth() <= 0) p.setStatus("DEAD");
        if (p.getStatus().compareTo("DEAD") == 0) return;

        // check collision with enemy
        enemyCollision(collision);
        stopKnockBack();

        // prevent player from moving outside screen
        preventMoveBelow();
        preventMoveOutRight();
        preventMoveOutLeft();
        int py = p.getYOrd() + p.getVelY();
       // System.out.println("1: " + p.getYOrd() + " " + p.getVelY() + " " + py);

        // update co-ordinates
        p.setYOrd(p.getYOrd() + p.getVelY());
       // System.out.println("2 " + p.getYOrd() + " " + p.getVelY());
        p.setXOrd(p.getXOrd() + p.getVelX());
    }

    /**
     * Stops knock back when player collides with enemy
     */
    private void stopKnockBack() {
        if (knockBackStartX == -1) return;
        if (Math.abs(knockBackStartX - p.getXOrd()) < jam.GAME_LENGTH/2) return;
        p.setVelX(0);
        p.setVelY(10);
        knockBackStartX = -1;
        jumped = false;
        //curvedJump = false;
        knockBack = false;
    }

    /**
     * Move player backwards when collided with enemy
     */
    private void knockBackSetUp() {
        if (knockBack) return;
        int direction = (p.getXOrd() + p.getPlayerLength() < e.getXOrd() + e.getPlayerLength()/2) ? -1 : 1;
        if (p.getYOrd() + p.getPlayerHeight() <= jam.PLATFORM_YORD) {
            if (p.getXOrd() <= 0 + p.getPlayerLength()/2) direction = 1;
            if (p.getXOrd() + p.getPlayerLength() >= jam.GAME_LENGTH - p.getPlayerLength()/2) direction = -1;
        }
        p.setVelX(20*direction);
        p.setVelY(0);
        knockBack = true;
        knockBackStartX = p.getXOrd();
    }

    /**
     * Prevent player from moving below platform
     */
    private void preventMoveBelow() {
        if (p.getYOrd() <= jam.PLATFORM_YORD - p.getPlayerHeight()) return;
        jumped = false;
        fallBack = false;
        curvedJump = false;
        knockBack = false;

        // stun player when player collided with enemy and got knocked into the wall
        if (knockBackStun) {
            p.setStatus("STUNNED");
            p.setStunnedStart(jam.getCounter());
            knockBackStun = false;
        }

        p.setVelY(0);
        p.setVelX(0);
        p.setAngle(0);
        p.setYOrd(jam.PLATFORM_YORD - p.getPlayerHeight());
    }


    /**
     * Prevent player from moving outside the right wall
     */
    private void preventMoveOutRight() {
        if (p.getXOrd() + p.getPlayerLength() <= jam.GAME_LENGTH) return;
        p.setAngle(0);
        p.setVelX(0);
        if (fallBack) fallBackAgainstWall();
        if (knockBack) knockBackAgainstWall();
        if (curvedJump) curveJumpAgainstWall(-1);
        p.setXOrd(jam.GAME_LENGTH - p.getPlayerLength());
    }

    /**
     * Prevent player from moving outside the left wall
     */
    private void preventMoveOutLeft() {
        if (p.getXOrd() >= 0) return;
        p.setAngle(0);
        p.setVelX(0);
        if (fallBack) fallBackAgainstWall();
        if (knockBack) knockBackAgainstWall();
        if (curvedJump) curveJumpAgainstWall(1);
        p.setXOrd(0);
    }

    /**
     * Bounce player off the wall
     * @param direction    x-ordinate direction to move player towards
     */
    private void curveJumpAgainstWall(int direction) {
        curvedJump = false;
        p.setVelY(10);
        p.setVelX(direction*5);
    }

    /**
     * Bounce player off the wall
     */
    private void fallBackAgainstWall() {
        fallBack = false;
        p.setVelY(10);
        p.setVelY(5);
    }

    /**
     * Stun player
     */
    private void knockBackAgainstWall() {
        knockBack = false;
        knockBackStun = true;
        p.setVelY(7);
    }

    /**
     * Perform attack
     * @param g
     * @param collision
     */
    public void playerAttack(Graphics g, boolean collision) {
        playerAttackSetUp();
        boolean collided = collision;

        // attack when player is on platform and not in collision with enemy
        if (p.getYOrd() <= jam.PLATFORM_YORD - p.getPlayerHeight() && !collided) performPlayerAttack(g);
        else playerAttackFinish(collided); // player missed/hit enemy
    }

    /**
     * Start player attack
     */
    private void playerAttackSetUp() {
        if (attackLock) return;
        setPlayerLastOrdinates();
        int direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? -1 : 1;
        //if (p.getXOrd() == 0) direction = 1;
        //if (p.getXOrd() + p.getPlayerLength() == jam.GAME_LENGTH) direction = -1;
        p.setVelX(direction*p.getSpeedX());
        attackLock = true;
    }

    /**
     * Attack enemy
     * Player moves in a curved trajectory to the enemy
     * @param g
     */
    private void performPlayerAttack(Graphics g) {
        int startX = playerPA.getLastPlayerXOrd();
        int endX = playerPA.getLastEnemyXOrd() + e.getPlayerLength()/2;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 250));
        jav.drawPlayer(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
    }

    /**
     * Stop player attack
     * @param collided    true if collided with enemy
     */
    private void playerAttackFinish(boolean collided) {
        attackLock = false;
        attack = false;
        p.setAngle(0);
        p.setVelX(0);
        if (collided) {
            // knock back player if collided with attacking enemy
            if (e.getAttacking() && e.getStatus().compareTo("STUNNED") != 0) {
                knockBackSetUp();

            // jump backwards if collided with non-attacking enemy
            } else {
                //e.setColor(Color.RED);
                fallBack = true;
                p.setSpeedX(12);
                e.setHealth(e.getHealth() - 5);
            }
        }
    }

    /**
     * Perform player jump
     * Player moves in a curved trajectory
     * @param g
     */
    public void curvedJump(Graphics g) {
        int direction = (p.getLastXMove().compareTo("left") == 0) ? -1 : 1;
        int startX = playerPA.getLastPlayerXOrd();
        int endX = playerPA.getLastPlayerXOrd() + direction*500;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 200));
        jav.drawPlayer(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
        p.setVelX(p.getSpeedX()*direction);
    }

    /**
     * Calculates y-ordinate to move player in a curved directory
     * @param startX     starting x-ordinate
     * @param endX       x-ordinate to move towards
     * @param startY     starting y-ordinate
     * @param vertexH    y-ordinate to move towards
     * @return           y-ordinate
     */
    private int calculateY(int startX, int endX, int startY, int vertexH) {
        // y = a(x-h)^2 + k
        double h = (startX + endX)/2;
        double k = vertexH;
        double a = (startY - k)/Math.pow(startX - h, 2);
        double dY = Math.pow(p.getXOrd() - h, 2)*a + k;
        return (int) dY;
    }

    /**
     * Player jump backwards when collided with enemy
     * @param g
     */
    public void fallBack(Graphics g) {
        int direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? 1 : -1;
        int startX = playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2 + direction * 400;
        int endX = playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 250));
        jav.drawPlayer(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
        p.setVelX(p.getSpeedX()*direction);
    }

    /**
     * Jump straight up
     */
    public void jumpUpAttack() {
        jumped = true;
        p.setVelY(-17);
        p.setVelX(0);
    }

    /**
     * perform a player action
     * @param g
     * @param collision    true if collision with enemy
     */
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
            p.setVelX(9);
            p.setLastXMove("right");
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(-9);
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
                p.setSpeedX(10);
            }
        }

        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            curvedJump = true;
            setPlayerLastOrdinates();
            p.setSpeedX(12);
        }

    }
}
