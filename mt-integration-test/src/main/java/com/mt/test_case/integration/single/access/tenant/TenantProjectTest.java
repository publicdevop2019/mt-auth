package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.ProjectUtility;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import com.mt.test_case.helper.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantProjectTest extends CommonTest {
    @Test
    public void tenant_can_create_project() throws InterruptedException {
        User user = UserUtility.createUser();
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        //get current project list
        ResponseEntity<SumTotal<Project>> exchange = ProjectUtility.readTenantProjects(user);
        Integer previousCount = exchange.getBody().getTotalItemCount();
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //create project
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.OK, tenantProject.getStatusCode());
        Thread.sleep(20000);
        //get updated project list
        ResponseEntity<SumTotal<Project>> exchange2 = ProjectUtility.readTenantProjects(user);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Integer currentCount = exchange2.getBody().getTotalItemCount();
        Assert.assertNotSame(previousCount,
            currentCount);
    }

    @Test
    public void tenant_can_view_project_detail() throws InterruptedException {
        User user = UserUtility.createUser();
        //create project
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.OK, tenantProject.getStatusCode());
        Thread.sleep(20000);
        String id = UrlUtility.getId(tenantProject);
        randomProjectObj.setId(id);
        //get updated project list
        ResponseEntity<SumTotal<Project>> exchange2 = ProjectUtility.readTenantProjects(user);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

        //get project detail
        ResponseEntity<Project> projectResponseEntity =
            ProjectUtility.readTenantProject(user, randomProjectObj);
        Assert.assertEquals(HttpStatus.OK, projectResponseEntity.getStatusCode());
        Assert.assertEquals(1, projectResponseEntity.getBody().getTotalUserOwned().intValue());
    }
    @Test
    public void validation_create_name(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_update_name(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_patch_name(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
