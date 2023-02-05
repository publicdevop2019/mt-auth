package com.hw.integration.identityaccess.proxy;

import org.junit.Test;

public class SubscriptionTest {
    @Test
    public void proxy_should_reject_any_call_to_internal_api_from_external_network(){

    }
    //public api shared
    @Test
    public void external_shared_none_auth_api_has_rate_limit_on_ip_and_lifecycle_mngmt(){

    }
    @Test
    public void external_shared_auth_api_has_rate_limit_on_user_id_and_lifecycle_mngmt(){

    }
    //public api none-shared
    @Test
    public void external_none_shared_none_auth_api_has_rate_limit_on_ip_without_lifecycle_mngmt(){

    }
    @Test
    public void external_none_shared_auth_api_has_rate_limit_on_user_id_without_lifecycle_mngmt(){

    }
}
