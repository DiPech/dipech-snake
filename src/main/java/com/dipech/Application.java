package com.dipech;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStream;

public class Application {

    // #################################################################################################################
    // #                                               GAME LOGIC PART                                                 #
    // #################################################################################################################

    /*
     * Constants
     */

    // World's size
    final int ROWS = 10;
    final int COLS = 10;

    /*
     * Mvc – Model (data that represents our game objects).
     * Just simple data like game objects coordinates, snake's moving direction, and so on...
     */

    // Two-dimensional array of walls
    boolean[][] walls = new boolean[ROWS][COLS];

    /*
     * mVc – View (how we're going to render our game objects).
     * We will take each game object and draw it on the text field (world).
     * We should not have any game logic here! only logic for rendering our game objects!
     */

    public void onRender(char[][] world) {
        // Render walls
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (walls[row][col]) {
                    world[row][col] = '#';
                }
            }
        }
    }

    /*
     * mvC – Controller (how we're going to react when a player presses some keys).
     * It's just a key listener. We can do something with Model when a player presses some key.
     */

    public void onKeyPressed(int code) {

    }

    public void onGameStep() {
        // nothing is here for now
    }

    private void startGame() {
        generateBorders();
    }

    private void generateBorders() {
        for (int row = 0; row < ROWS; row++) {
            walls[row][0] = true;
        }
        for (int row = 0; row < ROWS; row++) {
            walls[row][COLS - 1] = true;
        }
        for (int col = 1; col < COLS - 1; col++) {
            walls[0][col] = true;
        }
        for (int col = 1; col < COLS - 1; col++) {
            walls[ROWS - 1][col] = true;
        }
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
