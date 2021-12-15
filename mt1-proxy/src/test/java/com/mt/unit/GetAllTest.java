package com.mt.unit;

import org.junit.Assert;
import org.junit.Test;

public class GetAllTest {
    @Test
    public void getAll() {
        Long a = 15L;
        Integer b = 10;
        double l = (double)a / b;
        Assert.assertEquals("1.5", String.valueOf(l));

    }
}
