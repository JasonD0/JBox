package com.GameCenter;

import com.GameCenter.Flappy.FlappyLayout;
import com.GameCenter.GraviyShift.GravityShiftLayout;
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import com.GameCenter.JumpOver.*;

public class GameCenter extends JFrame {
    private static final int LENGTH = 1000;
    private static int HEIGHT = 535;
    private static final String TITLE = "JumpOver";
    private static Box boxLayout;
    private User u;

    public GameCenter() {
        u = new User();
        init();
    }

    private void init() {
        setTitle(TITLE);
        setPreferredSize(new Dimension(LENGTH, HEIGHT));
        setMaximumSize(new Dimension(LENGTH, HEIGHT));
        setMinimumSize(new Dimension(LENGTH, HEIGHT));
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
        setContentPane(boxLayout);
        pack();
        setLocationRelativeTo(null);
    }

    public void setJumpOver(boolean mp) {
        boxLayout.removeAll();
        int height = (mp) ? 1000 : HEIGHT;
        setPreferredSize(new Dimension(LENGTH, height));
        setMaximumSize(new Dimension(LENGTH, height));
        setMinimumSize(new Dimension(LENGTH, height));
        JumpOverLayout jpl = new JumpOverLayout(this, mp, u);
        boxLayout.add(jpl);
        setLocationRelativeTo(null);
        setContentPane(boxLayout);
    }

    public void setFlappy() {
        boxLayout.removeAll();
        int height = (false) ? 1000 : HEIGHT;
        setPreferredSize(new Dimension(LENGTH, height));
        setMaximumSize(new Dimension(LENGTH, height));
        setMinimumSize(new Dimension(LENGTH, height));
        FlappyLayout fp = new FlappyLayout(this);
        boxLayout.add(fp);
        setLocationRelativeTo(null);
        setContentPane(boxLayout);
    }

    public void setGravityShift() {
        boxLayout.removeAll();
        int height = (false) ? 1000 : HEIGHT;
        setPreferredSize(new Dimension(LENGTH, height));
        setMaximumSize(new Dimension(LENGTH, height));
        setMinimumSize(new Dimension(LENGTH, height));
        GravityShiftLayout gs = new GravityShiftLayout(this,  u);
        boxLayout.add(gs);
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
            GameCenter game = new GameCenter();
          //  game.setVisible(true);
        //});
    }
}
