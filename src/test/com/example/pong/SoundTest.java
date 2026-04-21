package com.example.pong;

public final class SoundTest {
    private SoundTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("Sound rejects blank resource paths", SoundTest::rejectsBlankResourcePath);
        TestSupport.runTest("Sound handles missing resources gracefully", SoundTest::handlesMissingResourceGracefully);
    }

    private static void rejectsBlankResourcePath() {
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Sound("   "), "Blank resource paths must be rejected.");
    }

    private static void handlesMissingResourceGracefully() {
        Sound sound = new Sound("/missing.wav");
        sound.play();
        TestSupport.assertTrue(true, "A missing sound should not cause a runtime failure.");
    }
}

