package tetrisModel.figures;

import static tetrisModel.Field.*;

public class Position {
    private int linePos;
    private int columnPos;
    public int line(){
        return linePos;
    }
    public int column(){
        return columnPos;
    }
    public void setLine(int newLine){
        linePos = newLine;
    }
    public void setColumn(int newColumn){
        columnPos = newColumn;
    }
    public void set(int newLine, int newColumn){
        linePos = newLine;
        columnPos = newColumn;
    }
    public void left(){
        if (columnPos != 0){
            --columnPos;
        }
    }
    public void right(){
        if (columnPos != columns){
            ++columnPos;
        }
    }
    public void down(){
        if (linePos != 0) {
            --linePos;
        }
    }
    static public int positionToInt(Position pos) {
        return (pos.column() * lines + (maxLine - pos.line()));
    }
    static public int positionToInt(int line, int column) {
        return (column * lines + (maxLine - line));
    }
}
