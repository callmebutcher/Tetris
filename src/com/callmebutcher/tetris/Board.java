package com.callmebutcher.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

public class Board extends JPanel implements Runnable {

    public static final int BOARD_WIDTH_IN_BLOCKS = 12;
    public static final int BOARD_HEIGHT_IN_BLOCKS = 23;

    private final int xOffsetBoard = 300;
    private final int yOffsetBoard = 100;

    private final int xScoreLabel = 175;
    private final int yScoreLabel = 460;
    private final int xScorePointsLabel = this.xScoreLabel + 25;
    private final int yScorePointsLabel = this.yScoreLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private final int xLevelLabel = 175;
    private final int yLevelLabel = 535;
    private final int xLevelValueLabel = this.xLevelLabel + 25;
    private final int yLevelValueLabel = this.yLevelLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private final int xLinesScoredLabel = 175;
    private final int yLinesScoredLabel = 610;
    private final int xLinesScoredValueLabel = this.xLinesScoredLabel + 25;
    private final int yLinesScoredValueLabel = this.yLinesScoredLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private JFrame frame;
    private Thread thread;
    private Tetromino tetromino;
    private Tetromino storedTetromino;
    private int boardMatrix[][];
    private int fourNextTetrominos[][][];

    private int score; // This is score
    private int level;
    private int speed;
    private int linesScored;
    private boolean alreadyPussedChangeTetromino;
    static boolean playerAlive;
    static boolean pause;
    private BufferedImage background;
    private BufferedImage template;
    private BufferedImage gameOver;
    private BufferedImage paused;

    JButton musicbutton;

    Thread playThread;

    AudioPlayer audio = new AudioPlayer();

    public Board(JFrame frame) {
        super.setBackground(Color.BLACK);
        super.setFocusable(true);

        this.frame = frame;
        this.thread = new Thread(this);
        this.tetromino = new Tetromino();
        this.storedTetromino = null;
        this.fourNextTetrominos = assignFourTetrominos();
        this.boardMatrix = new int[Board.BOARD_HEIGHT_IN_BLOCKS][Board.BOARD_WIDTH_IN_BLOCKS];

        this.score = 0;
        this.level = 0;
        this.speed = 1000;
        this.linesScored = 0;
        this.alreadyPussedChangeTetromino = false;
        Board.playerAlive = true;
        Board.pause = false;

        try {
            this.background = ImageIO.read(this.getClass().getResource("assets/game_area.jpg"));
            this.template = ImageIO.read(this.getClass().getResource("assets/template.png"));
            this.gameOver = ImageIO.read(this.getClass().getResource("assets/gameover.jpg"));
            this.paused = ImageIO.read(this.getClass().getResource("assets/paused.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.thread.start();
        addControlsToGame();

        Runnable task = () -> {
            AudioPlayer.playTitle();
        };
        playThread = new Thread(task);
        playThread.start();

        musicbutton = new JButton("Music ON");
        musicbutton.setFont(Main.mainFont);
        musicbutton.setBounds(0, 0, 100, 50);

        musicbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (AudioPlayer.getMusicStatus()) {
                    AudioPlayer.stopTitle();
                    AudioPlayer.setMusicStatus(false);
                    musicbutton.setText("Music OFF");
                } else if (!AudioPlayer.getMusicStatus()) {
                    AudioPlayer.playTitle();
                    AudioPlayer.setMusicStatus(true);
                    musicbutton.setText("Music ON");
                }
            }
        });
        super.add(musicbutton);

    }

    private int[][][] assignFourTetrominos() {
        int tetrominos[][][] = new int[4][4][4];
        tetrominos[0] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[1] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[2] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[3] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        return tetrominos;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBackground(g);
        paintFourNextTetrominos(g);
        paintStoredTetromino(g);
        paintStoredTetrominoLines(g);
        paintNextTetrominosLines(g);
        paintTexts(g);
        paintBoardMatrix(g);
        paintCurrentTetromino(g);
        paintBoardLines(g);
        Main.paintInfo(g);
        paintPaused(g);
        paintLoose(g);

    }

