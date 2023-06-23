package com.mt.test_case.helper.pojo;

import lombok.Data;

@Data
public class PatchCommand {
    private String op;
    private String path;
    private Object value;
}
