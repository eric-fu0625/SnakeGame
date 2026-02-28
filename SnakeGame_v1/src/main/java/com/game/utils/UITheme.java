package com.game.utils;

import java.awt.*;

public class UITheme {

    // ===== Color constants =====
    public static final Color COLOR_BACKGROUND = Color.BLACK;
    public static final Color COLOR_TEXT = Color.WHITE;
    public static final Color COLOR_ACCENT = new Color(100, 255, 100);
    public static final Color COLOR_WARNING = Color.RED;
    public static final Color COLOR_HIGHLIGHT = Color.YELLOW;
    public static final Color COLOR_INFO = Color.CYAN;

    // UI colors
    public static final Color COLOR_GRID = new Color(30, 30, 30, 100);
    public static final Color COLOR_SCORE_PANEL = new Color(50, 50, 50, 200);
    public static final Color COLOR_HINT = new Color(200, 200, 200, 180);
    public static final Color COLOR_OVERLAY = new Color(0, 0, 0, 180);
    public static final Color COLOR_OVERLAY_DARK = new Color(0, 0, 0, 220);

    // Snake colors
    public static final Color COLOR_SNAKE_HEAD = new Color(100, 255, 100);
    public static final Color COLOR_SNAKE_BODY_BORDER = Color.DARK_GRAY;
    public static final Color COLOR_SNAKE_EYES = Color.BLACK;

    // Food colors
    public static final Color COLOR_FOOD = Color.ORANGE;
    public static final Color COLOR_SPECIAL_FOOD = Color.RED;
    public static final Color COLOR_FOOD_HIGHLIGHT = Color.YELLOW;
    public static final Color COLOR_SpecialFOOD_HIGHLIGHT = Color.ORANGE;

    // State colors
    public static final Color COLOR_GRID_OFF_HINT = new Color(255, 255, 255, 150);

    // ===== Font constant =====

    public static final String FONT_FAMILY = "Times New Roman";

    public static final Font FONT_SCORE = new Font(FONT_FAMILY, Font.BOLD, 20);

    public static final Font FONT_CONTROL_HINT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SHORTCUT_HINT = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_GRID_STATUS = new Font(FONT_FAMILY, Font.ITALIC, 12);

    public static final Font FONT_PAUSE = new Font(FONT_FAMILY, Font.BOLD, 50);
    public static final Font FONT_CONTINUE = new Font(FONT_FAMILY, Font.PLAIN, 20);
    public static final Font FONT_GRID_STATUS_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 16);

    public static final Font FONT_GAME_OVER = new Font(FONT_FAMILY, Font.BOLD, 60);
    public static final Font FONT_FINAL_SCORE = new Font(FONT_FAMILY, Font.BOLD, 30);
    public static final Font FONT_RESTART_HINT = new Font(FONT_FAMILY, Font.PLAIN, 24);

    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BUTTON_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);

    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 32);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 18);

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color createSnakeBodyColor(int index, int total) {
        float ratio = (float) index / total;
        int green = (int) (45 + 100 * ratio);
        return new Color(0, green, 0);
    }

    public static Font createFont(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static Font getGameFont() {
        return new Font(FONT_FAMILY, Font.PLAIN, 14);
    }
}
