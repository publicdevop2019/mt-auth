package com.hw.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Job {
    private String name;
    private int maxLockAcquireFailureAllowed;
    private String lastStatus;
    private String id;
    private String type;
    private int failureCount;
    private String failureReason;
    private int failureAllowed;
    private long minimumIdleTimeAllowed;
    private boolean notifiedAdmin;
    private long lastExecution;

}
