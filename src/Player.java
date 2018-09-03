import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Color;

public class Player extends JLabel{

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

    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, playerLength, playerHeight);
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

    public int getVelY() { return velY; }


    public void setYord(int y) {
        this.y = y;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }
}
