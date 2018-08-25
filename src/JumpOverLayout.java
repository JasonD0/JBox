import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

public class JumpOverLayout extends JPanel implements ActionListener {

    private Timer obstacleDelayer; // delays new obstacles
    private Timer gameTimer;
    private Timer t =  new Timer(5, this);
    private ArrayList<Obstacle> obstacles;
    private Player p;
    private boolean endGame = false;    // quick way to prevent concurrency (clearing list when list currently being modified)
    private int counter;

    public JumpOverLayout() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        p = new Player();
        add(p, BorderLayout.WEST);
        add(initTimeLabel(), BorderLayout.NORTH);
        obstacles = new ArrayList<>();
        obstacleDelayer = new Timer(1500, new ActionListener(){
            // decrease delay slowly (when obs moves faster)     but random speeds between ranges
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Obstacle o = new Obstacle(1000, 500 + 16);
                obstacles.add(o);
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
                    counter = -1;
                    endGame();
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

        JLabel message = new JLabel("You lasted " + 2 + " seconds!", SwingConstants.CENTER);

        JButton retry = new JButton("Retry");
        retry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.getRootFrame().dispose();
                dialog.dispose();
                removeObstacles();
                t.start();
                obstacleDelayer.start();
                gameTimer.start();
            }
        });

        Object option[] = {retry};
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(message, BorderLayout.CENTER);

        pane.setMessage(panel);
        pane.setOptions(option);

        dialog.setVisible(true);
    }

    private JLabel initTimeLabel() {
        counter = 0;
        JLabel timer = new JLabel("Time    " + counter);
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                timer.setText("Time    " + counter);
            }
        });
        return timer;
    }
}
