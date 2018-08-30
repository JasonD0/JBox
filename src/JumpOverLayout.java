import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class JumpOverLayout extends JPanel implements ActionListener, KeyListener {

    private Timer obstacleDelayer; // delays new obstacles
    private Timer gameTimer;        // survival time
    private Timer t =  new Timer(5, this);      // activator for actionperformed
    private Timer delay;           // delay for speed increase
    private ArrayList<Obstacle> obstacles;
    private Player p;
    private boolean endGame = false;    // quick way to prevent concurrency (clearing list when list currently being modified)
    private int counter;
    private JumpOver game;
    private Random rand;
    private int delayMin, delayMax;
    private int obstacleLength, obstacleHeight, obstacleVel;
    private final static int GAME_LENGTH = 1000;
    private final static int GAME_HEIGHT = 550;

    public JumpOverLayout(JumpOver g) {
        game = g;
        rand = new Random();
        p = new Player();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(new Color(51, 51, 51));
        add(p, BorderLayout.WEST);
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);

        initTimeLabel();    //     add(initTimeLabel(), BorderLayout.NORTH);
        initObstacles();
        initSpeedIncreaseDelayer();

        obstacleDelayer.start();
        t.start();
        gameTimer.start();
    }

    private void initObstacles() {
        delayMin = 975;
        delayMax = 2000;
        obstacleVel = 5;
        obstacles = new ArrayList<>();
        obstacleDelayer = new Timer(1000, new ActionListener(){
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                obstacleHeight = (counter < 85) ? 50 : rand.nextInt(150 - 50 + 1) + 50;
                obstacleLength = (counter < 85) ? 100 : rand.nextInt(250 - 100 + 1) + 100;
                Obstacle o = new Obstacle(GAME_LENGTH, GAME_HEIGHT - obstacleHeight, obstacleVel, obstacleLength, obstacleHeight);
                obstacles.add(o);
                int x = rand.nextInt(delayMax - delayMin + 1) + delayMin;
                obstacleDelayer.setDelay(x);
            }
        });
    }

    private void initSpeedIncreaseDelayer() {
        delay = new Timer(1000, new ActionListener(){

            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                delay.stop();
                obstacleDelayer.start();
            }
        });
    }

    private JLabel initTimeLabel() {
        counter = 0;
        JLabel timer = new JLabel("Time    " + counter);
        timer.setFont(new Font(null, Font.BOLD, 20));
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                timer.setText("Time    " + counter);
                if (counter == 15 || counter == 35 || counter == 85 || counter == 155 || counter == 300) changeObstacleDelay();
            }
        });
        return timer;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        //Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        Stroke defaultStroke = g2d.getStroke();
        drawPlayer(g);
        drawTime(g);
        drawPlatform(g);
        g2d.setStroke(defaultStroke);
        drawObstacles(g);
    }

    private void drawTime(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString(counter + "", 870, 50);
        g.setColor(new Color(255, 153, 0));
        g2d.drawString("Time ", 800, 50);
    }

    private void drawPlatform(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(0, 550, 1000, 550);
    }

    private void drawObstacles(Graphics g) {
        for (Obstacle o : obstacles) {
            g.setColor(new Color(45,45,45));
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
            g.setColor(new Color(255, 153, 0));
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
        }
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        requestFocusInWindow();
        Iterator<Obstacle> itr = obstacles.iterator();
        while (itr.hasNext() && endGame != false) {
            Obstacle o = itr.next();
            if (!o.inFrame()) {
                itr.remove();
            } else {
                if (checkCollision(o)) o.move();
                else endGame();
            }
        }
        int y = p.getYOrd();
        int velY = p.getVelY();
        if (y < 240) {
            p.setVelY(5);
            p.setYord(242);
        }
        // create small delay at apex of jump
        else if (y <= 252 && velY > 0) {
            p.setVelY(3);
        }
        else if (y < 399 && velY > 0) {
            p.setVelY(5);
        }
        else if (y > 500) {
            p.setVelY(0);
            p.setYord(500);
        }
        p.setYord(p.getYOrd() + p.getVelY());

        endGame = true;
        repaint();
    }

    private boolean checkCollision(Obstacle o) {
        return (p.getXOrd() + p.getPlayerLength() < o.getX() ||
                p.getXOrd() > o.getX() + o.getLength() ||
                p.getYOrd() + p.getPlayerHeight() < o.getY() ||
                p.getYOrd() > o.getY() + o.getHeight());
    }

    private void endGame() {
        System.out.println("Game Over");
        t.stop();
        obstacleDelayer.stop();
        gameTimer.stop();

        UIManager.put("Panel.background", new Color(51, 51, 51));
        UIManager.put("OptionPane.background", new Color(51, 51, 51));

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
        b.setBackground(new Color(45, 45, 45));
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
                        removeObstacles();
                        obstacleVel = 5;
                        t.start();
                        obstacleDelayer.start();
                        gameTimer.start();
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


    private void removeObstacles() {
        endGame = false;
        Iterator<Obstacle> itr = obstacles.iterator();
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
    }

    private void changeObstacleDelay() {
        switch (counter) {
            case 15:
                delayMin = 750;
                delayMax = 1150;
                obstacleVel = 7;
                break;
            case 35:
                delayMin = 650;
                delayMax = 1050;
                obstacleVel = 9;
                break;
            case 85:
                delayMin = 650;
                delayMax = 1150;
                obstacleVel = 13;
                // add floating platforms -> halfway up from square
                // add crouch mechanic -> reduces size of sq to half
                break;
            case 155:
                // float platforms can now be extremley high  ie cant jump over some
                delayMin = 950;
                delayMax = 1250;
                obstacleVel = 15;
                break;
            case 300:
                obstacleVel = 16;
                break;
        }
        obstacleDelayer.stop();
        delay.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            p.setVelY(0);
            p.setYord(500);
        }
        if (p.getYOrd() != 500) return;
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
            p.setVelY(-5);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            p.setVelY(0);
            p.setYord(500);
        }
        if (p.getYOrd() != 500) return;
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
            p.setVelY(-5);
        }
    }
}
