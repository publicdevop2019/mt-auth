package com.mt.test_case.helper;

import com.mt.test_case.helper.utility.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({TestResultLoggerExtension.class})
@Slf4j
public class CommonTest {

    @BeforeAll
    public static void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }
}
