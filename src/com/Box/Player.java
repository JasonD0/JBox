package com.Box;

import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Color;

public class Player extends JLabel{

    private int playerLength = 50;
    private int playerHeight = 50;
    private int x = 0, y = 0, velY = 0;

    public Player(int y, int x) {
        this.y = y;
        this.x = x;
        initPlayer();
    }

    private void initPlayer() {
        setBackground(Color.BLUE);
        setPreferredSize(new Dimension(100,100));
        setMaximumSize(new Dimension(100, 100));
        setMinimumSize(new Dimension(100, 100));
        setSize(new Dimension(100, 100));
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, playerLength, playerHeight);
    }

    public int getPlayerLength() {
        return playerLength;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }

    public void setPlayerHeight(int h) { this.playerHeight = h; }

    public int getXOrd() {
        return x;
    }

    public int getYOrd() {
        return y;
    }

    public int getVelY() { return velY; }

    public void setYord(int y) {
        this.y = y;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }

/*    public void movePlayer() {

        if (y < 240) {
            velY = 6;
            y = 242;
        }
        // create small delay at apex of jump
        else if (y <= 252 && velY > 0) {
            velY = 4;
        }
        else if (y < 399 && velY > 0) {
            velY = 6;
        }
        else if (y > PLATFORM - playerHeight) {
            velY = 0;
            y = PLATFORM - playerHeight;
        }
        y += velY;

    }*/
}
