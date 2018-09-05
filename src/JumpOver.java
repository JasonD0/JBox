import javax.swing.*;
import java.awt.*;

public class JumpOver extends JFrame implements Runnable {

    public static final int LENGTH = 1000;
    public static final int HEIGHT = 700;
    public static final String TITLE = "JumpOver";
    private Box boxLayout;

    public static void main(String[] arg) {
        JumpOver game = new JumpOver();
        SwingUtilities.invokeLater(game);
    }

    @Override
    public void run() {
        setTitle(TITLE);
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setFocusable(true);

        boxLayout = new Box(BoxLayout.Y_AXIS);
        JumpOverLayout jpl = new JumpOverLayout(this);
        boxLayout.add(jpl);

        setContentPane(boxLayout);

        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
