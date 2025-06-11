package com.mt.common.domain.model.instance;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@EqualsAndHashCode
public class Instance {
    private Integer id;
    private Long createdAt;
    private Long renewedAt;
    private String name;
    private String url;

    public static Instance create(String appName, @Nullable String url) {
        log.info("assigning new instance");
        List<Instance> allInstances =
            CommonDomainRegistry.getInstanceRepository().getAllInstances();
        Set<Integer> collect =
            allInstances.stream().map(Instance::getId).collect(Collectors.toSet());
        Instance instance = new Instance();
        for (int i = 0; i < 64; i++) {
            if (!collect.contains(i)) {
                instance.setId(i);
                long milli = Instant.now().toEpochMilli();
                instance.setCreatedAt(milli);
                instance.setRenewedAt(milli);
                instance.setName(appName);
                instance.setUrl(url == null ? null : url.trim());
                break;
            }
        }
        CommonDomainRegistry.getInstanceRepository().addInstance(instance);
        return instance;
    }

    public static Instance fromDatabaseRow(Integer id, Long createdAt, Long renewedAt, String url,
                                           String name) {
        Instance instance = new Instance();
        instance.setId(id);
        instance.setCreatedAt(createdAt);
        instance.setRenewedAt(renewedAt);
        instance.setUrl(url);
        instance.setName(name);
        return instance;
    }

    public static Instance of(Integer id) {
        Instance instance = new Instance();
        instance.setId(id);
        return instance;
    }

    public static Instance renew(Integer id) {
        Instance instance = new Instance();
        instance.setId(id);
        instance.setRenewedAt(Instant.now().toEpochMilli());
        return instance;
    }
}
