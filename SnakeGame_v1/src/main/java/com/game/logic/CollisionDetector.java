package com.game.logic;

import java.awt.Point;
import java.util.List;

/**
 * Collision detection utility class for game
 * Provides static methods to detect various collision scenarios in the game
 */
public class CollisionDetector {
    /**
     * Check if the snake collides with its own body
     * 
     * @param snakeBody List of Points representing snake's body (head at index 0)
     * @return true if self-collision occurs, false otherwise
     */
    public static boolean isSnakeSelfCollided(List<Point> snakeBody) {
        if (snakeBody == null || snakeBody.size() < 2) {
            return false;
        }
        Point head = snakeBody.get(0);
        for (int i = 1; i < snakeBody.size(); i++) {
            Point bodyPart = snakeBody.get(i);
            if (head.x == bodyPart.x && head.y == bodyPart.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the snake's head eats the regular food
     * 
     * @param snakeHead    Point of snake's head position
     * @param foodPosition Point of regular food position
     * @param unitSize     Size of game unit (not used in current logic)
     * @return true if food is eaten, false otherwise
     */
    public static boolean isFoodEaten(Point snakeHead, Point foodPosition, int unitSize) {
        if (snakeHead == null || foodPosition == null) {
            return false;
        }
        return snakeHead.x == foodPosition.x
                && snakeHead.y == foodPosition.y;
    }

    /**
     * Check if the snake's head eats the special food
     * 
     * @param snakeHead   Point of snake's head position
     * @param specialFood SpecialFood object to check
     * @return true if special food is eaten (and visible), false otherwise
     */
    public static boolean isSpecialFoodEaten(Point snakeHead, SpecialFood specialFood) {
        if (snakeHead == null || specialFood == null || !specialFood.isVisible()) {
            return false;
        }
        return specialFood.isEaten(snakeHead);
    }

    /**
     * Check if a position is out of the game boundary
     * 
     * @param position Point to check
     * @param width    Game area width (max X coordinate)
     * @param height   Game area height (max Y coordinate)
     * @return true if out of bounds, false otherwise
     */
    public static boolean isOutOfBounds(Point position, int width, int height) {
        if (position == null) {
            return false;
        }
        return position.x < 0 || position.x >= width
                || position.y < 0 || position.y >= height;
    }

    /**
     * Private constructor to prevent instantiation of utility class
     * 
     * @throws UnsupportedOperationException Always thrown to prohibit instantiation
     */
    private CollisionDetector() {
        throw new UnsupportedOperationException(
                "This is a utility class and instantiation is prohibited.");
    }
}