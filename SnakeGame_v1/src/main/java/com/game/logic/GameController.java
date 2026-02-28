package com.game.logic;

import com.game.data.PlayerData;
import com.game.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Core controller for managing snake game logic, state, and player data
 */
public class GameController {

    // Game core entities
    private Snake snake;
    private Food food;
    private PlayerData playerData;
    private int score;
    private int localHighScore;
    private GameState gameState;

    // Listeners for game state/score/high score changes
    private List<GameStateListener> listeners;

    /**
     * Listener interface for game state/score updates
     */
    public interface GameStateListener {
        /**
         * Triggered when game state changes
         * 
         * @param newState New game state
         */
        void onGameStateChanged(GameState newState);

        /**
         * Triggered when current score updates
         * 
         * @param newScore Updated score value
         */
        void onScoreChanged(int newScore);

        /**
         * Triggered when high score is updated
         * 
         * @param newHighScore New high score value
         */
        void onHighScoreChanged(int newHighScore);
    }

    /**
     * Constructor - initialize game with player data
     * 
     * @param playerData Player's persistent data object
     */
    public GameController(PlayerData playerData) {
        this.playerData = playerData;
        this.localHighScore = 0;
        this.listeners = new ArrayList<>();
        resetGame(); // Initialize game state on creation
    }

    /**
     * Add a listener for game state/score changes
     * 
     * @param listener Listener to add
     */
    public void addGameStateListener(GameStateListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a game state/score listener
     * 
     * @param listener Listener to remove
     */
    public void removeGameStateListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all listeners of game state change
     * 
     * @param newState New game state
     */
    private void notifyGameStateChanged(GameState newState) {
        for (GameStateListener listener : listeners) {
            listener.onGameStateChanged(newState);
        }
    }

    /**
     * Notify all listeners of score change
     * 
     * @param newScore Updated score
     */
    private void notifyScoreChanged(int newScore) {
        for (GameStateListener listener : listeners) {
            listener.onScoreChanged(newScore);
        }
    }

    /**
     * Notify all listeners of high score change
     * 
     * @param newHighScore New high score
     */
    private void notifyHighScoreChanged(int newHighScore) {
        for (GameStateListener listener : listeners) {
            listener.onHighScoreChanged(newHighScore);
        }
    }

    /**
     * Reset game to initial state (new snake, food, score)
     */
    public void resetGame() {
        int startX = GameConstants.GAME_WIDTH / 2;
        int startY = GameConstants.GAME_HEIGHT / 2;

        // Initialize snake at center of game area
        snake = new Snake(startX, startY, GameConstants.UNIT_SIZE, GameConstants.INITIAL_SNAKE_LENGTH);
        food = new Food(GameConstants.UNIT_SIZE);
        // Generate food at random position (avoid snake body)
        food.generate(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, snake.getBody());

        score = 0;
        setGameState(GameState.RUNNING);
    }

    /**
     * Update game logic per frame (snake movement, collision, food check)
     */
    public void update() {
        if (gameState != GameState.RUNNING) {
            return; // Skip update if game not running
        }

        snake.move(); // Move snake in current direction
        // Make snake wrap around screen edges
        snake.wrapAround(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Check if snake hits its own body (game over)
        if (snake.checkSelfCollision()) {
            gameOver();
            return;
        }

        // Check if snake ate food
        if (food.isEaten(snake.getHead())) {
            addScore(10); // Add 10 points for eating food
            food.generate(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, snake.getBody()); // Spawn new food
        } else {
            snake.removeTail(); // Remove tail if no food eaten (snake moves forward)
        }
    }

    /**
     * Add points to current score and notify listeners
     * 
     * @param points Points to add
     */
    public void addScore(int points) {
        score += points;
        notifyScoreChanged(score);
    }

    /**
     * Handle game over logic (update state and check high score)
     */
    private void gameOver() {
        setGameState(GameState.GAME_OVER);
        checkAndUpdateHighScore();
    }

    /**
     * Check if current score beats high score and update it
     */
    public void checkAndUpdateHighScore() {
        if (score > getHighScore()) {
            // Update cloud high score if player logged in, else update local
            if (playerData != null && playerData.isLoggedIn()) {
                playerData.updateHighScore(score);
            } else {
                localHighScore = score;
            }
            notifyHighScoreChanged(getHighScore());
        }
    }

    /**
     * Get current high score (player's cloud score or local score)
     * 
     * @return Current high score value
     */
    public int getHighScore() {
        if (playerData != null && playerData.isLoggedIn()) {
            return playerData.getCurrentHighScore();
        }
        return localHighScore;
    }

    /**
     * Check if current score is a new record
     * 
     * @return True if current score > high score
     */
    public boolean isNewRecord() {
        return score > getHighScore();
    }

    // Getters and Setters
    /** Get the snake entity */
    public Snake getSnake() {
        return snake;
    }

    /** Get the food entity */
    public Food getFood() {
        return food;
    }

    /** Get current game score */
    public int getScore() {
        return score;
    }

    /** Get current game state */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Update game state and notify listeners if changed
     * 
     * @param gameState New game state
     */
    public void setGameState(GameState gameState) {
        GameState oldState = this.gameState;
        this.gameState = gameState;

        if (oldState != gameState) {
            notifyGameStateChanged(gameState);
        }
    }

    /** Set snake's movement direction */
    public void setDirection(Direction direction) {
        snake.setDirection(direction);
    }

    /** Get snake's current movement direction */
    public Direction getDirection() {
        return snake.getDirection();
    }

    /** Get game unit size (size of snake/food blocks) */
    public int getUnitSize() {
        return GameConstants.UNIT_SIZE;
    }

    /** Get player's persistent data object */
    public PlayerData getPlayerData() {
        return playerData;
    }

    /** Update player's persistent data */
    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    /** Set local high score (for offline play) */
    public void setLocalHighScore(int localHighScore) {
        this.localHighScore = localHighScore;
    }
}