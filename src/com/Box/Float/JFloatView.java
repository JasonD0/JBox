package com.Box.Float;

import com.Box.Obstacle;
import com.Box.Player;
import com.Box.User;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class JFloatView {
    /**
     * Updates time
     * @param g
     * @param counter
     * @param u
     */
    public void drawHeader(Graphics g, int counter, User u) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        int time = counter;
        g2d.drawString(u.getHighScore("GravityShift") + "", 175, 55);
        g2d.drawString(time + "", 775, 55);
    }

    /**
     * Draws player
     * @param g
     * @param p
     */
    public void drawPlayer(Graphics g, Player p) {
        g.setColor(Color.BLACK);
        g.fillRect(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
    }

    /**
     * Draws all obstacles
     * @param g
     * @param obstacles
     * @param fill
     * @param border
     * @param endGame
     */
    public void drawObstacles(Graphics g, List<Obstacle> obstacles, Color fill, Color border, boolean endGame) {
        for (int i = obstacles.size() - 1; i >=0 && endGame; i--) {
            Obstacle o = null;
            if (obstacles.size() > 0) o = obstacles.get(i);
            if (o == null) break;
            g.setColor(fill);
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
            g.setColor(border);
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
        }
    }

    /**
     * Shows instructions on how to play the game
     * @param parent        component to add the instructions on
     * @param background    background color for the instructions label
     */
    public void showInstructions(JFloat parent, Color background) {
        JLabel instructions = new JLabel("", SwingConstants.CENTER);
        String s = "<html><font color='rgb(127, 255, 212)'>UP/DOWN:</font> MOVE #SPACE# <font color='rgb(127, 255, 212)'>SPACE:</font> STOP #SPACE# <font color='rgb(127, 255, 212)'>P:</font> PAUSE</html>";
        s = s.replaceAll("#SPACE#",  "&emsp; &emsp; &emsp; &emsp; &emsp; &emsp;");
        instructions.setText(s);
        instructions.setFont(new Font(null, Font.BOLD, 20));
        instructions.setBackground(background);
        instructions.setForeground(Color.WHITE);
        parent.add(instructions);
    }

    /**
     * Shows platform for the player to be on
     * @param parent    component to add the platform on
     * @param c         color of the platform
     */
    public void showPlatform(JFloat parent, Color c) {
        JLabel platform = new JLabel();
        platform.setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, c));
        platform.setMaximumSize(new Dimension(1000, 10));
        platform.setMinimumSize(new Dimension(1000, 10));
        platform.setPreferredSize(new Dimension(1000,10));
        platform.setBackground(c);
        parent.add(platform);
    }

    /**
     * Shows high score and time
     * @param parent    component to add the header
     * @param c         color of the text
     */
    public void showHeader(JFloat parent, Color c) {
        String head = "\tHigh Score \t\t\t\t\t\t\t\t\t\t Time ";
        head = head.replaceAll("\\t", "         ");
        JLabel header = new JLabel(head);
        header.setMaximumSize(new Dimension(1000, 95));
        header.setMinimumSize(new Dimension(1000, 95));
        header.setPreferredSize(new Dimension(1000,95));
        header.setFont(new Font(null, Font.BOLD, 20));
        header.setForeground(c);
        parent.add(header);
    }
}
