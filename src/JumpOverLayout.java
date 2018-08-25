import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

public class JumpOverLayout extends JPanel implements ActionListener {

    private Timer obstacleDelayer; // delays new obstacles
    private Timer t =  new Timer(5, this);
    private ArrayList<Obstacle> obstacles;

    public JumpOverLayout() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        Player p = new Player();
        add(p, BorderLayout.WEST);
        obstacles = new ArrayList<>();
        obstacleDelayer = new Timer(1000, new ActionListener(){
            // decrease delay slowly (when obs moves faster)     but random speeds between ranges
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Obstacle o = new Obstacle(1000, 500 - 0);
                obstacles.add(o);
            }
        });
        obstacleDelayer.start();
        t.start();
    }

    private void createObstacle() {
        obstacleDelayer.start();
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
        while (itr.hasNext()) {
            Obstacle o = itr.next();
            if (!o.inFrame()) {
                itr.remove();
            } else {
                // if intersect then game over
                
                o.move();
            }
        }
        repaint();
    }
}
