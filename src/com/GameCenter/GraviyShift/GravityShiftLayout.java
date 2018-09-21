package com.GameCenter.GraviyShift;

import com.GameCenter.GameCenter;
import com.GameCenter.Player;
import com.GameCenter.JumpOver.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class GravityShiftLayout extends JPanel implements KeyListener, Runnable {
    private final static Color AQUA = new Color(127, 255, 212);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static int GAME_HEIGHT = 500;
    private GameCenter game;
    private Player p1;
    private User u;
    private boolean running = false;
    private Thread t;
    private int y = 250, velY = 0;

    public GravityShiftLayout(GameCenter g) {
        this.game = g;
        p1 = new Player(250);
        //u = new User();
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
        setBackground(LIGHT_GRAY);
        setFocusTraversalKeysEnabled(false);
        setRequestFocusEnabled(true);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);
        setLayout(new BorderLayout());
        start();
    }

    private void actionPerformed() {
        requestFocusInWindow();
        if (y > GAME_HEIGHT - 50) {
            y = GAME_HEIGHT - 50;
            velY = 0;
        }
        if (y < 0) {
            y = 0;
//            velY = 5;
        }
        y += velY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(150, this.y, 50, 50);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            velY = - 4;
        }
        y += velY;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        velY = 5;
        y += velY;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double fps = 60.0;
        final double updateInterval = 1000000000 / fps;
        double delta = 0;
        // game loop
        while (running) {
            long now = System.nanoTime();
            /*if (now - lastTime > 1000000000) lastTime = now;
            double updates = (now - lastTime) / updateInterval;
            for (int i = 0; i < updates; i++) {
                actionPerformed();
                lastTime += updateInterval;
            }*/
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

    @Override
    public void keyTyped(KeyEvent e) {}
}

