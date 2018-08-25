import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends JLabel implements KeyListener, ActionListener {

    private Timer t = new Timer(5, this);
    private boolean doubleJump = false;
    private final static int playerLength = 50;
    private final static int playerHeight = 50;
    private int x = 20, y = 500, velY = 0;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        setBackground(Color.BLUE);
        setPreferredSize(new Dimension(100,100));
        setMaximumSize(new Dimension(100, 100));
        setMinimumSize(new Dimension(100, 100));
        setSize(new Dimension(100, 100));
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        setVisible(true);
        addKeyListener(this);
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, playerLength, playerHeight);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        requestFocusInWindow();
        if (y < 250) {
            velY = 5;
            y = 249;
        }
        else if (y >= 250 && y <= 260) {
            velY = 1;
        }
        else if (y > 260 && y < 399 && velY > 0) {
            velY = 5;
        }
        else if (y > 500) {
            velY = 0;
            y = 500;
        }
        y += velY;
        repaint();
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (y != 500) return;
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
            velY = -6;
        }
    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (y != 500) return;
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
            velY = -6;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(x, y, playerLength, playerHeight);
        t.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
