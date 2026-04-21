package com.example.pong;

public final class AllTests {
    private static final String[] TEST_CLASSES = {
            "com.example.pong.GameConfigTest",
            "com.example.pong.MatchStateTest",
            "com.example.pong.PaddleTest",
            "com.example.pong.BallTest",
            "com.example.pong.InputHandlerTest",
            "com.example.pong.GamePanelTest",
            "com.example.pong.MainTest",
            "com.example.pong.SoundTest",
            "com.example.pong.BackgroundMusicPlayerTest",
            "com.example.pong.screen.ScreenTest",
            "com.example.pong.screen.StartScreenTest",
            "com.example.pong.screen.CountdownScreenTest",
            "com.example.pong.screen.PauseScreenTest",
            "com.example.pong.screen.GameOverScreenTest",
            "com.example.pong.screen.MatchballScreenTest",
            "com.example.pong.screen.HUDTest"
    };

    private AllTests() {
    }

    public static void main(String[] args) {
        for (String testClass : TEST_CLASSES) {
            invokeMain(testClass, args);
        }
        System.out.println("All tests completed successfully.");
    }

    private static void invokeMain(String className, String[] args) {
        try {
            Class<?> testClass = Class.forName(className);
            testClass.getDeclaredMethod("main", String[].class).invoke(null, (Object) args);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not execute test class: " + className, e);
        }
    }
}

