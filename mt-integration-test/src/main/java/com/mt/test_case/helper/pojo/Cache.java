package com.mt.test_case.helper.pojo;

import java.util.Set;
import lombok.Data;

@Data
public class Cache {
    private String id;
    private String name;
    private String description;
    private Set<String> cacheControl;
    private Long expires;
    private Long maxAge;
    private Long smaxAge;
    private String vary;
    private boolean etag;
    private boolean allowCache;
    private boolean weakValidation;
    private Integer version;
}
