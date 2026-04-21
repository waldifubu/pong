package com.example.pong;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class GameConfig {
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 1100;
    private static final int DEFAULT_MAX_SCORE = 10;
    private static final int DEFAULT_AI_STRENGTH = 1;
    private static final int MIN_WIDTH = 400;
    private static final int MAX_WIDTH = 4000;
    private static final int MIN_HEIGHT = 300;
    private static final int MAX_HEIGHT = 3000;
    private static final int MIN_MAX_SCORE = 1;
    private static final int MAX_MAX_SCORE = 15;
    private static final String DEFAULT_LEFT_PLAYER = "Left Player";
    private static final String DEFAULT_RIGHT_PLAYER = "Right Player";
    private static final boolean DEFAULT_SOUNDS_ENABLED = true;
    private static final boolean DEFAULT_MUSIC_ENABLED = false;
    private static final String CONFIG_RESOURCE = "/config.properties";
    private static final String BOOLEAN_EXPECTATION = "one of: 1, 0, true, false, yes, no, on, off";

    private final int width;
    private final int height;
    private final int maxScore;
    private final int aiStrength;
    private final String leftPlayerName;
    private final String rightPlayerName;
    private final boolean soundsEnabled;
    private final boolean musicEnabled;

    private GameConfig(int width, int height, int maxScore, int aiStrength, String leftPlayerName, String rightPlayerName,
                       boolean soundsEnabled, boolean musicEnabled) {
        this.width = width;
        this.height = height;
        this.maxScore = maxScore;
        this.aiStrength = aiStrength;
        this.leftPlayerName = leftPlayerName;
        this.rightPlayerName = rightPlayerName;
        this.soundsEnabled = soundsEnabled;
        this.musicEnabled = musicEnabled;
    }

    public static GameConfig loadDefault() {
        Properties properties = loadDefaultProperties();

        return new GameConfig(
                parsePositiveInt(properties.getProperty("WIDTH"), DEFAULT_WIDTH),
                parsePositiveInt(properties.getProperty("HEIGHT"), DEFAULT_HEIGHT),
                parsePositiveInt(properties.getProperty("MAX_SCORE"), DEFAULT_MAX_SCORE),
                parseIntInRange(properties.getProperty("AI_STRENGTH"), 1, 4, DEFAULT_AI_STRENGTH),
                parseString(properties.getProperty("LEFT_PLAYER"), DEFAULT_LEFT_PLAYER),
                parseString(properties.getProperty("RIGHT_PLAYER"), DEFAULT_RIGHT_PLAYER),
                parseBoolean(properties.getProperty("SOUNDS_ENABLED"), DEFAULT_SOUNDS_ENABLED),
                parseBoolean(properties.getProperty("MUSIC_ENABLED"), DEFAULT_MUSIC_ENABLED)
        );
    }

    public static String validateDefaultConfig() {
        return validateProperties(loadDefaultProperties());
    }

    public static String validateProperties(Properties properties) {
        List<String> errors = new ArrayList<>();

        validateIntegerRange(properties, errors, "WIDTH", MIN_WIDTH, MAX_WIDTH,
                "a whole number between " + MIN_WIDTH + " and " + MAX_WIDTH);
        validateIntegerRange(properties, errors, "HEIGHT", MIN_HEIGHT, MAX_HEIGHT,
                "a whole number between " + MIN_HEIGHT + " and " + MAX_HEIGHT);
        validateIntegerRange(properties, errors, "MAX_SCORE", MIN_MAX_SCORE, MAX_MAX_SCORE,
                "a whole number between " + MIN_MAX_SCORE + " and " + MAX_MAX_SCORE);
        validateIntegerRange(properties, errors, "AI_STRENGTH", 1, 4,
                "a whole number between 1 and 4");
        validateNonBlank(properties, errors, "LEFT_PLAYER");
        validateNonBlank(properties, errors, "RIGHT_PLAYER");
        validateBooleanToken(properties, errors, "SOUNDS_ENABLED");
        validateBooleanToken(properties, errors, "MUSIC_ENABLED");

        String leftPlayer = trimToNull(properties.getProperty("LEFT_PLAYER"));
        String rightPlayer = trimToNull(properties.getProperty("RIGHT_PLAYER"));
        if (leftPlayer != null && leftPlayer.equalsIgnoreCase(rightPlayer)) {
            errors.add("LEFT_PLAYER and RIGHT_PLAYER must not use the same name.");
        }

        return String.join(System.lineSeparator(), errors);
    }

    private static Properties loadDefaultProperties() {
        Properties properties = new Properties();
        loadInto(properties);
        return properties;
    }

    private static void loadInto(Properties properties) {
        for (Path candidate : getConfigCandidates()) {
            if (!Files.isRegularFile(candidate)) {
                continue;
            }

            try (InputStream inputStream = Files.newInputStream(candidate)) {
                properties.load(inputStream);
                return;
            } catch (IOException e) {
                System.err.println("Configuration could not be loaded: " + candidate + " (" + e.getMessage() + ")");
            }
        }

        try (InputStream inputStream = GameConfig.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            System.err.println("Configuration could not be loaded: " + e.getMessage());
        }
    }

    private static List<Path> getConfigCandidates() {
        List<Path> candidates = new ArrayList<>();
        Path directory = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();

        for (int depth = 0; depth < 5 && directory != null; depth++) {
            candidates.add(directory.resolve("config.properties"));
            directory = directory.getParent();
        }

        return candidates;
    }

    private static int parsePositiveInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static void validateIntegerRange(Properties properties, List<String> errors, String key,
                                             int minValue, int maxValue, String expectation) {
        String rawValue = properties.getProperty(key);
        if (rawValue == null || rawValue.isBlank()) {
            errors.add(key + " is missing or blank. Expected " + expectation + ".");
            return;
        }

        try {
            int value = Integer.parseInt(rawValue.trim());
            if (value < minValue || value > maxValue) {
                errors.add(key + " has an implausible value '" + rawValue.trim() + "'. Expected " + expectation + ".");
            }
        } catch (NumberFormatException e) {
            errors.add(key + " has an invalid value '" + rawValue.trim() + "'. Expected " + expectation + ".");
        }
    }

    private static void validateNonBlank(Properties properties, List<String> errors, String key) {
        String value = trimToNull(properties.getProperty(key));
        if (value == null) {
            errors.add(key + " is missing or blank. Expected a non-empty player name.");
        }
    }

    private static void validateBooleanToken(Properties properties, List<String> errors, String key) {
        String rawValue = properties.getProperty(key);
        if (rawValue == null || rawValue.isBlank()) {
            errors.add(key + " is missing or blank. Expected " + BOOLEAN_EXPECTATION + ".");
            return;
        }

        if (!isSupportedBooleanToken(rawValue)) {
            errors.add(key + " has an invalid value '" + rawValue.trim() + "'. Expected " + BOOLEAN_EXPECTATION + ".");
        }
    }

    private static boolean isSupportedBooleanToken(String value) {
        return switch (value.trim().toLowerCase()) {
            case "1", "0", "true", "false", "yes", "no", "on", "off" -> true;
            default -> false;
        };
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String parseString(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static int parseIntInRange(String value, int minValue, int maxValue, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= minValue && parsed <= maxValue ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return switch (value.trim().toLowerCase()) {
            case "1", "true", "yes", "on" -> true;
            case "0", "false", "no", "off" -> false;
            default -> defaultValue;
        };
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getAiStrength() {
        return aiStrength;
    }

    public String getLeftPlayerName() {
        return leftPlayerName;
    }

    public String getRightPlayerName() {
        return rightPlayerName;
    }

    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }
}
