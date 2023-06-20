package com.callmebutcher.tetris;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    static final int JFRAME_WIDTH = 900;
    static final int JFRAME_HEIGHT = 800;
    static Font info;
    static Font mainFont;
    static Font text;
    static Font textField;
    static Font labelFont;

    public static void main(String args[]) {
        JFrame frame = new JFrame("Tetris");
        Main.info = new Font("Monospaced", Font.BOLD, 15);
        Main.mainFont = new Font("Dialog", Font.BOLD, 20);
        Main.text = new Font("Dialog", Font.BOLD, 15);
        Main.textField = new Font("SansSerif", Font.BOLD, 20);
        Main.labelFont = new Font("SansSerif", Font.BOLD, 25);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainMenu(frame));
        frame.setPreferredSize(new Dimension(JFRAME_WIDTH, JFRAME_HEIGHT));
        centerWindow(frame);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void centerWindow(JFrame f) {
        Dimension windowSize = f.getPreferredSize();
        Dimension pos = Toolkit.getDefaultToolkit().getScreenSize();

        int dx = (pos.width / 2) - (windowSize.width / 2);
        int dy = (pos.height / 2) - (windowSize.height / 2);
        f.setLocation(dx, dy);

    }

    public static void loadScene(JFrame frame, JPanel panel) {
        frame.setContentPane(panel);
        frame.invalidate();
        frame.validate();
        panel.requestFocus();
    }

    public static void paintInfo(Graphics g) {
        int x = 5;
        int y = 740;
        g.setColor(Color.ORANGE);
        g.setFont(Main.info);
        g.drawString("Github: callmebutcher, Version: 1.0.1", x, y);
    }
}
