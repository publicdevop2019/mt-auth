package com.mt.helper;

import org.slf4j.MDC;

public class LogHelper {
    public static void init(String unitId) {
        MDC.clear();
        MDC.put("UNIT_ID", unitId);
    }
}
