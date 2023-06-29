package com.mt.test_case.helper;

import com.mt.test_case.helper.utility.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

@Slf4j
public class TestResultLoggerExtension implements TestWatcher {
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("test failed, method {}, id {}", context.getDisplayName(),
            TestContext.getTestId());
    }
}
