package com.Box.Float;

import com.Box.Obstacle;
import java.awt.Color;
import java.util.ArrayList;

public class JFloatModel {
    public final static Color AQUA = new Color(127, 255, 212);
    public final static Color LIGHT_GRAY = new Color(51, 51, 51);
    public final static Color DARK_GRAY = new Color(45, 45, 45);
    public final static int GAME_HEIGHT = 405;
    public final static int GAME_LENGTH = 1000;
    private ArrayList<Obstacle> obstacles;
    private int delayMin, delayMax;
    private int obstacleVel;

    public JFloatModel() {
        this.obstacles = new ArrayList<>();
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public void addObstacle(Obstacle o) {
        this.obstacles.add(o);
    }

    public void removeObstacle(int index) {
        this.obstacles.remove(index);
    }

    public void removeAllObstacles() {
        this.obstacles.removeAll(obstacles);
    }

    public int getDelayMin() {
        return this.delayMin;
    }

    public int getDelayMax() {
        return this.delayMax;
    }

    public void setDelayRange(int min, int max) {
        this.delayMin = min;
        this.delayMax = max;
    }

    public int getObstacleVel() {
        return this.obstacleVel;
    }

    public void setObstacleVel(int vel) {
        this.obstacleVel = vel;
    }
}
