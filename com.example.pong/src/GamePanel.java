import screen.*;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1400;
    public static final int HEIGHT = 1100;
    private static final int TARGET_FPS = 60;
    private static final double FRAME_TIME = 1_000_000_000.0 / TARGET_FPS;
    private static final int MAX_SCORE = 10;

    public static Sound paddleSound;
    public static Sound wallSound;
    public static Sound scoreSound;
    private final InputHandler input;
    private final Ball ball;
    private final double aiDecisionInterval = 0.15;
    private final double aiErrorRange = 30;

    private StartScreen startScreen;
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;
    private MatchballScreen matchballScreen;

    private final HUD hud;
    private Paddle leftPaddle, rightPaddle;
    private int leftScore = 0;
    private int rightScore = 0;
    private GameState gameState = GameState.START;
    private boolean pauseKeyHandled = false;
    private boolean running = true;
    private boolean plusHandled = false;
    private boolean minusHandled = false;
    private double gameTime = 0; // Seconds
    private boolean aiMode = true;
    private double aiTargetY = HEIGHT / 2.0;
    private double aiDecisionTimer = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        input = new InputHandler();
        setFocusable(true);
        addKeyListener(input);

        leftPaddle = new Paddle(50, HEIGHT / 2 - 50, 10, 100);
        rightPaddle = new Paddle(WIDTH - 60, HEIGHT / 2 - 50, 10, 100);
        ball = new Ball(WIDTH / 2, HEIGHT / 2, 20);

        startScreen = new StartScreen(WIDTH, HEIGHT);
        pauseScreen = new PauseScreen(WIDTH, HEIGHT);
        gameOverScreen = new GameOverScreen(WIDTH, HEIGHT);
        matchballScreen = new MatchballScreen(WIDTH, HEIGHT);
        hud = new HUD(WIDTH);

        paddleSound = new Sound("resources/hit.wav");
        wallSound = new Sound("resources/wall.wav");
        scoreSound = new Sound("resources/score.wav");
    }

    public void startGame() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            double elapsed = now - lastTime;
            lastTime = now;

            delta += elapsed / FRAME_TIME;

            // Only update on a full frame step
            while (delta >= 1) {
                update(1.0 / TARGET_FPS); // fixed Delta-Time!
                delta--;
            }

            repaint();

            // Sleep for VSync-like behavior
            long sleepTime = (long) (FRAME_TIME - (System.nanoTime() - now));

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update(double dt) {
        // Check exit key is pressed
        if (input.isXPressed()) {
            quitGame();
        }

        // START SCREEN
        if (gameState == GameState.START) {
            startScreen.update(dt);
            if (input.isOnePressed()) {
                aiMode = true;
                gameState = GameState.PLAYING;
            } else if (input.isTwoPressed()) {
                aiMode = false;
                gameState = GameState.PLAYING;
            }
            return;
        }

        // Check reset key is pressed
        if (input.isRPressed()) {
            startScreen.reset();
            resetGame();
            gameState = GameState.START;
        }

        // Check pause key is pressed
        if (input.isPPressed()) {
            if (!pauseKeyHandled) {
                togglePause();
                pauseKeyHandled = true;
                pauseScreen.update(dt);
                return;
            }
        } else {
            pauseKeyHandled = false;
        }

        // GAME OVER SCREEN
        if (gameState == GameState.GAME_OVER) {
            gameOverScreen.update(dt);
            if (input.isSpacePressed()) {
                restart();
            }
            return;
        }

        // Stop updates while paused
        if (gameState == GameState.PAUSED) {
            pauseScreen.update(dt);
            return;
        }

        if (gameState == GameState.PLAYING) {
            gameTime += dt;
        }

        matchballScreen.update(dt);

        if (input.isPlusPressed()) {
            if (!plusHandled) {
                ball.changeSpeed(50);
                plusHandled = true;
            }
        } else {
            plusHandled = false;
        }

        if (input.isMinusPressed()) {
            if (!minusHandled) {
                ball.changeSpeed(-50);
                minusHandled = true;
            }
        } else {
            minusHandled = false;
        }

        if (aiMode) {
            updateAiPaddle(dt);
        } else {
            leftPaddle.update(input.isWPressed(), input.isSPressed(), HEIGHT, dt);
        }
        rightPaddle.update(input.isUpPressed(), input.isDownPressed(), HEIGHT, dt);
        ball.update(dt, leftPaddle, rightPaddle);

        // Scoring
        if (ball.getX() <= 0) {
            rightScore++;
            scoreSound.play();
            checkMatchball();
            checkGameOver();
            ball.reset(WIDTH / 2, HEIGHT / 2);
        }
        if (ball.getX() >= WIDTH) {
            leftScore++;
            scoreSound.play();
            checkMatchball();
            checkGameOver();
            ball.reset(WIDTH / 2, HEIGHT / 2);
        }
    }

    private void quitGame() {
        running = false;
        System.exit(0);
    }

    private void resetGame() {
        leftScore = 0;
        rightScore = 0;
        gameTime = 0;
        matchballScreen.reset();
        ball.reset(WIDTH / 2, HEIGHT / 2);
        leftPaddle = new Paddle(50, HEIGHT / 2 - 50, 10, 100);
        rightPaddle = new Paddle(WIDTH - 60, HEIGHT / 2 - 50, 10, 100);
        gameState = GameState.PLAYING;
    }

    private void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) gameState = GameState.PLAYING;
    }

    private void restart() {
        leftScore = 0;
        rightScore = 0;
        gameTime = 0;
        matchballScreen.reset();
        aiDecisionTimer = 0;
        aiTargetY = HEIGHT / 2.0;
        ball.reset(WIDTH / 2, HEIGHT / 2);
        gameState = GameState.PLAYING;
    }

    private void updateAiPaddle(double dt) {
        aiDecisionTimer -= dt;

        if (aiDecisionTimer <= 0) {
            aiDecisionTimer = aiDecisionInterval;

            if (ball.getVx() < 0) {
                aiTargetY = ball.getY() - leftPaddle.getHeight() / 2.0
                        + (Math.random() * 2 - 1) * aiErrorRange;
            } else {
                aiTargetY = HEIGHT / 2.0 - leftPaddle.getHeight() / 2.0;
            }
        }

        double paddleCenter = leftPaddle.getY() + leftPaddle.getHeight() / 2.0;
        double targetCenter = aiTargetY + leftPaddle.getHeight() / 2.0;
        double deadZone = 20;

        boolean moveUp = paddleCenter > targetCenter + deadZone;
        boolean moveDown = paddleCenter < targetCenter - deadZone;

        leftPaddle.update(moveUp, moveDown, HEIGHT, dt);
    }

    private void checkGameOver() {
        if (leftScore >= MAX_SCORE || rightScore >= MAX_SCORE) gameState = GameState.GAME_OVER;
    }

    private void checkMatchball() {
        if (leftScore == MAX_SCORE - 1 || rightScore == MAX_SCORE - 1) {
            matchballScreen.show();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        if (gameState == GameState.START) {
            startScreen.draw(g);
            return;
        }

        for (int y = 0; y < HEIGHT; y += 30) {
            g.fillRect(WIDTH / 2 - 2, y, 4, 20);
        }

        leftPaddle.draw(g);
        rightPaddle.draw(g);
        ball.draw(g);

        int speed = (int) Math.sqrt(ball.getVx() * ball.getVx() + ball.getVy() * ball.getVy());
        hud.setData(leftScore, rightScore, speed, gameTime);
        hud.draw(g);

        if (gameState == GameState.PAUSED) {
            pauseScreen.draw(g);
        }

        if (gameState == GameState.GAME_OVER) {
            String winner = leftScore > rightScore ? "Left Player Wins!" : "Right Player Wins!";
            gameOverScreen.setWinner(winner);
            gameOverScreen.draw(g);
        }

        if (gameState == GameState.PLAYING && matchballScreen.isVisible()) {
            matchballScreen.draw(g);
        }
    }

    private enum GameState {PLAYING, PAUSED, GAME_OVER, START}
}
