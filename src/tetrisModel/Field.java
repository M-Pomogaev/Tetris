package tetrisModel;

import tetrisModel.figures.AbsFigure;
import tetrisModel.figures.Position;

import java.util.*;

import static java.lang.Math.pow;
import static tetrisModel.figures.AbsFigure.*;

public class Field {
    public final static int columns = 10;
    public final static int lines = 20;
    public final static int maxLine = lines - 1;
    public final static int maxColumn = columns - 1;
    public final static int minLine = 0;
    public final static int minColumn = 0;
    public byte emptySlot = 0;
    public byte fullSlot = 1;
    private boolean canLeftMove;
    private boolean canRightMove;
    private boolean canDownMove;
    private boolean gameIsLost;
    private boolean canLeftRotate;
    private boolean canRightRotate;
    private byte[][] field = new byte[lines][columns];
    private AbsFigure figure;
    private int score;
    private int scorePerLine = 1000;
    private int multiplier = 2;
    private Queue<Integer> linesToReset = new PriorityQueue<>();
    void resetField(){
        for (int line = 0; line < lines; ++line){
            for (int collumn = 0; collumn < columns; ++collumn){
                field[line][collumn] = emptySlot;
            }
        }
        score = 0;
        canDownMove = true;
        gameIsLost = false;
    }
    public Field(){
        resetField();
    }
    public void cheackMove(){
        canLeftMove = true;
        canDownMove = true;
        canRightMove = true;
        for (int blockInd = 0; blockInd < AbsFigure.numOfBlocks; ++blockInd){
            Position blockPosition = figure.blockPosition(blockInd);
            if (blockPosition.column() == maxColumn || field[blockPosition.line()][blockPosition.column()+1] != emptySlot){
                canRightMove = false;
            }
            if (blockPosition.line() == minLine || field[blockPosition.line()-1][blockPosition.column()] != emptySlot){
                canDownMove = false;
            }
            if (blockPosition.column() == minColumn || field[blockPosition.line()][blockPosition.column()-1] != emptySlot){
                canLeftMove = false;
            }
        }
    }
    public boolean changeRotationFlag(Position[] state){
        for(int block = 0; block < numOfBlocks; ++block){
            if (state[block].column() < minColumn || state[block].column() > maxColumn || state[block].line() < minLine
                    || positionState(state[block].line(), state[block].column()) == fullSlot){
                return false;
            }
        }
        return true;
    }
    public void checkRotate(){
        Position[] state = figure.getRotate(leftRotate);
        canLeftRotate = changeRotationFlag(state);
        state = figure.getRotate(rightRotate);
        canRightRotate = changeRotationFlag(state);
    }
    public void moveLeft(){
        if (canLeftMove) {
            figure.left();
            cheackMove();
        }
    }
    public void moveRight(){
        if (canRightMove) {
            figure.right();
            cheackMove();
        }
    }
    public void moveDown(){
        if (canDownMove){
            figure.down();
            cheackMove();
        }
    }
    public void rotateLeft(){
        checkRotate();
        if (canLeftRotate){
            figure.rotateLeft();
        }
    }
    public void rotateRight(){
        checkRotate();
        if (canRightRotate){
            figure.rotateRight();
        }
    }
    int score(){
        cheakScoreAndField();
        return score;
    }
    void freeLine(int lineInd){
        for (int line = lineInd; line < lines - 1; ++line){
            for (int column = 0; column < columns; ++column) {
                field[line][column] = field[line + 1][column];
            }
        }
        for (int column = 0; column < columns; ++column){
            field[maxLine][column] = emptySlot;
        }
    }
    void cheakScoreAndField(){
        linesToReset.clear();
        boolean lineIsFull = true;
        int linesInARow = 0;
        for (int line = 0; line < maxLine; ++line){
            for (int column = 0; column < columns; ++column){
                if (field[line][column] == emptySlot){
                    lineIsFull = false;
                }
            }
            if (lineIsFull == true){
                linesToReset.add(line);
                ++linesInARow;
                freeLine(line);
                --line;
            }
            else {
                score += linesInARow*scorePerLine*pow(multiplier, linesInARow-1);
                linesInARow = 0;
            }
            lineIsFull = true;
        }
    }
    public Integer nextLineToReset(){
        return linesToReset.poll();
    }
    void refreshField(){
        if (!canDownMove){
            for (int blockInd = 0; blockInd < AbsFigure.numOfBlocks; ++blockInd) {
                Position blockPosition = figure.blockPosition(blockInd);
                field[blockPosition.line()][blockPosition.column()] = fullSlot;
            }
        }
        else{
            moveDown();
        }
    }
    void setFigure(AbsFigure newFigure){
        figure = newFigure;
        cheackMove();
        if (!canDownMove){
            gameIsLost = true;
        }
    }
    public boolean figureIsSet(){
        return !canDownMove;
    }
    public boolean isLost(){
        return gameIsLost;
    }
    public byte positionState(int line, int column){
        return field[line][column];
    }
    void resetScore(){
        score = 0;
    }
}
