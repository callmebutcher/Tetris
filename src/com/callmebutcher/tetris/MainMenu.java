package com.callmebutcher.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
//import java.util.Properties;

public class MainMenu extends JPanel {
    private BufferedImage background;
    private BufferedImage startButton, highscore, rule, close;
    private JFrame frame;

    private JButton play;
    private JButton highscores;
    private JButton rules;
    private JButton exit;

    private int buttonsWidth;
    private int buttonsHeight;

    private int yOffset;
    private int spacing;
    private BufferedImage startPressed;
    private BufferedImage highscorepressed;
    private BufferedImage rulespressed;
    private BufferedImage exitpressed;

    public MainMenu(JFrame frame) {
        super.setLayout(null);
        this.buttonsWidth = 175;
        this.buttonsHeight = 65;
        this.yOffset = Main.JFRAME_HEIGHT / 3;
        this.spacing = (int) (this.buttonsHeight * 1.5f);
        this.frame = frame;

        try {
            startButton = ImageIO.read(this.getClass().getResource("assets/startButton.png"));
            highscore = ImageIO.read(this.getClass().getResource("assets/highscore.png"));
            rule = ImageIO.read(this.getClass().getResource("assets/rules.png"));
            close = ImageIO.read(this.getClass().getResource("assets/close.png"));
            startPressed = ImageIO.read(this.getClass().getResource("assets/startpressed.png"));
            highscorepressed = ImageIO.read(this.getClass().getResource("assets/highscorepressed.png"));
            rulespressed = ImageIO.read(this.getClass().getResource("assets/rulespressed.png"));
            exitpressed = ImageIO.read(this.getClass().getResource("assets/exitpressed.png"));
            background = ImageIO.read(this.getClass().getResource("assets/main_menu.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.play = new JButton(new ImageIcon(startButton, "PLAY"));
        this.play.setPressedIcon(new ImageIcon(startPressed));
        this.highscores = new JButton(new ImageIcon(highscore, "HIGH SCORES"));
        this.highscores.setPressedIcon(new ImageIcon(highscorepressed));
        this.rules = new JButton(new ImageIcon(rule, "OPTIONS"));
        this.rules.setPressedIcon(new ImageIcon(rulespressed));
        this.exit = new JButton(new ImageIcon(close, "EXIT"));
        this.exit.setPressedIcon(new ImageIcon(exitpressed));

        this.play.setBorder(BorderFactory.createEmptyBorder());
        this.play.setContentAreaFilled(false);
        this.highscores.setBorder(BorderFactory.createEmptyBorder());
        this.highscores.setContentAreaFilled(false);
        this.rules.setBorder(BorderFactory.createEmptyBorder());
        this.rules.setContentAreaFilled(false);
        this.exit.setBorder(BorderFactory.createEmptyBorder());
        this.exit.setContentAreaFilled(false);

        this.play.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2,
                Main.JFRAME_HEIGHT / 2 + this.buttonsHeight - this.yOffset, this.buttonsWidth, this.buttonsHeight);

        this.highscores.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2, this.play.getY() + this.spacing,
                this.buttonsWidth, this.buttonsHeight);

        this.rules.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2, this.highscores.getY() + this.spacing,
                this.buttonsWidth, this.buttonsHeight);

        this.exit.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2, this.rules.getY() + this.spacing,
                this.buttonsWidth, this.buttonsHeight);

        this.play.addActionListener(e -> Main.loadScene(this.frame, new Board(this.frame)));
        this.highscores.addActionListener(e -> Main.loadScene(this.frame, new HighScores(this.frame)));
        this.rules.addActionListener(e -> new Help().createHelpWindow());
        this.exit.addActionListener(e -> Main.loadScene(this.frame, new Exit()));

        super.add(this.play);
        super.add(this.highscores);
        super.add(this.rules);
        super.add(this.exit);
        // Properties p = new Properties();

        // try {
        // p.load(new
        // FileInputStream(this.getClass().getResource("/settings/options.properties").getPath()));
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.background, 0, 0, null);
        Main.paintInfo(g);
    }
}
