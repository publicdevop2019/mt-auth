package com.hw.integration.single.proxy;

import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class CsrfTest extends CommonTest {
    @Test
    public void create_endpoint_which_requires_csrf() {

    }

    @Test
    public void create_endpoint_which_not_requires_csrf() {

    }

    @Test
    public void create_endpoint_which_requires_csrf_and_access_from_another_domain() {

    }
}
