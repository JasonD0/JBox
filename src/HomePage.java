import javax.swing.*;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

// add buttons to label      add animation to panel

public class HomePage extends JPanel {
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static Color AQUA = new Color(127, 255, 212);
    private JumpOver game;
    private JButton jumpOver;
    private JButton exit;

    public HomePage(JumpOver g) {
        this.game = g;
        init();
    }

    private void init() {
        setBackground(LIGHT_GRAY);
        Box layout = new Box(BoxLayout.Y_AXIS);

        jumpOver = createButton("JumpOver");
        exit = createButton("Exit!");
        layout.add(Box.createRigidArea(new Dimension(0, 200)));
        layout.add(jumpOver);
        layout.add(Box.createRigidArea(new Dimension(0, 25)));
        layout.add(exit);
        add(layout);
    }

    private JButton createButton(String option) {
        JButton b = new JButton();
        b.setFocusable(false);
        b.setBorderPainted(false);
        b.setBackground(LIGHT_GRAY);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setOpaque(true);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (option) {
                    case "JumpOver":
                        game.setJumpOver();
                        break;
                    case "Exit!":
                        game.dispose();
                        System.exit(0);
                        break;
                }
            }
        });

        String image = option + ".png";
        Image icon = new ImageIcon(image).getImage();
        icon = icon.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
        b.setIcon(new ImageIcon(icon));
        return b;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
