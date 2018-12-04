package com.Box.Attack;

import java.awt.Color;
import java.util.Random;

public class EnemyControl {
    private PreAttack enemyPA;
    private JAttackModel jam;
    private JAttackPlayer p;
    private Enemy e;
    private Random rand;
    private int currentAttack;
    private boolean falling;
    private boolean fallBack, fallDownWall;
    private int jumpCount;
    private boolean halfHealthHyper;
    private boolean phase2;

    /**
     * Constructor
     * @param jam    JAttack data
     * @param e      the enemy
     */
    public EnemyControl(JAttackModel jam, Enemy e) {
        this.enemyPA = new PreAttack();
        this.jam = jam;
        this.currentAttack = 0;
        this.p = jam.getPlayer();
        this.e = e;
        this.rand = new Random();
        this.falling = false;
        this.fallBack = false;
        this.fallDownWall = false;
        this.jumpCount = 0;
        this.halfHealthHyper = false;
        this.phase2 = false;
    }

    /**
     * Saves location of enemy and player before enemy attack
     */
    public void setEnemyLastOrdinates() {
        enemyPA.setLastPlayerXOrd(p.getXOrd());
        enemyPA.setLastPlayerYOrd(p.getYOrd());
        enemyPA.setLastEnemyXOrd(e.getXOrd());
        enemyPA.setLastEnemyYOrd(e.getYOrd());
    }

    /**
     * Implements the jumping attack of the enemy
     * The enemy jumps above the player and drops down
     * @param direction    the x-ordinate direction in which the enemy will move towards
     */
    private void jumpAttack(int direction) {
        if (fallBack) {
            fallBack();
            return;
        }
        if (falling) return;
        int maxHeight = 50;

        // start falling at the max height
        if (e.getYOrd() <= maxHeight) {
            e.setVelX(0);
            e.setVelY(25);
            falling = true;

        // moves diagonally above player
        } else {
            e.setYOrd(calculateLinearY(maxHeight));
            e.setVelY(0);
            e.setVelX(35 * direction);
        }
    }

    /**
     * Calculate y ordinate such that the enemy moves along a linear trajectory
     * @param maxHeight    y-ordinate in which the enemy moves towards
     * @return             y ordinate
     */
    private int calculateLinearY(int maxHeight) {
        // y = mx + b     end pt is enemy position before attacking
        int startX = enemyPA.getLastPlayerXOrd();
        int standingYOrd = jam.PLATFORM_YORD - e.getPlayerHeight();
        int jumpPeak = (standingYOrd - maxHeight) + standingYOrd;    // rescale to normal x-y axis
        double gradient = (double)(standingYOrd - jumpPeak) / (double)(enemyPA.getLastEnemyXOrd() - startX);
        double b = jumpPeak - gradient*startX;
        double dY = gradient * e.getXOrd() + b;
        int y = standingYOrd - Math.abs(((int) dY - standingYOrd));
        return y;
    }

    /**
     * Implements the roll attack of the enemy
     * The enemy moves towards the player while rotating
     * @param direction    the x-ordinate direction in which the enemy will move towards
     */
    private void rollAttack(int direction) {
        if (fallBack) fallBack(); // jump backwards when collision with player
        else {
            e.setVelX(direction * 15);
            e.setRotationAngle((e.getRotationAngle() + 10) % 360);
        }
    }

    /**
     * Performs the jump attack multiple times
     * @param direction    the x-ordinate direction in which the enemy will move towards
     */
    private void tripleJumpAttack(int direction) {
        jumpAttack(direction);
    }

    /**
     * Implements the high jump attack of the enemy
     * The enemy jumps above the screen and drops down
     */
    private void highJumpAttack() {
        if (fallBack) fallBack();   // jump backwards when collision with player
        if (falling || fallBack) return;
        // enemy is above the game
        if (e.getYOrd() + e.getPlayerHeight() <= 0) {
            // randomly choose an x-ordinate between an interval (using player ordinates before the jump) to drop down
            int maxX = enemyPA.getLastPlayerXOrd() + e.getPlayerLength();
            int minX = enemyPA.getLastPlayerXOrd() - e.getPlayerLength();
            int x = rand.nextInt(maxX - minX + 1) + minX;
            e.setXOrd(x);
            e.setYOrd(0);
            e.setVelY(25);
            falling = true;
        // drop down
        } else {
            e.setVelX(0);
            e.setVelY(-10);
        }
    }

