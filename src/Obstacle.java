import java.awt.*;

public class Obstacle {

    private int length, height, x, y;
    private boolean inFrame;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
        length = 100;
        height = 50;
        inFrame = true;
    }

    public void move() {
        // in jumpoverlayout   when looping to print  ->  if inFrame false remove
        if (x + length < 0) {
            inFrame = false;
        }
        x -= 3; // variable for number so increase slowly   put limit on speed (so wont be impossible)
            // 10    15 for extra hard mode
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLength() {
        return this.length;     // randomly generate length
    }

    public int getHeight() {
        return this.height;
    }

    public boolean inFrame() {
        return this.inFrame;
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, length, height);
    }
}
