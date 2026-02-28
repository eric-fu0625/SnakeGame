package com.game.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages player data operations including loading/saving player info,
 * login/registration, high score tracking, and session management
 * (login/logout) for a game application.
 * Player data is stored in a text file (players.txt) with format: username
 * password highScore
 */
public class PlayerData {
    private static final String USER_FILE = "players.txt";
    private Map<String, Player> players;
    private Player currentPlayer;

    public static class Player {
        private String username;
        private String password;
        private int highScore;

        public Player(String username, String password) {
            this.username = username;
            this.password = password;
            this.highScore = 0;
        }

        public Player(String username, String password, int highScore) {
            this.username = username;
            this.password = password;
            this.highScore = highScore;
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public int getHighScore() {
            return highScore;
        }

        public void setHighScore(int highScore) {
            this.highScore = highScore;
        }
    }

    public PlayerData() {
        players = new HashMap<>();
        loadPlayers();
    }

    private void loadPlayers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            System.out.println("No player exists. Creating new file.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip empty lines or comment lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String username = parts[0];
                    String password = parts[1];
                    int highScore = 0;

                    // Parse high score if available
                    if (parts.length >= 3) {
                        try {
                            highScore = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            highScore = 0;
                        }
                    }

                    players.put(username, new Player(username, password, highScore));
                    count++;
                }
            }

            System.out.println("Successfully load " + count + " players");

        } catch (IOException e) {
            System.err.println("Loading player data failed: " + e.getMessage());
        }
    }

    public void savePlayers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {
            bw.write("# Player data");
            bw.newLine();
            bw.write("# Format: username password highScore");
            bw.newLine();

            for (Player player : players.values()) {
                bw.write(player.username + " " +
                        player.password + " " +
                        player.highScore);
                bw.newLine();
            }

            System.out.println("Player data saved.");

        } catch (IOException e) {
            System.err.println("Saving player data failed: " + e.getMessage());
        }
    }

    /**
     * Validates player login credentials (username + password).
     * Sets the currentPlayer if credentials are valid.
     * 
     * @param username Player's username
     * @param password Player's password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String username, String password) {
        Player player = players.get(username);
        if (player != null && player.password.equals(password)) {
            currentPlayer = player;
            System.out.println("Player login: " + username);
            return true;
        }
        return false;
    }

    /**
     * Registers a new player with validation rules for username/password length.
     * - Username: 3-10 characters
     * - Password: 4-10 characters
     * - Username must be unique (not already in the players map)
     * Creates a new Player, sets currentPlayer, and saves data to file.
     * 
     * @param username New player's username
     * @param password New player's password
     * @return true if registration is successful, false otherwise
     */
    public boolean register(String username, String password) {
        // Check if username already exists
        if (players.containsKey(username)) {
            return false;
        }

        // Validate username length (3-10 characters)
        if (username.length() < 3 || username.length() > 10) {
            return false;
        }

        // Validate password length (4-10 characters)
        if (password.length() < 4 || password.length() > 10) {
            return false;
        }

        Player newPlayer = new Player(username, password);
        players.put(username, newPlayer);
        currentPlayer = newPlayer;

        savePlayers();

        System.out.println("New player login: " + username);
        return true;
    }

    /**
     * Logs out the current player by setting currentPlayer to null.
     * Prints a logout message if a player was logged in.
     */
    public void logout() {
        if (currentPlayer != null) {
            System.out.println("Player logout: " + currentPlayer.username);
        }
        currentPlayer = null;
    }

    public void updateHighScore(int score) {
        if (currentPlayer != null && score > currentPlayer.highScore) {
            currentPlayer.highScore = score;
            savePlayers();
            System.out.println("New Highscore Recorded: " + currentPlayer.username + " = " + score);
        }
    }

    public boolean isNewRecord(int score) {
        return currentPlayer != null && score > currentPlayer.highScore;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentUsername() {
        return currentPlayer != null ? currentPlayer.username : "Guest";
    }

    public int getCurrentHighScore() {
        return currentPlayer != null ? currentPlayer.highScore : 0;
    }

    public boolean isLoggedIn() {
        return currentPlayer != null;
    }

    public Map<String, Player> getAllPlayers() {
        return new HashMap<>(players);
    }
}