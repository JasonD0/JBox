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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
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

public class GravityShiftLayout extends JPanel implements KeyListener, Runnable {
    private final static Color AQUA = new Color(127, 255, 212);
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static int GAME_HEIGHT = 405;
    private final static int GAME_LENGTH = 1000;
    private int PLAYER_VEL = 7;
    private ArrayList<Obstacle> obstacles;
    private Map<Integer, Integer> rows;
    private Timer obstacleDelayer; // delays new obstacles
    private GameCenter game;
    private Player p1;
    private User u;
    private boolean running = false;
    private boolean exited = false;
    private boolean endGame = false;
    private Thread t;
    private Timer gameTimer;        // survival time
    private int counter;
    private int orientation = 0;
    private Random rand;
    private int obstacleHeight, obstacleLength, obstacleVel;
    private boolean paused = false;
    private int delayMin, delayMax;

    public GravityShiftLayout(GameCenter g, User u) {
        this.game = g;
        p1 = new Player(250, 150);
        rand = new Random();
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
        add(initInstructions());
        add(Box.createRigidArea(new Dimension(0, 90)));
        initGameTime();
        initObstacles();
        startTimers();
        setOpaque(true);
        start();
    }

    private JLabel initInstructions() {
        JLabel instructions = new JLabel("", SwingConstants.CENTER);
        String s = "<html><font color='rgb(127, 255, 212)'>UP/DOWN:</font> MOVE #SPACE# <font color='rgb(127, 255, 212)'>SPACE:</font> STOP #SPACE# <font color='rgb(127, 255, 212)'>P:</font> PAUSE</html>";
        s = s.replaceAll("#SPACE#",  "&emsp; &emsp; &emsp; &emsp; &emsp; &emsp;");
        instructions.setText(s);
        instructions.setFont(new Font(null, Font.BOLD, 20));
        instructions.setBackground(LIGHT_GRAY);
        instructions.setForeground(Color.WHITE);
        return instructions;
    }

    private void initObstacles() {
        defineObstacle(400, 900, 10);
        obstacles = new ArrayList<>();
        rows = new HashMap<>();
        obstacleDelayer = new Timer(2000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addObstacles();
                int v = my_rand(delayMax, delayMin);
                obstacleDelayer.setDelay(v);
            }
        });
    }

    private void addObstacles() {
        int numObstacles = (counter >= 35) ? (counter >= 250) ? my_rand(3, 1) : my_rand(2, 1) : 1;
        for (; numObstacles > 0; numObstacles--) {
            int row = my_rand(6, 1) * 50;
            Obstacle o = new Obstacle(GAME_LENGTH, GAME_HEIGHT - row, obstacleVel, 100, 50);
            obstacles.add(o);
        }
    }

    private boolean checkCollision(Obstacle o, Player p) {
        return (p.getXOrd() + p.getPlayerLength() <= o.getX() ||
                p.getXOrd() >= o.getX() + o.getLength() ||
                p.getYOrd() + p.getPlayerHeight() <= o.getY() ||
                p.getYOrd() >= o.getY() + o.getHeight());
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
                if (counter == 15 || counter == 35 || counter == 85 || counter == 155 || counter == 250) changeDifficulty();
            }
        });
    }

    private void drawHeader(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        int time = counter;
        g2d.drawString(u.getHighScore("GravityShift") + "", 175, 55);
        g2d.drawString(time + "", 775, 55);
    }

    private void actionPerformed() {
        requestFocusInWindow();
        if (paused) return;
        moveObject();
        movePlayer();
    }

    private void moveObject() {
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            if (!obstacles.get(i).inFrame() && endGame) {
                obstacles.remove(i);
            } else {
                if (checkCollision(obstacles.get(i), p1)) {
                    obstacles.get(i).move();
                } else {
                    endGame();
                }
            }
        }
        endGame = true;
    }

    private void removeObstacles() {
        endGame = false;
        obstacles.removeAll(obstacles);
    }

    private void movePlayer() {
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
        g.fillRect(p1.getXOrd(), p1.getYOrd(), 50, 50);
        drawHeader(g);
        drawObstacles(g);
    }

    private void drawObstacles(Graphics g) {
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Obstacle o = obstacles.get(i);
            g.setColor(DARK_GRAY);
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
            g.setColor(AQUA);
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (paused) return;
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            p1.setVelY(-PLAYER_VEL);
            orientation = -1;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            p1.setYord(roundNearest50(p1.getYOrd()) + 5);
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
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (paused) {
                paused = false;
                obstacleDelayer.setInitialDelay(200);
                startTimers();
            } else {
                paused = true;
                stopTimers();
            }
        }
        if (paused) return;
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

    private void changeDifficulty() {
        switch (counter) {
            case 15:
                defineObstacle(300, 800, 10);
                break;
            case 35:
                defineObstacle(350, 700, 12);
                break;
            case 85:
                defineObstacle(300, 550, 13);
                break;
            case 155:
                defineObstacle(250, 500, 15);
                PLAYER_VEL = 10;
                break;
            case 250:
                defineObstacle(200, 450, 18);
                PLAYER_VEL = 12;
                break;
        }
    }

    private int roundNearest50(int num) {
        int x = num/100;
        int y = num%100;
        int res = (y > 25) ? ((y < 75) ? x * 100 + 50 : x * 100 + 100) : x * 100;
        // 623  ->  623/100 = 6   623%100 = 23    23 < 25  =>  6 * 100 = 600
        // 868  ->  868/868 = 8   868%100 = 68    68 > 25  =>  68 < 75  =>  8*100 + 50 = 850
        return res;
    }

    private int my_rand(int upper, int lower) {
        return rand.nextInt(upper - lower + 1) + lower;
    }

    private void defineObstacle(int delayMin, int delayMax, int obstacleVel) {
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.obstacleVel = obstacleVel;
    }

    private void startTimers() {
        gameTimer.start();
        obstacleDelayer.start();
    }

    private void stopTimers() {
        gameTimer.stop();
        obstacleDelayer.stop();
    }

    private void endGame() {
        System.out.println("Game Over");
        u.setHighScore(counter, "GravityShift");
        stopTimers();
        UIManager.put("Panel.background", LIGHT_GRAY);
        UIManager.put("OptionPane.background", LIGHT_GRAY);

        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("Game Over!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        String s = "You lasted " + counter + " seconds!";

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
                        defineObstacle(400, 900, 10);
                        running = true;
                        removeObstacles();
                        obstacleDelayer.setInitialDelay(2000);
                        initObstacles();
                        startTimers();
                        break;
                }
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

    @Override
    public void keyTyped(KeyEvent e) {}
}
