package com.Box.Jump;

import com.Box.JBox;

import com.Box.Obstacle;
import com.Box.Player;
import com.Box.User;
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

public class JJump extends JPanel implements KeyListener, Runnable {
    private Timer obstacleDelayer; // delays new obstacles
    private Timer gameTimer;        // survival time
    private Timer delay;           // delay before speed increase
    private JBox game;
    private Random rand;
    private Player p1, p2;
    private User u;
    private int obstacleLength, obstacleHeight;
    private int counter;
    private boolean instructions = true;
    private boolean endGame = false;
    private boolean paused = false;
    private boolean exited = false;     // exited jumpover game
    private boolean running = false;
    private Thread t;
    private boolean multiplayer;
    private int p1_dead = -1, p2_dead = -1;
    private JJumpView jjv;
    private JJumpModel jjm;

    /**
     * Constructor
     * @param g     game frame
     * @param mp    indicates if multiplayer
     * @param u     user
     */
    public JJump (JBox g, boolean mp, User u) {
        this.game = g;
        this.multiplayer = mp;
        this.rand = new Random();
        this.p1 = new Player(500, 50, 0, 50, 50);
        this.p2 = (!mp) ? null : new Player(500 + jjm.OFFSET, 50, 0, 50, 50);
        this.jjv = new JJumpView();
        this.jjm = new JJumpModel();
        this.u = u;
        init();
    }

    /**
     * Sets up game
     */
    private void init() {
        setBackground(jjm.LIGHT_GRAY);
        setFocusTraversalKeysEnabled(false);
        setRequestFocusEnabled(true);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);
        setLayout(new BorderLayout());
        jjv.initPlatform(this, BorderLayout.SOUTH, multiplayer, jjm.AQUA, jjm.LIGHT_GRAY);
        if (multiplayer) jjv.initHeader(this, BorderLayout.CENTER, Color.CYAN);
        jjv.initHeader(this, BorderLayout.NORTH, jjm.AQUA);
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

