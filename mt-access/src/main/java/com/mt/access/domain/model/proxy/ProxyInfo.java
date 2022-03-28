package com.mt.access.domain.model.proxy;

import lombok.Getter;

@Getter
public class ProxyInfo {
    private final String id;

    public ProxyInfo(int i) {
        id = String.valueOf(i);
    }
}
