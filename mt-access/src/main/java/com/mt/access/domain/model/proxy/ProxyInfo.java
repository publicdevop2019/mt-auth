package com.mt.access.domain.model.proxy;

import lombok.Getter;

@Getter
public class ProxyInfo {
    private final String url;

    public ProxyInfo(String url) {
        this.url = url;
    }
}
