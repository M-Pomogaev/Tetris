package tetrisModel;

import java.io.*;
import java.util.Scanner;


public class ScoreWritter {
    File scoreFile = new File("Score.txt");
    File tmpFile = new File("temp.txt");
    Scanner scan;
    PrintWriter writer;
    String space = " ";
    String getName(String str) {
        Scanner scanner = new Scanner(str);
        scanner.useDelimiter(space);
        String name = scanner.next();
        return name;
    }

    int getScore(String str){
        Scanner scanner = new Scanner(str);
        int score = 0;
        scanner.useDelimiter(space);
        scanner.next();
        if (scanner.hasNextInt()){
            score = scanner.nextInt();
        }
        return score;
    }
    void copy() throws FileNotFoundException {
        Scanner scanner = new Scanner(tmpFile);
        PrintWriter print = new PrintWriter(scoreFile);
        while (scanner.hasNextLine()){
            print.println(scanner.nextLine());
        }
        print.close();
        scanner.close();
    }
    void writeScore(String name, int score) throws IOException {
        scan = new Scanner(scoreFile);
        writer = new PrintWriter(tmpFile);
        String newLine, lineName;
        while (scan.hasNextLine()){
            newLine = scan.nextLine();
            lineName = getName(newLine);
            if (!name.equals(lineName)){
                writer.println(newLine);
            }
            else {
                int newScore = getScore(newLine);
                if (newScore > score) {
                    score = newScore;
                }
            }
        }
        writer.println(name + space + score);
        writer.close();
        scan.close();
        copy();
    }
}
