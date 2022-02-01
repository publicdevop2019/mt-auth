package com.mt.access.port.adapter.persistence;

import com.mt.access.port.adapter.persistence.cache_profile.SpringDataJpaCacheProfileRepository;
import com.mt.access.port.adapter.persistence.client.SpringDataJpaClientRepository;
import com.mt.access.port.adapter.persistence.cors_profile.SpringDataJpaCorsProfileRepository;
import com.mt.access.port.adapter.persistence.endpoint.SpringDataJpaEndpointRepository;
import com.mt.access.port.adapter.persistence.organization.SpringDataJpaOrganizationRepository;
import com.mt.access.port.adapter.persistence.permission.SpringDataJpaPermissionRepository;
import com.mt.access.port.adapter.persistence.position.SpringDataJpaPositionRepository;
import com.mt.access.port.adapter.persistence.project.SpringDataJpaProjectRepository;
import com.mt.access.port.adapter.persistence.revoke_token.RedisRevokeTokenRepository;
import com.mt.access.port.adapter.persistence.role.SpringDataJpaRoleRepository;
import com.mt.access.port.adapter.persistence.system_role.SpringDataJpaSystemRoleRepository;
import com.mt.access.port.adapter.persistence.user.SpringDataJpaUserRepository;
import com.mt.access.port.adapter.persistence.user.UpdateUserQueryBuilder;
import com.mt.access.port.adapter.persistence.user_relation.SpringDataJpaUserRelationRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder;
    @Getter
    private static SpringDataJpaUserRepository.JpaCriteriaApiUserAdaptor userQueryBuilder;
    @Getter
    private static UpdateUserQueryBuilder updateUserQueryBuilder;
    @Getter
    private static RedisRevokeTokenRepository.RedisRevokeTokenAdaptor redisRevokeTokenAdaptor;
    @Getter
    private static SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter endpointQueryBuilder;
    @Getter
    private static SpringDataJpaSystemRoleRepository.JpaCriteriaApiSystemRoleAdaptor systemRoleAdaptor;
    @Getter
    private static SpringDataJpaCorsProfileRepository.JpaCriteriaApiCorsProfileAdaptor corsProfileAdaptor;
    @Getter
    private static SpringDataJpaCacheProfileRepository.JpaCriteriaApiCacheProfileAdaptor cacheProfileAdaptor;
    @Getter
    private static SpringDataJpaProjectRepository.JpaCriteriaApiProjectAdaptor projectAdaptor;
    @Getter
    private static SpringDataJpaRoleRepository.JpaCriteriaApiRoleAdaptor roleAdaptor;
    @Getter
    private static SpringDataJpaUserRelationRepository.JpaCriteriaApiUserRelationAdaptor userRelationAdaptor;
    @Getter
    private static SpringDataJpaPermissionRepository.JpaCriteriaApiPermissionAdaptor permissionAdaptor;
    @Getter
    private static SpringDataJpaOrganizationRepository.JpaCriteriaApiOrganizationAdaptor organizationAdaptor;
    @Getter
    private static SpringDataJpaPositionRepository.JpaCriteriaApiPositionAdaptor positionAdaptor;


    @Autowired
    public void setJpaCriteriaApiUserRelationAdaptor(SpringDataJpaUserRelationRepository.JpaCriteriaApiUserRelationAdaptor userRelationAdaptor) {
        QueryBuilderRegistry.userRelationAdaptor = userRelationAdaptor;
    }

    @Autowired
    public void setJpaCriteriaApiPositionAdaptor(SpringDataJpaPositionRepository.JpaCriteriaApiPositionAdaptor positionAdaptor) {
        QueryBuilderRegistry.positionAdaptor = positionAdaptor;
    }
    @Autowired
    public void setJpaCriteriaApiOrganizationAdaptor(SpringDataJpaOrganizationRepository.JpaCriteriaApiOrganizationAdaptor organizationAdaptor) {
        QueryBuilderRegistry.organizationAdaptor = organizationAdaptor;
    }
    @Autowired
    public void setJpaCriteriaApiProjectAdaptor(SpringDataJpaProjectRepository.JpaCriteriaApiProjectAdaptor projectAdaptor) {
        QueryBuilderRegistry.projectAdaptor = projectAdaptor;
    }
    @Autowired
    public void setJpaCriteriaApiRoleAdaptor(SpringDataJpaRoleRepository.JpaCriteriaApiRoleAdaptor roleAdaptor) {
        QueryBuilderRegistry.roleAdaptor = roleAdaptor;
    }
    @Autowired
    public void setJpaCriteriaApiPermissionAdaptor(SpringDataJpaPermissionRepository.JpaCriteriaApiPermissionAdaptor permissionAdaptor) {
        QueryBuilderRegistry.permissionAdaptor = permissionAdaptor;
    }
    @Autowired
    public void setJpaCriteriaApiCacheProfileAdaptor(SpringDataJpaCacheProfileRepository.JpaCriteriaApiCacheProfileAdaptor cacheProfileAdaptor) {
        QueryBuilderRegistry.cacheProfileAdaptor = cacheProfileAdaptor;
    }

    @Autowired
    public void setJpaCriteriaApiSystemRoleAdaptor(SpringDataJpaSystemRoleRepository.JpaCriteriaApiSystemRoleAdaptor jpaCriteriaApiSystemRoleAdaptor) {
        QueryBuilderRegistry.systemRoleAdaptor = jpaCriteriaApiSystemRoleAdaptor;
    }

    @Autowired
    public void setJpaCriteriaApiCorsProfileAdaptor(SpringDataJpaCorsProfileRepository.JpaCriteriaApiCorsProfileAdaptor corsProfileAdaptor) {
        QueryBuilderRegistry.corsProfileAdaptor = corsProfileAdaptor;
    }

    @Autowired
    public void setEndpointQueryBuilder(SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter endpointQueryBuilder) {
        QueryBuilderRegistry.endpointQueryBuilder = endpointQueryBuilder;
    }

    @Autowired
    public void setRevokeTokenAdaptor(RedisRevokeTokenRepository.RedisRevokeTokenAdaptor redisRevokeTokenAdaptor) {
        QueryBuilderRegistry.redisRevokeTokenAdaptor = redisRevokeTokenAdaptor;
    }

    @Autowired
    public void setClientQueryBuilder(SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }

    @Autowired
    public void setUpdateUserQueryBuilder(UpdateUserQueryBuilder userUpdateQueryBuilder) {
        QueryBuilderRegistry.updateUserQueryBuilder = userUpdateQueryBuilder;
    }

    @Autowired
    public void setUserQueryBuilder(SpringDataJpaUserRepository.JpaCriteriaApiUserAdaptor userQueryBuilder) {
        QueryBuilderRegistry.userQueryBuilder = userQueryBuilder;
    }
}
