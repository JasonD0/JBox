import java.awt.*;

public class Obstacle {

    private int length, height, x, y;
    private boolean inFrame;

    public Obstacle(int x, int y) {

        inFrame = true;
    }

    public void move() {
        // in jumpoverlayout   when looping to print  ->  if inFrame false remove
        if (x + length > 600 || x < 0) {
            inFrame = false;
        }
        x += 2; // variable for number so increase slowly   put lmit on speed (so wont be impossible)
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean inFrame() {
        return this.inFrame;
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, length, height);
    }
}
