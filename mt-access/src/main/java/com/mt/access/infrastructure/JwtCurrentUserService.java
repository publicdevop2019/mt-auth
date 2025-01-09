package com.mt.access.infrastructure;

import com.mt.access.domain.model.CurrentUserService;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
public class JwtCurrentUserService implements CurrentUserService {

    public static final String TENANT_IDS = "tenantIds";
    public static final String VIEW_TENANT_ID = "viewTenantId";
    @Autowired
    private UserJwt userJwt;

    /**
     * debug usage
     * userJwt.value will not work due to spring unable to inject value.
     *
     * @return raw jwt
     */
    public String getRawJwt() {
        return userJwt.get();
    }

    @Override
    public Set<String> userPermissionIds() {
        String jwt = userJwt.get();
        List<String> permissionIds = JwtUtility.getPermissionIds(jwt);
        return new HashSet<>(permissionIds);
    }

    @Override
    public boolean isClient() {
        String jwt = userJwt.get();
        return JwtUtility.getUserId(jwt) == null && JwtUtility.getClientId(jwt) != null;
    }

    @Override
    public boolean isUser() {
        String jwt = userJwt.get();
        return JwtUtility.getUserId(jwt) != null;
    }

    @Override
    public void setUserJwt(Object jwt) {
        userJwt.setValue((String) jwt);
    }

    @Override
    public UserId getUserId() {
        String jwt = userJwt.get();
        String userId;
        try {
            userId = JwtUtility.getUserId(jwt);
        } catch (Exception ex) {
            throw new DefinedRuntimeException("error getting current user id", "1083",
                HttpResponseCode.BAD_REQUEST);
        }
        return new UserId(userId);
    }

    @Override
    public ClientId getClientId() {
        String jwt = userJwt.get();
        return new ClientId(JwtUtility.getClientId(jwt));
    }

    @Override
    public Set<ProjectId> getTenantIds() {
        String jwt = userJwt.get();
        List<String> ids = JwtUtility.getField(TENANT_IDS, jwt);
        return ids == null ? Collections.emptySet() :
            ids.stream().map(ProjectId::new).collect(Collectors.toSet());
    }

    @Override
    public ProjectId getViewProjectId() {
        String jwt = userJwt.get();
        String id = JwtUtility.getField(VIEW_TENANT_ID, jwt);
        return id == null ? null : new ProjectId(id);
    }

    @Override
    public Set<PermissionId> getPermissionIds() {
        String jwt = userJwt.get();
        List<String> permissionIds = JwtUtility.getPermissionIds(jwt);
        return permissionIds.stream().map(PermissionId::new).collect(Collectors.toSet());
    }

    @Bean
    @RequestScope
    public UserJwt userJwt() {
        return new UserJwt();
    }

    @Getter
    @Setter
    @Slf4j
    private static class UserJwt {
        private String value;

        public UserJwt() {
            log.trace("creating new UserJwt");
        }

        public String get() {
            return value;
        }
    }

}
