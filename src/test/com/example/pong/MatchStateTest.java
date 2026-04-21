package com.example.pong;

public final class MatchStateTest {
    private MatchStateTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("MatchState tracks score and time", MatchStateTest::tracksScoreAndTime);
        TestSupport.runTest("MatchState detects match point and winner", MatchStateTest::detectsMatchballAndWinner);
        TestSupport.runTest("MatchState.reset clears state", MatchStateTest::resetClearsState);
    }

    private static void tracksScoreAndTime() {
        MatchState state = new MatchState(3, "Left", "Right");
        state.advanceTime(1.5);
        state.advanceTime(-1.0);
        state.scoreLeft();
        state.scoreRight();

        TestSupport.assertEquals(1, state.getLeftScore(), "The left score should increase.");
        TestSupport.assertEquals(1, state.getRightScore(), "The right score should increase.");
        TestSupport.assertApprox(1.5, state.getGameTime(), 1e-9, "Negative time values must not be added.");
    }

    private static void detectsMatchballAndWinner() {
        MatchState state = new MatchState(3, "Alice", "Bob");
        state.scoreLeft();
        state.scoreLeft();

        TestSupport.assertTrue(state.isMatchball(), "At 2 out of 3 points, match point should be active.");
        TestSupport.assertFalse(state.isGameOver(), "The game must not be over before the final point.");
        TestSupport.assertEquals("Alice", state.getWinnerName(), "The leading player should be treated as the winner.");
        TestSupport.assertEquals("Alice Wins!", state.getWinnerText(), "The winner text should be correct.");

        state.scoreLeft();
        TestSupport.assertTrue(state.isGameOver(), "Game over should be active at the max score.");
    }

    private static void resetClearsState() {
        MatchState state = new MatchState(5, "Alice", "Bob");
        state.scoreLeft();
        state.scoreRight();
        state.advanceTime(2.0);

        state.reset();

        TestSupport.assertEquals(0, state.getLeftScore(), "Reset should clear the left score.");
        TestSupport.assertEquals(0, state.getRightScore(), "Reset should clear the right score.");
        TestSupport.assertApprox(0.0, state.getGameTime(), 1e-9, "Reset should clear the game time.");
    }
}

