package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics2D;

public final class CountdownScreenTest {
    private CountdownScreenTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("CountdownScreen counts down", CountdownScreenTest::countsDownAndFinishes);
        TestSupport.runTest("CountdownScreen.reset hides the countdown", CountdownScreenTest::resetHidesCountdown);
        TestSupport.runTest("CountdownScreen.draw renders when visible", CountdownScreenTest::drawsVisibleCountdown);
    }

    private static void countsDownAndFinishes() {
        CountdownScreen screen = new CountdownScreen(800, 600, 3.0);
        screen.start();
        TestSupport.assertTrue(screen.isVisible(), "The countdown should be visible after start.");
        TestSupport.assertEquals(3, screen.getCurrentValue(), "The countdown should start at 3.");

        screen.update(1.2);
        TestSupport.assertEquals(2, screen.getCurrentValue(), "After 1.2 seconds, the countdown should still show 2.");

        screen.update(2.0);
        TestSupport.assertTrue(screen.isFinished(), "The countdown should be finished after it expires.");
        TestSupport.assertFalse(screen.isVisible(), "The countdown should be hidden after it expires.");
        TestSupport.assertEquals(0, screen.getCurrentValue(), "A hidden countdown should return 0.");
    }

    private static void resetHidesCountdown() {
        CountdownScreen screen = new CountdownScreen(800, 600, 2.0);
        screen.start();
        screen.reset();
        TestSupport.assertFalse(screen.isVisible(), "reset should hide the countdown.");
        TestSupport.assertTrue(screen.isFinished(), "reset should mark the countdown as finished.");
    }

    private static void drawsVisibleCountdown() {
        CountdownScreen screen = new CountdownScreen(800, 600, 3.0);
        screen.start();
        Graphics2D graphics = TestSupport.createGraphics(800, 600);
        try {
            screen.draw(graphics);
        } finally {
            graphics.dispose();
        }
    }
}

