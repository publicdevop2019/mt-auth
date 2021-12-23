package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.RegisteredApplication;

import java.util.Set;

public interface RegisterApplicationAdapter {
    Set<RegisteredApplication> fetchAll();
}
