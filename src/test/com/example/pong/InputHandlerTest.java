package com.example.pong;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.event.KeyEvent;

public final class InputHandlerTest {
    private InputHandlerTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("InputHandler updates key state on press and release", InputHandlerTest::updatesKeyStatesOnPressAndRelease);
        TestSupport.runTest("InputHandler supports alternative key bindings", InputHandlerTest::supportsAlternativeBindings);
    }

    private static void updatesKeyStatesOnPressAndRelease() {
        InputHandler handler = new InputHandler();
        Component source = new Canvas();

        handler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        handler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));

        TestSupport.assertTrue(handler.isWPressed(), "W should be recognized as pressed.");
        TestSupport.assertTrue(handler.isUpPressed(), "Up arrow should be recognized as pressed.");

        handler.keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        handler.keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));

        TestSupport.assertFalse(handler.isWPressed(), "W should be false after release.");
        TestSupport.assertFalse(handler.isUpPressed(), "Up arrow should be false after release.");
    }

    private static void supportsAlternativeBindings() {
        InputHandler handler = new InputHandler();
        Component source = new Canvas();

        handler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_Q, 'Q'));
        handler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A'));
        handler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_EQUALS, '+'));
        handler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_NUMPAD7, '7'));

        TestSupport.assertTrue(handler.isWPressed(), "Q should set the W state.");
        TestSupport.assertTrue(handler.isSPressed(), "A should set the S state.");
        TestSupport.assertTrue(handler.isPlusPressed(), "= should be recognized as a plus alias.");
        TestSupport.assertTrue(handler.isSevenPressed(), "Numpad 7 should be recognized.");
    }
}

