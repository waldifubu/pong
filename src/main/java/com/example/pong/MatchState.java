package com.example.pong;

public final class MatchState {
    private final int maxScore;
    private final String leftPlayerName;
    private final String rightPlayerName;

    private int leftScore;
    private int rightScore;
    private double gameTime;

    public MatchState(int maxScore, String leftPlayerName, String rightPlayerName) {
        this.maxScore = maxScore;
        this.leftPlayerName = leftPlayerName;
        this.rightPlayerName = rightPlayerName;
    }

    public void reset() {
        leftScore = 0;
        rightScore = 0;
        gameTime = 0;
    }

    public void advanceTime(double dt) {
        if (dt > 0) {
            gameTime += dt;
        }
    }

    public void scoreLeft() {
        leftScore++;
    }

    public void scoreRight() {
        rightScore++;
    }

    public boolean isGameOver() {
        return leftScore >= maxScore || rightScore >= maxScore;
    }

    public boolean isMatchball() {
        return maxScore > 1 && (leftScore == maxScore - 1 || rightScore == maxScore - 1);
    }

    public String getWinnerName() {
        return leftScore > rightScore ? leftPlayerName : rightPlayerName;
    }

    public String getWinnerText() {
        return getWinnerName() + " Wins!";
    }

    public int getLeftScore() {
        return leftScore;
    }

    public int getRightScore() {
        return rightScore;
    }

    public double getGameTime() {
        return gameTime;
    }

    public String getLeftPlayerName() {
        return leftPlayerName;
    }

    public String getRightPlayerName() {
        return rightPlayerName;
    }
}