    /**
     * Implements the hyper attack of the enemy
     * The enemy randomly bounces on the walls
     */
    private void hyperAttack() {
        // start rotating enemy during attack
        if (e.getStatus().compareTo("HYPER") == 0){
            e.setRotationAngle((e.getRotationAngle() + 10) % 360);
        // choose the initial random co-ordinates to move the enemy towards
        } else{
            setEnemyLastOrdinates();
            e.setStatus("HYPER");
            e.setStunnedStart(jam.getCounter());
            e.setVelX(rand.nextInt(40 - 10 + 1) + 10);
            e.setVelY(rand.nextInt(40 - 10 + 1) + 10);
        }
    }

    /**
     * Move enemy
     * @param collision
     */
    public void moveEnemy(boolean collision) {
        checkDead();

        // enemy is not currently attacking (not moving)
        if (e.getStatus().compareTo("") != 0 && e.getStatus().compareTo("HYPER") != 0) return;
        if (e.getStatus().compareTo("DEAD") == 0 || e.getStatus().compareTo("RECOVERING...") == 0) return;

        // performs attack and check collisions
        currentAttack = (currentAttack == 0) ? chooseAttack() : currentAttack;
        performAttack();
        checkAttackCollisions(collision);

        // prevent enemy from moving outside walls of the game
        preventMoveBelow();
        preventHyperMoveAbove();
        preventMoveOutLeft();
        preventMoveOutRight();

        // update enemy co-ordinates
        e.setXOrd(e.getXOrd() + e.getVelX());
        e.setYOrd(e.getYOrd() + e.getVelY());
    }

    /**
     * Checks if enemy has no health
     */
    private void checkDead() {
        // enemy has no health remaining
        if (e.getHealth() <= 0) {
            // phase 2 ended and enemy is defeated
            if (phase2) e.setStatus("DEAD");

            // start second phase of the game
            else {
                // stop enemy from moving
                e.setStatus("RECOVERING...");
                e.setAttacking(false);
                currentAttack = 0;
                e.setStunnedStart(jam.getCounter());
                e.setHealth(100);
                phase2 = true;
            }
        }
        // allow enemy to move
        if (e.getStatus().compareTo("RECOVERING...") == 0 && jam.getCounter() - e.getStunnedStart() >= 5) e.setStatus("");
    }

    /**
     * Perform an attack
     */
    private void performAttack() {
        if (e.getStatus().compareTo("CHARGING...") == 0 || e.getStatus().compareTo("STUNNED") == 0) return;
        if (!e.getAttacking()) setEnemyLastOrdinates();
        int direction = (enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2 < enemyPA.getLastPlayerXOrd()) ? 1 : -1;

        if (currentAttack == jam.JUMP) jumpAttack(direction);
        if (currentAttack == jam.TRIPLE_JUMP) tripleJumpAttack(direction);
        if (currentAttack == jam.ROLL) rollAttack(direction);
        if (currentAttack == jam.HIGH_JUMP) highJumpAttack();
        if (currentAttack == jam.HYPER) hyperAttack();

        e.setAttacking(true);
    }

    /**
     * Checks collision with player during the enemy attack and act accordingly
     * @param collision    true if collided
     */
    private void checkAttackCollisions(boolean collision) {
        if (currentAttack == jam.ROLL) checkRollAttackCollision(collision);
        if (currentAttack == jam.JUMP || currentAttack == jam.TRIPLE_JUMP) checkJumpAttackCollision(collision);
        if (currentAttack == jam.HYPER) hyperAttackCollision(collision);
        if (currentAttack == jam.HIGH_JUMP) highJumpAttackCollision(collision);
    }

    /**
     * Reduce player health when collision occurs during high jump attack
     * @param collided    true if collision occurs
     */
    private void highJumpAttackCollision(boolean collided) {
        if (!collided || fallBack) return;
        p.setHealth(p.getHealth() - 20);
        setUpFallBack();
    }

