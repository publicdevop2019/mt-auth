package com.mt.helper.utility;

public class TestUtility {
    public static void proxyDefaultWait() {
        try {
            Thread.sleep(90 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createProjectDefaultWait() {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
