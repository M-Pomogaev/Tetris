package tetrisModel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class FileParser {
    private FileReader reader;
    public void setFileToRead(int fileType) {
        try {
            switch (fileType) {
                case (Files.ABOUT):
                    reader = new FileReader("About.txt");
                    break;
                case(Files.SCORE):
                    reader = new FileReader("Score.txt");
                    break;
            }
        } catch (FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }
    public char getNextInfoChar() throws IOException {
        return (char)reader.read();
    }
}
