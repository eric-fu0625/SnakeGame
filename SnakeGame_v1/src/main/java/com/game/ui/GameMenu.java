package com.game.ui;

import com.game.utils.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class GameMenu extends JMenuBar {
    private JFrame parentFrame;
    private GamePanel gamePanel;
    private Runnable onLogout;

    public GameMenu(JFrame parentFrame, GamePanel gamePanel, Runnable onLogout) {
        this.parentFrame = parentFrame;
        this.gamePanel = gamePanel;
        this.onLogout = onLogout;

        initMenu();
    }

    private void initMenu() {
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setFont(UITheme.FONT_BUTTON);

        JMenuItem newGameItem = new JMenuItem("Restart");
        newGameItem.setFont(UITheme.FONT_BUTTON);
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        newGameItem.addActionListener(e -> {
            if (gamePanel != null) {
                gamePanel.restartGame();
            }
        });

        JMenuItem pauseItem = new JMenuItem("Pause / Continue");
        pauseItem.setFont(UITheme.FONT_BUTTON);
        pauseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        pauseItem.addActionListener(e -> {
            if (gamePanel != null) {
                gamePanel.togglePause();
            }
        });

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(UITheme.FONT_BUTTON);
        logoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        logoutItem.addActionListener(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(UITheme.FONT_BUTTON);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(pauseItem);
        gameMenu.addSeparator();
        gameMenu.add(logoutItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(UITheme.FONT_BUTTON);

        JMenu speedMenu = new JMenu("Game Speed");
        speedMenu.setFont(UITheme.FONT_BUTTON);

        String[] speeds = { "Slow", "Medium", "Fast", "Lightning" };
        int[] delays = { 300, 200, 100, 50 };

        for (int i = 0; i < speeds.length; i++) {
            JMenuItem speedItem = new JMenuItem(speeds[i]);
            speedItem.setFont(UITheme.FONT_BUTTON);
            final int delay = delays[i];
            speedItem.addActionListener(e -> {
                if (gamePanel != null) {
                    gamePanel.setGameSpeed(delay);
                }
            });
            speedMenu.add(speedItem);
        }

        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Grid", true);
        gridItem.setFont(UITheme.FONT_BUTTON);
        gridItem.addActionListener(e -> {
            if (gamePanel != null) {
                boolean showGrid = gridItem.isSelected();
                gamePanel.setGridVisible(showGrid);
            }
        });

        settingsMenu.add(speedMenu);
        settingsMenu.addSeparator();
        settingsMenu.add(gridItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(UITheme.FONT_BUTTON);

        JMenuItem controlsItem = new JMenuItem("Controls");
        controlsItem.setFont(UITheme.FONT_BUTTON);
        controlsItem.addActionListener(e -> showControlsHelp());

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setFont(UITheme.FONT_BUTTON);
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(controlsItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        add(gameMenu);
        add(settingsMenu);
        add(helpMenu);
    }

    private void showControlsHelp() {
        String helpText = "<html><body style='width: 320px; font-family: " + UITheme.FONT_FAMILY + ";'>" +
                "<h3 style='color: " + toHex(Color.BLACK) + ";'>Game Controls</h3>" +
                "<p><b style='color: " + toHex(Color.BLACK) + ";'>Movement:</b></p>" +
                "• Arrow Keys: ↑ ↓ ← →<br>" +
                "• WASD Keys: W A S D<br><br>" +

                "<p><b style='color: " + toHex(Color.BLACK) + ";'>Game Control:</b></p>" +
                "• SPACE: Pause/Resume Game<br>" +
                "• ESC: Logout / Exit Menu<br>" +
                "• SPACE (Game Over): Restart<br>" +
                "• G: Toggle Grid Display<br><br>" +

                "<p><b style='color: " + toHex(Color.BLACK) + ";'>Shortcuts:</b></p>" +
                "• F2: Restart Game<br>" +
                "• 1-4: Change Game Speed (1=Slow, 4=Fast)<br>" +
                "• Alt+F4: Exit Game<br>" +
                "</body></html>";

        JOptionPane.showMessageDialog(parentFrame,
                helpText,
                "Game Controls",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String aboutText = "<html><body style='width: 350px; font-family: " + UITheme.FONT_FAMILY + ";'>" +
                "<h3 style='color: " + toHex(Color.BLACK) + ";'>Snake Game</h3>" +
                "<p><b>Version:</b> 1.0.0</p>" +
                "<p><b>Author:</b> FDY</p>" +
                "<p><b>Description:</b> Classic Snake game with modern features</p>" +
                "<p><b>Features:</b></p>" +
                "<ul>" +
                "<li>User login/registration system</li>" +
                "<li>Automatic score saving</li>" +
                "<li>Multiple game speeds</li>" +
                "<li>Customizable grid display</li>" +
                "<li>Beautiful visual effects</li>" +
                "</ul>" +
                "<p style='color: " + toHex(UITheme.COLOR_HINT) + "; font-size: 12px;'>" +
                "Enjoy the game! 🐍" +
                "</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(parentFrame,
                aboutText,
                "About Snake Game",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }
}