package com.example.pong;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public final class TestSupport {
    private TestSupport() {
    }

    public static void runTest(String name, Runnable test) {
        try {
            test.run();
            System.out.println("[OK] " + name);
        } catch (Throwable throwable) {
            System.err.println("[FAIL] " + name + ": " + throwable.getMessage());
            if (throwable instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(throwable);
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertApprox(double expected, double actual, double tolerance, String message) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual + ", tolerance: " + tolerance);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(message + " Unexpected exception type: " + throwable.getClass().getName(), throwable);
        }
        throw new AssertionError(message + " Expected exception: " + expectedType.getName());
    }

    public static Graphics2D createGraphics(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).createGraphics();
    }

    public static Object getField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not read field '" + fieldName + "'", e);
        }
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not write field '" + fieldName + "'", e);
        }
    }

    public static Class<?> findInnerClass(Class<?> outerClass, String simpleName) {
        for (Class<?> innerClass : outerClass.getDeclaredClasses()) {
            if (innerClass.getSimpleName().equals(simpleName)) {
                return innerClass;
            }
        }
        throw new AssertionError("Inner class not found: " + simpleName);
    }

    public static Object invokeStatic(Class<?> type, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = type.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not invoke static method '" + methodName + "'", e);
        }
    }

    public static Object invoke(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not invoke method '" + methodName + "'", e);
        }
    }
}

