package com.example.pong;

public final class BallTest {
    private BallTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("Ball starts with the default speed", BallTest::startsWithDefaultSpeed);
        TestSupport.runTest("Ball.changeSpeed respects min and max limits", BallTest::changeSpeedClampsToLimits);
        TestSupport.runTest("Ball.invertY flips the vertical velocity", BallTest::invertYFlipsVerticalVelocity);
    }

    private static void startsWithDefaultSpeed() {
        Ball ball = new Ball(100, 200, 20, 800, 600);
        double speed = Math.hypot(ball.getVx(), ball.getVy());
        TestSupport.assertApprox(400.0, speed, 1e-6, "The ball should start with the default speed.");
        TestSupport.assertApprox(100.0, ball.getX(), 1e-9, "X should match the start position.");
        TestSupport.assertApprox(200.0, ball.getY(), 1e-9, "Y should match the start position.");
    }

    private static void changeSpeedClampsToLimits() {
        Ball ball = new Ball(100, 100, 20, 800, 600);
        ball.changeSpeed(-10_000);
        TestSupport.assertApprox(50.0, Math.hypot(ball.getVx(), ball.getVy()), 1e-6, "The minimum speed should stay at 50.");

        ball.changeSpeed(10_000);
        TestSupport.assertApprox(1000.0, Math.hypot(ball.getVx(), ball.getVy()), 1e-6, "The maximum speed should stay at 1000.");
    }

    private static void invertYFlipsVerticalVelocity() {
        Ball ball = new Ball(100, 100, 20, 800, 600);
        TestSupport.setField(ball, "vy", 123.5);
        ball.invertY();
        TestSupport.assertApprox(-123.5, ball.getVy(), 1e-9, "invertY should flip the sign of vy.");
    }
}

