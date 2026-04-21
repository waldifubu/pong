package com.example.pong;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class MainTest {
    private MainTest() {
    }

    public static void main(String[] args) {
        TestSupport.runTest("Main declares a static main method", MainTest::containsStaticMainMethod);
    }

    private static void containsStaticMainMethod() {
        try {
            Method method = Main.class.getDeclaredMethod("main", String[].class);
            TestSupport.assertEquals(void.class, method.getReturnType(), "The main method should return void.");
            TestSupport.assertTrue(Modifier.isStatic(method.getModifiers()), "The main method should be static.");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("The main method was not found.", e);
        }
    }
}