    @Override
    public void run() {

        while (Board.playerAlive) {
            System.out.println(pause);
            if (!pause) {
                if (!tetromino.isAlive() || tetrominoCollidesOnBottom()) {
                    createNewTetromino();
                }
                if (Board.playerAlive)
                    tetromino.applyGravity(1);
                repaint();
                try {
                    Thread.sleep(this.speed);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void paintCurrentTetromino(Graphics g) {
        for (int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++) {
            for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++) {
                if (tetromino.isBlock(i, j)) {
                    g.setColor(tetromino.getColor());
                    g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0] + this.xOffsetBoard,
                            i * Tetromino.TETROMINO_BLOCK_SIZE
                                    + tetromino.getPosition()[1],
                            Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                }
            }
        }
    }

    private void paintFourNextTetrominos(Graphics g) {
        int x = 650;
        int y = 150;

        for (int tetromino_[][] : this.fourNextTetrominos) {
            for (int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++) {
                for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++) {
                    if (tetromino_[i][j] != 0) {
                        g.setColor(chooseColorForBlock(tetromino_[i][j]));
                        g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + x, i * Tetromino.TETROMINO_BLOCK_SIZE + y,
                                Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                    }
                }
            }
            y += 125;
        }
    }

    private void paintStoredTetromino(Graphics g) {
        int x = 150;
        int y = 150;
        if (this.storedTetromino != null)
            for (int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++) {
                for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++) {
                    if (this.storedTetromino.getCurrentTetromino()[i][j] != 0) {
                        g.setColor(chooseColorForBlock(this.storedTetromino.getCurrentTetromino()[i][j]));
                        g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + x +
                                (1 - this.storedTetromino.getMatrixColumnsWithoutBlocksOnLeftHalf())
                                        * Tetromino.TETROMINO_BLOCK_SIZE,
                                i * Tetromino.TETROMINO_BLOCK_SIZE + y,
                                Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                    }
                }
            }
    }

    private void paintBoardLines(Graphics g) {
        g.setColor(Color.GRAY);
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS; i++) {
            g.drawLine(this.xOffsetBoard, i * Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard,
                    2 * Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_WIDTH_IN_BLOCKS,
                    i * Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard);
        }

