package com.dipech;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.Random;

public class Application {

    // #################################################################################################################
    // #                                               GAME LOGIC PART                                                 #
    // #################################################################################################################

    // Random number generator
    final Random random = new Random();

    /*
     * Constants
     */

    // World's size
    final int ROWS = 30;
    final int COLS = 30;
    // How many obstacles to generate
    final int OBSTACLES_COUNT = 20;
    // Newborn snake will this amount of segments
    final int SNAKE_INITIAL_LENGTH = 5;
    // How large our snake can be
    final int SNAKE_MAX_SIZE = 200;
    // Possible snake moving directions
    final int DIRECTION_UP = 0;
    final int DIRECTION_DOWN = 1;
    final int DIRECTION_LEFT = 2;
    final int DIRECTION_RIGHT = 3;

    /*
     * Mvc – Model (data that represents our game objects).
     * Just simple data like game objects coordinates, snake's moving direction, and so on...
     */

    // Two-dimensional array of walls
    boolean[][] walls = new boolean[ROWS][COLS];
    // Two-dimensional array of foods
    boolean[][] foods = new boolean[ROWS][COLS];
    // Two one-dimensional arrays of snake body part coordinates
    int[] snakeRow = new int[SNAKE_MAX_SIZE];
    int[] snakeCol = new int[SNAKE_MAX_SIZE];
    // Snake moving direction
    int snakeDirection = DIRECTION_RIGHT;
    // When the snake bumps into something prohibited
    boolean isGameOver = false;
    boolean isGamePaused = true;

    /*
     * mVc – View (how we're going to render our game objects).
     * We will take each game object and draw it on the text field (world).
     * We should not have any game logic here! only logic for rendering our game objects!
     */

