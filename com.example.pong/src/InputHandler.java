import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private boolean wPressed, sPressed, upPressed, downPressed, spacePressed, pPressed, rPressed, xPressed;
    private boolean onePressed, twoPressed;

    private boolean plusPressed, minusPressed;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_Q -> wPressed = true;
            case KeyEvent.VK_S, KeyEvent.VK_A -> sPressed = true;
            case KeyEvent.VK_UP -> upPressed = true;
            case KeyEvent.VK_DOWN -> downPressed = true;
            case KeyEvent.VK_SPACE -> spacePressed = true;
            case KeyEvent.VK_P -> pPressed = true;
            case KeyEvent.VK_R -> rPressed = true; // Reset
            case KeyEvent.VK_X -> xPressed = true;
            case KeyEvent.VK_1 -> onePressed = true;
            case KeyEvent.VK_2 -> twoPressed = true;
            case KeyEvent.VK_PLUS -> plusPressed = true;
            case KeyEvent.VK_MINUS -> minusPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_Q -> wPressed = false;
            case KeyEvent.VK_S, KeyEvent.VK_A -> sPressed = false;
            case KeyEvent.VK_UP -> upPressed = false;
            case KeyEvent.VK_DOWN -> downPressed = false;
            case KeyEvent.VK_SPACE -> spacePressed = false;
            case KeyEvent.VK_P -> pPressed = false;
            case KeyEvent.VK_R -> rPressed = false; // Reset
            case KeyEvent.VK_X -> xPressed = false;
            case KeyEvent.VK_1 -> onePressed = false;
            case KeyEvent.VK_2 -> twoPressed = false;
            case KeyEvent.VK_PLUS -> plusPressed = false;
            case KeyEvent.VK_MINUS -> minusPressed = false;
        }
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

    public boolean isOnePressed() {
        return onePressed;
    }

    public boolean isTwoPressed() {
        return twoPressed;
    }

    public boolean isPlusPressed() {
        return plusPressed;
    }

    public boolean isMinusPressed() {
        return minusPressed;
    }
}
