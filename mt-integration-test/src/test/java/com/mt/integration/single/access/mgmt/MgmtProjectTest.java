package com.mt.integration.single.access.mgmt;

import com.mt.helper.AppConstant;
import com.mt.helper.CommonTest;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.UserUtility;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)

@Slf4j
public class MgmtProjectTest extends CommonTest {
    @Test
    public void admin_can_view_project() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Project>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_PROJECTS), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());
    }
}
