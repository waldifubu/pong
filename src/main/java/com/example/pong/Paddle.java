package com.example.pong;

import java.awt.*;

public class Paddle {
    private static final double DEFAULT_SPEED = 400;

    private double x, y;
    private final int width;
    private final int height;
    private final double speed = DEFAULT_SPEED; // pixels per second
    private double velocityY;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(boolean upPressed, boolean downPressed, int panelHeight, double dt) {
        velocityY = 0;

        if (upPressed != downPressed) {
            if (upPressed) {
                y -= speed * dt;
                velocityY = -speed;
            } else {
                y += speed * dt;
                velocityY = speed;
            }
        }

        if (y < 0) y = 0;
        if (y + height > panelHeight) y = panelHeight - height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect((int) x, (int) y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getVelocityY() {
        return velocityY;
    }
}
