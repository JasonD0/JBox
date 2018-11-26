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
    private Timer obstacleDelayer;
    private Timer gameTimer;
    private int counter;
    private Random rand;
    private boolean running;
    private boolean start;
    private Thread t;
    private JFlyView jfv;
    private JFlyModel jfm;
    private JBox game;
    private Player p1;
    private User u;

    /**
     * Constructor
     * @param g    game frame
     */
    public JFly(JBox g) {
        this.game = g;
        this.start = false;
        this.running = false;
        this.rand = new Random();
        this.jfv = new JFlyView();
        this.jfm = new JFlyModel();
        this.p1 = new Player(250, 150, 0, 0,50, 50);
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
        setBackground(jfm.LIGHT_GRAY);
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
        obstacleDelayer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Obstacle o = generateObstacle();
                jfm.addObstacle(o);
            }
        });
    }

    /**
     * Create new obstacle pair
     * @return     Obstacle
     */
    private Obstacle generateObstacle() {
        int gap = rand.nextInt(jfm.maxGap() - jfm.minGap() + 1) + jfm.minGap();
        int totalObstacleHeight = jfm.GAME_HEIGHT - 2*75 - gap; // 75 is the minimum obstacle height
        int obstaclePadding = rand.nextInt(totalObstacleHeight) + 1;
        int obstacleH1 = 75 + obstaclePadding;
        int obstacleH2 = 75 + (totalObstacleHeight - obstaclePadding);
        Obstacle o = new Obstacle(jfm.GAME_LENGTH, 0, jfm.GAME_HEIGHT - obstacleH2, 10, 150, obstacleH1, obstacleH2);
        return o;
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
        ArrayList<Obstacle> obstacles = jfm.getObstacles();
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            if (!o.inFrame()) jfm.removeObstacle(i);  // remove non-viewable obstacles
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
        // prevent player from going below the game window
        if (y > jfm.GAME_HEIGHT - 50) {
            p1.setVelY(0);
            p1.setYOrd(jfm.GAME_HEIGHT - 50);
            endGame();
        }
        // prevent player from going above the game window
        y = p1.getYOrd();
        y = (y < 0) ? 0 : y;

        // update player position
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
        if (!start) jfv.drawInstructions(g);
        jfv.drawPlayer(g, p1);
        jfv.drawObstacles(g, jfm.getObstacles(), jfm.DARK_GRAY, jfm.AQUA);
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
        switch (counter) {
            case 0:
                jfm.setGapRange(180, 230);
                break;
            case 10:
                jfm.setGapRange(150, 200);
                break;
            case 50:
                jfm.setGapRange(120, 180);
                break;
            case 100:
                jfm.setGapRange(120, 120);
                break;
        }
    }

    /**
     * Create the end game pop-up component
     */
    private void endGame() {
        System.out.println("Game Over");
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
     * Create end game message
     * @return    JLabel
     */
    private JLabel endGameMessage() {
        String s = "You lasted " + counter + " seconds!";
        JLabel message = new JLabel(s, SwingConstants.CENTER);
        counter = 0;
        message.setForeground(Color.WHITE);
        message.setFont(new Font(null, Font.BOLD, 20));
        return message;
    }

    /**
     * Create all buttons for the end game pop-up
     * @param dialog
     * @return     array of JButtons
     */
    private Object[] endGameButtons(JDialog dialog) {
        // create all the buttons on the component
        JButton retry = createButton("Retry", dialog);
        JButton home = createButton("Home", dialog);
        JButton exit = createButton("Exit", dialog);
        Object option[] = {retry, home, exit};
        return option;
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
        b.setBackground(jfm.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.addActionListener(new ActionListener() {
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
     * implements the functionality of the end game pop-up buttons
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
                game.setHome();
                break;
            case "Retry":
                jfm.removeAllObstacles();
                p1.setYOrd(250);
                p1.setVelY(0);
                start();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
