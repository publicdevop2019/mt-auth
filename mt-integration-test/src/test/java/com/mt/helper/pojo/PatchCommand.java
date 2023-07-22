package com.mt.helper.pojo;

import lombok.Data;

@Data
public class PatchCommand {
    private String op;
    private String path;
    private Object value;
}
