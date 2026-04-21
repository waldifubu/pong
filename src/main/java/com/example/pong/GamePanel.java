package com.example.pong;

import com.example.pong.screen.*;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    private static final int TARGET_FPS = 60;
    private static final double FRAME_TIME = 1_000_000_000.0 / TARGET_FPS;
    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int LEFT_PADDLE_X = 50;
    private static final int BALL_SPEED_STEP = 50;
    private static final String HIT_SOUND_RESOURCE = "/hit.wav";
    private static final String WALL_SOUND_RESOURCE = "/wall.wav";
    private static final String SCORE_SOUND_RESOURCE = "/score.wav";
    private static final String BACKGROUND_MUSIC_RESOURCE = "/theme.mp3";

    public static Sound paddleSound;
    public static Sound wallSound;
    public static Sound scoreSound;
    private final GameConfig config;
    private final int panelWidth;
    private final int panelHeight;
    private final int rightPaddleX;
    private final int paddleStartY;
    private final int ballStartX;
    private final int ballStartY;
    private final AiSettings aiSettings;
    private final MatchState matchState;
    private final InputHandler input;
    private final Ball ball;
    private final BackgroundMusicPlayer backgroundMusic;

    private final StartScreen startScreen;
    private final CountdownScreen countdownScreen;
    private final PauseScreen pauseScreen;
    private final GameOverScreen gameOverScreen;
    private final MatchballScreen matchballScreen;

    private final HUD hud;
    private Paddle leftPaddle, rightPaddle;
    private GameState gameState = GameState.START;
    private boolean pauseKeyHandled = false;
    private boolean musicToggleHandled = false;
    private boolean volumeDownHandled = false;
    private boolean volumeUpHandled = false;
    private boolean running = true;
    private boolean plusHandled = false;
    private boolean minusHandled = false;
    private boolean aiMode = true;
    private double aiTargetY;
    private double aiDecisionTimer = 0;
    private Thread gameThread;

    public GamePanel() {
        config = GameConfig.loadDefault();
        panelWidth = config.getWidth();
        panelHeight = config.getHeight();
        rightPaddleX = panelWidth - 60;
        paddleStartY = panelHeight / 2 - PADDLE_HEIGHT / 2;
        ballStartX = panelWidth / 2;
        ballStartY = panelHeight / 2;
        aiTargetY = panelHeight / 2.0;
        aiSettings = AiSettings.fromStrength(config.getAiStrength());

        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(Color.BLACK);

        matchState = new MatchState(
                config.getMaxScore(),
                config.getLeftPlayerName(),
                config.getRightPlayerName()
        );

        input = new InputHandler();
        setFocusable(true);
        addKeyListener(input);

        leftPaddle = createLeftPaddle();
        rightPaddle = createRightPaddle();
        ball = new Ball(ballStartX, ballStartY, BALL_SIZE, panelWidth, panelHeight);

        startScreen = new StartScreen(panelWidth, panelHeight);
        countdownScreen = new CountdownScreen(panelWidth, panelHeight);
        pauseScreen = new PauseScreen(panelWidth, panelHeight);
        gameOverScreen = new GameOverScreen(panelWidth, panelHeight);
        matchballScreen = new MatchballScreen(panelWidth, panelHeight);
        hud = new HUD(panelWidth);

        initializeSounds();
        backgroundMusic = initializeBackgroundMusic();
    }

    public synchronized void startGame() {
        if (gameThread != null && gameThread.isAlive()) {
            return;
        }

        gameThread = new Thread(this, "pong-game-loop");
        gameThread.start();

        if (backgroundMusic != null) {
            backgroundMusic.playLoop();
        }
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
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }
    }

    private void update(double dt) {
        // Check exit key is pressed
        if (input.isXPressed()) {
            quitGame();
        }

        if (input.isMPressed()) {
            if (!musicToggleHandled) {
                toggleBackgroundMusicMute();
                musicToggleHandled = true;
            }
        } else {
            musicToggleHandled = false;
        }

        if (input.isSixPressed()) {
            if (!volumeDownHandled) {
                decreaseBackgroundMusicVolume();
                volumeDownHandled = true;
            }
        } else {
            volumeDownHandled = false;
        }

        if (input.isSevenPressed()) {
            if (!volumeUpHandled) {
                increaseBackgroundMusicVolume();
                volumeUpHandled = true;
            }
        } else {
            volumeUpHandled = false;
        }

        // START SCREEN
        if (gameState == GameState.START) {
            startScreen.update(dt);
            if (input.isOnePressed()) {
                aiMode = true;
                beginCountdown();
            } else if (input.isTwoPressed()) {
                aiMode = false;
                beginCountdown();
            }
            return;
        }

        // Check reset key is pressed
        if (input.isRPressed()) {
            startScreen.reset();
            resetMatchState();
            resetPaddles();
            countdownScreen.reset();
            gameState = GameState.START;
        }

        if (gameState == GameState.COUNTDOWN) {
            countdownScreen.update(dt);
            if (countdownScreen.isFinished()) {
                beginMatch();
            }
            return;
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
            matchState.advanceTime(dt);
        }

        matchballScreen.update(dt);

        plusHandled = handleSpeedChange(input.isPlusPressed(), plusHandled, BALL_SPEED_STEP);
        minusHandled = handleSpeedChange(input.isMinusPressed(), minusHandled, -BALL_SPEED_STEP);

        if (aiMode) {
            updateAiPaddle(dt);
        } else {
            leftPaddle.update(input.isWPressed(), input.isSPressed(), panelHeight, dt);
        }
        rightPaddle.update(input.isUpPressed(), input.isDownPressed(), panelHeight, dt);
        ball.update(dt, leftPaddle, rightPaddle);

        // Scoring
        if (ball.getX() <= 0) {
            matchState.scoreRight();
            playSound(scoreSound);
            checkMatchball();
            checkGameOver();
            ball.reset(ballStartX, ballStartY);
        }
        if (ball.getX() >= panelWidth) {
            matchState.scoreLeft();
            playSound(scoreSound);
            checkMatchball();
            checkGameOver();
            ball.reset(ballStartX, ballStartY);
        }
    }

    private void quitGame() {
        running = false;
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
        System.exit(0);
    }

    private boolean handleSpeedChange(boolean keyPressed, boolean alreadyHandled, int speedDelta) {
        if (!keyPressed) {
            return false;
        }

        if (!alreadyHandled) {
            ball.changeSpeed(speedDelta);
        }

        return true;
    }

    private void initializeSounds() {
        if (!config.isSoundsEnabled()) {
            paddleSound = null;
            wallSound = null;
            scoreSound = null;
            return;
        }

        paddleSound = new Sound(HIT_SOUND_RESOURCE);
        wallSound = new Sound(WALL_SOUND_RESOURCE);
        scoreSound = new Sound(SCORE_SOUND_RESOURCE);
    }

    private BackgroundMusicPlayer initializeBackgroundMusic() {
        if (!config.isMusicEnabled()) {
            return null;
        }

        return new BackgroundMusicPlayer(BACKGROUND_MUSIC_RESOURCE);
    }

    private void toggleBackgroundMusicMute() {
        if (backgroundMusic != null) {
            backgroundMusic.toggleMuted();
        }
    }

    private void setBackgroundMusicPaused(boolean paused) {
        if (backgroundMusic != null) {
            backgroundMusic.setPaused(paused);
        }
    }

    private void decreaseBackgroundMusicVolume() {
        if (backgroundMusic != null) {
            backgroundMusic.decreaseVolume();
        }
    }

    private void increaseBackgroundMusicVolume() {
        if (backgroundMusic != null) {
            backgroundMusic.increaseVolume();
        }
    }

    private String getLeftDisplayName() {
        return aiMode ? "Bot" : matchState.getLeftPlayerName();
    }

    private String getRightDisplayName() {
        return matchState.getRightPlayerName();
    }

    private String getWinnerDisplayText() {
        return (matchState.getLeftScore() > matchState.getRightScore()
                ? getLeftDisplayName()
                : getRightDisplayName()) + " wins!";
    }

    private void playSound(Sound sound) {
        if (sound != null) {
            sound.play();
        }
    }

    private Paddle createLeftPaddle() {
        return new Paddle(LEFT_PADDLE_X, paddleStartY, PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    private Paddle createRightPaddle() {
        return new Paddle(rightPaddleX, paddleStartY, PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    private void resetPaddles() {
        leftPaddle = createLeftPaddle();
        rightPaddle = createRightPaddle();
    }

    private void resetMatchState() {
        matchState.reset();
        matchballScreen.reset();
        countdownScreen.reset();
        aiDecisionTimer = 0;
        aiTargetY = panelHeight / 2.0;
        ball.reset(ballStartX, ballStartY);
    }

    private void beginCountdown() {
        resetMatchState();
        resetPaddles();
        countdownScreen.start();
        gameState = GameState.COUNTDOWN;
    }

    private void beginMatch() {
        countdownScreen.reset();
        gameState = GameState.PLAYING;
    }

    private void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            setBackgroundMusicPaused(true);
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            setBackgroundMusicPaused(false);
        }
    }

    private void restart() {
        beginCountdown();
    }

    private void updateAiPaddle(double dt) {
        aiDecisionTimer -= dt;

        if (aiDecisionTimer <= 0) {
            aiDecisionTimer = aiSettings.decisionInterval();

            if (ball.getVx() < 0) {
                aiTargetY = ball.getY() - leftPaddle.getHeight() / 2.0
                        + (Math.random() * 2 - 1) * aiSettings.errorRange();
            } else {
                aiTargetY = panelHeight / 2.0 - leftPaddle.getHeight() / 2.0;
            }
        }

        double paddleCenter = leftPaddle.getY() + leftPaddle.getHeight() / 2.0;
        double targetCenter = aiTargetY + leftPaddle.getHeight() / 2.0;

        boolean moveUp = paddleCenter > targetCenter + aiSettings.deadZone();
        boolean moveDown = paddleCenter < targetCenter - aiSettings.deadZone();

        leftPaddle.update(moveUp, moveDown, panelHeight, dt);
    }

    private record AiSettings(double decisionInterval, double errorRange, double deadZone) {
        private static AiSettings fromStrength(int strength) {
            return switch (strength) {
                case 4 -> new AiSettings(0.08, 10, 12);
                case 3 -> new AiSettings(0.14, 35, 20);
                case 2 -> new AiSettings(0.22, 70, 28);
                case 1 -> new AiSettings(0.35, 120, 35);
                default -> new AiSettings(0.35, 120, 35);
            };
        }
    }

    private void checkGameOver() {
        if (matchState.isGameOver()) {
            gameState = GameState.GAME_OVER;
        }
    }

    private void checkMatchball() {
        if (matchState.isMatchball()) {
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

        for (int y = 0; y < panelHeight; y += 30) {
            g.fillRect(panelWidth / 2 - 2, y, 4, 20);
        }

        leftPaddle.draw(g);
        rightPaddle.draw(g);
        ball.draw(g);

        int speed = (int) Math.hypot(ball.getVx(), ball.getVy());
        hud.setData(
                matchState.getLeftScore(),
                matchState.getRightScore(),
                speed,
                matchState.getGameTime(),
                getLeftDisplayName(),
                getRightDisplayName()
        );
        hud.draw(g);

        if (gameState == GameState.PAUSED) {
            pauseScreen.draw(g);
        }

        if (gameState == GameState.GAME_OVER) {
            gameOverScreen.setWinner(getWinnerDisplayText());
            gameOverScreen.draw(g);
        }

        if (gameState == GameState.COUNTDOWN && countdownScreen.isVisible()) {
            countdownScreen.draw(g);
        }

        if (gameState == GameState.PLAYING && matchballScreen.isVisible()) {
            matchballScreen.draw(g);
        }
    }

    private enum GameState {PLAYING, PAUSED, GAME_OVER, START, COUNTDOWN}
}
