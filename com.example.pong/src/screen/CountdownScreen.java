package screen;

import java.awt.*;

public class CountdownScreen implements Screen {
    private static final double DEFAULT_START_VALUE = 3.0;
    private static final Font COUNTDOWN_FONT = new Font("Arial", Font.BOLD, 220);

    private final int width;
    private final int height;
    private final double startValue;
    private double timer;

    public CountdownScreen(int width, int height) {
        this(width, height, DEFAULT_START_VALUE);
    }

    public CountdownScreen(int width, int height, double startValue) {
        this.width = width;
        this.height = height;
        this.startValue = startValue;
    }

    @Override
    public void update(double dt) {
        if (timer > 0) {
            timer = Math.max(0, timer - dt);
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!isVisible()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(COUNTDOWN_FONT);
        g2.setColor(Color.WHITE);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));

        String text = Integer.toString(getCurrentValue());
        FontMetrics metrics = g2.getFontMetrics();
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
    }

    public void start() {
        timer = startValue;
    }

    public void reset() {
        timer = 0;
    }

    public boolean isVisible() {
        return timer > 0;
    }

    public boolean isFinished() {
        return timer <= 0;
    }

    public int getCurrentValue() {
        if (!isVisible()) {
            return 0;
        }

        return (int) Math.ceil(timer);
    }

    private float getAlpha() {
        double elapsed = startValue - timer;
        double localProgress = elapsed - Math.floor(elapsed);
        double alpha = 1.0 - localProgress;
        return (float) Math.max(0.15, Math.min(1.0, alpha));
    }
}

