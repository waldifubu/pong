package screen;

import java.awt.*;

public class HUD implements Screen {

    private static final Font SCORE_FONT = new Font("Monospaced", Font.BOLD, 40);
    private static final Font INFO_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final Font PLAYER_FONT = new Font("Arial", Font.BOLD, 18);

    private final int width;
    private int leftScore;
    private int rightScore;
    private int speed;
    private double gameTime;
    private String leftPlayerName = "Left Player";
    private String rightPlayerName = "Right Player";

    public HUD(int width) {
        this.width = width;
    }

    public void setData(int leftScore, int rightScore, int speed, double gameTime,
                        String leftPlayerName, String rightPlayerName) {
        this.leftScore = leftScore;
        this.rightScore = rightScore;
        this.speed = speed;
        this.gameTime = gameTime;
        this.leftPlayerName = leftPlayerName;
        this.rightPlayerName = rightPlayerName;
    }

    @Override
    public void update(double dt) {
        // HUD uses externally provided state
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(PLAYER_FONT);
        drawCentered(g, leftPlayerName, width / 4, 30);
        drawCentered(g, rightPlayerName, (width * 3) / 4, 30);

        g.setFont(SCORE_FONT);
        String scoreText = leftScore + "   " + rightScore;
        FontMetrics scoreMetrics = g.getFontMetrics();
        g.drawString(scoreText, (width - scoreMetrics.stringWidth(scoreText)) / 2, 50);

        g.setFont(INFO_FONT);
        g.drawString("Speed: " + speed, 10, 30);

        String timeText = String.format("Time: %.1fs", gameTime);
        FontMetrics timeMetrics = g.getFontMetrics();
        g.drawString(timeText, width - timeMetrics.stringWidth(timeText) - 10, 30);
    }

    private void drawCentered(Graphics g, String text, int centerX, int y) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, centerX - metrics.stringWidth(text) / 2, y);
    }
}
