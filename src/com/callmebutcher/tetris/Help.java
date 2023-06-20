package com.callmebutcher.tetris;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Help extends JPanel {

    private String howToPlay = "C: Change Tetromino\n" +
            "Up: Drop Tetromino to bottom\n" +
            "Down: Drop Tetromino faster\n" +
            "Right: Move Tetromino to the right\n" +
            "Left: Move Tertomino to the left\n" +
            "Space: Rotate Tetromino\n" +
            "P: Pause / Resume";

    public void createHelpWindow() {
        JPanel middlePanel = new JPanel();
        middlePanel.setBorder(new TitledBorder(new EtchedBorder(), "Instructions"));

        JTextArea display = new JTextArea(10, 20);
        display.setText(this.howToPlay);
        display.setFont(Main.textField);
        display.setEditable(false);
        JScrollPane scroll = new JScrollPane(display);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        middlePanel.add(scroll);

        JFrame frame2 = new JFrame();
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame2.add(middlePanel);
        frame2.pack();
        frame2.setLocationRelativeTo(null);
        frame2.setResizable(false);
        frame2.setVisible(true);
    }

}
