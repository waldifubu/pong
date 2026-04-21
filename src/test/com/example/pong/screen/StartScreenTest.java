package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics2D;

public final class StartScreenTest {
    private StartScreenTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("StartScreen completes its intro animation", StartScreenTest::completesAnimation);
        TestSupport.runTest("StartScreen.reset restores the initial state", StartScreenTest::resetRestoresInitialState);
        TestSupport.runTest("StartScreen.draw renders without throwing", StartScreenTest::drawsWithoutFailure);
    }

    private static void completesAnimation() {
        StartScreen screen = new StartScreen(800, 600);
        screen.update(10.0);
        TestSupport.assertTrue(screen.isAnimationDone(), "The animation should finish after enough time has passed.");
    }

    private static void resetRestoresInitialState() {
        StartScreen screen = new StartScreen(800, 600);
        screen.update(10.0);
        screen.reset();
        TestSupport.assertFalse(screen.isAnimationDone(), "After reset, the animation must no longer be marked as finished.");
    }

    private static void drawsWithoutFailure() {
        StartScreen screen = new StartScreen(800, 600);
        screen.update(10.0);
        Graphics2D graphics = TestSupport.createGraphics(800, 600);
        try {
            screen.draw(graphics);
        } finally {
            graphics.dispose();
        }
    }
}