        for (int i = 0; i < Board.BOARD_WIDTH_IN_BLOCKS; i++) {
            g.drawLine(this.xOffsetBoard + i * Tetromino.TETROMINO_BLOCK_SIZE, this.yOffsetBoard,
                    i * Tetromino.TETROMINO_BLOCK_SIZE + this.xOffsetBoard,
                    Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_HEIGHT_IN_BLOCKS + this.yOffsetBoard);
        }
    }

    private void paintNextTetrominosLines(Graphics g) {
        g.setColor(Color.GRAY);
        int x1 = this.xOffsetBoard + Board.BOARD_WIDTH_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE +
                Tetromino.TETROMINO_BLOCK_SIZE * 2;
        int x2 = x1 + 4 * Tetromino.TETROMINO_BLOCK_SIZE;
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS - 3; i++) {
            g.drawLine(x1, i * Tetromino.TETROMINO_BLOCK_SIZE + 125, x2, i * Tetromino.TETROMINO_BLOCK_SIZE + 125);
        }

        int nextTetrominosBoardWidth = 4;
        for (int i = 0; i < nextTetrominosBoardWidth; i++) {
            g.drawLine(x1 + Tetromino.TETROMINO_BLOCK_SIZE * i, Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard,
                    x1 + Tetromino.TETROMINO_BLOCK_SIZE * i, this.yOffsetBoard
                            + (Board.BOARD_HEIGHT_IN_BLOCKS - 2) * Tetromino.TETROMINO_BLOCK_SIZE);
        }
    }

    private void paintStoredTetrominoLines(Graphics g) {
        g.setColor(Color.GRAY);
        int xOffsetToGoal = 150;
        int yOffsetToGoal = 150;
        int squareWidthAndHeight = 4;
        int x1 = xOffsetToGoal;
        int x2 = x1 + squareWidthAndHeight * Tetromino.TETROMINO_BLOCK_SIZE;

        for (int i = 0; i < squareWidthAndHeight; i++) {
            g.drawLine(x1, i * Tetromino.TETROMINO_BLOCK_SIZE + yOffsetToGoal, x2, i *
                    Tetromino.TETROMINO_BLOCK_SIZE + yOffsetToGoal);
        }

        int y1 = yOffsetToGoal;
        int y2 = yOffsetToGoal + squareWidthAndHeight * Tetromino.TETROMINO_BLOCK_SIZE;
        for (int i = 0; i < squareWidthAndHeight; i++) {
            g.drawLine(i * Tetromino.TETROMINO_BLOCK_SIZE + xOffsetToGoal, y1,
                    i * Tetromino.TETROMINO_BLOCK_SIZE + xOffsetToGoal, y2);
        }
    }

    private void paintTexts(Graphics g) {
        g.setFont(Main.mainFont);
        g.setColor(Color.WHITE);

        g.drawString("SCORE", this.xScoreLabel, this.yScoreLabel);
        g.drawString(String.valueOf(this.score), this.xScorePointsLabel, this.yScorePointsLabel);

        g.drawString("LEVEL", this.xLevelLabel, this.yLevelLabel);
        g.drawString(String.valueOf(this.level), this.xLevelValueLabel, this.yLevelValueLabel);

        g.drawString("LINES", this.xLinesScoredLabel, this.yLinesScoredLabel);
        g.drawString(String.valueOf(this.linesScored), this.xLinesScoredValueLabel, this.yLinesScoredValueLabel);
    }

    /*
     * Loose Text on Game Over
     * private void paintLoose(Graphics g){
     * if(!Board.playerAlive){
     * g.setFont(Main.loose);
     * g.setColor(Color.WHITE);
     * g.drawString("You Loose", (int)(Main.JFRAME_WIDTH / 2.5), Main.JFRAME_HEIGHT
     * / 2);
     * }
     * 
     * //saveScore();
     * 
     * }
     */
    private void paintPaused(Graphics g) {
        if (pause) {
            g.drawImage(this.paused, (Main.JFRAME_WIDTH / 3) + 50, 300, null);
            repaint();
        }
    }

    // Paint Game over Endscreen
    private void paintLoose(Graphics g) {
        if (!Board.playerAlive) {
            g.drawImage(this.gameOver, 0, 0, this);
        }
    }

    private void saveScore(String playerName) {
        Writer output;

        if (playerName.length() == 0) {
            playerName = "Unknown Player";
        }

        try {
            output = new BufferedWriter(new FileWriter("scores.txt", true)); // clears file every time
            output.append(playerName + ":" + score + "\n"); // Player name, player score
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void paintBoardMatrix(Graphics g) {
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS; i++)
            for (int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++) {
                if (boardMatrix[i][j] != 0) {
                    g.setColor(chooseColorForBlock(boardMatrix[i][j]));
                    g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + this.xOffsetBoard,
                            i * Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard, Tetromino.TETROMINO_BLOCK_SIZE,
                            Tetromino.TETROMINO_BLOCK_SIZE);
                }
            }
    }

    private void paintBackground(Graphics g) {
        g.drawImage(this.background, 0, 0, null);
        g.drawImage(this.template, 0, 0, null);
    }

    private Color chooseColorForBlock(int id) {
        switch (id) {
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.RED;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.ORANGE;
            case 6:
                return Color.BLUE;
            case 7:
                return Color.PINK;
            default:
                return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------\\

    private void addControlsToGame() {
        super.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change
                // body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (Board.playerAlive && !pause)
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        spaceKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        rightKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        leftKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        downKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        upKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_C) {
                        cKeyPressed();
                    }

                if (e.getKeyCode() == KeyEvent.VK_P) {
                    pKeyPressed();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change
                // body of generated methods, choose Tools | Templates.
            }

        });
    }

    private void spaceKeyPressed() {
        tetromino.spinTetrominio();
        int xCountingFromFirstBlockOnLeft = tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf() *
                Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0];
        if (xCountingFromFirstBlockOnLeft < 0)
            tetromino.setPosition(new int[] { 0, tetromino.getPosition()[1] });

        else if (xCountingFromFirstBlockOnLeft + Tetromino.TETROMINO_BLOCK_SIZE *
                tetromino.numberOfBlocksFromFirstLeftBlock() > Tetromino.TETROMINO_BLOCK_SIZE *
                        Board.BOARD_WIDTH_IN_BLOCKS) {

            int dif = (xCountingFromFirstBlockOnLeft + Tetromino.TETROMINO_BLOCK_SIZE *
                    tetromino.numberOfBlocksFromFirstLeftBlock()) -
                    (Board.BOARD_WIDTH_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE);
            tetromino.setPosition(new int[] { tetromino.getPosition()[0] - dif, tetromino.getPosition()[1] });
        }

        repaint();
    }

    private void rightKeyPressed() {
        int xCountingFromFirstBlockOnLeft = tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf() *
                Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0];

        if ((xCountingFromFirstBlockOnLeft < Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_WIDTH_IN_BLOCKS -
                Tetromino.TETROMINO_BLOCK_SIZE * tetromino.numberOfBlocksFromFirstLeftBlock()) &&
                !checkIfTetrominoCollidesOnAxisX(1))
            tetromino.moveOnAxisX(1);

        repaint();
    }

    private void leftKeyPressed() {
        if ((tetromino.getPosition()[0] > 0 - Tetromino.TETROMINO_BLOCK_SIZE *
                tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf()) && !checkIfTetrominoCollidesOnAxisX(-1))
            tetromino.moveOnAxisX(-1);

        repaint();
    }

    private void downKeyPressed() {
        if (!tetromino.isAlive() || tetrominoCollidesOnBottom())
            createNewTetromino();

        tetromino.applyGravity(1);
        repaint();
    }

    private void upKeyPressed() {
        while (!tetrominoCollidesOnBottom() && tetromino.isAlive()) {
            tetromino.applyGravity(1);
        }
        createNewTetromino();
        repaint();
    }

    private void cKeyPressed() {
        if (!this.alreadyPussedChangeTetromino) {
            if (this.storedTetromino == null)
                storeTetromino();
            else
                replaceTetromino();

        }
    }

    private void pKeyPressed() {
        if (!pause) {
            pause = true;
        } else {
            pause = false;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------\\

    private boolean checkIfTetrominoCollidesOnAxisX(int direction) {
        for (int blocksPositions[] : tetromino.positionsToAttachToBoardMatrix()) {
            int positionInMatrixCoordinates[] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(
                    tetromino.getPosition());
            positionInMatrixCoordinates[0] += blocksPositions[0];
            positionInMatrixCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - blocksPositions[1];
            if (positionInMatrixCoordinates[0] > 0 && positionInMatrixCoordinates[0] < (Board.BOARD_WIDTH_IN_BLOCKS - 1)
                    && (positionInMatrixCoordinates[0] + direction) >= 0 && (positionInMatrixCoordinates[1]) >= 0) {
                if (boardMatrix[positionInMatrixCoordinates[1]][positionInMatrixCoordinates[0] + direction] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tetrominoCollidesOnBottom() {
        for (int blocksPositions[] : tetromino.positionsToAttachToBoardMatrix()) {
            int positionInMatrixCoordinates[] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(
                    tetromino.getPosition());
            positionInMatrixCoordinates[0] += blocksPositions[0];
            positionInMatrixCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - blocksPositions[1];
            if (positionInMatrixCoordinates[1] < (Board.BOARD_HEIGHT_IN_BLOCKS - 1)
                    && (positionInMatrixCoordinates[1] + 1) >= 0) {
                if (this.boardMatrix[positionInMatrixCoordinates[1] + 1][positionInMatrixCoordinates[0]] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tetrominoOutOfBoard() {
        for (int blocksPositions[] : tetromino.positionsToAttachToBoardMatrix()) {
            int positionInMatrixCoordinates[] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(
                    tetromino.getPosition());
            positionInMatrixCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - blocksPositions[1];
            if (positionInMatrixCoordinates[1] * Tetromino.TETROMINO_BLOCK_SIZE <= 0)
                return true;
        }
        return false;
    }

    // ----------------------------------------------------------------------------------------------------------------\\

    private void createNewTetromino() {
        if (!tetrominoOutOfBoard()) {
            attachTetrominoToBoardMatrix(tetromino);
            removeRows();
            this.tetromino = new Tetromino(fourNextTetrominos[0]);
            this.alreadyPussedChangeTetromino = false;
            restructureTetrominos();
        } else {
            Board.playerAlive = false;
            super.remove(musicbutton);
            if (AudioPlayer.getMusicStatus())
                AudioPlayer.stopTitle();
            audio.playGameOver();
            addRetryAndMenuButton();
        }
    }

    private void storeTetromino() {
        this.storedTetromino = tetromino;
        this.alreadyPussedChangeTetromino = true;
        this.tetromino = new Tetromino(fourNextTetrominos[0]);
        restructureTetrominos();
    }

    private void replaceTetromino() {
        Tetromino aux = this.tetromino;
        this.tetromino = new Tetromino(this.storedTetromino.getCurrentTetromino());
        this.storedTetromino = aux;
        this.alreadyPussedChangeTetromino = true;
    }

    private void restructureTetrominos() {
        int tet1[][] = fourNextTetrominos[1];
        int tet2[][] = fourNextTetrominos[2];
        int tet3[][] = fourNextTetrominos[3];

        fourNextTetrominos[0] = tet1;
        fourNextTetrominos[1] = tet2;
        fourNextTetrominos[2] = tet3;

        fourNextTetrominos[3] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];

    }

    private int[] worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[] positionInWorldCoordinates) {
        int[] boardMatrixBlocksCoordinates = { positionInWorldCoordinates[0] / Tetromino.TETROMINO_BLOCK_SIZE,
                (positionInWorldCoordinates[1] / Tetromino.TETROMINO_BLOCK_SIZE) - 1 };
        return boardMatrixBlocksCoordinates;
    }

    private void attachTetrominoToBoardMatrix(Tetromino tetromino) {
        int tetrominoPosition[] = { tetromino.getPosition()[0], tetromino.getPosition()[1] };
        int blocksPositions[][] = tetromino.positionsToAttachToBoardMatrix();
        for (int[] rows : blocksPositions) {
            int positionsInBoardMatrixBlocksCoordinates[] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(
                    tetrominoPosition);
            positionsInBoardMatrixBlocksCoordinates[0] += rows[0];
            positionsInBoardMatrixBlocksCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - rows[1];
            boardMatrix[positionsInBoardMatrixBlocksCoordinates[1]][positionsInBoardMatrixBlocksCoordinates[0]] = tetromino
                    .getId();
        }
    }

    // ----------------------------------------------------------------------------------------------------------------\\

    private void removeRows() {
        int filledRows[] = iPositionOfFilledRows();
        int firstRow = 0;
        if (filledRows.length > 0) {
            firstRow = filledRows[0];
            this.score += scorePoints(filledRows.length);
            linesScored += filledRows.length;
            levelUp();
            for (int i : filledRows) {
                for (int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++) {
                    this.boardMatrix[i][j] = 0;
                }
            }
            dropRow(firstRow);
        }
    }

    private int[] iPositionOfFilledRows() {
        ArrayList<Integer> rows = new ArrayList<>();
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS; i++) {
            boolean add = true;
            for (int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++) {
                if (this.boardMatrix[i][j] == 0)
                    add = false;
            }
            if (add)
                rows.add(i);
        }
        int arr[] = new int[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            arr[i] = rows.get(i);
        }
        return arr;
    }

    private void dropRow(int firstRow) {
        audio.playClearLine();
        firstRow--;
        boolean noTetrominoOver = true;
        for (int i = firstRow; i > 0; i--) {
            for (int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++) {
                if (this.boardMatrix[i][j] != 0)
                    noTetrominoOver = false;
                dropElement(i, j);
            }
            if (noTetrominoOver)
                break;
        }
        removeRows();
    }

    private void dropElement(int i, int j) {
        int block = this.boardMatrix[i][j];
        while (this.boardMatrix[i + 1][j] == 0) {
            this.boardMatrix[i][j] = 0;
            this.boardMatrix[i + 1][j] = block;
            if (i < Board.BOARD_HEIGHT_IN_BLOCKS - 2)
                i++;
            else
                break;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------\\

    private int scorePoints(int numberOfRows) {
        switch (numberOfRows) {
            case 1:
                return 40 * (this.level + 1);
            case 2:
                return 100 * (this.level + 1);
            case 3:
                return 300 * (this.level + 1);
            case 4:
                return 1200 * (this.level + 1);
        }
        return 0;
    }

    private void levelUp() {
        if (linesScored > this.level * 10 + 10) { // Modify Level Speed
            this.level++;
            if (this.speed > 200)
                this.speed -= 200;
            else if (this.speed > 100)
                this.speed -= 50;
            System.out.println(this.speed);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------\\

    private void addRetryAndMenuButton() {
        int buttonsWidth = 325;
        int saveButtonWidth = 175;
        int buttonsHeight = 65;
        int yOffset = Main.JFRAME_HEIGHT / 3;
        int spacing = (int) (buttonsHeight * 1.5f);

        JButton retry = new JButton("Man Up and Retry");
        retry.setFont(Main.mainFont);

        retry.setBounds(Main.JFRAME_WIDTH / 6 - 100, Main.JFRAME_HEIGHT / 2 + buttonsHeight - yOffset,
                buttonsWidth, buttonsHeight);
        retry.addActionListener(e -> Main.loadScene(this.frame, new Board(this.frame)));

        JButton back = new JButton("Give Up and go cry to Mother");
        back.setFont(Main.mainFont);

        back.setBounds(Main.JFRAME_WIDTH / 6 - 100, retry.getY() + spacing, buttonsWidth, buttonsHeight);
        back.addActionListener(e -> Main.loadScene(this.frame, new MainMenu(this.frame)));

        JLabel label = new JLabel("Enter Your Name:");
        label.setFont(Main.labelFont);
        label.setBounds(Main.JFRAME_WIDTH / 2 - 20, Main.JFRAME_HEIGHT / 2 + buttonsHeight - yOffset, buttonsWidth,
                buttonsHeight);
        label.setForeground(Color.WHITE);

        JTextField userText = new JTextField(50);
        userText.setName("Enter your name:");
        userText.setFont(Main.textField);
        userText.setBounds(Main.JFRAME_WIDTH / 2 + 200, Main.JFRAME_HEIGHT / 2 + buttonsHeight - yOffset + 10,
                saveButtonWidth, buttonsHeight - 20);
        // userText.addActionListener(e -> this.saveScore());

        JButton save = new JButton("SAVE");
        save.setFont(Main.mainFont);
        save.setBounds(Main.JFRAME_WIDTH / 2 + 100, retry.getY() + spacing, saveButtonWidth,
                buttonsHeight);
        save.addActionListener(e -> this.saveScore(userText.getText()));
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                save.setText(" Score Saved");
            }
        });

        super.setLayout(null);
        super.add(retry);
        super.add(back);
        super.add(save);
        super.add(userText);
        super.add(label);
    }

}
