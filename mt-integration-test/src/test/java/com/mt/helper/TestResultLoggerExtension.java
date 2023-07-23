package com.mt.helper;


import static com.mt.helper.TestHelper.RUN_ID;
import static com.mt.helper.TestHelper.TEST_ID;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.MDC;

@Slf4j
public class TestResultLoggerExtension implements TestWatcher {
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("test failed, class {}.{} run id {} test id {}",
            context.getTestClass().get().getName(), context.getDisplayName(),
            MDC.get(RUN_ID), MDC.get(TEST_ID));
    }
}
