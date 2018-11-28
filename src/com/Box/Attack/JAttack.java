package com.Box.Attack;

import com.Box.JBox;
import com.Box.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JAttack extends JPanel implements Runnable, KeyListener {
    private Timer gameTimer;
    private JAttackView jav;
    private JAttackModel jam;
    private PreAttack playerPA, enemyPA;
    private JAttackPlayer p;
    private Enemy e;
    private User u;
    private JBox g;
    private boolean running;
    private Thread t;
    private int counter;
    private boolean attack, attackLock;
    private boolean curvedJump, fallBack, jumped;

    public JAttack(JBox game, User u) {
        this.jav = new JAttackView();
        this.jam = new JAttackModel();
        this.playerPA = new PreAttack();
        this.enemyPA = new PreAttack();
        this.p = new JAttackPlayer(jam.GAME_HEIGHT - 100 - 50, 100, 0, 0,50, 50);
        this.e = new Enemy(jam.GAME_HEIGHT - 100 - 200, 700, 0, 0, 200, 200);
        this.g = game;
        this.u = u;
        this.running = false;
        this.attack = false;
        this.attackLock = false;
        this.fallBack = false;
        this.jumped = false;
        init();
    }

    private synchronized void start() {
        if (running) return;
        running = true;
        t = new Thread(this);
        t.start();
    }

    private synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        setBackground(jam.LIGHT_GRAY);
        setFocusTraversalKeysEnabled(false);
        setRequestFocusEnabled(true);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
        initGameTimer();
        setLayout(new BorderLayout());
        setPlayerLastOrdinates();
        setEnemyLastOrdinates();
        start();
        startTimers();
    }

    private void setPlayerLastOrdinates() {
        playerPA.setLastPlayerXOrd(p.getXOrd());
        playerPA.setLastPlayerYOrd(p.getYOrd());
        playerPA.setLastEnemyXOrd(e.getXOrd());
        playerPA.setLastEnemyYOrd(e.getYOrd());
    }

    private void setEnemyLastOrdinates() {
        enemyPA.setLastPlayerXOrd(p.getXOrd());
        enemyPA.setLastPlayerYOrd(p.getYOrd());
        enemyPA.setLastEnemyXOrd(e.getXOrd());
        enemyPA.setLastEnemyYOrd(e.getYOrd());
    }

    private void initGameTimer() {
        counter = 0;
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(counter);
                counter++;
            }
        });
    }

    private void actionPerformed() {
        requestFocusInWindow();
        movePlayer();
    }

    private void movePlayer() {
        // checkCollision && curvedJumped   rollBack = true;

        // enemy in the air and jumpUp
        if (checkCollision() && jumped) {
            p.setVelY(20);
            p.setVelX(0);
            jumped = false;
        }
        // jumping peak
        else if (p.getYOrd() <= 250 && jumped) {
            p.setVelY(15);
        }

        preventMoveBelow();
        preventMoveOutRight();
        preventMoveOutLeft();

        p.setYOrd(p.getYOrd() + p.getVelY());
        p.setXOrd(p.getXOrd() + p.getVelX());
    }

    // prevent player from moving below platform
    private void preventMoveBelow() {
        if (p.getYOrd() <= jam.GAME_HEIGHT - p.getPlayerHeight() - 100) return;
        jumped = false;
        fallBack = false;
        curvedJump = false;
        p.setVelY(0);
        p.setVelX(0);
        p.setYOrd(jam.GAME_HEIGHT - p.getPlayerHeight() - 100);
    }

    // prevent player from moving outside the right of the window
    private void preventMoveOutRight() {
        if (p.getXOrd() + p.getPlayerLength() <= jam.GAME_LENGTH) return;
        if (curvedJump) curveJumpAgainstWall(-1);
        if (fallBack) fallBackAgainstWall(1);
        p.setXOrd(jam.GAME_LENGTH - p.getPlayerLength());
    }

    // prevent player from moving outside the left of the window
    private void preventMoveOutLeft() {
        if (p.getXOrd() >= 0) return;
        if (curvedJump) curveJumpAgainstWall(1);
        if (fallBack) fallBackAgainstWall(-1);
        p.setXOrd(0);
    }

    private void curveJumpAgainstWall(int direction) {
        curvedJump = false;
        p.setVelY(10);
        p.setVelX(direction*5);
    }

    private void fallBackAgainstWall(int direction) {
        p.setVelY(7*direction);
        fallBack = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fallBack && !attackLock) fallBack(g);
        else if (curvedJump) curvedJump(g);
        else if (!attack) jav.drawPlayer(g, p);
        else playerAttack(g);
        jav.drawEnemy(g, e);
        jav.drawPlatform(g, jam.PLATFORM_YORD, jam.AQUA);
        jav.hideGlitch(g, jam.LIGHT_GRAY);
        e.setColor(Color.BLACK);
    }

    private void playerAttack(Graphics g) {
        playerAttackSetUp();
        boolean collided = checkCollision();
        if (p.getYOrd() <= jam.GAME_HEIGHT - 150 && !collided) performPlayerAttack(g);
        else playerAttackFinish(collided); // player missed/hit enemy
    }

    private void playerAttackSetUp() {
        if (attackLock) return;
        setPlayerLastOrdinates();
        int direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? -1 : 1;
        p.setVelX(direction*p.getSpeedX());
        attackLock = true;
    }

    private void performPlayerAttack(Graphics g) {
        int startX = playerPA.getLastPlayerXOrd();
        int endX = playerPA.getLastEnemyXOrd() + e.getPlayerLength()/2;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 250));
        jav.rollAttack(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
    }

    private void playerAttackFinish(boolean collided) {
        attackLock = false;
        attack = false;
        p.setVelX(0);
        if (collided) {
            e.setColor(Color.RED);
            fallBack = true;
            p.setSpeedX(10);
        }
    }

    private void curvedJump(Graphics g) {
        int direction = (p.getLastXMove().compareTo("left") == 0) ? -1 : 1;
        int startX = playerPA.getLastPlayerXOrd();
        int endX = playerPA.getLastPlayerXOrd() + direction*500;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 200));
        jav.curvedJump(g, p);
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

    private void fallBack(Graphics g) {
        int direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? 1 : -1;
        int startX = playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2 + direction * 400;
        int endX = playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2;
        p.setYOrd(calculateY(startX, endX, playerPA.getLastPlayerYOrd(), 250));
        jav.rollAttack(g, p);
        p.setAngle((p.getAngle() + 5) % 360);
        p.setVelX(p.getSpeedX()*direction);
    }

    private void jumpUpAttack() {
        jumped = true;
        p.setVelY(-17);
        p.setVelX(0);
    }

    private boolean checkCollision() {
        return p.getBoundary().intersects(e.getBoundary());
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double fps = 60.0;
        final double updateInterval = 1000000000 / fps;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime)/updateInterval;
            lastTime = now;

            if (delta >= 1) {
                actionPerformed();
                delta--;
            }
            repaint();

            try {
                if ((lastTime - System.nanoTime() + updateInterval)/1000000 < 0) continue;
                Thread.sleep(8 /*(long)(lastTime - System.nanoTime() + updateInterval)/1000000*/);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (attack || fallBack || curvedJump) return;
        if (p.getYOrd() + p.getPlayerHeight() != jam.PLATFORM_YORD) return; // player not on platform

        if (e.getKeyCode() == KeyEvent.VK_UP && p.getVelX() == 0 && p.getVelY() == 0) {
            p.setVelY(-12);
            jumped = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            p.setVelX(7);
            p.setLastXMove("right");
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(-7);
            p.setLastXMove("left");
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            curvedJump = true;
            setPlayerLastOrdinates();
            p.setSpeedX(7);
        }
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (attack || fallBack || curvedJump) return;
        if (p.getYOrd() + p.getPlayerHeight() != jam.PLATFORM_YORD) return; // player not on platform

        if (e.getKeyCode() == KeyEvent.VK_UP && p.getVelY() == 0 && p.getVelX() == 0) {
            p.setVelY(-12);
            jumped = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            p.setVelX(0);
            p.setLastXMove("right");
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(0);
            p.setLastXMove("left");
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            if (p.getXOrd() + p.getPlayerLength() > this.e.getXOrd() &&
                    p.getXOrd() < this.e.getXOrd() + this.e.getPlayerLength()) {
                jumpUpAttack();
            } else {
                attack = true;
                p.setSpeedX(7);
            }
        }
    }


    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    private void startTimers() {
        gameTimer.start();
    }

    private void stopTimers() {
        gameTimer.stop();
    }
}
