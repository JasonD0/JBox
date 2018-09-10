import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JumpOver extends JFrame {
    private static final int LENGTH = 1000;
    private static int HEIGHT = 535;  //1000 multiplayer     535 single
    private static final String TITLE = "JumpOver";
    private static Box boxLayout;
    private static boolean mp = false;

    public JumpOver() {
        init();
    }

    private void init() {
        setTitle(TITLE);
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setFocusable(true);

        boxLayout = new Box(BoxLayout.Y_AXIS);
        setHome();

        pack();
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setHome() {
        boxLayout.removeAll();
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setMaximumSize(new Dimension(LENGTH, HEIGHT));
        setMinimumSize(new Dimension(LENGTH, HEIGHT));
        HomePage hp = new HomePage(this);
        boxLayout.add(hp);
        setLocationRelativeTo(null);
        setContentPane(boxLayout);
    }

    public void setJumpOver() {
        boxLayout.removeAll();
        HEIGHT = (mp) ? 1000 : HEIGHT;
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setMaximumSize(new Dimension(LENGTH, HEIGHT));
        setMinimumSize(new Dimension(LENGTH, HEIGHT));
        JumpOverLayout jpl = new JumpOverLayout(this, mp);
        boxLayout.add(jpl);
        setLocationRelativeTo(null);
        setContentPane(boxLayout);
    }

    /*
    @Override
    public void actionPerformed(ActionEvent e) {
        Rectangle bp = b.getBounds();
        Point mp = panel.getMousePosition();
        if (bp != null && mp != null) {
            highlight = bp.contains(mp);
        }
        repaint();
    }*/

    public static void main(String[] arg) {
      //  EventQueue.invokeLater(() -> {
            JumpOver game = new JumpOver();
          //  game.setVisible(true);
        //});
    }
}
