package com.mt.helper.utility;

import com.mt.helper.TenantContext;
import com.mt.helper.pojo.Cors;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.SumTotal;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class CorsUtility {
    private static final ParameterizedTypeReference<SumTotal<Cors>> reference =
        new ParameterizedTypeReference<>() {
        };

    public static String getUrl(Project project) {
        return HttpUtility.appendPath(TenantUtility.getTenantUrl(project), "cors");
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
        cors.setAllowedHeaders(Collections.singleton(
            RandomUtility.randomStringNoNum() + "-" + RandomUtility.randomStringNoNum()));
        cors.setExposedHeaders(Collections.singleton(RandomUtility.randomStringNoNum()));
        return cors;
    }

    public static Cors createValidCors() {
        Cors cors = createRandomCorsObj();
        cors.setMaxAge(60L);
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

    public static ResponseEntity<Void> patchTenantCache(TenantContext tenantContext,
                                                        Cors cors, PatchCommand command) {
        String url = getUrl(tenantContext.getProject());
        return Utility.patchResource(tenantContext.getCreator(), url, command, cors.getId());
    }

    public static ResponseEntity<SumTotal<Cors>> readTenantCors(TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<SumTotal<Cors>> readTenantCorsById(TenantContext tenantContext,
                                                                    String id) {
        String url = HttpUtility.appendQuery(getUrl(tenantContext.getProject()), "query=id:" + id);
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }
}
