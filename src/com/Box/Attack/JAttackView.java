package com.Box.Attack;

import com.Box.Player;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class JAttackView {
    private int angle;
    public JAttackView() {
        this.angle = 10;
    }
    public void rollAttack(Graphics g, int x, int y, int angleIncrease, Color border) {
        Graphics2D g2d = (Graphics2D) g;
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
    }

    public void drawPlayer(Graphics g, Player p) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        Rectangle player = new Rectangle(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
        g2d.fill(player);
    }

    public void drawEnemy(Graphics g, Enemy enemy) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(enemy.getColor());
        Rectangle e = new Rectangle(enemy.getXOrd(), enemy.getYOrd() - 150, enemy.getPlayerLength(), enemy.getPlayerHeight());
        g2d.fill(e);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(null, Font.BOLD, 15));
        g2d.drawString(enemy.getStatus(), enemy.getXOrd()  /*+ enemy.getPlayerLength()/3 */,enemy.getYOrd() - 150 - 10);
    }
}
