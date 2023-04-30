package com.hw.integration.single.access.tenant;

import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantMarketTest  extends CommonTest {
    @Test
    public void tenant_can_view_api_on_market(){

    }

    @Test
    public void tenant_can_view_public_api(){

    }
    @Test
    public void tenant_can_view_shared_api(){

    }
    @Test
    public void tenant_can_send_sub_req_for_shared_api(){

    }
    @Test
    public void tenant_cannot_send_sub_req_for_public_api(){

    }

    @Test
    public void tenant_can_approve_sub_req_for_shared_endpoint(){

    }
    @Test
    public void tenant_can_assign_approved_api_to_role(){

    }
}
