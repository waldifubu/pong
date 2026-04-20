package screen;

import java.awt.*;

public class GameOverScreen {

    private int width, height;

    private String winnerText = "";

    public GameOverScreen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setWinner(String winner) {
        this.winnerText = winner;
    }

    public void update(double dt) {
        // optional animation later
    }

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
        g2.setFont(new Font("Monospaced", Font.BOLD, 130));
        drawCentered(g2, "GAME OVER", height / 2 - 80);

        // Winner
        g2.setFont(new Font("Monospaced", Font.BOLD, 50));
        drawCentered(g2, winnerText, height / 2 + 10);

        // Restart
        g2.setFont(new Font("Monospaced", Font.PLAIN, 30));
        drawCentered(g2, "Press SPACE for rematch", height / 2 + 80);
    }

    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
