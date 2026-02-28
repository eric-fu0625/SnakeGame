package com.game.main;

import com.game.data.PlayerData;
import com.game.ui.LoginWindow;
import com.game.ui.GameWindow;

public class SnakeGame {
  private static PlayerData playerData;
  private static LoginWindow loginWindow;
  private static GameWindow gameWindow;

  public static void main(String[] args) {
    playerData = new PlayerData();
    showLoginWindow();
  }

  private static void showLoginWindow() {
    if (gameWindow != null) {
      gameWindow = null;
    }

    loginWindow = new LoginWindow(playerData, () -> {
      showGameWindow();
    });
  }

  private static void showGameWindow() {
    if (loginWindow != null) {
      loginWindow = null;
    }

    gameWindow = new GameWindow(playerData, () -> {
      showLoginWindow();
    });
  }
}