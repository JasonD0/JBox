package com.Box;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class User {
    private Map<String, Integer> highScores;

    public User() {
        highScores = new HashMap<>();
    }

    public int getHighScore(String game) {
        readHighScores(game);
        return highScores.getOrDefault(game, 0);
    }

    public void setHighScore(int score, String game) {
        if (highScores.get(game) > score) return;
        try {
            PrintWriter pw = new PrintWriter("user_info.txt");
            for (Map.Entry<String, Integer> m : highScores.entrySet()) {
                if (m.getKey().compareTo(game) == 0) {
                    pw.println(game + " " + score);
                    highScores.put(game, score);
                } else {
                    pw.println(m.getKey() + " " + m.getValue());
                }
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readHighScores(String game) {
        File userFile = new File("user_info.txt");
        try {
            userFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Scanner input = new Scanner(userFile);
            while (input.hasNext()) {
                String[] in = input.nextLine().split(" ");
                if (in.length < 2) continue;
                highScores.put(in[0], Integer.parseInt(in[1]));
            }
            if (!highScores.containsKey(game)) highScores.put(game, 0);
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
