package screen;

import java.awt.*;

public class GameOverScreen implements Screen {

    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 130);
    private static final Font WINNER_FONT = new Font("Monospaced", Font.BOLD, 50);
    private static final Font MESSAGE_FONT = new Font("Monospaced", Font.PLAIN, 30);
    private static final Color[] RAINBOW_COLORS = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.WHITE,
            Color.MAGENTA,
            Color.PINK
    };
    private static final double WINNER_SCROLL_SPEED = 220;
    private static final double COLOR_SHIFT_SPEED = 8;
    private static final double MESSAGE_BLINK_SPEED = 3.5;
    private static final int WINNER_GAP = 120;

    private final int width;
    private final int height;

    private String winnerText = "";
    private double animationTime;

    public GameOverScreen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setWinner(String winner) {
        if (!winner.equals(this.winnerText)) {
            this.winnerText = winner;
            animationTime = 0;
        }
    }

    @Override
    public void update(double dt) {
        animationTime += dt;
    }

    @Override
    public void draw(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        // Darken background
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.7f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 1f));

        g2.setColor(Color.WHITE);

        // Title
        g2.setFont(TITLE_FONT);
        drawCentered(g2, "GAME OVER", height / 2 - 80);

        // Winner
        g2.setFont(WINNER_FONT);
        drawAnimatedWinner(g2, height / 2 + 10);

        // Restart
        drawBlinkingMessage(g2, ">>> Press SPACE for rematch <<<", height / 2 + 80);
    }

    private void drawAnimatedWinner(Graphics2D g2, int y) {
        if (winnerText.isBlank()) {
            return;
        }

        Graphics2D animatedGraphics = (Graphics2D) g2.create();
        animatedGraphics.setFont(WINNER_FONT);

        FontMetrics metrics = animatedGraphics.getFontMetrics();
        int textWidth = metrics.stringWidth(winnerText);
        int cycleWidth = width + textWidth + WINNER_GAP;
        double scroll = (animationTime * WINNER_SCROLL_SPEED) % cycleWidth;
        int x = (int) Math.round(width - scroll);
        int colorShift = (int) Math.floor(animationTime * COLOR_SHIFT_SPEED);

        drawRainbowText(animatedGraphics, winnerText, x, y, colorShift);
        drawRainbowText(animatedGraphics, winnerText, x + cycleWidth, y, colorShift);
        animatedGraphics.dispose();
    }

    private void drawRainbowText(Graphics2D g2, String text, int x, int y, int colorShift) {
        FontMetrics metrics = g2.getFontMetrics();
        int drawX = x;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            g2.setColor(RAINBOW_COLORS[Math.floorMod(i + colorShift, RAINBOW_COLORS.length)]);
            g2.drawString(String.valueOf(ch), drawX, y);
            drawX += metrics.charWidth(ch);
        }
    }

    private void drawBlinkingMessage(Graphics2D g2, String text, int y) {
        Graphics2D blinkingGraphics = (Graphics2D) g2.create();
        blinkingGraphics.setFont(MESSAGE_FONT);

        float alpha = (float) (0.65 + 0.35 * ((Math.sin(animationTime * MESSAGE_BLINK_SPEED * Math.PI * 2) + 1) / 2.0));
        blinkingGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        blinkingGraphics.setColor(Color.WHITE);
        drawCentered(blinkingGraphics, text, y);
        blinkingGraphics.dispose();
    }

    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
