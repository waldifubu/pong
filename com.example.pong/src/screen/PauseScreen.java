package screen;

import java.awt.*;

public class PauseScreen {

    private int width, height;

    public PauseScreen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void update(double dt) {
        // keine Animation nötig (optional später)
    }

    public void draw(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        // light dark overlay
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.6f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        // Text
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 1f));

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Monospaced", Font.BOLD, 50));

        drawCentered(g2, "PAUSED", height / 2);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 20));
        drawCentered(g2, "Press P to continue", height / 2 + 40);
    }

    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
