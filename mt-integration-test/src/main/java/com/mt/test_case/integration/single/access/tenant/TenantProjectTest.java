package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.CommonTest;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.ProjectUtility;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
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
        User user = UserUtility.createUser();
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.OK, tenantProject.getStatusCode());
        //null
        randomProjectObj.setName(null);
        ResponseEntity<Void> response =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        randomProjectObj.setName(" ");
        ResponseEntity<Void> response1 =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        randomProjectObj.setName("");
        ResponseEntity<Void> response2 =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        randomProjectObj.setName("01");
        ResponseEntity<Void> response3 =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        randomProjectObj.setName("0123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        randomProjectObj.setName("<");
        ResponseEntity<Void> response5 =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
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
