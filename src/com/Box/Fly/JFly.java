package com.Box.Fly;

import com.Box.JBox;
import com.Box.Obstacle;
import com.Box.Player;
import com.Box.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

public class JFly extends JPanel implements KeyListener, Runnable {
    private final static Color AQUA = new Color(127, 255, 212);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static int GAME_HEIGHT = 500;
    private final static int GAME_LENGTH = 1000;
    private Timer obstacleDelayer;
    private Timer gameTimer;
    private int obstacleVel, minObstacleHeight;
    private int counter;
    private double delta;
    private int minGap, maxGap;
    private JBox game;
    private Player p1;
    private User u;
    private Random rand;
    private ArrayList<Obstacle> obstacles;
    private boolean running = false;
    private Thread t;
    private boolean start;

    /**
     * Constructor
     * @param g    game frame
     */
    public JFly(JBox g) {
        this.game = g;
        this.rand = new Random();
        this.start = false;
        p1 = new Player(250, 150, 0, 50, 50);
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

    /**
     * Set up the game
     */
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
        start();
    }

    /**
     * Create game timer
     */
    private void initGameTime() {
        counter = 0;
        //int[] intervals = new int[] {0, 10, 50, 100, 200};
        List<Integer> intervals = Arrays.asList(0, 10, 50, 100);
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (intervals.contains(counter)) changeDifficulty();
                counter++;
                System.out.println(counter);
            }
        });
    }

    /**
     * Creates timer to create obstacles at regular intervals
     */
    private void initObstacles() {
        obstacles = new ArrayList<>();
        obstacleVel = 10;
        obstacleDelayer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int gap = rand.nextInt(maxGap - minGap + 1) + minGap;
                int totalObstacleHeight = GAME_HEIGHT - 2*minObstacleHeight - gap;
                int obstaclePadding = rand.nextInt(totalObstacleHeight) + 1;
                int obstacleH1 = minObstacleHeight + obstaclePadding;
                int obstacleH2 = minObstacleHeight + (totalObstacleHeight - obstaclePadding);
                Obstacle o = new Obstacle(GAME_LENGTH, 0, GAME_HEIGHT - obstacleH2, obstacleVel, 150, obstacleH1, obstacleH2);
                obstacles.add(o);
            }
        });
    }

    /**
     * Remove all obstacles from the game
     */
    private void removeObstacles() {
        obstacles.removeAll(obstacles);
    }

    /**
     * Move objects in the game
     */
    private void actionPerformed() {
        if (!running) return;
        requestFocusInWindow();
        moveObstacles();
        movePlayer();
    }

    /**
     * Incrementally move all obstacles across the screen
     */
    private void moveObstacles() {
        if (!running) return;
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            if (!o.inFrame()) obstacles.remove(i);  // remove non-viewable obstacles
            else {
                // check collision with player
                if (checkCollision(o)) o.move();
                else endGame();
            }
        }
    }

    /**
     * Move player
     */
    private void movePlayer() {
        int y = p1.getYOrd();
        // prevent player from going below the screen
        if (y > GAME_HEIGHT - 50) {
            p1.setVelY(0);
            p1.setYOrd(GAME_HEIGHT - 50);
            endGame();
        }
        y = p1.getYOrd();
        y = (y < 0) ? 0 : y;
        p1.setYOrd(y + p1.getVelY());
    }

    /**
     * Check collision between the player and an obstacle
     * @param o    obstacle
     * @return     true if obstacle isn't colliding with the player
     */
    private boolean checkCollision(Obstacle o) {
        return p1.getXOrd() + p1.getPlayerLength() < o.getX() ||
                p1.getXOrd() > o.getX() + o.getLength() ||
                (p1.getYOrd() + p1.getPlayerHeight() < o.getY2() && p1.getYOrd() > o.getY() + o.getTopH());
    }

    /**
     * Draws components on the screen
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!start) drawInstructions(g);
        drawPlayer(g);
        drawObstacles(g);
    }

    /**
     * Shows instructions on how to play the game
     * @param g
     */
    private void drawInstructions(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 25));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Press any key to start. Press up to move.", 250, 100);
    }

    /**
     * Draws the player
     * @param g
     */
    private void drawPlayer(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(p1.getXOrd(), p1.getYOrd(), p1.getPlayerLength(), p1.getPlayerHeight());
    }

    /**
     * Draw all obstacles
     * @param g
     */
    private void drawObstacles(Graphics g) {
        for (Obstacle o : obstacles) {
            g.setColor(DARK_GRAY);
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getTopH());
            g.fillRect(o.getX(), o.getY2(), o.getLength(), o.getBotH());
            g.setColor(AQUA);
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getTopH());
            g.drawRect(o.getX(), o.getY2(), o.getLength(), o.getBotH());
        }
    }

    /**
     * Change player movement to up
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_UP) return;
        p1.setVelY(-5);
    }

    /**
     * Change player movement to down
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (!start) startTimers();
        p1.setVelY(5);
    }

    /**
     * Game loop
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double fps = 60.0;
        final double updateInterval = 1000000000 / fps;
        delta = 0;
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
     * Start all timers for the game
     */
    private void startTimers() {
        start = true;
        gameTimer.start();
        obstacleDelayer.start();
    }

    /**
     * Stop all timers for the game
     */
    private void stopTimers() {
        start = false;
        gameTimer.stop();
        obstacleDelayer.stop();
    }

    /**
     * Change minimum and maximum gap for the player to go through
     */
    private void changeDifficulty() {
        minObstacleHeight = 75;
        switch (counter) {
            case 0:
                maxGap = 230;
                minGap = 180;
                break;
            case 10:
                maxGap = 200;
                minGap = 150;
                break;
            case 50:
                minGap = 120;
                break;
            case 100:
                maxGap = 120;
                break;
        }
    }

    /**
     * Create the end game pop-up component
     */
    private void endGame() {
        System.out.println("Game Over");
        stopTimers();
        UIManager.put("Panel.background", LIGHT_GRAY);
        UIManager.put("OptionPane.background", LIGHT_GRAY);

        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("Game Over!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // create the message showing time lasted on the component
        String s = "You lasted " + counter + " seconds!";
        JLabel message = new JLabel(s, SwingConstants.CENTER);
        counter = 0;
        message.setForeground(Color.WHITE);
        message.setFont(new Font(null, Font.BOLD, 20));

        // create all the buttons on the component
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

    /**
     * Creates buttons for the end game pop-up component
     * @param option    indicates the functionality of the button
     * @param dialog    parent component of the button
     * @return          button
     */
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
                        System.exit(0);
                        break;
                    case "Home":
                        running = false;
                        game.setHome();
                        break;
                    case "Retry":
                        delta = 0;
                        removeObstacles();
                        p1.setYOrd(250);
                        p1.setVelY(0);
                        start();
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

    @Override
    public void keyTyped(KeyEvent e) {}
}
