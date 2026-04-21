package com.example.pong;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Ball {

    private static final double DEFAULT_SPEED = 400;
    private static final double MIN_SPEED = 50;
    private static final double MAX_SPEED = 1000;
    private static final double SPEED_BOOST_FACTOR = 1.05;
    private static final double MAX_BOUNCE_ANGLE = Math.toRadians(60);
    private static final double MIN_HORIZONTAL_RATIO = 0.15;
    private static final double SPIN_FACTOR = 0.35;
    private static final double MIN_RESET_ANGLE = -30;
    private static final double MAX_RESET_ANGLE = 30;

    private double x, y;
    private final int size;
    private final int playfieldWidth;
    private final int playfieldHeight;

    // Velocity (px/s)
    private double vx;
    private double vy;

    public Ball(int startX, int startY, int size, int playfieldWidth, int playfieldHeight) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        this.playfieldWidth = playfieldWidth;
        this.playfieldHeight = playfieldHeight;
        reset(startX, startY);
    }

    // =========================
    // UPDATE
    // =========================
    public void update(double dt, Paddle leftPaddle, Paddle rightPaddle) {
        x += vx * dt;
        y += vy * dt;

        Rectangle ballBounds = getBounds();
        Rectangle leftPaddleBounds = leftPaddle.getBounds();
        Rectangle rightPaddleBounds = rightPaddle.getBounds();

        // Wall collision (top/bottom)
        if (y <= 0) {
            y = 0;
            invertY();
            if (GamePanel.wallSound != null) GamePanel.wallSound.play();
        }

        if (y >= playfieldHeight - size) {
            y = playfieldHeight - size;
            invertY();
            if (GamePanel.wallSound != null) GamePanel.wallSound.play();
        }

        // Left paddle collision
        if (ballBounds.intersects(leftPaddleBounds)) {
            x = leftPaddleBounds.getMaxX();
            reflect(leftPaddle, leftPaddleBounds);
            if (GamePanel.paddleSound != null) GamePanel.paddleSound.play();
        }

        // Right paddle collision
        if (ballBounds.intersects(rightPaddleBounds)) {
            x = rightPaddleBounds.getX() - size;
            reflect(rightPaddle, rightPaddleBounds);
            if (GamePanel.paddleSound != null) GamePanel.paddleSound.play();
        }
    }

    // =========================
    // REFLECTION + SPIN
    // =========================
    private void reflect(Paddle paddle, Rectangle paddleRect) {

        double paddleCenterY = paddleRect.getY() + paddleRect.getHeight() / 2.0;
        double ballCenterY = y + size / 2.0;

        // =========================
        // HIT POSITION (-1 .. 1)
        // =========================
        double relativeIntersectY = (ballCenterY - paddleCenterY) / (paddleRect.getHeight() / 2.0);

        // Clamp (Security)
        relativeIntersectY = Math.max(-1, Math.min(1, relativeIntersectY));

        // =========================
        // BOUNCE ANGLE
        // =========================
        double bounceAngle = relativeIntersectY * MAX_BOUNCE_ANGLE;

        double speed = Math.hypot(vx, vy);

        // =========================
        // BASE DIRECTION
        // =========================
        double newVx = speed * Math.cos(bounceAngle);
        double newVy = speed * Math.sin(bounceAngle);

        // =========================
        // SPIN (REDUCED & CONTROLLED)
        // =========================
        newVy += paddle.getVelocityY() * SPIN_FACTOR;

        // =========================
        // HORIZONTAL DIRECTION
        // =========================
        if (paddleRect.getX() > playfieldWidth / 2.0) {
            newVx = -Math.abs(newVx);
        } else {
            newVx = Math.abs(newVx);
        }

        // =========================
        // MIN HORIZONTAL SPEED (ANTI VERTICAL LOCK)
        // =========================
        double minVx = speed * MIN_HORIZONTAL_RATIO;

        if (Math.abs(newVx) < minVx) {
            newVx = Math.signum(newVx) * minVx;
        }

        // =========================
        // NORMALIZE SPEED
        // =========================
        double length = Math.hypot(newVx, newVy);

        // Moderate speed boost on each hit
        double newSpeed = speed * SPEED_BOOST_FACTOR;

        // clamp
        newSpeed = Math.max(DEFAULT_SPEED, Math.min(newSpeed, MAX_SPEED));

        vx = (newVx / length) * newSpeed;
        vy = (newVy / length) * newSpeed;
    }

    // =========================
    // RESET (always left/right)
    // =========================
    public void reset(int startX, int startY) {
        x = startX;
        y = startY;

        double speed = DEFAULT_SPEED;

        // Angle (-30° to +30°)
        double angle = Math.toRadians(ThreadLocalRandom.current().nextDouble(MIN_RESET_ANGLE, MAX_RESET_ANGLE));

        boolean goRight = ThreadLocalRandom.current().nextBoolean();

        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);

        if (!goRight) vx = -vx;
    }

    // =========================
    // SPEED CONTROL (+ / -)
    // =========================
    public void changeSpeed(double delta) {
        double speed = Math.hypot(vx, vy);

        speed += delta;

        speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, speed));

        double angle = Math.atan2(vy, vx);

        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);
    }

    // =========================
    // HELPER
    // =========================
    public void invertY() {
        vy = -vy;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval((int) x, (int) y, size, size);
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }
}
