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
    private PlayerControl pc;
    private EnemyControl ec;
    private JAttackPlayer p;
    private Enemy e;
    private User u;
    private JBox g;
    private boolean running;
    private Thread t;

    public JAttack(JBox game, User u) {
        this.p = new JAttackPlayer(jam.GAME_HEIGHT - 100 - 50, 100, 0, 0,50, 50);
        this.e = new Enemy(jam.GAME_HEIGHT - 100 - 200, 700, 0, 0, 200, 200);
        this.jav = new JAttackView();
        this.jam = new JAttackModel(p, e);
        this.pc = new PlayerControl(jam, jav);
        this.ec = new EnemyControl(jam, jav);
        this.g = game;
        this.u = u;
        this.running = false;
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
        pc.setPlayerLastOrdinates();
        ec.setEnemyLastOrdinates();
        start();
        startTimers();
    }

    private void initGameTimer() {
        gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(counter);
                jam.updateCounter();
            }
        });
    }

    private void actionPerformed() {
        requestFocusInWindow();
        if (p.getStatus().compareTo("STUNNED") == 0 && jam.getCounter() - p.getStunnedStart() == 2) p.setStatus("");
        if (e.getStatus().compareTo("STUNNED") == 0 && jam.getCounter() - e.getStunnedStart() == 4) e.setStatus("");
        if (e.getStatus().compareTo("CHARGING...") == 0 && jam.getCounter() - e.getStunnedStart() == 3) e.setStatus("");
        boolean collided = checkCollision();
        pc.movePlayer(collided);
        ec.moveEnemy(collided);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean collided = checkCollision();
        pc.playerAction(g, collided);
        //ec.enemyAction(g, collided);
        jav.drawEnemy(g, e);
        jav.drawPlatform(g, jam.PLATFORM_YORD, jam.AQUA);
        jav.hideGlitch(g, jam.LIGHT_GRAY);
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
        pc.keyPressed(e);
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
        pc.keyReleased(e);
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
