package com.Box.Attack;

import com.Box.Player;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class JAttackView {
    public void rollAttack(Graphics g, JAttackPlayer p) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        Rectangle player = new Rectangle(p.getXOrd(), p.getYOrd(), 50, 50);
        g2d.rotate(Math.toRadians(p.getAngle()), p.getXOrd() + p.getPlayerLength()/2, p.getYOrd() + p.getPlayerHeight()/2);
        g2d.fill(player);
        g2d.dispose();
    }

    public void curvedJump(Graphics g, JAttackPlayer p) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        Rectangle player = new Rectangle(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
        g2d.rotate(Math.toRadians(p.getAngle()), p.getXOrd() + p.getPlayerLength()/2, p.getYOrd() + p.getPlayerHeight()/2);
        g2d.fill(player);
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

    public void drawPlatform(Graphics g, int y, Color c) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(c);
        Rectangle platform = new Rectangle(0, y, 1255, 20);
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
