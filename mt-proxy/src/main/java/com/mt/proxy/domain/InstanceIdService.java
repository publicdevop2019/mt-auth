package com.mt.proxy.domain;

public interface InstanceIdService {
    void iniInstanceId(String name);
    void removeInstanceId();
    void renew();
}
