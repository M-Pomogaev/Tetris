package view;

import tetrisModel.Files;
import tetrisModel.FileParser;
import tetrisModel.Tetris;
import tetrisModel.figures.Position;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import static tetrisModel.figures.Position.positionToInt;
import static view.Block.blockSize;
import static view.TetrisLayout.*;

public class TetrisGUI {
    private FileParser model = new FileParser();
    private static JFrame frame;
    private JPanel MainPanel;
    private JButton exitButton;
    private JButton aboutButton;
    private JButton scoresButton;
    private JButton newGameButton;
    private JPanel Menu;
    private JPanel ControlPanel;
    private JPanel Game;
    private JPanel Score;
    private JPanel About;
    private JButton GameBack;
    private JButton ScoreBack;
    private JButton AboutBack;
    private JTextArea Info;
    private JTextArea Scores;
    private JPanel field;
    private JButton StartGame;
    private JTextField scoreTable;
    private JPanel PrepereGame;
    private JPanel EnterName;
    private JButton NameBack;
    private JTextPane NameField;
    private JTextPane NameText;
    private JTextArea AreaToWrite;
    private int writeDelay = 35;
    private int turnDelay = 450;
    private int moveDelay = 130;
    private Timer writeTimer;
    private Timer turnTimer;
    private Timer moveTimer;
    private char eof = (char) -1;
    private char enter = '\n';
    private int numOfBlocks = 200;
    private Block[] fieldBlocks = new Block[numOfBlocks];
    private Tetris tetrisModel = new Tetris();
    Color emptyColor = Color.BLACK;
    Color blockColor = Color.GREEN;
    int numOfColors = 6;
    Random randColor = new Random(numOfColors);
    int numOfBlocksInFigure = 4;
    boolean pressed = false;
    boolean rotated = false;
    boolean setMoreThanOneTurn = false;
    int numOfChars = 0;
    int numYouSureCh = 25;
    int numOfTooMuchCh = 40;
    int numOfStopCh = 55;
    int charClear = 8;

    public class MoveAction implements ActionListener {
        private String mode;

        public void setMode(String newMode) {
            mode = newMode;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            colorFigureOnScrean(emptyColor);
            if (mode == "left") {
                tetrisModel.leftMove();
            }
            if (mode == "right") {
                tetrisModel.rightMove();
            }
            colorFigureOnScrean(blockColor);
        }
    }

    MoveAction moveAction = new MoveAction();

