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

public class JumpOver extends JFrame implements ActionListener {
    private static final int LENGTH = 1000;
    private static final int HEIGHT = 600;  //1030 multiplayer
    private static final String TITLE = "JumpOver";
    private boolean highlight = false;
    private Timer t = new Timer(10, this);
    private Box boxLayout;
    private JPanel panel;
    private JButton b;

    public static void main(String[] arg) {
        JumpOver game = new JumpOver();
        //SwingUtilities.invokeLater(game);
        game.setTitle(TITLE);
        game.setPreferredSize(new Dimension(LENGTH, HEIGHT));
        game.setFocusable(true);

        Box boxLayout = new Box(BoxLayout.Y_AXIS);
        JumpOverLayout jpl = new JumpOverLayout(game);
        boxLayout.add(jpl);
        game.setContentPane(boxLayout);

        game.pack();
        game.setVisible(true);
        game.setResizable(false);
        game.setLocationRelativeTo(null);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
/*
    @Override
    public void run() {
        //t.start();
        setTitle(TITLE);
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setFocusable(true);

        boxLayout = new Box(BoxLayout.Y_AXIS);

        b = new JButton("Dsad");
        b.setFocusable(false);
        //b.setContentAreaFilled(false);
        //setLayout(new BorderLayout());
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.fillRect(100, 200, 100, 100);
                if (highlight) {
                    b.setBackground(Color.BLACK);
                } else {
                    b.setBackground(Color.GREEN);
                }
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.BLUE);
        //b.addMouseMotionListener();
        //panel.add(b, 2);


        //add(panel, BorderLayout.CENTER);
        JumpOverLayout jpl = new JumpOverLayout(this);
        boxLayout.add(jpl);
        setContentPane(boxLayout);
        //add(panel);

        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }*/

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Rectangle bp = b.getBounds();
        Point mp = panel.getMousePosition();
        if (bp != null && mp != null) {
            highlight = bp.contains(mp);
        }
        repaint();
    }
}
