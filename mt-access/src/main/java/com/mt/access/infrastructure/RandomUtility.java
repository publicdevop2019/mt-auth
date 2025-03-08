package com.mt.access.infrastructure;

import java.util.Random;

public class RandomUtility {
    public static String randomNumber(int length) {
        int m = (int) Math.pow(10, length - 1);
        return String.valueOf(m + new Random().nextInt(9 * m));
    }
}
