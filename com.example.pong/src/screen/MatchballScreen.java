package screen;

import java.awt.*;

public class MatchballScreen implements Screen {

    private static final double DEFAULT_DURATION = 2.0;
    private static final double DEFAULT_FADE_DURATION = 0.5;

    private final int width;
    private final int height;
    private final double duration;
    private final double fadeDuration;
    private double timer = 0;

    public MatchballScreen(int width, int height) {
        this(width, height, DEFAULT_DURATION, DEFAULT_FADE_DURATION);
    }

    public MatchballScreen(int width, int height, double duration, double fadeDuration) {
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.fadeDuration = fadeDuration;
    }

    @Override
    public void update(double dt) {
        if (timer > 0) {
            timer = Math.max(0, timer - dt);
        }
    }

    @Override
    public void draw(Graphics g) {
        if (timer <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        double elapsed = duration - timer;
        float alpha = 1f;

        if (elapsed < fadeDuration) {
            alpha = (float) (elapsed / fadeDuration);
        } else if (timer < fadeDuration) {
            alpha = (float) (timer / fadeDuration);
        }

        alpha = Math.max(0f, Math.min(1f, alpha));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 58));
        drawCentered(g2, "MATCHBALL!!!", height / 2);
        g2.dispose();
    }

    public void show() {
        timer = duration;
    }

    public void reset() {
        timer = 0;
    }

    public boolean isVisible() {
        return timer > 0;
    }

    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
