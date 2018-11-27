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
    private boolean attack, attackLock;
    private int counter;

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
        if (p.getYOrd() > jam.GAME_HEIGHT - p.getPlayerHeight() - 100) {
            p.setVelY(0);
            p.setYOrd(jam.GAME_HEIGHT - p.getPlayerHeight() - 100);
        }
        p.setYOrd(p.getYOrd() + p.getVelY());

        if (p.getXOrd() + p.getPlayerLength() > jam.GAME_LENGTH) {
            p.setXOrd(jam.GAME_LENGTH - p.getPlayerLength());
        }
        if (p.getXOrd() < 0) {
            p.setXOrd(0);
        }
        p.setXOrd(p.getXOrd() + p.getVelX());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        jav.drawPlatform(g, jam.AQUA);
        if (!attack) jav.drawPlayer(g, p);
        else playerAttack(g);
        jav.drawEnemy(g, e);
    }

    private void playerAttack(Graphics g) {
        if (!attackLock) {
            setPlayerLastOrdinates();
            attackLock = true;
        }
        boolean collided = checkCollision();
        if (p.getYOrd() <= jam.GAME_HEIGHT - 150 && !collided) {
            e.setColor(Color.BLACK);
            p.setYOrd(calculateY());
            jav.rollAttack(g, p.getXOrd(), p.getYOrd(), angleIncrease, border);
            int direction = (p.getXOrd() > e.getXOrd()) ? -1 : 1;
            p.setXOrd(p.getXOrd() + playerVel*direction);
            if (flag == 0 && p.getYOrd() < 255) {
                playerVel = 4;
                angleIncrease = 10;
                flag = 1;
                border = jam.AQUA;
            }
        } else {
            if (collided) e.setColor(Color.RED); // if collided go to ground and roll back
            p.setXOrd(100);
            p.setYOrd(jam.GAME_HEIGHT - 150);
            border = Color.WHITE;
            playerVel = 3;
            angleIncrease = 5;
            flag = 0;
            attack = false;
            attackLock = false;
        }
    }

    private int calculateY() {
        // y = a(x-h)^2 + k
        double h = (playerPA.getLastPlayerXOrd() + playerPA.getLastEnemyXOrd())/2;
        double k = 250;
        double a = (playerPA.getLastPlayerYOrd() - k)/Math.pow(playerPA.getLastPlayerXOrd() - h, 2);
        double dY = Math.pow(p.getXOrd() - h, 2)*a + k;
        return (int) dY;
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
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            p.setVelY(-7);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && p.getVelY() == 0) {
            p.setVelX(-7);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && p.getVelY() == 0) {
            p.setVelX(7);
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
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            p.setVelY(12);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            p.setVelX(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            p.setVelX(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            attack = true;
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
