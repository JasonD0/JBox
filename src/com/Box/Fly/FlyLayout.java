package com.Box.Fly;

import com.Box.JBox;
import com.Box.Obstacle;
import com.Box.Player;
import com.Box.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FlyLayout extends JPanel implements KeyListener, Runnable {
    private final static Color AQUA = new Color(127, 255, 212);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static int GAME_HEIGHT = 500;
    private final static int GAME_LENGTH = 1000;
    private Timer obstacleDelayer;
    private Timer gameTimer;
    private int obstacleVel, minObstacleHeight;
    private int counter;
    private int minGap, maxGap;
    private JBox game;
    private Player p1;
    private User u;
    private Random rand;
    private ArrayList<Obstacle> obstacles;
    private boolean running = false;
    private Thread t;
    private int y = 250, velY = 0;

    public FlyLayout(JBox g) {
        this.game = g;
        this.rand = new Random();
        p1 = new Player(250, 50);
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
        initGameTime();
        initObstacles();
        startTimers();
        start();
    }

    private void initGameTime() {
        counter = 0;
        //int[] intervals = new int[] {0, 10, 50, 100, 200};
        List<Integer> intervals = Arrays.asList(0, 10, 50, 100, 150);
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (intervals.contains(counter)) changeDifficulty();
                counter++;
                System.out.println(counter);
            }
        });
    }

    private void changeDifficulty() {
        switch (counter) {
            case 0:
                maxGap = 250;
                minGap = 200;
                minObstacleHeight = 75;
                break;
            case 10:
                maxGap = 200;
                minGap = 150;
                minObstacleHeight = 75;
                break;
            case 50:
                maxGap = 150;
                minGap = 120;
                minObstacleHeight = 100;
                break;
            case 100:
                minObstacleHeight = 125;
                break;
            case 150:
                minObstacleHeight = 140;
                break;
        }
    }

    private void initObstacles() {
        obstacles = new ArrayList<>();
        obstacleVel = 10;
        obstacleDelayer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int gap = rand.nextInt(maxGap - minGap + 1) + minGap;
                int maxExtraPadding = GAME_HEIGHT - minObstacleHeight*2 - gap; // windowHeight - min length of the 2 obstacles - gap
                int obstaclePadding = rand.nextInt(maxExtraPadding) + 1;
                int obstacleH1 = minObstacleHeight + obstaclePadding;
                obstaclePadding = rand.nextInt(maxExtraPadding - obstaclePadding + 1) + 1;
                int obstacleH2 = minObstacleHeight + obstaclePadding;
                Obstacle o = new Obstacle(GAME_LENGTH, 0, GAME_HEIGHT - obstacleH2, obstacleVel, 150, obstacleH1, obstacleH2);
                obstacles.add(o);
            }
        });
    }

    private void actionPerformed() {
        requestFocusInWindow();
        movePlayer();
        moveObstacles();
    }

    private void moveObstacles() {
        for (int i = 0; i < obstacles.size(); i++) {
            if (!obstacles.get(i).inFrame()) obstacles.remove(i);
            else obstacles.get(i).move();
        }
    }

    private void movePlayer() {
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
     //   g.fillRect(350, 0, 50, GAME_HEIGHT);
        drawObstacles(g);
    }

    private void drawObstacles(Graphics g) {
        for (Obstacle o : obstacles) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getTopH());
            g.fillRect(o.getX(), o.getY2(), o.getLength(), o.getBotH());
            g.setColor(LIGHT_GRAY);
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getTopH());
            g.drawRect(o.getX(), o.getY2(), o.getLength(), o.getBotH());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            velY = - 5;
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

    private void startTimers() {
        gameTimer.start();
        obstacleDelayer.start();
    }

    private void stopTimers() {
        gameTimer.stop();
        obstacleDelayer.stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
