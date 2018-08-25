import javax.swing.*;
import java.awt.*;

public class JumpOver extends JFrame implements Runnable {

    public static final int LENGTH = 1000;
    public static final int HEIGHT = 700;
    public static final String TITLE = "JumpOver";

    public static void main(String[] arg) {
        JumpOver game = new JumpOver();
        SwingUtilities.invokeLater(game);
    }

    @Override
    public void run() {
        setTitle(TITLE);
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setFocusable(true);

        setLayout(new BorderLayout());
        Player p = new Player();
        add(p, BorderLayout.WEST);
        getContentPane().setBackground(Color.BLACK);

        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
