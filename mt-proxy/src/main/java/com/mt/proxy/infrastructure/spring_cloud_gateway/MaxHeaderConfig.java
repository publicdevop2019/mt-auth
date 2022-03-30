package com.mt.proxy.infrastructure.spring_cloud_gateway;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class MaxHeaderConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
    // config max header size to 1MB
    private static final int MAX_HEADER_SIZE = 1024 * 1024;

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(server ->
            server.httpRequestDecoder(decoder -> decoder.maxHeaderSize(MAX_HEADER_SIZE)));
    }
}
