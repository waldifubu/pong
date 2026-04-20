import java.awt.*;

public class Ball {

    private double x, y;
    private final int size;

    // Velocity (px/s)
    private double vx;
    private double vy;

    public Ball(int startX, int startY, int size) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        reset(startX, startY);
    }

    // =========================
    // UPDATE
    // =========================
    public void update(double dt, Paddle leftPaddle, Paddle rightPaddle) {
        x += vx * dt;
        y += vy * dt;

        // Wall collision (top/bottom)
        if (y <= 0) {
            y = 0;
            invertY();
            if (GamePanel.wallSound != null) GamePanel.wallSound.play();
        }

        if (y >= GamePanel.HEIGHT - size) {
            y = GamePanel.HEIGHT - size;
            invertY();
            if (GamePanel.wallSound != null) GamePanel.wallSound.play();
        }

        // Left paddle collision
        if (getBounds().intersects(leftPaddle.getBounds())) {
            x = leftPaddle.getBounds().getMaxX(); // rausdrücken
            reflect(leftPaddle);
            if (GamePanel.paddleSound != null) GamePanel.paddleSound.play();
        }

        // Right paddle collision
        if (getBounds().intersects(rightPaddle.getBounds())) {
            x = rightPaddle.getBounds().getX() - size; // rausdrücken
            reflect(rightPaddle);
            if (GamePanel.paddleSound != null) GamePanel.paddleSound.play();
        }
    }

    // =========================
    // REFLECTION + SPIN
    // =========================
    private void reflect(Paddle paddle) {

        Rectangle paddleRect = paddle.getBounds();

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
        double maxBounceAngle = Math.toRadians(60);
        double bounceAngle = relativeIntersectY * maxBounceAngle;

        double speed = Math.sqrt(vx * vx + vy * vy);

        // =========================
        // BASE DIRECTION
        // =========================
        double newVx = speed * Math.cos(bounceAngle);
        double newVy = speed * Math.sin(bounceAngle);

        // =========================
        // SPIN (REDUCED & CONTROLLED)
        // =========================
        double spinFactor = 0.35; // less is cleaner
        newVy += paddle.getVelocityY() * spinFactor;

        // =========================
        // HORIZONTAL DIRECTION
        // =========================
        if (paddleRect.getX() > GamePanel.WIDTH / 2.0) {
            newVx = -Math.abs(newVx);
        } else {
            newVx = Math.abs(newVx);
        }

        // =========================
        // MIN HORIZONTAL SPEED (ANTI VERTICAL LOCK)
        // =========================
        double minHorizontalRatio = 0.15; // 15% of total speed
        double minVx = speed * minHorizontalRatio;

        if (Math.abs(newVx) < minVx) {
            newVx = Math.signum(newVx) * minVx;
        }

        // =========================
        // NORMALIZE SPEED
        // =========================
        double length = Math.sqrt(newVx * newVx + newVy * newVy);

        // leichte Beschleunigung
        double newSpeed = speed * 1.05;

        // clamp
        newSpeed = Math.max(400, Math.min(newSpeed, 1000));

        vx = (newVx / length) * newSpeed;
        vy = (newVy / length) * newSpeed;
    }

    // =========================
    // RESET (always left/right)
    // =========================
    public void reset(int startX, int startY) {
        x = startX;
        y = startY;

        double speed = 400;

        // Angle (-30° to +30°)
        double angle = Math.toRadians((Math.random() * 60) - 30);

        boolean goRight = Math.random() < 0.5;

        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);

        if (!goRight) vx = -vx;
    }

    // =========================
    // SPEED CONTROL (+ / -)
    // =========================
    public void changeSpeed(double delta) {
        double speed = Math.sqrt(vx * vx + vy * vy);

        speed += delta;

        double minSpeed = 50;
        double maxSpeed = 1000;

        speed = Math.max(minSpeed, Math.min(maxSpeed, speed));

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
