package com.Box.Float;

import com.Box.JBox;
import com.Box.Obstacle;
import com.Box.Player;
import com.Box.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

public class JFloat extends JPanel implements KeyListener, Runnable {
    private Timer obstacleDelayer; // delays new obstacles
    private JBox game;
    private Player p1;
    private User u;
    private boolean running = false;
    private boolean exited = false;
    private boolean endGame = false;
    private double delta;
    private Thread t;
    private Timer gameTimer;        // survival time
    private int counter;
    private int orientation = 0;
    private Random rand;
    private boolean paused = false;
    private JFloatView jfv;
    private JFloatModel jfm;

    /**
     * Constructor
     * @param g    game frame
     * @param u    user
     */
    public JFloat(JBox g, User u) {
        this.game = g;
        this.p1 = new Player(250, 150, 0, 50, 50);
        this.jfv = new JFloatView();
        this.jfm = new JFloatModel();
        this.rand = new Random();
        this.u = u;
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
     * Sets up the game
     */
    private void init() {
        setBackground(jfm.LIGHT_GRAY);
        setFocusTraversalKeysEnabled(false);
        setRequestFocusEnabled(true);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        jfv.showHeader(this, jfm.AQUA);
        jfv.showPlatform(this, jfm.AQUA);
        add(Box.createRigidArea(new Dimension(0, 300)));
        jfv.showPlatform(this, jfm.AQUA);
        jfv.showInstructions(this, jfm.LIGHT_GRAY);
        add(Box.createRigidArea(new Dimension(0, 90)));
        initGameTime();
        initObstacles();
        startTimers();
        setOpaque(true);
        start();
    }

    /**
     * Creates timer to create obstacles at randomised intervals
     */
    private void initObstacles() {
        jfm.setDelayRange(400, 900);
        jfm.setObstacleVel(10);
        obstacleDelayer = new Timer(2000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addObstacles();
                int v = my_rand(jfm.getDelayMax(), jfm.getDelayMin());
                obstacleDelayer.setDelay(v);
            }
        });
    }

    /**
     * Adds obstacles to game
     */
    private void addObstacles() {
        int numObstacles = (counter >= 35) ? (counter >= 250) ? my_rand(3, 1) : my_rand(2, 1) : 1;
        for (; numObstacles > 0; numObstacles--) {
            int row = my_rand(6, 1) * 50;
            Obstacle o = new Obstacle(jfm.GAME_LENGTH, jfm.GAME_HEIGHT - row, jfm.getObstacleVel(), 100, 50);
            jfm.addObstacle(o);
        }
    }

    /**
     * Creates game timer
     */
    private void initGameTime() {
        counter = 0;
        JLabel timer = new JLabel("Time    " + counter);
        timer.setFont(new Font(null, Font.BOLD, 20));
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                timer.setText("Time    " + counter);
                if (counter == 15 || counter == 35 || counter == 85 || counter == 155 || counter == 250) changeDifficulty();
            }
        });
    }

    /**
     * Move objects in the game
     */
    private void actionPerformed() {
        requestFocusInWindow();
        if (paused) return;
        moveObject();
        movePlayer();
    }

    /**
     * Moves all objects incrementally across the screen
     */
    private void moveObject() {
        ArrayList<Obstacle> obstacles = jfm.getObstacles();
        for (int i = obstacles.size() - 1; i >= 0 && endGame; i--) {
            // remove non-viewable obstacles
            if (endGame && !obstacles.get(i).inFrame()) {
                jfm.removeObstacle(i);

            // check collisions
            } else {
                if (endGame && checkCollision(obstacles.get(i), p1)) obstacles.get(i).move();
                else endGame();
            }
        }
        endGame = true;
    }

    /**
     * Checks collision between obstacle and player
     * @param o    obstacles
     * @param p    player
     * @return     true if obstacle not collided with player
     */
    private boolean checkCollision(Obstacle o, Player p) {
        return (p.getXOrd() + p.getPlayerLength() <= o.getX() ||
                p.getXOrd() >= o.getX() + o.getLength() ||
                p.getYOrd() + p.getPlayerHeight() <= o.getY() ||
                p.getYOrd() >= o.getY() + o.getHeight());
    }

    /**
     * Remove all obstacles from the game
     */
    private void removeObstacles() {
        endGame = false;
        jfm.removeAllObstacles();
    }

    /**
     * Move players
     */
    private void movePlayer() {
        // prevent players from moving below the bottom platform
        if (p1.getYOrd() > jfm.GAME_HEIGHT - 50) {
            p1.setYOrd(jfm.GAME_HEIGHT - 50);
            p1.setVelY(0);
        }
        // prevent players from above the top platform
        if (p1.getYOrd() < 105) {
            p1.setYOrd(105);
            p1.setVelY(0);
        }
        p1.setYOrd(p1.getYOrd() + p1.getVelY());
    }

    /**
     * Draws all components of the game
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        jfv.drawPlayer(g, p1);
        jfv.drawHeader(g, counter, u);
        jfv.drawObstacles(g, jfm.getObstacles(), jfm.DARK_GRAY, jfm.AQUA, endGame);
    }

    /**
     * Updates player velocity and direction
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (paused) return;
        // change player direction to up
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            p1.setVelY(-p1.getVelY());
            orientation = -1;
        }
        // stop player movement
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            p1.setYOrd(roundNearest50(p1.getYOrd()) + 5);
            p1.setVelY(0);
        }
        // change player direction to down
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            p1.setVelY(p1.getVelY());
            orientation = 1;
        }
        p1.setYOrd(p1.getYOrd() + p1.getVelY());
    }

    /**
     * Pause/Unpause game or unfreeze the player
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            // unpause game
            if (paused) {
                paused = false;
                obstacleDelayer.setInitialDelay(200);
                startTimers();
            // pause game
            } else {
                paused = true;
                stopTimers();
            }
        }
        if (paused) return;
        // unfreeze player
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            p1.setVelY(p1.getVelY()*orientation);
        }
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
            if (exited) break;
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
     * Change obstacle velocity and rate at which obstacles are created
     */
    private void changeDifficulty() {
        switch (counter) {
            case 15:
                jfm.setDelayRange(300, 800);
                jfm.setObstacleVel(10);
                break;
            case 35:
                jfm.setDelayRange(350, 700);
                jfm.setObstacleVel(12);
                break;
            case 85:
                jfm.setDelayRange(300, 550);
                jfm.setObstacleVel(13);
                break;
            case 155:
                jfm.setDelayRange(250, 500);
                jfm.setObstacleVel(15);
                p1.setVelY(10);
                break;
            case 250:
                jfm.setDelayRange(200, 450);
                jfm.setObstacleVel(18);
                p1.setVelY(12);
                break;
        }
    }

    /**
     * Round number to nearest 50
     * @param num    the number
     * @return       the rounded number
     */
    private int roundNearest50(int num) {
        int x = num/100;
        int y = num%100;
        int res = (y > 25) ? ((y < 75) ? x * 100 + 50 : x * 100 + 100) : x * 100;
        return res;
    }

    /**
     * Get a random number between an interval
     * @param upper    upper limit of the interval
     * @param lower    lower limit of the interval
     * @return         number
     */
    private int my_rand(int upper, int lower) {
        return rand.nextInt(upper - lower + 1) + lower;
    }

    /**
     * Start all timers for the game
     */
    private void startTimers() {
        gameTimer.start();
        obstacleDelayer.start();
    }

    /**
     * Stop all timers for the game
     */
    private void stopTimers() {
        gameTimer.stop();
        obstacleDelayer.stop();
    }

    /**
     * Creates end game pop-up component
     */
    private void endGame() {
        System.out.println("Game Over");
        u.setHighScore(counter, "GravityShift");
        stopTimers();
        UIManager.put("Panel.background", jfm.LIGHT_GRAY);
        UIManager.put("OptionPane.background", jfm.LIGHT_GRAY);

        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("Game Over!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(endGameMessage(), BorderLayout.CENTER);
        pane.setMessage(panel);
        pane.setOptions(endGameButtons(dialog));

        dialog.setSize(new Dimension(350, 170));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Creates message for end game pop-up
     * @return
     */
    private JLabel endGameMessage() {
        // create label to show time lasted
        String s = "You lasted " + counter + " seconds!";
        JLabel message = new JLabel(s, SwingConstants.CENTER);
        counter = 0;
        message.setForeground(Color.WHITE);
        message.setFont(new Font(null, Font.BOLD, 20));
        return message;
    }

    /**
     * Create buttons for end game pop-up
     * @return
     */
    private Object[] endGameButtons(JDialog dialog) {
        JButton retry = createButton("Retry", dialog);
        JButton home = createButton("Home", dialog);
        JButton exit = createButton("Exit", dialog);
        Object option[] = {retry, home, exit};
        return option;
    }

    /**
     * Creates button for the end game pop-up
     * @param option    indicates the button functionality
     * @param dialog    parent component of the button
     * @return          button
     */
    private JButton createButton(String option, JDialog dialog) {
        JButton b = new JButton(option);
        b.setFocusable(false);
        b.setBackground(jfm.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.getRootFrame().dispose();
                dialog.dispose();
                buttonFunctionality(option);
                paused = false;
                orientation = 0;
                obstacleDelayer.setInitialDelay(2000);
            }
        });
        String image = option + ".png";
        Image icon = new ImageIcon(image).getImage();
        icon = icon.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        b.setIcon(new ImageIcon(icon));
        return b;
    }

    /**
     * implements functionality of end game pop-up buttons
     * @param option    indicates the functionality of the button
     */
    private void buttonFunctionality(String option) {
        switch (option) {
            case "Exit":
                running = false;
                game.dispose();
                System.exit(0);
                break;
            case "Home":
                running = false;
                exited = true;
                game.setHome();
                break;
            case "Retry":
                jfm.setDelayRange(400, 900);
                jfm.setObstacleVel(10);
                delta = 0;
                removeObstacles();
                obstacleDelayer.setInitialDelay(2000);
                start();
                startTimers();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

