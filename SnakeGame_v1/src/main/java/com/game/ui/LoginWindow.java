package com.game.ui;

import com.game.data.PlayerData;
import com.game.utils.UITheme;

import javax.swing.*;
import java.awt.*;

public class LoginWindow {
    private JFrame frame;
    private PlayerData playerData;
    private Runnable onLoginSuccess;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public LoginWindow(PlayerData playerData, Runnable onLoginSuccess) {
        this.playerData = playerData;
        this.onLoginSuccess = onLoginSuccess;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Snake Game - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(UITheme.COLOR_BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(UITheme.withAlpha(UITheme.COLOR_BACKGROUND, 240));

        // Title
        JLabel titleLabel = new JLabel("Snake Game", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.COLOR_ACCENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Tab buttons
        JPanel tabPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        tabPanel.setBackground(UITheme.COLOR_SCORE_PANEL);
        tabPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton loginTab = new JButton("Login");
        JButton registerTab = new JButton("Register");

        // 创建卡片面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.COLOR_BACKGROUND);
        cardPanel.add(createLoginPanel(), "login");
        cardPanel.add(createRegisterPanel(), "register");

        // 设置初始状态
        styleTabButton(loginTab, true);
        styleTabButton(registerTab, false);
        cardLayout.show(cardPanel, "login");

        tabPanel.add(loginTab);
        tabPanel.add(registerTab);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabPanel, BorderLayout.CENTER);
        mainPanel.add(cardPanel, BorderLayout.SOUTH);

        // Tab switching
        loginTab.addActionListener(e -> {
            cardLayout.show(cardPanel, "login");
            styleTabButton(loginTab, true);
            styleTabButton(registerTab, false);
            frame.revalidate();
            frame.repaint();
        });

        registerTab.addActionListener(e -> {
            cardLayout.show(cardPanel, "register");
            styleTabButton(loginTab, false);
            styleTabButton(registerTab, true);
            frame.revalidate();
            frame.repaint();
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void styleTabButton(JButton button, boolean active) {
        button.setFont(UITheme.FONT_BUTTON_BOLD);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        if (active) {
            button.setBackground(UITheme.COLOR_ACCENT);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.COLOR_ACCENT.darker(), 2),
                    BorderFactory.createEmptyBorder(10, 23, 10, 23)));
        } else {
            button.setBackground(UITheme.COLOR_SCORE_PANEL);
            button.setForeground(UITheme.COLOR_TEXT);
            button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        }
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(UITheme.FONT_BUTTON);
        userLabel.setForeground(UITheme.COLOR_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        userField.setFont(UITheme.FONT_BUTTON);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_BUTTON);
        passLabel.setForeground(UITheme.COLOR_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        passField.setFont(UITheme.FONT_BUTTON);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        styleActionButton(loginButton, UITheme.COLOR_ACCENT);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginButton, gbc);

        // Login action
        loginButton.addActionListener(e -> handleLogin(
                userField.getText().trim(),
                new String(passField.getPassword()).trim()));

        // Enter key support
        passField.addActionListener(e -> loginButton.doClick());

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // New username
        JLabel userLabel = new JLabel("New Username:");
        userLabel.setFont(UITheme.FONT_BUTTON);
        userLabel.setForeground(UITheme.COLOR_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        userField.setFont(UITheme.FONT_BUTTON);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_BUTTON);
        passLabel.setForeground(UITheme.COLOR_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        passField.setFont(UITheme.FONT_BUTTON);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passField, gbc);

        // Confirm password
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setFont(UITheme.FONT_BUTTON);
        confirmLabel.setForeground(UITheme.COLOR_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(confirmLabel, gbc);

        JPasswordField confirmField = new JPasswordField(15);
        confirmField.setFont(UITheme.FONT_BUTTON);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(confirmField, gbc);

        // Register button
        JButton registerButton = new JButton("Register");
        styleActionButton(registerButton, new Color(76, 175, 80)); // Green color for register

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(registerButton, gbc);

        // Register action
        registerButton.addActionListener(e -> handleRegister(
                userField.getText().trim(),
                new String(passField.getPassword()).trim(),
                new String(confirmField.getPassword()).trim()));

        confirmField.addActionListener(e -> registerButton.doClick());

        return panel;
    }

    private void styleActionButton(JButton button, Color bgColor) {
        button.setFont(UITheme.FONT_BUTTON_BOLD);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (playerData.login(username, password)) {
            showMessage("Welcome Back!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            frame.dispose();
            onLoginSuccess.run();
        } else {
            showMessage("Invalid username or password!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister(String username, String password, String confirmPassword) {
        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 3 || username.length() > 10) {
            showMessage("Username must be 3-10 characters!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 4 || password.length() > 10) {
            showMessage("Password must be 4-10 characters!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registration
        if (playerData.register(username, password)) {
            showMessage("Welcome!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Auto login
            if (playerData.login(username, password)) {
                frame.dispose();
                onLoginSuccess.run();
            }
        } else {
            showMessage("Username already exists!",
                    "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        String styledMessage = String.format(
                "<html><body style='font-family: %s; font-size: 12px;'>%s</body></html>",
                UITheme.FONT_FAMILY, message);

        JOptionPane.showMessageDialog(frame,
                styledMessage,
                title,
                messageType);
    }

    public void show() {
        frame.setVisible(true);
    }
}