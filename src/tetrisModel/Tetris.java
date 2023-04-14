package tetrisModel;

import tetrisModel.figures.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tetris {
    Map<Integer, Class> figureMap;
    private Field field;
    private AbsFigure figure;
    private boolean gameIsStarted = false;
    private Random randomFigure = new Random();
    private int numOfFigures = 7;
    private String player;
    private ScoreWritter scoreWritter = new ScoreWritter();
    public Tetris() throws FileNotFoundException {
        field = new Field();
        figureMap = new HashMap<Integer, Class>();
        figureMap.put(0, Line.class);
        figureMap.put(1, Square.class);
        figureMap.put(2, NightFigureLeft.class);
        figureMap.put(3, NightFigureRight.class);
        figureMap.put(4, FigureT.class);
        figureMap.put(5, ZigZagLeft.class);
        figureMap.put(6, ZigZagRight.class);
    }
    AbsFigure genFigure() throws InstantiationException, IllegalAccessException {
        int num = randomFigure.nextInt(numOfFigures);
        figure = (AbsFigure) figureMap.get(num).newInstance();
        return figure;
    }
    public void startGame() throws InstantiationException, IllegalAccessException {
        field.resetField();
        field.setFigure(genFigure());
        gameIsStarted = true;
    }
    public void endGame() throws IOException {
        gameIsStarted = false;
        scoreWritter.writeScore(player, field.score());
        field.resetScore();
        field.resetField();
    }
    public void nextTurn(){
        field.refreshField();
    }
    public void nextFigure() throws InstantiationException, IllegalAccessException {
        field.refreshField();
        field.setFigure(genFigure());

    }
    public int score(){
        return field.score();
    }
    public Integer nextLineToReset(){
        return field.nextLineToReset();
    }
    public boolean isStarted(){
        return gameIsStarted;
    }
    public boolean figureIsSet(){
        return field.figureIsSet();
    }
    public byte positionState(int line, int column){
        return field.positionState(line, column);
    }
    public Position figurePositiond(int blockInd){
        return figure.blockPosition(blockInd);
    }
    public  void rightMove() {
        field.moveRight();
    }
    public void leftMove(){
        field.moveLeft();
    }
    public void rightRotate(){
        field.rotateRight();
    }
    public void leftRotate(){
        field.rotateLeft();
    }
    public boolean isLost(){
        return field.isLost();
    }
    public void stopGame(){
        try {
            scoreWritter.writeScore(player, field.score());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameIsStarted = false;
        field.resetScore();
    }
    public void setPlayer(String pl){
        player = pl;
    }
}
