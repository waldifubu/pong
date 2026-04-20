package screen;

import java.awt.*;

public class StartScreen implements Screen {

    private double titleY;
    private double targetY;
    private double speed = 200;

    private float alpha = 0f;
    private float fadeSpeed = 1.0f;

    private boolean animationDone = false;

    private final int width;
    private final int height;

    public StartScreen(int width, int height) {
        this.width = width;
        this.height = height;

        targetY = (double) height / 6;
        titleY = -100;
    }

    public void update(double dt) {
        // Slide
        if (titleY < targetY) {
            titleY += speed * dt;
            if (titleY > targetY) {
                titleY = targetY;
            }
        }

        // Fade
        if (alpha < 1f) {
            alpha += (float) (fadeSpeed * dt);
            if (alpha > 1f) alpha = 1f;
        }

        // Done?
        if (titleY == targetY && alpha >= 1f) {
            animationDone = true;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g2.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 22));

        String[] pongAscii = {
                "    ██████   ██████  ███    ██  ██████ ",
                "   ██   ██ ██    ██ ████   ██ ██      ",
                "  ██████  ██    ██ ██ ██  ██ ██  ███ ",
                " ██      ██    ██ ██  ██ ██ ██   ██ ",
                "██       ██████  ██   ████  ██████ "
        };

        int y = (int) titleY;

        for (String line : pongAscii) {
            drawCentered(g2, line, y);
            y += 27;
        }

        g2.setFont(new Font("Monospaced", Font.PLAIN, 18));

        String[] controls = {
                "Modes",
                " 1                Start AI Mode",
                "     2            Start Two Player Mode",
                "",
                "Controls",
                "      W,Q / S,A     Left Player (AI Player)",
                "UP / DOWN         Right Player",
                "+ / -             Ball Speed",
                "P                 Pause",
                "       M         Mute / Unmute Music",
                "       6 / 7     Music quieter / louder",
                "    R             Restart Game",
                "X                 Exit",
                "",
                "(c) 2026 by Waldemar Dell",
                "with support from ChatGPT"
        };

        y += 40;

        for (String line : controls) {
            drawCentered(g2, line, y);
            y += 27;
        }

        // Blinked notice
        if (animationDone) {
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                drawCentered(g2, ">>> PRESS 1 OR 2 <<<", height - 105);
            }
        }

        // Reset Alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    public boolean isAnimationDone() {
        return animationDone;
    }

    public void reset() {
        titleY = -100;
        alpha = 0f;
        animationDone = false;
    }
}