    /**
     * Create timer for creating obstacles at randomised intervals
     */
    private void initObstacles() {
        jjm.setDelayRange(975, 2000);
        jjm.setObstacleVel(10);
        obstacleDelayer = new Timer(2000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                jjm.addObstacle(generateObstacle());
                int v = my_rand(jjm.getDelayMax(), jjm.getDelayMin());
                obstacleDelayer.setDelay(v);
            }
        });
    }

    /**
     * Create new obstacle
     * @return    Obstacle
     */
    private Obstacle generateObstacle() {
        int prob = my_rand(100, 1);
        int y = (counter < 35) ? 0 : (prob <= 30) ? my_rand(50, 26) : 0;
        obstacleHeight = (counter < 85) ? 50 : (prob <= 30) ? my_rand(150, 100 - prob) : my_rand(150,50);
        obstacleLength = (counter < 85) ? 100 : my_rand(250, 100);
        Obstacle o = new Obstacle(jjm.GAME_LENGTH, jjm.GAME_HEIGHT1 - obstacleHeight - y, jjm.getObstacleVel(), obstacleLength, obstacleHeight);
        return o;
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
                if (counter == 15 || counter == 35 || counter == 85 || counter == 155 || counter == 300) changeDifficulty();
                if (counter == 0 || counter == 15 || counter == 35) instructions = true;
                else if (counter >= 40) instructions = false;
            }
        });
    }

    /**
     * Draws components of the game
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        jjv.drawPlayer(g, p1, p2, multiplayer);
        jjv.drawHeader(g, counter, multiplayer, p1_dead, p2_dead, u);
        if (instructions) jjv.drawInstructions(g, p1_dead, p2_dead, multiplayer, counter);
        if (p1_dead > 0) jjv.drawGameOver(g, 225);
        if (multiplayer && p2_dead > 0) jjv.drawGameOver(g, 675);
        jjv.drawObstacles(g, jjm.getObstacles(), jjm.DARK_GRAY, jjm.AQUA, jjm.OFFSET, p1_dead, p2_dead, multiplayer);
        jjv.hideGlitch(g, jjm.LIGHT_GRAY);
        if (multiplayer) jjv.drawPlatform(g, jjm.AQUA);
    }

    /**
     * Moves obstacles incrementally across the screen
     */
    //@Override
    public void actionPerformed(/*ActionEvent e*/) {
        if (paused) return;
        requestFocusInWindow();
        Iterator<Obstacle> itr = jjm.getObstacles().iterator();
        // check all obstacles for collision and remove obstacles that are not on the screen
        while (itr.hasNext() && endGame != false) {
            Obstacle o = itr.next();
            if (!o.inFrame() && endGame != false) {
                itr.remove();
            } else {
                if (multiplayer && p2_dead == -1) moveObstacle1(o);
                if (p1_dead == -1) moveObstacle2(o);
                if (p1_dead > 0 && p2_dead > 0) endGame();
            }
        }
        movePlayer();
        endGame = true;
    }

    /**
     * Move obstacle for player 1
     * @param o
     */
    private void moveObstacle1(Obstacle o) {
        if (checkCollision(o, p2, jjm.OFFSET)) {
            o.move();
        } else {
            p2_dead = counter;
        }
    }

    /**
     * Move obstacle for player 2
     * @param o
     */
    private void moveObstacle2(Obstacle o) {
        if (checkCollision(o, p1, 0)) {
            if ((multiplayer && p2_dead > 0) || !multiplayer) o.move();
        } else {
            p1_dead = counter;
        }
        if (!multiplayer && p1_dead > 0) endGame();
    }

    /**
     * Moves player(s) up or down
     */
    private void movePlayer() {
        updateYOrd(p1.getYOrd(), p1, jjm.GAME_HEIGHT1, 0); // move first player
        if (multiplayer) updateYOrd(p2.getYOrd(), p2, jjm.GAME_HEIGHT2, jjm.OFFSET); // move second player
    }

    /**
     * Updates y ordinate of player
     * @param y
     * @param p
     * @param gameHeight
     * @param offset
     */
    private void updateYOrd(int y, Player p, int gameHeight, int offset) {
        // player's maximum jump height
        if (y < 155 + offset) {
            p.setVelY(jjm.getPlayerVel() + 1);
            p.setYOrd(156 + offset);
        }
        // stop player from falling below platform
        else if (y > gameHeight - p.getPlayerHeight()) {
            p.setVelY(0);
            p.setYOrd(gameHeight - p.getPlayerHeight());
        }
        p.setYOrd(p.getYOrd() + p.getVelY());
    }

    /**
     * Checks collision between obstacle and player
     * @param o
     * @param p
     * @param offset
     * @return
     */
    private boolean checkCollision(Obstacle o, Player p, int offset) {
        return (p.getXOrd() + p.getPlayerLength() < o.getX() ||
                p.getXOrd() > o.getX() + o.getLength() ||
                p.getYOrd() + p.getPlayerHeight() < o.getY() + offset ||
                p.getYOrd() > o.getY() + offset + o.getHeight());
    }

    /**
     * Removes all obstacles
     */
    private void removeObstacles() {
        endGame = false;
        jjm.removeAllObstacles();
    }

    /**
     * Change rate of obstacle creation at arbitrary times
     */
    private void changeDifficulty() {
        switch (counter) {
            case 15:
                jjm.setDelayRange(850, 1550);
                jjm.setObstacleVel(12);
                break;
            case 35:
                jjm.setDelayRange(750, 1250);
                jjm.setObstacleVel(18);
                break;
            case 85:
                jjm.setDelayRange(800, 1400);
                jjm.setObstacleVel(22);
                break;
            case 155:
                jjm.setDelayRange(750, 1350);
                jjm.setObstacleVel(26);
                jjm.setPlayerVel(12);
                break;
            case 300:
                jjm.setDelayRange(850, 1400);
                jjm.setObstacleVel(32);
                jjm.setPlayerVel(14);
                break;
        }
        obstacleDelayer.stop();
        delay.start();
    }

    /**
     * Change player velocity, direction and size(crouch)
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (paused) return;
        if (p1.getYOrd() == jjm.GAME_HEIGHT1 - p1.getPlayerHeight()) updatePlayer(e, p1);
        if (multiplayer && p2.getYOrd() == jjm.GAME_HEIGHT2 - p2.getPlayerHeight()) updatePlayer(e, p2);
    }

    /**
     * Update player velocity and size
     * @param e
     * @param p
     */
    private void updatePlayer(KeyEvent e, Player p) {
        // player jumped
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            setOriginalHeight(p);
            p.setVelY(-jjm.getPlayerVel());
        }
        // player crouched
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (p.getPlayerHeight() == jjm.PLAYER_HEIGHT) {
                p.setPlayerHeight(p.getPlayerHeight() / 2);
                p.setYOrd(p.getYOrd() + p.getPlayerHeight());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // pausing game
        if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!paused) {
                paused = true;
                int v = my_rand(jjm.getDelayMax(), jjm.getDelayMin()) - 200;
                obstacleDelayer.setInitialDelay(v);
                stopTimers();
            } else {
                paused = false;
                startTimers();
            }
        }
        if (paused) return;

        // update size and velocity of player
        if (p1.getYOrd() == jjm.GAME_HEIGHT1 - p1.getPlayerHeight()) checkOnPlatform(p1, e);
        if (multiplayer && p2.getYOrd() == jjm.GAME_HEIGHT2 - p2.getPlayerHeight()) checkOnPlatform(p2, e);
    }

    /**
     * Change player size and velocity if on platform
     * @param p
     * @param e
     */
    private void checkOnPlatform(Player p, KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            setOriginalHeight(p);
            p.setVelY(-jjm.getPlayerVel());
        }
    }

    /**
     * Uncrouch player
     * @param p
     */
    private void setOriginalHeight(Player p) {
        if (p.getPlayerHeight() < jjm.PLAYER_HEIGHT) {
            p.setPlayerHeight(jjm.PLAYER_HEIGHT);
            p.setYOrd(p.getYOrd() + p.getPlayerHeight());
        }
    }

    /**
     * Create end of game screen
     */
    private void endGame() {
        System.out.println("Game Over");
        u.setHighScore(counter, "JumpOver");
        stopTimers();
        UIManager.put("Panel.background", jjm.LIGHT_GRAY);
        UIManager.put("OptionPane.background", jjm.LIGHT_GRAY);

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
     * Create end game pop-up message
     * @return    JLabel
     */
    private JLabel endGameMessage() {
        String s = (multiplayer) ? (p1_dead == p2_dead) ? "Tied!" : (p1_dead > p2_dead) ? "Player 1 Wins!" : "Player 2 Wins!" : "You lasted " + counter + " seconds!";
        JLabel message = new JLabel(s, SwingConstants.CENTER);
        counter = 0;
        message.setForeground(Color.WHITE);
        message.setFont(new Font(null, Font.BOLD, 20));
        return message;
    }

    /**
     * Create end game pop-up buttons
     * @param dialog
     * @return           array of JButtons
     */
    private Object[] endGameButtons(JDialog dialog) {
        JButton retry = createButton("Retry", dialog);
        JButton home = createButton("Home", dialog);
        JButton exit = createButton("Exit", dialog);
        Object option[] = {retry, home, exit};
        return option;
    }

    /**
     * Create button for end game pop-up component
     * @param option     indicates functionality of button
     * @param dialog     parent component for the button
     * @return           JButton
     */
    private JButton createButton(String option, JDialog dialog) {
        JButton b = new JButton(option);
        b.setFocusable(false);
        b.setBackground(jjm.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.getRootFrame().dispose();
                dialog.dispose();
                buttonFunctionality(option);
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
                u.getHighScore("JumpOver");
                instructions = true;
                p1_dead = -1;
                p2_dead = -1;
                paused = false;
                removeObstacles();
                obstacleDelayer.setInitialDelay(2000);
                p1.setPlayerHeight(jjm.PLAYER_HEIGHT);
                if (multiplayer) p2.setPlayerHeight(jjm.PLAYER_HEIGHT);
                start();
                startTimers();
                break;
        }
    }

    /**
     * Get random number between an interval
     * @param upper     upper limit of the interval
     * @param lower     lower limit of the interval
     * @return          number
     */
    private int my_rand(int upper, int lower) {
        return rand.nextInt(upper - lower + 1) + lower;
    }

    /**
     * Stop all timers
     */
    private void stopTimers() {
        //t.stop();
        obstacleDelayer.stop();
        gameTimer.stop();
    }

    /**
     * Start all timers
     */
    private void startTimers() {
      //  t.start();
        obstacleDelayer.start();
        gameTimer.start();
    }

    /**
     * Game loop
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double fps = 60.0;
        final double updateInterval = 1000000000 / fps;
        double delta = 0;

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

    @Override
    public void keyTyped(KeyEvent e) {}
}
