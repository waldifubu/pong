package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics2D;

public final class PauseScreenTest {
    private PauseScreenTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("PauseScreen.update is robust", PauseScreenTest::updateDoesNotFail);
        TestSupport.runTest("PauseScreen.draw renders without throwing", PauseScreenTest::drawsWithoutFailure);
    }

    private static void updateDoesNotFail() {
        PauseScreen screen = new PauseScreen(800, 600);
        screen.update(1.0);
        TestSupport.assertTrue(true, "update should not throw an exception.");
    }

    private static void drawsWithoutFailure() {
        PauseScreen screen = new PauseScreen(800, 600);
        Graphics2D graphics = TestSupport.createGraphics(800, 600);
        try {
            screen.draw(graphics);
        } finally {
            graphics.dispose();
        }
    }
}

