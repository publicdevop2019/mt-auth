package com.mt.test_case.helper.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredEvent {
    private Long id;
    private String eventBody;
    private Long timestamp;
    private String name;
    private boolean internal;
    private String domainId;

}
