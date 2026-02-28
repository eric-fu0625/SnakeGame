package com.game.logic;

import java.awt.*;
import java.util.List;
import java.util.Random;

import com.game.utils.UITheme;

/**
 * Food class represents basic food in the game
 * Provides methods for generating position, drawing, and getting score
 */
public class Food {
    private Random random;
    private Point position;
    private int unitSize;
    private int FOOD_SCORE = 10;

    public Food(int unitSize) {
        this.random = new Random();
        this.unitSize = unitSize;
    }

    public void generate(int width, int height, List<Point> snakeBody) {
        int maxX = width / unitSize;
        int maxY = height / unitSize;

        if (random == null) {
            random = new Random();
        }

        for (int attempt = 0; attempt < 100; attempt++) {
            int x = random.nextInt(maxX) * unitSize;
            int y = random.nextInt(maxY) * unitSize;
            position = new Point(x, y);

            if (!snakeBody.contains(position)) {
                return;
            }
        }

        for (int y = 0; y < height; y += unitSize) {
            for (int x = 0; x < width; x += unitSize) {
                Point point = new Point(x, y);
                if (!snakeBody.contains(point)) {
                    position = point;
                    return;
                }
            }
        }

        position = new Point(0, 0);
    }

    public void draw(Graphics g, int unitSize) {
        if (position == null)
            return;

        g.setColor(UITheme.COLOR_FOOD);
        g.fillOval(position.x, position.y, unitSize, unitSize);

        g.setColor(UITheme.COLOR_FOOD_HIGHLIGHT);
        g.fillOval(
                position.x + unitSize / 4,
                position.y + unitSize / 4,
                unitSize / 4,
                unitSize / 4);
    }

    // Getters and Setters

    public int getScore() {
        return FOOD_SCORE;
    }

    public boolean isEaten(Point snakeHead) {
        return position != null && position.equals(snakeHead);
    }

    public boolean isSpecial() {
        return false;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public void cleanUp() {
    }
}