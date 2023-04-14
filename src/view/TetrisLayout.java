package view;

import java.awt.*;

import static view.Block.blockSize;

public class TetrisLayout implements LayoutManager {
    public static final int lines = 20;
    public static final int columns = 10;
    public static final int shift = 1;
    private Dimension size = new Dimension();

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension minimumLayoutSize(Container c) {
        return calculateBestSize();
    }

    public Dimension preferredLayoutSize(Container c) {
        return calculateBestSize();
    }

    public void layoutContainer(Container container) {
        Component list[] = container.getComponents();
        for (int compInd = 0; compInd < list.length; compInd++) {
            list[compInd].setBounds(shift + (compInd / lines) * (shift + blockSize),
                    shift + (compInd % lines) * (shift + blockSize), blockSize, blockSize);
        }
    }

    private Dimension calculateBestSize() {
        size.width = columns * (blockSize + shift) + shift;
        size.height = lines * (blockSize + shift) + shift;
        return size;
    }
}
