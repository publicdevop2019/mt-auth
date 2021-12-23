package com.mt.proxy.domain;

import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.RegisteredApplication;

import java.util.Set;

public interface RetrieveRegisterApplicationService {
    Set<RegisteredApplication> fetchAll();
}
