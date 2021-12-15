package com.hw.entity;

import lombok.Data;

@Data
public class TestResult {
    private Long id;
    private Integer testExecuted;
    private Integer ignored;
    private Integer failed;
    private Long elapse;
    private String status;
    private String failedMsg;
}
