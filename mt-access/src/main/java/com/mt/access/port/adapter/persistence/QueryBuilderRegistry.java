package com.mt.access.port.adapter.persistence;

import com.mt.access.port.adapter.persistence.client.SpringDataJpaClientRepository;
import com.mt.access.port.adapter.persistence.endpoint.SpringDataJpaEndpointRepository;
import com.mt.access.port.adapter.persistence.revoke_token.RedisRevokeTokenRepository;
import com.mt.access.port.adapter.persistence.role.SpringDataJpaRoleRepository;
import com.mt.access.port.adapter.persistence.sub_request.SpringDataJpaSubRequestRepository;
import com.mt.access.port.adapter.persistence.user.SpringDataJpaUserRelationRepository;
import com.mt.access.port.adapter.persistence.user.SpringDataJpaUserRepository;
import com.mt.access.port.adapter.persistence.user.UpdateUserQueryBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor
        clientSelectQueryBuilder;
    @Getter
    private static SpringDataJpaUserRepository.JpaCriteriaApiUserAdaptor userQueryBuilder;
    @Getter
    private static UpdateUserQueryBuilder updateUserQueryBuilder;
    @Getter
    private static RedisRevokeTokenRepository.RedisRevokeTokenAdaptor redisRevokeTokenAdaptor;
    @Getter
    private static SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter
        endpointQueryBuilder;
    @Getter
    private static SpringDataJpaRoleRepository.JpaCriteriaApiRoleAdaptor roleAdaptor;
    @Getter
    private static SpringDataJpaSubRequestRepository.JpaCriteriaApiSubRequestAdaptor
        subRequestAdaptor;
    @Getter
    private static SpringDataJpaUserRelationRepository.JpaCriteriaApiUserRelationAdaptor
        userRelationAdaptor;


    @Autowired
    public void setJpaCriteriaApiSubRequestAdaptor(
        SpringDataJpaSubRequestRepository.JpaCriteriaApiSubRequestAdaptor subRequestAdaptor) {
        QueryBuilderRegistry.subRequestAdaptor = subRequestAdaptor;
    }

    @Autowired
    public void setJpaCriteriaApiUserRelationAdaptor(
        SpringDataJpaUserRelationRepository.JpaCriteriaApiUserRelationAdaptor userRelationAdaptor) {
        QueryBuilderRegistry.userRelationAdaptor = userRelationAdaptor;
    }

    @Autowired
    public void setJpaCriteriaApiRoleAdaptor(
        SpringDataJpaRoleRepository.JpaCriteriaApiRoleAdaptor roleAdaptor) {
        QueryBuilderRegistry.roleAdaptor = roleAdaptor;
    }

    @Autowired
    public void setEndpointQueryBuilder(
        SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter endpointQueryBuilder) {
        QueryBuilderRegistry.endpointQueryBuilder = endpointQueryBuilder;
    }

    @Autowired
    public void setRevokeTokenAdaptor(
        RedisRevokeTokenRepository.RedisRevokeTokenAdaptor redisRevokeTokenAdaptor) {
        QueryBuilderRegistry.redisRevokeTokenAdaptor = redisRevokeTokenAdaptor;
    }

    @Autowired
    public void setClientQueryBuilder(
        SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }

    @Autowired
    public void setUpdateUserQueryBuilder(UpdateUserQueryBuilder userUpdateQueryBuilder) {
        QueryBuilderRegistry.updateUserQueryBuilder = userUpdateQueryBuilder;
    }

    @Autowired
    public void setUserQueryBuilder(
        SpringDataJpaUserRepository.JpaCriteriaApiUserAdaptor userQueryBuilder) {
        QueryBuilderRegistry.userQueryBuilder = userQueryBuilder;
    }
}
