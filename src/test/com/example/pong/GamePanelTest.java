package com.example.pong;

import javax.swing.JPanel;
import java.util.Arrays;
import java.util.List;

public final class GamePanelTest {
    private GamePanelTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("GamePanel has the expected type structure", GamePanelTest::hasExpectedTypeStructure);
        TestSupport.runTest("GamePanel.AiSettings returns the expected values", GamePanelTest::aiSettingsFactoryReturnsExpectedValues);
        TestSupport.runTest("GamePanel.GameState contains the expected states", GamePanelTest::containsExpectedGameStates);
    }

    private static void hasExpectedTypeStructure() {
        TestSupport.assertEquals(JPanel.class, GamePanel.class.getSuperclass(), "GamePanel should extend JPanel.");
        List<String> interfaceNames = Arrays.stream(GamePanel.class.getInterfaces())
                .map(Class::getName)
                .toList();
        TestSupport.assertTrue(interfaceNames.contains(Runnable.class.getName()), "GamePanel should implement Runnable.");
    }

    private static void aiSettingsFactoryReturnsExpectedValues() {
        Class<?> aiSettingsClass = TestSupport.findInnerClass(GamePanel.class, "AiSettings");
        Object strong = TestSupport.invokeStatic(aiSettingsClass, "fromStrength", new Class[]{int.class}, 4);
        Object fallback = TestSupport.invokeStatic(aiSettingsClass, "fromStrength", new Class[]{int.class}, 99);

        double strongDecisionInterval = (double) TestSupport.invoke(strong, "decisionInterval", new Class[0]);
        double strongErrorRange = (double) TestSupport.invoke(strong, "errorRange", new Class[0]);
        double fallbackDeadZone = (double) TestSupport.invoke(fallback, "deadZone", new Class[0]);

        TestSupport.assertApprox(0.08, strongDecisionInterval, 1e-9, "Strength 4 should have the smallest decision interval.");
        TestSupport.assertApprox(10.0, strongErrorRange, 1e-9, "Strength 4 should have the smallest error range.");
        TestSupport.assertApprox(35.0, fallbackDeadZone, 1e-9, "Invalid strength should fall back to the default values.");
    }

    private static void containsExpectedGameStates() {
        Class<?> gameStateClass = TestSupport.findInnerClass(GamePanel.class, "GameState");
        List<String> states = Arrays.stream(gameStateClass.getEnumConstants())
                .map(Object::toString)
                .toList();

        TestSupport.assertTrue(states.contains("START"), "START should be present.");
        TestSupport.assertTrue(states.contains("COUNTDOWN"), "COUNTDOWN should be present.");
        TestSupport.assertTrue(states.contains("PLAYING"), "PLAYING should be present.");
        TestSupport.assertTrue(states.contains("PAUSED"), "PAUSED should be present.");
        TestSupport.assertTrue(states.contains("GAME_OVER"), "GAME_OVER should be present.");
    }
}

