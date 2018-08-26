import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class JumpOverLayout extends JPanel implements ActionListener {

    private Timer obstacleDelayer; // delays new obstacles
    private Timer gameTimer;
    private Timer t =  new Timer(5, this);
    private Timer delay;
    private ArrayList<Obstacle> obstacles;
    private Player p;
    private boolean endGame = false;    // quick way to prevent concurrency (clearing list when list currently being modified)
    private int counter;
    private JumpOver game;
    private Random rand;
    private int delayLowerBound, delayUpperBound;
    private int obstacleLength, obstacleHeight, obstacleVel;

    public JumpOverLayout(JumpOver g) {
        game = g;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        rand = new Random();
        p = new Player();
        add(p, BorderLayout.WEST);
   //     add(initTimeLabel(), BorderLayout.NORTH);
        initTimeLabel();

        delayLowerBound = 975;
        delayUpperBound = 2000;
        obstacleVel = 5;
        obstacles = new ArrayList<>();
        obstacleDelayer = new Timer(1500, new ActionListener(){
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                obstacleHeight = (counter < 85) ? 50 : rand.nextInt(150 - 50 + 1) + 50;
                obstacleLength = (counter < 85) ? 100 : rand.nextInt(250 - 100 + 1) + 100;
                Obstacle o = new Obstacle(1000, 550 - obstacleHeight, obstacleVel, obstacleLength, obstacleHeight);
                obstacles.add(o);
                int x = rand.nextInt(delayUpperBound - delayLowerBound + 1) + delayLowerBound;
                obstacleDelayer.setDelay(x);
            }
        });

        delay = new Timer(2500, new ActionListener(){

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

        obstacleDelayer.start();
        t.start();
        gameTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Obstacle o : obstacles) {
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawString("Time   " + counter, 800, 50);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Iterator<Obstacle> itr = obstacles.iterator();
        while (itr.hasNext() && endGame != false) {
            Obstacle o = itr.next();
            if (!o.inFrame()) {
                itr.remove();
            } else {
                // if intersect then game over
                if (p.getXOrd() + p.getPlayerLength() < o.getX() ||
                        p.getXOrd() > o.getX() + o.getLength() ||
                        p.getYOrd() + p.getPlayerHeight() < o.getY() ||
                        p.getYOrd() > o.getY() + o.getHeight())
                    o.move();
                else {
                    System.out.println("Game Over");
                    t.stop();
                    obstacleDelayer.stop();
                    gameTimer.stop();
                    endGame();
                  //  counter = -1;
                }
            }
        }
        repaint();
        endGame = true;
    }

    private void removeObstacles() {
        endGame = false;
        Iterator<Obstacle> itr = obstacles.iterator();
        while (itr.hasNext()) {
            Object o = itr.next();
            itr.remove();
        }
    }

    private void endGame() {
        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("Game Over!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JLabel message = new JLabel("You lasted " + counter + " seconds!", SwingConstants.CENTER);
        counter = -1;

        JButton retry = new JButton("Retry");
        retry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.getRootFrame().dispose();
                dialog.dispose();
                removeObstacles();
                obstacleVel = 5;
                t.start();
                obstacleDelayer.start();
                gameTimer.start();
            }
        });

        JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.getRootFrame().dispose();
                dialog.dispose();
                game.dispose();
                System.exit(0);
            }
        });

        Object option[] = {retry, exit};
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(message, BorderLayout.CENTER);

        pane.setMessage(panel);
        pane.setOptions(option);

        dialog.setVisible(true);
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

    private void changeObstacleDelay() {
        switch (counter) {
            case 15:
                delayLowerBound = 950;
                delayUpperBound = 1700;
                obstacleVel = 7;
                break;
            case 35:
                delayLowerBound = 950;
                delayUpperBound = 1250;
                obstacleVel = 9;
                break;
            case 85:
                delayLowerBound = 950;
                delayUpperBound = 1250;
                obstacleVel = 13;
                break;
            case 155:
                delayLowerBound = 920;
                delayUpperBound = 1150;
                obstacleVel = 15;
                break;
            case 300:
                obstacleVel = 17;
                break;
        }
        obstacleDelayer.stop();
        delay.start();
    }
}
