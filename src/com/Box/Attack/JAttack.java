package com.Box.Attack;

import com.Box.JBox;
import com.Box.Player;
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
    private Player p;
    private Enemy e;
    private User u;
    private JBox g;
    private boolean running;
    private Thread t;
    int playerVel = 3, angleIncrease = 5;
    int flag = 0;
    Color border = Color.WHITE;
    private boolean attack, attackLock, rollBack, jumped;
    private int counter;
    private int direction;
    private boolean curvedJump;
    private int angle;
    private String lastXMove;

    public JAttack(JBox game, User u) {
        this.jav = new JAttackView();
        this.jam = new JAttackModel();
        this.playerPA = new PreAttack();
        this.enemyPA = new PreAttack();
        this.p = new Player(jam.GAME_HEIGHT - 150, 100, 0, 0,50, 50);
        this.e = new Enemy(jam.GAME_HEIGHT - 150 - 150, 700, 0, 0, 200, 200);
        this.g = game;
        this.u = u;
        this.running = false;
        this.attack = false;
        this.attackLock = false;
        this.direction = 1;
        this.rollBack = false;
        this.jumped = false;
        this.angle = 10;
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
        if (checkCollision() && jumped) {
            p.setVelY(20);
            p.setVelX(0);
            jumped = false;
        }
        else if (p.getYOrd() <= 250 && jumped) {
            p.setVelY(15);
        }

        if (p.getYOrd() > jam.GAME_HEIGHT - p.getPlayerHeight() - 100) {
            if (curvedJump) {
                curvedJump = false;
                angle = 0;
            }
            p.setVelY(0);
            p.setYOrd(jam.GAME_HEIGHT - p.getPlayerHeight() - 100);
            rollBack = false;
            direction = 1;
            p.setVelX(0);
        }
        p.setYOrd(p.getYOrd() + p.getVelY());

        if (p.getXOrd() + p.getPlayerLength() > jam.GAME_LENGTH) {
            if (curvedJump) {
                curvedJump = false;
                angle = 0;
                p.setVelY(7);
                p.setVelX(-3);
            }
            p.setXOrd(jam.GAME_LENGTH - p.getPlayerLength());
            if (rollBack) p.setVelY(7);
            rollBack = false;
            direction = 1;
        }
        if (p.getXOrd() < 0) {
            if (curvedJump) {
                curvedJump = false;
                angle = 0;
                p.setVelY(7);
            }
            p.setXOrd(0);
            if (rollBack) p.setVelY(-7);
            rollBack = false;
            direction = 1;
        }
        p.setXOrd(p.getXOrd() + direction*p.getVelX());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rollBack && !attackLock) fallBack(g);
        else if (curvedJump) curvedJump(g);
        else if (!attack) jav.drawPlayer(g, p);
        else playerAttack(g);
        jav.drawEnemy(g, e);
        jav.drawPlatform(g, jam.AQUA);
        jav.hideGlitch(g, jam.LIGHT_GRAY);
        e.setColor(Color.BLACK);
    }

    private void playerAttack(Graphics g) {
        if (!attackLock) {
            setPlayerLastOrdinates();
            direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? -1 : 1;
            attackLock = true;
            p.setVelX(3);
        }
        boolean collided = checkCollision();
        if (p.getYOrd() <= jam.GAME_HEIGHT - 150 && !collided) {
            p.setYOrd(calculateY(playerPA.getLastPlayerXOrd(), playerPA.getLastEnemyXOrd() + e.getPlayerLength()/2, playerPA.getLastPlayerYOrd(), 250));
            jav.rollAttack(g, p.getXOrd(), p.getYOrd(), angleIncrease, border);
            p.setXOrd(p.getXOrd() + p.getVelX()*direction);
            if (flag == 0 && p.getYOrd() < 255) {
                p.setVelX(4);
                angleIncrease = 7;
                flag = 1;
                border = jam.AQUA;
            }
        // player missed/hit enemy
        } else {
            border = Color.WHITE;
            p.setVelX(0);
            angleIncrease = 5;
            flag = 0;
            direction = 1;
            attack = false;
            attackLock = false;
            if (collided) {
                e.setColor(Color.RED);
                rollBack = true;
                p.setVelX(5);
            }
        }
    }

    private void curvedJump(Graphics g) {
        direction = (this.lastXMove != null && this.lastXMove.compareTo("left") == 0) ? -1 : 1;
        p.setYOrd(calculateY(playerPA.getLastPlayerXOrd(), playerPA.getLastPlayerXOrd() + direction*500, playerPA.getLastPlayerYOrd(), 200));
        jav.curvedJump(g, p, angle);
        p.setXOrd(p.getXOrd() + p.getVelX()*direction);
        angle = (angle + 10) % 360;
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
        direction = (p.getXOrd() > e.getXOrd() + e.getPlayerLength()/2) ? 1 : -1;
        p.setYOrd(calculateY(playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2 + direction * 400, playerPA.getLastEnemyXOrd() + e.getPlayerLength() / 2, playerPA.getLastPlayerYOrd(), 250));
        jav.rollAttack(g, p.getXOrd(), p.getYOrd(), angleIncrease, border);
        p.setXOrd(p.getXOrd() + playerVel * direction);
    }

    private void jumpUpAttack() {
        jumped = true;
        direction = 0;
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
        if (attack || rollBack || curvedJump) return;
        if (e.getKeyCode() == KeyEvent.VK_UP && p.getVelX() == 0 && p.getVelY() == 0) {
            p.setVelY(-12);
            jumped = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && p.getVelY() == 0) {
            p.setVelX(-7);
            this.lastXMove = "left";
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && p.getVelY() == 0) {
            p.setVelX(7);
            this.lastXMove = "right";
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE && p.getVelY() == 0) {
            curvedJump = true;
            setPlayerLastOrdinates();
            p.setVelX(3);
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
        if (attack || rollBack || curvedJump) return;
        if (e.getKeyCode() == KeyEvent.VK_UP && p.getVelY() == 0) {
            p.setVelY(-12);
            jumped = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(0);
            this.lastXMove = "left";
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            p.setVelX(0);
            this.lastXMove = "right";
        }
        if (e.getKeyCode() == KeyEvent.VK_A && p.getVelY() == 0) {
            if (p.getXOrd() + p.getPlayerLength() > this.e.getXOrd() && p.getXOrd() < this.e.getXOrd() + this.e.getPlayerLength()) jumpUpAttack();
            else {
                attack = true;
                //this.e.setYOrd(100);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE && p.getVelY() == 0) {
            curvedJump = true;
            setPlayerLastOrdinates();
            p.setVelX(3);
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
