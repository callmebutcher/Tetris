package com.callmebutcher.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HighScores extends JPanel {
    private BufferedImage backgroundImage, backButton, backPressed;

    public HighScores(JFrame frame) {

        try {
            this.backgroundImage = ImageIO.read(this.getClass().getResource("assets/high_score.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            backButton = ImageIO.read(this.getClass().getResource("assets/backButton.png"));
            backPressed = ImageIO.read(this.getClass().getResource("assets/backPressed.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnNames = { "Position", "Name", "Score" };

        ArrayList<String> playerNames = new ArrayList<String>();
        ArrayList<Integer> playerScores = new ArrayList<Integer>();

        // Map<String, Integer> ScoreMap = new TreeMap<String, Integer>();

        try (BufferedReader br = new BufferedReader(new FileReader("scores.txt"))) {
            for (String line; (line = br.readLine()) != null;) {
                String[] scores = line.split(":");
                String playerName = scores[0];
                playerNames.add(playerName);
                int playerScore = Integer.parseInt(scores[1]);
                playerScores.add(playerScore);
                // System.out.println(playerName+" == "+ playerScore);
                // ScoreMap.put(playerName, playerScore);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(playerNames);
        System.out.println(playerScores);

        // Ordering based on score
        for (int i = 0; i < playerScores.size() - 1; i++) {
            for (int j = 0; j < playerScores.size() - i - 1; j++) {
                int temp_score = playerScores.get(j);
                String temp_name = playerNames.get(j);
                if (playerScores.get(j) < playerScores.get(j + 1)) {
                    playerScores.set(j, playerScores.get(j + 1));
                    playerScores.set(j + 1, temp_score);

                    playerNames.set(j, playerNames.get(j + 1));
                    playerNames.set(j + 1, temp_name);
                }
            }
        }

        Object[][] data = new Object[playerNames.size()][3];

        for (int i = 0; i < playerScores.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = playerNames.get(i);
            data[i][2] = playerScores.get(i);
        }

        // Back Button
        JButton button = new JButton(new ImageIcon(backButton));
        button.setPressedIcon(new ImageIcon(backPressed));
        button.setFont(Main.mainFont);
        button.setBounds(Main.JFRAME_WIDTH / 2 - 100, 650, 175, 65);

        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Main.loadScene(frame, new MainMenu(frame));
            }
        });
        super.add(button);

        // Table Title
        super.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "HIGHSCORES",
                TitledBorder.CENTER, TitledBorder.TOP));

        JTable table = new JTable(data, columnNames);

        table.setFont(Main.text);
        table.setRowHeight(25);
        table.setForeground(Color.WHITE);

        table.setOpaque(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            {
                setOpaque(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        super.add(scrollPane);
        super.add(table);
        super.setVisible(true);
        super.setLayout(new BorderLayout());
        super.add(table.getTableHeader(), BorderLayout.PAGE_START);
        super.add(table, BorderLayout.CENTER);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, null);
    }

}
