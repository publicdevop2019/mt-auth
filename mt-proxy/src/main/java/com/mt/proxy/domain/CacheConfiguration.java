package com.mt.proxy.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public class CacheConfiguration {
    private final Boolean allowCache;
    private final Set<String> cacheControl;

    private final Long expires;

    private final Long maxAge;

    private final Long smaxAge;

    private final String vary;

    private final Boolean etag;

    private final Boolean weakValidation;

    public CacheConfiguration(Endpoint endpoint) {
        this.allowCache = endpoint.getCacheConfig().getAllowCache();
        this.cacheControl = endpoint.getCacheConfig().getCacheControl();
        this.expires = endpoint.getCacheConfig().getExpires();
        this.maxAge = endpoint.getCacheConfig().getMaxAge();
        this.smaxAge = endpoint.getCacheConfig().getSmaxAge();
        this.vary = endpoint.getCacheConfig().getVary();
        this.etag = endpoint.getCacheConfig().getEtag();
        this.weakValidation = endpoint.getCacheConfig().getWeakValidation();
    }

    public String getCacheControlValue() {
        List<String> list = new ArrayList<>();
        Stream<String> stringStream =
            cacheControl.stream().filter(e -> !List.of("max-age", "s-maxage").contains(e));
        if (this.maxAge != null) {
            list.add("max-age=" + this.maxAge);
        }

        if (this.smaxAge != null) {
            list.add("s-maxage=" + this.smaxAge);
        }
        return Stream.concat(stringStream, list.stream()).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "CacheConfiguration{" +
            "allowCache=" + allowCache +
            ", cacheControl=" + cacheControl +
            ", expires=" + expires +
            ", maxAge=" + maxAge +
            ", smaxAge=" + smaxAge +
            ", vary='" + vary + '\'' +
            ", etag=" + etag +
            ", weakValidation=" + weakValidation +
            '}';
    }
}
