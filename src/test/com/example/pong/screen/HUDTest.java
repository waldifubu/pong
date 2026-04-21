package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics2D;

public final class HUDTest {
    private HUDTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("HUD stores the provided data", HUDTest::storesProvidedData);
        TestSupport.runTest("HUD.draw renders without throwing", HUDTest::drawsWithoutFailure);
    }

    private static void storesProvidedData() {
        HUD hud = new HUD(800);
        hud.setData(3, 5, 420, 12.5, "Alice", "Bob");

        TestSupport.assertEquals(3, TestSupport.getField(hud, "leftScore"), "The left score should be stored.");
        TestSupport.assertEquals(5, TestSupport.getField(hud, "rightScore"), "The right score should be stored.");
        TestSupport.assertEquals(420, TestSupport.getField(hud, "speed"), "The speed should be stored.");
        TestSupport.assertApprox(12.5, (double) TestSupport.getField(hud, "gameTime"), 1e-9, "The game time should be stored.");
        TestSupport.assertEquals("Alice", TestSupport.getField(hud, "leftPlayerName"), "The left player name should be stored.");
        TestSupport.assertEquals("Bob", TestSupport.getField(hud, "rightPlayerName"), "The right player name should be stored.");
    }

    private static void drawsWithoutFailure() {
        HUD hud = new HUD(800);
        hud.setData(1, 2, 300, 4.5, "Alice", "Bob");
        Graphics2D graphics = TestSupport.createGraphics(800, 120);
        try {
            hud.draw(graphics);
        } finally {
            graphics.dispose();
        }
    }
}

