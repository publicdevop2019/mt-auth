package com.hw.helper;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Cors {
    private String id;
    private String name;
    private String description;
    private Boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private Long maxAge;
    private Integer version;
}
