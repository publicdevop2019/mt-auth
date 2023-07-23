package com.mt.helper;

import com.mt.helper.utility.TestContext;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({TestResultLoggerExtension.class})
@Slf4j
public class CommonTest {

    @BeforeAll
    public static void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @BeforeEach
    public void setUpUnite() {
        LogHelper.init(UUID.randomUUID().toString());
        log.info("initiate unit case complete, test id {}", TestContext.getTestId());
    }
}
