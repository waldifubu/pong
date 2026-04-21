package com.example.pong;

public final class PaddleTest {
    private PaddleTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("Paddle moves up and down", PaddleTest::movesInExpectedDirection);
        TestSupport.runTest("Paddle stays within the playfield", PaddleTest::staysInsideBounds);
        TestSupport.runTest("Paddle stays still on conflicting input", PaddleTest::stopsOnConflictingInput);
    }

    private static void movesInExpectedDirection() {
        Paddle paddle = new Paddle(10, 100, 10, 80);
        paddle.update(true, false, 400, 0.5);
        TestSupport.assertApprox(0.0, paddle.getY(), 1e-9, "The paddle should move upward and stop at the edge.");
        TestSupport.assertTrue(paddle.getVelocityY() < 0, "Upward velocity should be negative.");

        paddle.update(false, true, 400, 0.25);
        TestSupport.assertApprox(100.0, paddle.getY(), 1e-9, "The paddle should move downward.");
        TestSupport.assertTrue(paddle.getVelocityY() > 0, "Downward velocity should be positive.");
    }

    private static void staysInsideBounds() {
        Paddle paddle = new Paddle(10, 350, 10, 80);
        paddle.update(false, true, 400, 1.0);
        TestSupport.assertApprox(320.0, paddle.getY(), 1e-9, "The paddle must not move below the lower edge.");
    }

    private static void stopsOnConflictingInput() {
        Paddle paddle = new Paddle(10, 120, 10, 80);
        paddle.update(true, true, 400, 1.0);
        TestSupport.assertApprox(120.0, paddle.getY(), 1e-9, "The paddle must not move when opposite directions are pressed together.");
        TestSupport.assertApprox(0.0, paddle.getVelocityY(), 1e-9, "Velocity must be zero on conflicting input.");
    }
}

