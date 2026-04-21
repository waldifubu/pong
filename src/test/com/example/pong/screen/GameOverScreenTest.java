package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics2D;

public final class GameOverScreenTest {
    private GameOverScreenTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("GameOverScreen resets animation when the winner changes", GameOverScreenTest::resetsAnimationWhenWinnerChanges);
        TestSupport.runTest("GameOverScreen.draw renders without throwing", GameOverScreenTest::drawsWithoutFailure);
    }

    private static void resetsAnimationWhenWinnerChanges() {
        GameOverScreen screen = new GameOverScreen(800, 600);
        screen.setWinner("Alice wins!");
        screen.update(1.25);
        TestSupport.assertApprox(1.25, (double) TestSupport.getField(screen, "animationTime"), 1e-9, "update should increase the animation time.");

        screen.setWinner("Bob wins!");
        TestSupport.assertApprox(0.0, (double) TestSupport.getField(screen, "animationTime"), 1e-9, "A new winner should reset the animation time.");
    }

    private static void drawsWithoutFailure() {
        GameOverScreen screen = new GameOverScreen(800, 600);
        screen.setWinner("Alice wins!");
        screen.update(0.5);
        Graphics2D graphics = TestSupport.createGraphics(800, 600);
        try {
            screen.draw(graphics);
        } finally {
            graphics.dispose();
        }
    }
}

