package com.example.pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class GameConfigTest {
    private GameConfigTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("GameConfig.validateProperties accepts valid values", GameConfigTest::validatePropertiesAcceptsValidValues);
        TestSupport.runTest("GameConfig.validateProperties reports invalid values", GameConfigTest::validatePropertiesReportsInvalidValues);
        TestSupport.runTest("GameConfig.loadDefault returns sane defaults", GameConfigTest::loadDefaultReturnsSaneValues);
        TestSupport.runTest("GameConfig.loadDefault prefers config.properties in the root directory", GameConfigTest::loadDefaultPrefersRootConfig);
        TestSupport.runTest("GameConfig.loadDefault falls back to bundled resource config", GameConfigTest::loadDefaultFallsBackToResourceConfig);
    }

    private static void validatePropertiesAcceptsValidValues() {
        String validation = GameConfig.validateProperties(createValidProperties());
        TestSupport.assertEquals("", validation, "Valid properties should not produce errors.");
    }

    private static void validatePropertiesReportsInvalidValues() {
        Properties properties = createValidProperties();
        properties.setProperty("WIDTH", "399");
        properties.setProperty("HEIGHT", "abc");
        properties.setProperty("LEFT_PLAYER", " ");
        properties.setProperty("SOUNDS_ENABLED", "maybe");

        String validation = GameConfig.validateProperties(properties);

        TestSupport.assertTrue(validation.contains("WIDTH has an implausible value '399'"), "WIDTH errors should be reported.");
        TestSupport.assertTrue(validation.contains("HEIGHT has an invalid value 'abc'"), "HEIGHT errors should be reported.");
        TestSupport.assertTrue(validation.contains("LEFT_PLAYER is missing or blank"), "Blank player names should be reported.");
        TestSupport.assertTrue(validation.contains("SOUNDS_ENABLED has an invalid value 'maybe'"), "Invalid boolean values should be reported.");

        Properties duplicateNames = createValidProperties();
        duplicateNames.setProperty("LEFT_PLAYER", "SameName");
        duplicateNames.setProperty("RIGHT_PLAYER", "samename");

        String duplicateValidation = GameConfig.validateProperties(duplicateNames);
        TestSupport.assertTrue(duplicateValidation.contains("LEFT_PLAYER and RIGHT_PLAYER must not use the same name."), "Duplicate player names should be reported.");
    }

    private static void loadDefaultReturnsSaneValues() {
        GameConfig config = GameConfig.loadDefault();
        TestSupport.assertTrue(config.getWidth() >= 400, "The default width should be plausible.");
        TestSupport.assertTrue(config.getHeight() >= 300, "The default height should be plausible.");
        TestSupport.assertTrue(config.getMaxScore() >= 1, "The default max score should be positive.");
        TestSupport.assertTrue(!config.getLeftPlayerName().isBlank(), "The left player name must not be blank.");
        TestSupport.assertTrue(!config.getRightPlayerName().isBlank(), "The right player name must not be blank.");
    }

    private static void loadDefaultPrefersRootConfig() {
        withWorkingDirectory(createTempDirectory(), workingDirectory -> {
            writeConfig(workingDirectory.resolve("config.properties"),
                    "WIDTH=1234\n"
                            + "HEIGHT=777\n"
                            + "MAX_SCORE=9\n"
                            + "AI_STRENGTH=2\n"
                            + "LEFT_PLAYER=RootLeft\n"
                            + "RIGHT_PLAYER=RootRight\n"
                            + "SOUNDS_ENABLED=off\n"
                            + "MUSIC_ENABLED=on\n");

            GameConfig config = GameConfig.loadDefault();

            TestSupport.assertEquals(1234, config.getWidth(), "The root config should override the bundled width.");
            TestSupport.assertEquals(777, config.getHeight(), "The root config should override the bundled height.");
            TestSupport.assertEquals("RootLeft", config.getLeftPlayerName(), "The root config should override the bundled left player.");
            TestSupport.assertEquals("RootRight", config.getRightPlayerName(), "The root config should override the bundled right player.");
            TestSupport.assertFalse(config.isSoundsEnabled(), "The root config should override the bundled sound flag.");
            TestSupport.assertTrue(config.isMusicEnabled(), "The root config should override the bundled music flag.");
        });
    }

    private static void loadDefaultFallsBackToResourceConfig() {
        withWorkingDirectory(createTempDirectory(), workingDirectory -> {
            GameConfig config = GameConfig.loadDefault();

            TestSupport.assertEquals(1400, config.getWidth(), "The bundled config should supply the default width.");
            TestSupport.assertEquals(1100, config.getHeight(), "The bundled config should supply the default height.");
            TestSupport.assertEquals("Left Player", config.getLeftPlayerName(), "The bundled config should supply the default left player.");
            TestSupport.assertEquals("Right Player", config.getRightPlayerName(), "The bundled config should supply the default right player.");
            TestSupport.assertTrue(config.isSoundsEnabled(), "The bundled config should supply the default sound flag.");
            TestSupport.assertTrue(config.isMusicEnabled(), "The bundled config should supply the default music flag.");
        });
    }

    private static Properties createValidProperties() {
        Properties properties = new Properties();
        properties.setProperty("WIDTH", "1400");
        properties.setProperty("HEIGHT", "900");
        properties.setProperty("MAX_SCORE", "10");
        properties.setProperty("AI_STRENGTH", "3");
        properties.setProperty("LEFT_PLAYER", "Alice");
        properties.setProperty("RIGHT_PLAYER", "Bob");
        properties.setProperty("SOUNDS_ENABLED", "on");
        properties.setProperty("MUSIC_ENABLED", "off");
        return properties;
    }

    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("pong-config-test-");
        } catch (IOException e) {
            throw new AssertionError("Could not create a temporary directory.", e);
        }
    }

    private static void writeConfig(Path file, String content) {
        try {
            Files.writeString(file, content);
        } catch (IOException e) {
            throw new AssertionError("Could not write test config: " + file, e);
        }
    }

    private static void withWorkingDirectory(Path workingDirectory, DirectoryAction action) {
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", workingDirectory.toAbsolutePath().toString());
        try {
            action.run(workingDirectory);
        } finally {
            if (originalUserDir == null) {
                System.clearProperty("user.dir");
            } else {
                System.setProperty("user.dir", originalUserDir);
            }
        }
    }

    @FunctionalInterface
    private interface DirectoryAction {
        void run(Path workingDirectory);
    }
}

