package com.Box.Attack;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class JAttackView {

    public void enemyRollAttack(Graphics g, Enemy e) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(e.getColor());
        Rectangle enemy = e.getBoundary();
        //Ellipse2D.Double enemy = new Ellipse2D.Double(e.getXOrd(), e.getYOrd(), e.getPlayerLength(), e.getPlayerHeight());
        g2d.rotate(Math.toRadians(e.getRotationAngle()), e.getXOrd() + e.getPlayerLength()/2, e.getYOrd() + e.getPlayerHeight()/2);
        g2d.fill(enemy);
        g2d.dispose();
    }

    public void drawPlayer(Graphics g, JAttackPlayer p) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        //g2d.rotate(0);
        g2d.rotate(Math.toRadians(p.getAngle()), p.getXOrd() + p.getPlayerLength()/2, p.getYOrd() + p.getPlayerHeight()/2);
        Rectangle player = new Rectangle(p.getXOrd(), p.getYOrd(), p.getPlayerLength(), p.getPlayerHeight());
        g2d.fill(player);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(null, Font.BOLD, 10));
        g2d.drawString(p.getStatus(), p.getXOrd(), p.getYOrd() - 15);
        if (p.getAngle() == 0) {
            double percentHealth = (p.getHealth() <= 0) ? 0 : (double)p.getHealth()/100;
            double redBarWidth = p.getPlayerLength() * percentHealth;
            int redBarXOrd = p.getXOrd() + (int) redBarWidth;
            int grayBarLength = p.getPlayerLength() - (int) redBarWidth;

            g2d.setColor(Color.RED);
            g2d.fillRect(p.getXOrd(), p.getYOrd() - 7,  (int) redBarWidth, 2);

            g2d.setColor(Color.GRAY);
            g2d.fillRect(redBarXOrd, p.getYOrd() - 7, grayBarLength, 2);
        }
        g2d.dispose();
    }

    public void drawEnemy(Graphics g, Enemy enemy) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(enemy.getColor());
        Rectangle e = new Rectangle(enemy.getXOrd(), enemy.getYOrd(), enemy.getPlayerLength(), enemy.getPlayerHeight());
        g2d.rotate(Math.toRadians(enemy.getRotationAngle()), enemy.getXOrd() + enemy.getPlayerLength()/2, enemy.getYOrd() + enemy.getPlayerHeight()/2);
        g2d.fill(e);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(null, Font.BOLD, 15));
        g2d.drawString(enemy.getStatus(), enemy.getXOrd(),enemy.getYOrd() - 15);
        if (enemy.getRotationAngle() == 0) {
            double percentHealth = (enemy.getHealth() <= 0) ? 0 : (double)enemy.getHealth()/100;
            double redBarWidth = enemy.getPlayerLength() * percentHealth;
            int redBarXOrd = enemy.getXOrd() + (int) redBarWidth;
            int grayBarLength = enemy.getPlayerLength() - (int) redBarWidth;

            g2d.setColor(Color.RED);
            g2d.fillRect(enemy.getXOrd(), enemy.getYOrd() - 7,  (int) redBarWidth, 3);

            g2d.setColor(Color.GRAY);
            g2d.fillRect(redBarXOrd, enemy.getYOrd() - 7, grayBarLength, 3);
        }
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
/*
    public void knockBack(Graphics g, Player p) {
        Graphics2D g2d = (Graphics2D) g.create();
        Rectangle player = p.getBoundary();
        g2d.fill(player);
        g2d.dispose();
    }*/
}
