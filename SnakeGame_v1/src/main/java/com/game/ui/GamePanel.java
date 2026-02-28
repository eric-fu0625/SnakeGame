package com.game.ui;

import com.game.data.PlayerData;
import com.game.logic.*;
import com.game.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamePanel extends JPanel implements ActionListener {
    private GameController gameController;
    private javax.swing.Timer gameTimer;
    private static final int DEFAULT_DELAY = 200;
    private boolean showGrid = true;
    private Runnable onLogout;
    private long lastToggleTime = 0;
    private static final long TOGGLE_COOLDOWN = 200;

    private SpecialFood specialFood;
    private java.util.Timer specialFoodSpawnTimer;
    private java.util.Timer specialFoodExistenceTimer;
    private boolean specialFoodVisible = false;
    private AtomicBoolean isGamePaused = new AtomicBoolean(false);

    public GamePanel() {
        this(null, null);
    }

    public GamePanel(GameController gameController) {
        this(gameController, null);
    }

    public GamePanel(GameController gameController, Runnable onLogout) {
        this.gameController = gameController;
        this.onLogout = onLogout;
        if (this.gameController == null) {
            this.gameController = new GameController(new PlayerData());
        }

        initPanel();
        initSpecialFood();
    }

    private void initSpecialFood() {
        int unitSize = gameController.getUnitSize();
        specialFood = new SpecialFood(unitSize, isGamePaused, 800, 600, () -> gameController.getSnake().getBody());
    }

    public long getSpecialFoodRemainingTime() {
        return specialFood.getRemainingTime();
    }

    public void toggleGrid() {
        this.showGrid = !this.showGrid;
        repaint();
        System.out.println("Grid toggled to: " + showGrid);
    }

    public void setGridVisible(boolean visible) {
        this.showGrid = visible;
        repaint();
        System.out.println("Grid visibility set to: " + visible);
    }

    public void setShowGrid(boolean showGrid) {
        setGridVisible(showGrid);
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    private void initPanel() {
        setPreferredSize(new Dimension(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT));
        setBackground(UITheme.COLOR_BACKGROUND);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new GameKeyListener(gameController, this, onLogout));

        SwingUtilities.invokeLater(() -> {
            disableMenuShortcutsForKeys();
        });

        startGame();
        requestFocusInWindow();
    }

    public void setGameSpeed(int delay) {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new javax.swing.Timer(delay, this);
        gameTimer.start();
        System.out.println("Set Game Speed to: " + delay + "ms");
        repaint();
    }

    public void startGame() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        gameTimer = new javax.swing.Timer(DEFAULT_DELAY, this);
        gameTimer.start();
        gameController.setGameState(GameState.RUNNING);
        isGamePaused.set(false);
    }

    public void restartGame() {
        gameController.resetGame();

        specialFoodVisible = false;
        specialFood.setPosition(null);
        isGamePaused.set(false);

        if (specialFoodExistenceTimer != null) {
            specialFoodExistenceTimer.cancel();
            specialFoodExistenceTimer.purge();
            specialFoodExistenceTimer = null;
        }
        if (specialFoodSpawnTimer != null) {
            specialFoodSpawnTimer.cancel();
            specialFoodSpawnTimer.purge();
            specialFoodSpawnTimer = null;
        }

        startGame();
        requestFocusInWindow();
    }

    public void disableMenuShortcutsForKeys() {
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window instanceof javax.swing.JFrame) {
            javax.swing.JFrame frame = (javax.swing.JFrame) window;
            javax.swing.JMenuBar menuBar = frame.getJMenuBar();
            if (menuBar != null) {
                disableSpaceShortcutsInMenu(menuBar);
            }
        }
    }

    private void disableSpaceShortcutsInMenu(javax.swing.JMenuBar menuBar) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            javax.swing.JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                disableSpaceShortcutsInMenuItems(menu);
            }
        }
    }

    private void disableSpaceShortcutsInMenuItems(javax.swing.JMenu menu) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            javax.swing.JMenuItem item = menu.getItem(i);
            if (item != null && item.getAccelerator() != null) {
                javax.swing.KeyStroke ks = item.getAccelerator();
                if (ks.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    item.setAccelerator(null);
                    System.out.println("Disabled SPACE shortcut for menu item: " + item.getText());
                }
            }
        }
    }

    public void togglePause() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime < TOGGLE_COOLDOWN) {
            System.out.println("Toggle pause ignored - too fast");
            return;
        }
        lastToggleTime = currentTime;

        GameState currentState = gameController.getGameState();
        System.out.println("togglePause called. Current state: " + currentState);

        if (currentState == GameState.RUNNING) {
            isGamePaused.set(true);
            gameController.setGameState(GameState.PAUSED);
            if (gameTimer != null) {
                gameTimer.stop();
                System.out.println("Game paused. Timer stopped.");
            }
        } else if (currentState == GameState.PAUSED) {
            gameController.setGameState(GameState.RUNNING);
            if (gameTimer != null) {
                gameTimer.start();
                System.out.println("Game resumed. Timer started.");
            }
            isGamePaused.set(false);
            specialFood.resumeTimers();
            specialFood.startSpawnCooldown();
        } else {
            System.out.println("Cannot toggle pause in state: " + currentState);
        }
        repaint();
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
        removeAllKeyListeners();
        addKeyListener(new GameKeyListener(gameController, this, onLogout));
        requestFocusInWindow();
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        removeAllKeyListeners();
        addKeyListener(new GameKeyListener(gameController, this, onLogout));
        restartGame();
    }

    private void removeAllKeyListeners() {
        for (java.awt.event.KeyListener listener : getKeyListeners()) {
            removeKeyListener(listener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGamePaused.get()) {
            return;
        }
        gameController.update();
        checkSpecialFoodCollision();
        repaint();
    }

    private void checkSpecialFoodCollision() {
        if (!isGamePaused.get() && gameController.getSnake() != null) {
            Point snakeHead = gameController.getSnake().getHead();
            if (CollisionDetector.isSpecialFoodEaten(snakeHead, specialFood)) {
                gameController.addScore(specialFood.getScore());
                System.out.println("Eat SpecialFood! + 100 points");
                specialFood.disappear();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        GameState state = gameController.getGameState();
        switch (state) {
            case RUNNING:
            case PAUSED:
                drawGameElements(g);
                drawUI(g);
                if (state == GameState.PAUSED) {
                    drawPauseOverlay(g);
                }
                break;
            case GAME_OVER:
                drawGameOverScreen(g);
                break;
        }
    }

    private void drawGameElements(Graphics g) {
        drawFood(g);
        drawSnake(g);
        drawSpecialFood(g);
    }

    private void drawFood(Graphics g) {
        Food food = gameController.getFood();
        if (food != null) {
            food.draw(g, gameController.getUnitSize());
        }
    }

    private void drawSpecialFood(Graphics g) {
        specialFood.draw(g, gameController.getUnitSize());
    }

    private void drawSnake(Graphics g) {
        Snake snake = gameController.getSnake();
        if (snake != null) {
            snake.draw(g, gameController.getUnitSize());
        }
    }

    private void drawUI(Graphics g) {
        drawGrid(g);
        drawScorePanel(g);
        drawHints(g);
        drawGridStatus(g);
    }

    private void drawGrid(Graphics g) {
        if (showGrid) {
            g.setColor(UITheme.COLOR_GRID);
            int unitSize = gameController.getUnitSize();
            for (int x = 0; x < GameConstants.GAME_WIDTH; x += unitSize) {
                g.drawLine(x, 0, x, GameConstants.GAME_HEIGHT);
            }
            for (int y = 0; y < GameConstants.GAME_HEIGHT; y += unitSize) {
                g.drawLine(0, y, GameConstants.GAME_WIDTH, y);
            }
        }
    }

    private void drawScorePanel(Graphics g) {
        g.setColor(UITheme.COLOR_TEXT);
        g.setFont(UITheme.FONT_SCORE);
        g.drawString("Score: " + gameController.getScore(), 15, 30);
        g.drawString("High Score: " + gameController.getHighScore(), 15, 55);
    }

    private void drawGridStatus(Graphics g) {
        if (!showGrid) {
            g.setColor(UITheme.COLOR_GRID_OFF_HINT);
            g.setFont(UITheme.FONT_GRID_STATUS);
            g.drawString("Grid: OFF", GameConstants.GAME_WIDTH - 80, GameConstants.GAME_HEIGHT - 10);
        }
    }

    private void drawHints(Graphics g) {
        g.setColor(UITheme.COLOR_HINT);
        g.setFont(UITheme.FONT_SHORTCUT_HINT);

        String controlHint1 = "SPACE: Pause";
        g.drawString(controlHint1, 15, GameConstants.GAME_HEIGHT - 85);
        String controlHint2 = "F2: Restart";
        g.drawString(controlHint2, 15, GameConstants.GAME_HEIGHT - 70);
        String controlHint3 = "ESC: Logout";
        g.drawString(controlHint3, 15, GameConstants.GAME_HEIGHT - 55);
        String controlHint4 = "WASD/Arrows: Move";
        g.drawString(controlHint4, 15, GameConstants.GAME_HEIGHT - 40);
        String controlHint5 = "G: Toggle Grid";
        g.drawString(controlHint5, 15, GameConstants.GAME_HEIGHT - 25);
        String shortcutHint = "1-4: Speed (1-Slow, 4-Fast)";
        g.drawString(shortcutHint, 15, GameConstants.GAME_HEIGHT - 10);

        if (!specialFoodVisible) {
            String specialFoodHint = "SpecialFood appear every 20 seconds. ";
            g.drawString(specialFoodHint, GameConstants.GAME_WIDTH - 200, GameConstants.GAME_HEIGHT - 25);
        }
    }

    private void drawPauseOverlay(Graphics g) {
        g.setColor(UITheme.COLOR_OVERLAY);
        g.fillRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        g.setColor(UITheme.COLOR_HIGHLIGHT);
        g.setFont(UITheme.FONT_PAUSE);
        FontMetrics metrics = getFontMetrics(g.getFont());
        String pauseText = "PAUSED";
        g.drawString(pauseText,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(pauseText)) / 2,
                GameConstants.GAME_HEIGHT / 2 - 50);

        g.setColor(UITheme.COLOR_TEXT);
        g.setFont(UITheme.FONT_CONTINUE);
        metrics = getFontMetrics(g.getFont());
        String continueText = "Press SPACE to continue";
        g.drawString(continueText,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(continueText)) / 2,
                GameConstants.GAME_HEIGHT / 2 + 20);

        String logoutText = "Press ESC to logout";
        g.drawString(logoutText,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(logoutText)) / 2,
                GameConstants.GAME_HEIGHT / 2 + 50);

        String speedHint = "Speed: 1-Slow, 4-Fast";
        g.drawString(speedHint,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(speedHint)) / 2,
                GameConstants.GAME_HEIGHT / 2 + 80);

        g.setFont(UITheme.FONT_GRID_STATUS_SMALL);
        String gridStatus = "Grid: " + (showGrid ? "ON" : "OFF");
        metrics = getFontMetrics(g.getFont());
        g.drawString(gridStatus,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(gridStatus)) / 2,
                GameConstants.GAME_HEIGHT / 2 + 110);

        if (specialFoodVisible) {
            long remainingSeconds = getSpecialFoodRemainingTime() / 1000;
            String specialFoodStatus = "SpecialFood: " + remainingSeconds + " seconds remaining";
            metrics = getFontMetrics(g.getFont());
            g.drawString(specialFoodStatus,
                    (GameConstants.GAME_WIDTH - metrics.stringWidth(specialFoodStatus)) / 2,
                    GameConstants.GAME_HEIGHT / 2 + 140);
        }
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(UITheme.COLOR_OVERLAY_DARK);
        g.fillRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        g.setColor(UITheme.COLOR_WARNING);
        g.setFont(UITheme.FONT_GAME_OVER);
        FontMetrics metrics = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER";
        g.drawString(gameOverText,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(gameOverText)) / 2,
                GameConstants.GAME_HEIGHT / 2 - 80);

        g.setColor(UITheme.COLOR_TEXT);
        g.setFont(UITheme.FONT_FINAL_SCORE);
        metrics = getFontMetrics(g.getFont());
        String scoreText = "Final Score: " + gameController.getScore();
        g.drawString(scoreText,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(scoreText)) / 2,
                GameConstants.GAME_HEIGHT / 2 - 10);

        boolean isNewHighScore = gameController.getScore() > gameController.getHighScore();
        if (isNewHighScore) {
            g.setColor(UITheme.COLOR_HIGHLIGHT);
            String newHighScore = "NEW HIGH SCORE!";
            metrics = getFontMetrics(g.getFont());
            g.drawString(newHighScore,
                    (GameConstants.GAME_WIDTH - metrics.stringWidth(newHighScore)) / 2,
                    GameConstants.GAME_HEIGHT / 2 + 30);
        }

        g.setColor(UITheme.COLOR_INFO);
        g.setFont(UITheme.FONT_RESTART_HINT);
        metrics = getFontMetrics(g.getFont());
        String restartText = "Press SPACE or F2 to restart";
        g.drawString(restartText,
                (GameConstants.GAME_WIDTH - metrics.stringWidth(restartText)) / 2,
                GameConstants.GAME_HEIGHT / 2 + 80);
    }

    public void cleanUp() {
        isGamePaused.set(true);

        if (specialFood != null) {
            specialFood.cleanUp();
        }

        if (specialFoodExistenceTimer != null) {
            specialFoodExistenceTimer.cancel();
            specialFoodExistenceTimer.purge();
            specialFoodExistenceTimer = null;
        }

        if (specialFoodSpawnTimer != null) {
            specialFoodSpawnTimer.cancel();
            specialFoodSpawnTimer.purge();
            specialFoodSpawnTimer = null;
        }

        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
}