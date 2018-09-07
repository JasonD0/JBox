import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class JumpOverLayout extends JPanel implements ActionListener, KeyListener {
    private Timer obstacleDelayer; // delays new obstacles
    private Timer gameTimer;        // survival time
    private Timer t =  new Timer(5, this);      // activator for actionperformed
    private Timer delay;           // delay before speed increase
    private JumpOver game;
    private Random rand;
    private Player p;
    private User u;
    private ArrayList<Obstacle> obstacles;
    private int obstacleLength, obstacleHeight, obstacleVel;
    private int delayMin, delayMax;
    private int counter;
    private boolean instructions = true;
    private boolean endGame = false;    // quick way to prevent concurrency (clearing list when list currently being modified)
    private boolean paused = false;
    private final static int GAME_LENGTH = 1000;
    private final static int GAME_HEIGHT = 550;
    private final static int PLAYER_HEIGHT = 50;
    private final static Color ORANGE = new Color(255, 153, 0);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private int PLAYER_VEL = 10;

    public JumpOverLayout(JumpOver g) {
        game = g;
        rand = new Random();
        p = new Player();
        u = new User();
        init();
    }

    private void init() {
        setBackground(LIGHT_GRAY);
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);
        setLayout(new BorderLayout());

        add(initHeader(), BorderLayout.NORTH);
        add(initPlatform(), BorderLayout.SOUTH);

        initGameTime();
        initObstacles();
        initSpeedIncreaseDelayer();

        startTimers();
    }

    private JLabel initHeader() {
        String head = "\tHigh Score \t\t\t\t\t\t\t\t\t\t Time ";
        head = head.replaceAll("\\t", "         ");
        JLabel header = new JLabel(head);
        header.setMaximumSize(new Dimension(1000, 95));
        header.setMinimumSize(new Dimension(1000, 95));
        header.setPreferredSize(new Dimension(1000,95));
        header.setFont(new Font(null, Font.BOLD, 20));
        header.setForeground(ORANGE);
        return header;
    }

    private JLabel initPlatform() {
        JLabel platform = new JLabel();
        platform.setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, ORANGE));
        platform.setBackground(LIGHT_GRAY);
        platform.setMaximumSize(new Dimension(1000, 113));
        platform.setMinimumSize(new Dimension(1000, 113));
        platform.setPreferredSize(new Dimension(1000,113));
        platform.setOpaque(true);
        return platform;
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
                if (counter == 15 || counter == 35 || counter == 85 || counter == 155 || counter == 300) changeObstacleDelay();
                if (counter == 0 || counter == 15 || counter == 35 || counter == 85) instructions = true;
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        //Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        drawPlayer(g);
        drawHeader(g);
        if (instructions) drawInstructions(g);
        drawObstacles(g);
    }

    private void drawHeader(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString(u.getHighScore() + "", 175, 55);
        g2d.drawString(counter + "", 775, 55);
    }

    private void drawObstacles(Graphics g) {
        for (Obstacle o : obstacles) {
            g.setColor(DARK_GRAY);
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
            g.setColor(ORANGE);
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
        }
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
    }

    private void drawInstructions(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 25));
        g2d.setColor(Color.WHITE);
        if (counter < 5) g2d.drawString("PRESS UP OR SPACE TO JUMP", 300, 225);
        else if (counter >= 15 && counter < 20) g2d.drawString("PRESS P TO PAUSE/UNPAUSE", 300, 225);
        else if (counter >= 35 && counter < 40) g2d.drawString("PRESS DOWN TO DUCK", 333, 225);
        else instructions = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        requestFocusInWindow();
        Iterator<Obstacle> itr = obstacles.iterator();
        // check all obstacles for collision and remove obstacles that are not on the screen
        while (itr.hasNext() && endGame != false) {
            Obstacle o = itr.next();
            if (!o.inFrame()) {
                itr.remove();
            } else {
                if (checkCollision(o)) o.move();
                else endGame();
            }
        }
        movePlayer();
        endGame = true;
        repaint();
    }

    private void movePlayer() {
        int y = p.getYOrd();
        int velY = p.getVelY();
        // player's maximum jump height
        if (y < 240) {
            p.setVelY(PLAYER_VEL + 1);
            p.setYord(242);
        }
        // create small delay at apex of jump
        else if (y <= 252 && velY > 0) {
            p.setVelY(PLAYER_VEL - 2);
        }
        // remove the small delay
        else if (y < 399 && velY > 0) {
            p.setVelY(PLAYER_VEL + 1);
        }
        // stop player from falling below platform
        else if (y > GAME_HEIGHT - p.getPlayerHeight()) {
            p.setVelY(0);
            p.setYord(GAME_HEIGHT - p.getPlayerHeight());
        }
        p.setYord(p.getYOrd() + p.getVelY());
    }

    private boolean checkCollision(Obstacle o) {
        return (p.getXOrd() + p.getPlayerLength() < o.getX() ||
                p.getXOrd() > o.getX() + o.getLength() ||
                p.getYOrd() + p.getPlayerHeight() < o.getY() ||
                p.getYOrd() > o.getY() + o.getHeight());
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
    private void changeObstacleDelay() {
        switch (counter) {
            case 15:
                defineObstacle(850, 1550, 12);
                break;
            case 35:
                defineObstacle(750, 1250, 18);
                break;
            case 85:
                defineObstacle(800, 1400, 22);
                break;
            case 155:
                defineObstacle(750, 1350, 26);
                PLAYER_VEL = 12;
                break;
            case 300:
                defineObstacle(850, 1400, 32);
                PLAYER_VEL = 14;
                break;
        }
        obstacleDelayer.stop();
        delay.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (p.getYOrd() != GAME_HEIGHT - p.getPlayerHeight()) return;   // check player on platform
        // player jumped
        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) && p.getPlayerHeight() < PLAYER_HEIGHT) {
            if (p.getPlayerHeight() < PLAYER_HEIGHT) {
                p.setPlayerHeight(PLAYER_HEIGHT);
                p.setYord(p.getYOrd() + p.getPlayerHeight());
            }
            p.setVelY(-PLAYER_VEL);
        }
        // player crouched
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (p.getPlayerHeight() == PLAYER_HEIGHT) {
                p.setPlayerHeight(p.getPlayerHeight()/2);
                p.setYord(p.getYOrd() + p.getPlayerHeight());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // change player height back to original
        if (p.getPlayerHeight() < PLAYER_HEIGHT) {
           p.setPlayerHeight(PLAYER_HEIGHT);
           p.setYord(p.getYOrd() + p.getPlayerHeight());
        }
        // pausing game
        if (e.getKeyCode() == KeyEvent.VK_P) {
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
        if (p.getYOrd() != GAME_HEIGHT - p.getPlayerHeight()) return; // check player on platform

        // player jumped
        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) && p.getPlayerHeight() == PLAYER_HEIGHT) {
            p.setVelY(-PLAYER_VEL);
        }
    }

    /**
     * Create end of game screen
     */
    private void endGame() {
        System.out.println("Game Over");
        u.setHighScore(counter);
        stopTimers();
        UIManager.put("Panel.background", LIGHT_GRAY);
        UIManager.put("OptionPane.background", LIGHT_GRAY);

        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("Game Over!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JLabel message = new JLabel("You lasted " + counter + " seconds!", SwingConstants.CENTER);
        counter = 0;
        message.setForeground(Color.WHITE);
        message.setFont(new Font(null, Font.BOLD, 20));

        JButton retry = createButton("Retry", dialog);
        JButton exit = createButton("Exit", dialog);
        Object option[] = {retry, exit};

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(message, BorderLayout.CENTER);
        pane.setMessage(panel);
        pane.setOptions(option);

        dialog.setSize(new Dimension(300, 170));
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
                        game.dispose();
                        System.exit(0);
                        break;
                    case "Retry":
                        u.getHighScore();
                        removeObstacles();
                        obstacleDelayer.setInitialDelay(2000);
                        initObstacles();
                        p.setPlayerHeight(PLAYER_HEIGHT);
                        instructions = true;
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
        t.stop();
        obstacleDelayer.stop();
        gameTimer.stop();
    }

    private void startTimers() {
        t.start();
        obstacleDelayer.start();
        gameTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
