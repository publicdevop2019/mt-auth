package com.hw.helper;

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
