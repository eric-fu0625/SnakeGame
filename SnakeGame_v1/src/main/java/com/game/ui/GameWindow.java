package com.game.ui;

import com.game.data.PlayerData;
import com.game.logic.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow {
    private JFrame frame;
    private GameUI gameUI;
    private GamePanel gamePanel;
    private PlayerData playerData;
    private GameController gameController;
    private Runnable onWindowClosed;

    public GameWindow(PlayerData playerData, Runnable onWindowClosed) {
        this.playerData = playerData;
        this.onWindowClosed = onWindowClosed;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        gameUI = new GameUI();
        gameController = new GameController(playerData);
        gamePanel = new GamePanel(gameController);

        Runnable logoutAction = () -> {
            if (gameController != null) {
                gameController.checkAndUpdateHighScore();
            }

            frame.dispose();

            if (onWindowClosed != null) {
                onWindowClosed.run();
            }
        };

        GameMenu gameMenu = new GameMenu(frame, gamePanel, logoutAction);
        frame.setJMenuBar(gameMenu);

        setupUITimer();
        updateGameUI();

        frame.add(gameUI, BorderLayout.NORTH);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        gamePanel.requestFocusInWindow();
    }

    private void setupUITimer() {
        Timer uiTimer = new Timer(100, e -> updateGameUI());
        uiTimer.start();
    }

    private void updateGameUI() {
        if (playerData == null || gameController == null)
            return;

        String username = playerData.getCurrentUsername();
        int score = gameController.getScore();
        int highScore = gameController.getHighScore();

        String status;
        switch (gameController.getGameState()) {
            case RUNNING:
                status = "Playing";
                break;
            case PAUSED:
                status = "Paused";
                break;
            case GAME_OVER:
                status = "Game Over";
                break;
            default:
                status = "Start";
        }

        gameUI.updateAll(username, score, highScore, status);
        if (gameController.isNewRecord()) {
            gameUI.updateHighScore(score);
        }
    }

    public void restartGame() {
        if (gamePanel != null) {
            gamePanel.restartGame();
        }
        updateGameUI();
    }

    public void show() {
        frame.setVisible(true);
    }
}