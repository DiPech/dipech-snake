package com.dipech;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Application {

    public static void main(String[] args) {
        new Application().start();
    }

    public void start() {
        JFrame frame = new JFrame("Snake");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea(5, 5);
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
        textArea.setText("  t\n  e\ntenet\n  e\n  t");
        frame.getContentPane().add(textArea, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
