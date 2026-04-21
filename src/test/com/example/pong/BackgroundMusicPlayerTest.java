package com.example.pong;

public final class BackgroundMusicPlayerTest {
    private BackgroundMusicPlayerTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("BackgroundMusicPlayer rejects blank resource paths", BackgroundMusicPlayerTest::rejectsBlankResourcePath);
        TestSupport.runTest("BackgroundMusicPlayer manages state and volume", BackgroundMusicPlayerTest::managesStateAndVolume);
    }

    private static void rejectsBlankResourcePath() {
        TestSupport.assertThrows(IllegalArgumentException.class, () -> new BackgroundMusicPlayer(" "), "Blank resource paths must be rejected.");
    }

    private static void managesStateAndVolume() {
        BackgroundMusicPlayer player = new BackgroundMusicPlayer("/missing.mp3");
        try {
            TestSupport.assertFalse(player.isMuted(), "A new player should not start muted.");
            TestSupport.assertFalse(player.isPaused(), "A new player should not start paused.");

            player.setPaused(true);
            TestSupport.assertTrue(player.isPaused(), "setPaused(true) should update the paused state.");
            player.setPaused(false);
            TestSupport.assertFalse(player.isPaused(), "setPaused(false) should clear the paused state.");

            player.setMuted(true);
            TestSupport.assertTrue(player.isMuted(), "setMuted(true) should update the muted state.");
            player.toggleMuted();
            TestSupport.assertFalse(player.isMuted(), "toggleMuted() should invert the muted state.");

            for (int i = 0; i < 50; i++) {
                player.increaseVolume();
            }
            TestSupport.assertApprox(1.0, player.getVolume(), 1e-6, "Volume must be clamped at the upper limit.");

            for (int i = 0; i < 50; i++) {
                player.decreaseVolume();
            }
            TestSupport.assertApprox(0.0, player.getVolume(), 1e-6, "Volume must be clamped at the lower limit.");
        } finally {
            player.close();
        }
    }
}

