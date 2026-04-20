package screen;

import java.awt.*;

public class HUD implements Screen {

    private final int width;
    private int leftScore;
    private int rightScore;
    private int speed;
    private double gameTime;

    public HUD(int width) {
        this.width = width;
    }

    public void setData(int leftScore, int rightScore, int speed, double gameTime) {
        this.leftScore = leftScore;
        this.rightScore = rightScore;
        this.speed = speed;
        this.gameTime = gameTime;
    }

    @Override
    public void update(double dt) {
        // HUD uses externally provided state
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        String scoreText = leftScore + "   " + rightScore;
        FontMetrics scoreMetrics = g.getFontMetrics();
        g.drawString(scoreText, (width - scoreMetrics.stringWidth(scoreText)) / 2, 50);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Speed: " + speed, 10, 30);

        String timeText = String.format("Time: %.1fs", gameTime);
        FontMetrics timeMetrics = g.getFontMetrics();
        g.drawString(timeText, width - timeMetrics.stringWidth(timeText) - 10, 30);
    }
}
