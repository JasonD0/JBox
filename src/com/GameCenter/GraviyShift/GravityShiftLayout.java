package com.GameCenter.GraviyShift;

import com.GameCenter.GameCenter;
import com.GameCenter.Obstacle;
import com.GameCenter.Player;
import com.GameCenter.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GravityShiftLayout extends JPanel implements KeyListener, Runnable {
    private final static Color AQUA = new Color(127, 255, 212);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static int GAME_HEIGHT = 405;
    private final static int GAME_LENGTH = 1000;
    private final static int PLAYER_VEL = 5;
    private ArrayList<Obstacle> obstacles;
    private Timer obstacleDelayer; // delays new obstacles
    private GameCenter game;
    private Player p1;
    private User u;
    private boolean running = false;
    private Thread t;
    private Timer gameTimer;        // survival time
    private int counter;
    private int orientation = 0;
    private Random rand;
    private int obstacleHeight, obstacleLength, obstacleVel;

    public GravityShiftLayout(GameCenter g) {
        this.game = g;
        p1 = new Player(250);
        rand = new Random();
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
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(initHeader(AQUA));
        add(initPlatform());
        add(Box.createRigidArea(new Dimension(0, 300)));
        add(initPlatform());
        add(Box.createRigidArea(new Dimension(0, 91)));
        initGameTime();
        gameTimer.start();
        setOpaque(true);
        start();
    }

    private void initObstacles() {
        defineObstacle(975, 2000, 10);
        obstacles = new ArrayList<>();
        obstacleDelayer = new Timer(2000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int prob = my_rand(100, 1);
                int y = (counter < 35) ? 0 : (prob <= 30) ? my_rand(50, 26) : 0;
                obstacleHeight = (counter < 85) ? 50 : (prob <= 30) ? my_rand(150, 100 - prob) : my_rand(150,50);
                obstacleLength = (counter < 85) ? 100 : my_rand(250, 100);
                Obstacle o = new Obstacle(GAME_LENGTH, GAME_HEIGHT - obstacleHeight - y, obstacleVel, obstacleLength, obstacleHeight);
                obstacles.add(o);
                int v = my_rand(2000, 975);
                obstacleDelayer.setDelay(v);
            }
        });
    }

    private JLabel initHeader(Color c) {
        String head = "\tHigh Score \t\t\t\t\t\t\t\t\t\t Time ";
        head = head.replaceAll("\\t", "         ");
        JLabel header = new JLabel(head);
        header.setMaximumSize(new Dimension(1000, 95));
        header.setMinimumSize(new Dimension(1000, 95));
        header.setPreferredSize(new Dimension(1000,95));
        header.setFont(new Font(null, Font.BOLD, 20));
        header.setForeground(c);
        //header.setBackground(LIGHT_GRAY);
        //header.setOpaque(true);
        return header;
    }

    private JLabel initPlatform() {
        JLabel platform = new JLabel();
        platform.setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, AQUA));
        platform.setMaximumSize(new Dimension(1000, 10));
        platform.setMinimumSize(new Dimension(1000, 10));
        platform.setPreferredSize(new Dimension(1000,10));
        platform.setBackground(AQUA);
        //platform.setOpaque(true);
        return platform;
    }

    private void initGameTime() {
        counter = 0;
        JLabel timer = new JLabel("Time    " + counter);
        timer.setFont(new Font(null, Font.BOLD, 20));
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                timer.setText("Time    " + counter);
            }
        });
    }

    private void drawHeader(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        int time = counter;
        g2d.drawString(166 + "", 175, 55);
        g2d.drawString(time + "", 775, 55);
    }

    private void actionPerformed() {
        requestFocusInWindow();
        if (p1.getYOrd() > GAME_HEIGHT - 50) {
            p1.setYord(GAME_HEIGHT - 50);
            p1.setVelY(0);
        }
        if (p1.getYOrd() < 105) {
            p1.setYord(105);
            p1.setVelY(0);
        }
        p1.setYord(p1.getYOrd() + p1.getVelY());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(150, p1.getYOrd(), 50, 50);
        g.fillRect(200, p1.getYOrd(), 50, 300);
        drawHeader(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            p1.setVelY(-PLAYER_VEL);
            orientation = -1;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            p1.setVelY(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            p1.setVelY(PLAYER_VEL);
            orientation = 1;
        }
        p1.setYord(p1.getYOrd() + p1.getVelY());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //p1.setVelY(5);
        //p1.setYord(p1.getYOrd() + p1.getVelY());
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            p1.setVelY(PLAYER_VEL*orientation);
        }
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

    private int my_rand(int upper, int lower) {
        return rand.nextInt(upper - lower + 1) + lower;
    }

    private void defineObstacle(int delayMin, int delayMax, int obstacleVel) {
        //this.delayMin = delayMin;
        //this.delayMax = delayMax;
        this.obstacleVel = obstacleVel;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

