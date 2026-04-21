package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics2D;

public final class MatchballScreenTest {
    private MatchballScreenTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("MatchballScreen shows and hides over time", MatchballScreenTest::showsAndHidesOverTime);
        TestSupport.runTest("MatchballScreen.reset hides the screen", MatchballScreenTest::resetHidesScreen);
        TestSupport.runTest("MatchballScreen.draw renders without throwing", MatchballScreenTest::drawsWithoutFailure);
    }

    private static void showsAndHidesOverTime() {
        MatchballScreen screen = new MatchballScreen(800, 600, 2.0, 0.5);
        screen.show();
        TestSupport.assertTrue(screen.isVisible(), "The screen should be visible after show().");

        screen.update(1.0);
        TestSupport.assertTrue(screen.isVisible(), "The screen should remain visible before the duration expires.");

        screen.update(1.1);
        TestSupport.assertFalse(screen.isVisible(), "The screen should disappear after the duration expires.");
    }

    private static void resetHidesScreen() {
        MatchballScreen screen = new MatchballScreen(800, 600);
        screen.show();
        screen.reset();
        TestSupport.assertFalse(screen.isVisible(), "reset should hide the screen.");
    }

    private static void drawsWithoutFailure() {
        MatchballScreen screen = new MatchballScreen(800, 600);
        screen.show();
        Graphics2D graphics = TestSupport.createGraphics(800, 600);
        try {
            screen.draw(graphics);
        } finally {
            graphics.dispose();
        }
    }
}