    public void onRender(char[][] world) {
        // Render walls and foods
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (walls[row][col]) {
                    world[row][col] = '#';
                    continue;
                }
                if (foods[row][col]) {
                    world[row][col] = 'o';
                }
            }
        }

        // Render snake
        int snakeSize = getSnakeSize();
        for (int i = 0; i < snakeSize; i++) {
            world[snakeRow[i]][snakeCol[i]] = i < snakeSize - 1 ? 'O' : '0';
        }
    }

    /*
     * mvC – Controller (how we're going to react when a player presses some keys).
     * It's just a key listener. We can do something with Model when a player presses some key.
     */

    public void onKeyPressed(int code) {
        int snakeSize = getSnakeSize();
        int snakeHeadRow = snakeRow[snakeSize - 1];
        int snakeHeadCol = snakeCol[snakeSize - 1];
        switch (code) {
            case KeyEvent.VK_UP:
                if (!isSnake(snakeHeadRow - 1, snakeHeadCol)) {
                    snakeDirection = DIRECTION_UP;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (!isSnake(snakeHeadRow, snakeHeadCol - 1)) {
                    snakeDirection = DIRECTION_LEFT;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (!isSnake(snakeHeadRow + 1, snakeHeadCol)) {
                    snakeDirection = DIRECTION_DOWN;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!isSnake(snakeHeadRow, snakeHeadCol + 1)) {
                    snakeDirection = DIRECTION_RIGHT;
                }
                break;

            case KeyEvent.VK_R:
                if (isGameOver || isGamePaused) {
                    startGame();
                }
                break;
            case KeyEvent.VK_P:
                isGamePaused = !isGamePaused;
                break;
        }

        // Arrow keys start the game if the game isn't over
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_RIGHT ||
            code == KeyEvent.VK_DOWN || code == KeyEvent.VK_LEFT) {
            if (!isGameOver && isGamePaused) {
                isGamePaused = false;
            }
        }
    }

    public void onGameStep() {
        if (isGameOver || isGamePaused) {
            return;
        }
        moveSnake();
        handleObstacleCase();
        handleFoodCase();
    }

    private void moveSnake() {
        int snakeSize = getSnakeSize();
        // Move every part except the head
        for (int i = 0; i < snakeSize - 1; i++) {
            snakeRow[i] = snakeRow[i + 1];
            snakeCol[i] = snakeCol[i + 1];
        }
        // Move the head according moving direction
        if (snakeDirection == DIRECTION_UP) {
            snakeRow[snakeSize - 1]--;
        }
        if (snakeDirection == DIRECTION_DOWN) {
            snakeRow[snakeSize - 1]++;
        }
        if (snakeDirection == DIRECTION_LEFT) {
            snakeCol[snakeSize - 1]--;
        }
        if (snakeDirection == DIRECTION_RIGHT) {
            snakeCol[snakeSize - 1]++;
        }
    }

    private void handleObstacleCase() {
        int snakeSize = getSnakeSize();
        int snakeHeadRow = snakeRow[snakeSize - 1];
        int snakeHeadCol = snakeCol[snakeSize - 1];
        if (isWall(snakeHeadRow, snakeHeadCol) || isSnake(snakeHeadRow, snakeHeadCol, false)) {
            isGameOver = true;
        }
    }

    private void handleFoodCase() {
        int snakeSize = getSnakeSize();
        int snakeHeadRow = snakeRow[snakeSize - 1];
        int snakeHeadCol = snakeCol[snakeSize - 1];
        if (foods[snakeHeadRow][snakeHeadCol]) {
            // Remove the old food item
            foods[snakeHeadRow][snakeHeadCol] = false;
            // Create a new food item
            generateFood();
            // Increase snake length (add a body part)
            for (int i = snakeSize; i > 0; i--) {
                snakeRow[i] = snakeRow[i - 1];
                snakeCol[i] = snakeCol[i - 1];
            }
        }
    }

    private void startGame() {
        resetWallsAndFoods();
        resetGameState();
        generateBorders();
        generateObstacles();
        generateFood();
        generateSnake();
    }

    private void resetWallsAndFoods() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                walls[row][col] = foods[row][col] = false;
            }
        }
    }

    private void resetGameState() {
        snakeDirection = DIRECTION_RIGHT;
        isGameOver = false;
        isGamePaused = true;
    }

    private void generateBorders() {
        for (int row = 0; row < ROWS; row++) {
            walls[row][0] = true;
            walls[row][COLS - 1] = true;
        }
        for (int col = 1; col < COLS - 1; col++) {
            walls[0][col] = true;
            walls[ROWS - 1][col] = true;
        }
    }

    private void generateObstacles() {
        int counter = 0;
        while (counter < OBSTACLES_COUNT) {
            int row = random.nextInt(ROWS - 2) + 1;
            int col = random.nextInt(COLS - 2) + 1;
            if (isEmptySpace(row, col)) {
                walls[row][col] = true;
                counter++;
            }
        }
    }

    private void generateFood() {
        while (true) {
            int row = random.nextInt(ROWS - 4) + 2;
            int col = random.nextInt(COLS - 4) + 2;
            if (!isEmptySpace(row, col)) {
                continue;
            }
            int emptyNeighborCellsCount = 0;
            emptyNeighborCellsCount += isEmptySpace(row - 1, col) ? 1 : 0; // top
            emptyNeighborCellsCount += isEmptySpace(row + 1, col) ? 1 : 0; // bottom
            emptyNeighborCellsCount += isEmptySpace(row, col + 1) ? 1 : 0; // right
            emptyNeighborCellsCount += isEmptySpace(row, col - 1) ? 1 : 0; // left
            if (emptyNeighborCellsCount >= 3) {
                foods[row][col] = true;
                return;
            }
        }
    }

    private void generateSnake() {
        for (int i = 0; i < SNAKE_MAX_SIZE; i++) {
            snakeRow[i] = snakeCol[i] = -1;
        }
        int emptySpaceNeeded = SNAKE_INITIAL_LENGTH + 2;
        while (true) {
            int row = random.nextInt(ROWS - 2) + 1;
            int col = random.nextInt(COLS - 2 - emptySpaceNeeded) + 1;
            boolean enoughEmptySpace = true;
            for (int i = 0; i < emptySpaceNeeded; i++) {
                if (!isEmptySpace(row, col + i)) {
                    enoughEmptySpace = false;
                    break;
                }
            }
            if (enoughEmptySpace) {
                for (int i = 0; i < SNAKE_INITIAL_LENGTH; i++) {
                    snakeRow[i] = row;
                    snakeCol[i] = col + i;
                }
                return;
            }
        }
    }

    private boolean isWall(int row, int col) {
        return walls[row][col];
    }

    private boolean isFood(int row, int col) {
        return foods[row][col];
    }

    private boolean isSnake(int row, int col) {
        return isSnake(row, col, true);
    }

    private boolean isSnake(int row, int col, boolean checkHead) {
        int snakeSize = getSnakeSize();
        if (!checkHead) {
            snakeSize--;
        }
        for (int i = 0; i < snakeSize; i++) {
            if (snakeRow[i] == row && snakeCol[i] == col) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptySpace(int row, int col) {
        return !isWall(row, col) && !isFood(row, col) && !isSnake(row, col);
    }

    private int getSnakeSize() {
        int snakeSize = 0;
        for (int i = 0; i < SNAKE_MAX_SIZE; i++) {
            if (snakeRow[i] == -1 || snakeCol[i] == -1) {
                break;
            }
            snakeSize++;
        }
        return snakeSize;
    }

    // #################################################################################################################
    // #                                              GAME ENGINE PART                                                 #
    // #################################################################################################################

    public static void main(String[] args) {
        new Application().start();
    }

    public void start() {
        JFrame frame = new JFrame("Snake");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea(ROWS, COLS);
        Font font;
        try {
            InputStream fontFileStream = Application.class.getClassLoader().getResourceAsStream("square.ttf");
            assert fontFileStream != null;
            font = Font.createFont(Font.TRUETYPE_FONT, fontFileStream).deriveFont(Font.PLAIN, 30);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        textArea.setFont(font);
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        frame.getContentPane().add(textArea, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(keyEvent -> {
            if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                onKeyPressed(keyEvent.getKeyCode());
            }
            return true;
        });

        startGame();

        // Game Loop
        new Thread(() -> {
            char[][] world = new char[ROWS][COLS];
            while (true) {
                onGameStep();

                // Renderer step 1: clean the "world"
                for (int row = 0; row < ROWS; row++) {
                    for (int col = 0; col < COLS; col++) {
                        world[row][col] = ' ';
                    }
                }
                // Renderer step 2: fill in the "world"
                onRender(world);
                // Renderer step 3: translate "char[][] world" into just "String"
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < ROWS; i++) {
                    builder.append(world[i]);
                    if (i < ROWS - 1) {
                        builder.append("\n");
                    }
                }
                textArea.setText(builder.toString());

                // Wait for N msecs (just to not render so often)
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
