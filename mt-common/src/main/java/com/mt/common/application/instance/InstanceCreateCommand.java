package com.mt.common.application.instance;

import lombok.Data;

@Data
public class InstanceCreateCommand {
    private String name;
    private String url;
}
