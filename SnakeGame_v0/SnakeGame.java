import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    // Game constants
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int UNIT_SIZE = 20;
    private static final int MIN_DELAY = 50;
    private static final int DELAY = 200;
    private static final int INITIAL_SNAKE_LENGTH = 3;

    // Game state
    private enum GameState {
        RUNNING, PAUSED, GAME_OVER
    }

    private GameState gameState = GameState.GAME_OVER;

    // Game objects
    private Timer gameTimer;
    private Random random = new Random();

    private final LinkedList<Point> snake = new LinkedList<>();
    private Direction direction = Direction.RIGHT;

    private Point food;
    private int score = 0;
    private int highScore = 0;

    // Directions enum
    private enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public boolean isOpposite(Direction other) {
            return (this == UP && other == DOWN) ||
                    (this == DOWN && other == UP) ||
                    (this == LEFT && other == RIGHT) ||
                    (this == RIGHT && other == LEFT);
        }
    }

    public SnakeGame() {
        initializePanel();
        initializeGame();
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(new GameKeyListener());
    }

    private void initializeGame() {
        // Initial snake position
        int startX = (WIDTH / (2 * UNIT_SIZE)) * UNIT_SIZE + UNIT_SIZE;
        int startY = (HEIGHT / (2 * UNIT_SIZE)) * UNIT_SIZE;

        snake.clear();
        for (int i = 0; i < INITIAL_SNAKE_LENGTH; i++) {
            snake.add(new Point(startX - i * UNIT_SIZE, startY));
        }

        direction = Direction.RIGHT;
        score = 0;
        generateFood();
        startGame();
    }

    private void startGame() {
        gameState = GameState.RUNNING;
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
        requestFocusInWindow();
    }

    private void generateFood() {
        int maxX = WIDTH / UNIT_SIZE;
        int maxY = HEIGHT / UNIT_SIZE;

        // Try generating food up to 100 times to avoid infinite loop
        for (int attempt = 0; attempt < 100; attempt++) {
            int x = random.nextInt(maxX) * UNIT_SIZE;
            int y = random.nextInt(maxY) * UNIT_SIZE;
            food = new Point(x, y);

            if (!snake.contains(food)) {
                return;
            }
        }

        // Fallback: place food at first available position
        for (int y = 0; y < HEIGHT; y += UNIT_SIZE) {
            for (int x = 0; x < WIDTH; x += UNIT_SIZE) {
                Point point = new Point(x, y);
                if (!snake.contains(point)) {
                    food = point;
                    return;
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        switch (gameState) {
            case RUNNING:
            case PAUSED:
                drawGameElements(g);
                drawUI(g);
                if (gameState == GameState.PAUSED) {
                    drawPauseOverlay(g);
                }
                break;
            case GAME_OVER:
                drawGameOverScreen(g);
                break;
        }
    }

    private void drawGameElements(Graphics g) {
        // Draw food with shine effect
        g.setColor(Color.RED);
        g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);
        g.setColor(new Color(255, 200, 200));
        g.fillOval(food.x + UNIT_SIZE / 4, food.y + UNIT_SIZE / 4, UNIT_SIZE / 4, UNIT_SIZE / 4);

        // Draw snake
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);

            // Head is brighter green
            if (i == 0) {
                g.setColor(new Color(100, 255, 100));
                g.fillRoundRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE, 5, 5);

                // Draw eyes on head
                g.setColor(Color.BLACK);
                int eyeSize = UNIT_SIZE / 5;
                switch (direction) {
                    case RIGHT:
                        g.fillOval(segment.x + UNIT_SIZE - eyeSize * 2, segment.y + eyeSize * 2, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeSize * 2, segment.y + UNIT_SIZE - eyeSize * 3, eyeSize,
                                eyeSize);
                        break;
                    case LEFT:
                        g.fillOval(segment.x + eyeSize, segment.y + eyeSize * 2, eyeSize, eyeSize);
                        g.fillOval(segment.x + eyeSize, segment.y + UNIT_SIZE - eyeSize * 3, eyeSize, eyeSize);
                        break;
                    case UP:
                        g.fillOval(segment.x + eyeSize * 2, segment.y + eyeSize, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeSize * 3, segment.y + eyeSize, eyeSize, eyeSize);
                        break;
                    case DOWN:
                        g.fillOval(segment.x + eyeSize * 2, segment.y + UNIT_SIZE - eyeSize * 2, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeSize * 3, segment.y + UNIT_SIZE - eyeSize * 2, eyeSize,
                                eyeSize);
                        break;
                }
            } else {
                // Body segments with gradient
                float ratio = (float) i / snake.size();
                int green = (int) (45 + 100 * ratio);
                g.setColor(new Color(0, green, 0));
                g.fillRoundRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE, 5, 5);
            }

            // Segment border
            g.setColor(Color.DARK_GRAY);
            g.drawRoundRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE, 5, 5);
        }
    }

    private void drawUI(Graphics g) {
        // Draw grid (optional, can be disabled for better performance)
        g.setColor(new Color(30, 30, 30));
        for (int x = 0; x < WIDTH; x += UNIT_SIZE) {
            g.drawLine(x, 0, x, HEIGHT);
        }
        for (int y = 0; y < HEIGHT; y += UNIT_SIZE) {
            g.drawLine(0, y, WIDTH, y);
        }

        // Draw score panel
        g.setColor(new Color(50, 50, 50, 200));
        g.fillRect(5, 5, 200, 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 15, 30);
        g.drawString("High Score: " + highScore, 15, 55);

        // Draw controls hint
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("ESC: Pause | SPACE: Restart", WIDTH - 200, 25);
        g.drawString("WASD or Arrows: Move", WIDTH - 200, 45);
    }

    private void drawPauseOverlay(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Pause text
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String pauseText = "PAUSED";
        g.drawString(pauseText,
                (WIDTH - metrics.stringWidth(pauseText)) / 2,
                HEIGHT / 2 - 50);

        // Instructions
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        metrics = getFontMetrics(g.getFont());
        String continueText = "Press ESC to continue";
        g.drawString(continueText,
                (WIDTH - metrics.stringWidth(continueText)) / 2,
                HEIGHT / 2 + 20);
    }

    private void drawGameOverScreen(Graphics g) {
        // Dark overlay
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER";
        g.drawString(gameOverText,
                (WIDTH - metrics.stringWidth(gameOverText)) / 2,
                HEIGHT / 2 - 80);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        String scoreText = "Final Score: " + score;
        g.drawString(scoreText,
                (WIDTH - metrics.stringWidth(scoreText)) / 2,
                HEIGHT / 2 - 10);

        // High Score if beaten
        if (score > highScore) {
            g.setColor(Color.YELLOW);
            String newHighScore = "NEW HIGH SCORE!";
            metrics = getFontMetrics(g.getFont());
            g.drawString(newHighScore,
                    (WIDTH - metrics.stringWidth(newHighScore)) / 2,
                    HEIGHT / 2 + 30);
            highScore = score;
        }

        // Restart instructions
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        metrics = getFontMetrics(g.getFont());
        String restartText = "Press SPACE to restart";
        g.drawString(restartText,
                (WIDTH - metrics.stringWidth(restartText)) / 2,
                HEIGHT / 2 + 80);
    }

    private void updateGame() {
        if (gameState != GameState.RUNNING) {
            return;
        }

        // Get current head position
        Point head = snake.getFirst();

        // Calculate new head position based on direction
        Point newHead = new Point(head);
        switch (direction) {
            case UP:
                newHead.y -= UNIT_SIZE;
                break;
            case DOWN:
                newHead.y += UNIT_SIZE;
                break;
            case LEFT:
                newHead.x -= UNIT_SIZE;
                break;
            case RIGHT:
                newHead.x += UNIT_SIZE;
                break;
        }

        // Wrap around screen edges
        if (newHead.x < 0)
            newHead.x = WIDTH - UNIT_SIZE;
        if (newHead.x >= WIDTH)
            newHead.x = 0;
        if (newHead.y < 0)
            newHead.y = HEIGHT - UNIT_SIZE;
        if (newHead.y >= HEIGHT)
            newHead.y = 0;

        // Check for collisions with self
        for (Point segment : snake) {
            if (segment.equals(newHead)) {
                gameState = GameState.GAME_OVER;
                gameTimer.stop();
                return;
            }
        }

        // Add new head
        snake.addFirst(newHead);

        // Check if food is eaten
        if (newHead.equals(food)) {
            score += 10;
            generateFood();

            // Speed up game every 50 points
            if (score % 50 == 0 && gameTimer.getDelay() > 50) {
                gameTimer.setDelay(Math.max(MIN_DELAY, gameTimer.getDelay() - 10));
            }
        } else {
            // Remove tail if no food was eaten
            snake.removeLast();
        }
    }

    private void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
            gameTimer.stop();
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
            gameTimer.start();
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState == GameState.GAME_OVER && e.getKeyCode() == KeyEvent.VK_SPACE) {
                initializeGame();
                return;
            }

            switch (e.getKeyCode()) {
                // Movement controls
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (!direction.isOpposite(Direction.LEFT)) {
                        direction = Direction.LEFT;
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (!direction.isOpposite(Direction.RIGHT)) {
                        direction = Direction.RIGHT;
                    }
                    break;

                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (!direction.isOpposite(Direction.UP)) {
                        direction = Direction.UP;
                    }
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (!direction.isOpposite(Direction.DOWN)) {
                        direction = Direction.DOWN;
                    }
                    break;

                // Game controls
                case KeyEvent.VK_ESCAPE:
                    if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
                        togglePause();
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
                        togglePause();
                    }
                    break;

                case KeyEvent.VK_R:
                    initializeGame();
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SnakeGame game = new SnakeGame();
            frame.add(game);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Request focus for immediate keyboard input
            game.requestFocusInWindow();
        });
    }
}