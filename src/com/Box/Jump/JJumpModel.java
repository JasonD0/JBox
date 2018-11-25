package com.Box.Jump;

import com.Box.Obstacle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class JJumpModel {
    public final static int GAME_LENGTH = 1000;
    public final static int GAME_HEIGHT1 = 450;
    public final static int GAME_HEIGHT2 = 916;
    public final static int PLAYER_HEIGHT = 50;
    public final static int OFFSET = 466;   // offset height for player2 and their obstacles
    public final static Color AQUA = new Color(127, 255, 212);
    public final static Color LIGHT_GRAY = new Color(51, 51, 51);
    public final static Color DARK_GRAY = new Color(45, 45, 45);
    private ArrayList<Obstacle> obstacles;
    private int delayMin, delayMax;
    private int playerVel;
    private int obstacleVel;

    public JJumpModel() {
        this.obstacles = new ArrayList<>();
        this.playerVel = 10;
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public void addObstacle(Obstacle o) {
        this.obstacles.add(o);
    }

    public void removeAllObstacles() {
        Iterator<Obstacle> itr = this.obstacles.iterator();
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
    }

    public void setDelayRange(int min, int max) {
        this.delayMin = min;
        this.delayMax = max;
    }

    public int getDelayMin() {
        return this.delayMin;
    }

    public int getDelayMax() {
        return this.delayMax;
    }

    public int getPlayerVel() {
        return this.playerVel;
    }

    public void setPlayerVel(int vel) {
        this.playerVel = vel;
    }

    public void setObstacleVel(int vel) {
        this.obstacleVel = vel;
    }

    public int getObstacleVel() {
        return this.obstacleVel;
    }
}
