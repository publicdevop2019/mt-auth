package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Cors;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class CorsUtility {
    private static final ParameterizedTypeReference<SumTotal<Cors>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl(Project project) {
        return UrlUtility.appendPath(TenantUtility.getTenantUrl(project), "cors");
    }

    public static Cors createRandomCorsObj() {
        Cors cors = new Cors();
        cors.setName(RandomUtility.randomStringWithNum());
        cors.setDescription(RandomUtility.randomStringWithNumNullable());
        cors.setAllowCredentials(RandomUtility.randomBoolean());
        String s = RandomUtility.randomLocalHostUrl();
        String s2 = RandomUtility.randomLocalHostUrl();
        HashSet<String> strings = new HashSet<>();
        strings.add(s);
        strings.add(s2);
        cors.setAllowOrigin(strings);
        cors.setMaxAge(RandomUtility.randomLong());
        cors.setAllowedHeaders(Collections.singleton(RandomUtility.randomStringWithNum()));
        cors.setExposedHeaders(Collections.singleton(RandomUtility.randomStringWithNum()));
        return cors;
    }

    public static ResponseEntity<Void> createTenantCors(TenantContext tenantContext,
                                                        Cors cors) {
        String url = getUrl(tenantContext.getProject());
        return Utility.createResource(tenantContext.getCreator(), url, cors);
    }

    public static ResponseEntity<Void> updateTenantCors(TenantContext tenantContext,
                                                        Cors cors) {
        String url = getUrl(tenantContext.getProject());
        return Utility.updateResource(tenantContext.getCreator(), url, cors, cors.getId());
    }

    public static ResponseEntity<Void> deleteTenantCors(TenantContext tenantContext,
                                                        Cors cors) {
        String url = getUrl(tenantContext.getProject());
        return Utility.deleteResource(tenantContext.getCreator(), url, cors.getId());
    }

    public static ResponseEntity<SumTotal<Cors>> readTenantCors(TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }
}
