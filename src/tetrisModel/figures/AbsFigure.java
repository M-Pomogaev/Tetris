package tetrisModel.figures;

import static tetrisModel.Field.*;

public abstract class AbsFigure {
    public static final int numOfBlocks = 4;
    public static final int numOfRotates = 4;
    protected Position[] blocks = new Position[numOfBlocks];
    protected Position[][] states = new Position[numOfRotates][numOfBlocks];
    protected int stateInd = 1;
    protected Position center = new Position();
    int firstState = 0;
    int lastState = 3;
    public static int rightRotate = -1;
    public static int leftRotate = 1;
    private  int noMove = 0;
    private int leftMove = -1;
    private int rightMove = 1;
    private int downMove = -1;
    AbsFigure(){
        for (int i = 0; i < numOfBlocks; ++i) {
            blocks[i] = new Position();
        }
        for (int state = 0; state < numOfRotates; ++state){
            for (int block = 0; block < numOfBlocks; ++block){
                states[state][block] = new Position();
            }
        }
    }
    boolean checkLegalBlock(Position block, int lineMove, int colomnMove){
        if (block.line()+lineMove > maxLine || block.line()+lineMove < minLine
            || block.column()+colomnMove > maxColumn || block.column()+colomnMove < minColumn){
            return false;
        }
        return true;
    }
    protected void setState(int stateI){
        for (int block = 0; block < numOfBlocks; ++block){
            if (!checkLegalBlock(blocks[block], noMove, noMove)){
                return;
            }
        }
        for (int block = 0; block < numOfBlocks; ++block){
            blocks[block].set(states[stateI][block].line(), states[stateI][block].column());
        }
        stateInd = stateI;
    }
    public Position blockPosition(int blInd){
        return  blocks[blInd];
    }
    public void left(){
        for (int block = 0; block < numOfBlocks; ++block){
            if (!checkLegalBlock(blocks[block], noMove, leftMove)){
                return;
            }
        }
        for (int block = 0; block < numOfBlocks; ++block){
            blocks[block].left();
        }
        center.left();
    }
    public void right(){
        for (int block = 0; block < numOfBlocks; ++block){
            if (!checkLegalBlock(blocks[block], noMove, rightMove)){
                return;
            }
        }
        for (int block = 0; block < numOfBlocks; ++block){
            blocks[block].right();
        }
        center.right();
    }
    public void down(){
        for (int block = 0; block < numOfBlocks; ++block){
            if (!checkLegalBlock(blocks[block], downMove, noMove)){
                return;
            }
        }
        for (int block = 0; block < numOfBlocks; ++block){
            blocks[block].down();
        }
        center.down();
    }
    abstract void initStates();
    public void rotateLeft(){
        initStates();
        ++stateInd;
        stateInd %= numOfRotates;
        setState(stateInd);
    }
    public void rotateRight(){
        if (stateInd == firstState){
            stateInd = lastState;
        }
        else {
            --stateInd;
            stateInd %= numOfRotates;
        }
        setState(stateInd);
    }
    public Position[] getRotate(int shift){
        initStates();
        int ind;
        if (shift == rightRotate && stateInd == firstState) {
            ind = lastState;
        } else {
            ind = (stateInd + shift)%numOfRotates;
        }
        return states[ind];
    }
}
