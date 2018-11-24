package com.Box.Fly;

import com.Box.Obstacle;
import com.Box.Player;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

public class JFlyView {
    /**
     * Shows instructions on how to play the game
     * @param g
     */
    public void drawInstructions(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(null, Font.BOLD, 25));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Press any key to start. Press up to move.", 250, 100);
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
     * Draw all obstacles
     * @param g
     * @param obstacles
     * @param fill
     * @param border
     */
    public void drawObstacles(Graphics g, List<Obstacle> obstacles, Color fill, Color border) {
        for (Obstacle o : obstacles) {
            g.setColor(fill);
            g.fillRect(o.getX(), o.getY(), o.getLength(), o.getTopH());
            g.fillRect(o.getX(), o.getY2(), o.getLength(), o.getBotH());
            g.setColor(border);
            g.drawRect(o.getX(), o.getY(), o.getLength(), o.getTopH());
            g.drawRect(o.getX(), o.getY2(), o.getLength(), o.getBotH());
        }
    }


}
