package com.example.pong.screen;

import com.example.pong.TestSupport;

import java.awt.Graphics;

public final class ScreenTest {
    private ScreenTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("Screen is an interface with update and draw", ScreenTest::hasExpectedContract);
        TestSupport.runTest("All screens implement Screen", ScreenTest::isImplementedByConcreteScreens);
    }

    private static void hasExpectedContract() {
        TestSupport.assertTrue(Screen.class.isInterface(), "Screen should be an interface.");
        try {
            TestSupport.assertEquals(void.class, Screen.class.getMethod("update", double.class).getReturnType(), "update should return void.");
            TestSupport.assertEquals(void.class, Screen.class.getMethod("draw", Graphics.class).getReturnType(), "draw should return void.");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("The Screen methods were not found.", e);
        }
    }

    private static void isImplementedByConcreteScreens() {
        TestSupport.assertTrue(implementsScreen(StartScreen.class), "StartScreen should implement Screen.");
        TestSupport.assertTrue(implementsScreen(CountdownScreen.class), "CountdownScreen should implement Screen.");
        TestSupport.assertTrue(implementsScreen(PauseScreen.class), "PauseScreen should implement Screen.");
        TestSupport.assertTrue(implementsScreen(GameOverScreen.class), "GameOverScreen should implement Screen.");
        TestSupport.assertTrue(implementsScreen(MatchballScreen.class), "MatchballScreen should implement Screen.");
        TestSupport.assertTrue(implementsScreen(HUD.class), "HUD should implement Screen.");
    }

    private static boolean implementsScreen(Class<?> type) {
        for (Class<?> implementedInterface : type.getInterfaces()) {
            if (implementedInterface.getName().equals(Screen.class.getName())) {
                return true;
            }
        }
        return false;
    }
}