    /**
     * Reduce player health when collision occurs during hyper attack
     * @param collided    true if collision occurs
     */
    private void hyperAttackCollision(boolean collided) {
        if (!collided) return;
        p.setHealth(p.getHealth() - 5);
        fallBack = true;
    }

    /**
     * Reduce player health when collision occurs during roll attack
     * @param collided    true if collision occurs
     */
    private void checkRollAttackCollision(boolean collided) {
        if (!collided || fallBack) return;
        p.setHealth(p.getHealth() - 10);
        setUpFallBack();
    }

    /**
     * Reduce player health when collision occurs during jump attack
     * @param collided    true if collision occurs
     */
    private void checkJumpAttackCollision(boolean collided) {
        if (!collided || fallBack) return;
        p.setHealth(p.getHealth() - 10);
        setUpFallBack();
        falling = false;
    }

    /**
     * Set up to allow enemy to jump back when collision with player occurs
     */
    private void setUpFallBack() {
        e.setVelY(0);
        e.setVelX(0);
        setEnemyLastOrdinates();
        fallBack = true;
    }

    /**
     * Start jumping back when collision with player occurs
     */
    private void fallBack() {
        if (fallDownWall) return;
        int direction = (enemyPA.getLastPlayerXOrd() + p.getPlayerLength()/2 < enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2) ? 1 : -1;
        int startX = enemyPA.getLastEnemyXOrd() + e.getPlayerLength() / 2 + direction * 400;
        int endX = enemyPA.getLastEnemyXOrd() + e.getPlayerLength()/2;
        e.setVelX(10*direction);
        e.setYOrd(calculateQuadraticY(startX, endX, enemyPA.getLastEnemyYOrd(), enemyPA.getLastEnemyYOrd() - 250));
        e.setRotationAngle((e.getRotationAngle() + 5) % 360);
    }

    /**
     * Calculate y ordinate such that the enemy moves along a curved trajectory
     * @param startX     starting x-ordinate
     * @param endX       x-ordinate to move towards
     * @param startY     starting y-ordinate
     * @param vertexH    y-ordinate to move towards
     * @return           y-ordinate
     */
    private int calculateQuadraticY(int startX, int endX, int startY, int vertexH) {
        // y = a(x-h)^2 + k
        double h = (startX + endX)/2;
        double k = vertexH;
        double a = (startY - k)/Math.pow(startX - h, 2);
        double dY = Math.pow(e.getXOrd() + e.getPlayerLength()/2 - h, 2)*a + k;
        return (int) dY;
    }

    /**
     * Prevent enemy from moving below the screen
     */
    private void preventMoveBelow() {
        if (e.getYOrd() + e.getPlayerHeight() <= jam.PLATFORM_YORD) return;

        if (currentAttack == jam.HYPER) {
            // stop hyper attack
            if (jam.getCounter() - e.getStunnedStart() >= 10) {
                e.setStatus("");
                e.setAttacking(false);
                currentAttack = 0;
            }
            // bounce enemy off the platform
            else {
                int direction = (e.getXOrd() < jam.GAME_LENGTH / 2) ? 1 : -1;
                e.setVelX((rand.nextInt(30 - 10 + 1) + 10) * direction);
                e.setVelY(-(rand.nextInt(30 - 10 + 1) + 10));
                return;
            }
        }
        e.setRotationAngle(0);
        e.setVelY(0);
        e.setVelX(0);
        if (currentAttack != jam.HYPER) enemyAttackEnd();
        e.setYOrd(jam.PLATFORM_YORD - e.getPlayerHeight());
    }

    /**
     * Prevent player from moving outside the right of the screen
     */
    private void preventMoveOutRight() {
        if (e.getXOrd() + e.getPlayerLength() <= jam.GAME_LENGTH) return;
        // bounce enemy off the right wall
        if (currentAttack == jam.HYPER) {
            int direction = (e.getYOrd() < jam.PLATFORM_YORD/2) ? 1 : -1;
            e.setVelY((rand.nextInt(30 - 10 + 1) + 10)*direction);
            e.setVelX(-(rand.nextInt(30 - 10 + 1) + 10));
            return;
        }
        enemyAttackAgainstWall();
        e.setRotationAngle(0);
        e.setVelX(0);
        e.setXOrd(jam.GAME_LENGTH - e.getPlayerLength());
    }

