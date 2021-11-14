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
    final int ROWS = 1;
    final int COLS = 1;

    /*
     * Mvc – Model (data that represents our game objects).
     * Just simple data like game objects coordinates, snake's moving direction, and so on...
     */

    boolean isYes = true;

    /*
     * mVc – View (how we're going to render our game objects).
     * We will take each game object and draw it on the text field (world).
     * We should not have any game logic here! only logic for rendering our game objects!
     */

    public void onRender(char[][] world) {
        world[0][0] = isYes ? 'Y' : 'N';
    }

    /*
     * mvC – Controller (how we're going to react when a player presses some keys).
     * It's just a key listener. We can do something with Model when a player presses some key.
     */

    public void onKeyPressed(int code) {
        switch (code) {
            case KeyEvent.VK_Y:
                isYes = true;
                break;
            case KeyEvent.VK_N:
                isYes = false;
                break;
        }
    }

    public void onGameStep() {
        // nothing is here for now
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
            font = Font.createFont(Font.TRUETYPE_FONT, fontFileStream).deriveFont(Font.PLAIN, 200);
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

        // Game Loop
        new Thread(() -> {
            char[][] world = new char[ROWS][COLS];
            while (true) {
                onGameStep();
                onRender(world);
                textArea.setText(String.valueOf(world[0][0]));
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
