package tetrisModel.figures;

import static tetrisModel.Field.maxColumn;
import static tetrisModel.Field.maxLine;

public class ZigZagLeft extends AbsFigure{
    @Override
    void initStates(){
        states[0][0].set(center.line()+1, center.column()-1);
        states[0][1].set(center.line()+1, center.column());
        states[0][2].set(center.line(), center.column());
        states[0][3].set(center.line(), center.column()+1);

        states[1][0].set(center.line()+1, center.column()+1);
        states[1][1].set(center.line(), center.column()+1);
        states[1][2].set(center.line(), center.column());
        states[1][3].set(center.line()-1, center.column());

        states[2][0].set(center.line()+1, center.column()-1);
        states[2][1].set(center.line()+1, center.column());
        states[2][2].set(center.line(), center.column());
        states[2][3].set(center.line(), center.column()+1);

        states[3][0].set(center.line()+1, center.column()+1);
        states[3][1].set(center.line(), center.column()+1);
        states[3][2].set(center.line(), center.column());
        states[3][3].set(center.line()-1, center.column());
    }
    public ZigZagLeft(){
        center.set(maxLine - 1, maxColumn/2);
        initStates();
        setState(0);
    }
}