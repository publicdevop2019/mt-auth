package com.mt.integration.single.access.tenant;

import com.mt.helper.CommonTest;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ProjectUtility;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantProjectTest extends CommonTest {
    @Test
    public void tenant_can_create_project() throws InterruptedException {
        User user = UserUtility.createUser();
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        //get current project list
        ResponseEntity<SumTotal<Project>> exchange = ProjectUtility.readTenantProjects(user);
        Integer previousCount = exchange.getBody().getTotalItemCount();
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //create project
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assertions.assertEquals(HttpStatus.OK, tenantProject.getStatusCode());
        Thread.sleep(10*1000);
        //get updated project list
        ResponseEntity<SumTotal<Project>> exchange2 = ProjectUtility.readTenantProjects(user);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Integer currentCount = exchange2.getBody().getTotalItemCount();
        Assertions.assertNotSame(previousCount,
            currentCount);
    }

    @Test
    public void tenant_can_view_project_detail() throws InterruptedException {
        User user = UserUtility.createUser();
        //create project
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assertions.assertEquals(HttpStatus.OK, tenantProject.getStatusCode());
        Thread.sleep(10*1000);
        String id = UrlUtility.getId(tenantProject);
        randomProjectObj.setId(id);
        //get updated project list
        ResponseEntity<SumTotal<Project>> exchange2 = ProjectUtility.readTenantProjects(user);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

        //get project detail
        ResponseEntity<Project> projectResponseEntity =
            ProjectUtility.readTenantProject(user, randomProjectObj);
        Assertions.assertEquals(HttpStatus.OK, projectResponseEntity.getStatusCode());
        Assertions.assertEquals(1, projectResponseEntity.getBody().getTotalUserOwned().intValue());
    }

}