    /**
     * Prevent player from moving outside the left of the screen
     */
    private void preventMoveOutLeft() {
        if (e.getXOrd() >= 0) return;
        // bounce enemy off the left wall
        if (currentAttack == jam.HYPER) {
            int direction = (e.getYOrd() < jam.PLATFORM_YORD/2) ? 1 : -1;
            e.setVelY((rand.nextInt(30 - 10 + 1) + 10)*direction);
            e.setVelX(rand.nextInt(30 - 10 + 1) + 10);
            return;
        }
        enemyAttackAgainstWall();
        e.setRotationAngle(0);
        e.setVelX(0);
        e.setXOrd(0);
    }

    /**
     * Prevent enemy from moving above the screen during hyper attack
     */
    private void preventHyperMoveAbove() {
        if (currentAttack != jam.HYPER) return;
        if (e.getYOrd() >= 0 && currentAttack == jam.HYPER) return;
        int direction = (e.getXOrd() < jam.GAME_LENGTH/2) ? -1 : 1;
        e.setVelX((rand.nextInt(30 - 10 + 1) + 10)*direction);
        e.setVelY(rand.nextInt(30 - 10 + 1) + 10);
    }

    /**
     * Randomly choose an attack
     * @return    integer representing the chosen attack
     */
    private int chooseAttack() {
        if (e.getStatus().compareTo("") != 0) return 0;
        int prob = rand.nextInt(100) + 1;
        if (prob == 23 || (!halfHealthHyper && e.getHealth() <= 50)) {
            setUpAttack(Color.RED);
            halfHealthHyper = true;
            return jam.HYPER;
        }
        if (prob > 0 && prob < 13) {
            setUpAttack(new Color(4, 255, 206));
            return jam.TRIPLE_JUMP;
        }
        if (prob >= 13 && prob < 40) {
            setUpAttack(new Color(0, 255, 218));
            return jam.JUMP;
        }
        if (prob >= 41 && prob < 55 || p.getStatus().compareTo("STUNNED") == 0) {
            return jam.HIGH_JUMP;
        }
        //prob >= 55 && prob < 100
        setUpAttack(new Color(0, 255, 238));
        return jam.ROLL;
    }

    /**
     * Sets up attack
     * @param c    color of the enemy
     */
    private void setUpAttack(Color c) {
        this.e.setStatus("CHARGING...");
        this.e.setStunnedStart(jam.getCounter());
        this.e.setColor(c);
        if (phase2) this.e.setColor(jam.AQUA);
    }

    /**
     * Stop enemy attack when enemy reaches the platform
     */
    private void enemyAttackEnd() {
        if (fallBack) {
            fallBack = false;
            fallDownWall = false;
        }
        if (currentAttack == jam.HIGH_JUMP) {
            falling = false;
        }
        if (currentAttack == jam.TRIPLE_JUMP) {
            jumpCount++;
            setEnemyLastOrdinates();
        }
        if ((currentAttack == jam.JUMP || currentAttack == jam.TRIPLE_JUMP) && falling) {
            falling = false;
            e.setStatus("STUNNED");
            e.setStunnedStart(jam.getCounter());
        }
        if (currentAttack != jam.TRIPLE_JUMP || jumpCount == 3) {
            jumpCount = 0;
            currentAttack = 0;
            e.setAttacking(false);
        }
    }

    /**
     * Stop enemy attacks when reaching the wall
     */
    private void enemyAttackAgainstWall() {
        if (fallBack) {
            fallDownWall = true;
            e.setVelY(20);
        }
        else if (currentAttack == jam.JUMP || currentAttack == jam.TRIPLE_JUMP) {
            e.setVelY(20);
            falling = true;
            //e.setAttacking(false);
        }
        else if (currentAttack == jam.ROLL) {
            e.setStatus("STUNNED");
            e.setStunnedStart(jam.getCounter());
            e.setAttacking(false);
            currentAttack = 0;
        }
    }
}
