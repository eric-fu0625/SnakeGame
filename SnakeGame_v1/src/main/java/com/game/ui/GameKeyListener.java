package com.game.ui;

import com.game.logic.*;
import com.game.utils.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

public class GameKeyListener extends KeyAdapter {
    private GameController gameController;
    private GamePanel gamePanel;
    private Runnable onLogout;

    public GameKeyListener(GameController gameController, GamePanel gamePanel, Runnable onLogout) {
        this.gameController = gameController;
        this.gamePanel = gamePanel;
        this.onLogout = onLogout;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        GameState gameState = gameController.getGameState();

        if (gameState == GameState.GAME_OVER) {
            handleGameOverKeys(keyCode);
            return;
        }

        handleGameKeys(keyCode);
    }

    private void handleGameOverKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                gameController.checkAndUpdateHighScore();
                gamePanel.restartGame();
                break;

            case KeyEvent.VK_F2:
                gameController.checkAndUpdateHighScore();
                gamePanel.restartGame();
                break;

            case KeyEvent.VK_ESCAPE:
                performLogout();
                break;
        }
    }

    private void handleGameKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                gameController.setDirection(Direction.LEFT);
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                gameController.setDirection(Direction.RIGHT);
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                gameController.setDirection(Direction.UP);
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                gameController.setDirection(Direction.DOWN);
                break;

            case KeyEvent.VK_SPACE:
                if (gamePanel != null) {
                    gamePanel.togglePause();
                }
                break;

            case KeyEvent.VK_ESCAPE:
                if (onLogout != null) {
                    onLogout.run();
                }
                break;

            case KeyEvent.VK_F2:
                gameController.checkAndUpdateHighScore();
                gamePanel.restartGame();
                break;

            case KeyEvent.VK_G:
                if (gamePanel != null) {
                    gamePanel.toggleGrid();
                }
                break;

            case KeyEvent.VK_1:
                gamePanel.setGameSpeed(300);
                break;
            case KeyEvent.VK_2:
                gamePanel.setGameSpeed(200);
                break;
            case KeyEvent.VK_3:
                gamePanel.setGameSpeed(100);
                break;
            case KeyEvent.VK_4:
                gamePanel.setGameSpeed(50);
                break;
        }
    }

    private void performLogout() {
        if (gamePanel != null && gamePanel.getTopLevelAncestor() != null) {
            java.awt.Window window = SwingUtilities.getWindowAncestor(gamePanel);
            if (window != null && window instanceof javax.swing.JFrame) {
                System.out.println("ESC pressed - Logout requested");

                gamePanel.cleanUp();

                if (onLogout != null) {
                    onLogout.run();
                }
            }
        }
    }
}