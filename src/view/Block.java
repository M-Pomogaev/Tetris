package view;

import javax.swing.*;
import java.awt.*;

public class Block extends JPanel {
    public static final int blockSize = 25;
    private int x;
    private int y;
    public Block(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawRect(x, y, blockSize, blockSize);
    }
}
