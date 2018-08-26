import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends JLabel implements KeyListener, ActionListener {

    private Timer t = new Timer(5, this);
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
        if (y < 240) {
            velY = 9;
            y = 242;
        }
        // create small delay at apex of jump
        else if (y <= 252 && velY > 0) {
            velY = 7;
        }
        else if (y < 399 && velY > 0) {
            velY = 9;
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
            velY = -9;
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
            velY = -9;
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

    public int getPlayerLength() {
        return playerLength;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }

    public int getXOrd() {
        return x;
    }

    public int getYOrd() {
        return y;
    }
}
