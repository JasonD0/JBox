package com.GameCenter.JumpOver;

import com.GameCenter.GameCenter;

import com.GameCenter.Obstacle;
import com.GameCenter.Player;
import com.GameCenter.User;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class JumpOverLayout extends JPanel implements KeyListener, Runnable {
    private Timer obstacleDelayer; // delays new obstacles
    private Timer gameTimer;        // survival time
    private Timer delay;           // delay before speed increase
    private GameCenter game;
    private Random rand;
    private Player p1, p2;
    private User u;
    private ArrayList<Obstacle> obstacles;
    private int obstacleLength, obstacleHeight, obstacleVel;
    private int delayMin, delayMax;
    private int counter;
    private boolean instructions = true;
    private boolean endGame = false;    // quick way to prevent concurrency (clearing list when list currently being modified)
    private boolean paused = false;
    private boolean exited = false;     // exited jumpover game
    private final static int GAME_LENGTH = 1000;
    private final static int GAME_HEIGHT1 = 450;
    private final static int GAME_HEIGHT2 = 916;
    private final static int PLAYER_HEIGHT = 50;
    private final static Color AQUA = new Color(127, 255, 212);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private int PLAYER_VEL = 10;
    private final static int VEL_ADJUSTMENT = 0;    // temp solution to inconsistent frame rates
    private final static int OFFSET = 466;   // offset height for player2 and their obstacles
    private boolean running = false;
    private Thread t;
    private boolean multiplayer;
    private int p1_dead = -1, p2_dead = -1;

    public JumpOverLayout(GameCenter g, boolean mp, User u) {
        game = g;
        multiplayer = mp;
        rand = new Random();
        p1 = new Player(500, 50);
        p2 = (!mp) ? null : new Player(500 + OFFSET, 50);
        this.u = u;
        init();
    }

    private void init() {
        setBackground(LIGHT_GRAY);
        setFocusTraversalKeysEnabled(false);
        setRequestFocusEnabled(true);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);

        setLayout(new BorderLayout());
        add(initPlatform(), BorderLayout.SOUTH);
        if (multiplayer) add(initHeader(Color.CYAN), BorderLayout.CENTER);
        add(initHeader(AQUA), BorderLayout.NORTH);

        initGameTime();
        initObstacles();
        initSpeedIncreaseDelayer();

        startTimers();

        start();
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
        Color c = (multiplayer) ? Color.CYAN : AQUA;
        platform.setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, c));
        platform.setMaximumSize(new Dimension(1000, 45));
        platform.setMinimumSize(new Dimension(1000, 45));
        platform.setPreferredSize(new Dimension(1000,45));
        platform.setBackground(LIGHT_GRAY);
        platform.setOpaque(true);
        return platform;
    }

    private void initObstacles() {
        defineObstacle(975, 2000, 10 - VEL_ADJUSTMENT);
        obstacles = new ArrayList<>();
        obstacleDelayer = new Timer(2000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int prob = my_rand(100, 1);
                int y = (counter < 35) ? 0 : (prob <= 30) ? my_rand(50, 26) : 0;
                obstacleHeight = (counter < 85) ? 50 : (prob <= 30) ? my_rand(150, 100 - prob) : my_rand(150,50);
                obstacleLength = (counter < 85) ? 100 : my_rand(250, 100);
                Obstacle o = new Obstacle(GAME_LENGTH, GAME_HEIGHT1 - obstacleHeight - y, obstacleVel, obstacleLength, obstacleHeight);
                obstacles.add(o);
                int v = my_rand(delayMax, delayMin);
                obstacleDelayer.setDelay(v);
            }
        });
    }

    /**
     * Creates delay before obstacle speed increases to avoid collision between obstacles
     */
    private void initSpeedIncreaseDelayer() {
        delay = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                delay.stop();
                obstacleDelayer.start();
            }
        });
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
                if (counter == 15 || counter == 35 || counter == 85 || counter == 155 || counter == 300) changeDifficulty();
                if (counter == 0 || counter == 15 || counter == 35 || counter == 85) instructions = true;
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawPlayer(g);
        drawHeader(g);
        if (instructions) drawInstructions(g);
        if (p1_dead > 0) drawGameOver(g, 225);
        if (multiplayer && p2_dead > 0) drawGameOver(g, 675);
        drawObstacles(g);
        // hides glitching player
        g.setColor(LIGHT_GRAY);
        g.fillRect(0, 451, 100, 75);
        if (multiplayer) drawPlatform(g);
    }

    private void drawGameOver(Graphics g, int y) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(null, Font.BOLD, 100));
        g.drawString("Game Over", 220, y);
    }

    private void drawPlatform(Graphics g) {
        g.setColor(AQUA);
        g.fillRect(0, 450, 1000, 10);
    }

    private void drawHeader(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);

        int time = counter;
        if (multiplayer) {
            time = (p2_dead > 0) ? p2_dead : counter;
            g2d.drawString(u.getHighScore("JumpOver") + "", 175, 513);
            g2d.drawString(time + "", 775, 513);
            time = (p1_dead > 0) ? p1_dead : counter;
        }

        g2d.drawString(u.getHighScore("JumpOver") + "", 175, 55);
        g2d.drawString(time + "", 775, 55);
    }

    private void drawObstacles(Graphics g) {
        for (Obstacle o : obstacles) {
            if (p1_dead == -1) {
                g.setColor(DARK_GRAY);
                g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
                g.setColor(AQUA);
                g.drawRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
            }
            if (multiplayer && p2_dead == -1) {
                g.setColor(DARK_GRAY);
                g.fillRect(o.getX(), o.getY() + OFFSET, o.getLength(), o.getHeight());
                g.setColor(Color.CYAN);
                g.drawRect(o.getX(), o.getY() + OFFSET, o.getLength(), o.getHeight());
            }
        }
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(p1.getXOrd(), p1.getYOrd(), p1.getPlayerLength(), p1.getPlayerHeight());

        if (multiplayer) {
            g.setColor(Color.WHITE);
            g.fillRect(p2.getXOrd(), p2.getYOrd(), p2.getPlayerLength(), p2.getPlayerHeight());
        }
    }

    private void drawInstructions(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 25));
        g2d.setColor(Color.WHITE);

        if (p1_dead == -1) {
            if (counter < 5) g2d.drawString("PRESS UP TO JUMP", 360, 225);
            else if (counter >= 15 && counter < 20) g2d.drawString("PRESS P TO PAUSE/UNPAUSE", 300, 225);
            else if (counter >= 35 && counter < 40) g2d.drawString("PRESS DOWN TO DUCK", 333, 225);
            else instructions = false;
        }
        if (multiplayer) {
            if (p2_dead > 0) return;
            if (counter < 5) g2d.drawString("PRESS W TO JUMP", 360, 675);
            else if (counter >= 15 && counter < 20) g2d.drawString("PRESS ESC TO PAUSE/UNPAUSE", 285, 675);
            else if (counter >= 35 && counter < 40) g2d.drawString("PRESS S TO DUCK", 363, 675);
        }
    }

    //@Override
    public void actionPerformed(/*ActionEvent e*/) {
        if (paused) return;
        requestFocusInWindow();
        Iterator<Obstacle> itr = obstacles.iterator();
        // check all obstacles for collision and remove obstacles that are not on the screen
        while (itr.hasNext() && endGame != false) {
            Obstacle o = itr.next();
            if (!o.inFrame() && endGame != false) {
                itr.remove();
            } else {
                if (multiplayer && p2_dead == -1) {
                    if (checkCollision(o, p2, OFFSET)) {
                        o.move();
                    } else {
                        p2_dead = counter;
                    }
                }
                if (p1_dead == -1) {
                    if (checkCollision(o, p1, 0)) {
                        if ((multiplayer && p2_dead > 0) || !multiplayer) o.move();
                    } else {
                        p1_dead = counter;
                    }
                    if (!multiplayer && p1_dead > 0) endGame();
                }
                if (p1_dead > 0 && p2_dead > 0) endGame();
            }
        }
        movePlayer();
        endGame = true;
    }

    private void movePlayer() {
        int y1 = p1.getYOrd();
        // player's maximum jump height
        if (y1 < 155) {
            p1.setVelY(PLAYER_VEL + 1);
            p1.setYord(156);
        }
        // stop player from falling below platform
        else if (y1 > GAME_HEIGHT1 - p1.getPlayerHeight()) {
            p1.setVelY(0);
            p1.setYord(GAME_HEIGHT1 - p1.getPlayerHeight());
        }
        p1.setYord(p1.getYOrd() + p1.getVelY());

        if (multiplayer) {
            int y2 = p2.getYOrd();
            // player's maximum jump height
            if (y2 < 155 + OFFSET) {
                p2.setVelY(PLAYER_VEL + 1);
                p2.setYord(156 + OFFSET);
            }
            // stop player from falling below platform
            else if (y2 > GAME_HEIGHT2 - p2.getPlayerHeight()) {
                p2.setVelY(0);
                p2.setYord(GAME_HEIGHT2 - p2.getPlayerHeight());
            }
            p2.setYord(p2.getYOrd() + p2.getVelY());
        }
    }

    private boolean checkCollision(Obstacle o, Player p, int offset) {
        return (p.getXOrd() + p.getPlayerLength() < o.getX() ||
                p.getXOrd() > o.getX() + o.getLength() ||
                p.getYOrd() + p.getPlayerHeight() < o.getY() + offset ||
                p.getYOrd() > o.getY() + offset + o.getHeight());
    }

    private void removeObstacles() {
        endGame = false;
        Iterator<Obstacle> itr = obstacles.iterator();
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
    }

    /**
     * Change rate of obstacle creation at arbitrary times
     */
    private void changeDifficulty() {
        switch (counter) {
            case 15:
                defineObstacle(850, 1550, 12 - VEL_ADJUSTMENT);
                break;
            case 35:
                defineObstacle(750, 1250, 18 - VEL_ADJUSTMENT);
                break;
            case 85:
                defineObstacle(800, 1400, 22 - VEL_ADJUSTMENT);
                break;
            case 155:
                defineObstacle(750, 1350, 26 - VEL_ADJUSTMENT);
                PLAYER_VEL = 12;
                break;
            case 300:
                defineObstacle(850, 1400, 32 - VEL_ADJUSTMENT);
                PLAYER_VEL = 14;
                break;
        }
        obstacleDelayer.stop();
        delay.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (paused) return;
        // check player on platform
        if (p1.getYOrd() == GAME_HEIGHT1 - p1.getPlayerHeight()) {
            // player jumped
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                setOriginalHeight(p1);
                p1.setVelY(-PLAYER_VEL);
            }
            // player crouched
            else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (p1.getPlayerHeight() == PLAYER_HEIGHT) {
                    p1.setPlayerHeight(p1.getPlayerHeight() / 2);
                    p1.setYord(p1.getYOrd() + p1.getPlayerHeight());
                }
            }
        }

        if (multiplayer && p2.getYOrd() == GAME_HEIGHT2 - p2.getPlayerHeight()) {
            // player jumped
            if (e.getKeyCode() == KeyEvent.VK_W) {
                setOriginalHeight(p2);
                p2.setVelY(-PLAYER_VEL);
            }
            // player crouched
            else if (e.getKeyCode() == KeyEvent.VK_S) {
                if (p2.getPlayerHeight() == PLAYER_HEIGHT) {
                    p2.setPlayerHeight(p2.getPlayerHeight() / 2);
                    p2.setYord(p2.getYOrd() + p2.getPlayerHeight());
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // pausing game
        if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!paused) {
                paused = true;
                int v = my_rand(delayMax, delayMin) - 200;
                obstacleDelayer.setInitialDelay(v);
                stopTimers();
            } else {
                paused = false;
                startTimers();
            }
        }
        if (paused) return;

        // check player1 on platform
        if (p1.getYOrd() == GAME_HEIGHT1 - p1.getPlayerHeight()) {
            // player2 jumped
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                setOriginalHeight(p1);
                p1.setVelY(-PLAYER_VEL);
            }
        }

        // check player2 on platform
        if (multiplayer && p2.getYOrd() == GAME_HEIGHT2 - p2.getPlayerHeight()) {
            // player2 jumped
            if (e.getKeyCode() == KeyEvent.VK_W) {
                setOriginalHeight(p2);
                p2.setVelY(-PLAYER_VEL);
            }
        }
    }

    private void setOriginalHeight(Player p) {
        if (p.getPlayerHeight() < PLAYER_HEIGHT) {
            p.setPlayerHeight(PLAYER_HEIGHT);
            p.setYord(p.getYOrd() + p.getPlayerHeight());
        }
    }

    /**
     * Create end of game screen
     */
    private void endGame() {
        System.out.println("Game Over");
        u.setHighScore(counter, "JumpOver");
        stopTimers();
        UIManager.put("Panel.background", LIGHT_GRAY);
        UIManager.put("OptionPane.background", LIGHT_GRAY);

        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("Game Over!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        String s = (multiplayer) ? (p1_dead == p2_dead) ? "Tied!" : (p1_dead > p2_dead) ? "Player 1 Wins!" : "Player 2 Wins!" : "You lasted " + counter + " seconds!";

        JLabel message = new JLabel(s, SwingConstants.CENTER);
        counter = 0;
        message.setForeground(Color.WHITE);
        message.setFont(new Font(null, Font.BOLD, 20));

        JButton retry = createButton("Retry", dialog);
        JButton home = createButton("Home", dialog);
        JButton exit = createButton("Exit", dialog);
        Object option[] = {retry, home, exit};

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(message, BorderLayout.CENTER);
        pane.setMessage(panel);
        pane.setOptions(option);

        dialog.setSize(new Dimension(350, 170));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private JButton createButton(String option, JDialog dialog) {
        JButton b = new JButton(option);
        b.setFocusable(false);
        b.setBackground(DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.getRootFrame().dispose();
                dialog.dispose();
                switch (option) {
                    case "Exit":
                        running = false;
                        game.dispose();
                        stop();
                        System.exit(0);
                        break;
                    case "Home":
                        running = false;
                        exited = true;
                        stop();
                        game.setHome();
                    case "Retry":
                        u.getHighScore("JumpOver");
                        instructions = true;
                        p1_dead = -1;
                        p2_dead = -1;
                        running = true;
                        paused = false;
                        removeObstacles();
                        obstacleDelayer.setInitialDelay(2000);
                        initObstacles();
                        p1.setPlayerHeight(PLAYER_HEIGHT);
                        if (multiplayer) p2.setPlayerHeight(PLAYER_HEIGHT);
                        startTimers();
                        break;
                }
            }
        });
        String image = option + ".png";
        Image icon = new ImageIcon(image).getImage();
        icon = icon.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        b.setIcon(new ImageIcon(icon));
        return b;
    }

    private int my_rand(int upper, int lower) {
        return rand.nextInt(upper - lower + 1) + lower;
    }

    private void defineObstacle(int delayMin, int delayMax, int obstacleVel) {
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.obstacleVel = obstacleVel;
    }

    private void stopTimers() {
        //t.stop();
        obstacleDelayer.stop();
        gameTimer.stop();
    }

    private void startTimers() {
      //  t.start();
        obstacleDelayer.start();
        gameTimer.start();
    }

    @Override
    public void run() {
        // game loop
        long lastTime = System.nanoTime();
        final double fps = 60.0;
        final double updateInterval = 1000000000 / fps;
        double delta = 0;
        // game loop
        while (running) {
            if (exited) break;
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