    public class LeftMove extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tetrisModel.isStarted()) {
                if (!pressed) {
                    colorFigureOnScrean(emptyColor);
                    tetrisModel.leftMove();
                    colorFigureOnScrean(blockColor);
                    pressed = true;
                }
                moveAction.setMode("left");
                moveTimer.start();
            }
        }
    }

    public class RightMove extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tetrisModel.isStarted()) {
                if (!pressed) {
                    colorFigureOnScrean(emptyColor);
                    tetrisModel.rightMove();
                    colorFigureOnScrean(blockColor);
                    pressed = true;
                }
                moveAction.setMode("right");
                moveTimer.start();
            }
        }
    }

    public class StopMove extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveTimer.stop();
            pressed = false;
        }
    }

    public class DownMove extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            turnTimer.setDelay(moveDelay);
        }
    }

    public class DownStop extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            turnTimer.setDelay(turnDelay);
        }
    }

    public class RightRotate extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!rotated) {
                colorFigureOnScrean(emptyColor);
                tetrisModel.rightRotate();
                colorFigureOnScrean(blockColor);
                rotated = true;
            }
        }
    }

    public class LeftRotate extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!rotated) {
                colorFigureOnScrean(emptyColor);
                tetrisModel.leftRotate();
                colorFigureOnScrean(blockColor);
                rotated = true;
            }
        }
    }

    public class RotateStop extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            rotated = false;
        }
    }

    private void changeScrean(JPanel where, JPanel panelTo) {
        where.removeAll();
        where.add(panelTo);
        where.repaint();
        where.revalidate();
    }

    private void randColor() {
        int num = randColor.nextInt();
        switch (num % numOfColors) {
            case 0:
                blockColor = Color.GREEN;
                break;
            case 1:
                blockColor = Color.ORANGE;
                break;
            case 2:
                blockColor = Color.RED;
                break;
            case 3:
                blockColor = Color.WHITE;
                break;
            case 4:
                blockColor = Color.BLUE;
                break;
            case 5:
                blockColor = Color.MAGENTA;
        }
    }

    private void startReadingFile(int fileIntepreter, JTextArea textArea) {
        model.setFileToRead(fileIntepreter);
        AreaToWrite = textArea;
        writeTimer.start();
    }

    void startField() {
        for (int blockInd = 0; blockInd < numOfBlocks; ++blockInd) {
            fieldBlocks[blockInd].setBackground(emptyColor);
        }
        setScore();
    }

    void colorFigureOnScrean(Color color) {
        for (int blockInd = 0; blockInd < numOfBlocksInFigure; ++blockInd) {
            Position pos = tetrisModel.figurePositiond(blockInd);
            fieldBlocks[positionToInt(pos)].setBackground(color);
        }
    }

    ActionListener writter = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            char ch = eof;
            try {
                ch = model.getNextInfoChar();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            if (ch == eof) {
                writeTimer.stop();
            } else {
                AreaToWrite.append(String.valueOf(ch));
                AreaToWrite.repaint();
                AreaToWrite.revalidate();
            }
        }
    };

    void deleteLine(int lineInd) {
        boolean lineIsClean = false;
        while (!lineIsClean) {
            lineIsClean = true;
            for (int column = 0; column < columns; ++column) {
                if (fieldBlocks[positionToInt(lineInd, column)].getBackground() != emptyColor) {
                    lineIsClean = false;
                }
                int pos = positionToInt(lineInd, column);
                int newColorPos = positionToInt(lineInd + 1, column);
                Color newColor = fieldBlocks[newColorPos].getBackground();
                fieldBlocks[pos].setBackground(newColor);
            }
            ++lineInd;
        }
    }

    void deleteLines() {
        Integer line;
        line = tetrisModel.nextLineToReset();
        while (line != null) {
            deleteLine(line);
            line = tetrisModel.nextLineToReset();
        }
    }

    ActionListener makeTurn = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            colorFigureOnScrean(emptyColor);
            tetrisModel.nextTurn();
            colorFigureOnScrean(blockColor);
            if (tetrisModel.figureIsSet() && setMoreThanOneTurn) {
                if (tetrisModel.isLost()) {
                    turnTimer.stop();
                    tetrisModel.stopGame();
                } else {
                    randColor();
                    try {
                        tetrisModel.nextFigure();
                        setScore();
                        deleteLines();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    colorFigureOnScrean(blockColor);
                }
                setMoreThanOneTurn = false;
            } else if (tetrisModel.figureIsSet()) {
                setMoreThanOneTurn = true;
            }
        }
    };

    void setScore() {
        scoreTable.setText(((Integer) tetrisModel.score()).toString());
    }

    String removeEndOfLine(String str) {
        return str.substring(0, str.length() - 2);
    }

    public TetrisGUI() throws FileNotFoundException {
        $$$setupUI$$$();

        writeTimer = new Timer(writeDelay, writter);

        turnTimer = new Timer(turnDelay, makeTurn);

        moveTimer = new Timer(moveDelay, moveAction);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NameField.setBorder(BorderFactory.createLineBorder(new Color(-7351572)));
                NameText.setBorder(BorderFactory.createLineBorder(new Color(-7351572)));
                startField();
                randColor();
                setScore();
                changeScrean(ControlPanel, PrepereGame);
                changeScrean(PrepereGame, EnterName);
            }
        });
        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScrean(ControlPanel, Score);
                startReadingFile(Files.SCORE, Scores);
            }
        });
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScrean(ControlPanel, About);
                startReadingFile(Files.ABOUT, Info);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        GameBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnTimer.stop();
                try {
                    tetrisModel.endGame();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                NameField.setText("");
                changeScrean(ControlPanel, Menu);
            }
        });
        ScoreBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScrean(ControlPanel, Menu);
                Scores.setText("");
                writeTimer.stop();
            }
        });
        AboutBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScrean(ControlPanel, Menu);
                Info.setText("");
                writeTimer.stop();
            }
        });
        StartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.requestFocus();
                try {
                    if (tetrisModel.isStarted()) {
                        tetrisModel.stopGame();
                    }
                    startField();
                    tetrisModel.startGame();
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
                randColor();
                colorFigureOnScrean(blockColor);
                turnTimer.start();
            }
        });
        NameBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScrean(ControlPanel, Menu);
                NameField.setText("");
                numOfChars = 0;
                NameText.setText("Enter your name:");
            }
        });
        NameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == charClear) {
                    if (numOfChars != 0) {
                        --numOfChars;
                    }
                } else {
                    ++numOfChars;
                }
                if (numOfChars > numOfStopCh) {
                    NameText.setText("STOP!!!");
                } else {
                    if (numOfChars > numOfTooMuchCh) {
                        NameText.setText("That's already too much...");
                    } else {
                        if (numOfChars > numYouSureCh) {
                            NameText.setText("Are you sure about that name???");
                        } else {
                            NameText.setText("Enter your name:");
                        }
                    }
                }
                if (e.getKeyChar() == enter) {
                    String newName = NameField.getText();
                    newName = removeEndOfLine(newName);
                    tetrisModel.setPlayer(newName);
                    changeScrean(PrepereGame, Game);
                    numOfChars = 0;
                    NameText.setText("Enter your name:");
                }
            }
        });
    }

    public static void main(String[] args) throws FileNotFoundException {
        frame = new JFrame("TetrisGUI");
        frame.setContentPane(new TetrisGUI().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        MainPanel = new JPanel();
        MainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.setBackground(new Color(-13957510));
        MainPanel.setEnabled(false);
        MainPanel.setForeground(new Color(-16777216));
        ControlPanel = new JPanel();
        ControlPanel.setLayout(new CardLayout(0, 0));
        MainPanel.add(ControlPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(700, 800), new Dimension(700, 800), new Dimension(700, 800), 0, false));
        ControlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-7351572)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, ControlPanel.getFont()), new Color(-4473925)));
        Menu = new JPanel();
        Menu.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        Menu.setBackground(new Color(-13957510));
        Menu.setEnabled(true);
        Font MenuFont = this.$$$getFont$$$("Consolas", -1, -1, Menu.getFont());
        if (MenuFont != null) Menu.setFont(MenuFont);
        Menu.setForeground(new Color(-8250693));
        ControlPanel.add(Menu, "Card1");
        exitButton = new JButton();
        exitButton.setBackground(new Color(-16777216));
        Font exitButtonFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, exitButton.getFont());
        if (exitButtonFont != null) exitButton.setFont(exitButtonFont);
        exitButton.setForeground(new Color(-7351572));
        exitButton.setText("Exit");
        Menu.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutButton = new JButton();
        aboutButton.setBackground(new Color(-16777216));
        Font aboutButtonFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, aboutButton.getFont());
        if (aboutButtonFont != null) aboutButton.setFont(aboutButtonFont);
        aboutButton.setForeground(new Color(-7351572));
        aboutButton.setText("About");
        Menu.add(aboutButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scoresButton = new JButton();
        scoresButton.setBackground(new Color(-16777216));
        Font scoresButtonFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, scoresButton.getFont());
        if (scoresButtonFont != null) scoresButton.setFont(scoresButtonFont);
        scoresButton.setForeground(new Color(-7351572));
        scoresButton.setText("Scores");
        Menu.add(scoresButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newGameButton = new JButton();
        newGameButton.setBackground(new Color(-16777216));
        newGameButton.setEnabled(true);
        Font newGameButtonFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, newGameButton.getFont());
        if (newGameButtonFont != null) newGameButton.setFont(newGameButtonFont);
        newGameButton.setForeground(new Color(-7351572));
        newGameButton.setText("New Game");
        Menu.add(newGameButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        Menu.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        Menu.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        Menu.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        Menu.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        Score = new JPanel();
        Score.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        Score.setBackground(new Color(-13957510));
        ControlPanel.add(Score, "Card3");
        ScoreBack = new JButton();
        ScoreBack.setBackground(new Color(-16777216));
        Font ScoreBackFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, ScoreBack.getFont());
        if (ScoreBackFont != null) ScoreBack.setFont(ScoreBackFont);
        ScoreBack.setForeground(new Color(-7351572));
        ScoreBack.setText("Back");
        Score.add(ScoreBack, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        Score.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(140, -1), new Dimension(140, -1), new Dimension(140, -1), 0, false));
        Scores = new JTextArea();
        Scores.setBackground(new Color(-13957510));
        Scores.setEditable(false);
        Font ScoresFont = this.$$$getFont$$$("Droid Sans Mono", -1, 18, Scores.getFont());
        if (ScoresFont != null) Scores.setFont(ScoresFont);
        Scores.setForeground(new Color(-7351572));
        Scores.setLineWrap(true);
        Score.add(Scores, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        Score.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        Score.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(65, -1), new Dimension(65, -1), new Dimension(65, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        Score.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer9 = new com.intellij.uiDesigner.core.Spacer();
        Score.add(spacer9, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        About = new JPanel();
        About.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        About.setBackground(new Color(-13957510));
        ControlPanel.add(About, "Card4");
        AboutBack = new JButton();
        AboutBack.setBackground(new Color(-16777216));
        AboutBack.setEnabled(true);
        Font AboutBackFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, AboutBack.getFont());
        if (AboutBackFont != null) AboutBack.setFont(AboutBackFont);
        AboutBack.setForeground(new Color(-7351572));
        AboutBack.setText("Back");
        About.add(AboutBack, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer10 = new com.intellij.uiDesigner.core.Spacer();
        About.add(spacer10, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer11 = new com.intellij.uiDesigner.core.Spacer();
        About.add(spacer11, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(140, -1), new Dimension(140, -1), new Dimension(140, -1), 0, false));
        Info = new JTextArea();
        Info.setBackground(new Color(-13957510));
        Info.setEditable(false);
        Info.setEnabled(true);
        Font InfoFont = this.$$$getFont$$$("Droid Sans Mono", -1, 18, Info.getFont());
        if (InfoFont != null) Info.setFont(InfoFont);
        Info.setForeground(new Color(-7351572));
        Info.setLineWrap(true);
        Info.setText("");
        About.add(Info, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer12 = new com.intellij.uiDesigner.core.Spacer();
        About.add(spacer12, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 30), new Dimension(11, 30), new Dimension(-1, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer13 = new com.intellij.uiDesigner.core.Spacer();
        About.add(spacer13, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer14 = new com.intellij.uiDesigner.core.Spacer();
        About.add(spacer14, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(65, -1), new Dimension(65, -1), new Dimension(65, -1), 0, false));
        PrepereGame = new JPanel();
        PrepereGame.setLayout(new CardLayout(0, 0));
        PrepereGame.setBackground(new Color(-13957510));
        ControlPanel.add(PrepereGame, "Card5");
        Game = new JPanel();
        Game.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 4, new Insets(0, 0, 0, 0), -1, -1));
        Game.setBackground(new Color(-13957510));
        PrepereGame.add(Game, "Card1");
        GameBack = new JButton();
        GameBack.setBackground(new Color(-16777216));
        Font GameBackFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, GameBack.getFont());
        if (GameBackFont != null) GameBack.setFont(GameBackFont);
        GameBack.setForeground(new Color(-7351572));
        GameBack.setText("Back");
        Game.add(GameBack, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 7, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer15 = new com.intellij.uiDesigner.core.Spacer();
        Game.add(spacer15, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer16 = new com.intellij.uiDesigner.core.Spacer();
        Game.add(spacer16, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 6, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(149, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer17 = new com.intellij.uiDesigner.core.Spacer();
        Game.add(spacer17, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 6, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(239, -1), null, 0, false));
        StartGame = new JButton();
        StartGame.setBackground(new Color(-16777216));
        Font StartGameFont = this.$$$getFont$$$("Droid Sans Mono", -1, -1, StartGame.getFont());
        if (StartGameFont != null) StartGame.setFont(StartGameFont);
        StartGame.setForeground(new Color(-7351572));
        StartGame.setText("Start Game");
        Game.add(StartGame, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer18 = new com.intellij.uiDesigner.core.Spacer();
        Game.add(spacer18, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, 1, new Dimension(-1, 70), new Dimension(-1, 70), new Dimension(-1, 70), 0, false));
        field.setBackground(new Color(-13957510));
        Game.add(field, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer19 = new com.intellij.uiDesigner.core.Spacer();
        Game.add(spacer19, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scoreTable = new JTextField();
        scoreTable.setBackground(new Color(-16777216));
        scoreTable.setEditable(false);
        Font scoreTableFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 18, scoreTable.getFont());
        if (scoreTableFont != null) scoreTable.setFont(scoreTableFont);
        scoreTable.setForeground(new Color(-7351572));
        scoreTable.setHorizontalAlignment(0);
        scoreTable.setText("32480340592");
        Game.add(scoreTable, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer20 = new com.intellij.uiDesigner.core.Spacer();
        Game.add(spacer20, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        EnterName = new JPanel();
        EnterName.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        EnterName.setBackground(new Color(-13957510));
        PrepereGame.add(EnterName, "Card2");
        NameBack = new JButton();
        NameBack.setText("Back");
        EnterName.add(NameBack, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        NameField = new JTextPane();
        NameField.setBackground(new Color(-16777216));
        Font NameFieldFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, NameField.getFont());
        if (NameFieldFont != null) NameField.setFont(NameFieldFont);
        NameField.setForeground(new Color(-7351572));
        EnterName.add(NameField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(300, 30), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer21 = new com.intellij.uiDesigner.core.Spacer();
        EnterName.add(spacer21, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 4, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(200, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer22 = new com.intellij.uiDesigner.core.Spacer();
        EnterName.add(spacer22, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(200, -1), null, 0, false));
        NameText = new JTextPane();
        NameText.setBackground(new Color(-13957510));
        NameText.setEditable(false);
        Font NameTextFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, NameText.getFont());
        if (NameTextFont != null) NameText.setFont(NameTextFont);
        NameText.setForeground(new Color(-7351572));
        NameText.setText("Enter your name:");
        EnterName.add(NameText, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

    private void createUIComponents() {
        for (int blockInd = 0; blockInd < numOfBlocks; ++blockInd) {
            fieldBlocks[blockInd] = new Block(0, 0);
            fieldBlocks[blockInd].setBorder(BorderFactory.createLineBorder(new Color(-7351572)));
        }
        field = new JPanel();
        field.setLayout(new TetrisLayout());
        field.setSize(shift + columns * (blockSize + shift), shift + lines * (blockSize + shift));
        field.setLocation(0, 0);
        field.setBackground(Color.BLACK);
        field.setBorder(BorderFactory.createLineBorder(new Color(-7351572)));
        for (int blockInd = 0; blockInd < numOfBlocks; ++blockInd) {
            field.add(fieldBlocks[blockInd]);
        }
        field.setVisible(true);

        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "leftMove");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "leftMove");
        field.getActionMap().put("leftMove", new LeftMove());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "rightMove");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "rightMove");
        field.getActionMap().put("rightMove", new RightMove());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "stopMove");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "stopMove");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "stopMove");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "stopMove");
        field.getActionMap().put("stopMove", new StopMove());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "downMove");
        field.getActionMap().put("downMove", new DownMove());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"), "downStop");
        field.getActionMap().put("downStop", new DownStop());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "leftRotate");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "leftRotate");
        field.getActionMap().put("leftRotate", new LeftRotate());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "rightRotate");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "rightRotate");
        field.getActionMap().put("rightRotate", new RightRotate());
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "stopRotate");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "stopRotate");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "stopRotate");
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "stopRotate");
        field.getActionMap().put("stopRotate", new RotateStop());
    }
}
