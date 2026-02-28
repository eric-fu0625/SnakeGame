package com.game.logic;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.game.utils.UITheme;

/**
 * SpecialFood class extends Food, represents special food in the game
 * It has time-limited existence and spawn cooldown mechanisms
 */
public class SpecialFood extends Food {
    private Timer existenceTimer;
    private Timer spawnCooldownTimer;
    private Timer spawnCheckTimer;
    private boolean canSpawn = false;
    private boolean isVisible = false;
    private long createTime = 0;
    private final AtomicBoolean isGamePaused;
    private final int gameWidth;
    private final int gameHeight;
    private Supplier<List<Point>> snakeBodySupplier;

    public static final int SPECIALFOOD_SCORE = 100;
    public static final int DURATION_MS = 10000;
    public static final int SPAWN_COOLDOWN_MS = 20000;
    public static final int SPAWN_CHECK_INTERVAL = 100;

    public SpecialFood(int unitSize, AtomicBoolean isGamePaused, int gameWidth, int gameHeight,
            Supplier<List<Point>> snakeBodySupplier) {
        super(unitSize);
        this.isGamePaused = isGamePaused;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.snakeBodySupplier = snakeBodySupplier;

        startSpawnCooldown();
        startSpawnCheckTimer();
    }

    public SpecialFood(int unitSize, Supplier<List<Point>> snakeBodySupplier) {
        this(unitSize, new AtomicBoolean(false), 600, 600, snakeBodySupplier);
    }

    @Override
    public void generate(int width, int height, List<Point> snakeBody) {
        if (isGamePaused.get() || !canSpawn || isVisible) {
            return;
        }
        super.generate(width, height, snakeBody);
        Point foodPos = getPosition();

        if (foodPos != null && !snakeBody.contains(foodPos)) {
            isVisible = true;
            createTime = System.currentTimeMillis();
            canSpawn = false;
            startExistenceTimer();
            startSpawnCooldown();
            System.out.println("SpecialFood: " + foodPos + ", existing for " + (DURATION_MS / 1000) + " seconds");
        }
    }

    @Override
    public void draw(Graphics g, int unitSize) {
        if (getPosition() == null || !isVisible) {
            return;
        }

        g.setColor(UITheme.COLOR_SPECIAL_FOOD);
        g.fillOval(getPosition().x, getPosition().y, unitSize, unitSize);
        g.setColor(UITheme.COLOR_SpecialFOOD_HIGHLIGHT);
        g.fillOval(
                getPosition().x + unitSize / 4,
                getPosition().y + unitSize / 4,
                unitSize / 4,
                unitSize / 4);
    }

    /**
     * Start/reset spawn check timer
     * Check spawn condition at fixed interval (SPAWN_CHECK_INTERVAL)
     */
    private void startSpawnCheckTimer() {
        if (spawnCheckTimer != null) {
            spawnCheckTimer.cancel();
            spawnCheckTimer.purge();
        }

        spawnCheckTimer = new Timer("SpecialFoodSpawnChecker", true);
        spawnCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Skip if game paused, food visible or spawn not allowed
                if (isGamePaused.get() || isVisible || !canSpawn) {
                    return;
                }
                // 使用snakeBodySupplier获取当前蛇的身体位置
                List<Point> currentSnakeBody = snakeBodySupplier.get();
                if (currentSnakeBody != null) {
                    generate(gameWidth, gameHeight, currentSnakeBody);
                }
            }
        }, 0, SPAWN_CHECK_INTERVAL);
    }

    /**
     * Start/reset existence timer
     * Make food disappear after DURATION_MS
     */
    private void startExistenceTimer() {
        if (existenceTimer != null) {
            existenceTimer.cancel();
            existenceTimer.purge();
        }

        existenceTimer = new Timer("SpecialFoodExistenceTimer", true);
        existenceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Skip if game is paused
                if (isGamePaused.get()) {
                    return;
                }
                disappear();
            }
        }, DURATION_MS);
    }

    /**
     * Start/reset spawn cooldown timer
     * Set canSpawn to true after SPAWN_COOLDOWN_MS
     */
    public void startSpawnCooldown() {
        if (spawnCooldownTimer != null) {
            spawnCooldownTimer.cancel();
            spawnCooldownTimer.purge();
        }

        spawnCooldownTimer = new Timer("SpecialFoodCooldownTimer", true);
        spawnCooldownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Restart cooldown if game is paused
                if (isGamePaused.get()) {
                    startSpawnCooldown();
                    return;
                }
                canSpawn = true;
                System.out.println("SpecialFood cool down, generated available");
            }
        }, SPAWN_COOLDOWN_MS);
    }

    /**
     * Make special food disappear (reset position and visibility)
     */
    public void disappear() {
        if (!isVisible) {
            return;
        }
        cancelExistenceTimer();

        System.out.println("SpecialFood disappear, enter 20s CD");
        setPosition(null);
        isVisible = false;
        createTime = 0;
    }

    private void cancelExistenceTimer() {
        if (existenceTimer != null) {
            existenceTimer.cancel();
            existenceTimer.purge();
            existenceTimer = null;
        }
    }

    /**
     * Resume all timers after game unpauses
     * Recalculate remaining existence time for visible food
     */
    public void resumeTimers() {
        if (isVisible && createTime > 0) {
            long elapsed = System.currentTimeMillis() - createTime;
            long remaining = DURATION_MS - elapsed;

            if (remaining > 0) {
                if (existenceTimer != null) {
                    existenceTimer.cancel();
                    existenceTimer.purge();
                }
                existenceTimer = new Timer(true);
                existenceTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isGamePaused.get()) {
                            disappear();
                        }
                    }
                }, remaining);
            } else {
                disappear();
                startSpawnCooldown();
                return;
            }
        }
        startSpawnCooldown();
        startSpawnCheckTimer();
    }

    /**
     * Get score of special food
     * 
     * @return SPECIALFOOD_SCORE (100)
     */
    public int getScore() {
        return SPECIALFOOD_SCORE;
    }

    /**
     * Clean up all timers and reset state
     */
    @Override
    public void cleanUp() {
        if (existenceTimer != null) {
            existenceTimer.cancel();
            existenceTimer.purge();
            existenceTimer = null;
        }
        if (spawnCooldownTimer != null) {
            spawnCooldownTimer.cancel();
            spawnCooldownTimer.purge();
            spawnCooldownTimer = null;
        }
        if (spawnCheckTimer != null) {
            spawnCheckTimer.cancel();
            spawnCheckTimer.purge();
            spawnCheckTimer = null;
        }

        setPosition(null);
        isVisible = false;
        canSpawn = false;
        createTime = 0;
    }

    /**
     * Check if special food is visible
     * 
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Calculate remaining existence time of special food
     * 
     * @return Remaining time (ms), 0 if not visible
     */
    public long getRemainingTime() {
        if (!isVisible)
            return 0;
        long elapsed = System.currentTimeMillis() - createTime;
        return Math.max(0, DURATION_MS - elapsed);
    }

    /**
     * Set snake body supplier
     * 
     * @param snakeBodySupplier Supplier that provides current snake body positions
     */
    public void setSnakeBodySupplier(Supplier<List<Point>> snakeBodySupplier) {
        this.snakeBodySupplier = snakeBodySupplier;
    }
}