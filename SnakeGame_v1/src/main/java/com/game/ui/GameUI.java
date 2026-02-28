package com.game.ui;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JPanel {
    private JLabel usernameLabel;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JLabel gameStatusLabel;

    public GameUI() {
        setLayout(new GridLayout(1, 4, 10, 0)); // one row four columns
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(50, 50, 60));

        usernameLabel = createStyledLabel("Player: not login yet", new Color(200, 220, 255));
        add(usernameLabel);

        scoreLabel = createStyledLabel("Score: 0", new Color(255, 255, 200));
        add(scoreLabel);

        highScoreLabel = createStyledLabel("High Score: 0", new Color(200, 255, 200));
        add(highScoreLabel);

        gameStatusLabel = createStyledLabel("State: Start", new Color(255, 200, 200));
        add(gameStatusLabel);
    }

    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Times New Roman", Font.BOLD, 14));
        label.setForeground(color);
        label.setOpaque(true);
        label.setBackground(new Color(70, 70, 80));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 110), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return label;
    }

    public void updateUsername(String username) {
        usernameLabel.setText("Player: " + (username != null ? username : "Guest"));
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);

        if (score > 100) {
            scoreLabel.setForeground(new Color(255, 255, 100));
        } else if (score > 50) {
            scoreLabel.setForeground(new Color(255, 200, 100));
        } else {
            scoreLabel.setForeground(new Color(255, 255, 200));
        }
    }

    public void updateHighScore(int highScore) {
        highScoreLabel.setText("High Score: " + highScore);

        if (highScore > 500) {
            highScoreLabel.setForeground(new Color(100, 255, 100));
        } else if (highScore > 200) {
            highScoreLabel.setForeground(new Color(150, 255, 150));
        } else {
            highScoreLabel.setForeground(new Color(200, 255, 200));
        }
    }

    public void updateGameStatus(String status) {
        gameStatusLabel.setText("State: " + status);

        switch (status) {
            case "Playing":
                gameStatusLabel.setForeground(new Color(100, 255, 100));
                break;
            case "Paused":
                gameStatusLabel.setForeground(new Color(255, 255, 100));
                break;
            case "Game Over":
                gameStatusLabel.setForeground(new Color(255, 100, 100));
                break;
            default:
                gameStatusLabel.setForeground(new Color(255, 200, 200));
        }
    }

    public void updateAll(String username, int score, int highScore, String status) {
        updateUsername(username);
        updateScore(score);
        updateHighScore(highScore);
        updateGameStatus(status);
    }
}