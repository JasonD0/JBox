package com.Box;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HomePage extends JPanel {
    private final static Color LIGHT_GRAY = new Color(51, 51, 51);
    private final static Color DARK_GRAY = new Color(45, 45, 45);
    private final static Color AQUA = new Color(127, 255, 212);
    private boolean mp = false;
    private JBox game;
    private JButton jump, fly, Jfloat, attack;
    private JButton exit;

    /**
     * Constructor
     * @param g    game frame
     */
    public HomePage(JBox g) {
        this.game = g;
        init();
    }

    /**
     * Sets up buttons to play each game
     */
    private void init() {
        setBackground(LIGHT_GRAY);
        Box layout = new Box(BoxLayout.Y_AXIS);

        jump = createButton("Jump", null);
        exit = createButton("Exit!", null);
        fly = createButton("Fly", null);
        Jfloat = createButton("Float", null);
        attack = createButton("Attack", null);
        layout.add(Box.createRigidArea(new Dimension(0, 100)));
        layout.add(jump);
        layout.add(Box.createRigidArea(new Dimension(0, 25)));
        layout.add(Jfloat);
        layout.add(Box.createRigidArea(new Dimension(0, 25)));
        layout.add(fly);
        layout.add(Box.createRigidArea(new Dimension(0, 25)));
        layout.add(attack);
        layout.add(Box.createRigidArea(new Dimension(0, 25)));
        layout.add(exit);
        add(layout);
    }

    /**
     * Creates button to start a particular game/exit
     * @param option    indicates the button functionality
     * @param dialog    parent element of the button
     * @return          JButton
     */
    private JButton createButton(String option, JDialog dialog) {
        JButton b = new JButton();
        b.setFocusable(false);
        b.setBackground(LIGHT_GRAY);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setOpaque(true);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dialog != null) dialog.dispose();
                switch (option) {
                    case "Jump":
                        initPlayers();
                        game.setJJump(mp);
                        break;
                    case "Fly":
                        game.setJFly();
                        break;
                    case "Float":
                        game.setJFloat();
                        break;
                    case "Attack":
                        game.setJAttack();
                        break;
                    case "Exit!":
                        game.dispose();
                        System.exit(0);
                        break;
                    case "1P":
                        mp = false;
                        break;
                    case "2P":
                        mp = true;
                        break;
                }
            }
        });

        String image = option + ".png";
        Image icon = new ImageIcon(image).getImage();
        icon = icon.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
        if (dialog != null) icon = icon.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        b.setIcon(new ImageIcon(icon));

        return b;
    }

    /**
     * Creates comoponent to allow user to choose multiplayer mode
     */
    private void initPlayers() {
        UIManager.put("Panel.background", LIGHT_GRAY);
        UIManager.put("OptionPane.background", LIGHT_GRAY);

        JOptionPane pane = new JOptionPane();
        JDialog dialog = pane.createDialog("");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JButton onePlayer = createButton("1P", dialog);
        JButton twoPlayer = createButton("2P", dialog);
        Object option[] = {onePlayer, Box.createRigidArea(new Dimension(20,0)), twoPlayer};

        JPanel panel = new JPanel(new BorderLayout());
        pane.setMessage(panel);
        pane.setOptions(option);

        dialog.setSize(new Dimension(150, 100));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
