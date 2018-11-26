package com.Box.Attack;

import com.Box.JBox;
import com.Box.Player;
import com.Box.User;
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
    private Player p;
    private Enemy e;
    private User u;
    private JBox g;
    private boolean running;
    private Thread t;
    int playerVel = 3, angleIncrease = 5;
    int flag = 0;
    Color border = Color.WHITE;
    private int counter;

    public JAttack(JBox game, User u) {
        this.jav = new JAttackView();
        this.jam = new JAttackModel();
        this.p = new Player(jam.GAME_HEIGHT - 150, 100, 0, 0,50, 50);
        this.e = new Enemy(jam.GAME_HEIGHT - 150, 700, 0, 0, 200, 200);
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
        start();
        startTimers();
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
        jav.drawPlayer(g, p);
        jav.drawEnemy(g, e);
        if (counter % 3 == 0) e.prepareRollAttack(p);
        else e.reset();
        System.out.println(counter);
        /*
        // y - vertexY = a(x-vertexX)^2
        if (y <= p.getYOrd()) {
            double i = Math.pow(x - p.getYOrd(), 2) / 600 + 250;
            y = (int) i;
            jav.drawPlayer(g, x, y, angleIncrease, border);
            x += playerVel;
            if (flag == 0 && y < 255) {   // if (collision  (change enemy color to red for a second)))  move to ground and roll back
                playerVel = 4;
                angleIncrease = 10;
                flag = 1;
                border = jam.AQUA;
            }
        } else {
            x = 100;
            y = 400;
            border = Color.WHITE;
        }*/

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
        if (e.getKeyCode() == KeyEvent.VK_LEFT && /*p.getYOrd() == jam.GAME_HEIGHT - p.getPlayerHeight() - 100*/ p.getVelY() == 0) {
            p.setVelX(-7);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && /*p.getYOrd() == jam.GAME_HEIGHT - p.getPlayerHeight() - 100*/ p.getVelY() == 0) {
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
