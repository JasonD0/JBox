package com.Box.Attack;

import com.Box.Float.JFloat;
import com.Box.Player;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class JAttackView {
    private int angle;
    private Rectangle platform, player, enemy;
    public JAttackView() {
        this.angle = 10;
    }

    public void rollAttack(Graphics g, int x, int y, int angleIncrease, Color border) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        Rectangle player = new Rectangle(x, y, 50, 50);
        g2d.rotate(Math.toRadians(angle), x + 50/2, y + 50/2);
        this.angle = (this.angle + angleIncrease) % 360;
        //g2d.draw(player);
        g2d.fill(player);
        if (angleIncrease > 5) {
            g2d.setColor(border);
            g2d.drawRect(x, y, 50, 50);
        }
        g2d.dispose();
    }

    public void drawPlayer(Graphics g, Player p) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        g2d.rotate(0);
        Rectangle player = new Rectangle(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
        g2d.fill(player);
        g2d.dispose();
    }

    public void drawEnemy(Graphics g, Enemy enemy) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(enemy.getColor());
        Rectangle e = new Rectangle(enemy.getXOrd(), enemy.getYOrd(), enemy.getPlayerLength(), enemy.getPlayerHeight());
        g2d.fill(e);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(null, Font.BOLD, 15));
        g2d.drawString(enemy.getStatus(), enemy.getXOrd()  /*+ enemy.getPlayerLength()/3 */,enemy.getYOrd() - 10);
        g2d.dispose();
    }

    public void drawPlatform(Graphics g, Color c) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(c);
        platform = new Rectangle(0, 600, 1255, 20);
        g2d.fill(platform);
        g2d.dispose();
    }

    public void hideGlitch(Graphics g, Color c) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(c);
        g2d.fillRect(0, 620, 1255, 80);
        g2d.dispose();
    }
}
