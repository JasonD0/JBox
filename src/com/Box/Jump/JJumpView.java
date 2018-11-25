package com.Box.Jump;

import com.Box.Obstacle;
import com.Box.Player;
import com.Box.User;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class JJumpView {
    /**
     * Draws game over screen when collision occurs for multiplayer
     * @param g
     * @param y    y ordinate to show the gameover message
     */
    public void drawGameOver(Graphics g, int y) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(null, Font.BOLD, 100));
        g.drawString("Game Over", 220, y);
    }

    /**
     * Shows time and high score
     * @param g
     * @param counter
     * @param multiplayer
     * @param p1_dead
     * @param p2_dead
     * @param u
     */
    public void drawHeader(Graphics g, int counter, boolean multiplayer, int p1_dead, int p2_dead, User u) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 20));
        g2d.setColor(Color.WHITE);

        int time = counter;
        // extra header for second player
        if (multiplayer) {
            time = (p2_dead > 0) ? p2_dead : counter;
            g2d.drawString(u.getHighScore("JumpOver") + "", 175, 513);
            g2d.drawString(time + "", 775, 513);
            time = (p1_dead > 0) ? p1_dead : counter;
        }

        g2d.drawString(u.getHighScore("JumpOver") + "", 175, 55);
        g2d.drawString(time + "", 775, 55);
    }

    /**
     * Draws platform for the player and obstacles
     * @param g
     * @param c    color of the platform
     */
    public void drawPlatform(Graphics g, Color c) {
        g.setColor(c);
        g.fillRect(0, 450, 1000, 10);
    }


    /**
     * Draws all obstacles
     * @param g
     * @param obstacles
     * @param fill
     * @param border
     * @param offset
     * @param p1_dead
     * @param p2_dead
     * @param multiplayer
     */
    public void drawObstacles(Graphics g, ArrayList<Obstacle> obstacles, Color fill, Color border, int offset, int p1_dead, int p2_dead, boolean multiplayer) {
        for (Obstacle o : obstacles) {
            if (p1_dead == -1) {
                g.setColor(fill);
                g.fillRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
                g.setColor(border);
                g.drawRect(o.getX(), o.getY(), o.getLength(), o.getHeight());
            }
            // draws obstacles for the second player
            if (multiplayer && p2_dead == -1) {
                g.setColor(fill);
                g.fillRect(o.getX(), o.getY() + offset, o.getLength(), o.getHeight());
                g.setColor(Color.CYAN);
                g.drawRect(o.getX(), o.getY() + offset, o.getLength(), o.getHeight());
            }
        }
    }

    /**
     * Draws player(s)
     * @param g
     * @param p1
     * @param p2
     * @param multiplayer
     */
    public void drawPlayer(Graphics g, Player p1, Player p2, boolean multiplayer) {
        g.setColor(Color.BLACK);
        g.fillRect(p1.getXOrd(), p1.getYOrd(), p1.getPlayerLength(), p1.getPlayerHeight());

        // draws second player
        if (multiplayer) {
            g.setColor(Color.WHITE);
            g.fillRect(p2.getXOrd(), p2.getYOrd(), p2.getPlayerLength(), p2.getPlayerHeight());
        }
    }

    /**
     * Shows instructions
     * @param g
     * @param p1_dead
     * @param p2_dead
     * @param multiplayer
     * @param counter
     */
    public void drawInstructions(Graphics g, int p1_dead, int p2_dead, boolean multiplayer, int counter) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 25));
        g2d.setColor(Color.WHITE);

        if (p1_dead == -1) {
            if (counter < 5) g2d.drawString("PRESS UP TO JUMP", 360, 225);
            else if (counter >= 15 && counter < 20) g2d.drawString("PRESS P TO PAUSE/UNPAUSE", 300, 225);
            else if (counter >= 35 && counter < 40) g2d.drawString("PRESS DOWN TO DUCK", 333, 225);
        }
        // shows instructions for the second player
        if (multiplayer) {
            if (p2_dead > 0) return;
            if (counter < 5) g2d.drawString("PRESS W TO JUMP", 360, 675);
            else if (counter >= 15 && counter < 20) g2d.drawString("PRESS ESC TO PAUSE/UNPAUSE", 285, 675);
            else if (counter >= 35 && counter < 40) g2d.drawString("PRESS S TO DUCK", 363, 675);
        }
    }

    /**
     * Hides glitching player when down pressed consecutively
     * @param g
     * @param c
     */
    public void hideGlitch(Graphics g, Color c) {
        g.setColor(c);
        g.fillRect(0, 451, 100, 75);
    }


    /**
     * Create component showing high score and time
     * @param c         color
     * @param pos       position on the screen to add header on
     * @param parent    component for which the header is added to
     */
    public void initHeader(JJump parent, String pos, Color c) {
        String head = "\tHigh Score \t\t\t\t\t\t\t\t\t\t Time ";
        head = head.replaceAll("\\t", "         ");
        JLabel header = new JLabel(head);
        header.setMaximumSize(new Dimension(1000, 95));
        header.setMinimumSize(new Dimension(1000, 95));
        header.setPreferredSize(new Dimension(1000,95));
        header.setFont(new Font(null, Font.BOLD, 20));
        header.setForeground(c);
        parent.add(header, pos);
    }

    /**
     * Create platform for the player and obstacles
     * @param parent          component for which the header is added to
     * @param pos             position on the screen to add platform
     * @param multiplayer
     * @param c2              color of the 2nd platform
     * @param background      background color of the screen
     * @return
     */
    public void initPlatform(JJump parent, String pos, boolean multiplayer, Color c2, Color background) {
        JLabel platform = new JLabel();
        Color c = (multiplayer) ? Color.CYAN : c2;
        platform.setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, c));
        platform.setMaximumSize(new Dimension(1000, 45));
        platform.setMinimumSize(new Dimension(1000, 45));
        platform.setPreferredSize(new Dimension(1000,45));
        platform.setBackground(background);
        platform.setOpaque(true);
        parent.add(platform, pos);
    }
}
