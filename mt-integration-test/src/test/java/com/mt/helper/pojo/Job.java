package com.mt.helper.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Job {
    private String name;
    private Integer maxLockAcquireFailureAllowed;
    private String lastStatus;
    private String id;
    private String type;
    private Integer failureCount;
    private String failureReason;
    private Integer failureAllowed;
    private Long minimumIdleTimeAllowed;
    private Boolean notifiedAdmin;
    private Long lastExecution;

}
