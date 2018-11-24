package com.Box.Fly;

import com.Box.Obstacle;
import java.awt.Color;
import java.util.ArrayList;

public class JFlyModel {
    public final static Color AQUA = new Color(127, 255, 212);
    public final static Color LIGHT_GRAY = new Color(51, 51, 51);
    public final static Color DARK_GRAY = new Color(45, 45, 45);
    public final static int GAME_LENGTH = 1000;
    public final static int GAME_HEIGHT = 500;
    private int maxGapHeight, minGapHeight;
    private ArrayList<Obstacle> obstacles;

    public JFlyModel() {
        this.obstacles = new ArrayList<>();
    }

    public void setGapRange(int min, int max) {
        this.minGapHeight = min;
        this.maxGapHeight = max;
    }

    public int minGap() {
        return this.minGapHeight;
    }

    public int maxGap() {
        return this.maxGapHeight;
    }

    public void addObstacle(Obstacle o) {
        this.obstacles.add(o);
    }

    public void removeAllObstacles() {
        this.obstacles.removeAll(obstacles);
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public void removeObstacle(int index) {
        this.obstacles.remove(index);
    }
}
