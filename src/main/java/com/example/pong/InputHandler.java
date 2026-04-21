package com.example.pong;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private boolean wPressed, sPressed, upPressed, downPressed, spacePressed, pPressed, rPressed, xPressed, mPressed;
    private boolean onePressed, twoPressed, sixPressed, sevenPressed;

    private boolean plusPressed, minusPressed;

    private void setKeyState(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_Q -> wPressed = pressed;
            case KeyEvent.VK_S, KeyEvent.VK_A -> sPressed = pressed;
            case KeyEvent.VK_UP -> upPressed = pressed;
            case KeyEvent.VK_DOWN -> downPressed = pressed;
            case KeyEvent.VK_SPACE -> spacePressed = pressed;
            case KeyEvent.VK_P -> pPressed = pressed;
            case KeyEvent.VK_R -> rPressed = pressed;
            case KeyEvent.VK_X -> xPressed = pressed;
            case KeyEvent.VK_M -> mPressed = pressed;
            case KeyEvent.VK_1 -> onePressed = pressed;
            case KeyEvent.VK_2 -> twoPressed = pressed;
            case KeyEvent.VK_6, KeyEvent.VK_NUMPAD6 -> sixPressed = pressed;
            case KeyEvent.VK_7, KeyEvent.VK_NUMPAD7 -> sevenPressed = pressed;
            case KeyEvent.VK_PLUS, KeyEvent.VK_ADD, KeyEvent.VK_EQUALS -> plusPressed = pressed;
            case KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT -> minusPressed = pressed;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setKeyState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKeyState(e.getKeyCode(), false);
    }

    public boolean isWPressed() {
        return wPressed;
    }

    public boolean isSPressed() {
        return sPressed;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }

    public boolean isPPressed() {
        return pPressed;
    }

    public boolean isRPressed() {
        return rPressed;
    }

    public boolean isXPressed() {
        return xPressed;
    }

    public boolean isMPressed() {
        return mPressed;
    }

    public boolean isOnePressed() {
        return onePressed;
    }

    public boolean isTwoPressed() {
        return twoPressed;
    }

    public boolean isSixPressed() {
        return sixPressed;
    }

    public boolean isSevenPressed() {
        return sevenPressed;
    }

    public boolean isPlusPressed() {
        return plusPressed;
    }

    public boolean isMinusPressed() {
        return minusPressed;
    }
}
