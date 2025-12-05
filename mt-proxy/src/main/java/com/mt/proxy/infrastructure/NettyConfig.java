package com.mt.proxy.infrastructure;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class NettyConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
    // config max header size to 1MB
    private static final int MAX_HEADER_SIZE = 1024 * 1024;
    private static final String DOMAIN_PREFIX_REGEX = "^[0-9][A-Z][0-9A-Z]{10}$";

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(server ->
            server.httpRequestDecoder(decoder -> decoder.maxHeaderSize(MAX_HEADER_SIZE)));
    }

    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return httpServer -> httpServer.metrics(true, this::normalize);
    }

    private String normalize(String requestURI) {
        String[] split = requestURI.split("/");
        List<String> collect = Arrays.stream(split).map(s -> {
            if (s.matches(DOMAIN_PREFIX_REGEX)) {
                return "**";
            } else {
                return s;
            }
        }).collect(Collectors.toList());
        return String.join("/", collect);
    }
}
