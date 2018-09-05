import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class User {
    private int highScore;

    public User() {
        this.highScore = 0;
    }

    public int getHighScore() {
        if (highScore == 0) readHighScore();
        return highScore;
    }

    public void setHighScore(int score) {
        if (score <= highScore) return;
        try {
            PrintWriter pw = new PrintWriter("user_info.txt");
            pw.println(score);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readHighScore() {
        File userFile = new File("user_info.txt");
        try {
            userFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Scanner input = new Scanner(userFile);
            while (input.hasNextInt()) highScore = input.nextInt();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
