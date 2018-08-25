import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class JumpOverLayout extends JPanel {

    private ArrayList<Obstacle> obstacles;

    public JumpOverLayout() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        Player p = new Player();
        add(p, BorderLayout.WEST);
    }

 /*   @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(20, 450, 50, 100);
    }*/
}
