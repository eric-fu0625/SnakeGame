package com.game.logic;

import com.game.utils.Direction;
import com.game.utils.UITheme;

import java.awt.*;
import java.util.LinkedList;

public class Snake {
    private LinkedList<Point> body;
    private Direction direction;
    private int unitSize;

    public Snake(int startX, int startY, int unitSize, int initialLength) {
        this.unitSize = unitSize;
        this.direction = Direction.RIGHT;
        this.body = new LinkedList<>();

        for (int i = 0; i < initialLength; i++) {
            body.add(new Point(startX - i * unitSize, startY));
        }
    }

    public void move() {
        Point head = body.getFirst();

        Point newHead = new Point(head);
        switch (direction) {
            case UP:
                newHead.y -= unitSize;
                break;
            case DOWN:
                newHead.y += unitSize;
                break;
            case LEFT:
                newHead.x -= unitSize;
                break;
            case RIGHT:
                newHead.x += unitSize;
                break;
        }

        body.addFirst(newHead);
    }

    public void grow() {
    }

    public void removeTail() {
        if (body.size() > 0) {
            body.removeLast();
        }
    }

    public boolean checkSelfCollision() {
        if (body.size() < 2)
            return false;

        Point head = body.getFirst();
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Point point) {
        return body.contains(point);
    }

    public void reset(int startX, int startY, int initialLength) {
        body.clear();
        direction = Direction.RIGHT;

        for (int i = 0; i < initialLength; i++) {
            body.add(new Point(startX - i * unitSize, startY));
        }
    }

    public void wrapAround(int width, int height) {
        Point head = body.getFirst();
        if (head.x < 0)
            head.x = width - unitSize;
        if (head.x >= width)
            head.x = 0;
        if (head.y < 0)
            head.y = height - unitSize;
        if (head.y >= height)
            head.y = 0;

        body.set(0, head);
    }

    public void draw(Graphics g, int unitSize) {
        if (body == null || body.isEmpty())
            return;

        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);

            if (i == 0) {
                drawHead(g, segment, unitSize);
            } else {
                drawBodySegment(g, segment, i, unitSize);
            }

            g.setColor(UITheme.COLOR_SNAKE_BODY_BORDER);
            g.drawRoundRect(segment.x, segment.y, unitSize, unitSize, 5, 5);
        }
    }

    private void drawHead(Graphics g, Point head, int unitSize) {
        g.setColor(UITheme.COLOR_SNAKE_HEAD);
        g.fillRoundRect(head.x, head.y, unitSize, unitSize, 5, 5);
        drawEyes(g, head, unitSize);
    }

    private void drawBodySegment(Graphics g, Point segment, int index, int unitSize) {
        g.setColor(UITheme.createSnakeBodyColor(index, body.size()));
        g.fillRoundRect(segment.x, segment.y, unitSize, unitSize, 5, 5);
    }

    private void drawEyes(Graphics g, Point head, int unitSize) {
        g.setColor(UITheme.COLOR_SNAKE_EYES);
        int eyeSize = unitSize / 5;

        switch (direction) {
            case RIGHT:
                g.fillOval(head.x + unitSize - eyeSize * 2, head.y + eyeSize * 2, eyeSize, eyeSize);
                g.fillOval(head.x + unitSize - eyeSize * 2, head.y + unitSize - eyeSize * 3, eyeSize, eyeSize);
                break;
            case LEFT:
                g.fillOval(head.x + eyeSize, head.y + eyeSize * 2, eyeSize, eyeSize);
                g.fillOval(head.x + eyeSize, head.y + unitSize - eyeSize * 3, eyeSize, eyeSize);
                break;
            case UP:
                g.fillOval(head.x + eyeSize * 2, head.y + eyeSize, eyeSize, eyeSize);
                g.fillOval(head.x + unitSize - eyeSize * 3, head.y + eyeSize, eyeSize, eyeSize);
                break;
            case DOWN:
                g.fillOval(head.x + eyeSize * 2, head.y + unitSize - eyeSize * 2, eyeSize, eyeSize);
                g.fillOval(head.x + unitSize - eyeSize * 3, head.y + unitSize - eyeSize * 2, eyeSize, eyeSize);
                break;
        }
    }

    // Getters and Setters
    public LinkedList<Point> getBody() {
        return body;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (!this.direction.isOpposite(direction)) {
            this.direction = direction;
        }
    }

    public int getLength() {
        return body.size();
    }
}